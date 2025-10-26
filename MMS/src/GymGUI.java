package src;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDate;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.RowFilter;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;

/**
 * The Graphical User Interface (GUI) for the Member Management System.
 * This class provides a visual, window-based interface for all
 * GymManager operations.
 */
public class GymGUI extends JFrame {

    private GymManager manager;
    private JTable memberTable;
    private DefaultTableModel tableModel;
    private TableRowSorter<DefaultTableModel> sorter;

    private static final String[] COLUMN_NAMES = {
            "ID", "Name", "Type", "Join Date", "Status", "Monthly Fee ($)", "Details"
    };

    /**
     * Constructor for the GUI.
     * @param manager An instance of GymManager, shared from the AppLauncher.
     */
    public GymGUI(GymManager manager) {
        this.manager = manager;

        // Load default data
        this.manager.loadFromFile("gym_records.csv");

        // Set up the main window
        setTitle("Gym Member Management System");
        setSize(1000, 700);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null); // Center the window

        // Create the main table view
        initTable();

        // Create panels for buttons and controls
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.add(new JScrollPane(memberTable), BorderLayout.CENTER);
        mainPanel.add(createControlPanel(), BorderLayout.SOUTH);
        mainPanel.add(createSearchPanel(), BorderLayout.NORTH);

        add(mainPanel);
        setVisible(true);
        refreshTable(); // Initial data load
    }

    /**
     * Initializes the JTable and its model.
     */
    private void initTable() {
        tableModel = new DefaultTableModel(COLUMN_NAMES, 0) {
            // Make cells non-editable
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        memberTable = new JTable(tableModel);

        // Set up the sorter for the table
        sorter = new TableRowSorter<>(tableModel);
        memberTable.setRowSorter(sorter);
    }

    /**
    * Creates the top panel containing search and sort controls.
    */
    private JPanel createSearchPanel() {
        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        searchPanel.setBorder(BorderFactory.createTitledBorder("Find / Sort Members"));

        JTextField searchField = new JTextField(20);
        JButton searchButton = new JButton("Search");

        // Search logic
        searchButton.addActionListener((ActionEvent e) -> {
            String text = searchField.getText();
            if (text.trim().length() == 0) {
                sorter.setRowFilter(null);
            } else {
                // Case-insensitive search
                sorter.setRowFilter(RowFilter.regexFilter("(?i)" + text));
            }
        });

        JComboBox<String> sortComboBox = new JComboBox<>(new String[]{
            "Sort by ID", "Sort by Name", "Sort by Join Date"
        });
        
        // Sort logic
        sortComboBox.addActionListener((ActionEvent e) -> {
            String selection = (String) sortComboBox.getSelectedItem();
            if ("Sort by ID".equals(selection)) {
                manager.sortMembersById();
            } else if ("Sort by Name".equals(selection)) {
                manager.sortMembersByName();
            } else if ("Sort by Join Date".equals(selection)) {
                manager.sortMembersByJoinDate();
            }
            refreshTable();
        });

        searchPanel.add(new JLabel("Filter:"));
        searchPanel.add(searchField);
        searchPanel.add(searchButton);
        searchPanel.add(new JLabel("  |  Sort by:"));
        searchPanel.add(sortComboBox);

        return searchPanel;
    }

    /**
     * Creates the bottom panel containing the main action buttons.
     */
    private JPanel createControlPanel() {
        JPanel controlPanel = new JPanel(new FlowLayout());
        
        JButton addButton = new JButton("Add Member");
        addButton.addActionListener(e -> handleAddMember());
        
        JButton deleteButton = new JButton("Delete Selected");
        deleteButton.addActionListener(e -> handleDeleteMember());
        
        JButton statusButton = new JButton("Update Status");
        statusButton.addActionListener(e -> handleUpdateStatus());
        
        JButton perfButton = new JButton("Add Performance");
        perfButton.addActionListener(e -> handleAddPerformance());
        
        JButton saveButton = new JButton("Save to File");
        saveButton.addActionListener(e -> handleSaveToFile());

        JButton exitButton = new JButton("Exit and Save");
        exitButton.addActionListener(e -> handleExit());

        controlPanel.add(addButton);
        controlPanel.add(deleteButton);
        controlPanel.add(statusButton);
        controlPanel.add(perfButton);
        controlPanel.add(saveButton);
        controlPanel.add(exitButton);

        return controlPanel;
    }

    /**
     * Clears and repopulates the table with fresh data from the GymManager.
     */
    private void refreshTable() {
        tableModel.setRowCount(0); // Clear existing data
        List<Member> members = manager.getAllMembers();
        for (Member member : members) {
            String type = (member instanceof PremiumMember) ? "Premium" : "Regular";
            String details = (member instanceof PremiumMember)
                    ? "PT Fee: $" + ((PremiumMember) member).getPersonalTrainerFee()
                    : "-";
            
            tableModel.addRow(new Object[]{
                    member.getMemberId(),
                    member.getFullName(),
                    type,
                    member.getJoinDate(),
                    member.getStatus(),
                    String.format("%.2f", member.calculateMonthlyFee()),
                    details
            });
        }
    }

    /**
     * Gets the Member ID from the currently selected table row.
     * @return The Member ID string, or null if no row is selected.
     */
    private String getSelectedMemberId() {
        int selectedViewRow = memberTable.getSelectedRow();
        if (selectedViewRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select a member from the table first.", "No Member Selected", JOptionPane.WARNING_MESSAGE);
            return null;
        }
        // Convert view row index to model row index (in case of sorting)
        int modelRow = memberTable.convertRowIndexToModel(selectedViewRow);
        return (String) tableModel.getValueAt(modelRow, 0); // 0 is the ID column
    }

    /**
     * Handles the logic for adding a new member via a dialog box.
     */
    private void handleAddMember() {
        // Create text fields
        JTextField idField = new JTextField();
        JTextField nameField = new JTextField();
        JTextField feeField = new JTextField("0.0"); // For trainer fee

        // Create combo box for member type
        JComboBox<String> typeComboBox = new JComboBox<>(new String[]{"Regular", "Premium"});
        
        // When type is "Regular", disable the fee field
        typeComboBox.addActionListener(e -> {
            boolean isPremium = "Premium".equals(typeComboBox.getSelectedItem());
            feeField.setEnabled(isPremium);
        });
        typeComboBox.setSelectedItem("Regular"); // Default
        feeField.setEnabled(false);

        // Layout for the dialog
        JPanel panel = new JPanel(new GridLayout(4, 2, 10, 10));
        panel.setBorder(new EmptyBorder(10, 10, 10, 10));
        panel.add(new JLabel("Member ID:"));
        panel.add(idField);
        panel.add(new JLabel("Full Name:"));
        panel.add(nameField);
        panel.add(new JLabel("Member Type:"));
        panel.add(typeComboBox);
        panel.add(new JLabel("Personal Trainer Fee:"));
        panel.add(feeField);

        int result = JOptionPane.showConfirmDialog(this, panel, "Add New Member",
                JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

        if (result == JOptionPane.OK_OPTION) {
            try {
                String id = idField.getText();
                String name = nameField.getText();
                String type = (String) typeComboBox.getSelectedItem();

                if (id.isEmpty() || name.isEmpty()) {
                    throw new IllegalArgumentException("ID and Name cannot be empty.");
                }
                if (manager.findMemberById(id) != null) {
                    throw new IllegalArgumentException("A member with this ID already exists.");
                }

                Member member;
                if ("Regular".equals(type)) {
                    member = new RegularMember(id, name, LocalDate.now());
                } else {
                    double fee = Double.parseDouble(feeField.getText());
                    member = new PremiumMember(id, name, LocalDate.now(), fee);
                }
                manager.addMember(member);
                refreshTable();
                JOptionPane.showMessageDialog(this, "Member added successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);

            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Invalid fee format. Please enter a number.", "Input Error", JOptionPane.ERROR_MESSAGE);
            } catch (IllegalArgumentException ex) {
                JOptionPane.showMessageDialog(this, ex.getMessage(), "Input Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    /**
     * Handles deleting the selected member from the table.
     */
    private void handleDeleteMember() {
        String id = getSelectedMemberId();
        if (id == null) return;

        Member member = manager.findMemberById(id);
        int confirm = JOptionPane.showConfirmDialog(this,
                "Are you sure you want to delete " + member.getFullName() + "?",
                "Confirm Deletion", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);

        if (confirm == JOptionPane.YES_OPTION) {
            manager.deleteMember(id);
            refreshTable();
            JOptionPane.showMessageDialog(this, "Member deleted successfully.", "Success", JOptionPane.INFORMATION_MESSAGE);
        }
    }

    /**
     * Handles updating the status of the selected member.
     */
    private void handleUpdateStatus() {
        String id = getSelectedMemberId();
        if (id == null) return;

        Member member = manager.findMemberById(id);
        JComboBox<MembershipStatus> statusBox = new JComboBox<>(MembershipStatus.values());
        statusBox.setSelectedItem(member.getStatus());

        int result = JOptionPane.showConfirmDialog(this, statusBox, "Update Status for " + member.getFullName(),
                JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

        if (result == JOptionPane.OK_OPTION) {
            member.setStatus((MembershipStatus) statusBox.getSelectedItem());
            refreshTable();
            JOptionPane.showMessageDialog(this, "Status updated.", "Success", JOptionPane.INFORMATION_MESSAGE);
        }
    }

    /**
     * Handles adding a performance record to the selected member.
     */
    private void handleAddPerformance() {
        String id = getSelectedMemberId();
        if (id == null) return;
        Member member = manager.findMemberById(id);

        JTextField monthField = new JTextField(String.valueOf(LocalDate.now().getMonthValue()));
        JTextField yearField = new JTextField(String.valueOf(LocalDate.now().getYear()));
        JComboBox<String> achievedBox = new JComboBox<>(new String[]{"true", "false"});

        JPanel panel = new JPanel(new GridLayout(3, 2, 10, 10));
        panel.setBorder(new EmptyBorder(10, 10, 10, 10));
        panel.add(new JLabel("Month (1-12):"));
        panel.add(monthField);
        panel.add(new JLabel("Year:"));
        panel.add(yearField);
        panel.add(new JLabel("Goal Achieved:"));
        panel.add(achievedBox);

        int result = JOptionPane.showConfirmDialog(this, panel, "Add Performance for " + member.getFullName(),
                JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
        
        if (result == JOptionPane.OK_OPTION) {
            try {
                int month = Integer.parseInt(monthField.getText());
                int year = Integer.parseInt(yearField.getText());
                boolean achieved = Boolean.parseBoolean((String) achievedBox.getSelectedItem());
                
                member.addPerformanceRecord(new Performance(month, year, achieved));
                refreshTable(); // Refresh to show potential fee discount
                JOptionPane.showMessageDialog(this, "Performance record added.", "Success", JOptionPane.INFORMATION_MESSAGE);
                
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Invalid month or year.", "Input Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    /**
     * Handles saving the entire member list to a new CSV file.
     * This is the method with the corrected logic.
     */
    private void handleSaveToFile() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Save Member Data As");
        fileChooser.setSelectedFile(new File("members_backup.csv"));

        int userSelection = fileChooser.showSaveDialog(this);
        if (userSelection == JFileChooser.APPROVE_OPTION) {
            String filename = fileChooser.getSelectedFile().getAbsolutePath();
            try (BufferedWriter writer = new BufferedWriter(new FileWriter(filename))) {
                
                // --- THIS IS THE FIX ---
                // Write column headers by iterating through the table model
                StringBuilder header = new StringBuilder();
                for (int i = 0; i < tableModel.getColumnCount(); i++) {
                    header.append("\"").append(tableModel.getColumnName(i)).append("\"");
                    if (i < tableModel.getColumnCount() - 1) {
                        header.append(",");
                    }
                }
                writer.write(header.toString());
                writer.newLine();

                // Write data rows from the table model
                for (int i = 0; i < tableModel.getRowCount(); i++) {
                    StringBuilder row = new StringBuilder();
                    for (int j = 0; j < tableModel.getColumnCount(); j++) {
                        Object value = tableModel.getValueAt(i, j);
                        row.append("\"").append(value != null ? value.toString() : "").append("\"");
                        if (j < tableModel.getColumnCount() - 1) {
                            row.append(",");
                        }
                    }
                    writer.newLine();
                    writer.write(row.toString());
                }
                
                JOptionPane.showMessageDialog(this, "Data successfully saved to " + filename, "Save Successful", JOptionPane.INFORMATION_MESSAGE);

            } catch (IOException ex) {
                JOptionPane.showMessageDialog(this, "Error saving file: " + ex.getMessage(), "Save Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    /**
     * Handles saving the data to the default file and exiting the application.
     */
    private void handleExit() {
        manager.saveToFile("gym_records.csv");
        JOptionPane.showMessageDialog(this, "Data saved to gym_records.csv. Exiting.", "Exit", JOptionPane.INFORMATION_MESSAGE);
        dispose(); // Close the window
        System.exit(0); // Terminate the application
    }
}

