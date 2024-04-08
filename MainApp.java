import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.sql.ResultSet;
import java.sql.SQLException;
//Main application class inheriting from JFrame
public class MainApp extends JFrame {

    private DefaultListModel<String> contactListModel;// Model to hold contact list data
    private JList<String> contactList;// List component to display contacts
    private JTextField lastNameField, firstNameField, addressField, phoneNumberField, emailField;// Text fields for contact details


    private JAVA_Derby_DbConnection dbConnection; // Database connection instance

    public MainApp() {
        setTitle("Contact Management");// Set window title
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);// Set default close operation
        setSize(600, 400);// Set initial window size
        setLayout(new BorderLayout());// Set layout manager for main frame

        // Initialize database connection
        String jdbcURL = "jdbc:derby:Contacts_db;create=true";
        dbConnection = new JAVA_Derby_DbConnection(jdbcURL, "admin", "admin");


        // Initialize GUI components
        initGUI();

        // Populate initial contact list
        loadContacts();

        // Add selection listener to update details panel
        contactList.addListSelectionListener(e -> displayContactDetails());

        addWindowListener((WindowListener) new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                // Close the database connection when the window is closing
                dbConnection.close();
            }
        });
        
        setVisible(true);// Setting the main frame visible
    }



 // Method to initialize the graphical user interface components
    private void initGUI() {
        // Initialize components
        contactListModel = new DefaultListModel<>();
        contactList = new JList<>(contactListModel);
        JScrollPane contactScrollPane = new JScrollPane(contactList);

        // Set preferred width of the contactScrollPane
        contactScrollPane.setPreferredSize(new Dimension(200, getHeight())); // Adjust width as needed

        // Add the contactScrollPane to BorderLayout.WEST with adjusted width
        add(contactScrollPane, BorderLayout.WEST);
        // Create a panel for contact information using GridBagLayout
        JPanel infoPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = new Insets(5, 5, 5, 5); // Padding around components

        // First Name Label and Field
        gbc.gridx = 0;
        gbc.gridy = 0;
        infoPanel.add(new JLabel("Last Name:"), gbc);

        gbc.gridx = 1;
        gbc.gridy = 0;
        lastNameField = new JTextField(20); // Default width
        infoPanel.add(lastNameField, gbc);

        // Last Name Label and Field
        gbc.gridx = 0;
        gbc.gridy = 1;
        infoPanel.add(new JLabel("First Name:"), gbc);

        gbc.gridx = 1;
        gbc.gridy = 1;
        firstNameField = new JTextField(lastNameField.getColumns()); // Same width as Last Name field
        infoPanel.add(firstNameField, gbc);

        // Address Label and Field
        gbc.gridx = 0;
        gbc.gridy = 2;
        infoPanel.add(new JLabel("Company:"), gbc);

        gbc.gridx = 1;
        gbc.gridy = 2;
        addressField = new JTextField(lastNameField.getColumns()); // Same width as Last Name field
        infoPanel.add(addressField, gbc);

        // Phone Number Label and Field
        gbc.gridx = 0;
        gbc.gridy = 3;
        infoPanel.add(new JLabel("Phone Number:"), gbc);

        gbc.gridx = 1;
        gbc.gridy = 3;
        phoneNumberField = new JTextField(lastNameField.getColumns()); // Same width as Last Name field
        infoPanel.add(phoneNumberField, gbc);

        // Email Label and Field
        gbc.gridx = 0;
        gbc.gridy = 4;
        infoPanel.add(new JLabel("Email:"), gbc);

        gbc.gridx = 1;
        gbc.gridy = 4;
        emailField = new JTextField(lastNameField.getColumns()); // Same width as Last Name field
        infoPanel.add(emailField, gbc);

        add(infoPanel, BorderLayout.CENTER);
     // Creating buttons and adding them to a panel
        JButton addButton = new JButton("Add");
        JButton deleteButton = new JButton("Delete");
        JButton updateButton = new JButton("Update");
        JButton findButton = new JButton("Find");
        JButton previousButton = new JButton("Previous");
        JButton nextButton = new JButton("Next");
       //adding them to a panel
     
        JPanel buttonPanel = new JPanel();
        buttonPanel.add(addButton);
        buttonPanel.add(deleteButton);
        buttonPanel.add(updateButton);
        buttonPanel.add(findButton);
        buttonPanel.add(previousButton);
        buttonPanel.add(nextButton);
        add(buttonPanel, BorderLayout.SOUTH);

        // Add action listeners to buttons
        addButton.addActionListener(e -> addContact());
        deleteButton.addActionListener(e -> deleteContact());
        updateButton.addActionListener(e -> updateContact());
        findButton.addActionListener(e -> findContacts());
    
        // Add action listeners to navigate through contacts
        previousButton.addActionListener(e -> navigatePrevious());
        nextButton.addActionListener(e -> navigateNext());
    }
    // Method to navigate to the previous contact in the list
    private void navigatePrevious() {
        int selectedIndex = contactList.getSelectedIndex();
        if (selectedIndex > 0) {
            contactList.setSelectedIndex(selectedIndex - 1);
            contactList.ensureIndexIsVisible(selectedIndex - 1);
        } else if (selectedIndex == 0) {
        	// Wrap around to the end of the list
            contactList.setSelectedIndex(contactListModel.size() - 1); 
            contactList.ensureIndexIsVisible(contactListModel.size() - 1);
        }
    }
    // Method to navigate to the next contact in the list
    private void navigateNext() {
        int selectedIndex = contactList.getSelectedIndex();
        if (selectedIndex < contactListModel.size() - 1) {
            contactList.setSelectedIndex(selectedIndex + 1);
            contactList.ensureIndexIsVisible(selectedIndex + 1);
        } else if (selectedIndex == contactListModel.size() - 1) {
        	// Wrap around to the beginning of the list
        	contactList.setSelectedIndex(0); 
            contactList.ensureIndexIsVisible(0);
        }
    }
 // Wrap around to the beginning of the list
    private void loadContacts() {
        try {
            ResultSet resultSet = dbConnection.executeQuery("SELECT last_name, first_name FROM Contact");

            contactListModel.clear();
            while (resultSet.next()) {
                String lastName = resultSet.getString("last_name");
                String firstName = resultSet.getString("first_name");
                contactListModel.addElement(lastName + ", " + firstName);
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error loading contacts.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
 // Method to display selected contact details in the infoPanel
    private void displayContactDetails() {
    	// Retrieve selected contact from the list
        String selectedContact = contactList.getSelectedValue();
        if (selectedContact != null) {
            String[] nameParts = selectedContact.split(", ");
            String lastName = nameParts[0];
            String firstName = nameParts[1];

            try {
            	 // Fetch contact details from the database
                ResultSet resultSet = dbConnection.executeQueryWithParams(
                        "SELECT * FROM Contact WHERE last_name = ? AND first_name = ?", lastName, firstName);

                if (resultSet.next()) {
                	 // Display contact details in corresponding text fields
                    lastNameField.setText(resultSet.getString("last_name"));
                    firstNameField.setText(resultSet.getString("first_name"));
                    addressField.setText(resultSet.getString("address"));
                    phoneNumberField.setText(resultSet.getString("phone_number"));
                    emailField.setText(resultSet.getString("email"));
                }
            } catch (SQLException ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(this, "Error displaying contact details.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    // Method to add a new contact to the database
    private void addContact() {
    	
    	// Retrieve contact details from input fields
        String lastName = lastNameField.getText().trim();
        String firstName = firstNameField.getText().trim();
        String address = addressField.getText().trim();
        String phoneNumber = phoneNumberField.getText().trim();
        String email = emailField.getText().trim();

     // Validate required fields
        if (lastName.isEmpty() || firstName.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please provide both Last Name and First Name.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        // Insert new contact into the database
        String insertQuery = "INSERT INTO Contact (last_name, first_name, address, phone_number, email) VALUES (?, ?, ?, ?, ?)";
        try {
            dbConnection.executeUpdateWithParams(insertQuery, lastName, firstName, address, phoneNumber, email);
            JOptionPane.showMessageDialog(this, "Contact added successfully.", "Success", JOptionPane.INFORMATION_MESSAGE);
            loadContacts(); // Refresh contact list
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error adding contact.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    // Method to delete the selected contact from the database
    private void deleteContact() {
        String selectedContact = contactList.getSelectedValue();
        if (selectedContact != null) {
            String[] nameParts = selectedContact.split(", ");
            String lastName = nameParts[0];
            String firstName = nameParts[1];

            String deleteQuery = "DELETE FROM Contact WHERE last_name = ? AND first_name = ?";
            try {
                dbConnection.executeUpdateWithParams(deleteQuery, lastName, firstName);
                JOptionPane.showMessageDialog(this, "Contact deleted successfully.", "Success", JOptionPane.INFORMATION_MESSAGE);
                loadContacts(); // Refresh contact list
            } catch (SQLException ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(this, "Error deleting contact.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    // Method to update the selected contact in the database
    private void updateContact() {
    	 // Retrieve contact details from input fields
        String lastName = lastNameField.getText().trim();
        String firstName = firstNameField.getText().trim();
        String address = addressField.getText().trim();
        String phoneNumber = phoneNumberField.getText().trim();
        String email = emailField.getText().trim();
        // Validate required fields
        if (lastName.isEmpty() || firstName.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please provide both Last Name and First Name.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
     // Update contact details in the database
        String updateQuery = "UPDATE Contact SET address = ?, phone_number = ?, email = ? WHERE last_name = ? AND first_name = ?";
        try {
            int rowsUpdated = dbConnection.executeUpdateWithParams(updateQuery, address, phoneNumber, email, lastName, firstName);
            if (rowsUpdated > 0) {
                JOptionPane.showMessageDialog(this, "Contact updated successfully.", "Success", JOptionPane.INFORMATION_MESSAGE);
                loadContacts(); // Refresh contact list
            } else {
                JOptionPane.showMessageDialog(this, "Contact not found.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error updating contact.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
 // Method to find contacts by last name in the database
    private void findContacts() {
    	// Retrieve search criteria
        String searchLastName = lastNameField.getText().trim();
        String searchQuery = "SELECT last_name, first_name FROM Contact WHERE last_name = ?";

        try {
        	// Execute query with search parameters
            ResultSet resultSet = dbConnection.executeQueryWithParams(searchQuery, searchLastName);
         // Clear existing list and populate with search results
            contactListModel.clear();
            while (resultSet.next()) {
                String lastName = resultSet.getString("last_name");
                String firstName = resultSet.getString("first_name");
                contactListModel.addElement(lastName + ", " + firstName);
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error finding contacts.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    // Main method to start the application
    public static void main(String[] args) {
    	
    	// Create and display the main frame and creating instance 
        SwingUtilities.invokeLater(MainApp::new);
    }
}
