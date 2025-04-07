package com.example.demo;

import domain.PassEncTech2;
import domain.Utilizator;
import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.beans.property.BooleanProperty;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.transform.Rotate;
import javafx.stage.Stage;
import javafx.util.Duration;
import service.ServiceUser;

import java.io.IOException;
import java.net.URL;
import java.security.NoSuchAlgorithmException;
import java.util.Optional;
import java.util.ResourceBundle;

public class LoginController implements Initializable {
    public TextField emailTextField;
    public PasswordField passwordTextField;
    public CheckBox robotCheckBox;
    public Label titleLabel;
    @FXML
    public AnchorPane anchorPaneLogInStage;

    public ImageView imageBackround;
    private Stage loginStage;
    public Button loginButton;
    private ServiceUser serviceUser = new ServiceUser();
    private int width;
    private int height;

    @Override
    public void initialize(URL location, ResourceBundle resources) {


        //set the stage to be cool
        //this.loginStage  should look cool


        //add image
        Image image = new Image("https://cultureofyes.files.wordpress.com/2020/04/chit-chat.png");
        ImageView imageView = new ImageView(image);


        // Set up rotation animation for the image
        imageView.setFitHeight(175);
        imageView.setFitWidth(175);
        imageView.setX(0);
        imageView.setY(0);
        Rotate rotate = new Rotate(360);
        imageView.getTransforms().add(rotate);

        Timeline rotationAnimation = new Timeline(
                new KeyFrame(Duration.ZERO, new javafx.event.EventHandler<ActionEvent>() {
                    @Override
                    public void handle(ActionEvent event) {
                        // Update pivot point based on current image position
                        rotate.setPivotX(imageView.getX() + imageView.getFitWidth() / 2);
                        rotate.setPivotY(imageView.getY() + imageView.getFitHeight() / 2);

                        // Rotate the image
                        rotate.setAngle(rotate.getAngle() + 1);
                    }
                }),
                new KeyFrame(Duration.millis(10))
        );
        rotationAnimation.setCycleCount(Animation.INDEFINITE);
        rotationAnimation.play();
        anchorPaneLogInStage.getChildren().add(imageView);
        imageBackround = new ImageView("https://e0.pxfuel.com/wallpapers/1021/875/desktop-wallpaper-digital-art-neon-bubbles-abstract-gradient-background-3bcbc6-neon-gradient.jpg");

        // Event handler for Enter key pressed
        anchorPaneLogInStage.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.ENTER) {
                try {
                    logInUser(new ActionEvent()); // Call the login method when Enter is pressed
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (NoSuchAlgorithmException e) {
                    throw new RuntimeException(e);
                }
            }
        });


    }

    public void signupUser(MouseEvent mouseEvent) throws IOException {
        // on mouse clicked open signup window
        loginStage.close();
        FXMLLoader fxmlLoaderSignUpView = new FXMLLoader(getClass().getResource("signup-view.fxml"));
        Stage signUpStage = new Stage();
        signUpStage.setScene(new Scene(fxmlLoaderSignUpView.load(), 940, 560));
        SignupViewController signupViewController = fxmlLoaderSignUpView.getController();
        signupViewController.setSignUpStage(signUpStage);
        signUpStage.setTitle("Sign Up");
        signUpStage.show();
    }

    public void setStage(Stage stage) {
        this.loginStage = stage;

    }

    public void logInUser(ActionEvent actionEvent) throws IOException, NoSuchAlgorithmException {
        String email = emailTextField.getText();
        String password = passwordTextField.getText();
        String encryptedPassword = PassEncTech2.toHexString(PassEncTech2.getSHA(password));
        //see if robotCheckBox is checked


        Optional<Utilizator> utilizatorOptional = serviceUser.getUserByEmail(email);
        if (utilizatorOptional.isPresent()) {
            Utilizator utilizator = utilizatorOptional.get();
            if (utilizator.getPassword().equals(encryptedPassword)) {
                //close logInStage and open User's interface Stage
                loginStage.close();
                Stage userInterface = new Stage();
                FXMLLoader userInterfaceView = new FXMLLoader(getClass().getResource("userInterface-view.fxml"));
                userInterface.setScene(new Scene(userInterfaceView.load(), 1000, 600));
                UserInterfaceViewController userInterfaceViewController = userInterfaceView.getController();


                userInterfaceViewController.setUserInterfaceStage(userInterface);
                userInterfaceViewController.showCurrentUser(email);
                userInterface.setTitle("User Interface");
                userInterface.show();


            } else {
                //throw alert password does not match
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Incorrect Credentials!");
                alert.setHeaderText(null);
                DialogPane dialogPane = alert.getDialogPane();

                VBox vbox = new VBox();

                Label label = new Label("Password does not exist!");
                label.setStyle("-fx-text-fill: red;");

                ImageView imageView = new ImageView(new Image("D:\\school\\MAP\\demo\\src\\main\\java\\images\\exception.jpg")); // Adaugă o imagine personalizată, dacă dorești
                imageView.setFitHeight(100);
                imageView.setFitWidth(100);

                vbox.getChildren().addAll(label, imageView);
                dialogPane.setContent(vbox);
                alert.showAndWait();
                passwordTextField.clear();
            }
        } else {
            //throw alert email does not exist
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Incorrect Credentials!");
            alert.setHeaderText(null);
            DialogPane dialogPane = alert.getDialogPane();

            VBox vbox = new VBox();

            Label label = new Label("Email does not exist!");
            label.setStyle("-fx-text-fill: red;");

            ImageView imageView = new ImageView(new Image("D:\\school\\MAP\\demo\\src\\main\\java\\images\\exception.jpg")); // Adaugă o imagine personalizată, dacă dorești
            imageView.setFitHeight(100);
            imageView.setFitWidth(100);

            vbox.getChildren().addAll(label, imageView);
            dialogPane.setContent(vbox);
            alert.showAndWait();
            emailTextField.clear();
            passwordTextField.clear();
        }

    }


}
