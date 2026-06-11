package pl.sadowski.paymentservice.payment;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDate;
import java.util.List;

interface AccommodationPaymentRepository extends JpaRepository<AccommodationPayment, String> {

    @Query("SELECT ap FROM AccommodationPayment ap WHERE ap.reservationId = :reservationId " +
            "AND ap.isPaid = :isPaid " +
            "AND ap.arrivedAt >= :arrivedAt " +
            "AND ap.departedAt <= :departedAt")
    List<AccommodationPayment> findAccommodationPaymentForTime(String reservationId,
                                                               boolean isPaid,
                                                               LocalDate arrivedAt,
                                                               LocalDate departedAt);
}
