package controller;

import application.Main;

public class MainMenuController {
	
    public void goToStudents() {
        Main.loadView("/view/Students.fxml");
    }

    public void goToCourses() {
        Main.loadView("/view/Courses.fxml");
    }

    public void goToEnrollment() {
        Main.loadView("/view/Enrollments.fxml");
    }

}
