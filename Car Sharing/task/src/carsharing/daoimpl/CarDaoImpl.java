package carsharing.daoimpl;

import carsharing.dao.CarDao;
import carsharing.model.Car;
import carsharing.model.Company;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class CarDaoImpl implements CarDao {
    private final Connection connection;

    public CarDaoImpl(Connection connection) {
        this.connection = connection;
        initializeTable();
    }

    @Override
    public boolean addCar(Car car) {
        boolean result = false;
        String sqlStatement = "INSERT INTO CAR(name,company_id) VALUES(?,?)";
        if (!ifCarExists(car)) {
            try {
                PreparedStatement statement = connection.prepareStatement(sqlStatement);
                statement.setString(1, car.getName());
                statement.setInt(2, car.getCompanyId());
                statement.execute();
                result = true;
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return result;
    }

    @Override
    public boolean ifCarExists(Car car) {
        boolean ifSuccessful = false;
        String sqlStatement = "SELECT * FROM CAR WHERE name = ?";

        try {
            PreparedStatement preparedStatement = connection.prepareStatement(sqlStatement);
            preparedStatement.setString(1, car.getName());
            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                String savedCarName = resultSet.getString(2);
                if (car.getName().equals(savedCarName)) {
                    ifSuccessful = true;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return ifSuccessful;
    }

    @Override
    public List<Car> getAvailableCarList(Company company) {
        List<Car> carList = new ArrayList<>();
        String sqlStatement = "SELECT * " +
                "FROM CAR " +
                "LEFT JOIN CUSTOMER " +
                "ON CAR.id = CUSTOMER.rented_car_id " +
                "WHERE CUSTOMER.name IS NULL AND CAR.company_id = ?";
        try {
            PreparedStatement preparedStatement = connection.prepareStatement(sqlStatement);
            preparedStatement.setInt(1, company.getId());
            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                int carId = resultSet.getInt(1);
                String name = resultSet.getString(2);
                int companyId = resultSet.getInt(3);

                Car car = new Car(name, companyId);
                car.setId(carId);
                carList.add(car);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return carList;
    }

    @Override
    public Optional<Car> getCarById(int carId) {
        Optional<Car> result = Optional.empty();
        String sqlStatement = "SELECT * FROM CAR WHERE ID = ?";

        try {
            PreparedStatement preparedStatement = connection.prepareStatement(sqlStatement);
            preparedStatement.setInt(1, carId);
            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                int savedCarId = resultSet.getInt(1);
                String savedCarName = resultSet.getString(2);
                int savedCompanyId = resultSet.getInt(3);

                Car car = new Car(savedCarName, savedCompanyId);
                car.setId(savedCarId);

                if (savedCarId == carId) {
                    result = Optional.of(car);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return result;
    }

    private void initializeTable() {
        final String sqlCreateTableStatement = "CREATE TABLE IF NOT EXISTS CAR " +
                "(ID INTEGER NOT NULL AUTO_INCREMENT, " +
                " NAME VARCHAR(255) NOT NULL UNIQUE, " +
                " COMPANY_ID INT NOT NULL, " +
                " PRIMARY KEY (ID)," +
                " FOREIGN KEY (COMPANY_ID) REFERENCES COMPANY(ID))";

        try {
            connection.prepareStatement(sqlCreateTableStatement).execute();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}