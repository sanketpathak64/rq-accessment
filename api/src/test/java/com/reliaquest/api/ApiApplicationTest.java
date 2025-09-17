package com.reliaquest.api;

import static org.junit.jupiter.api.Assertions.*;

import com.reliaquest.api.dto.CreateEmployeeInput;
import com.reliaquest.api.dto.Employee;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class EmployeeControllerTest {

    @Autowired
    private TestRestTemplate restTemplate;

    @LocalServerPort
    private int port;

    private String baseUrl() {
        return "http://localhost:" + port + "/api/v1/employees";
    }

    @Test
    void shouldGetEmployeeById() {
        ResponseEntity<Employee[]> listResponse = restTemplate.getForEntity(baseUrl(), Employee[].class);
        Employee[] employees = listResponse.getBody();
        assertNotNull(employees);
        assertTrue(employees.length > 0);

        Employee first = employees[0];
        String id = String.valueOf(first.getId());
        String name = first.getName();

        ResponseEntity<Employee> response = restTemplate.getForEntity(baseUrl() + "/" + id, Employee.class);
        Employee emp = response.getBody();

        assertEquals(200, response.getStatusCodeValue());
        assertNotNull(emp);
        assertEquals(name, emp.getName());
    }

    @Test
    void shouldGetAllEmployees() {
        ResponseEntity<Employee[]> listResponse = restTemplate.getForEntity(baseUrl(), Employee[].class);
        Employee[] employees = listResponse.getBody();
        assertNotNull(employees);
        assertTrue(employees.length > 0);
    }

    @Test
    void shouldCreateEmployee() {
        CreateEmployeeInput input = new CreateEmployeeInput();
        input.setName("Test User");
        input.setAge(30);
        input.setSalary(80000);
        input.setTitle("Developer");

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<CreateEmployeeInput> request = new HttpEntity<>(input, headers);

        ResponseEntity<Employee> response =
                restTemplate.exchange(baseUrl(), HttpMethod.POST, request, new ParameterizedTypeReference<>() {});

        assertEquals(HttpStatus.OK.value(), response.getStatusCodeValue());

        Employee created = response.getBody();
        assertNotNull(created);
        assertEquals("Test User", created.getName());
        assertEquals(30, created.getAge());
        assertEquals(80000, created.getSalary());
        assertEquals("Developer", created.getTitle());
    }

    @Test
    void shouldDeleteEmployeeById() {
        ResponseEntity<Employee[]> listResponse = restTemplate.getForEntity(baseUrl(), Employee[].class);
        Employee[] employees = listResponse.getBody();
        assertNotNull(employees);
        assertTrue(employees.length > 0);

        Employee first = employees[0];
        String id = String.valueOf(first.getId());

        ResponseEntity<Boolean> deleteResponse =
                restTemplate.exchange(baseUrl() + "/" + id, HttpMethod.DELETE, null, Boolean.class);

        assertEquals(HttpStatus.OK.value(), deleteResponse.getStatusCodeValue());
    }

    @Test
    void shouldGetTop10HighestEarningEmployees() {
        ResponseEntity<List<String>> response = restTemplate.exchange(
                baseUrl() + "/topTenHighestEarningEmployeeNames",
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<>() {});

        assertEquals(HttpStatus.OK.value(), response.getStatusCodeValue());

        List<String> topEmployees = response.getBody();
        assertNotNull(topEmployees);
        assertTrue(topEmployees.size() <= 10, "Should return at most 10 employees");
    }
}
