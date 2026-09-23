package ke.co.myfuture.Myfuture.QuestionStore.Book;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface BookRepository extends JpaRepository<Book, Long> {
    public Optional<Book> findByModel(String model);
}
