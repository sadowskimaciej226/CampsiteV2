package pl.sadowski.bookingservice.reservation;

import lombok.experimental.UtilityClass;
import pl.sadowski.bookingservice.reservation.view.*;

import java.time.Instant;

@UtilityClass
class EventBuilder {

    AccommodationEvent buildAccommodationEvent(AccommodationCreationDto dto, Reservation reservation) {
        return new AccommodationEvent(
                AccommodationEventType.ARRIVAL,
                dto.reservationId(),
                dto.peopleCount(),
                reservation.getSector().toString(),
                dto.type().toString(),
                dto.arrivedAt(),
                null,
                reservation.getElectricBoxNum()
        );
    }

    public static AccommodationEvent buildDepartedEvent(AccommodationDepartedDto depart, AccommodationType type, Reservation reservation) {
        return new AccommodationEvent(
                AccommodationEventType.DEPARTURE,
                depart.reservationId(),
                depart.peopleToLeave(),
                reservation.getSector().toString(),
                type.toString(),
                null,
                depart.departureTime(),
                reservation.getElectricBoxNum()
        );
    }
}
