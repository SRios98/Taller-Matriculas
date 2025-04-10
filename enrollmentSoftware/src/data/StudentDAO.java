package data;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

import model.Student;

public class StudentDAO implements CRUD_Operation<Student,String>{
	
    private Connection connection;

    public StudentDAO(Connection connection) {
        this.connection = connection;
    }
	@Override
	public void save(Student student) {
		String query = "INSERT INTO Student (Id, Name, Email) VALUES (?, ?, ?)";

        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
        	

            pstmt.setString(1, student.getId());
            pstmt.setString(2, student.getName());
            pstmt.setString(3, student.getEmail());


            int rowsAffected = pstmt.executeUpdate();
            if (rowsAffected > 0) {
                System.out.println("Studend inserted successfully.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
		
	}

	@Override
	public ArrayList<Student> fetch() {
		
        ArrayList<Student> students = new ArrayList<>();
        String query = "SELECT Id, Name, Email FROM Student";
        
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {
            
            while (rs.next()) {
                String id = rs.getString("Id");
                String name = rs.getString("Name");
                String email = rs.getString("Email");
                
                Student student = new Student(id, name, email);
                students.add(student);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return students;
	}

	@Override
	public void update(Student student) {
		
		String sql = "UPDATE Student SET Name=?, Email=? WHERE Id=?";
		try (PreparedStatement stmt = connection.prepareStatement(sql)) {
			stmt.setString(1, student.getName());
			stmt.setString(2, student.getEmail());
			stmt.setString(3, student.getId());
			stmt.executeUpdate();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void delete(String id) {
		String sql = "DELETE FROM Student WHERE Id=?";
		try (PreparedStatement stmt = connection.prepareStatement(sql)) {
			stmt.setString(1, id);
			stmt.executeUpdate();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	@Override
	public boolean authenticate(String id) {
		String sql = "SELECT Id FROM Student WHERE Id=?";
		
		try (PreparedStatement stmt = connection.prepareStatement(sql)) {
			stmt.setString(1, id);
			ResultSet rs = stmt.executeQuery();
		
			if (rs.next()) {
			return rs.getString("Id")==id;
			}
		} catch (SQLException e) {
		e.printStackTrace();}
		
		return false;
	}

}
