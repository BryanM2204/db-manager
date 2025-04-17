package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public class DependentDAO {
    private final Connection conn;

    public DependentDAO(Connection conn) {
        this.conn = conn;
    }

    public void add(String ssn) {
        try {
            conn.setAutoCommit(false);

            // perform a lock on employee record
            System.out.println("\nAttempting to lock employee record...");
            String query = "SELECT * FROM Employee WHERE SSN = ? FOR UPDATE";
            PreparedStatement stmt = conn.prepareStatement(query);
            stmt.setString(1, ssn);
            ResultSet rs = stmt.executeQuery();
            System.out.println("Lock acquired");

            if(!rs.next()) {
                System.out.println("Employee not found");
                conn.rollback();
                return;
            }

            // Show all dependents of the Employee
            String query2 = "SELECT * FROM Dependent WHERE Essn = ?";
            PreparedStatement stmt2 = conn.prepareStatement(query2);
            stmt2.setString(1, ssn);
            ResultSet rs2 = stmt2.executeQuery();

            System.out.println("\nDependents: ");

            boolean dependents = false;
            while(rs2.next()) {
                dependents = true;
                System.out.println("\nEmployee SSN: " + rs2.getString("Essn"));
                System.out.println("Dependent Name: " + rs2.getString("Dependent_name"));
                System.out.println("Sex: " + rs2.getString("Sex"));
                System.out.println("Birthdate: " + rs2.getString("Bdate"));
                System.out.println("Relationship: " + rs2.getString("Relationship"));
            }

            if(!dependents) {
                System.out.println("None");
            }

            // ask for new dependent's information to add to the database
            System.out.println("\nEnter information:");

            System.out.println("Dependent_name: ");
            String Dependent_name = System.console().readLine();

            System.out.println("Sex: ");
            String Sex = System.console().readLine();

            System.out.println("Bdate: ");
            String Bdate = System.console().readLine();

            System.out.println("Relationship: ");
            String Relationship = System.console().readLine();

            // Insert the acquired information from the user into the database
            String insert_query = "INSERT INTO Dependent VALUES (?, ?, ?, ?, ?)";
            PreparedStatement insert_stmt = conn.prepareStatement(insert_query);
            insert_stmt.setString(1, ssn);
            insert_stmt.setString(2, Dependent_name);
            insert_stmt.setString(3, Sex);
            insert_stmt.setString(4, Bdate);
            insert_stmt.setString(5, Relationship);

            insert_stmt.executeUpdate();

            conn.commit();
            System.out.println("Dependent added");

        } catch (SQLException e) {
            System.out.println("Failed to add dependent: " + e.getMessage());
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

            // perform a lock on employee record
            System.out.println("\nAttempting to lock employee record...");
            String query = "SELECT * FROM Employee WHERE SSN = ? FOR UPDATE";
            PreparedStatement stmt = conn.prepareStatement(query);
            stmt.setString(1, ssn);
            ResultSet rs = stmt.executeQuery();
            System.out.println("Lock acquired\n");

            if(!rs.next()) {
                System.out.println("Employee not found");
                conn.rollback();
                return;
            }

            // Show all dependents of the Employee
            String query2 = "SELECT * FROM Dependent WHERE Essn = ?";
            PreparedStatement stmt2 = conn.prepareStatement(query2);
            stmt2.setString(1, ssn);
            ResultSet rs2 = stmt2.executeQuery();

            System.out.println("Dependents:");

            boolean dependents = false;
            ArrayList<String> dependents_list = new ArrayList<>();
            while(rs2.next()) {
                dependents = true;
                dependents_list.add(rs2.getString("Dependent_name"));
                System.out.println("Employee SSN: " + rs2.getString("Essn"));
                System.out.println("Dependent Name: " + rs2.getString("Dependent_name"));
                System.out.println("Sex: " + rs2.getString("Sex"));
                System.out.println("Birthdate: " + rs2.getString("Bdate"));
                System.out.println("Relationship: " + rs2.getString("Relationship") + "\n");
            }

            if(!dependents) {
                System.out.println("None\n");
                conn.rollback();
                return;
            }

            System.out.println("Enter name of Dependent to be removed: ");
            String Dependent_name = System.console().readLine();

            if(!dependents_list.contains(Dependent_name)) {
                System.out.println("Dependent not found");
                conn.rollback();
                return;
            }

            String delete_query = "DELETE FROM Dependent WHERE Essn = ? AND Dependent_name = ?";
            PreparedStatement delete_stmt = conn.prepareStatement(delete_query);
            delete_stmt.setString(1, ssn);
            delete_stmt.setString(2, Dependent_name);
            delete_stmt.executeUpdate();

            conn.commit();
            System.out.println("Dependent deleted");

        } catch (SQLException e) {
            System.out.println("Failed to delete dependent: " + e.getMessage());
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