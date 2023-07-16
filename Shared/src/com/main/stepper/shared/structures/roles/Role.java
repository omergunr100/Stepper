package com.main.stepper.shared.structures.roles;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Role {
    private String name;
    private String description;
    private List<String> allowedFlows;
    private Boolean automatic;
    private Boolean isLocal;

    public Role(String name, String description) {
        this(name, description, new ArrayList<>(), false, true);
    }

    public Role(String name, String description, List<String> allowedFlows) {
        this(name, description, allowedFlows, false, false);
    }

    public Role(String name, String description, List<String> allowedFlows, Boolean automatic, Boolean isLocal) {
        this.name = name;
        this.description = description;
        this.allowedFlows = allowedFlows;
        this.automatic = automatic;
        this.isLocal = isLocal;
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

    public Boolean isAutomatic() {
        return automatic;
    }

    public Boolean isLocal() { return isLocal; }
    public void setLocal(Boolean isLocal) { this.isLocal = isLocal; }

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
        if (!Objects.equals(allowedFlows, role.allowedFlows)) return false;
        if (!Objects.equals(automatic, role.automatic)) return false;
        return Objects.equals(isLocal, role.isLocal);
    }

    @Override
    public int hashCode() {
        int result = name != null ? name.hashCode() : 0;
        result = 31 * result + (description != null ? description.hashCode() : 0);
        result = 31 * result + (allowedFlows != null ? allowedFlows.hashCode() : 0);
        result = 31 * result + (automatic != null ? automatic.hashCode() : 0);
        result = 31 * result + (isLocal != null ? isLocal.hashCode() : 0);
        return result;
    }
}
