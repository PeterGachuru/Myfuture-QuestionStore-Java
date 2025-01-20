package ke.co.myfuture.Myfuture.QuestionStore.Reports;


import ke.co.myfuture.Myfuture.QuestionStore.CurriLevel.CurriLevelRepository;
import ke.co.myfuture.Myfuture.QuestionStore.Subject.SubjectRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class ReportsService {
    @Value("${report.logo}")
    String logo;
    @Value("${report.appname}")
    String appname;

    @Autowired
    SubjectRepository subjectRepository;
    @Autowired
    CurriLevelRepository curriLevelRepository;


    public Map<String, Object> setParameters(Long subject, Long classlevel) {
        Map<String, Object> parameters = new HashMap<>();
        parameters.put("logo", logo);
        parameters.put("app_name", appname);
        parameters.put("subject_name", subjectRepository.getName(subject));
        parameters.put("class_level_name", curriLevelRepository.getName(classlevel));
        parameters.put("query", "select *, char(row_num+64) AS num from (select ROW_NUMBER() OVER(PARTITION BY" +
                " cq.string) AS row_num, cq.string, ct.name, cnc.value, cq.qn_num  " +
                "from (select *, ROW_NUMBER() OVER(PARTITION BY '') AS qn_num from  " +
                "(select * from curri_question  where subtopic in " +
                "(select id from curri_topic where subject = "+subject+" and curri_level in " +
                "(select cl.id from  curri_level cl join curri_level scl " +
                "where cl.numbering > scl.numbering-3 and cl.numbering <= scl.numbering " +
                "and scl.curriculum = cl.curriculum and scl.id = "+classlevel+" )) order by rand() limit 50) as k) cq " +
                "join curri_topic ct on cq.subtopic = ct.id  join curri_normal_choice cnc on  cnc.question = cq.id  " +
                "order by cq.id) As k  order by qn_num;");
        return parameters;
    }
}
