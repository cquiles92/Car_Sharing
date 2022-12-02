package carsharing.manager;

import carsharing.model.Car;
import carsharing.model.Company;
import carsharing.model.Customer;
import carsharing.service.CarService;
import carsharing.service.CompanyService;
import carsharing.service.CustomerService;
import carsharing.service.DatabaseService;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.sql.Connection;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

public class DatabaseManager {
    private static final String DEFAULT_DIRECTORY_PATH = "default";
    private final BufferedReader bufferedReader;
    private final CompanyService companyService;
    private final CarService carService;
    private final CustomerService customerService;

    /**
     * @param databaseName Constructor to set up environment using JDBC to connect to given database.
     *                     Database URL is given using Command Line Argument or given a default URL.
     *                     Buffered Reader is used to handle all console inputs.
     *                     Database Service will handle connections to database and can be configured to handle multiple instances.
     *                     This particular application uses a single connection for the table services.
     *                     <p>
     *                     The Company Table is handled by the Company Service.
     *                     The Car Table is handled by the Car Service.
     *                     The Customer Table is handled by the Customer Service.
     */
    public DatabaseManager(String databaseName) {
        String DB_URL = "jdbc:h2:./src/carsharing/db/";

        if (databaseName == null || databaseName.length() == 0) {
            DB_URL += DEFAULT_DIRECTORY_PATH;
        } else {
            DB_URL += databaseName;
        }
        bufferedReader = new BufferedReader(new InputStreamReader(System.in));

        DatabaseService databaseService = new DatabaseService(DB_URL);
        Connection connection = databaseService.getConnection();

        companyService = new CompanyService(connection);
        carService = new CarService(connection);
        customerService = new CustomerService(connection);
    }

    /**
     * Default run argument which uses run. Chosen in case Runnable interface will be implemented in the future.
     */
    public void run() {
        mainMenu();
    }

    /**
     * Main menu of program that either exits the program loop or performs the following:
     * "Log in as a manager" option proceeds to go into further menus for "managers" to perform operations.
     * "Log in as a customer option proceeds to go into further menus for "customers" to perform operations.
     * "Create a customer" option creates a customer in the Customer table using Customer Service.
     */

    private void mainMenu() {
        boolean exitMenu = false;
        do {
            System.out.println("1. Log in as a manager");
            System.out.println("2. Log in as a customer");
            System.out.println("3. Create a customer");
            printReturnOption();

            switch (getInteger()) {
                case 1 -> companyManagerMenu();
                case 2 -> logInCustomer();
                case 3 -> createCustomer();
                case 0 -> exitMenu = true;
                default -> System.out.println("Error: Invalid selection.\n");
            }
        } while (!exitMenu);
    }

    /**
     * The initial Manager Menu in where Managers can request a List of Companies
     * to then be able to add Cars to the selected Company or Managers can create a company.
     */
    private void companyManagerMenu() {
        boolean exitMenu = false;

        do {
            System.out.println("1. Company list");
            System.out.println("2. Create a company");
            printReturnOption();

            switch (getInteger()) {

                case 1 -> {
                    Optional<Company> company = selectCompany();
                    company.ifPresent(this::carMenu);
                }

                case 2 -> createCompany();
                case 0 -> exitMenu = true;
                default -> System.out.println("Error: Invalid selection.\n");
            }
        } while (!exitMenu);
    }

    /**
     * @param company This menu requires the selected "Company" to be able to either request the list of
     *                Cars associated with the company or to create and add more cars to that particular company.
     */
    private void carMenu(Company company) {
        boolean exitMenu = false;
        System.out.printf("'%s' company\n", company.getName());
        do {
            List<Car> carList = carService.getCarList(company);

            System.out.println("1. Car list");
            System.out.println("2. Create a car");
            printReturnOption();

            switch (getInteger()) {
                case 1 -> {
                    printCarList(carList);
                    System.out.println();
                }
                case 2 -> createCar(company);
                case 0 -> exitMenu = true;
                default -> System.out.println("Error: Invalid selection.\n");
            }
        } while (!exitMenu);
    }

