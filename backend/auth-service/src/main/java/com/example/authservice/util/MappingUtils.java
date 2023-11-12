package com.example.authservice.util;

import com.example.authservice.dto.response.UserDetailResponse;
import com.example.authservice.dto.response.UserInfoResponse;
import com.example.authservice.model.Lawyer;
import org.modelmapper.ModelMapper;

import java.util.List;
import java.util.stream.Collectors;

public class MappingUtils {
    private static final ModelMapper modelMapper = new ModelMapper();

    public static UserInfoResponse toUserInfoResponse(Lawyer Lawyer) {
        return modelMapper.map(Lawyer, UserInfoResponse.class);
    }

    public static List<UserInfoResponse> toUserInfoResponseList(List<Lawyer> users) {
        return users.stream()
                .map(MappingUtils::toUserInfoResponse)
                .collect(Collectors.toList());
    }

    public static UserDetailResponse toUserDetailsResponse(Lawyer Lawyer) {
        return modelMapper.map(Lawyer, UserDetailResponse.class);
    }
}
