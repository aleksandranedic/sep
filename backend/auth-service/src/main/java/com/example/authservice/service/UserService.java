package com.example.authservice.service;

import com.example.authservice.dto.request.UserDetailsRequest;
import com.example.authservice.dto.response.UserDetailResponse;
import com.example.authservice.dto.response.UserInfoResponse;
import com.example.authservice.exception.UserNotFoundException;
import com.example.authservice.model.Lawyer;
import com.example.authservice.repository.LawyerRepository;
import com.example.authservice.util.MappingUtils;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Transactional
public class UserService {

    @Autowired
    private LawyerRepository lawyerRepository;

    public List<UserInfoResponse> getLawyers() {
        return MappingUtils.toUserInfoResponseList(lawyerRepository.findAll());
    }

    public UserDetailResponse getLawyer(String userEmail) {
        return MappingUtils.toUserDetailsResponse(lawyerRepository.findByEmail(userEmail).orElseThrow(UserNotFoundException::new));
    }

    public UserDetailResponse editLawyer(UserDetailsRequest userRequest) {
        Lawyer lawyer = lawyerRepository.findByEmail(userRequest.getEmail())
                .orElseThrow(UserNotFoundException::new);
        lawyer.setPhoneNumber(userRequest.getPhoneNumber());
        lawyer.setCity(userRequest.getCity());
        lawyer.setFirstName(userRequest.getFirstName());
        lawyer.setLastName(userRequest.getLastName());
        lawyerRepository.save(lawyer);
        return MappingUtils.toUserDetailsResponse(lawyer);
    }

    public void deleteUser(String userEmail) {
        Lawyer lawyer = lawyerRepository.findByEmail(userEmail)
                .orElseThrow(UserNotFoundException::new);
        lawyer.setDeleted(true);
        lawyerRepository.save(lawyer);
    }
}
