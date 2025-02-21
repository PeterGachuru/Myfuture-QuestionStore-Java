package ke.co.myfuture.Myfuture.QuestionStore.CurriTopic;

import ke.co.myfuture.Myfuture.QuestionStore.CurriQuestion.CurriQuestionRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@Slf4j
public class CurriTopicService {
    @Autowired
    CurriTopicRepository curriTopicRepository;


    @Autowired
    CurriQuestionRepository curriQuestionRepository;

    @Transactional
    public void updateCurriTopicStats() {
        log.info("Starting CurriTopic statistics update...");

        List<CurriTopic> allTopics = curriTopicRepository.findAll();

        Map<Long, Integer> topicApprovedCount = new HashMap<>();
        Map<Long, Integer> topicUnverifiedCount = new HashMap<>();
        Map<Long, Integer> topicRejectedCount = new HashMap<>();

        for (CurriTopic topic : allTopics) {
            if (topic.getParent() == null) {
                // Topic - Initialize aggregation counters
                topicApprovedCount.put(topic.getId(), 0);
                topicUnverifiedCount.put(topic.getId(), 0);
                topicRejectedCount.put(topic.getId(), 0);
            } else {
                // Subtopic - Calculate question stats
                int approvedCount = curriQuestionRepository.countBySubtopicAndReviewedAndDeleted(topic, true, false);
                int unverifiedCount = curriQuestionRepository.countBySubtopicAndReviewedAndDeleted(topic, false, false);
                int rejectedCount = curriQuestionRepository.countBySubtopicAndDeleted(topic, true);

                // Calculate percentage of rejected questions
                int totalQuestions = approvedCount + unverifiedCount + rejectedCount;
                int rejectedPercentage = (totalQuestions == 0) ? 0 : (rejectedCount * 100) / totalQuestions;

                // Update subtopic stats
                topic.setTotalNumberOfApprovedQuestions(approvedCount);
                topic.setTotalNumberOfUnverifiedQuestions(unverifiedCount);
                topic.setPercentageOfRejectedQuestions(rejectedPercentage);
                curriTopicRepository.save(topic);

                // Aggregate counts for parent topic
                CurriTopic parentTopic = topic.getParent();
                topicApprovedCount.put(parentTopic.getId(), topicApprovedCount.getOrDefault(parentTopic.getId(), 0) + approvedCount);
                topicUnverifiedCount.put(parentTopic.getId(), topicUnverifiedCount.getOrDefault(parentTopic.getId(), 0) + unverifiedCount);
                topicRejectedCount.put(parentTopic.getId(), topicRejectedCount.getOrDefault(parentTopic.getId(), 0) + rejectedCount);
            }
        }

        // Update parent topics with aggregated subtopic counts
        for (CurriTopic topic : allTopics) {
            if (topic.getParent() == null) { // Only update main topics
                int approvedCount = topicApprovedCount.getOrDefault(topic.getId(), 0);
                int unverifiedCount = topicUnverifiedCount.getOrDefault(topic.getId(), 0);
                int rejectedCount = topicRejectedCount.getOrDefault(topic.getId(), 0);
                int totalQuestions = approvedCount + unverifiedCount + rejectedCount;

                int rejectedPercentage = (totalQuestions == 0) ? 0 : (rejectedCount * 100) / totalQuestions;

                topic.setTotalNumberOfApprovedQuestions(approvedCount);
                topic.setTotalNumberOfUnverifiedQuestions(unverifiedCount);
                topic.setPercentageOfRejectedQuestions(rejectedPercentage);
                curriTopicRepository.save(topic);
            }
        }

        log.info("CurriTopic statistics update completed.");
    }
//    @Scheduled(cron = "0 0 2 * * ?") // Runs every day at 2 AM
    @Bean
    public void scheduleTopicUpdate() {
        log.info("Scheduled update for CurriTopic statistics started...");
        updateCurriTopicStats();
        log.info("Scheduled update for CurriTopic statistics completed.");
    }
}
