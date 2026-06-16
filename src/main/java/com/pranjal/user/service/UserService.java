package com.pranjal.user.service;

import com.pranjal.user.Role;
import com.pranjal.user.UserEntity;
import com.pranjal.user.UserRepository;
import com.pranjal.user.dto.CreateStaffRequest;
import com.pranjal.user.dto.UpdatePasswordRequest;
import com.pranjal.user.dto.UpdateUserRequest;
import com.pranjal.user.dto.UserSummaryResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserSummaryResponse createStaff(CreateStaffRequest request) {

        if (userRepository.existsByEmail(request.getEmail())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Staff already registered with" +
                    " " + request.getEmail());
        }

        UserEntity userEntity = UserEntity.builder()
                .name(request.getName())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .role(Role.STAFF)
                .isActive(true)
                .build();

        userEntity = userRepository.save(userEntity);
        return toSummary(userEntity);
    }

    public UserSummaryResponse getStaffById(Long id) {
        UserEntity user =
                userRepository.findById(id).orElseThrow(
                        () -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Staff not found")
                );

        return toSummary(user);
    }

    public List<UserSummaryResponse> getAllStaff() {
        return userRepository.findByRole(Role.STAFF)
                .stream()
                .map(this::toSummary)
                .toList();
    }

    public UserSummaryResponse deactivateStaff(Long id) {
        UserEntity user =
                userRepository.findById(id).orElseThrow(
                        () -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Staff not found")
                );
        user.setActive(false);
        userRepository.save(user);
        return toSummary(user);
    }

    public UserSummaryResponse activateStaff(Long id) {
        UserEntity user =
                userRepository.findById(id).orElseThrow(
                        () -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Staff not found")
                );
        user.setActive(true);
        userRepository.save(user);
        return toSummary(user);
    }

    public UserSummaryResponse updateStaff(UpdateUserRequest request, Long id) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Email already in use");
        }
        UserEntity user =
                userRepository.findById(id).orElseThrow(
                        () -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Staff not found")
                );
        user.setEmail(request.getEmail());
        user.setName(request.getName());
        userRepository.save(user);
        return toSummary(user);
    }

    public void changePassword(UpdatePasswordRequest request, Long id) {
        UserEntity user =
                userRepository.findById(id).orElseThrow(
                        () -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Staff not found")
                );

        boolean isValid = passwordEncoder.matches(request.getOldPassword(), user.getPassword());
        if(!isValid) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Password do not match");
        }

        user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        userRepository.save(user);
    }

    private UserSummaryResponse toSummary(UserEntity userEntity) {
        return UserSummaryResponse.builder()
                .id(userEntity.getId())
                .name(userEntity.getName())
                .email(userEntity.getEmail())
                .role(userEntity.getRole())
                .isActive(userEntity.isActive())
                .createdAt(userEntity.getCreatedAt())
                .build();
    }
}
