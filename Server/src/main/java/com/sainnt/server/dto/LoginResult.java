package com.sainnt.server.dto;

import com.sainnt.server.entity.User;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class LoginResult {
    private User user;
    private Result result;

    public enum Result {
        success,
        bad_credentials,
        user_already_logged_in
    }

}
