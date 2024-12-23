package adeo.leroymerlin.cdp;

import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

@ExtendWith(MockitoExtension.class)
public class EventServiceTest {

    @Mock
    private EventRepository eventRepository;

    @InjectMocks
    private EventService eventService;

    @Nested
    class UpdateEventTest {
        @Test
        void updateEvent_Success() {
            // Given
            final Long eventId = 1L;
            final Event existingEvent = new Event();
            existingEvent.setId(eventId);
            existingEvent.setComment("Old comment");
            existingEvent.setNbStars(3);

            final Event updatedEvent = new Event();
            updatedEvent.setComment("Updated comment");
            updatedEvent.setNbStars(5);

            Mockito.when(eventRepository.findById(eventId)).thenReturn(Optional.of(existingEvent));
            Mockito.when(eventRepository.save(Mockito.any(Event.class))).thenAnswer(invocation -> invocation.getArgument(0));

            // When
            final Event result = eventService.updateEvent(eventId, updatedEvent);

            // Then
            ArgumentCaptor<Event> captor = ArgumentCaptor.forClass(Event.class);
            Mockito.verify(eventRepository).save(captor.capture());

            Event savedEvent = captor.getValue();
            Assertions.assertEquals("Updated comment", savedEvent.getComment());
            Assertions.assertEquals(5, savedEvent.getNbStars());
            Assertions.assertEquals(eventId, savedEvent.getId());

            // Vérification du résultat final
            Assertions.assertEquals("Updated comment", result.getComment());
            Assertions.assertEquals(5, result.getNbStars());
        }

        @Test
        void updateEvent_EventNotFound() {
            // Given
            final Long eventId = 1L;
            final Event updatedEvent = new Event();
            updatedEvent.setComment("Updated comment");
            updatedEvent.setNbStars(5);

            Mockito.when(eventRepository.findById(eventId)).thenReturn(Optional.empty());

            // When
            final EntityNotFoundException exception = Assertions.assertThrows(EntityNotFoundException.class,
                    () -> eventService.updateEvent(eventId, updatedEvent));

            // Then
            Assertions.assertEquals("Event with id " + eventId + " not found", exception.getMessage());

            Mockito.verify(eventRepository, Mockito.never()).save(Mockito.any(Event.class));
        }
    }
}
