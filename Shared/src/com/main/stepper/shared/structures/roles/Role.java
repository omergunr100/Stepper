package com.main.stepper.shared.structures.roles;

import java.util.List;
import java.util.Objects;

public class Role {
    private String name;
    private String description;
    private List<String> allowedFlows;

    public Role(String name, String description, List<String> allowedFlows) {
        this.name = name;
        this.description = description;
        this.allowedFlows = allowedFlows;
    }

    public String name() {
        return name;
    }

    public String description() {
        return description;
    }

    public List<String> allowedFlows() {
        return allowedFlows;
    }

    public boolean update(Role role) {
        if (role == null) {
            return false;
        }
        if (role.name != null) {
            name = role.name;
        }
        if (role.description != null) {
            description = role.description;
        }
        if (role.allowedFlows != null) {
            allowedFlows = role.allowedFlows;
        }
        return true;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Role role = (Role) o;

        return Objects.equals(name, role.name);
    }

    @Override
    public int hashCode() {
        return name != null ? name.hashCode() : 0;
    }
}
