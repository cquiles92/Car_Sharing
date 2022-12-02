package carsharing.service;

import carsharing.dao.CustomerDao;
import carsharing.daoimpl.CustomerDaoImpl;
import carsharing.model.Car;
import carsharing.model.Customer;

import java.sql.Connection;
import java.util.List;

public class CustomerService {
    private CustomerDao customerDao;

    public CustomerService(Connection connection) {
        customerDao = new CustomerDaoImpl(connection);
    }

    /**
     * @param customerName
     * @return Method to create a new Customer in Customer table. Returns false upon duplicate record.
     */
    public boolean createCustomer(String customerName) {
        Customer customer = new Customer(customerName);
        return customerDao.addCustomer(customer);
    }

    /**
     * @return Get a copy of all customers saved in database.
     */

    public List<Customer> returnCustomerList() {
        return customerDao.getCustomerList();
    }

    /**
     * @param customer
     * @return boolean
     * Runs the internal Update method by setting the Car ID to zero.
     */
    public boolean returnCustomerCar(Customer customer) {
        return updateCustomer(customer, 0);
    }

    /**
     * @param customer
     * @return boolean
     * Tests whether the Rented Car ID is 0/Null.
     */
    public boolean isValidRentedCarId(Customer customer) {
        return customer.getRentedCarId() != 0;
    }

    /**
     * @param customer
     * @param car
     * @return boolean
     * Runs the internal Update method by grabbing the Car ID from Car object.
     */
    public boolean rentCustomerCar(Customer customer, Car car) {
        return updateCustomer(customer, car.getId());
    }

    /**
     * @param customer
     * @return boolean
     * <p>
     * Method to update the Customer table by updating the customer record to the given ID
     * or rolling back to previous state if it cannot be updated. Returns true upon success.
     */
    private boolean updateCustomer(Customer customer, int carId) {
        boolean ifSuccessful = false;
        int rentalId = customer.getRentedCarId();

        customer.setRentedCarId(carId);

        if (customerDao.updateCustomer(customer)) {
            ifSuccessful = true;
        } else {
            customer.setRentedCarId(rentalId);
            customerDao.updateCustomer(customer);
        }

        return ifSuccessful;
    }
}
