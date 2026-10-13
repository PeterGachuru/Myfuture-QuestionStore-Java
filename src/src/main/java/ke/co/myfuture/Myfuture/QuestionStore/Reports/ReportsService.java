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
//        String reportQuery = "select *, char(row_num+64) AS num from (select ROW_NUMBER() OVER(PARTITION BY" +
//                " cq.string) AS row_num, cq.string, ct.name, cnc.value, cq.qn_num  " +
//                "from (select *, ROW_NUMBER() OVER(PARTITION BY '') AS qn_num from  " +
//                "(select * from curri_question  where subtopic in " +
//                "(select id from curri_topic where subject = "+subject+" and curri_level in " +
//                "(select cl.id from  curri_level cl join curri_level scl " +
//                "where cl.numbering > scl.numbering-3 and cl.numbering <= scl.numbering " +
//                "and scl.curriculum = cl.curriculum and scl.id = "+classlevel+" )) order by rand() limit 50) as k) cq " +
//                "join curri_topic ct on cq.subtopic = ct.id  join curri_normal_choice cnc on  cnc.question = cq.id  " +
//                "order by cq.id) As k  order by qn_num;";

        String reportQuery =
                "SELECT *, CHAR(ROW_NUMBER() OVER (PARTITION BY qn_num ORDER BY RAND()) + 64) AS num " +
                        "FROM ( " +
                        "  SELECT cq.qn_num, cq.string, ct.name, cnc.value, cq.id " +
                        "  FROM ( " +
                        "    SELECT *, ROW_NUMBER() OVER (PARTITION BY '') AS qn_num " +
                        "    FROM ( " +
                        "      SELECT * FROM curri_question " +
                        "      WHERE subtopic IN ( " +
                        "        SELECT id FROM curri_topic " +
                        "        WHERE subject = " + subject + " " +
                        "        AND curri_level IN ( " +
                        "          SELECT cl.id FROM curri_level cl " +
                        "          JOIN curri_level scl ON cl.curriculum = scl.curriculum " +
                        "          WHERE cl.numbering > scl.numbering - 3 " +
                        "          AND cl.numbering <= scl.numbering " +
                        "          AND scl.id = " + classlevel + " " +
                        "        ) " +
                        "      ) " +
                        "      ORDER BY RAND() " +
                        "      LIMIT 50 " +
                        "    ) AS base_questions " +
                        "  ) cq " +
                        "  JOIN curri_normal_choice cnc ON cnc.question = cq.id " +
                        "  JOIN curri_topic ct ON cq.subtopic = ct.id " +
                        ") AS randomized_choices " +
                        "ORDER BY qn_num, num;";


        parameters.put("query", reportQuery);
        System.out.println("Report query: "+reportQuery);
        return parameters;
    }
}
