
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.*;

public class StudentGUI extends JFrame {

    JTextField idField, nameField, deptField, ageField, emailField;
    JTable table;
    DefaultTableModel model;
    Connection con;

    public StudentGUI() {
        setTitle("Student Management System");
        setSize(950, 650);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        // MySQL Connection
        try {
            con = DriverManager.getConnection(
                "jdbc:mysql://localhost:3306/student_db",
                "root",
                "your_password"
            );
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this,
                "Database Connection Failed!\n" + e.getMessage());
        }

        // Heading
        JLabel heading = new JLabel(
            "STUDENT MANAGEMENT SYSTEM", JLabel.CENTER);
        heading.setFont(new Font("Arial", Font.BOLD, 26));
        heading.setBorder(BorderFactory.createEmptyBorder(15, 5, 15, 5));
        add(heading, BorderLayout.NORTH);

        // Input Form
        JPanel form = new JPanel(new GridLayout(5, 2, 10, 10));
        form.setBorder(BorderFactory.createEmptyBorder(15, 25, 15, 25));

        idField = new JTextField();
        nameField = new JTextField();
        deptField = new JTextField();
        ageField = new JTextField();
        emailField = new JTextField();

        form.add(new JLabel("Student ID:"));
        form.add(idField);

        form.add(new JLabel("Student Name:"));
        form.add(nameField);

        form.add(new JLabel("Department:"));
        form.add(deptField);

        form.add(new JLabel("Age:"));
        form.add(ageField);

        form.add(new JLabel("Email:"));
        form.add(emailField);

        // Buttons
        JButton addBtn = new JButton("Add Student");
        JButton viewBtn = new JButton("View Students");
        JButton searchBtn = new JButton("Search");
        JButton updateBtn = new JButton("Update");
        JButton deleteBtn = new JButton("Delete");
        JButton clearBtn = new JButton("Clear");

        JPanel buttons = new JPanel(new FlowLayout());
        buttons.add(addBtn);
        buttons.add(viewBtn);
        buttons.add(searchBtn);
        buttons.add(updateBtn);
        buttons.add(deleteBtn);
        buttons.add(clearBtn);

        // Top Panel
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.add(form, BorderLayout.CENTER);
        topPanel.add(buttons, BorderLayout.SOUTH);

        add(topPanel, BorderLayout.CENTER);

        // Student Table
        model = new DefaultTableModel(
            new String[]{"ID", "Name", "Department", "Age", "Email"}, 0
        ) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        table = new JTable(model);
        table.setRowHeight(25);

        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBorder(BorderFactory.createTitledBorder(
            "Student Records"));

        add(scrollPane, BorderLayout.SOUTH);
        scrollPane.setPreferredSize(new Dimension(900, 250));

        // Button Actions
        addBtn.addActionListener(e -> addStudent());
        viewBtn.addActionListener(e -> viewStudents());
        searchBtn.addActionListener(e -> searchStudent());
        updateBtn.addActionListener(e -> updateStudent());
        deleteBtn.addActionListener(e -> deleteStudent());
        clearBtn.addActionListener(e -> clearFields());

