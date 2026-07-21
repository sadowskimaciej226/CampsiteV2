package pl.sadowski.bookingservice.reservation;

import org.springframework.data.jpa.repository.JpaRepository;

interface AccommodationRepository extends JpaRepository<Accommodation, String> {
}
