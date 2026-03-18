package pl.sadowski.bookingservice.reservation;


import org.springframework.data.jpa.repository.JpaRepository;

interface ReservationRepository extends JpaRepository<Reservation, String> {
}
