package ke.co.myfuture.Myfuture.QuestionStore.Cgroup;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CgroupService {
    @Autowired
    CgroupRepository repository;

    public Cgroup newCgroup(Cgroup cgroup) {
        return repository.save(cgroup);
    }
}
