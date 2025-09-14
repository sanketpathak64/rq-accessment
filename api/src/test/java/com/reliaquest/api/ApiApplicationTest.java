package com.reliaquest.api;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import com.reliaquest.server.model.MockEmployee;
import com.reliaquest.server.service.MockEmployeeService;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.ResponseEntity;

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

        System.out.println("Port: " + port);

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
}
