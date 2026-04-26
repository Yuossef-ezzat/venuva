package com.example.venuva.Core.ServiceLayer;

import org.springframework.stereotype.Service;

import com.example.venuva.Core.Domain.Abstractions.Result;
import com.example.venuva.Core.Domain.Models.UserDetails.Roles;
import com.example.venuva.Infrastructure.PresistenceLayer.Repos.UserRepository;
import com.example.venuva.Shared.Dtos.UpdatedOrganzier;
import com.example.venuva.Shared.Dtos.OrganizerDto;

import java.util.List;
import java.util.stream.Collectors;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class AdminSevice {

    private final UserRepository userRepository;

    public Result<Boolean> updateOrganizer(int userId,UpdatedOrganzier updatedOrganzier) {
        log.info("Updating organizer information for user: {}", userId);
        userRepository.findById(userId).ifPresent(user -> {
            user.setUsername(updatedOrganzier.getNewName() != null ? updatedOrganzier.getNewName() : user.getUsername());
            user.setEmail(updatedOrganzier.getNewEmail() != null ? updatedOrganzier.getNewEmail() : user.getEmail());
            userRepository.save(user);
        });
        return Result.success(true);
    }

    public Result<Boolean> deleteOrganizer(int userId) {
        log.info("Deleting user with ID: {}", userId);
        if (userRepository.existsById(userId)) {
            userRepository.deleteById(userId);
            return Result.success(true);
        } else {
            log.warn("User with ID {} not found for deletion", userId);
            return Result.failure(new com.example.venuva.Core.Domain.Abstractions.Error("User not found"));
        }
    }


    public Result<List<OrganizerDto>> getAllOrganizers() {
        log.info("Retrieving all organizers");
        var organizers = userRepository.findByRole(Roles.ROLE_ORGANIZER)
                .stream()
                .map(user -> {
                    OrganizerDto dto = new OrganizerDto();
                    dto.setId(user.getId()); 
                    dto.setName(user.getUsername());
                    dto.setEmail(user.getEmail());
                    return dto;
                })
                .collect(Collectors.toList());
        return Result.success(organizers);
    }

    public Result<OrganizerDto> getOrganizerById(int userId) {
        log.info("Retrieving organizer with ID: {}", userId);
        return userRepository.findById(userId)
                .filter(user -> user.getRole() == Roles.ROLE_ORGANIZER)
                .map(user -> {
                    OrganizerDto dto = new OrganizerDto();
                    dto.setId(user.getId());
                    dto.setName(user.getUsername());
                    dto.setEmail(user.getEmail());
                    return Result.success(dto);
                })
                .orElseGet(() -> {
                    log.warn("Organizer with ID {} not found", userId);
                    return Result.failure(new com.example.venuva.Core.Domain.Abstractions.Error("Organizer not found"));
                });
    }
}
