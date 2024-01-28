package com.example.authservice.service;

import com.example.authservice.config.AppProperties;
import com.example.authservice.dto.request.*;
import com.example.authservice.dto.response.LoginResponse;
import com.example.authservice.exception.*;
import com.example.authservice.model.Lawyer;
import com.example.authservice.model.Role;
import com.example.authservice.model.User;
import com.example.authservice.repository.RoleRepository;
import com.example.authservice.repository.UserRepository;
import com.example.authservice.security.AuthenticationManagerWrapper;
import com.example.authservice.security.TokenAuthenticationFilter;
import com.example.authservice.security.TokenProvider;
import com.example.authservice.util.CookieUtils;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.Map;
import java.util.UUID;

import static com.example.authservice.util.LoginUtils.*;

@Service
@Transactional
public class AccountService {

    @Autowired
    private AuthenticationManagerWrapper authenticationManager;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TokenProvider tokenProvider;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private AppProperties appProperties;

    @Autowired
    private MailingService mailingService;

    @Autowired
    private RoleRepository roleRepository;

    public LoginResponse login(LoginRequest loginRequest, HttpServletResponse response) throws Exception {
        Authentication authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(
                loginRequest.getEmail(),
                loginRequest.getPassword()
        ));
        User user = (User) authentication.getPrincipal();

        SecurityContextHolder.getContext().setAuthentication(authentication);
        String accessToken = tokenProvider.createAccessToken(authentication);
        Integer tokenExpirationSeconds = appProperties.getAuth().getTokenExpirationSeconds();
        CookieUtils.addCookie(
                response,
                TokenAuthenticationFilter.ACCESS_TOKEN_COOKIE_NAME,
                accessToken,
                tokenExpirationSeconds
        );


        Long expiresAt = tokenProvider.readClaims(accessToken).getExpiration().getTime();

        return new LoginResponse(user.getId().toString(), expiresAt, user.getRole());
    }

    public boolean verifyLogin(LoginVerificationRequest verificationRequest, User user) {
        return passwordEncoder.matches(verificationRequest.getPassword(), user.getPassword());
    }

    public void logout(HttpServletRequest request, HttpServletResponse response) {
        CookieUtils.deleteCookie(request, response, TokenAuthenticationFilter.ACCESS_TOKEN_COOKIE_NAME);
    }

    public void registerLawyer(RegistrationRequest registrationRequest) {
        checkEmailAvailability(registrationRequest.getEmail());
        if (!checkPasswordStrength(registrationRequest.getPassword())) {
            throw new RuntimeException("Password is not strong enough");
        }
        Lawyer Lawyer = populateLawyer(registrationRequest);
        Lawyer.setPassword(passwordEncoder.encode(registrationRequest.getPassword()));
        Lawyer.setPasswordSet(true);
        Lawyer.setRole(roleRepository.findById(1L).get());
        Lawyer.lockAccount("Email address for this account has not been verified.");
        userRepository.save(Lawyer);
        mailingService.sendEmailVerificationMail(Lawyer);
    }

    public Boolean checkPasswordStrength(String password) {
        if (password.length() < 12) {
            return false;
        }
        boolean hasUppercase = !password.equals(password.toLowerCase());
        boolean hasLowercase = !password.equals(password.toUpperCase());
        boolean hasNumber = password.matches(".*\\d.*");
        boolean hasSpecialChar = password.matches(".*[!@#$%^&*()_+].*");
        return hasUppercase && hasLowercase && hasNumber && hasSpecialChar;
    }

    public void verifyEmail(VerificationRequest verificationRequest) {
        User userToVerify = userRepository.findByVerificationCode(verificationRequest.getCode()).orElseThrow(InvalidVerificationCodeException::new);

        if (userToVerify.isEmailVerified())
            throw new EmailAlreadyVerifiedException();

        userToVerify.unlockAccount();
        userToVerify.setEmailVerified(true);
        userRepository.save(userToVerify);
//        mailingService.sendTwoFactorSetupKey(userToVerify);
    }

    private void checkEmailAvailability(String email) {
        if (userRepository.existsByEmail(email))
            throw new EmailAlreadyInUseException();
    }

    public void createLawyer(UserDetailsRequest createUserRequest) {
        checkEmailAvailability(createUserRequest.getEmail());
        Lawyer Lawyer = populateLawyer(createUserRequest);
        Lawyer.setPasswordSet(false);
        userRepository.save(Lawyer);
        mailingService.sendEmailVerificationMail(Lawyer);
    }

    private Lawyer populateLawyer(UserDetailsRequest createUserRequest) {
        Lawyer Lawyer = new Lawyer();
        Lawyer.setId(UUID.randomUUID());
        Lawyer.setRole(roleRepository.findById(1L).get());
        Lawyer.setEmail(createUserRequest.getEmail());
        Lawyer.setLastLoginAttempt(Instant.parse("2023-12-09T12:30:00Z"));
        Lawyer.setLockedUntil(Instant.parse("2023-12-09T12:30:00Z"));
        Lawyer.setFirstName(createUserRequest.getFirstName());
        Lawyer.setLastName(createUserRequest.getLastName());
        Lawyer.setEmailVerified(false);
        Lawyer.setVerificationCode(generateVerificationCode());
        Lawyer.setTwoFactorKey(generateTwoFactorKey());
        Lawyer.setCity(createUserRequest.getCity());
        Lawyer.setPhoneNumber(createUserRequest.getPhoneNumber());
        return Lawyer;
    }

    public boolean isPasswordSet(String verificationCode) {
        User user = userRepository.findByVerificationCode(verificationCode).orElseThrow(InvalidVerificationCodeException::new);

        if (user.isEmailVerified())
            throw new EmailAlreadyVerifiedException();
        return user.isPasswordSet();
    }

    public void setPassword(SetPasswordRequest setPasswordRequest) {
        User userToVerify = userRepository.findByVerificationCode(setPasswordRequest.getVerificationCode()).orElseThrow(InvalidVerificationCodeException::new);
        if (!setPasswordRequest.getPassword().equals(setPasswordRequest.getPasswordConfirmation())) {
            throw new PasswordsDoNotMatchException();
        }

        if (userToVerify.isEmailVerified())
            throw new EmailAlreadyVerifiedException();
        userToVerify.unlockAccount();
        userToVerify.setEmailVerified(true);
        userToVerify.setPassword(passwordEncoder.encode(setPasswordRequest.getPassword()));
        userToVerify.setPasswordSet(true);
        userRepository.save(userToVerify);
//        mailingService.sendTwoFactorSetupKey(userToVerify);
    }


    public User getLoggedUserInfo(String loggedUserId) {
        return userRepository.getById(UUID.fromString(loggedUserId));
    }

    public void verify(Map<String, Object> req) {
        System.out.println(req);
    }
}
