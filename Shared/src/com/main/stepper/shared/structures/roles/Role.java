package com.main.stepper.shared.structures.roles;

import java.util.ArrayList;
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
        synchronized (allowedFlows) {
            return new ArrayList<>(allowedFlows);
        }
    }
    public void setAllowedFlows(List<String> allowedFlows) {
        synchronized (allowedFlows) {
            this.allowedFlows.clear();
            this.allowedFlows.addAll(allowedFlows);
        }
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
            setAllowedFlows(role.allowedFlows);
        }
        return true;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Role role = (Role) o;

        if (!Objects.equals(name, role.name)) return false;
        if (!Objects.equals(description, role.description)) return false;
        return Objects.equals(allowedFlows, role.allowedFlows);
    }

    @Override
    public int hashCode() {
        int result = name != null ? name.hashCode() : 0;
        result = 31 * result + (description != null ? description.hashCode() : 0);
        result = 31 * result + (allowedFlows != null ? allowedFlows.hashCode() : 0);
        return result;
    }
}
