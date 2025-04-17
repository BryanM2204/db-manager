package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public class Dept_locationsDAO {
    private final Connection conn;

    public Dept_locationsDAO(Connection conn) {
        this.conn = conn;
    }

    public void add(int Dnumber) {
        try {
            conn.setAutoCommit(false);

            System.out.println("\nAttempting to lock department record...");

            // Lock the department row to avoid conflicts
            String query = "SELECT * FROM Department WHERE Dnumber = ? FOR UPDATE";
            PreparedStatement stmt = conn.prepareStatement(query);
            stmt.setInt(1, Dnumber);
            ResultSet rs = stmt.executeQuery();
            System.out.println("Lock acquired");

            if(!rs.next()) {
                System.out.println("\nDepartment not found");
                conn.rollback();
                return;
            }

            // Display all existing locations for the department
            String query2 = "SELECT * FROM Dept_locations WHERE Dnumber = ?";
            PreparedStatement stmt2 = conn.prepareStatement(query2);
            stmt2.setInt(1, Dnumber);
            ResultSet rs2 = stmt2.executeQuery();

            System.out.println("\nLocations:");
            boolean locations = false;
            while(rs2.next()) {
                locations = true;
                System.out.println("Department Number: " + rs2.getString("Dnumber"));
                System.out.println("Location: " + rs2.getString("Dlocation\n"));
            }

            if(!locations) {
                System.out.println("Department has no locations");
            }

            // create the new location record
            System.out.println("\nEnter information:");

            System.out.println("Location Name: ");
            String Dlocation = System.console().readLine();

            // insert the information inputted from the user into the database
            String insert_query = "INSERT INTO Dept_locations VALUES (?, ?)";
            PreparedStatement insert_stmt = conn.prepareStatement(insert_query);
            insert_stmt.setInt(1, Dnumber);
            insert_stmt.setString(2, Dlocation);
            insert_stmt.executeUpdate();

            conn.commit();
            System.out.println("New department location added");

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

    public void delete(int Dnumber) {
        try {
            conn.setAutoCommit(false);

            System.out.println("\nAttempting to lock department record...");

            // Lock the department row to ensure consistent deletion
            String query = "SELECT * FROM Department WHERE Dnumber = ? FOR UPDATE";
            PreparedStatement stmt = conn.prepareStatement(query);
            stmt.setInt(1, Dnumber);
            ResultSet rs = stmt.executeQuery();
            System.out.println("Lock acquired");

            if(!rs.next()) {
                System.out.println("\nDepartment not found");
                conn.rollback();
                return;
            }

            // Fetch and display all department locations
            String query2 = "SELECT * FROM Dept_locations WHERE Dnumber = ?";
            PreparedStatement stmt2 = conn.prepareStatement(query2);
            stmt2.setInt(1, Dnumber);
            ResultSet rs2 = stmt2.executeQuery();

            System.out.println("\nLocations:");
            ArrayList<String> location_names = new ArrayList<>();

            if(!rs2.next()) {
                System.out.println("Department has no locations");
                conn.rollback();
                return;
            }

            do {
                location_names.add(rs2.getString("Dlocation"));
                System.out.println("\t- " + rs2.getString("Dlocation"));

            } while(rs2.next());

            System.out.println("\nEnter name of location to be removed: ");
            String location = System.console().readLine();

            if(!location_names.contains(location)) {
                while(!location_names.contains(location)) {
                    System.out.println("Location not found");
                    System.out.println("Enter name of location to be removed: ");
                    location = System.console().readLine();
                }
            }

            // Delete the selected location from the table
            String delete_query = "DELETE FROM Dept_locations WHERE Dnumber = ? AND Dlocation = ?";
            PreparedStatement delete_stmt = conn.prepareStatement(delete_query);
            delete_stmt.setInt(1, Dnumber);
            delete_stmt.setString(2, location);
            delete_stmt.executeUpdate();

            conn.commit();
            System.out.println("Department location deleted");


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
}