package carsharing.service;

import carsharing.dao.CarDao;
import carsharing.daoimpl.CarDaoImpl;
import carsharing.model.Car;
import carsharing.model.Company;
import carsharing.model.Customer;

import java.sql.Connection;
import java.util.List;

public class CarService {
    private CarDao carDao;

    public CarService(Connection connection) {
        carDao = new CarDaoImpl(connection);
    }

    /**
     *
     * @param company
     * @param carName
     * @return
     * Accepts a Company object (which the Car belongs to) and a Car name to create a
     * Car object to save into Car Database
     */
    public boolean createCar(Company company, String carName) {
        return carDao.addCar(new Car(carName, company.getId()));
    }

    /**
     *
     * @param customer
     * @return
     * Returns a Car object based off the Foreign Key in Car Database from Customer ID
     */
    public Car getCar(Customer customer) {
        return carDao.getCarById(customer.getRentedCarId()).orElseThrow();
    }

    /**
     *
     * @param company
     * @return
     * Returns a List of Cars based off implementation (Current implementation returns
     * all available cars that are not rented and are from the specific company)
     */
    public List<Car> getCarList(Company company) {
        return carDao.getAvailableCarList(company);
    }
}
