package pl.sadowski.customerservice;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Builder
class Customer {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    String id;
    private String firstName;
    private String lastName;
    private String email;

}
