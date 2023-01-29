package com.eldorado.microservico.funcionario.service;


import com.eldorado.commons.dto.UserLoginDto;
import com.eldorado.commons.exceptions.NotFoundException;
import com.eldorado.microservico.funcionario.dto.CustomEmployeeList;
import com.eldorado.microservico.funcionario.domain.model.EmployeeEntity;
import com.eldorado.microservico.funcionario.dto.MessageDto;
import com.eldorado.microservico.funcionario.dto.EmployeeDto;
import com.eldorado.microservico.funcionario.publisher.EmployeePublisher;
import com.eldorado.microservico.funcionario.domain.repository.EmployeeRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.tomcat.util.codec.binary.Base64;
import org.modelmapper.ModelMapper;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import java.util.List;
import java.util.UUID;


@Service
@Slf4j
@RequiredArgsConstructor
public class EmployeeService {
    private static final String MESSAGE_WITH_PASSWORD = "Cadastro realizado\nUsuario: %s\nSenha: %s";
    private static final String MESSAGE_WITHOUT_PASSWORD = "Cadastro realizado\nUsuario: %s";
    private static final String SUBJECT = "NÃO RESPONDA";

    private final ModelMapper modelMapper;

    private final ObjectMapper objectMapper;

    private final EmployeeRepository employeeRepository;

    private final EmployeePublisher employeePublisher;
    private final PasswordEncoder passwordEncoder;


    public EmployeeDto createEmployee(@Validated EmployeeDto employeeDto) {
        var employeeEntity = modelMapper.map(employeeDto, EmployeeEntity.class);
        employeeEntity.setId(UUID.randomUUID());
        var password = passwordGenerator();
        employeeEntity.setPassword(passwordEncoder.encode(password));
        employeeEntity = employeeRepository.save(employeeEntity);
        log.info("Employee successfully saved {}", employeeEntity);
//        sendMessage(employeeDto, password);

        return employeeDto;
    }

    public EmployeeDto updateEmployee(@Validated EmployeeDto employeeDto) {
        var employeeInDB = employeeRepository.findByUsername(employeeDto.getUsername())
                .orElseThrow(() -> new NotFoundException("Employee not found"));

        var employeeEntity = modelMapper.map(employeeDto, EmployeeEntity.class);

        employeeInDB.setName(employeeEntity.getName());
        employeeInDB.setPassword(employeeEntity.getPassword());
        employeeInDB.setGender(employeeEntity.getGender());
        employeeInDB.setPassword(employeeEntity.getPassword());

        employeeEntity = employeeRepository.save(employeeInDB);
        log.info("Employee successfully updated {}", employeeEntity);
        sendMessage(employeeDto);

        return employeeDto;
    }

    public List<EmployeeDto> getAllEmployees() {
        return modelMapper.map(employeeRepository.findAll(), CustomEmployeeList.class);
    }

    public EmployeeDto getEmployeeById(UUID employeeId) {
        var employee = employeeRepository.findById(employeeId)
                .orElseThrow(() -> new NotFoundException("Employee not found"));
        return modelMapper.map(employee, EmployeeDto.class);
    }

    @SneakyThrows
    private void sendMessage(EmployeeDto employeeDto, String password) {
        var message = MessageDto.builder().to(employeeDto.getUsername()).message(String.format(MESSAGE_WITH_PASSWORD, employeeDto.getUsername(), password)).subject(SUBJECT).build();

        employeePublisher.sendToQueue(objectMapper.writeValueAsString(message));
        log.info("Message to queue {}", message);
    }

    @SneakyThrows
    private void sendMessage(EmployeeDto employeeDto) {
        var message = MessageDto.builder().to(employeeDto.getUsername()).message(String.format(MESSAGE_WITHOUT_PASSWORD, employeeDto.getUsername())).subject(SUBJECT).build();

        employeePublisher.sendToQueue(objectMapper.writeValueAsString(message));
        log.info("Message to queue {}", message);
    }

    private String passwordGenerator() {
        RandomStringUtils.randomAlphabetic(10);
        return Base64.encodeBase64String(RandomStringUtils.randomAlphanumeric(10).getBytes());
    }

    @SneakyThrows
    public EmployeeDto login(UserLoginDto userLoginDto) {

        log.info("Retrieving information from login {}", userLoginDto.getUserName());

        var user = employeeRepository.findByUsername(userLoginDto.getUserName())
                .orElseThrow(() -> new NotFoundException("Invalid Access"));

        return modelMapper.map(user, EmployeeDto.class);

    }
}
