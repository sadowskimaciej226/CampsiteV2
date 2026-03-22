package pl.sadowski.bookingservice.reservation;

import lombok.experimental.UtilityClass;
import pl.sadowski.bookingservice.reservation.view.AccommodationCreationDto;
import pl.sadowski.bookingservice.reservation.view.AccommodationEvent;
import pl.sadowski.bookingservice.reservation.view.AccommodationType;

import java.time.Instant;

@UtilityClass
class EventBuilder {

    AccommodationEvent buildAccommodationEvent(String reservationId, AccommodationCreationDto dto, Reservation reservation) {
        return new AccommodationEvent(
                reservationId,
                dto.peopleCount(),
                reservation.getSector(),
                dto.type().toString(),
                dto.arrivedAt(),
                null,
                reservation.getElectricBoxNum()
        );
    }

    public static AccommodationEvent buildDepartedEvent(String reservationId, int peopleLeft, AccommodationType type, Instant departedTime, Reservation reservation) {
        return new AccommodationEvent(
                reservationId,
                peopleLeft,
                reservation.getSector(),
                type.toString(),
                null,
                departedTime,
                reservation.getElectricBoxNum()
        );
    }
}
