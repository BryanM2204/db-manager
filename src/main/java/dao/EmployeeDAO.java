package dao;

import model.Employee;

import java.sql.*;
import java.util.ArrayList;


public class EmployeeDAO {
    private final Connection conn;

    public EmployeeDAO(Connection conn) {
        this.conn = conn;
    }

    public void get(String ssn) {

        // Retrieves detailed employee info by SSN, including supervisor and dependents
        String query = """
                SELECT E.Fname, E.Minit, E.Lname, E.Ssn, E.Bdate, E.Address, E.Sex, E.Salary, E.Super_ssn, E.Dno, S.Fname AS
                Supervisor_Fname, S.Minit as Supervisor_Minit, S.Lname AS Supervisor_Lname, D.Dname AS Department_Name, F.Dependent_name AS Dependent_Name
                FROM Employee E LEFT JOIN Employee S ON E.Super_ssn = S.Ssn
                LEFT JOIN Department D ON E.Dno = D.Dnumber
                LEFT JOIN Dependent F ON E.Ssn = F.Essn
                WHERE E.Ssn = ?""";
        try(PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, ssn);
            ResultSet rs = stmt.executeQuery();

            if(!rs.next()) {
                System.out.println("Employee not found");
            }
            ArrayList<String> dependentNames = new ArrayList<>();

            System.out.println("\nFirst Name: " + rs.getString("Fname"));
            System.out.println("Middle Initial: " + rs.getString("Minit"));
            System.out.println("Last Name: " + rs.getString("Lname"));
            System.out.println("SSN: " + rs.getString("Ssn"));
            System.out.println("Birthdate: " + rs.getString("Bdate"));
            System.out.println("Address: " + rs.getString("Address"));
            System.out.println("Sex: " + rs.getString("Sex"));
            System.out.println("Salary: " + rs.getString("Salary"));
            System.out.println("Super_ssn: " + rs.getString("Super_ssn"));
            System.out.println("Dno: " + rs.getString("Dno"));
            System.out.println("Supervisor: " + rs.getString("Supervisor_Fname") + " " + rs.getString("Supervisor_Minit") + " " + rs.getString("Supervisor_Lname"));
            System.out.println("Department: " + rs.getString("Department_Name"));
            System.out.println("Dependent(s): ");
            
            do {
                dependentNames.add(rs.getString("Dependent_Name"));
            } while (rs.next());

            for(String s : dependentNames) {
                if(s == null) {
                    System.out.println("\t- none");
                } else {
                    System.out.println("\t- " + s);
                }
            }

        } catch (SQLException e) {
            System.out.println("Failed to get employee: " + e.getMessage());
        }
    }

    public void add(Employee employee) {

        // Adds a new employee record to the Employee table
        String query = "INSERT INTO Employee VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try(PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, employee.getFname());
            stmt.setString(2, String.valueOf(employee.getMinit()));
            stmt.setString(3, employee.getLname());
            stmt.setString(4, employee.getSsn());
            stmt.setDate(5, employee.getBdate());
            stmt.setString(6, employee.getAddress());
            stmt.setString(7, String.valueOf(employee.getSex()));
            stmt.setFloat(8, employee.getSalary());
            stmt.setString(9, employee.getSuper_ssn());
            stmt.setInt(10, employee.getDno());

            stmt.executeUpdate();
            System.out.println("Employee added successfully");
        } catch (SQLException e) {
            System.out.println("Failed to add employee: " + e.getMessage());
        }
    }


    public void update(String ssn) {
        try{
            conn.setAutoCommit(false);

            System.out.println("\nAttempting to lock employee record...");

            // Retrieve details of Employee while also performing a lock on the row
            String query = "SELECT * FROM Employee WHERE Ssn = ? FOR UPDATE";
            PreparedStatement stmt = conn.prepareStatement(query);
            stmt.setString(1, ssn);
            ResultSet rs = stmt.executeQuery();
            System.out.println("Lock acquired");

            if(rs.next()) {
                System.out.println("\nFirst Name: " + rs.getString("Fname"));
                System.out.println("Middle Initial: " + rs.getString("Minit"));
                System.out.println("Last Name: " + rs.getString("Lname"));
                System.out.println("SSN: " + rs.getString("Ssn"));
                System.out.println("Birthdate: " + rs.getString("Bdate"));
                System.out.println("Address: " + rs.getString("Address"));
                System.out.println("Sex: " + rs.getString("Sex"));
                System.out.println("Salary: " + rs.getString("Salary"));
                System.out.println("Super_ssn: " + rs.getString("Super_ssn"));
                System.out.println("Dno: " + rs.getString("Dno"));

                System.out.println("\nEnter new information (leave blank to keep current):");
                System.out.print("Address: ");
                String Address = System.console().readLine();

                System.out.print("Sex: ");
                String Sex = System.console().readLine();

                System.out.print("Salary: ");
                String Salary = System.console().readLine();

                System.out.print("Supervisor SSN (type NULL if desired): ");
                String Super_ssn = System.console().readLine();
                if(Super_ssn.equalsIgnoreCase("null")) {
                    Super_ssn = null;
                }

                System.out.print("Department Number: ");
                String Dno = System.console().readLine();

                // Dynamically construct a query where an update query is performed
                // This sets all the user defined fields to the newest values inputted by the user
                StringBuilder update_query = new StringBuilder("UPDATE Employee SET ");
                ArrayList<String> fields = new ArrayList<>();

                if(!Address.isBlank()) {
                    update_query.append("Address = ?, ");
                    fields.add(Address);
                }

                if(!Sex.isBlank()) {
                    update_query.append("Sex = ?, ");
                    fields.add(Sex);
                }

                if(!Salary.isBlank()) {
                    update_query.append("Salary = ?, ");
                    fields.add(Salary);
                }

                if(Super_ssn != null) {
                    if(!Super_ssn.isBlank()) {
                        update_query.append("Super_ssn = ?, ");
                        fields.add(Super_ssn);
                    }
                }


                if(!Dno.isBlank()) {
                    update_query.append("Dno = ?, ");
                    fields.add(Dno);
                }

                // Added to end of query - matches the Ssn with the user inputted Ssn
                update_query.setLength(update_query.length() - 2);
                update_query.append(" WHERE Ssn = ?");
                fields.add(ssn);

                PreparedStatement update_stmt = conn.prepareStatement(String.valueOf(update_query));
                for(int i = 0; i < fields.size(); i++) {
                    update_stmt.setString(i + 1, fields.get(i));
                }

                update_stmt.executeUpdate();
                System.out.println("\nEmployee updated successfully");
            } else {
                System.out.println("\nEmployee not found");
            }

            conn.commit();
            System.out.println("Commit successful");

        } catch (SQLException e) {
            System.out.println("Failed to update employee: " + e.getMessage());
            try {
                conn.rollback();
                System.out.println("Rollback successful");
            } catch (SQLException ex) {
                System.out.println("Rollback failed: " + ex.getMessage());
            }
        } finally {
            try {
                conn.setAutoCommit(true);
            } catch (SQLException e) {
                System.out.println("Failed to enable autocommit: " + e.getMessage());
            }
        }
    }

    public void delete(String ssn) {
        try {
            conn.setAutoCommit(false);

            // Retrieve details of employee while also locking the row
            System.out.println("\nAttempting to lock employee record...");
            String query = "SELECT * FROM Employee WHERE Ssn = ? FOR UPDATE";
            PreparedStatement stmt = conn.prepareStatement(query);
            stmt.setString(1, ssn);
            ResultSet rs = stmt.executeQuery();
            System.out.println("Lock acquired");

            if(rs.next()) {
                System.out.println("\nFirst Name: " + rs.getString("Fname"));
                System.out.println("Middle Initial: " + rs.getString("Minit"));
                System.out.println("Last Name: " + rs.getString("Lname"));
                System.out.println("SSN: " + rs.getString("Ssn"));
                System.out.println("Birthdate: " + rs.getString("Bdate"));
                System.out.println("Address: " + rs.getString("Address"));
                System.out.println("Sex: " + rs.getString("Sex"));
                System.out.println("Salary: " + rs.getString("Salary"));
                System.out.println("Super_ssn: " + rs.getString("Super_ssn"));
                System.out.println("Dno: " + rs.getString("Dno"));

                boolean hasDependencies = false;

                // obtain employees whose boss has the ssn inputted by the user
                String query2 = "SELECT * FROM Employee WHERE Super_ssn = ?";
                PreparedStatement stmt2 = conn.prepareStatement(query2);
                stmt2.setString(1, ssn);
                ResultSet rs2 = stmt2.executeQuery();
                if(rs2.next()) {
                    hasDependencies = true;
                }

                // obtain departments where the manager is the employee with the user inputted ssn
                String query3 = "SELECT * FROM Department WHERE Mgr_ssn = ?";
                PreparedStatement stmt3 = conn.prepareStatement(query3);
                stmt3.setString(1, ssn);
                ResultSet rs3 = stmt3.executeQuery();
                if(rs3.next()) {
                    hasDependencies = true;
                }

                // obtain dependents that are connected to the employee of the ssn inputted by user
                String query4 = "SELECT * FROM Dependent WHERE Essn = ?";
                PreparedStatement stmt4 = conn.prepareStatement(query4);
                stmt4.setString(1, ssn);
                ResultSet rs4 = stmt4.executeQuery();
                if(rs4.next()) {
                    hasDependencies = true;
                }

                // checks works_on table to see if employee is connected to any entries
                String query5 = "SELECT * FROM Works_on WHERE Essn = ?";
                PreparedStatement stmt5 = conn.prepareStatement(query5);
                stmt5.setString(1, ssn);
                ResultSet rs5 = stmt5.executeQuery();
                if(rs5.next()) {
                    hasDependencies = true;
                }

                if(hasDependencies) {
                    System.out.println("\nCannot delete employee with dependencies. Please remove dependencies first.\nDeletion cancelled");
                    conn.rollback();
                    return;
                }

                System.out.println("\nComfirm deletion? Yes or no?");
                String answer = System.console().readLine();

                if(answer.equalsIgnoreCase("yes")) {
                    // if no dependencies and the user inputs yes, then perform a delete on the employee with the specified ssn
                    String delete_query = "DELETE FROM Employee WHERE Ssn = ?";
                    PreparedStatement delete_stmt = conn.prepareStatement(delete_query);
                    delete_stmt.setString(1, ssn);
                    delete_stmt.executeUpdate();

                } else {
                    System.out.println("Deletion cancelled");
                    conn.rollback();
                    return;
                }

                conn.commit();
                System.out.println("Employee deleted successfully");

            } else {
                System.out.println("Employee not found");
            }


        } catch (SQLException e) {
            System.out.println("Failed to delete employee: " + e.getMessage());
            try {
                conn.rollback();
                System.out.println("Rollback successful");
            } catch (SQLException ex) {
                System.out.println("Rollback failed: " + ex.getMessage());
            }
        } finally {
            try {
                conn.setAutoCommit(true);
            } catch (SQLException e) {
                System.out.println("Failed to enable autocommit: " + e.getMessage());
            }
        }
    }
}