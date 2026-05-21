import java.sql.*;

public class JDBCAll {

    // JDBC driver name
    static final String JDBC_DRIVER = "com.mysql.cj.jdbc.Driver";

    // Database URL
    static final String DB_URL = "jdbc:mysql://localhost:3306/companydb";

    // Database credentials
    static final String USER = "javauser";
    static final String PASSWORD = "java123";

    public static void main(String[] args) {

        // Connection object
        Connection conn = null;

        // Statement object
        Statement stmt = null;

        try {

            // STEP 1: Register JDBC Driver
            Class.forName(JDBC_DRIVER);

            // STEP 2: Open a connection
            System.out.println("Connecting to database...");
            conn = DriverManager.getConnection(DB_URL, USER, PASSWORD);

            // STEP 3: Create statement object
            stmt = conn.createStatement();

            // STEP 4: Execute SELECT query using executeQuery()
            String query = "SELECT fname, lname, salary, dno FROM employee";

            // ResultSet stores the returned table data
            ResultSet rs = stmt.executeQuery(query);

            System.out.println("All Employees");

            // STEP 5: Process ResultSet
            while (rs.next()) {

                // Retrieve data using column names
                String fname = rs.getString("fname");
                String lname = rs.getString("lname");
                double salary = rs.getDouble("salary");
                int dno = rs.getInt("dno");

                // Display employee details
                System.out.println(
                        fname + " " + lname +
                        " | Dept: " + dno +
                        " | Salary: " + salary
                );
            }

            // Close ResultSet
            rs.close();

            // executeUpdate() is used for UPDATE / INSERT / DELETE
            int rows = stmt.executeUpdate(
                    "UPDATE employee SET salary = salary + 5000 WHERE dno = 1"
            );

            // Number of rows affected
            System.out.println("Rows updated: " + rows);

            // PreparedStatement is used for dynamic queries
            // ? are placeholders
            PreparedStatement ps = conn.prepareStatement(
                    "SELECT fname, salary FROM employee WHERE dno = ? AND salary > ?"
            );

            // Set values for placeholders
            ps.setInt(1, 5);
            ps.setDouble(2, 30000.0);

            // Execute prepared query
            ResultSet rs2 = ps.executeQuery();

            System.out.println("Filtered Employees");

            // Read filtered result
            while (rs2.next()) {

                System.out.println(
                        rs2.getString("fname") +
                        " : " +
                        rs2.getDouble("salary")
                );
            }

            // Close resources
            rs2.close();
            ps.close();

            // INSERT query using PreparedStatement
            PreparedStatement ps2 = conn.prepareStatement(
                    "INSERT INTO employee(fname, lname, ssn, salary, dno) VALUES(?, ?, ?, ?, ?)"
            );

            // Set values for insertion
            ps2.setString(1, "Test");
            ps2.setString(2, "User");
            ps2.setString(3, "123456789");
            ps2.setDouble(4, 40000.0);
            ps2.setInt(5, 1);

            // Execute INSERT
            int inserted = ps2.executeUpdate();

            System.out.println("Inserted: " + inserted);

            // Close PreparedStatement
            ps2.close();

            // TRANSACTION MANAGEMENT

            // Disable auto commit
            conn.setAutoCommit(false);

            try {

                // First query
                stmt.executeUpdate(
                        "UPDATE employee SET salary = salary - 1000 WHERE ssn = '123456789'"
                );

                // Second query
                stmt.executeUpdate(
                        "UPDATE employee SET dno = 2 WHERE ssn = '123456789'"
                );

                // Save both changes permanently
                conn.commit();

                System.out.println("Transaction committed.");

            } catch (SQLException se) {

                // Undo changes if any query fails
                conn.rollback();

                System.out.println("Transaction rolled back.");

                se.printStackTrace();

            } finally {

                // Restore default auto commit mode
                conn.setAutoCommit(true);
            }

            // Close statement
            stmt.close();

            // Close connection
            conn.close();

        } catch (SQLException se) {

            // Handle JDBC errors
            se.printStackTrace();

        } catch (Exception e) {

            // Handle Class.forName errors
            e.printStackTrace();

        } finally {

            // finally block always executes

            try {

                // Close statement if not already closed
                if (stmt != null)
                    stmt.close();

            } catch (SQLException se) {

                se.printStackTrace();
            }

            try {

                // Close connection if not already closed
                if (conn != null)
                    conn.close();

            } catch (SQLException se) {

                se.printStackTrace();
            }
        }

        System.out.println("End of program.");
    }
}