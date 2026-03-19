package pl.sadowski.customerservice;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;


import java.util.List;

@RestController
@RequestMapping("/api/customers")
@RequiredArgsConstructor
public class CustomerController {

    private final CustomerService customerService;
    private final CustomerRepository customerRepository;

    // 1️⃣ Tworzenie nowego klienta
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Customer createCustomer(@RequestBody CustomerDto request) {
        return customerService.createCustomer(request);
    }

    // 2️⃣ Pobranie wszystkich klientów (przydatne np. do testów / listy)
    @GetMapping
    public List<Customer> getAllCustomers() {
        return customerRepository.findAll();
    }
}

