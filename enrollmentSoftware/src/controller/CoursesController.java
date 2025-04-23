package controller;

import java.sql.Connection;

import application.Main;
import data.CourseDAO;
import data.DBConnection;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
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

    private Connection connection = DBConnection.getInstance().getConnection();
    private CourseDAO courseDAO = new CourseDAO(connection);

    @FXML
    public void initialize() {
        codeColumn.setCellValueFactory(new PropertyValueFactory<>("code"));
        nameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        creditsColumn.setCellValueFactory(new PropertyValueFactory<>("credits"));
        loadCourses();

        courseTable.setOnMouseClicked((MouseEvent event) -> {
            if (courseTable.getSelectionModel().getSelectedItem() != null) {
                Course selected = courseTable.getSelectionModel().getSelectedItem();
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
        try {
            String code = codeField.getText();
            String name = nameField.getText();
            int credits = Integer.parseInt(creditsField.getText());
            courseDAO.save(new Course(code, name, credits));
            loadCourses();
            clearFields();
        } catch (NumberFormatException e) {
            showAlert("Error", "Créditos debe ser un número entero.");
        }
    }

    @FXML
    private void handleUpdateCourse() {
        try {
            String code = codeField.getText();
            String name = nameField.getText();
            int credits = Integer.parseInt(creditsField.getText());
            courseDAO.update(new Course(code, name, credits));
            loadCourses();
            clearFields();
        } catch (NumberFormatException e) {
            showAlert("Error", "Créditos debe ser un número entero.");
        }
    }

    @FXML
    private void handleDeleteCourse() {
        String code = codeField.getText();
        if (!code.isEmpty()) {
            courseDAO.delete(code);
            loadCourses();
            clearFields();
        } else {
            showAlert("Error", "Seleccione un curso para eliminar.");
        }
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
        Alert alert = new Alert(AlertType.ERROR);
        alert.setTitle(title);
        alert.setContentText(content);
        alert.showAndWait();
    }
}

