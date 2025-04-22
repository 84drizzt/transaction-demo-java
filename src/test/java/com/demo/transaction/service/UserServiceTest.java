
package com.demo.transaction.service;

import com.demo.transaction.dto.UserDTO;
import com.demo.transaction.entity.User;
import com.demo.transaction.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

import java.util.Collections;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserService userService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void getUserById_ShouldReturnUserDTO_WhenUserExists() {
        // Arrange
        Long userId = 1L;
        User user = new User();
        user.setId(userId);
        user.setUsername("testuser");

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));

        // Act
        Optional<UserDTO> result = userService.getUserById(userId);

        // Assert
        assertTrue(result.isPresent());
        assertEquals(userId, result.get().getId());
        assertEquals("testuser", result.get().getUsername());
    }

    @Test
    void getUserById_ShouldReturnEmpty_WhenUserNotExists() {
        // Arrange
        Long userId = 1L;
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        // Act
        Optional<UserDTO> result = userService.getUserById(userId);

        // Assert
        assertFalse(result.isPresent());
    }

    @Test
    void getUsersByParameters_ShouldReturnUserPage() {
        // Arrange
        UserDTO searchDTO = new UserDTO();
        searchDTO.setUsername("test");

        User user = new User();
        user.setId(1L);
        user.setUsername("testuser");

        Page<User> userPage = new PageImpl<>(Collections.singletonList(user));
        when(userRepository.findAll((Specification<User>) any(), any(Pageable.class))).thenReturn(userPage);

        // Act
        Page<UserDTO> result = userService.getUsersByParameters(searchDTO, Pageable.unpaged());

        // Assert
        assertEquals(1, result.getContent().size());
        assertEquals(1L, result.getContent().get(0).getId());
        assertEquals("testuser", result.getContent().get(0).getUsername());
        assertEquals(1, result.getTotalElements());
    }
}
