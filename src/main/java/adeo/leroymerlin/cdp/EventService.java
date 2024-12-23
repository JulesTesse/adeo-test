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

        return events;
    }

    public Event updateEvent(Long id, Event event) {
        Optional<Event> optionalEvent = eventRepository.findById(id);
        if(optionalEvent.isEmpty()) {
            throw new EntityNotFoundException("Event with id " + id + " not found");
        }

        Event existing = optionalEvent.get();
        existing.setComment(event.getComment());
        existing.setNbStars(event.getNbStars());

        return eventRepository.save(existing);
    }
}
