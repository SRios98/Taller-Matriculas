package controller;

import java.sql.Connection;
import application.Main;
import data.CourseDAO;
import data.DBConnection;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseEvent;
import model.Course;

public class CoursesController {

    @FXML private TextField codeField;
    @FXML private TextField nameField;
    @FXML private TextField creditsField;
    @FXML private TableView<Course> courseTable;
    @FXML private TableColumn<Course, String> codeColumn;
    @FXML private TableColumn<Course, String> nameColumn;
    @FXML private TableColumn<Course, Integer> creditsColumn;

    private final Connection connection = DBConnection.getInstance().getConnection();
    private final CourseDAO courseDAO = new CourseDAO(connection);

    @FXML
    public void initialize() {
        codeColumn.setCellValueFactory(new PropertyValueFactory<>("code"));
        nameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        creditsColumn.setCellValueFactory(new PropertyValueFactory<>("credits"));
        loadCourses();

        courseTable.setOnMouseClicked((MouseEvent event) -> {
            Course selected = courseTable.getSelectionModel().getSelectedItem();
            if (selected != null) {
                codeField.setText(selected.getCode());
                nameField.setText(selected.getName());
                creditsField.setText(String.valueOf(selected.getCredits()));
            }
        });
    }

    private void loadCourses() {
        courseTable.getItems().setAll(courseDAO.fetch());
    }

    @FXML
    private void handleSaveCourse() {
        String code = codeField.getText().trim();
        String name = nameField.getText().trim();
        String creditsText = creditsField.getText().trim();

        if (code.isEmpty() || name.isEmpty() || creditsText.isEmpty()) {
            showAlert("Validation Error", "All fields must be filled.");
            return;
        }

        try {
            int credits = Integer.parseInt(creditsText);

            if (courseDAO.authenticate(code)) {
                showAlert("Error", "A course with this code already exists.");
                return;
            }

            courseDAO.save(new Course(code, name, credits));
            loadCourses();
            clearFields();
        } catch (NumberFormatException e) {
            showAlert("Error", "Credits must be an integer.");
        }
    }

    @FXML
    private void handleUpdateCourse() {
        String code = codeField.getText().trim();
        String name = nameField.getText().trim();
        String creditsText = creditsField.getText().trim();

        if (code.isEmpty() || name.isEmpty() || creditsText.isEmpty()) {
            showAlert("Validation Error", "All fields must be filled.");
            return;
        }

        try {
            int credits = Integer.parseInt(creditsText);

            if (!courseDAO.exists(code)) {
                showAlert("Error", "This course does not exist.");
                return;
            }

            courseDAO.update(new Course(code, name, credits));
            loadCourses();
            clearFields();
        } catch (NumberFormatException e) {
            showAlert("Error", "Credits must be an integer.");
        }
    }

    @FXML
    private void handleDeleteCourse() {
        String code = codeField.getText().trim();

        if (code.isEmpty()) {
            showAlert("Validation Error", "Please select a course to delete.");
            return;
        }

        if (!courseDAO.exists(code)) {
            showAlert("Error", "The course does not exist.");
            return;
        }

        courseDAO.delete(code);
        loadCourses();
        clearFields();
    }

    @FXML
    private void clearFields() {
        codeField.clear();
        nameField.clear();
        creditsField.clear();
        courseTable.getSelectionModel().clearSelection();
    }

    @FXML
    private void goBack() {
        Main.loadView("/view/MainMenu.fxml");
    }

    private void showAlert(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
}

