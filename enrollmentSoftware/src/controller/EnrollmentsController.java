package controller;

import java.sql.Connection;
import java.time.LocalDate;

import application.Main;
import data.CourseDAO;
import data.DBConnection;
import data.EnrollmentDAO;
import data.StudentDAO;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseEvent;
import model.Enrollment;
import model.Student;
import model.Course;

public class EnrollmentsController {

    @FXML private ComboBox<String> studentComboBox;
    @FXML private ComboBox<String> courseComboBox;
    @FXML private DatePicker enrollmentDatePicker;
    @FXML private TableView<Enrollment> enrollmentTable;
    @FXML private TableColumn<Enrollment, String> studentIdColumn;
    @FXML private TableColumn<Enrollment, String> courseCodeColumn;
    @FXML private TableColumn<Enrollment, LocalDate> dateColumn;

    private Connection connection = DBConnection.getInstance().getConnection();
    private EnrollmentDAO enrollmentDAO = new EnrollmentDAO(connection);
    private StudentDAO studentDAO = new StudentDAO(connection);
    private CourseDAO courseDAO = new CourseDAO(connection);

    @FXML
    public void initialize() {
        studentIdColumn.setCellValueFactory(new PropertyValueFactory<>("studentId"));
        courseCodeColumn.setCellValueFactory(new PropertyValueFactory<>("courseCode"));
        dateColumn.setCellValueFactory(new PropertyValueFactory<>("enrollmentDate"));

        enrollmentDatePicker.setValue(LocalDate.now());
        loadComboBoxes();
        loadEnrollments();

        enrollmentTable.setOnMouseClicked((MouseEvent event) -> {
            Enrollment selected = enrollmentTable.getSelectionModel().getSelectedItem();
            if (selected != null) {
                studentComboBox.setValue(selected.getStudentId());
                courseComboBox.setValue(selected.getCourseCode());
                enrollmentDatePicker.setValue(selected.getEnrollmentDate());
            }
        });
    }

    private void loadComboBoxes() {
        studentComboBox.getItems().setAll(
            studentDAO.fetch().stream().map(Student::getId).toList()
        );
        courseComboBox.getItems().setAll(
            courseDAO.fetch().stream().map(Course::getCode).toList()
        );
    }

    private void loadEnrollments() {
        enrollmentTable.getItems().setAll(enrollmentDAO.fetch());
    }

    @FXML
    private void handleSaveEnrollment() {
        try {
            String studentId = studentComboBox.getValue();
            String courseCode = courseComboBox.getValue();
            LocalDate date = enrollmentDatePicker.getValue();

            if (studentId == null || courseCode == null || date == null) {
                showAlert("Error", "Por favor completa todos los campos.");
                return;
            }

            Enrollment enrollment = new Enrollment(studentId, courseCode, date);
            enrollmentDAO.save(enrollment);
            loadEnrollments();
            clearFields();
        } catch (Exception e) {
            showAlert("Error", "No se pudo guardar la inscripción.");
            e.printStackTrace();
        }
    }

    @FXML
    private void handleUpdateEnrollment() {
        try {
            String studentId = studentComboBox.getValue();
            String courseCode = courseComboBox.getValue();
            LocalDate date = enrollmentDatePicker.getValue();

            if (studentId == null || courseCode == null || date == null) {
                showAlert("Error", "Selecciona una inscripción para actualizar.");
                return;
            }

            Enrollment enrollment = new Enrollment(studentId, courseCode, date);
            enrollmentDAO.update(enrollment);
            loadEnrollments();
            clearFields();
        } catch (Exception e) {
            showAlert("Error", "No se pudo actualizar la inscripción.");
            e.printStackTrace();
        }
    }

    @FXML
    private void handleDeleteEnrollment() {
        String studentId = studentComboBox.getValue();
        String courseCode = courseComboBox.getValue();

        if (studentId != null && courseCode != null) {
            enrollmentDAO.delete(studentId, courseCode);
            loadEnrollments();
            clearFields();
        } else {
            showAlert("Error", "Selecciona una inscripción para eliminar.");
        }
    }

    @FXML
    private void clearFields() {
        studentComboBox.setValue(null);
        courseComboBox.setValue(null);
        enrollmentDatePicker.setValue(LocalDate.now());
        enrollmentTable.getSelectionModel().clearSelection();
    }

    @FXML
    private void goBack() {
    	Main.loadView("/view/MainMenu.fxml");
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setContentText(message);
        alert.showAndWait();
    }
}

