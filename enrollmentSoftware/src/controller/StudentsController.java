package controller;

import java.sql.Connection;

import application.Main;
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
    @FXML private TableColumn<Student, String> idCol;
    @FXML private TableColumn<Student, String> nameColumn;
    @FXML private TableColumn<Student, String> emailColumn;

    private final Connection connection = DBConnection.getInstance().getConnection();
    private final StudentDAO studentDAO = new StudentDAO(connection);

    @FXML
    public void initialize() {
        idCol.setCellValueFactory(new PropertyValueFactory<>("id"));
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
    private void handleSaveStudent() {
        String id = idField.getText().trim();
        String name = nameField.getText().trim();
        String email = emailField.getText().trim();

        if (!validateFields(id, name, email)) return;

        Student student = new Student(id, name, email);

        if (!studentDAO.authenticate(student.getId())) {
            studentDAO.save(student);
            showAlert("Student saved successfully!");
            loadStudents();
            clearFields();
        } else {
            showAlert("A student with this ID already exists.");
        }
    }

    @FXML
    private void handleUpdateStudent() {
        String id = idField.getText().trim();
        String name = nameField.getText().trim();
        String email = emailField.getText().trim();

        if (!validateFields(id, name, email)) return;

        Student student = new Student(id, name, email);
        studentDAO.update(student);
        showAlert("Student updated.");
        loadStudents();
        clearFields();
    }

    @FXML
    private void handleDeleteCourse() {
        String id = idField.getText();
        studentDAO.delete(id);
        showAlert("Student deleted.");
        loadStudents();
        clearFields();
    }

    @FXML
    private void clearFields() {
        idField.clear();
        nameField.clear();
        emailField.clear();
    }

    @FXML
    private void goBack() {
        Main.loadView("/view/MainMenu.fxml");
    }

    private boolean validateFields(String id, String name, String email) {
        if (id.isEmpty() || name.isEmpty() || email.isEmpty()) {
            showAlert("All fields must be filled.");
            return false;
        }

        if (!id.matches("^[A-Za-z][0-9]{3}$")) {
            showAlert("ID must start with a letter followed by 3 digits (e.g., A123).");
            return false;
        }

        if (!name.matches("^[A-Za-zÁÉÍÓÚáéíóúÑñ ]+$")) {
            showAlert("Name must only contain letters and spaces.");
            return false;
        }

        if (!email.matches("^[\\w.-]+@[\\w.-]+\\.\\w{2,}$")) {
            showAlert("Enter a valid email address.");
            return false;
        }

        return true;
    }

    private void showAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Información");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}

