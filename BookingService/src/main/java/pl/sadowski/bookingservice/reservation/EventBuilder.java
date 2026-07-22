package pl.sadowski.bookingservice.reservation;

import lombok.experimental.UtilityClass;
import pl.sadowski.sdk.avro.AccommodationEvent;


@UtilityClass
class EventBuilder {

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
