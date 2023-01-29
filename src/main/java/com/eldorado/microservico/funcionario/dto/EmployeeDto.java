package com.eldorado.microservico.funcionario.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;


@Builder
@NoArgsConstructor
@AllArgsConstructor
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class EmployeeDto {

    @NonNull
    private String name;
    @NonNull
    private String email;
    @NonNull
    private String username;
    private char gender;

}
