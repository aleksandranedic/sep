package com.example.authservice.service;

import com.example.authservice.dto.request.Subscriptions;
import com.example.authservice.dto.request.UserDetailsRequest;
import com.example.authservice.dto.response.UserDetailResponse;
import com.example.authservice.dto.response.UserInfoResponse;
import com.example.authservice.exception.UserNotFoundException;
import com.example.authservice.model.Lawyer;
import com.example.authservice.model.PaymentType;
import com.example.authservice.model.Subscription;
import com.example.authservice.repository.LawyerRepository;
import com.example.authservice.repository.SubscriptionRepository;
import com.example.authservice.util.MappingUtils;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@Transactional
public class UserService {

    @Autowired
    private LawyerRepository lawyerRepository;
    @Autowired
    private SubscriptionRepository subscriptionRepository;

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

    public List<String> getSubscriptions(String userId) {
        List<Subscription> subscriptions = subscriptionRepository.getAllByUserId(UUID.fromString(userId));
        return subscriptions.stream().map(subscription -> subscription.getPaymentType().toString()).toList();
    }

    public void overrideSubscriptions(Subscriptions subscriptions) {
        UUID userId = UUID.fromString(subscriptions.getUserId());
        List<Subscription> currentSubscriptions = subscriptionRepository.getAllByUserId(userId);

        List<PaymentType> currentPaymentTypes = currentSubscriptions.stream()
                .map(Subscription::getPaymentType)
                .toList();

        List<PaymentType> newPaymentTypes = subscriptions.getServices().stream()
                .map(PaymentType::valueOf)
                .toList();

        List<PaymentType> paymentsToDelete = currentPaymentTypes.stream()
                .filter(type -> !newPaymentTypes.contains(type))
                .toList();

        List<PaymentType> paymentsToAdd = newPaymentTypes.stream()
                .filter(type -> !currentPaymentTypes.contains(type))
                .toList();

        currentSubscriptions.stream()
                .filter(subscription -> paymentsToDelete.contains(subscription.getPaymentType()))
                .forEach(subscriptionRepository::delete);

        paymentsToAdd.stream()
                .map(paymentType -> Subscription.builder()
                        .paymentType(paymentType)
                        .userId(userId)
                        .build())
                .forEach(subscriptionRepository::save);
    }
}
