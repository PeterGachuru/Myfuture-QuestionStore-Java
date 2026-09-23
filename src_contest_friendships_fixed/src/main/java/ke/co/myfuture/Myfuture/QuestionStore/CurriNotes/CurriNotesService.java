package ke.co.myfuture.Myfuture.QuestionStore.CurriNotes;

import ke.co.myfuture.Myfuture.QuestionStore.Book.BookInitialModels;
import ke.co.myfuture.Myfuture.QuestionStore.CurriTopic.CurriTopic;
import ke.co.myfuture.Myfuture.QuestionStore.CurriTopic.CurriTopicRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class CurriNotesService {
    @Autowired
    CurriNotesRepository curriNotesRepository;

    @Autowired
    CurriTopicRepository curriTopicRepository;

    @Bean
    private void copyNotes() {
        List<CurriTopic> curriTopicList = curriTopicRepository.findChildrenWithContent();
//        List<CurriNotes> curriNotesList = new ArrayList<>();

        for (CurriTopic curriTopic: curriTopicList) {
            CurriNotes curriNotes = new CurriNotes();
            curriNotes.setBookModel(BookInitialModels.written1Version);
            curriNotes.setSubtopic(curriTopic);
            curriNotes.setContent(curriTopic.getContent());

            curriNotesRepository.save(curriNotes);
            curriTopic.setContent(null);
            curriTopicRepository.save(curriTopic);
        }
    }
}
