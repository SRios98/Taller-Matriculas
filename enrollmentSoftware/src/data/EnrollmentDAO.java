package data;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;
import java.util.ArrayList;

import model.Enrollment;

public class EnrollmentDAO {
    private Connection connection;

    public EnrollmentDAO(Connection connection) {
        this.connection = connection;
    }

    public void save(Enrollment enrollment) {
        // Validate inputs
        if (enrollment.getStudentId().isEmpty() || enrollment.getCourseCode().isEmpty()) {
            System.out.println("Error: Student ID or Course Code cannot be empty.");
            return;
        }

        // Check if the enrollment already exists
        if (authenticate(enrollment.getStudentId(), enrollment.getCourseCode())) {
            System.out.println("Error: The student is already enrolled in this course.");
            return;
        }

        // Validate the enrollment date (it should not be a future date)
        if (enrollment.getEnrollmentDate().isAfter(LocalDate.now())) {
            System.out.println("Error: The enrollment date cannot be in the future.");
            return;
        }

        String query = "INSERT INTO Enrollment (Student_id, course_code, enrollment_date) VALUES (?, ?, ?)";
        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setString(1, enrollment.getStudentId());
            pstmt.setString(2, enrollment.getCourseCode());
            pstmt.setDate(3, java.sql.Date.valueOf(enrollment.getEnrollmentDate()));

            int rowsAffected = pstmt.executeUpdate();
            if (rowsAffected > 0) {
                System.out.println("Enrollment inserted successfully.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public ArrayList<Enrollment> fetch() {
        ArrayList<Enrollment> enrollments = new ArrayList<>();
        String query = "SELECT Student_id, Course_code, Enrollment_date FROM Enrollment";

        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {

            while (rs.next()) {
                String student_id = rs.getString("Student_id");
                String course_code = rs.getString("Course_code");
                LocalDate enrollment_date = rs.getDate("Enrollment_date").toLocalDate();

                Enrollment enrollment = new Enrollment(student_id, course_code, enrollment_date);
                enrollments.add(enrollment);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return enrollments;
    }

    public void update(Enrollment enrollment) {
        // Check if the enrollment exists first
        if (!authenticate(enrollment.getStudentId(), enrollment.getCourseCode())) {
            System.out.println("Error: The student is not enrolled in this course.");
            return;
        }

        // Validate the enrollment date (it should not be a future date)
        if (enrollment.getEnrollmentDate().isAfter(LocalDate.now())) {
            System.out.println("Error: The enrollment date cannot be in the future.");
            return;
        }

        String sql = "UPDATE Enrollment SET Student_id=?, Course_code=? WHERE Enrollment_date=?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, enrollment.getStudentId());
            stmt.setString(2, enrollment.getCourseCode());
            stmt.setDate(3, java.sql.Date.valueOf(enrollment.getEnrollmentDate()));
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void delete(String studentId, String courseCode) {
        // Validate that the enrollment exists
        if (!authenticate(studentId, courseCode)) {
            System.out.println("Error: Enrollment does not exist.");
            return;
        }

        String sql = "DELETE FROM Enrollment WHERE Student_id = ? AND Course_code = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, studentId);
            stmt.setString(2, courseCode);
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public boolean authenticate(String studentId, String courseCode) {
        String sql = "SELECT Student_id, Course_code FROM Enrollment WHERE Student_id=? AND Course_code=?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, studentId);
            stmt.setString(2, courseCode);
            ResultSet rs = stmt.executeQuery();

            return rs.next();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
}

