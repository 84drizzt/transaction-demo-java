package com.demo.transaction.service;

import com.demo.transaction.dto.UserDTO;
import com.demo.transaction.entity.User;
import com.demo.transaction.enumeration.SearchOperation;
import com.demo.transaction.repository.SpecificationBuilder;
import com.demo.transaction.repository.UserRepository;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@Transactional
public class UserService {
    @Autowired
    private UserRepository userRepository;

    @Transactional(readOnly = true)
    public Optional<UserDTO> getUserById(Long id) {
        return userRepository.findById(id)
                .map(user -> {
                    UserDTO userDTO = new UserDTO();
                    BeanUtils.copyProperties(user, userDTO);
                    return userDTO;
                });
    }

    @Transactional(readOnly = true)
    public Page<UserDTO> getUsersByParameters(UserDTO searchDTO, Pageable pageable) {
        SpecificationBuilder<User> builder = new SpecificationBuilder<User>()
                .with("id", searchDTO.getId(), SearchOperation.LIKE)
                .with("username", searchDTO.getUsername(), SearchOperation.LIKE)
                .with("email", searchDTO.getEmail(), SearchOperation.LIKE)
                .with("fullName", searchDTO.getFullName(), SearchOperation.LIKE)
                .with("phoneNumber", searchDTO.getPhoneNumber(), SearchOperation.LIKE);

        return userRepository.findAll(builder.build(), pageable)
                .map(user -> {
                    UserDTO userDTO = new UserDTO();
                    BeanUtils.copyProperties(user, userDTO);
                    return userDTO;
                });
    }

} 