import java.sql.*;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.*;
import javafx.stage.Stage;

public class App extends Application {
    public static void main(String[] args) throws Exception {

        // Connect to MySQL Server & Create Database-Table
        try {
            Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/ap", "root", "Barsan2023++");
            Statement statement = connection.createStatement();
            createDatabase(statement);
        } catch (Exception e) {
            e.printStackTrace();
        }

        // Launch GUI
        launch(args);
    }

    // Database Details
    private static void createDatabase(Statement statement) {
        try {
            statement.executeUpdate("CREATE DATABASE IF NOT EXISTS roster");
            statement.executeUpdate("CREATE TABLE IF NOT EXISTS roster.students"
                                    + "("
                                    + "id               INT         NOT NULL        UNIQUE          AUTO_INCREMENT,"
                                    + "last_name        VARCHAR(50) NOT NULL,"
                                    + "first_name       VARCHAR(50) NOT NULL,"
                                    + "major            VARCHAR(50),"
                                    + "current_grade    VARCHAR(5),"
                                    + "grade_option     INT,"
                                    + "honor_status     INT,"
                                    + "note_area        VARCHAR(200),"
                                    + "photo            VARCHAR(100),"
                                    + "PRIMARY KEY      (id)"
                                    + ") ENGINE=InnoDB AUTO_INCREMENT=100001;");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // GUI Start
    @Override
    public void start(Stage stage) throws Exception {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("layout.fxml"));
            Parent root = loader.load();
            Scene scene = new Scene(root);
            scene.getStylesheets().add(getClass().getResource("style.css").toExternalForm());

            stage.setTitle("Student Roster");
            //stage.setResizable(false);
            stage.setScene(scene);
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}