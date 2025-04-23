package controller;

import java.sql.Connection;

import data.DBConnection;
import data.StudentDAO;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import model.Student;

public class StudentsController {

    @FXML private TextField idField;
    @FXML private TextField nameField;
    @FXML private TextField emailField;

    @FXML private TableView<Student> studentTable;
    @FXML private TableColumn<Student, String> idColumn;
    @FXML private TableColumn<Student, String> nameColumn;
    @FXML private TableColumn<Student, String> emailColumn;

    private final Connection connection = DBConnection.getInstance().getConnection();
    private final StudentDAO studentDAO = new StudentDAO(connection);

    @FXML
    public void initialize() {
        idColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        nameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        emailColumn.setCellValueFactory(new PropertyValueFactory<>("email"));

        loadStudents();
        studentTable.setOnMouseClicked(e -> fillForm());
    }

    private void loadStudents() {
        studentTable.setItems(FXCollections.observableArrayList(studentDAO.fetch()));
    }

    private void fillForm() {
        Student selected = studentTable.getSelectionModel().getSelectedItem();
        if (selected != null) {
            idField.setText(selected.getId());
            nameField.setText(selected.getName());
            emailField.setText(selected.getEmail());
        }
    }

    @FXML
    private void saveStudent() {
        Student student = new Student(
                idField.getText(),
                nameField.getText(),
                emailField.getText()
        );

        if (!studentDAO.authenticate(student.getId())) {
            studentDAO.save(student);
            showAlert("Student saved successfully!");
        } else {
            showAlert("A student with this ID already exists.");
        }

        loadStudents();
        clearFields();
    }

    @FXML
    private void updateStudent() {
        Student student = new Student(
                idField.getText(),
                nameField.getText(),
                emailField.getText()
        );
        studentDAO.update(student);
        showAlert("Student updated.");
        loadStudents();
        clearFields();
    }

    @FXML
    private void deleteStudent() {
        String id = idField.getText();
        studentDAO.delete(id);
        showAlert("Student deleted.");
        loadStudents();
        clearFields();
    }

    private void clearFields() {
        idField.clear();
        nameField.clear();
        emailField.clear();
    }

    private void showAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Info");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}

