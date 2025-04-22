package com.demo.transaction.controller;

import com.demo.transaction.dto.UserDTO;
import com.demo.transaction.dto.response.ApiResponse;
import com.demo.transaction.service.UserService;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Pattern;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@Validated
@RestController
@RequestMapping("/api/users")
public class UserController {

    @Autowired
    UserService userService;

    /* TODO: when need to implement:
           - createUser
           - updateUser
           - deleteUser
    */

    @GetMapping
    public ResponseEntity<ApiResponse<Page<UserDTO>>> listUsersWithParameters(
            UserDTO userDTO,
            @DecimalMax(value = "100", message = "page must be 0~100")
            @RequestParam(defaultValue = "0") int page,
            @DecimalMin(value = "1", message = "size must be 1~100")
            @DecimalMax(value = "100", message = "size must be 1~100")
            @RequestParam(defaultValue = "5") int size,
            @RequestParam(defaultValue = "id") String sortField,
            @Pattern(regexp = "asc|desc")
            @RequestParam(defaultValue = "asc") String direction) {

        Sort sort = Sort.by(Sort.Direction.fromString(direction), sortField);
        Pageable pageable = PageRequest.of(page, size, sort);
        Page<UserDTO> users = userService.getUsersByParameters(userDTO, pageable);
        return ResponseEntity.ok(ApiResponse.success(users));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<UserDTO>> getUserById(@PathVariable Long id) {
        return userService.getUserById(id)
                .map(user -> ResponseEntity.ok(ApiResponse.success(user)))
                .orElse(ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(ApiResponse.error("User Not Found")));
    }



} 