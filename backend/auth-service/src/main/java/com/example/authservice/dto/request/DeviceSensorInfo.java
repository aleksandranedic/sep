package com.example.authservice.dto.request;

import com.example.authservice.validation.NoHTMLTags;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class DeviceSensorInfo {
    @NotBlank
    @NoHTMLTags
    private String name;

    @NotBlank
    @NoHTMLTags
    private String unit;

    @Pattern(regexp = "^(int|long|float|string|double|boolean)$", message = "Invalid data type. Allowed types: int,long,float,string,double,boolean")
    private String type;
}
