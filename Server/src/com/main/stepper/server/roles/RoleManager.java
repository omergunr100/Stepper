package com.main.stepper.server.roles;

import com.main.stepper.shared.structures.roles.Role;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class RoleManager {
    // This class is a singleton
    private static RoleManager instance = new RoleManager();
    // default roles
    public static final String ALL_FLOWS = "All Flows";
    public static final String READ_ONLY_FLOWS = "Read Only Flows";

    private final List<Role> roleList;

    public static List<Role> getRoles() {
        return instance.pGetRoles();
    }

    public static boolean addRole(Role role) {
        return instance.pAddRole(role);
    }

    public static boolean removeRole(String name) {
        if (name.equals(ALL_FLOWS) || name.equals(READ_ONLY_FLOWS))
            return false;
        return instance.pRemoveRole(name);
    }

    public static List<String> uniqueUnionGroup(List<Role> roles) {
        List<String> unionGroup = new ArrayList<>();
        for (Role role : roles) {
            for (String flow : role.allowedFlows()) {
                if (!unionGroup.contains(flow)) {
                    unionGroup.add(flow);
                }
            }
        }
        return unionGroup;
    }

    private RoleManager() {
        roleList = new ArrayList<>();
        roleList.add(new Role(ALL_FLOWS, "Can call any flow", new ArrayList<>()));
        roleList.add(new Role(READ_ONLY_FLOWS, "Can call only flows that are marked as read only", new ArrayList<>()));
    }

    private List<Role> pGetRoles() {
        synchronized (roleList) {
            return new ArrayList<>(roleList);
        }
    }

    private boolean pAddRole(Role role) {
        synchronized (roleList) {
            if (roleList.contains(role))
                return false;
            return roleList.add(role);
        }
    }

    private boolean pRemoveRole(String name) {
        synchronized (roleList) {
            for (int i = 0; i < roleList.size(); i++) {
                if (roleList.get(i).name().equals(name)) {
                    roleList.remove(i);
                    return true;
                }
            }
            return false;
        }
    }
}
