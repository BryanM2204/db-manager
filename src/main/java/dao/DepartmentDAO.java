package dao;

import model.Department;

import java.sql.*;
import java.util.ArrayList;

public class DepartmentDAO {
    private final Connection conn;

    public DepartmentDAO(Connection conn) {
        this.conn = conn;
    }

    public void get(int Dept_number) {

        // Retrieves department details, including manager info and locations
        String query = "SELECT D.Dname, D.Dnumber, D.Mgr_ssn, D.Mgr_start_date, S.Fname AS Supervisor_Fname, S.Minit as Supervisor_Minit, S.Lname AS" +
                " Supervisor_Lname, L.Dlocation FROM Department D LEFT JOIN Employee S ON D.Mgr_ssn = S.Ssn LEFT JOIN Dept_locations L ON D.Dnumber = L.Dnumber WHERE D.Dnumber = ?";
        try(PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, Dept_number);
            ResultSet rs = stmt.executeQuery();

            if(!rs.next()) {
                System.out.println("Department not found");
            }

            System.out.println("\nDepartment Name: " + rs.getString("Dname"));
            System.out.println("Dnumber: " + rs.getInt("Dnumber"));
            System.out.println("Manager SSN: " + rs.getString("Mgr_ssn"));
            System.out.println("Manager Start_Date: " + rs.getDate("Mgr_start_date"));
            System.out.println("Supervisor: " + rs.getString("Supervisor_Fname") + " " + rs.getString("Supervisor_Minit") + " " + rs.getString("Supervisor_Lname"));
            System.out.println("Location(s): ");

            ArrayList<String> locations = new ArrayList<>();

            do {
                locations.add(rs.getString("Dlocation"));
            } while (rs.next());

            for(String s : locations) {
                System.out.println("\t- " + s);
            }

        } catch (SQLException e) {
            System.out.println("Failed to get department: " + e.getMessage());
        }
    }

    public void add(Department department) {
        // Insert a new department record to the database in the Department table
        String query = "INSERT INTO Department VALUES (?, ?, ?, ?)";
        try(PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, department.getDname());
            stmt.setInt(2, department.getDnumber());
            stmt.setString(3, department.getMgr_ssn());
            stmt.setDate(4, department.getMgr_start_date());

            stmt.executeUpdate();
            System.out.println("Department added successfully");
        } catch (SQLException e) {
            System.out.println("Failed to add department: " + e.getMessage());
        }
    }

    public void delete(int Dnumber) {
        try {
            conn.setAutoCommit(false);

            System.out.println("\nAttempting to lock department record...");

            // Lock department row for safe deletion
            String query = "SELECT * FROM Department WHERE Dnumber = ? FOR UPDATE";

            PreparedStatement stmt = conn.prepareStatement(query);
            stmt.setInt(1, Dnumber);
            ResultSet rs = stmt.executeQuery();
            System.out.println("Lock acquired");

            if(rs.next()) {
                System.out.println("\nDepartment information: ");
                System.out.println("Department Name: " + rs.getString("Dname"));
                System.out.println("Department Number: " + rs.getString("Dnumber"));
                System.out.println("Manager SSN: " + rs.getString("Mgr_ssn"));
                System.out.println("Manager Start Date: " + rs.getString("Mgr_start_date"));


                boolean hasDependencies = false;

                // Check for locations assigned to the department
                String query2 = "SELECT * FROM Dept_locations WHERE Dnumber = ?";

                PreparedStatement stmt2 = conn.prepareStatement(query2);
                stmt2.setInt(1, Dnumber);
                ResultSet rs2 = stmt2.executeQuery();
                if(rs2.next()) {
                    hasDependencies = true;
                }

                // Check for employees assigned to the department
                String query3 = "SELECT * FROM Employee WHERE Dno = ?";
                PreparedStatement stmt3 = conn.prepareStatement(query3);
                stmt3.setInt(1, Dnumber);
                ResultSet rs3 = stmt3.executeQuery();
                if(rs3.next()) {
                    hasDependencies = true;
                }

                // Check for projects assigned to department
                String query4 = "SELECT * FROM Project WHERE Dnum = ?";
                PreparedStatement stmt4 = conn.prepareStatement(query4);
                stmt4.setInt(1, Dnumber);
                ResultSet rs4 = stmt4.executeQuery();
                if(rs4.next()) {
                    hasDependencies = true;
                }

                if(hasDependencies) {
                    System.out.println("\nCannot delete department with dependencies\nDeletion cancelled");
                    conn.rollback();
                    return;
                }

                System.out.println("\nComfirm deletion? Yes or no?");
                String answer = System.console().readLine();

                if(answer.equalsIgnoreCase("Yes")) {

                    // Delete the department that was specified by the user
                    String delete_query = "DELETE FROM Department WHERE Dnumber = ?";
                    PreparedStatement delete_stmt = conn.prepareStatement(delete_query);
                    delete_stmt.setInt(1, Dnumber);
                    delete_stmt.executeUpdate();
                } else {
                    System.out.println("Deletion cancelled");
                    conn.rollback();
                    return;
                }
            } else {
                System.out.println("Department not found");
                conn.rollback();
                return;
            }

            conn.commit();
            System.out.println("Department deleted successfully");


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