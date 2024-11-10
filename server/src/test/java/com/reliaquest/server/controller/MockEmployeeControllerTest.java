package com.reliaquest.server.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.reliaquest.server.model.MockEmployee;
import com.reliaquest.server.service.MockEmployeeService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;


import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;

@WebMvcTest(MockEmployeeController.class)
public class MockEmployeeControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private MockEmployeeService mockEmployeeService;

    private final ObjectMapper objectMapper = new ObjectMapper();


    @Test
    public void getEmployees_ReturnsEmployeeList() throws Exception {
        // Arrange
        List<MockEmployee> mockEmployees = Arrays.asList(
                new MockEmployee(new UUID(1, 1), "John", 10, 21,"Tester", "Engineering@gmail.com"),
                new MockEmployee(new UUID(1,1), "Jane", 20, 22, "SDE", "Marketing@gmil.com")
        );

        when(mockEmployeeService.getMockEmployees()).thenReturn(mockEmployees);

        // Act & Assert
        //TODO: Object mapper can be used with recursive comparison to avoid this many asserts
        mockMvc.perform(get("/api/v1/employees"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data[0].employee_name").value("John"))
                .andExpect(jsonPath("$.data[0].employee_salary").value(10))
                .andExpect(jsonPath("$.data[0].employee_age").value(21))
                .andExpect(jsonPath("$.data[0].employee_title").value("Tester"))
                .andExpect(jsonPath("$.data[0].employee_email").value("Engineering@gmail.com"))
                .andExpect(jsonPath("$.data[1].employee_name").value("Jane"))
                .andExpect(jsonPath("$.data[1].employee_salary").value(20))
                .andExpect(jsonPath("$.data[1].employee_age").value(22))
                .andExpect(jsonPath("$.data[1].employee_title").value("SDE"))
                .andExpect(jsonPath("$.data[1].employee_email").value("Marketing@gmil.com"));
    }
}