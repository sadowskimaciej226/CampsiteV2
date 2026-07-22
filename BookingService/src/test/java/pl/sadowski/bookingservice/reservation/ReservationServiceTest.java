package pl.sadowski.bookingservice.reservation;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.kafka.core.KafkaTemplate;
import pl.sadowski.bookingservice.reservation.exceptions.AccommodationNotFoundException;
import pl.sadowski.bookingservice.reservation.exceptions.ReservationNotFoundException;
import pl.sadowski.bookingservice.reservation.view.AccommodationDepartedDto;
import pl.sadowski.sdk.avro.AccommodationEvent;

import java.time.LocalDate;
import java.util.Optional;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;
import static pl.sadowski.bookingservice.reservation.TestObjectCreator.*;

@ExtendWith(MockitoExtension.class)
class ReservationServiceTest {

    @Mock
    private ReservationRepository reservationRepository;
    @Mock
    private AccommodationRepository accommodationRepository;
    @Mock
    KafkaTemplate<String, Object> kafkaTemplate;
    @InjectMocks
    private ReservationService reservationService;

    @Test
    void shouldCreateReservationWhen() {
        //given
        //when
        when(reservationRepository.save(ArgumentMatchers.any())).thenReturn(new Reservation("userId", Sector.A, null));
        Reservation reservation = reservationService.createReservation("userId", Sector.A, null);
        //then
        assertThat(reservation.getUserId(), notNullValue());
        assertThat(reservation.getSector(), equalTo(Sector.A));
        assertThat(reservation.getElectricBoxNum(), is(nullValue()));
    }

    @Test
    void addAccommodationShouldThrowExceptionWhenReservationNotFound() {
        //given
        var accommodationCreationDto = createAccommodationCreationDto("reservationId");
        //when
        when(reservationRepository.findById(ArgumentMatchers.any())).thenReturn(Optional.empty());
        //then
        assertThrows(ReservationNotFoundException.class,
                () -> reservationService.addAccommodation(accommodationCreationDto));

    }

    @Test
    void addAccommodationShouldSendEventAndSaveAccommodationWhenReservationIsFound() {
        //given
        var accommodationCreationDto = createAccommodationCreationDto("reservationId");
        Reservation reservation = new Reservation("userId", Sector.A, 1);
        //when
        when(reservationRepository.findById(ArgumentMatchers.any())).thenReturn(Optional.of(reservation));
        when(accommodationRepository.save(ArgumentMatchers.any())).thenReturn(createAccommodation(reservation));
        reservationService.addAccommodation(accommodationCreationDto);
        //then
        verify(accommodationRepository,
                times(1)).save(ArgumentMatchers.any());
        verify(kafkaTemplate, times(1))
                .send(ArgumentMatchers.any(), ArgumentMatchers.any(), ArgumentMatchers.any());
    }

    @Test
    void finishAccommodationAndCreateNextOneShouldThrowReservationNotFoundExceptionWhenReservationIsNotFound() {
        //given
        var accommodationDepartedDto
                = new AccommodationDepartedDto("reservationId", "accommodationId", LocalDate.now(),
                1, "clientId", null);
        //when
        when(reservationRepository.findById(ArgumentMatchers.any())).thenReturn(Optional.empty());
        //then
        assertThrows(ReservationNotFoundException.class,
                () -> reservationService.finishAccommodationAndCreateNextOne(accommodationDepartedDto));
    }

    @Test
    void finishAccommodationShouldThrowAccommodationNotFoundExceptionWhenAccommodationAndCreateNextOneNotExists() {
        //given
        var accommodationDepartedDto
                = new AccommodationDepartedDto("reservationId", "accommodationId", LocalDate.now(),
                1, "clientId", null);
        Reservation reservation = new Reservation("userId", Sector.A, 1);
        //when
        when(reservationRepository.findById(ArgumentMatchers.any())).thenReturn(Optional.of(reservation));
        when(accommodationRepository.findById(ArgumentMatchers.any()))
                .thenReturn(Optional.empty());

        //then
        assertThrows(AccommodationNotFoundException.class,
                () -> reservationService.finishAccommodationAndCreateNextOne(accommodationDepartedDto));
    }

    @Test
    void finishAccommodationShouldCloseOneAccommodationAndCreateNextOneAndCreateAnotherWhenAllDataAreValid() {
        //given
        var reservation = new Reservation("reservationId", "userId", Sector.A, 1, true);
        Accommodation accommodation = createAccommodation(reservation);
        var accommodationDepartedDto
                = createAccommodationDepartedDto(reservation, accommodation, 1);
        //when
        when(accommodationRepository.findById("accommodationId"))
                .thenReturn(Optional.of(accommodation));
        when(reservationRepository.findById(ArgumentMatchers.any())).thenReturn(Optional.of(reservation));

        reservationService.finishAccommodationAndCreateNextOne(accommodationDepartedDto);
        //then
        var accommodationEventCaptor = ArgumentCaptor.forClass(AccommodationEvent.class);
        verify(kafkaTemplate, times(2))
                .send(ArgumentMatchers.any(), ArgumentMatchers.any(), accommodationEventCaptor.capture());

        var events = accommodationEventCaptor.getAllValues();

        AccommodationEvent departedEvent = events.getLast();
        assertThat(departedEvent.getAmount(), equalTo(2));
        assertThat(departedEvent.getDepartedAt(), is(notNullValue()));
        assertThat(departedEvent.getReservationId(), equalTo("reservationId"));

        AccommodationEvent newAccommodation = events.getFirst();
        assertThat(newAccommodation.getAmount(), equalTo(1));
        assertThat(newAccommodation.getDepartedAt(), is(nullValue()));
        assertThat(newAccommodation.getArrivedAt(), equalTo(departedEvent.getDepartedAt()));
    }

}