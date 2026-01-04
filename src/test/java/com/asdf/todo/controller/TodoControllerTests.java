package com.asdf.todo.controller;

import com.asdf.todo.model.Todo;
import com.asdf.todo.service.TodoService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Collections;

import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(SpringExtension.class)
@WebMvcTest(TodoController.class)
public class TodoControllerTests {
    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private TodoService todoService;

    @Test
    public void testGetTodoById() throws Exception {
        Todo todo = new Todo();
        todo.setId(1L);
        todo.setTitle("Test Todo");

        given(todoService.findById(1L)).willReturn(todo);

        mockMvc.perform(get("/api/todos/v1/1")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.title").value("Test Todo"));
    }

    @Test
    public void testGetAllTodos() throws Exception {
        given(todoService.findAll()).willReturn(Collections.emptyList());

        mockMvc.perform(get("/api/todos/v1")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());

        given(todoService.findAll()).willReturn(
                Collections.singletonList(new Todo(1L, "Test Todo", "Description", false))
        );

        mockMvc.perform(get("/api/todos/v1")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1L))
                .andExpect(jsonPath("$[0].title").value("Test Todo"));
    }
}
