package application;

import javafx.application.Application;
import javafx.stage.Stage;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.fxml.FXMLLoader;

public class Main extends Application {
	private static AnchorPane rootLayout;

	@Override
	public void start(Stage primaryStage) {
		try {
			FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/MainMenu.fxml"));
			rootLayout = loader.load();
			Scene scene = new Scene(rootLayout);
			primaryStage.setScene(scene);
			primaryStage.setTitle("Enrollment Software");
			primaryStage.show();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public static void loadView(String fxmlFile) {
		try {
		FXMLLoader loader = new FXMLLoader(Main.class.getResource(fxmlFile));
		rootLayout.setCenterShape(loader.load());
		} catch (Exception e) {
		e.printStackTrace();
		}
		}


	public static void main(String[] args) {
		launch(args);
	}
}
