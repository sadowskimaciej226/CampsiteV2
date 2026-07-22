package pl.sadowski.bookingservice.reservation;

import lombok.experimental.UtilityClass;
import pl.sadowski.bookingservice.reservation.view.AccommodationCreatedDto;

@UtilityClass
class Mapper {

    AccommodationCreatedDto mapToDto(Accommodation accommodation) {
        return new AccommodationCreatedDto(accommodation.getReservation().getId(),
                accommodation.getId(),
                accommodation.getType(),
                accommodation.getDescription(),
                accommodation.getArrivedAt(),
                accommodation.getDepartedAt(),
                accommodation.getAmount(),
                null);
    }
}
