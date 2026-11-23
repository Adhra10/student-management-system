
import java.sql.*;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {

        String url = "jdbc:mysql://localhost:3306/student_db";
        String username = "root";
        String password = "your_password";

        try {
            Connection con = DriverManager.getConnection(url, username, password);
            Scanner sc = new Scanner(System.in);

            System.out.println("Database Connected Successfully!");

            int choice;

            do {
                System.out.println("\n===== STUDENT MANAGEMENT SYSTEM =====");
                System.out.println("1. Add Student");
                System.out.println("2. View All Students");
                System.out.println("3. Search Student");
                System.out.println("4. Update Student");
                System.out.println("5. Delete Student");
                System.out.println("6. Exit");

                System.out.print("Enter your choice: ");
                choice = sc.nextInt();
                sc.nextLine();

                switch (choice) {

                    case 1: {
                        System.out.print("Enter Student ID: ");
                        int id = sc.nextInt();
                        sc.nextLine();

                        System.out.print("Enter Name: ");
                        String name = sc.nextLine();

                        System.out.print("Enter Department: ");
                        String department = sc.nextLine();

                        System.out.print("Enter Age: ");
                        int age = sc.nextInt();
                        sc.nextLine();

                        System.out.print("Enter Email: ");
                        String email = sc.nextLine();

                        String sql = "INSERT INTO students VALUES (?, ?, ?, ?, ?)";

                        try (PreparedStatement pst = con.prepareStatement(sql)) {
                            pst.setInt(1, id);
                            pst.setString(2, name);
                            pst.setString(3, department);
                            pst.setInt(4, age);
                            pst.setString(5, email);

                            pst.executeUpdate();
                            System.out.println("Student Added Successfully!");
                        }
                        break;
                    }

                    case 2: {
                        String sql = "SELECT * FROM students";

                        try (Statement st = con.createStatement();
                             ResultSet rs = st.executeQuery(sql)) {

                            while (rs.next()) {
                                System.out.println("\nStudent ID: " + rs.getInt("student_id"));
                                System.out.println("Name: " + rs.getString("name"));
                                System.out.println("Department: " + rs.getString("department"));
                                System.out.println("Age: " + rs.getInt("age"));
                                System.out.println("Email: " + rs.getString("email"));
                            }
                        }
                        break;
                    }

                    case 3: {
                        System.out.print("Enter Student ID to Search: ");
                        int searchId = sc.nextInt();

                        String sql = "SELECT * FROM students WHERE student_id = ?";

                        try (PreparedStatement pst = con.prepareStatement(sql)) {
                            pst.setInt(1, searchId);

                            try (ResultSet rs = pst.executeQuery()) {
                                if (rs.next()) {
                                    System.out.println("Student ID: " + rs.getInt("student_id"));
                                    System.out.println("Name: " + rs.getString("name"));
                                    System.out.println("Department: " + rs.getString("department"));
                                    System.out.println("Age: " + rs.getInt("age"));
                                    System.out.println("Email: " + rs.getString("email"));
                                } else {
                                    System.out.println("Student Not Found!");
                                }
                            }
                        }
                        break;
                    }

                    case 4: {
                        System.out.print("Enter Student ID to Update: ");
                        int updateId = sc.nextInt();
                        sc.nextLine();

                        System.out.print("Enter New Name: ");
                        String newName = sc.nextLine();

                        System.out.print("Enter New Department: ");
                        String newDepartment = sc.nextLine();

                        System.out.print("Enter New Age: ");
                        int newAge = sc.nextInt();
                        sc.nextLine();

                        System.out.print("Enter New Email: ");
                        String newEmail = sc.nextLine();

                        String sql = "UPDATE students SET name=?, department=?, age=?, email=? WHERE student_id=?";

                        try (PreparedStatement pst = con.prepareStatement(sql)) {
                            pst.setString(1, newName);
                            pst.setString(2, newDepartment);
                            pst.setInt(3, newAge);
                            pst.setString(4, newEmail);
                            pst.setInt(5, updateId);

                            int rows = pst.executeUpdate();

                            if (rows > 0) {
                                System.out.println("Student Updated Successfully!");
                            } else {
                                System.out.println("Student ID Not Found!");
                            }
                        }
                        break;
                    }

                    case 5: {
                        System.out.print("Enter Student ID to Delete: ");
                        int deleteId = sc.nextInt();

                        String sql = "DELETE FROM students WHERE student_id = ?";

                        try (PreparedStatement pst = con.prepareStatement(sql)) {
                            pst.setInt(1, deleteId);

                            int rows = pst.executeUpdate();

                            if (rows > 0) {
                                System.out.println("Student Deleted Successfully!");
                            } else {
                                System.out.println("Student ID Not Found!");
                            }
                        }
                        break;
                    }

                    case 6:
                        System.out.println("Thank you for using Student Management System!");
                        break;

                    default:
                        System.out.println("Invalid Choice! Please try again.");
                }

            } while (choice != 6);

            con.close();
            sc.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
