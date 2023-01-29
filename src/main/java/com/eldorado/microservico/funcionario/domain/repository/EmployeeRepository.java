package com.eldorado.microservico.funcionario.domain.repository;

import com.eldorado.microservico.funcionario.domain.model.EmployeeEntity;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;
import java.util.UUID;

public interface EmployeeRepository extends MongoRepository<EmployeeEntity, UUID> {
    Optional<EmployeeEntity> findByUsername(String username);
    Optional<EmployeeEntity> findById(UUID id);
}