        // Select table row to fill fields
        table.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting() &&
                table.getSelectedRow() != -1) {

                int r = table.getSelectedRow();

                idField.setText(model.getValueAt(r, 0).toString());
                nameField.setText(model.getValueAt(r, 1).toString());
                deptField.setText(String.valueOf(
                    model.getValueAt(r, 2)));
                ageField.setText(String.valueOf(
                    model.getValueAt(r, 3)));
                emailField.setText(String.valueOf(
                    model.getValueAt(r, 4)));
            }
        });

        setVisible(true);
        viewStudents();
    }

    // Validate Student ID
    private int getStudentId() {
        String id = idField.getText().trim();

        if (id.isEmpty()) {
            throw new IllegalArgumentException(
                "Please enter Student ID!");
        }

        try {
            int value = Integer.parseInt(id);

            if (value <= 0) {
                throw new IllegalArgumentException(
                    "Student ID must be positive!");
            }

            return value;

        } catch (NumberFormatException e) {
            throw new IllegalArgumentException(
                "Student ID must contain numbers only!");
        }
    }

    // Validate Age (optional)
    private Integer getAge() {
        String age = ageField.getText().trim();

        if (age.isEmpty()) {
            return null;
        }

        try {
            int value = Integer.parseInt(age);

            if (value < 1 || value > 150) {
                throw new IllegalArgumentException(
                    "Enter a valid age between 1 and 150!");
            }

            return value;

        } catch (NumberFormatException e) {
            throw new IllegalArgumentException(
                "Age must contain numbers only!");
        }
    }

    // Add Student
    void addStudent() {
        try {
            int id = getStudentId();
            String name = nameField.getText().trim();

            if (name.isEmpty()) {
                throw new IllegalArgumentException(
                    "Please enter Student Name!");
            }

            Integer age = getAge();

            String sql = "INSERT INTO students " +
                "(student_id, name, department, age, email) " +
                "VALUES (?, ?, ?, ?, ?)";

            PreparedStatement ps = con.prepareStatement(sql);

            ps.setInt(1, id);
            ps.setString(2, name);
            setNullableString(ps, 3, deptField.getText());
            setNullableAge(ps, 4, age);
            setNullableString(ps, 5, emailField.getText());

            ps.executeUpdate();

            JOptionPane.showMessageDialog(this,
                "Student Added Successfully!");

            viewStudents();
            clearFields();

        } catch (Exception e) {
            showError(e);
        }
    }

    // View All Students
    void viewStudents() {
        try {
            checkConnection();

            model.setRowCount(0);

            String sql = "SELECT * FROM students ORDER BY student_id";

            Statement st = con.createStatement();
            ResultSet rs = st.executeQuery(sql);

            while (rs.next()) {
                model.addRow(new Object[]{
                    rs.getInt("student_id"),
                    rs.getString("name"),
                    rs.getString("department"),
                    rs.getObject("age"),
                    rs.getString("email")
                });
            }

            rs.close();
            st.close();

        } catch (Exception e) {
            showError(e);
        }
    }

    // Search Student
    void searchStudent() {
        try {
            int id = getStudentId();
            checkConnection();

            String sql =
                "SELECT * FROM students WHERE student_id = ?";

            PreparedStatement ps = con.prepareStatement(sql);
            ps.setInt(1, id);

            ResultSet rs = ps.executeQuery();
            model.setRowCount(0);

            if (rs.next()) {
                model.addRow(new Object[]{
                    rs.getInt("student_id"),
                    rs.getString("name"),
                    rs.getString("department"),
                    rs.getObject("age"),
                    rs.getString("email")
                });

                JOptionPane.showMessageDialog(this,
                    "Student Found!");

            } else {
                JOptionPane.showMessageDialog(this,
                    "Student Not Found!");
            }

            rs.close();
            ps.close();

        } catch (Exception e) {
            showError(e);
        }
    }

    // Update Student
    void updateStudent() {
        try {
            int id = getStudentId();
            String name = nameField.getText().trim();

            if (name.isEmpty()) {
                throw new IllegalArgumentException(
                    "Please enter Student Name!");
            }

            Integer age = getAge();
            checkConnection();

            String sql = "UPDATE students SET " +
                "name=?, department=?, age=?, email=? " +
                "WHERE student_id=?";

            PreparedStatement ps = con.prepareStatement(sql);

            ps.setString(1, name);
            setNullableString(ps, 2, deptField.getText());
            setNullableAge(ps, 3, age);
            setNullableString(ps, 4, emailField.getText());
            ps.setInt(5, id);

            int result = ps.executeUpdate();

            if (result > 0) {
                JOptionPane.showMessageDialog(this,
                    "Student Updated Successfully!");
            } else {
                JOptionPane.showMessageDialog(this,
                    "Student Not Found!");
            }

            ps.close();
            viewStudents();
            clearFields();

        } catch (Exception e) {
            showError(e);
        }
    }

    // Delete Student
    void deleteStudent() {
        try {
            int id = getStudentId();
            checkConnection();

            int confirm = JOptionPane.showConfirmDialog(
                this,
                "Are you sure you want to delete Student ID " + id + "?",
                "Confirm Delete",
                JOptionPane.YES_NO_OPTION
            );

            if (confirm != JOptionPane.YES_OPTION) {
                return;
            }

            String sql =
                "DELETE FROM students WHERE student_id=?";

            PreparedStatement ps = con.prepareStatement(sql);
            ps.setInt(1, id);

            int result = ps.executeUpdate();

            if (result > 0) {
                JOptionPane.showMessageDialog(this,
                    "Student Deleted Successfully!");
            } else {
                JOptionPane.showMessageDialog(this,
                    "Student Not Found!");
            }

            ps.close();
            viewStudents();
            clearFields();

        } catch (Exception e) {
            showError(e);
        }
    }

    // Clear Fields
    void clearFields() {
        idField.setText("");
        nameField.setText("");
        deptField.setText("");
        ageField.setText("");
        emailField.setText("");
        table.clearSelection();
    }

    // Set optional text fields
    private void setNullableString(
        PreparedStatement ps, int index, String value
    ) throws SQLException {

        String text = value.trim();

        if (text.isEmpty()) {
            ps.setNull(index, Types.VARCHAR);
        } else {
            ps.setString(index, text);
        }
    }

    // Set optional age
    private void setNullableAge(
        PreparedStatement ps, int index, Integer age
    ) throws SQLException {

        if (age == null) {
            ps.setNull(index, Types.INTEGER);
        } else {
            ps.setInt(index, age);
        }
    }

    // Check database connection
    private void checkConnection() throws SQLException {
        if (con == null || con.isClosed()) {
            throw new SQLException(
                "Database is not connected. Check MySQL connection!");
        }
    }

    // Display Errors
    void showError(Exception e) {
        e.printStackTrace();

        JOptionPane.showMessageDialog(
            this,
            e.getMessage(),
            "Error",
            JOptionPane.ERROR_MESSAGE
        );
    }

    // Main Method
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new StudentGUI());
    }
}
