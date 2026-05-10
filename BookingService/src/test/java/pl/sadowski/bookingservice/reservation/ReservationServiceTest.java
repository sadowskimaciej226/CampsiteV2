package pl.sadowski.bookingservice.reservation;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.kafka.core.KafkaTemplate;
import pl.sadowski.bookingservice.reservation.exceptions.AccommodationNotFoundException;
import pl.sadowski.bookingservice.reservation.exceptions.ReservationNotFoundException;
import pl.sadowski.bookingservice.reservation.view.AccommodationCreationDto;
import pl.sadowski.bookingservice.reservation.view.AccommodationDepartedDto;
import pl.sadowski.bookingservice.reservation.view.AccommodationType;

import java.time.Instant;
import java.util.Optional;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

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
        AccommodationCreationDto accommodationCreationDto = new AccommodationCreationDto("id",
                AccommodationType.BIKE, "description", Instant.now(), 1, "clientId");
        //when
        when(reservationRepository.findById(ArgumentMatchers.any())).thenReturn(Optional.empty());
        //then
        assertThrows(ReservationNotFoundException.class,
                () -> reservationService.addAccommodation(accommodationCreationDto));

    }

    @Test
    void addAccommodationShouldSendEventAndSaveAccommodationWhenReservationIsFound() {
        //given
        AccommodationCreationDto accommodationCreationDto = new AccommodationCreationDto("id",
                AccommodationType.BIKE, "description", Instant.now(), 1, "clientId");
        Reservation reservation = new Reservation("userId", Sector.A, 1);
        //when
        when(reservationRepository.findById(ArgumentMatchers.any())).thenReturn(Optional.of(reservation));
        reservationService.addAccommodation(accommodationCreationDto);
        //then
        verify(reservationRepository,
                times(1)).save(ArgumentMatchers.any());
        verify(kafkaTemplate, times(1))
                .send(ArgumentMatchers.any(), ArgumentMatchers.any(), ArgumentMatchers.any());
    }

    @Test
    void finishAccommodationShouldThrowReservationNotFoundExceptionWhenReservationIsNotFound() {
        //given
        var accommodationDepartedDto
                = new AccommodationDepartedDto("reservationId", "accommodationId", Instant.now(),
                1, "clientId", null);
        //when
        when(reservationRepository.findById(ArgumentMatchers.any())).thenReturn(Optional.empty());
        //then
        assertThrows(ReservationNotFoundException.class,
                () -> reservationService.finishAccommodation(accommodationDepartedDto));
    }

    @Test
    void finishAccommodationShouldThrowAccommodationNotFoundExceptionWhenAccommodationNotExists() {
        //given
        var accommodationDepartedDto
                = new AccommodationDepartedDto("reservationId", "accommodationId", Instant.now(),
                1, "clientId", null);
        Reservation reservation = new Reservation("userId", Sector.A, 1);
        //when
        when(reservationRepository.findById(ArgumentMatchers.any())).thenReturn(Optional.of(reservation));
        when(accommodationRepository.findAccommodationByReservationIdAndId(ArgumentMatchers.any(), ArgumentMatchers.any()))
                .thenReturn(Optional.empty());

        //then
        assertThrows(AccommodationNotFoundException.class,
                () -> reservationService.finishAccommodation(accommodationDepartedDto));
    }

    @Test
    void finishAccommodationShouldThrowNoOneOneToDepartExceptionWhenNoOneIsLeaving() {
        //given
        var accommodationDepartedDto
                = new AccommodationDepartedDto("reservationId", "accommodationId", Instant.now(),
                2, "clientId", null);
        Reservation reservation = new Reservation("userId", Sector.A, 1);
        Accommodation accommodation = new Accommodation(AccommodationType.CAR, "description", Instant.now(), 1, reservation);
        //when
        when(accommodationRepository.findAccommodationByReservationIdAndId(ArgumentMatchers.any(), ArgumentMatchers.any()))
                .thenReturn(Optional.of(accommodation));
        when(reservationRepository.findById(ArgumentMatchers.any())).thenReturn(Optional.of(reservation));
        //then
        assertThrows(NoOneOneToDepartException.class,
                () -> reservationService.finishAccommodation(accommodationDepartedDto));
    }

    @Test
    void finishAccommodationShouldCloseOneAccommodationAndCreateAnotherWhenAllDataAreValid() {
        //given
        var accommodationDepartedDto
                = new AccommodationDepartedDto("reservationId", "accommodationId", Instant.now(),
                1, "clientId", null);
        Reservation reservation = new Reservation("userId", Sector.A, 1);
        Accommodation accommodation = new Accommodation(AccommodationType.CAR, "description", Instant.now(), 1, reservation);
        //when
        when(accommodationRepository.findAccommodationByReservationIdAndId(ArgumentMatchers.any(), ArgumentMatchers.any()))
                .thenReturn(Optional.of(accommodation));
        when(reservationRepository.findById(ArgumentMatchers.any())).thenReturn(Optional.of(reservation));

        Accommodation createdAccommodation = reservationService.finishAccommodation(accommodationDepartedDto);
        //then
        verify(kafkaTemplate, times(2))
                .send(ArgumentMatchers.any(), ArgumentMatchers.any(), ArgumentMatchers.any());
    }

}