    /**
     * @param customer The Customer menu in where a particular customer has access to either rent a car
     *                 from a company, return a car, or to check on their rental car status.
     */
    private void customerMainMenu(Customer customer) {
        boolean exitMenu = false;
        do {
            System.out.println("1. Rent a car");
            System.out.println("2. Return a rented car");
            System.out.println("3. My rented car");
            printReturnOption();

            switch (getInteger()) {
                case 1 -> {
                    if (customer.getRentedCarId() != 0) {
                        System.out.println("You've already rented a car!");
                        continue;
                    }

                    Optional<Company> selectedCompany = selectCompany();
                    selectedCompany.ifPresent(company -> carRentalMenu(customer, company));
                }
                case 2 -> returnRentedCar(customer);
                case 3 -> rentedCarStatus(customer);
                case 0 -> exitMenu = true;
                default -> System.out.println("Error: Invalid selection.\n");
            }
        } while (!exitMenu);
    }

    /**
     * @param customer
     * @param company  This is menu to select a car from a list of Cars a company has. It requires the
     *                 particular customer that is making the request as well as the company to retrieve
     *                 the correct list of cars to select from.
     */
    private void carRentalMenu(Customer customer, Company company) {
        boolean exitMenu = false;
        List<Car> carList = carService.getCarList(company);

        if (customerService.isValidRentedCarId(customer)) {
            System.out.println("You've already rented a car!");
            return;
        }

        do {
            System.out.println("Choose a car:");
            printCarList(carList);
            if (carList == null || carList.size() == 0) {
                return;
            }
            printReturnOption();

            int userChoice = getInteger();
            if (userChoice == 0) {
                exitMenu = true;
            } else if (userChoice > 0 && userChoice <= carList.size()) {
                Car selectedCar = carList.get(userChoice - 1);
                customerService.rentCustomerCar(customer, selectedCar);
                System.out.printf("You rented '%s'\n", selectedCar.getName());
                exitMenu = true;
            } else {
                System.out.println("Error: Invalid Selection.\n");
            }
        } while (!exitMenu);
    }


    /**
     * This is the function used by managers to create a company in the Company table.
     * Returns true if successful and false if the value already exists.
     */
    private void createCompany() {
        System.out.println("Enter the company name:");
        if (companyService.createCompany(getUserInput())) {
            System.out.println("The company was created!\n");
        } else {
            System.out.println("Error: The company already exists in the database.\n");
        }
    }

    /**
     * @param company This is the function used by managers to create a car for a company in the Car table.
     *                Returns true if successful and false if the value already exists.
     */
    private void createCar(Company company) {
        System.out.println("Enter the car name:");
        if (carService.createCar(company, getUserInput())) {
            System.out.println("The car was created!\n");
        } else {
            System.out.println("Error: The car already exists in the database.\n");
        }
    }

    /**
     * This is the function used to create a new Customer to save in the Customer table.
     * Returns true if successful and false if the value already exists.
     */
    private void createCustomer() {
        System.out.println("Enter the customer name:");
        if (customerService.createCustomer(getUserInput())) {
            System.out.println("The customer was added!\n");
        } else {
            System.out.println("Error: The customer already exists in the database\n");
        }
    }

    /**
     * This is the function to call the menu to select the Customer from the list of Customers
     * from the Customer table or to return an empty Optional to return from the menu.
     */
    private void logInCustomer() {
        Optional<Customer> selectedCustomer = selectCustomer();

        if (selectedCustomer.isPresent()) {
            Customer customer = selectedCustomer.get();
            customerMainMenu(customer);
        }
    }

    /**
     * @return This is the function to call the menu to select a Company from the list of Companies from
     * the Company table or to return an empty Optional to indicate no Company was selected and to return.
     */
    private Optional<Company> selectCompany() {
        do {
            List<Company> companyList = companyService.getCompanyList();

            System.out.println("Choose the company:");
            printCompanyList(companyList);

            if (companyList.size() == 0) {
                return Optional.empty();
            }

            printReturnOption();
            int selection = getInteger();

            if (selection == 0) {
                return Optional.empty();
            } else if (selection > 0 && selection <= companyList.size()) {
                return Optional.of(companyList.get(selection - 1));
            } else {
                System.out.println("INVALID CHOICE\n");
            }
        } while (true);
    }

