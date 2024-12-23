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
        return events.stream()
                .filter((Event e) -> e.getBands()
                        .stream()
                        .anyMatch((Band b) -> b.getMembers()
                                .stream()
                                .anyMatch(m -> m.getName().toLowerCase().contains(query.toLowerCase()))))
                .toList();
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
