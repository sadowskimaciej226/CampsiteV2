package pl.sadowski.utils.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.time.LocalDate;

class ArrivalDateValidator implements ConstraintValidator<Arrival, LocalDate> {
    @Override
    public void initialize(Arrival constraintAnnotation) {
        ConstraintValidator.super.initialize(constraintAnnotation);
    }

    @Override
    public boolean isValid(LocalDate value, ConstraintValidatorContext context) {
        if (value == null) {
            return false;
        }

        LocalDate today = LocalDate.now();
        LocalDate startOfYear = LocalDate.of(today.getYear(), 1, 1);

        return value.isAfter(startOfYear) && (value.isBefore(today) || value.isEqual(today));
    }
}
