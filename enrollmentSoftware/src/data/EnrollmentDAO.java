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
        String query = "SELECT Stundet_id, Course_code, Enrollment_date FROM Book";
        
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {
            
            while (rs.next()) {
                String student_id = rs.getString("Stundet_id");
                String course_code = rs.getString("Course_code");
                LocalDate enrollment_date = rs.getDate("Enrollment_date").toLocalDate();

;
                
		Enrollment enrollment = new Enrollment(student_id, course_code, enrollment_date);
			enrollments.add(enrollment);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return enrollments;
	}


	public void update(Enrollment enrollment) {
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


	public void delete(String id, String code) {
		String sql = "DELETE FROM Enrollment WHERE Student_id = ? AND Course_code = ?";
		try (PreparedStatement stmt = connection.prepareStatement(sql)) {
			stmt.setString(1, id);
			stmt.setString(2, code);
			stmt.executeUpdate();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public boolean authenticate(String id, String code) {
		String sql = "SELECT Student_id, Course_code FROM Enrollment WHERE Student_id=? AND Course_code=?";
		
		try (PreparedStatement stmt = connection.prepareStatement(sql)) {
			stmt.setString(1, id);
			stmt.setString(2, code);
			ResultSet rs = stmt.executeQuery();
		
			if (rs.next()) {
				return rs.getString("Student_id").equals(id) && rs.getString("Course_code").equals(code);
			
			}
		} catch (SQLException e) {
		e.printStackTrace();}
		
		return false;
	}

}
