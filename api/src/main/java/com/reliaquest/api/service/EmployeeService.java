package com.reliaquest.api.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;


import com.reliaquest.api.dto.*;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class EmployeeService {

    private final RestTemplate restTemplate = new RestTemplate();
    private final String baseUrl = "http://localhost:8111/api/v1/employee";

    public List<Employee> getAllEmployees() {
        ResponseEntity<ServerResponse<List<Employee>>> response = restTemplate.exchange(baseUrl, HttpMethod.GET, null, new ParameterizedTypeReference<ServerResponse<List<Employee>>>() {
        });

        return response.getBody().getData();
    }

    public List<Employee> getEmployeesByNameSearch(String search) {
        return getAllEmployees().stream().filter(e -> e.getName() != null && e.getName().toLowerCase().contains(search.toLowerCase())).collect(Collectors.toList());
    }

    public Employee getEmployeeById(String id) {
        ResponseEntity<ServerResponse<Employee>> response = restTemplate.exchange(baseUrl + "/" + id, HttpMethod.GET,
                null, new ParameterizedTypeReference<>() {
                });

        return response.getBody().getData();
    }

    public Integer getHighestSalaryOfEmployees() {
        return getAllEmployees().stream().map(Employee::getSalary).filter(Objects::nonNull).max(Integer::compareTo).orElse(0);
    }

    public List<String> getTop10HighestEarningEmployeeNames() {
        return getAllEmployees().stream().sorted(Comparator.comparing(Employee::getSalary, Comparator.nullsLast(Comparator.reverseOrder()))).limit(10).map(Employee::getName).collect(Collectors.toList());
    }

    public Employee createEmployee(CreateEmployeeInput input) {
        HttpEntity<CreateEmployeeInput> request = new HttpEntity<>(input);
        log.info("Request to create employee {}", input.getName());
        ResponseEntity<ServerResponse<Employee>> response = restTemplate.exchange(baseUrl, HttpMethod.POST, request, new ParameterizedTypeReference<ServerResponse<Employee>>() {
        });

        return response.getBody().getData();
    }

    public String deleteEmployeeById(String id) {
        Employee employee = getEmployeeById(id);

        String name = employee.getName();

        DeleteEmployeeInput input = new DeleteEmployeeInput(name);
        HttpEntity<DeleteEmployeeInput> request = new HttpEntity<>(input);

        ResponseEntity<Map<String, Object>> response = restTemplate.exchange(
                baseUrl,
                HttpMethod.DELETE,
                request,
                new ParameterizedTypeReference<>() {}
        );

        Map<String, Object> body = response.getBody();
        return body != null ? body.get("data").toString() : null;
    }


}
