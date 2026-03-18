package pl.sadowski.customerservice;

import org.springframework.data.jpa.repository.JpaRepository;

interface CustomerRepository extends JpaRepository<Customer, String> {
}
