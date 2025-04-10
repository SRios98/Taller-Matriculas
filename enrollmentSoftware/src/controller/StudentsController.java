package controller;

import java.sql.Connection;

import data.DBConnection;
import data.StudentDAO;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import model.Student;

public class StudentsController {
	@FXML
	private TextField idField;
	@FXML
	private TextField nameField;
	@FXML
	private TextField emailField;
	@FXML
	private TableView<Student> studentTable;
	@FXML
	private TableColumn<Student, String> idColumn;
	@FXML
	private TableColumn<Student, String> nameColumn;
	@FXML
	private TableColumn<Student, String> emailColumn;
    
    private Connection connection = DBConnection.getInstance().getConnection();
    private StudentDAO studentDAO = new StudentDAO(connection);

    
    @FXML
    public void initialize() {
    	
    	
        idColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        nameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        emailColumn.setCellValueFactory(new PropertyValueFactory<>("email"));
        loadStudents();
    }

    private void loadStudents() {
        studentTable.getItems().setAll(studentDAO.fetch());
    }
    
	
	
    private void clearFields() {
        idField.clear();
        nameField.clear();
        emailField.clear();
    }
}
