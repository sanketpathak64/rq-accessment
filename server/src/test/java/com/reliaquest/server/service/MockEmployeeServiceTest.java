package com.reliaquest.server.service;

import static org.junit.jupiter.api.Assertions.*;

import com.reliaquest.server.model.CreateMockEmployeeInput;
import com.reliaquest.server.model.MockEmployee;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import net.datafaker.Faker;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class MockEmployeeServiceTest {

    private MockEmployeeService mockEmployeeService;
    private Faker faker = new Faker();

    UUID id = new UUID(1, 1);
    List<MockEmployee> testEmployees = new ArrayList<>(List.of(
            new MockEmployee(id, "Alice Johnson", 1000, 25, "Developer", "alice@example.com"),
            new MockEmployee(id, "Bob Smith", 2000, 30, "Manager", "bob@example.com"),
            new MockEmployee(id, "Alina Brown", 1500, 28, "Designer", "alina@example.com")));

    @BeforeEach
    void setUp() {
        mockEmployeeService = new MockEmployeeService(faker, testEmployees);
    }

    @Test
    void shouldFindEmployeeByNameCaseInsensitive() {
        String searchLower = "alice";
        String searchUpper = "ALICE";
        String searchPartial = "lice";

        List<MockEmployee> resultLower = mockEmployeeService.findEmployeeByNameSearch(searchLower);
        List<MockEmployee> resultUpper = mockEmployeeService.findEmployeeByNameSearch(searchUpper);
        List<MockEmployee> resultPartial = mockEmployeeService.findEmployeeByNameSearch(searchPartial);

        assertTrue(resultLower.stream().allMatch(e -> e.getName().toLowerCase().contains(searchLower)));
        assertTrue(resultUpper.stream().allMatch(e -> e.getName().toLowerCase().contains(searchLower)));
        assertTrue(
                resultPartial.stream().allMatch(e -> e.getName().toLowerCase().contains(searchPartial)));
    }

    @Test
    void shouldGetEmployeeById() {
        MockEmployee firstEmployee = testEmployees.get(0);
        Optional<MockEmployee> employee = mockEmployeeService.findById(firstEmployee.getId());
        assertTrue(employee.isPresent());

        assertEquals(employee.get().getName(), firstEmployee.getName());
    }

    @Test
    void shouldNotGetEmployeeById() {
        Optional<MockEmployee> employee = mockEmployeeService.findById(new UUID(1, 4));
        assertFalse(employee.isPresent());
    }

    @Test
    void shouldGetEmployeeByHighestSalary() {
        Integer salary = mockEmployeeService.findEmployeeByHighestSalary();

        assertEquals(2000, salary);
    }

    @Test
    void shouldReturnTop10HighestEarningEmployeeNames() {
        List<String> top10Names = mockEmployeeService.findTop10HighestEarningEmployeeNames();

        assertEquals("Bob Smith", top10Names.get(0));
        assertEquals("Alina Brown", top10Names.get(1));
        assertEquals("Alice Johnson", top10Names.get(2));

        assertEquals(3, top10Names.size());

        for (String name : top10Names) {
            assertTrue(testEmployees.stream().anyMatch(e -> e.getName().equals(name)));
        }
    }

    @Test
    void shouldCreateEmployee() {
        CreateMockEmployeeInput input = new CreateMockEmployeeInput();
        input.setName("Alice");
        input.setAge(28);
        input.setSalary(75000);
        input.setTitle("Engineer");

        MockEmployee employee = mockEmployeeService.create(input);

        assertNotNull(employee.getId());
        assertEquals("Alice", employee.getName());
        assertEquals(28, employee.getAge());
        assertEquals(75000, employee.getSalary());
        assertEquals("Engineer", employee.getTitle());

        assertTrue(mockEmployeeService.getMockEmployees().contains(employee));
    }
}
