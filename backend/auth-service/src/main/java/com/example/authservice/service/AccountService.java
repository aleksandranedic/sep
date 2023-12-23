package com.example.authservice.service;

import com.example.authservice.config.AppProperties;
import com.example.authservice.dto.request.*;
import com.example.authservice.dto.response.LoginResponse;
import com.example.authservice.exception.*;
import com.example.authservice.model.Lawyer;
import com.example.authservice.model.Role;
import com.example.authservice.model.User;
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

    public LoginResponse login(LoginRequest loginRequest, HttpServletResponse response) throws Exception {
        Authentication authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(
                loginRequest.getEmail(),
                loginRequest.getPassword()
        ));
        User user = (User) authentication.getPrincipal();

//        if (loginRequest.getCode() == null)
//            throw new Missing2FaCodeException("Please provide 2FA code along with the credentials to authenticate.");
//
//        if (!verify2FA(loginRequest.getCode(), user))
//            throw new Invalid2FaCodeException("Invalid 2FA code.", user);

        SecurityContextHolder.getContext().setAuthentication(authentication);
        String accessToken = tokenProvider.createAccessToken(authentication);
        Integer tokenExpirationSeconds = appProperties.getAuth().getTokenExpirationSeconds();
        Cookie cookie = CookieUtils.addCookie(
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

        Lawyer Lawyer = populateLawyer(registrationRequest);
        Lawyer.setPassword(passwordEncoder.encode(registrationRequest.getPassword()));
        Lawyer.setPasswordSet(true);
        Lawyer.setRole(Role.ROLE_PROPERTY_OWNER);
        Lawyer.lockAccount("Email address for this account has not been verified.");
        userRepository.save(Lawyer);
        mailingService.sendEmailVerificationMail(Lawyer);
    }

    public void verifyEmail(VerificationRequest verificationRequest) {
        User userToVerify = userRepository.findByVerificationCode(verificationRequest.getCode()).orElseThrow(InvalidVerificationCodeException::new);

        if (userToVerify.isEmailVerified())
            throw new EmailAlreadyVerifiedException();

        userToVerify.unlockAccount();
        userToVerify.setEmailVerified(true);
        userRepository.save(userToVerify);
        mailingService.sendTwoFactorSetupKey(userToVerify);
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
        Lawyer.setRole(Role.ROLE_PROPERTY_OWNER);
        Lawyer.setEmail(createUserRequest.getEmail());
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
        mailingService.sendTwoFactorSetupKey(userToVerify);
    }

    public void initUsers() {
        Lawyer admin = new Lawyer();
        admin.setId(UUID.fromString("e3661c31-d1a4-47ab-94b6-1c6500dccf24"));
        admin.setEmail("admin@authservice.com");
        admin.setEmailVerified(true);
        admin.setFirstName("Super");
        admin.setLastName("Admin");
        admin.setPassword("$2a$10$Qg.gpYTtZiVMJ6Fs9QbQA.BtCx4106oSj92X.A/Gv7iAEKQXAg.gy");
        admin.setRole(Role.valueOf("ROLE_ADMIN"));
        admin.setPhoneNumber("+48123456789");
        admin.setCity("Warszawa");
        admin.setPasswordSet(true);
        admin.setLoginAttempts(0);
        admin.setLastLoginAttempt(Instant.parse("2023-12-09T12:30:00Z"));
        admin.setLockedUntil(Instant.parse("2023-12-09T12:30:00Z"));
        admin.setTwoFactorKey("F3OPURVECFTYHZXAM62YME7PVESQZXP7");
        admin.setDeleted(false);
        userRepository.save(admin);
    }

    public User getLoggedUserInfo(String loggedUserId) {
        return userRepository.getById(UUID.fromString(loggedUserId));
    }
}
