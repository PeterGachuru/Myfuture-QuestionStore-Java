package ke.co.myfuture.Myfuture.Commonauth.Auth.Role;

//import co.ke.emtechhousee.emtr.Auditing.AuditTrail.AuditTrailProvider;
import ke.co.myfuture.Myfuture.Commonauth.Auth.Data.Http.Response.AuthEntityResponse;
import ke.co.myfuture.Myfuture.Commonauth.Auth.Data.Http.Response.Role.RoleResponse;
import ke.co.myfuture.Myfuture.Commonauth.Auth.Data.Http.Response.Role.RolesResponse;
import ke.co.myfuture.Myfuture.Commonauth.Auth.Data.Role.RoleAccessRights;
import ke.co.myfuture.Myfuture.Commonauth.Auth.Data.Role.RoleData;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.java.Log;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;
import java.util.logging.Level;
import java.util.stream.Collectors;

@Service
@Log
@RequiredArgsConstructor
public class RoleConfigService {
    private final RoleConfigRepository roleConfigRepository;
//    private final AuditTrailProvider audit;

    public List<RoleAccessRights> accessRights(){
        return Arrays.stream(AccessRight.values())
                .map(s -> RoleAccessRights.builder().name(s.getName()).category(s.getCategory()).subCategory(s.getSubCategory()).accessRights(s).build())
                .collect(Collectors.toList());
    }

    public AuthEntityResponse createRole(@NonNull String name, List<AccessRight> accessRights){
        AuthEntityResponse response;

        if (roleConfigRepository.findByName(name).isPresent()){
            throw new RuntimeException("Role with name: "+ name +" already exists");
        }

        RoleConfig roleConfig = new RoleConfig();
        roleConfig.setName(name);
        roleConfig.setAccessRights(accessRights);
        roleConfig.setStatus(1);
        roleConfig = roleConfigRepository.save(roleConfig);

        response = AuthEntityResponse.builder().statusCode(HttpStatus.CREATED.value()).message("Role created successfully !").build();
        log.log(Level.INFO, "Role created successfully");
//        audit.log("ROLE", "Creating role: ", roleConfig.getName());

        return  response;
    }

    public AuthEntityResponse updateRole(@NonNull Long id, @NonNull String name, @NonNull List<AccessRight> accessRights){
        AtomicReference<AuthEntityResponse> res = new AtomicReference<>();

        this.roleConfigRepository.findById(id).ifPresentOrElse(role -> {
            AtomicReference<RoleConfig> roleConfig = new AtomicReference<>(role);

            if(roleConfig.get().getStatus().compareTo(1) == 0){
                roleConfig.get().setName(name);
                roleConfig.get().setAccessRights(accessRights);
                roleConfig.set(this.roleConfigRepository.save(roleConfig.get()));
                log.log(Level.INFO, "Role updated successfully");
//                audit.log("USER ACCOUNTS","Updating role: ", roleConfig.get().getName());

                res.set(AuthEntityResponse.builder().statusCode(HttpStatus.OK.value()).message("Role updated successfully").build());
            }else{
                res.set(AuthEntityResponse.builder().statusCode(HttpStatus.BAD_REQUEST.value()).message(String.format("Role with the id %s is not active", id)).build());
                log.log(Level.WARNING, String.format("Role with the id %s is not active", id));
//                audit.log("USER ACCOUNT","Attempting to update an inactive role");
            }
        },() -> {
            res.set(AuthEntityResponse.builder().statusCode(HttpStatus.BAD_REQUEST.value()).message(String.format("Role with the id %s not found", id)).build());
        });

        return res.get();
    }

    public AuthEntityResponse activateRole(@NonNull Long id){
        AtomicReference<AuthEntityResponse> res = new AtomicReference<>();

        this.roleConfigRepository.findById(id).ifPresentOrElse(role -> {
            AtomicReference<RoleConfig> roleConfig = new AtomicReference<>(role);

            if(roleConfig.get().getStatus().compareTo(1) != 0){
                roleConfig.get().setStatus(1);
                roleConfig.set(this.roleConfigRepository.save(roleConfig.get()));
                log.log(Level.INFO, "Role activated successfully");
//                audit.log("ROLES","Activating an inactive role: ", roleConfig.get().getName());

                res.set(AuthEntityResponse.builder().statusCode(HttpStatus.OK.value()).message("Role activated successfully").build());
            }else{
                res.set(AuthEntityResponse.builder().statusCode(HttpStatus.BAD_REQUEST.value()).message("Selected role is active").build());
                log.log(Level.WARNING, "Selected role is active");
//                audit.log("ROLES", "Attempting to reactivate an active role");
            }
        },() -> {
            res.set(AuthEntityResponse.builder().statusCode(HttpStatus.BAD_REQUEST.value()).message(String.format("Role with the id %s not found", id)).build());
        });

        return res.get();
    }

