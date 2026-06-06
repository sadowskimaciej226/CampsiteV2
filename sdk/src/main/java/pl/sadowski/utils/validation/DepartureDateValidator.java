package pl.sadowski.utils.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.time.LocalDate;

class DepartureDateValidator implements ConstraintValidator<Departure, LocalDate> {
    @Override
    public void initialize(Departure constraintAnnotation) {
        ConstraintValidator.super.initialize(constraintAnnotation);
    }

    @Override
    public boolean isValid(LocalDate value, ConstraintValidatorContext context) {
        LocalDate startOfYear = LocalDate.of(LocalDate.now().getYear(), 1, 1);

        return value.isAfter(startOfYear) && value.isBefore(startOfYear.plusYears(1));
    }
}
