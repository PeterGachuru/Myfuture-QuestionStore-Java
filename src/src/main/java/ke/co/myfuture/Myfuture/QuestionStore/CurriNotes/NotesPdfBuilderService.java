package ke.co.myfuture.Myfuture.QuestionStore.CurriNotes;

import ke.co.myfuture.Myfuture.QuestionStore.CurriTopic.CurriTopic;
import ke.co.myfuture.Myfuture.QuestionStore.CurriTopic.CurriTopicRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class NotesPdfBuilderService {

    private final CurriTopicRepository curriTopicRepository;
    private final CurriNotesRepository notesRepository;

    public NotesPdfBuilderService(CurriTopicRepository curriTopicRepository,
                                  CurriNotesRepository notesRepository) {
        this.curriTopicRepository = curriTopicRepository;
        this.notesRepository = notesRepository;
    }

    public String buildSubjectNotesHtml(Long subjectId, Long levelId) {

        List<CurriTopic> parentTopics =
                curriTopicRepository.findBySubjectAndClass(subjectId, levelId);

        StringBuilder topicsHtml = new StringBuilder();

        for (CurriTopic parent : parentTopics) {

            topicsHtml.append("<div class='topic'>");
            topicsHtml.append("<h2>").append(parent.getName()).append("</h2>");

            List<CurriTopic> children =
                    curriTopicRepository.findByParent(parent.getId());

            for (CurriTopic child : children) {

                topicsHtml.append("<div class='subtopic'>");
                topicsHtml.append("<h3>").append(child.getName()).append("</h3>");

                Optional<CurriNotes> notesOpt =
                        notesRepository.findBySubtopicIdAndDeletedFlagFalse(child.getId());

                if(notesOpt.isPresent()){

                    topicsHtml.append("<div class='notes'>");
                    topicsHtml.append(notesOpt.get().getContent());
                    topicsHtml.append("</div>");

                }else{

                    topicsHtml.append("<p>No notes available.</p>");

                }

                topicsHtml.append("</div>");
            }

            topicsHtml.append("</div>");
        }

        String htmlTemplate = """
                <!DOCTYPE html>
                <html>
                <head>
                <meta charset="UTF-8">
                <style>

                body{
                    font-family: Arial, sans-serif;
                    line-height:1.6;
                    margin:40px;
                }

                h1{
                    text-align:center;
                    margin-bottom:40px;
                }

                h2{
                    margin-top:40px;
                    border-bottom:2px solid #444;
                    padding-bottom:5px;
                }

                h3{
                    margin-top:25px;
                }

                .topic{
                    page-break-before: always;
                }

                .subtopic{
                    margin-top:20px;
                }

                </style>
                </head>
                <body>

                <h1>%s %s Revision Notes</h1>

                %s

                </body>
                </html>
                """;

        return String.format(
                htmlTemplate,
                parentTopics.get(0).getCurriLevel().getName(),
                parentTopics.get(0).getSubject().getName(),
                topicsHtml.toString()
        );
    }
}
