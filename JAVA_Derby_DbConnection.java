import java.sql.*;

public class JAVA_Derby_DbConnection {
    // Connection object to hold the database connection
    private Connection connection = null;

    /**
     * Constructor for initializing the database connection.
     * @param dbPath Path to the Derby database
     * @param userName Database username
     * @param password Database password
     */
    public JAVA_Derby_DbConnection(String dbPath, String userName, String password) {
        try {
            // Register the Derby JDBC driver
            Class.forName("org.apache.derby.jdbc.EmbeddedDriver");

            // Create connection to the database using provided credentials
            connection = DriverManager.getConnection(dbPath, userName, password);
            System.out.println("Database connection established...");

            // Check if the Contact table exists; if not, create it
            if (!tableExists("Contact")) {
                createContactTable();
            }
        } catch (ClassNotFoundException | SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * Method to close the database connection.
     */
    public void close() {
        if (connection != null) {
            try {
                connection.close();
                System.out.println("Database connection closed...");
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Method to execute a query and return a ResultSet.
     * @param sql SQL query to execute
     * @return ResultSet containing the query results
     * @throws SQLException if a database access error occurs
     */
    public ResultSet executeQuery(String sql) throws SQLException {
        // Create a Statement object to execute the query
        Statement statement = connection.createStatement();
        // Execute the query and return the ResultSet
        return statement.executeQuery(sql);
    }

    /**
     * Method to execute a parameterized query and return a ResultSet.
     * @param sql SQL query with placeholders for parameters
     * @param params Values to replace the placeholders in the query
     * @return ResultSet containing the query results
     * @throws SQLException if a database access error occurs
     */
    public ResultSet executeQueryWithParams(String sql, String... params) throws SQLException {
        // Create a PreparedStatement with the parameterized query
        PreparedStatement preparedStatement = connection.prepareStatement(sql);
        // Set parameter values in the PreparedStatement
        for (int i = 0; i < params.length; i++) {
            preparedStatement.setString(i + 1, params[i]);
        }
        // Execute the parameterized query and return the ResultSet
        return preparedStatement.executeQuery();
    }

    /**
     * Method to execute an update (insert, update, delete) query.
     * @param sql SQL update query to execute
     * @return Number of rows affected by the query
     * @throws SQLException if a database access error occurs
     */
    public int executeUpdate(String sql) throws SQLException {
        // Create a Statement object to execute the update query
        Statement statement = connection.createStatement();
        // Execute the update query and return the count of affected rows
        return statement.executeUpdate(sql);
    }

    /**
     * Method to execute a parameterized update query.
     * @param sql SQL update query with placeholders for parameters
     * @param params Values to replace the placeholders in the query
     * @return Number of rows affected by the query
     * @throws SQLException if a database access error occurs
     */
    public int executeUpdateWithParams(String sql, String... params) throws SQLException {
        // Create a PreparedStatement with the parameterized update query
        PreparedStatement preparedStatement = connection.prepareStatement(sql);
        // Set parameter values in the PreparedStatement
        for (int i = 0; i < params.length; i++) {
            preparedStatement.setString(i + 1, params[i]);
        }
        // Execute the parameterized update query and return the count of affected rows
        return preparedStatement.executeUpdate();
    }

    /**
     * Helper method to check if a table exists in the database.
     * @param tableName Name of the table to check
     * @return true if the table exists; false otherwise
     * @throws SQLException if a database access error occurs
     */
    private boolean tableExists(String tableName) throws SQLException {
        // Get metadata about the database
        DatabaseMetaData meta = connection.getMetaData();
        // Retrieve information about tables with the specified name
        ResultSet resultSet = meta.getTables(null, null, tableName.toUpperCase(), null);
        // Check if a table with the specified name exists
        return resultSet.next();
    }

    /**
     * Helper method to create the Contact table if it doesn't exist.
     * @throws SQLException if a database access error occurs
     */
    private void createContactTable() throws SQLException {
        // SQL statement to create the Contact table
        String createTableSQL = "CREATE TABLE Contact ("
                + "id INT PRIMARY KEY GENERATED ALWAYS AS IDENTITY,"
                + "last_name VARCHAR(255) NOT NULL,"
                + "first_name VARCHAR(255) NOT NULL,"
                + "address VARCHAR(255),"
                + "phone_number VARCHAR(20),"
                + "email VARCHAR(255)"
                + ")";
        
        // Execute the SQL statement to create the Contact table
        executeUpdate(createTableSQL);
        System.out.println("Contact table created.");
    }
}
