package com.reliaquest.api.dto;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreateEmployeeInput {

    @NotBlank
    private String name;

    @NotNull
    @Min(1)
    private Integer salary;

    @NotNull
    @Min(16)
    @Max(75)
    private Integer age;

    @NotBlank
    private String title;
}

