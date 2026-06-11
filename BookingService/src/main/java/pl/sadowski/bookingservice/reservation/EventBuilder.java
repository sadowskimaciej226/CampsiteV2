package pl.sadowski.bookingservice.reservation;

import lombok.experimental.UtilityClass;
import pl.sadowski.bookingservice.reservation.view.AccommodationCreationDto;
import pl.sadowski.bookingservice.reservation.view.AccommodationDepartedDto;
import pl.sadowski.bookingservice.reservation.view.AccommodationType;
import pl.sadowski.sdk.avro.AccommodationEvent;
import pl.sadowski.sdk.avro.AccommodationEventType;


@UtilityClass
class EventBuilder {

    AccommodationEvent buildAccommodationEvent(AccommodationCreationDto dto, Reservation reservation) {
        return new AccommodationEvent(
                AccommodationEventType.ARRIVAL,
                dto.accommodationId(),
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
                depart.accommodationId(),
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
