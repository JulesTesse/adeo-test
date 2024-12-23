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

import java.util.*;

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

    @Nested
    class FilteredEventsTest {
        @Test
        void getFilteredEvents_Success() {
            // Given
            final Member member = new Member();
            member.setName("Alice");
            final Band band = new Band();
            band.setName("Band X");
            band.setMembers(Set.of(member));
            final Event event = new Event();
            event.setTitle("Event Title");
            event.setBands(Set.of(band));

            Mockito.when(eventRepository.findAll()).thenReturn(List.of(event));

            // When
            List<Event> result = eventService.getFilteredEvents("Al");

            // Then
            Assertions.assertEquals(1, result.size());
            Assertions.assertEquals("Event Title ["+result.size()+"]", result.getFirst().getTitle());
        }

        @Test
        void getFilteredEvents_NoMatch() {
            // Given
            final Member member = new Member();
            member.setName("Bob");
            final Band band = new Band();
            band.setName("Band X");
            band.setMembers(Set.of(member));
            final Event event = new Event();
            event.setTitle("Event Title");
            event.setBands(Set.of(band));

            Mockito.when(eventRepository.findAll()).thenReturn(List.of(event));

            // When
            List<Event> result = eventService.getFilteredEvents("Al");

            // Then
            Assertions.assertTrue(result.isEmpty());
        }

        @Test
        void getFilteredEvents_MultipleEvents_OneMatch() {
            // Given
            final Member member1 = new Member();
            member1.setName("Alice");
            final Member member2 = new Member();
            member2.setName("Bob");

            final Band band1 = new Band();
            band1.setName("Band X");
            band1.setMembers(Set.of(member1));
            final Band band2 = new Band();
            band2.setName("Band Y");
            band2.setMembers(Set.of(member2));

            final Event event1 = new Event();
            event1.setTitle("Event Title");
            event1.setBands(Set.of(band1));
            final Event event2 = new Event();
            event2.setTitle("Event Title");
            event2.setBands(Set.of(band2));

            Mockito.when(eventRepository.findAll()).thenReturn(List.of(event1, event2));

            // When
            List<Event> result = eventService.getFilteredEvents("Al");

            // Then
            Assertions.assertEquals(1, result.size());
            Assertions.assertEquals("Event Title ["+result.size()+"]", result.getFirst().getTitle());
        }

        @Test
        void getFilteredEvents_EmptyList() {
            // Given
            Mockito.when(eventRepository.findAll()).thenReturn(Collections.emptyList());

            // When
            List<Event> result = eventService.getFilteredEvents("Alice");

            // Then
            Assertions.assertTrue(result.isEmpty());
        }
    }
}
