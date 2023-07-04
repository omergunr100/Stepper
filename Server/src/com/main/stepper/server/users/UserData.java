package com.main.stepper.server.users;

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
    private final List<UUID> stepExecutionHistory;

    public UserData(String name) {
        this.name = name;
        this.isManager = false;
        roles = new ArrayList<>();
        flowExecutionHistory = new ArrayList<>();
        stepExecutionHistory = new ArrayList<>();
    }

    public String name() {
        return name;
    }

    public boolean isManager() {
        return isManager;
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

    public List<UUID> flowExecutionHistory() {
        synchronized (flowExecutionHistory) {
            return new ArrayList<>(flowExecutionHistory);
        }
    }

    public void addFlowToHistory(UUID flowId) {
        synchronized (flowExecutionHistory) {
            flowExecutionHistory.add(flowId);
        }
    }

    public List<UUID> stepExecutionHistory() {
        synchronized (stepExecutionHistory) {
            return new ArrayList<>(stepExecutionHistory);
        }
    }

    public void addStepToHistory(UUID stepId) {
        synchronized (stepExecutionHistory) {
            stepExecutionHistory.add(stepId);
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        UserData userData = (UserData) o;

        return Objects.equals(name, userData.name);
    }

    @Override
    public int hashCode() {
        return name != null ? name.hashCode() : 0;
    }
}
