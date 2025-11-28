package com.openclassrooms.etudiant.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.openclassrooms.etudiant.entities.Student;
import com.openclassrooms.etudiant.repository.StudentRepository;
import com.openclassrooms.etudiant.service.StudentService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@Testcontainers
class StudentControllerTest {

    private static final String FIRST_NAME = "John";
    private static final String LAST_NAME = "Doe";
    private static final String LEVEL = "L1";
    private static final String MATTER = "Math";

    @Container
    private static final MySQLContainer<?> mySQLContainer = new MySQLContainer<>("mysql:8.0")
            .withTmpFs(java.util.Collections.singletonMap("/var/lib/mysql", "rw"));


    @Autowired
    private StudentService studentService;
    @Autowired
    private StudentRepository studentRepository;
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private MockMvc mockMvc;

    // apparemment non necessaire à partir de springboot 3.1+
    @DynamicPropertySource
    static void configureTestProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.driver-class-name", () -> "com.mysql.cj.jdbc.Driver");
        registry.add("spring.datasource.url", () -> mySQLContainer.getJdbcUrl());
        registry.add("spring.datasource.username", () -> mySQLContainer.getUsername());
        registry.add("spring.datasource.password", () -> mySQLContainer.getPassword());
        registry.add("spring.jpa.hibernate.ddl-auto", () -> "create");

    }

    @AfterEach
    public void afterEach() {
        studentRepository.deleteAll();
    }

    @Test
    public void testSaveStudent() throws Exception {
        // GIVEN
        Student student = new Student();
        student.setFirstName(FIRST_NAME);
        student.setLastName(LAST_NAME);
        student.setLevel(LEVEL);
        student.setMatter(MATTER);

        // WHEN & THEN
        mockMvc.perform(MockMvcRequestBuilders.post("/api/students")
                .content(objectMapper.writeValueAsString(student))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(jsonPath("$.firstName", org.hamcrest.Matchers.is(FIRST_NAME)))
                .andExpect(jsonPath("$.lastName", org.hamcrest.Matchers.is(LAST_NAME)))
                .andExpect(jsonPath("$.level", org.hamcrest.Matchers.is(LEVEL)))
                .andExpect(jsonPath("$.matter", org.hamcrest.Matchers.is(MATTER)));
    }

    @Test
    public void testFetchStudentList() throws Exception {
        // GIVEN
        Student student1 = new Student();
        student1.setFirstName(FIRST_NAME);
        student1.setLastName(LAST_NAME);
        student1.setLevel(LEVEL);
        student1.setMatter(MATTER);
        studentService.saveStudent(student1);

        Student student2 = new Student();
        student2.setFirstName("Jane");
        student2.setLastName(LAST_NAME);
        student2.setLevel("L2");
        student2.setMatter("Physics");
        studentService.saveStudent(student2);

        // WHEN & THEN
        mockMvc.perform(MockMvcRequestBuilders.get("/api/students"))
                .andDo(print())
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(jsonPath("$", org.hamcrest.Matchers.hasSize(2)))
                .andExpect(jsonPath("$[0].firstName", org.hamcrest.Matchers.is(FIRST_NAME)))
                .andExpect(jsonPath("$[1].firstName", org.hamcrest.Matchers.is("Jane")));
    }

    @Test
    public void testGetStudentById() throws Exception {
        // GIVEN
        Student student = new Student();
        student.setFirstName(FIRST_NAME);
        student.setLastName(LAST_NAME);
        student.setLevel(LEVEL);
        student.setMatter(MATTER);
        Student savedStudent = studentService.saveStudent(student);

        // WHEN & THEN
        mockMvc.perform(MockMvcRequestBuilders.get("/api/students/" + savedStudent.getId()))
                .andDo(print())
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(jsonPath("$.firstName", org.hamcrest.Matchers.is(FIRST_NAME)));
    }

    @Test
    public void testGetStudentById_NotFound() throws Exception {
        // GIVEN
        // No student saved

        // WHEN & THEN
        mockMvc.perform(MockMvcRequestBuilders.get("/api/students/1"))
                .andDo(print())
                .andExpect(MockMvcResultMatchers.status().isOk());
    }

    @Test
    public void testUpdateStudent() throws Exception {
        // GIVEN
        Student student = new Student();
        student.setFirstName(FIRST_NAME);
        student.setLastName(LAST_NAME);
        student.setLevel(LEVEL);
        student.setMatter(MATTER);
        Student savedStudent = studentService.saveStudent(student);

        Student updateData = new Student();
        updateData.setFirstName(FIRST_NAME);
        updateData.setLastName("DoeUpdated");
        updateData.setLevel(LEVEL);
        updateData.setMatter("UpdatedMath");

        // WHEN & THEN
        mockMvc.perform(MockMvcRequestBuilders.put("/api/students/" + savedStudent.getId())
                .content(objectMapper.writeValueAsString(updateData))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(jsonPath("$.lastName", org.hamcrest.Matchers.is("DoeUpdated")))
                .andExpect(jsonPath("$.matter", org.hamcrest.Matchers.is("UpdatedMath")));
    }

    @Test
    public void testDeleteStudentById() throws Exception {
        // GIVEN
        Student student = new Student();
        student.setFirstName(FIRST_NAME);
        student.setLastName(LAST_NAME);
        student.setLevel(LEVEL);
        student.setMatter(MATTER);
        Student savedStudent = studentService.saveStudent(student);

        // WHEN & THEN
        mockMvc.perform(MockMvcRequestBuilders.delete("/api/students/" + savedStudent.getId()))
                .andDo(print())
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().string("Deleted Successfully"));
    }
}
