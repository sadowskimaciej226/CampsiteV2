package pl.sadowski.bookingservice.reservation;

import org.springframework.data.repository.CrudRepository;

interface AccommodationRepository extends CrudRepository<Accommodation, String> {
}