    public AuthEntityResponse deactivateRole(@NonNull Long id){
        AtomicReference<AuthEntityResponse> res = new AtomicReference<>();

        this.roleConfigRepository.findById(id).ifPresentOrElse(role -> {
            AtomicReference<RoleConfig> roleConfig = new AtomicReference<>(role);

            if(roleConfig.get().getStatus().compareTo(1) == 0){
                roleConfig.get().setStatus(0);
                roleConfig.set(this.roleConfigRepository.save(roleConfig.get()));
                log.log(Level.INFO, "Role activated successfully");
//                audit.log("ROLES","Deactivating role: ", roleConfig.get().getName());

                res.set(AuthEntityResponse.builder().statusCode(HttpStatus.OK.value()).message("Role deactivated successfully").build());
            }else{
                res.set(AuthEntityResponse.builder().statusCode(HttpStatus.BAD_REQUEST.value()).message("Selected role is already inactive").build());
                log.log(Level.WARNING, "Selected role is already inactive");
//                audit.log("ROLES", "Attempting to deactivate an already inactive role");
            }
        },() -> {
            res.set(AuthEntityResponse.builder().statusCode(HttpStatus.BAD_REQUEST.value()).message(String.format("Role with the id %s not found", id)).build());
        });

        return res.get();
    }

    
    public RolesResponse getRoles(){
        AtomicReference<RolesResponse> rolesResponse = new AtomicReference<>();

        List<RoleConfig> roles = this.roleConfigRepository.findAll();

        List<RoleData> rolesData = new ArrayList<>();

        if(!roles.isEmpty()){
            roles.forEach(roleConfig -> {
                RoleData roleData = RoleData.builder()
                        .id(roleConfig.getId())
                        .name(roleConfig.getName())
                        .status(roleConfig.getStatus())
                        .creationDate(roleConfig.getCreationDate())
                        .updateDate(roleConfig.getUpdateDate())
                        .build();

                List<RoleAccessRights> accessRights = new ArrayList<>();

                if(roleConfig.getAccessRights() != null && !roleConfig.getAccessRights().isEmpty()){
                    roleConfig.getAccessRights().forEach(s -> {
                        accessRights.add(RoleAccessRights.builder().name(s.getName()).category(s.getCategory()).subCategory(s.getSubCategory()).accessRights(s).build());
                    });
                }

                roleData.setAccessRights(accessRights);

                rolesData.add(roleData);

            });

            rolesResponse.set(RolesResponse.builder().status(HttpStatus.OK.value()).message("Roles Found").roles(rolesData).build());
        }

        return rolesResponse.get();
    }

    public RoleResponse getRolesById(@NonNull Long id){
        AtomicReference<RoleResponse> roleResponse = new AtomicReference<>();

        this.roleConfigRepository.findById(id).ifPresentOrElse(roleConfig -> {
            RoleData roleData = RoleData.builder()
                    .id(roleConfig.getId())
                    .name(roleConfig.getName())
                    .status(roleConfig.getStatus())
                    .creationDate(roleConfig.getCreationDate())
                    .updateDate(roleConfig.getUpdateDate())
                    .build();

            List<RoleAccessRights> accessRights = new ArrayList<>();

            if(roleConfig.getAccessRights() != null && !roleConfig.getAccessRights().isEmpty()){
                roleConfig.getAccessRights().forEach(s -> {
                    accessRights.add(RoleAccessRights.builder().name(s.getName()).category(s.getCategory()).subCategory(s.getSubCategory()).accessRights(s).build());
                });
            }

            roleData.setAccessRights(accessRights);

            roleResponse.set(RoleResponse.builder().status(HttpStatus.OK.value()).message("Role Found").role(roleData).build());
        }, () ->{
            log.log(Level.INFO, String.format("Role with the id %s not found", id));
        });

        return roleResponse.get();
    }

    public RolesResponse getRolesByStatus(@NonNull Integer status){
        AtomicReference<RolesResponse> rolesResponse = new AtomicReference<>();

        List<RoleConfig> roles = this.roleConfigRepository.findByStatus(status);

        List<RoleData> rolesData = new ArrayList<>();

        if(!roles.isEmpty()){
            roles.forEach(roleConfig -> {
                RoleData roleData = RoleData.builder()
                        .id(roleConfig.getId())
                        .name(roleConfig.getName())
                        .status(roleConfig.getStatus())
                        .creationDate(roleConfig.getCreationDate())
                        .updateDate(roleConfig.getUpdateDate())
                        .build();

                List<RoleAccessRights> accessRights = new ArrayList<>();

                if(roleConfig.getAccessRights() != null && !roleConfig.getAccessRights().isEmpty()){
                    roleConfig.getAccessRights().forEach(s -> {
                        accessRights.add(RoleAccessRights.builder().name(s.getName()).category(s.getCategory()).subCategory(s.getSubCategory()).accessRights(s).build());
                    });
                }

                roleData.setAccessRights(accessRights);

                rolesData.add(roleData);

            });

            rolesResponse.set(RolesResponse.builder().status(HttpStatus.OK.value()).message("Roles Found").roles(rolesData).build());
        }

        return rolesResponse.get();
    }


}
