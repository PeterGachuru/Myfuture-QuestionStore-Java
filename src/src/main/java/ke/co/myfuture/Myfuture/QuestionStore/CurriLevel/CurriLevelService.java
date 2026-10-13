package ke.co.myfuture.Myfuture.QuestionStore.CurriLevel;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.cache.annotation.Cacheable;

@Service
@RequiredArgsConstructor
public class CurriLevelService {

    private final CurriLevelRepository repository;

    // Cache by ID
    @Cacheable(value = "curriLevelCache", key = "#id", sync = true)
    public CurriLevel getById(Long id) {
        System.out.println("Fetching from DB...");
        return repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Not found"));
    }

    // Cache by slug
    @Cacheable(value = "curriLevelCacheBySlug", key = "#slug")
    public CurriLevel getBySlug(String slug) {
        System.out.println("Fetching from DB...");
        return repository.findBySlug(slug)
                .orElseThrow(() -> new RuntimeException("Not found"));
    }
}