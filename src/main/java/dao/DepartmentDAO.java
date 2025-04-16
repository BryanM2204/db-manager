package dao;

import model.Department;

import java.sql.*;

public class DepartmentDAO {
    private final Connection conn;

    public DepartmentDAO(Connection conn) {
        this.conn = conn;
    }

    public void get(int Dept_number) {
        String query = "SELECT D.Dname, D.Dnumber, D.Mgr_ssn, D.Mgr_start_date, S.Fname AS Supervisor_Fname, S.Minit as Supervisor_Minit, S.Lname AS" +
                " Supervisor_Lname, L.Dlocation FROM Department D LEFT JOIN Employee S ON D.Mgr_ssn = S.Ssn LEFT JOIN Dept_locations L ON D.Dnumber = L.Dnumber WHERE D.Dnumber = ?";
        try(PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, Dept_number);
            ResultSet rs = stmt.executeQuery();
            while(rs.next()) {
                String Dname = rs.getString("Dname");
                int Dnumber = rs.getInt("Dnumber");
                String Mgr_ssn = rs.getString("Mgr_ssn");
                Date Mgr_start_date = rs.getDate("Mgr_start_date");
                String Supervisor_Fname = rs.getString("Supervisor_Fname");
                String Supervisor_Minit = rs.getString("Supervisor_Minit");
                String Supervisor_Lname = rs.getString("Supervisor_Lname");
                String Dlocation = rs.getString("Dlocation");
                System.out.println(Dname + ", " + Dnumber + ", " + Mgr_ssn + ", " + Mgr_start_date + ", " + Supervisor_Fname + ", " + Supervisor_Minit + ", " + Supervisor_Lname + ", " + Dlocation);

            }
        } catch (SQLException e) {
            System.out.println("Failed to get department: " + e.getMessage());
        }
    }

    public void add(Department department) {
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
            System.out.println("Autocommit disabled");
            conn.setAutoCommit(false);

            System.out.println("\nAttempting to lock department record");
            String query = "SELECT * FROM Department WHERE Dnumber = ? FOR UPDATE";
            PreparedStatement stmt = conn.prepareStatement(query);
            stmt.setInt(1, Dnumber);
            ResultSet rs = stmt.executeQuery();
            System.out.println("Lock acquired");

            if(rs.next()) {
                System.out.println("Department information: ");
                System.out.println(rs.getString("Dname") + ", " + rs.getString("Dnumber") + ", "
                + rs.getString("Mgr_ssn") + ", " + rs.getString("Mgr_start_date"));

                boolean hasDependencies = false;
                String query2 = "SELECT * FROM Dept_locations WHERE Dnumber = ?";
                PreparedStatement stmt2 = conn.prepareStatement(query2);
                stmt2.setInt(1, Dnumber);
                ResultSet rs2 = stmt2.executeQuery();
                if(rs2.next()) {
                    hasDependencies = true;
                }

                String query3 = "SELECT * FROM Employee WHERE Dno = ?";
                PreparedStatement stmt3 = conn.prepareStatement(query3);
                stmt3.setInt(1, Dnumber);
                ResultSet rs3 = stmt3.executeQuery();
                if(rs3.next()) {
                    hasDependencies = true;
                }

                String query4 = "SELECT * FROM Project WHERE Dnum = ?";
                PreparedStatement stmt4 = conn.prepareStatement(query4);
                stmt4.setInt(1, Dnumber);
                ResultSet rs4 = stmt4.executeQuery();
                if(rs4.next()) {
                    hasDependencies = true;
                }

                if(hasDependencies) {
                    System.out.println("Cannot delete department with dependencies\nDeletion cancelled");
                    conn.rollback();
                    return;
                }

                System.out.println("Comfirm deletion? Yes or no?");
                String answer = System.console().readLine();

                if(answer.equalsIgnoreCase("Yes")) {
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
                System.out.println("Autocommit enabled");
            } catch (SQLException e) {
                System.out.println("Failed to enable autocommit: " + e.getMessage());
            }
        }
    }
}