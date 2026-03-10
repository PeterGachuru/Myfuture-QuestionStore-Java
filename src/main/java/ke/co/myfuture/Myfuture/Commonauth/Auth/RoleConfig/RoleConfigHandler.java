package ke.co.myfuture.Myfuture.Commonauth.Auth.RoleConfig;


import ke.co.myfuture.Myfuture.Commonauth.Auth.Data.CreateRoleConfigRequest;
import ke.co.myfuture.Myfuture.Commonauth.Auth.Data.UpdateRoleConfigRequest;
import ke.co.myfuture.Myfuture.Commonauth.Auth.Data.UpdateRoleConfigStatusRequest;
import ke.co.myfuture.Myfuture.Commonauth.Auth.Data.AuthEntityResponse;
import ke.co.myfuture.Myfuture.Commonauth.Auth.Data.RoleResponse;
import ke.co.myfuture.Myfuture.Commonauth.Auth.Data.RolesResponse;
import ke.co.myfuture.Myfuture.Commonauth.Auth.Data.RoleAccessRights;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin
@RestController
@RequestMapping(path = "/powers")
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
