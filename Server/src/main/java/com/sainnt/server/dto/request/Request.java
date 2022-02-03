package com.sainnt.server.dto.request;

import com.sainnt.server.entity.User;
import lombok.Data;

@Data
public abstract class Request {
    private User user;
}
