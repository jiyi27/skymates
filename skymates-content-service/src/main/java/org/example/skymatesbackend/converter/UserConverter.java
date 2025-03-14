package org.example.skymatesbackend.converter;

import org.example.skymatesbackend.dto.UserDTO;
import org.example.skymatesbackend.model.User;
import org.springframework.stereotype.Component;

@Component
public class UserConverter {
    public UserDTO convertToDTO(User user) {
        if (user == null) {
            return null;
        }
        UserDTO dto = new UserDTO();
        dto.setId(user.getId());
        dto.setUsername(user.getUsername());
        dto.setEmail(user.getEmail());
        dto.setCreatedAt(user.getCreatedAt());
        return dto;
    }
}

