package com.reliaquest.api.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.reliaquest.api.dto.CreateEmployeeInput;
import com.reliaquest.api.dto.DeleteEmployeeInput;
import com.reliaquest.api.dto.Employee;
import com.reliaquest.api.service.EmployeeService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/employees")
@RequiredArgsConstructor
@Slf4j
public class EmployeeController implements IEmployeeController {

    private final EmployeeService employeeService;

    @GetMapping
    public ResponseEntity<List<Employee>> getAllEmployees() {
        return ResponseEntity.ok(employeeService.getAllEmployees());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Employee> getEmployeeById(@PathVariable String id) {
        return ResponseEntity.ok(employeeService.getEmployeeById(id));
    }

    @GetMapping("/search/{name}")
    public ResponseEntity<List<Employee>> getEmployeesByNameSearch(@PathVariable String name) {
        return ResponseEntity.ok(employeeService.getEmployeesByNameSearch(name));
    }

    @GetMapping("/highestSalary")
    public ResponseEntity<Integer> getHighestSalaryOfEmployees() {
        return ResponseEntity.ok(employeeService.getHighestSalaryOfEmployees());
    }

    @Override
    @PostMapping
    public ResponseEntity<Employee> createEmployee(@RequestBody Object employeeInput) {
        ObjectMapper mapper = new ObjectMapper();
        CreateEmployeeInput input = mapper.convertValue(employeeInput, CreateEmployeeInput.class);

        return ResponseEntity.ok(employeeService.createEmployee(input));
    }

    @GetMapping("/topTenHighestEarningEmployeeNames")
    public ResponseEntity<List<String>> getTopTenHighestEarningEmployeeNames() {
        return ResponseEntity.ok(employeeService.getTop10HighestEarningEmployeeNames());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteEmployeeById(@PathVariable String id) {
        return ResponseEntity.ok(employeeService.deleteEmployeeById(id));
    }
}


