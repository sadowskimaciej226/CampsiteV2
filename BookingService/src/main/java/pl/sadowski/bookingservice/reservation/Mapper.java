package pl.sadowski.bookingservice.reservation;

import lombok.experimental.UtilityClass;
import pl.sadowski.bookingservice.reservation.view.AccommodationCreationDto;

@UtilityClass
class Mapper {

    AccommodationCreationDto mapToDto(Accommodation accommodation) {
        return new AccommodationCreationDto(accommodation.getReservation().getId(),
                accommodation.getId(),
                accommodation.getType(),
                accommodation.getDescription(),
                accommodation.getArrivedAt(),
                accommodation.getDepartedAt(),
                accommodation.getAmount(),
                null);
    }
}
