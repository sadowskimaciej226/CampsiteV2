package pl.sadowski.bookingservice.reservation;

import lombok.experimental.UtilityClass;
import pl.sadowski.bookingservice.reservation.view.AccommodationCreationDto;
import pl.sadowski.sdk.avro.AccommodationEvent;


@UtilityClass
class EventBuilder {

    AccommodationEvent buildAccommodationEvent(AccommodationCreationDto dto, Reservation reservation) {
        return new AccommodationEvent(
                dto.accommodationId(),
                dto.reservationId(),
                dto.amount(),
                reservation.getSector().toString(),
                dto.type().toString(),
                dto.arrivedAt(),
                dto.departedAt(),
                reservation.getElectricBoxNum()
        );
    }

    AccommodationEvent buildAccommodationEvent(Accommodation accommodation, Reservation reservation) {
        return new AccommodationEvent(
                accommodation.getId(),
                reservation.getId(),
                accommodation.getAmount(),
                reservation.getSector().toString(),
                accommodation.getType().toString(),
                accommodation.getArrivedAt(),
                accommodation.getDepartedAt(),
                reservation.getElectricBoxNum()
        );
    }
}
