package pl.sadowski.bookingservice.reservation;

import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface AccommodationRepository extends CrudRepository<Accommodation, String> {
}
