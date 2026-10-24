package ke.co.myfuture.Myfuture.QuestionStore.CurriTopic;

import ke.co.myfuture.Myfuture.QuestionStore.Subject.Subject;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.ArrayList;
import java.util.stream.Collectors;

/** Batches topic navigation queries used by public read pages. */
@Service
@RequiredArgsConstructor
public class ReadTopicNavigationService {

    private final CurriTopicRepository topicRepository;

    public void populateChildren(List<CurriTopic> parents) {
        if (parents.isEmpty()) return;

        List<Long> parentIds = parents.stream().map(CurriTopic::getId).toList();
        Map<Long, List<CurriTopic>> childrenByParent = topicRepository.findChildrenByParents(parentIds)
                .stream()
                .collect(Collectors.groupingBy(topic -> topic.getParent().getId()));
        parents.forEach(parent -> parent.setChildren(
                childrenByParent.getOrDefault(parent.getId(), new ArrayList<>())));
    }

    public void populateFirstSubtopicSlugs(List<Subject> subjects, Long classLevelId) {
        if (subjects.isEmpty()) return;

        List<Long> subjectIds = subjects.stream().map(Subject::getId).toList();
        Map<Long, List<CurriTopic>> parentsBySubject = topicRepository
                .findBySubjectsAndClass(subjectIds, classLevelId)
                .stream()
                .collect(Collectors.groupingBy(topic -> topic.getSubject().getId()));

        List<CurriTopic> firstParents = subjects.stream()
                .map(subject -> parentsBySubject.get(subject.getId()))
                .filter(topics -> topics != null && !topics.isEmpty())
                .map(topics -> topics.get(0))
                .toList();
        Map<Long, List<CurriTopic>> childrenByParent = firstParents.isEmpty()
                ? Map.of()
                : topicRepository.findChildrenByParents(firstParents.stream().map(CurriTopic::getId).toList())
                    .stream()
                    .collect(Collectors.groupingBy(topic -> topic.getParent().getId()));

        subjects.forEach(subject -> {
            List<CurriTopic> parents = parentsBySubject.get(subject.getId());
            if (parents == null || parents.isEmpty()) return;

            CurriTopic firstParent = parents.get(0);
            List<CurriTopic> children = childrenByParent.getOrDefault(firstParent.getId(), List.of());
            subject.setFirstSubtopicSlug((children.isEmpty() ? firstParent : children.get(0)).getSlug());
        });
    }
}
