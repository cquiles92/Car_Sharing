package carsharing.dao;

import carsharing.model.Car;
import carsharing.model.Company;

import java.util.List;
import java.util.Optional;

public interface CarDao {
    boolean addCar(Car car);

    boolean ifCarExists(Car car);

    List<Car> getAvailableCarList(Company company);

    Optional<Car> getCarById(int id);
}
