package ke.co.myfuture.Myfuture.Commonauth.AuthenticationModule.Roles;

import ke.co.myfuture.Myfuture.Commonauth.AuthenticationModule.utils.DataNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
public class RoleService {
    private final RoleRepository roleRepo;

    public RoleService(RoleRepository roleRepo) {
        this.roleRepo = roleRepo;
    }

    public Role addRole(Role role) {
        try {
            return roleRepo.save(role);
        } catch (Exception e) {
            log.info("Catched Error {} " + e);
            return null;
        }
    }

    public List<Role> findAllRoles() {
        try {
            return roleRepo.findAll();
        } catch (Exception e) {
            log.info("Catched Error {} " + e);
            return null;
        }
    }
    public Role findById(Long id){
        try{
            return roleRepo.findById(id).orElseThrow(()-> new DataNotFoundException("Data " + id +"was not found"));
        } catch (Exception e) {
            log.info("Catched Error {} "+e);
            return null;
        }
    }

    public Role updateRole(Role role) {
        try {
            return roleRepo.save(role);
        } catch (Exception e) {
            log.info("Catched Error {} " + e);
            return null;
        }
    }

    public void deleteRole(Long id) {
        try {
            roleRepo.deleteById(id);
        } catch (Exception e) {
            log.info("Catched Error {} " + e);
        }
    }
}