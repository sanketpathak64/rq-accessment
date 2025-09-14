package com.reliaquest.api;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.http.HttpStatus.OK;

import com.reliaquest.server.model.CreateMockEmployeeInput;
import com.reliaquest.server.model.DeleteMockEmployeeInput;
import com.reliaquest.server.model.MockEmployee;
import com.reliaquest.server.service.MockEmployeeService;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class ApiApplicationTest {

    @Autowired
    private TestRestTemplate restTemplate;

    @LocalServerPort
    private int port;

    @Autowired
    private MockEmployeeService mockEmployeeService;

    String baseUrl = "http://localhost:";

    @Test
    void shouldGetListOfEmployees() {
        List<MockEmployee> employees = mockEmployeeService.getMockEmployees();
        assertNotNull(employees);
        ResponseEntity<Map> response = restTemplate.getForEntity(baseUrl + port + "/api/v1/employee", Map.class);

        assertEquals(200, response.getStatusCodeValue());

        Map<String, Object> body = response.getBody();
        assertNotNull(body);
        assertNotNull(body.get("status"));

        List<Map<String, Object>> data = (List<Map<String, Object>>) body.get("data");
        assertNotNull(data);

        for (Map<String, Object> emp : data) {
            assertNotNull(emp.get("id"));
            assertNotNull(emp.get("employee_name"));
            assertNotNull(emp.get("employee_email"));
            assertNotNull(emp.get("employee_salary"));
            assertNotNull(emp.get("employee_age"));
            assertNotNull(emp.get("employee_title"));
        }
    }

    @Test
    void shouldReturnEmployeeWhenNameSearchMatches() {
        List<MockEmployee> employees = mockEmployeeService.getMockEmployees();
        assertFalse(employees.isEmpty());

        String firstEmployeeName = employees.get(0).getName();
        String searchFragment = firstEmployeeName.substring(0, 3);

        ResponseEntity<Map> response =
                restTemplate.getForEntity(baseUrl + port + "/api/v1/employee/search/" + searchFragment, Map.class);

        assertEquals(200, response.getStatusCodeValue());

        List<Map<String, Object>> data =
                (List<Map<String, Object>>) response.getBody().get("data");
        assertTrue(data.stream().anyMatch(e -> e.get("employee_name").equals(firstEmployeeName)));
    }

    @Test
    void shouldReturnEmployeeById() {
        List<MockEmployee> employees = mockEmployeeService.getMockEmployees();
        assertFalse(employees.isEmpty());

        MockEmployee firstEmployee = employees.get(0);

        ResponseEntity<Map> response =
                restTemplate.getForEntity(baseUrl + port + "/api/v1/employee/" + firstEmployee.getId(), Map.class);

        assertEquals(200, response.getStatusCodeValue());

        Map<String, Object> data = (Map<String, Object>) response.getBody().get("data");

        assertTrue(data.get("employee_name").equals(firstEmployee.getName()));
    }

    @Test
    void shouldReturnHighestSalary() {
        List<MockEmployee> employees = mockEmployeeService.getMockEmployees();
        assertFalse(employees.isEmpty());

        Integer salary = employees.stream()
                .max(Comparator.comparingInt(MockEmployee::getSalary))
                .map(MockEmployee::getSalary)
                .orElse(0);

        ResponseEntity<Map> response =
                restTemplate.getForEntity(baseUrl + port + "/api/v1/employee/highestSalary", Map.class);

        assertEquals(200, response.getStatusCodeValue());

        Integer data = (Integer) response.getBody().get("data");

        assertTrue(data.equals(salary));
    }

    @Test
    void shouldReturnTop10HighestEarningEmployeeNames() {
        List<String> expectedNames = mockEmployeeService.findTop10HighestEarningEmployeeNames();
        assertFalse(expectedNames.isEmpty());

        ResponseEntity<Map> response = restTemplate.getForEntity(
                baseUrl + port + "/api/v1/employee/topTenHighestEarningEmployeeNames", Map.class);

        assertEquals(200, response.getStatusCodeValue());

        List<String> data = (List<String>) response.getBody().get("data");

        assertEquals(expectedNames.size(), data.size());

        for (String name : expectedNames) {
            assertTrue(data.contains(name));
        }
    }

    @Test
    void shouldCreateEmployee() {
        CreateMockEmployeeInput input = new CreateMockEmployeeInput();
        input.setName("Alice");
        input.setAge(28);
        input.setSalary(75000);
        input.setTitle("Engineer");

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<CreateMockEmployeeInput> request = new HttpEntity<>(input, headers);

        ResponseEntity<Map> response =
                restTemplate.postForEntity(baseUrl + port + "/api/v1/employee", request, Map.class);

        assertEquals(200, response.getStatusCodeValue());

        Map<String, Object> body = response.getBody();
        assertNotNull(body);
        Map<String, Object> data = (Map<String, Object>) body.get("data");
        assertNotNull(data);

        assertEquals("Alice", data.get("employee_name"));
        assertEquals(28, data.get("employee_age"));
        assertEquals(75000, data.get("employee_salary"));
        assertEquals("Engineer", data.get("employee_title"));
        assertNotNull(data.get("id"));
    }

    @Test
    void shouldDeleteEmployeeByName() {
        MockEmployee employeeToDelete = mockEmployeeService.getMockEmployees().get(0);

        DeleteMockEmployeeInput input = new DeleteMockEmployeeInput();
        input.setName(employeeToDelete.getName());

        HttpEntity<DeleteMockEmployeeInput> request = new HttpEntity<>(input);

        ResponseEntity<Map> response =
                restTemplate.exchange(baseUrl + port + "/api/v1/employee", HttpMethod.DELETE, request, Map.class);

        assertEquals(OK.value(), response.getStatusCodeValue());

        List<MockEmployee> remainingEmployees = mockEmployeeService.getMockEmployees();
        assertFalse(
                remainingEmployees.stream().anyMatch(e -> e.getName().equalsIgnoreCase(employeeToDelete.getName())));
    }
}
