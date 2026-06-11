package pl.sadowski.paymentservice.payment;

import lombok.experimental.UtilityClass;
import pl.sadowski.sdk.avro.AccommodationEvent;

@UtilityClass
class Mapper {

    AccommodationPayment mapToAccommodationPayment(AccommodationEvent accommodationEvent) {
        AccommodationPayment accommodationPayment = new AccommodationPayment();
        accommodationPayment.setAccommodationId(accommodationEvent.getAccommodationId());
        accommodationPayment.setAccommodationType(accommodationPayment.getAccommodationType());
        accommodationPayment.setAmountOfPeople(accommodationEvent.getAmountOfPeople());
        accommodationPayment.setSector(accommodationEvent.getSector());
        accommodationPayment.setArrivedAt(accommodationEvent.getArrivedAt());
        accommodationPayment.setDepartedAt(accommodationEvent.getDepartedAt());
        accommodationPayment.setReservationId(accommodationPayment.getReservationId());
        accommodationPayment.setElectricityConnected(accommodationPayment.isElectricityConnected());
        return accommodationPayment;
    }
}
