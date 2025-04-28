package backend.service;

import backend.model.Events;
import backend.model.UserSubscription;
import backend.repository.EventRepository;
import backend.repository.UserSubscriptionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class DashboardService {
    private final EventRepository eventRepository;
    private final UserSubscriptionRepository userSubscriptionRepository;

    @Autowired
    public DashboardService(EventRepository eventRepository, UserSubscriptionRepository userSubscriptionRepository) {
        this.eventRepository = eventRepository;
        this.userSubscriptionRepository = userSubscriptionRepository;
    }

    public List<Events> getAllEvents() {
        return eventRepository.findAll()
                .stream()
                .filter(Events::getIsPublic)
                .collect(Collectors.toList());
    }

    public List<String> getCategories() {
        return eventRepository.findDistinctCategories();
    }

    public List<String> getTags() {
        return eventRepository.findDistinctTags();
    }
}