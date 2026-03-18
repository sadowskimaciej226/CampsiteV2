package pl.sadowski.customerservice;


import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CustomerService {

    private final CustomerRepository customerRepository;

    @Transactional
    public Customer createCustomer(CustomerDto dto) {
        Customer customer = Customer.builder()
                .firstName(dto.firstName())
                .lastName(dto.lastName())
                .email(dto.email())
                .build();
        return customerRepository.save(customer);
    }

}
