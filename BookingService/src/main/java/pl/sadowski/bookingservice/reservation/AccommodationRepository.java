package pl.sadowski.bookingservice.reservation;

import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

interface AccommodationRepository extends CrudRepository<Accommodation, String> {

    Optional<Accommodation> findAccommodationByReservationIdAndId(String reservationId, String id);
}
