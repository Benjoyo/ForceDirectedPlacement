package de.bennetkrause.fdp;

import java.io.IOException;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Main extends Application {

	public static void main(String[] args) {
		launch(args);
	}

	@Override
	public void start(Stage primaryStage) throws IOException {

		Parent root = FXMLLoader.load(getClass().getClassLoader().getResource("force_directed_placement.fxml"));

		primaryStage.setTitle("Force-directed Placement Demo");

		primaryStage.setScene(new Scene(root, 975, 600));
		primaryStage.setOnCloseRequest(e -> System.exit(0));
		primaryStage.show();
	}
}
