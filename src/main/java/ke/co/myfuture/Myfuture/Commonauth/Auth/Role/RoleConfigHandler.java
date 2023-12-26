package ke.co.myfuture.Myfuture.Commonauth.Auth.Role;


import ke.co.myfuture.Myfuture.Commonauth.Auth.Data.Http.Request.Role.CreateRoleConfigRequest;
import ke.co.myfuture.Myfuture.Commonauth.Auth.Data.Http.Request.Role.UpdateRoleConfigRequest;
import ke.co.myfuture.Myfuture.Commonauth.Auth.Data.Http.Request.Role.UpdateRoleConfigStatusRequest;
import ke.co.myfuture.Myfuture.Commonauth.Auth.Data.Http.Response.AuthEntityResponse;
import ke.co.myfuture.Myfuture.Commonauth.Auth.Data.Http.Response.Role.RoleResponse;
import ke.co.myfuture.Myfuture.Commonauth.Auth.Data.Http.Response.Role.RolesResponse;
import ke.co.myfuture.Myfuture.Commonauth.Auth.Data.Role.RoleAccessRights;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin
@RestController
@RequestMapping(path = "/api/v1/roles")
public class RoleConfigHandler {
    @Autowired
    private RoleConfigService roleConfigService;

    @GetMapping("/access-rights")
    public List<RoleAccessRights> fetchAccessRights(){
        return this.roleConfigService.accessRights();
    }

    @PostMapping("/create-role")
    public ResponseEntity<AuthEntityResponse> createRole(@RequestBody CreateRoleConfigRequest body){
        try{
            return ResponseEntity.ok().body(this.roleConfigService.createRole(body.getName(), body.getAccessRights()));
        }catch (Exception ex){
            return ResponseEntity.status(HttpStatus.NOT_ACCEPTABLE).body(new AuthEntityResponse(406, ex.getMessage()));
        }
    }

    @PutMapping("/update-role")
    public ResponseEntity<AuthEntityResponse> updateRole(@RequestBody UpdateRoleConfigRequest body){
        return ResponseEntity.ok().body(this.roleConfigService.updateRole(body.getId(), body.getName(), body.getAccessRights()));
    }

    @PutMapping("/deactivate-role")
    public ResponseEntity<AuthEntityResponse> deactivateRole(@RequestBody UpdateRoleConfigStatusRequest body){
        return ResponseEntity.ok().body(this.roleConfigService.deactivateRole(body.getId()));
    }

    @PutMapping("/activate-role")
    public ResponseEntity<AuthEntityResponse> activateRole(@RequestBody UpdateRoleConfigStatusRequest body){
        return ResponseEntity.ok().body(this.roleConfigService.activateRole(body.getId()));
    }

    @GetMapping("/all-roles")
    public ResponseEntity<RolesResponse> fetchAllRoles(){
        return ResponseEntity.ok().body(this.roleConfigService.getRoles());
    }

    @GetMapping("/{id}")
    public ResponseEntity<RoleResponse> fetchRoleById(@PathVariable Long id){
        return ResponseEntity.ok().body(this.roleConfigService.getRolesById(id));
    }

    @GetMapping("/active-roles")
    public ResponseEntity<RolesResponse> fetchActiveRoles(){
        return ResponseEntity.ok().body(this.roleConfigService.getRolesByStatus(1));
    }

    @GetMapping("/inactive-roles")
    public ResponseEntity<RolesResponse> fetchInactiveRoles(){
        return ResponseEntity.ok().body(this.roleConfigService.getRolesByStatus(0));
    }

}
