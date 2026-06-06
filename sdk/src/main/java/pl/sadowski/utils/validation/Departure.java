package pl.sadowski.utils.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

@Target({ElementType.FIELD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = DepartureDateValidator.class)
@Documented
public @interface Departure {
    String message() default "Arrival has to be from current year and can't be future";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
