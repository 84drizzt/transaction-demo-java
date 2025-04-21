package com.demo.transaction.controller;

import com.demo.transaction.dto.UserDTO;
import com.demo.transaction.dto.response.ApiResponse;
import com.demo.transaction.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class UserControllerTest {

    @Mock
    private UserService userService;

    @InjectMocks
    private UserController userController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void listUsersWithParameters_ShouldReturnUsers() {
        // 准备测试数据
        UserDTO userDTO = new UserDTO();
        userDTO.setId(1L);
        userDTO.setUsername("testUser");
        List<UserDTO> expectedUsers = Arrays.asList(userDTO);

        // 配置mock行为
        when(userService.getUsersByParameters(any(UserDTO.class), any(Pageable.class)))
                .thenReturn(expectedUsers);

        // 执行测试
        ResponseEntity<ApiResponse<List<UserDTO>>> response = userController.listUsersWithParameters(
                new UserDTO(), 0, 5, "id", "asc");

        // 验证结果
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(ApiResponse.SUCCESS, response.getBody().getCode());
        assertEquals(ApiResponse.DEFAULT_SUCCESS_MESSAGE, response.getBody().getMessage());
        assertEquals(expectedUsers, response.getBody().getData());
    }

    @Test
    void getUserById_WhenUserExists_ShouldReturnUser() {
        // 准备测试数据
        Long userId = 1L;
        UserDTO expectedUser = new UserDTO();
        expectedUser.setId(userId);
        expectedUser.setUsername("testUser");

        // 配置mock行为
        when(userService.getUserById(userId)).thenReturn(Optional.of(expectedUser));

        // 执行测试
        ResponseEntity<ApiResponse<UserDTO>> response = userController.getUserById(userId);

        // 验证结果
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(0, response.getBody().getCode());
        assertEquals(expectedUser, response.getBody().getData());
    }

    @Test
    void getUserById_WhenUserDoesNotExist_ShouldReturnNotFound() {
        // 准备测试数据
        Long userId = 1L;

        // 配置mock行为
        when(userService.getUserById(userId)).thenReturn(Optional.empty());

        // 执行测试
        ResponseEntity<ApiResponse<UserDTO>> response = userController.getUserById(userId);

        // 验证结果
        assertNotNull(response);
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(ApiResponse.FAILURE, response.getBody().getCode());
        assertEquals("User Not Found", response.getBody().getMessage());
    }

    @Test
    void listUsersWithParameters_ShouldHandlePaginationAndSorting() {
        // 准备测试数据
        UserDTO userDTO = new UserDTO();
        userDTO.setId(1L);
        userDTO.setUsername("testUser");
        List<UserDTO> expectedUsers = Arrays.asList(userDTO);

        // 配置mock行为
        when(userService.getUsersByParameters(any(UserDTO.class), any(Pageable.class)))
                .thenReturn(expectedUsers);

        // 执行测试 - 使用不同的分页和排序参数
        ResponseEntity<ApiResponse<List<UserDTO>>> response = userController.listUsersWithParameters(
                new UserDTO(), 1, 10, "username", "desc");

        // 验证结果
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(ApiResponse.SUCCESS, response.getBody().getCode());
        assertEquals(expectedUsers, response.getBody().getData());

        // 验证service方法被调用时使用了正确的参数
        verify(userService).getUsersByParameters(
                any(UserDTO.class),
                argThat(pageable -> 
                    pageable.getPageNumber() == 1 &&
                    pageable.getPageSize() == 10 &&
                    pageable.getSort().getOrderFor("username").getDirection() == Sort.Direction.DESC
                )
        );
    }
} 