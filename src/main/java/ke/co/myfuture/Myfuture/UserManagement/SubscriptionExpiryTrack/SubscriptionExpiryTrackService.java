package ke.co.myfuture.Myfuture.UserManagement.SubscriptionExpiryTrack;

import ke.co.myfuture.Myfuture.UserManagement.StudySubscription.StudySubscription;
import ke.co.myfuture.Myfuture.UserManagement.StudySubscription.StudySubscriptionRepository;
import org.hibernate.event.spi.SaveOrUpdateEvent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.persistence.EntityNotFoundException;
import java.util.Calendar;
import java.util.Date;
import java.util.Optional;

@Service
public class SubscriptionExpiryTrackService {
    @Autowired
    SubscriptionExpiryTrackRepository repository;

    @Autowired
    StudySubscriptionRepository studySubscriptionRepository;

    public void receiveSubscription(StudySubscription subscription) {

        System.out.println("Received new subscription");

        if (Boolean.TRUE.equals(subscription.getCalculated())) {
            System.out.println("Is already calculated");
            return; // already processed
        }

        // 1. Identify the user uniquely
        Optional<SubscriptionExpiryTrack> optionalTrack = Optional.empty();

        if (subscription.getInid() != null) {
            optionalTrack = repository.findByParent(subscription.getInid());
        } else if (subscription.getEmailAddress() != null) {
            optionalTrack = repository.findByParentUsername(subscription.getEmailAddress());
        } else if (subscription.getInstallId() != null) {
            optionalTrack = repository.findByInstallId(subscription.getInstallId());
        }

        // 2. Get or create SubscriptionExpiryTrack
        SubscriptionExpiryTrack track = optionalTrack.orElseGet(() -> {
            SubscriptionExpiryTrack t = new SubscriptionExpiryTrack();
            t.setParent(subscription.getInid());
            t.setParentUsername(subscription.getEmailAddress());
            t.setInstallId(subscription.getInstallId());
            t.setExpiryDate(new Date()); // default start
            return repository.save(t);
        });

        System.out.println("Now have a track");
        System.out.println(track);

        // 3. Calculate new expiry
        Date baseDate = track.getExpiryDate();
        Date now = new Date();

        if (baseDate == null || baseDate.before(now)) {
            System.out.println("Base date is null or before now ");
            baseDate = now;
        }

        System.out.println("BaseDate: "+baseDate);

        Calendar cal = Calendar.getInstance();
        cal.setTime(baseDate);
        cal.add(Calendar.DAY_OF_MONTH, subscription.getNumberOfDays());

        System.out.println("added "+subscription.getNumberOfDays()+" to basedate, now "+cal);

        Date newExpiry = cal.getTime();

        System.out.println("New expiry date: "+newExpiry);

        track.setExpiryDate(newExpiry);
        System.out.println("Now have a track");
        System.out.println(track);
        repository.save(track);

        // 4. Link subscription to track
        subscription.setExpiryTrack(track);
        subscription.setCalculated(true);
        subscription.setStartDate(baseDate);
        subscription.setEndDate(newExpiry);

        System.out.println("Updating subscription");
        System.out.println(subscription);

        studySubscriptionRepository.save(subscription);
    }


    public SubscriptionExpiryTrack getByInstallIdOrUsername(
            Long installId,
            String parentUsername) {

        if (installId == null && parentUsername == null) {
            throw new IllegalArgumentException(
                    "Either installId or parentUsername must be provided");
        }

        if (installId != null) {
            return repository.findByInstallId(installId)
                    .orElseThrow(() -> new EntityNotFoundException(
                            "No subscription found for installId=" + installId));
        }

        return repository.findByParentUsername(parentUsername)
                .orElseThrow(() -> new EntityNotFoundException(
                        "No subscription found for parentUsername=" + parentUsername));
    }
}