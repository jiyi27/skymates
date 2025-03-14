package org.example.skymatesuserservice.converter;

import org.example.skymatesuserservice.dto.UserDTO;
import org.example.skymatesuserservice.model.User;
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
