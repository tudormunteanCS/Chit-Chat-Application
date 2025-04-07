package com.example.demo;

import domain.PassEncTech2;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import org.postgresql.util.PSQLException;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import service.ServiceUser;
import validators.ValidationException;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.sql.SQLException;

public class SignupViewController {
    private final ServiceUser serviceUser = new ServiceUser();
    public TextField emailSignupTextField;
    public TextField firstNameSignupTextField;
    public TextField lastNameSignupTextField;
    public Button SignInButton;
    public PasswordField passwordSignupPasswordField;
    public Stage signUpStage;

    public void setSignUpStage(Stage stage) {
        this.signUpStage = stage;
    }


    public void signInUser(ActionEvent actionEvent) throws IOException {
        String firstName = firstNameSignupTextField.getText();
        String lastName = lastNameSignupTextField.getText();
        String email = emailSignupTextField.getText();
        String password = passwordSignupPasswordField.getText();
        try {

            serviceUser.addUser(firstName, lastName, email, PassEncTech2.toHexString(PassEncTech2.getSHA(password)));

            //close signUpStage so the UserInterfaceStage appears
            signUpStage.close();

            //User interface scene should appear after sign in
            Stage userInterface = new Stage();
            FXMLLoader userInterfaceView = new FXMLLoader(getClass().getResource("userInterface-view.fxml"));
            userInterface.setScene(new Scene(userInterfaceView.load(), 1000, 600));
            UserInterfaceViewController userInterfaceViewController = userInterfaceView.getController();


            userInterfaceViewController.setUserInterfaceStage(userInterface);
            userInterfaceViewController.showCurrentUser(email);
            userInterface.setTitle("User Interface");
            userInterface.show();



        } catch (ValidationException ve) {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("ValidationException!");
            alert.setHeaderText(null);
            DialogPane dialogPane = alert.getDialogPane();

            VBox vbox = new VBox();

            Label label = new Label(ve.getMessage());
            label.setStyle("-fx-text-fill: red;");

            ImageView imageView = new ImageView(new Image("D:\\school\\MAP\\demo\\src\\main\\java\\images\\exception.jpg")); // Adaugă o imagine personalizată, dacă dorești
            imageView.setFitHeight(100);
            imageView.setFitWidth(100);

            vbox.getChildren().addAll(label, imageView);
            dialogPane.setContent(vbox);
            alert.showAndWait();
        } catch (RuntimeException e) {

            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Sql exception!");
            alert.setHeaderText(null);
            DialogPane dialogPane = alert.getDialogPane();

            VBox vbox = new VBox();

            Label label = new Label(e.getMessage());
            label.setStyle("-fx-text-fill: red;");

            ImageView imageView = new ImageView(new Image("D:\\school\\MAP\\demo\\src\\main\\java\\images\\exception.jpg")); // Adaugă o imagine personalizată, dacă dorești
            imageView.setFitHeight(100);
            imageView.setFitWidth(100);

            vbox.getChildren().addAll(label, imageView);
            dialogPane.setContent(vbox);
            alert.showAndWait();
        } catch (NoSuchAlgorithmException e) {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("SNoSuchAlgorithmException");
            alert.setHeaderText(null);
            DialogPane dialogPane = alert.getDialogPane();

            VBox vbox = new VBox();

            Label label = new Label("Exception thrown for incorrect algorithm: " + e);
            label.setStyle("-fx-text-fill: red;");

            ImageView imageView = new ImageView(new Image("D:\\school\\MAP\\demo\\src\\main\\java\\images\\exception.jpg")); // Adaugă o imagine personalizată, dacă dorești
            imageView.setFitHeight(100);
            imageView.setFitWidth(100);

            vbox.getChildren().addAll(label, imageView);
            dialogPane.setContent(vbox);
            alert.showAndWait();
        } finally {
            firstNameSignupTextField.clear();
            lastNameSignupTextField.clear();
            emailSignupTextField.clear();
            passwordSignupPasswordField.clear();
        }


    }
}
