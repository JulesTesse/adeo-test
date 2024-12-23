package adeo.leroymerlin.cdp;

import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class EventService {

    private final EventRepository eventRepository;

    public EventService(EventRepository eventRepository) {
        this.eventRepository = eventRepository;
    }

    public List<Event> getEvents() {
        return eventRepository.findAll();
    }

    public void delete(Long id) {
        eventRepository.deleteById(id);
    }

    public List<Event> getFilteredEvents(String query) {
        List<Event> events = eventRepository.findAll();

        // Filter the events list in pure JAVA here
        String normalizedQuery = query.toLowerCase();

        List<Event> filtered = events.stream()
                .filter(e -> e.getBands() != null && e.getBands()
                        .stream()
                        .filter(b -> b.getMembers() != null)
                        .anyMatch(b -> b.getMembers()
                                .stream()
                                .filter(m -> m.getName() != null)
                                .anyMatch(m -> m.getName().toLowerCase().contains(normalizedQuery))))
                .toList();

        addCount(filtered);
        return filtered;
    }

    private static void addCount(List<Event> filtered) {
        filtered
                .forEach(e -> {
                    e.setTitle(e.getTitle() + " [" + e.getBands().size() + "]");
                    e.getBands()
                            .forEach(b -> b.setName(b.getName() + " [" + b.getMembers().size() + "]"));
                });
    }

    public Event updateEvent(Long id, Event event) {
        final Optional<Event> optionalEvent = eventRepository.findById(id);
        if (optionalEvent.isEmpty()) {
            throw new EntityNotFoundException("Event with id " + id + " not found");
        }

        final Event existing = optionalEvent.get();
        existing.setComment(event.getComment());
        existing.setNbStars(event.getNbStars());

        return eventRepository.save(existing);
    }
}
