
package com.creditrisk.controller;

import com.creditrisk.entity.Customer;
import com.creditrisk.entity.DeletedCustomer;
import com.creditrisk.service.CustomerService;

import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/customers")
@CrossOrigin(origins = "*")
public class CustomerController {

    private final CustomerService service;

    public CustomerController(CustomerService service) {
        this.service = service;
    }

    // Add Customer
    @PostMapping
    public Customer addCustomer(@RequestBody Customer customer) {
        return service.addCustomer(customer);
    }

    // Get All Active Customers
    @GetMapping
    public List<Customer> getAllCustomers() {
        return service.getAllCustomers();
    }

    // Get Customer By ID
    @GetMapping("/{id}")
    public Customer getCustomerById(@PathVariable Integer id) {
        return service.getCustomerById(id);
    }

    // Update Customer
    @PutMapping("/{id}")
    public Customer updateCustomer(
            @PathVariable Integer id,
            @RequestBody Customer customer) {

        return service.updateCustomer(id, customer);
    }

    // Move Customer to Deleted Customers
    @DeleteMapping("/{id}")
    public String deleteCustomer(@PathVariable Integer id) {
        service.deleteCustomer(id);
        return "Customer deleted successfully";
    }

    // Get All Deleted Customers
    @GetMapping("/deleted")
    public List<DeletedCustomer> getDeletedCustomers() {
        return service.getDeletedCustomers();
    }

    // Restore Customer
    @PutMapping("/{id}/restore")
    public Customer restoreCustomer(@PathVariable Integer id) {
        return service.restoreCustomer(id);
    }

    // Permanently Delete Customer
    @DeleteMapping("/{id}/permanent")
    public String permanentlyDeleteCustomer(@PathVariable Integer id) {
        service.permanentlyDeleteCustomer(id);
        return "Customer permanently deleted";
    }
}
