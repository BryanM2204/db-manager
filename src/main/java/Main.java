import java.sql.Connection;
import java.sql.Date;
import java.sql.SQLException;

import dao.DepartmentDAO;
import dao.DependentDAO;
import dao.Dept_locationsDAO;
import dao.EmployeeDAO;
import model.Department;
import model.Employee;

public class Main {
    public static void main(String[] args) {
        DBConnection db = new DBConnection();
        try(Connection conn = db.getConnection()) {
            System.out.println("Currently using connection:");

            // Continue work here:
            boolean flag = true;
            while(flag) {
                System.out.println("\n******* Database Menu *******");
                System.out.println("1. Add new employee");
                System.out.println("2. View employee");
                System.out.println("3. Modify employee");
                System.out.println("4. Remove employee");
                System.out.println("5. Add new dependent");
                System.out.println("6. Remove dependent");
                System.out.println("7. Add new department");
                System.out.println("8. View department");
                System.out.println("9. Remove department");
                System.out.println("10. Add department location");
                System.out.println("11. Remove department location");
                System.out.println("12. Exit");

                System.out.println("\nEnter your choice: ");
                String choice = System.console().readLine();

                switch(choice) {
                    case "1":
                        EmployeeDAO employeeDAO = new EmployeeDAO(conn);
                        Employee employee = new Employee();

                        System.out.println("Fname: ");
                        employee.setFname(System.console().readLine());

                        System.out.println("Minit: ");
                        employee.setMinit(System.console().readLine().charAt(0));

                        System.out.println("Lname: ");
                        employee.setLname(System.console().readLine());

                        System.out.println("Ssn: ");
                        employee.setSsn(System.console().readLine());

                        System.out.println("Bdate: ");
                        employee.setBdate(Date.valueOf(System.console().readLine()));

                        System.out.println("Address: ");
                        employee.setAddress(System.console().readLine());

                        System.out.println("Sex: ");
                        employee.setSex(System.console().readLine().charAt(0));

                        System.out.println("Salary: ");
                        employee.setSalary(Float.parseFloat(System.console().readLine()));

                        System.out.println("Super_ssn: ");
                        employee.setSuper_ssn(System.console().readLine());

                        System.out.println("Dno: ");
                        employee.setDno(Integer.parseInt(System.console().readLine()));

                        employeeDAO.add(employee);

                        break;

                    case "2":
                        EmployeeDAO employeeDAO2 = new EmployeeDAO(conn);

                        System.out.println("Employee SSN: ");
                        employeeDAO2.get(System.console().readLine());

                        break;

                    case "3":
                        EmployeeDAO employeeDAO3 = new EmployeeDAO(conn);

                        System.out.println("Employee SSN: ");
                        employeeDAO3.update(System.console().readLine());

                        break;

                    case "4":
                        EmployeeDAO employeeDAO4 = new EmployeeDAO(conn);

                        System.out.println("Employee SSN: ");
                        employeeDAO4.delete(System.console().readLine());
                        break;

                    case "5":
                        DependentDAO dependentDAO = new DependentDAO(conn);

                        System.out.println("Employee SSN: ");
                        dependentDAO.add(System.console().readLine());

                        break;

                    case "6":
                        DependentDAO dependentDAO2 = new DependentDAO(conn);

                        System.out.println("Employee SSN: ");
                        dependentDAO2.delete(System.console().readLine());
                        break;

                    case "7":
                        DepartmentDAO departmentDAO = new DepartmentDAO(conn);
                        Department department = new Department();

                        System.out.println("Dname: ");
                        department.setDname(System.console().readLine());

                        System.out.println("Dnumber: ");
                        department.setDnumber(Integer.parseInt(System.console().readLine()));

                        System.out.println("Mgr_ssn");
                        department.setMgr_ssn(System.console().readLine());

                        System.out.println("Mgr_start_date");
                        department.setMgr_start_date(Date.valueOf(System.console().readLine()));

                        departmentDAO.add(department);

                        break;

                    case "8":
                        DepartmentDAO departmentDAO3 = new DepartmentDAO(conn);

                        System.out.println("Dnumber: ");
                        departmentDAO3.get(Integer.parseInt(System.console().readLine()));
                        break;

                    case "9":
                        DepartmentDAO departmentDAO4 = new DepartmentDAO(conn);
                        System.out.println("Dnumber: ");
                        departmentDAO4.delete(Integer.parseInt(System.console().readLine()));

                        break;

                    case "10":
                        Dept_locationsDAO dept_locationsDAO = new Dept_locationsDAO(conn);

                        System.out.println("Dnumber: ");
                        dept_locationsDAO.add(Integer.parseInt(System.console().readLine()));

                        break;

                    case "11":
                        Dept_locationsDAO dept_locationsDAO2 = new Dept_locationsDAO(conn);

                        System.out.println("Dnumber: ");
                        dept_locationsDAO2.delete(Integer.parseInt(System.console().readLine()));
                        break;

                    case "12":
                        flag = false;
                        break;

                    default:
                        System.out.println("Invalid choice");
                        break;
                }

            }

        } catch (SQLException e) {
            System.out.println("Connection failed: " + e.getMessage());
        }
    }
}