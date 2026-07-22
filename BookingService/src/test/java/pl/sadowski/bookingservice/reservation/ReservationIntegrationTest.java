package pl.sadowski.bookingservice.reservation;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import pl.sadowski.bookingservice.reservation.view.AccommodationCreatedDto;
import pl.sadowski.bookingservice.reservation.view.ReservationRequestDto;
import pl.sadowski.bookingservice.reservation.view.ReservationResponseDto;
import pl.sadowski.bookingservice.setup.BaseIntegrationTest;

import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static pl.sadowski.bookingservice.reservation.TestObjectCreator.*;

class ReservationIntegrationTest extends BaseIntegrationTest {

    @Autowired
    private ReservationRepository reservationRepository;

    @Autowired
    private AccommodationRepository accommodationRepository;

    @BeforeEach
    void setUp() {
        accommodationRepository.deleteAll();
        reservationRepository.deleteAll();
    }

    @Test
    void createReservation_shouldCreateReservation_whenAllDataAreValid() throws Exception {
        //given
        ReservationRequestDto reservationRequestDto = new ReservationRequestDto("userId", "A", 1);
        //when
        ReservationResponseDto reservationResponseDto = objectMapper.readValue(mockMvc.perform(post("/reservation")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(reservationRequestDto)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString(), ReservationResponseDto.class);
        //then
        assertThat(reservationRequestDto.electricBoxNum(), equalTo(1));
        assertThat(reservationResponseDto.userId(), equalTo("userId"));
        assertThat(reservationResponseDto.reservationId(), notNullValue());
        assertThat(reservationResponseDto.sector(), equalTo("A"));
    }

    @Test
    void createAccommodation_shouldCreateAccommodation_whenReservationIsCreated() throws Exception {
        //given
        Reservation reservation = reservationRepository.save(createReservation());
        var accommodationCreationDto = createAccommodationCreationDto(reservation.getId());
        //when
        AccommodationCreatedDto accommodationResponse =
                objectMapper.readValue(mockMvc.perform(post("/reservation/accommodation")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(accommodationCreationDto)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString(), AccommodationCreatedDto.class);
        //then
        List<Accommodation> all = accommodationRepository.findAll();
        assertThat(all, hasSize(1));
        Accommodation first = all.getFirst();
        assertThat(first.getReservation().getId(), equalTo(reservation.getId()));
    }

    @Test
    void finishAccommodation_shouldCreateNewAccommodation_AfterFinishAmountOfSourceIsGreaterThen0() throws Exception {
        //given
        Reservation reservation = reservationRepository.save(createReservation());
        Accommodation oldAccommodation = accommodationRepository.save(createAccommodation(reservation));
        //when
        mockMvc.perform(post("/reservation/departure")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(
                        createAccommodationDepartedDto(reservation, oldAccommodation, 1))))
                .andExpect(status().isOk());
        //then
        List<Reservation> all = reservationRepository.findAll();
        assertThat(all, hasSize(1));
        List<Accommodation> allAccommodations = accommodationRepository.findAll();
        assertThat(allAccommodations, hasSize(2));
        Accommodation newAccommodation = allAccommodations.getLast();
        assertThat(newAccommodation.getAmount(), equalTo(1));
        assertThat(newAccommodation.getType(), equalTo(oldAccommodation.getType()));
        assertThat(newAccommodation.getDescription(), equalTo("newAccommodationDescription"));
    }


}
