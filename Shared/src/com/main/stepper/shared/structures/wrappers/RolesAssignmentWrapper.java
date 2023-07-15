package com.main.stepper.shared.structures.wrappers;

import java.util.ArrayList;
import java.util.List;

public class RolesAssignmentWrapper {
    private String username;
    private ArrayList<String> roles;

    public RolesAssignmentWrapper(String username, List<String> roles) {
        this.username = username;
        this.roles = new ArrayList<>(roles);
    }

    public String username() {
        return username;
    }

    public List<String> roles() {
        return roles;
    }
}