    /**
     * @return This is a function to select a Customer from the list of Customers from the Customer table
     * or to return an empty Optional which indicates no Customer was selected and to exit.
     */
    private Optional<Customer> selectCustomer() {
        do {
            List<Customer> customerList = customerService.returnCustomerList();

            printCustomerList(customerList);
            if (customerList.size() == 0) {
                return Optional.empty();
            }
            printReturnOption();
            int selection = getInteger();
            if (selection == 0) {
                return Optional.empty();
            } else if (selection > 0 && selection <= customerList.size()) {
                return Optional.of(customerList.get(selection - 1));
            } else {
                System.out.println("Error: Invalid selection.\n");
            }
        } while (true);
    }


    /**
     * @param customer This is the function for a Customer to return a car. It checks if there is a
     *                 rented car ID exists to indicate there is a rented car to then proceed into an
     *                 update request in the database.
     */
    private void returnRentedCar(Customer customer) {
        if (customerService.isValidRentedCarId(customer)) {
            customerService.returnCustomerCar(customer);
            System.out.println("You've returned a rented car!\n");
        } else {
            System.out.println("You didn't rent a car!\n");
        }
    }

    /**
     * @param customer This is the function to allow customers to check the status of their rented car.
     *                 If there is a valid rental, the information is pulled from both the Company and Car
     *                 tables.
     */
    private void rentedCarStatus(Customer customer) {
        if (customerService.isValidRentedCarId(customer)) {
            Car rentedCar = carService.getCar(customer);
            Company rentalCompany = companyService.getCompany(rentedCar.getCompanyId());

            System.out.println("You rented car:");
            System.out.println(rentedCar.getName());
            System.out.println("Company:");
            System.out.println(rentalCompany.getName());
            System.out.println();
        } else {
            System.out.println("You didn't rent a car!\n");
        }
    }

    //user interface functions below
    private void printReturnOption() {
        System.out.println("0. Back\n");
    }

    /*
    So there are three methods that probably could be condensed into one which are
    these three print lists but the problem I have is that I cannot use reflection
    to get the name of the class inside the list due to type erasure.
     */
    private void printCompanyList(List<Company> companyList) {
        if (companyList == null || companyList.size() == 0) {
            System.out.println("The company list is empty!\n");
            return;
        }

        companyList.sort(Comparator.comparingInt(Company::getId));

        System.out.println("Company list:");
        for (int i = 0; i < companyList.size(); i++) {
            System.out.println(i + 1 + ". " + companyList.get(i).getName());
        }
    }

    private void printCarList(List<Car> carList) {
        if (carList == null || carList.size() == 0) {
            System.out.println("The car list is empty!\n");
            return;
        }

        carList.sort(Comparator.comparingInt(Car::getId));

        System.out.println("Car list:");
        for (int i = 0; i < carList.size(); i++) {
            System.out.println(i + 1 + ". " + carList.get(i).getName());
        }
    }

    private void printCustomerList(List<Customer> customerList) {
        if (customerList == null || customerList.size() == 0) {
            System.out.println("The customer list is empty!\n");
            return;
        }

        customerList.sort(Comparator.comparingInt(Customer::getId));

        System.out.println("Customer list:");
        for (int i = 0; i < customerList.size(); i++) {
            System.out.println(i + 1 + ". " + customerList.get(i).getName());
        }
    }

    private String getUserInput() {
        try {
            String input = bufferedReader.readLine();
            System.out.println();
            return input;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private int getInteger() {
        int result = -1;
        try {
            result = Integer.parseInt(getUserInput());
        } catch (NumberFormatException e) {
            System.out.println("Error: Invalid Input as number.\n");
        }
        return result;
    }
}
