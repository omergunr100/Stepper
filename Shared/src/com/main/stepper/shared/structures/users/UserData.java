package com.main.stepper.shared.structures.users;

import com.main.stepper.shared.structures.roles.Role;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

public class UserData {
    private String name;
    private boolean isManager;
    private final List<Role> roles;
    private final List<UUID> flowExecutionHistory;

    public UserData(String name) {
        this.name = name;
        this.isManager = false;
        roles = new ArrayList<>();
        flowExecutionHistory = new ArrayList<>();
    }

    public String name() {
        return name;
    }

    public boolean isManager() {
        return isManager;
    }
    public void setManager(boolean manager) {
        isManager = manager;
    }

    public List<Role> roles() {
        synchronized (roles) {
            return new ArrayList<>(roles);
        }
    }

    public boolean addRole(Role role) {
        synchronized (roles) {
            if (roles.contains(role)) {
                return false;
            }
            roles.add(role);
            return true;
        }
    }

    public boolean removeRole(Role role) {
        synchronized (roles) {
            if (!roles.contains(role)) {
                return false;
            }
            roles.remove(role);
            return true;
        }
    }

    public boolean updateRoles(List<Role> roles) {
        synchronized (this.roles) {
            this.roles.clear();
            this.roles.addAll(roles);
            return true;
        }
    }

    public List<UUID> flowExecutionHistory() {
        synchronized (flowExecutionHistory) {
            return new ArrayList<>(flowExecutionHistory);
        }
    }

    public void addFlowToHistory(UUID flowId) {
        synchronized (flowExecutionHistory) {
            flowExecutionHistory.add(0, flowId);
        }
    }

    public void update(UserData userData) {
        this.isManager = userData.isManager;
        this.roles.clear();
        this.roles.addAll(userData.roles);
        this.flowExecutionHistory.clear();
        this.flowExecutionHistory.addAll(userData.flowExecutionHistory);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        UserData userData = (UserData) o;

        if (isManager != userData.isManager) return false;
        if (!Objects.equals(name, userData.name)) return false;
        if (!roles.equals(userData.roles)) return false;
        return flowExecutionHistory.equals(userData.flowExecutionHistory);
    }

    @Override
    public int hashCode() {
        int result = name != null ? name.hashCode() : 0;
        result = 31 * result + (isManager ? 1 : 0);
        result = 31 * result + roles.hashCode();
        result = 31 * result + flowExecutionHistory.hashCode();
        return result;
    }
}
