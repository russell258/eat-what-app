package com.eatwhat.backend.batch;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserCsvRecord {
    private String username;
    private String email;
    private String role;

    public UserCsvRecord() {
    }

    public UserCsvRecord(String username, String email, String role) {
        this.username = username;
        this.email = email;
        this.role = role;
    }

}
