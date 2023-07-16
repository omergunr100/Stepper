package com.main.stepper.server.roles;

import com.main.stepper.flow.definition.api.IFlowDefinition;
import com.main.stepper.shared.structures.roles.Role;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class RoleManager {
    // This class is a singleton
    // default roles
    private static final String ALL_FLOWS = "All Flows";
    private static final String READ_ONLY_FLOWS = "Read Only Flows";
    private static final Role ALL_FLOWS_ROLE = new Role(ALL_FLOWS, "Can call any flow", new ArrayList<>(), true, false);
    private static final Role READ_ONLY_FLOWS_ROLE = new Role(READ_ONLY_FLOWS, "Can call only flows that are marked as read only", new ArrayList<>(), true, false);
    private static RoleManager instance = new RoleManager();

    private final List<Role> roleList;

    public static List<Role> getRoles() {
        return instance.pGetRoles();
    }

    public static Optional<Role> getRole(String name) {
        return instance.pGetRole(name);
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
            synchronized (role.allowedFlows()) {
                for (String flow : role.allowedFlows()) {
                    if (!unionGroup.contains(flow)) {
                        unionGroup.add(flow);
                    }
                }
            }
        }
        return unionGroup;
    }

    public static void updateDefaultRoles(List<IFlowDefinition> flows) {
        instance.pUpdateDefaultRoles(flows);
    }

    private RoleManager() {
        roleList = new ArrayList<>();
        roleList.add(ALL_FLOWS_ROLE);
        roleList.add(READ_ONLY_FLOWS_ROLE);
    }

    private List<Role> pGetRoles() {
        synchronized (roleList) {
            return new ArrayList<>(roleList);
        }
    }

    private Optional<Role> pGetRole(String name) {
        synchronized (roleList) {
            return roleList.stream().filter(r -> r.name().equals(name)).findFirst();
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

    private void pUpdateDefaultRoles(List<IFlowDefinition> flows) {
        synchronized (roleList) {
            synchronized (READ_ONLY_FLOWS_ROLE) {
                READ_ONLY_FLOWS_ROLE.setAllowedFlows(
                        flows.stream().filter(f -> f.isReadOnly()).map(IFlowDefinition::name).collect(Collectors.toList())
                );
            }
            synchronized (ALL_FLOWS_ROLE) {
                ALL_FLOWS_ROLE.setAllowedFlows(
                        flows.stream().map(IFlowDefinition::name).collect(Collectors.toList())
                );
            }
        }
    }
}
