package com.eldorado.microservico.funcionario.domain.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.UUID;


@Data
@AllArgsConstructor
@NoArgsConstructor
@Document("employee")
public class EmployeeEntity {

    @Id
    private UUID id;
    @NonNull
    private String name;
    private char gender;
    @NonNull
    private String username;
    private String password;

}
