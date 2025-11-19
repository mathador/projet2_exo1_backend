package com.openclassrooms.etudiant.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper=true)
public class RegisterDTO extends LoginRequestDTO {
    @NotBlank
    private String firstName;
    @NotBlank
    private String lastName;
}
