package ke.co.myfuture.Myfuture.QuestionStore.CurriQuestion;

import ke.co.myfuture.Myfuture.QuestionStore.Book.BookInitialModels;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Service;

@Service
public class CurriQuestionService {
    @Autowired
    CurriQuestionRepository curriQuestionRepository;

    @Bean
    private void updateBookModel() {
        curriQuestionRepository.setDefaultBookModel(BookInitialModels.written1Version);
    }
}
