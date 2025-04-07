package com.example.demo;


import domain.*;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import service.ServiceFriend;
import service.ServiceMessages;
import service.ServicePendingFriends;
import service.ServiceUser;

import java.io.*;
import java.net.Socket;
import java.net.URL;
import java.time.LocalDate;
import java.util.Objects;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class UserInterfaceViewController implements Initializable {
    public Label labelUserLogged;
    private final ServiceUser serviceUser = new ServiceUser();
    private final ServiceFriend serviceFriend = new ServiceFriend();
    private final ServicePendingFriends servicePendingFriends = new ServicePendingFriends();
    private final ServiceMessages serviceMessages = new ServiceMessages();
    public Button logOutButton;
    public Button friendRequestButton;
    public Button deleteFriendButton;
    @FXML
    public TableView<RequestingFriend> pendingRequestsTable;
    @FXML
    public TableColumn<RequestingFriend, String> firstNamePendingReqColumn;
    @FXML
    public TableColumn<RequestingFriend, String> lastNamePendingReqColumn;

    public Button acceptFriendButton;

    public Button declineFriendButton;

    public TextField messageTextField;
    public Button sendMessageButton;
    public Tab tabConversations;
    public VBox vBoxChat;
    public Label replyLabel;
    public Button nextPageButton;
    public Button previousPageButton;
    public TextField numberOfUsers;
    @FXML
    private ListView<Message> listOfMessages;
    private ObservableList<Message> messages = FXCollections.observableArrayList();

    @FXML
    private ListView<UserFriend> friendsChatList;
    private ObservableList<UserFriend> friendsChat = FXCollections.observableArrayList();
    private ObservableList<RequestedFriend> requestedFriends = FXCollections.observableArrayList();
    @FXML
    public TableView<RequestedFriend> sentFriendRequestsTable;
    @FXML
    public TableColumn<RequestedFriend, String> firstNameSentFriendRequestsColumn;
    @FXML
    public TableColumn<RequestedFriend, String> lastNameSentFriendRequestsColumn;
    @FXML
    public TableColumn<RequestedFriend, String> statusSentFriendRequestsColumn;
    @FXML
    private TableColumn<UserFriend, String> lastNameColumn;

    @FXML
    private TableColumn<UserFriend, LocalDate> dateColumn;
    @FXML
    private TableColumn<UserFriend, String> firstNameColumn;

    private Stage userInterfaceStage;
    @FXML
    private ListView<UserFriend> listOtherUsers;
    private ObservableList<UserFriend> users = FXCollections.observableArrayList();
    private String currentUserEmail;
    private Long currentUserId;
    @FXML
    private TableView<UserFriend> tableOfFriends;
    private ObservableList<UserFriend> friends = FXCollections.observableArrayList();

    private ObservableList<RequestingFriend> requestingFriends = FXCollections.observableArrayList();

    private Socket clientSocket;

    private int currentPageIndex = 0; // Pagina curentă
    private int numberOfUsersToBeShown = 5;
    private int sizeOfUsers;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        sizeOfUsers = serviceUser.getSizeOfUsers();
        //seteaza proprietatea de reflexie a fiecarei celule pentru fiecare tabel in parte

        //tableOfFriends
        firstNameColumn.setCellValueFactory(new PropertyValueFactory<>("firstName"));
        lastNameColumn.setCellValueFactory(new PropertyValueFactory<>("lastName"));
        dateColumn.setCellValueFactory(new PropertyValueFactory<>("date"));

        //sentFriendRequestsTable
        firstNameSentFriendRequestsColumn.setCellValueFactory(new PropertyValueFactory<>("firstName"));
        lastNameSentFriendRequestsColumn.setCellValueFactory(new PropertyValueFactory<>("lastName"));
        statusSentFriendRequestsColumn.setCellValueFactory(new PropertyValueFactory<>("status"));

        //pendingFriendRequestsTable
        firstNamePendingReqColumn.setCellValueFactory(new PropertyValueFactory<>("firstName"));
        lastNamePendingReqColumn.setCellValueFactory(new PropertyValueFactory<>("lastName"));

        //set buttons
        Image imageDeclined = new Image("https://as2.ftcdn.net/v2/jpg/05/68/19/53/1000_F_568195350_frsM0j9iPnBBdHJyCyOAWdE9EZsR93cn.jpg");
        ImageView imageView = new ImageView(imageDeclined);
        imageView.setFitHeight(40); // Set your desired height
        imageView.setFitWidth(40); // Set your desired width
        declineFriendButton.setGraphic(imageView);

        Image imageAccepted = new Image("https://png.pngtree.com/png-vector/20190228/ourmid/pngtree-check-mark-icon-design-template-vector-isolated-png-image_711433.jpg");
        ImageView imageView2 = new ImageView(imageAccepted);
        imageView2.setFitHeight(40); // Set your desired height
        imageView2.setFitWidth(40); // Set your desired width
        acceptFriendButton.setGraphic(imageView2);

        //adjust the cell factory for the list of messages
        // Assuming you have a ListView called listOfMessages and a Message class

        listOfMessages.setCellFactory(message -> new ListCell<>() {
            @Override
            protected void updateItem(Message item, boolean empty) {
                super.updateItem(item, empty);

                if (empty || item == null) {
                    setText(null);
                    setGraphic(null);
                } else {
                    //setText(item.toString());

                    Label label = new Label(item.toString());
                    label.setPadding(new Insets(5));

                    if (item.getFrom().equals(currentUserEmail)) {
                        label.setStyle("-fx-background-color: blue; -fx-background-radius: 15; -fx-text-fill: white;");
                        setAlignment(Pos.CENTER_RIGHT);
                    } else {
                        label.setStyle("-fx-background-color: #DCDCDC; -fx-background-radius: 15; -fx-text-fill: black;");
                        setAlignment(Pos.CENTER_LEFT);
                    }

                    setGraphic(label);
                }
            }
        });

        vBoxChat.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.ENTER) {
                sendMessageButton.fire();
            }
        });
        //set a white border to replyLabel
        replyLabel.setBorder(new Border(new BorderStroke(Color.WHITE,
                BorderStrokeStyle.SOLID, CornerRadii.EMPTY, BorderWidths.DEFAULT)));
        //when hover make the text responsive by making it bigger
        replyLabel.setOnMouseEntered(event -> {
            replyLabel.setScaleX(1.1);
            replyLabel.setScaleY(1.1);
        });


        replyLabel.setOnMouseExited(event -> {
            replyLabel.setScaleX(1);
            replyLabel.setScaleY(1);
        });

        replyLabel.setVisible(false);
        replyToMessage();


    }

    public void setUserInterfaceStage(Stage userIntefaceStage) {
        this.userInterfaceStage = userIntefaceStage;
    }

    public void setCurrentUserEmail(String currentUserEmail) {
        this.currentUserEmail = currentUserEmail;
        this.currentUserId = serviceUser.getIdByEmail(currentUserEmail);

        try {
            this.clientSocket = new Socket("localhost", 12345);
            BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true);
            out.println(this.currentUserEmail);

            Thread listenerThread = new Thread(() -> {
                try {
                    while (true) {
                        String message;
                        while ((message = in.readLine()) != null) {
                            if (message.equals("Load Pending Table")) {
                                // Actualizează interfața grafică folosind Platform.runLater()
                                Platform.runLater(this::loadPendingFriendRequests);
                            } else if (message.equals("Load messages")) {
                                String emailTrimitatorMesaj = in.readLine();
                                if (!currentUserEmail.equals(emailTrimitatorMesaj)) {
                                    Platform.runLater(() -> loadChatWithMessages2(emailTrimitatorMesaj));
                                }


                            }
                        }
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });

            listenerThread.start();

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void showCurrentUser(String email) {
        setCurrentUserEmail(email); // set the current email of the user so that I can send to repo to not display that unique email
        // in the list of users
        Optional<Utilizator> currentUserToBeChecked = serviceUser.getUserByEmail(email);
        if (currentUserToBeChecked.isPresent()) {
            Utilizator currentUser = currentUserToBeChecked.get();
            labelUserLogged.setText(currentUser.getFirstName() + " " + currentUser.getLastName());

        }
        numberOfUsers.setOnKeyPressed(event -> {
            if(event.getCode() == KeyCode.ENTER)
            {
                numberOfUsersToBeShown = Integer.parseInt(numberOfUsers.getText());
                loadUsersToListPaginated();
                numberOfUsers.clear();
            }
        });
        loadFriendsToList();
        loadSentFriendRequests();
        loadPendingFriendRequests();
        loadFriendsToChatList();
        loadChatWithMessages();
    }

    private void loadFriendsToChatList() {
        friendsChatList.getItems().clear();
        friendsChat.clear();
        friendsChat.addAll(serviceFriend.getAllFriendsOfUserWithEmail(currentUserEmail));
        friendsChatList.setItems(friendsChat);

    }

    public void loadPendingFriendRequests() {
        pendingRequestsTable.getItems().clear();
        requestingFriends.clear();
        requestingFriends.addAll(servicePendingFriends.findAllRequestingFriends(currentUserId));
        pendingRequestsTable.setItems(requestingFriends);

    }

    public void logOutUser(ActionEvent actionEvent) throws IOException {
        this.userInterfaceStage.close();

        FXMLLoader fxmlLoaderLoginSignup = new FXMLLoader(HelloApplication.class.getResource("login-view.fxml"));
        Scene sceneLogin = new Scene(fxmlLoaderLoginSignup.load(), 1100, 615);
        LoginController loginController = fxmlLoaderLoginSignup.getController();
        Stage logInStage = new Stage();
        loginController.setStage(logInStage);
        logInStage.setTitle("Login");
        logInStage.setScene(sceneLogin);
        logInStage.show();
    }

    public void loadUsersToList() {
        users.addAll(serviceUser.getNamesOfOtherUsers(this.currentUserEmail));
        listOtherUsers.setItems(users);
    }

    public void loadUsersToListPaginated() {
        listOtherUsers.getItems().clear();
        users.clear();
        users.addAll(serviceUser.getUsersPaginated(currentUserEmail, numberOfUsersToBeShown, currentPageIndex));
        listOtherUsers.setItems(users);
    }


    public void loadFriendsToList() {
        tableOfFriends.getItems().clear();
        friends.clear();
        friends.addAll(serviceFriend.getAllFriendsOfUserWithEmail(currentUserEmail));
        tableOfFriends.setItems(friends);
    }

    public void sendFriendRequest(ActionEvent actionEvent) {

        //send friend request
        try {
            UserFriend userSelected = listOtherUsers.getSelectionModel().getSelectedItem();
            String emailDesiredFriend = userSelected.getEmail();
            servicePendingFriends.addPendingFriendship(currentUserId, serviceUser.getIdByEmail(emailDesiredFriend));
            loadSentFriendRequests();

            //send signal to server
            try {
                PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true);
                out.println("Update Pending Friend Request");
                out.println(emailDesiredFriend);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        } catch (NullPointerException npe) {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("No friend Selected!");
            alert.setHeaderText(null);
            DialogPane dialogPane = alert.getDialogPane();

            VBox vbox = new VBox();

            Label label = new Label("Please select a friend!");
            label.setStyle("-fx-text-fill: red;");

            ImageView imageView = new ImageView(new Image("D:\\school\\MAP\\demo\\src\\main\\java\\images\\exception.jpg")); // Adaugă o imagine personalizată, dacă dorești
            imageView.setFitHeight(100);
            imageView.setFitWidth(100);

            vbox.getChildren().addAll(label, imageView);
            dialogPane.setContent(vbox);
            alert.showAndWait();
        }

    }

    private void loadSentFriendRequests() {
        sentFriendRequestsTable.getItems().clear();
        requestedFriends.clear();
        requestedFriends.addAll(servicePendingFriends.findAllMyRequestedFriends(currentUserId));
        sentFriendRequestsTable.setItems(requestedFriends);
    }


    public void deleteFriend(ActionEvent actionEvent) {
        UserFriend userSelected = tableOfFriends.getSelectionModel().getSelectedItem();
        String email = userSelected.getEmail();
        serviceFriend.removeFriendship(currentUserId, serviceUser.getIdByEmail(email));
        loadFriendsToList();
    }

    public void acceptFriend(ActionEvent actionEvent) {
        //selecteaza prietenul din tabelul pendingRequests
        //updateaza statusul prietenului ca fiind acceptat
        //adauga prietenul in lista de prieteni
        try {
            RequestingFriend requestingFriend = pendingRequestsTable.getSelectionModel().getSelectedItem();
            serviceFriend.addFriendship(currentUserId, serviceUser.getIdByEmail(requestingFriend.getEmail()));
            loadFriendsToList();
            servicePendingFriends.updateStatusToApproved(currentUserId, serviceUser.getIdByEmail(requestingFriend.getEmail()));
            loadSentFriendRequests();
            loadPendingFriendRequests();
            loadFriendsToChatList();
        } catch (NullPointerException e) {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("No friend Selected!");
            alert.setHeaderText(null);
            DialogPane dialogPane = alert.getDialogPane();

            VBox vbox = new VBox();

            Label label = new Label("Please select a friend!");
            label.setStyle("-fx-text-fill: red;");

            ImageView imageView = new ImageView(new Image("D:\\school\\MAP\\demo\\src\\main\\java\\images\\exception.jpg")); // Adaugă o imagine personalizată, dacă dorești
            imageView.setFitHeight(100);
            imageView.setFitWidth(100);

            vbox.getChildren().addAll(label, imageView);
            dialogPane.setContent(vbox);
            alert.showAndWait();
        }

    }

    public void declineFriend(ActionEvent actionEvent) {
        try {
            RequestingFriend requestingFriend = pendingRequestsTable.getSelectionModel().getSelectedItem();
            serviceFriend.removeFriendship(currentUserId, serviceUser.getIdByEmail(requestingFriend.getEmail()));
            loadFriendsToList();
            servicePendingFriends.updateStatusToDeclined(currentUserId, serviceUser.getIdByEmail(requestingFriend.getEmail()));
            loadSentFriendRequests();
            loadPendingFriendRequests();
        } catch (NullPointerException e) {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("No friend Selected!");
            alert.setHeaderText(null);
            DialogPane dialogPane = alert.getDialogPane();

            VBox vbox = new VBox();

            Label label = new Label("Please select a friend!");
            label.setStyle("-fx-text-fill: red;");

            ImageView imageView = new ImageView(new Image("D:\\school\\MAP\\demo\\src\\main\\java\\images\\exception.jpg")); // Adaugă o imagine personalizată, dacă dorești
            imageView.setFitHeight(100);
            imageView.setFitWidth(100);

            vbox.getChildren().addAll(label, imageView);
            dialogPane.setContent(vbox);
            alert.showAndWait();
        }
    }

    private void loadChatWithMessages() {
        friendsChatList.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            listOfMessages.getItems().clear();
            messages.clear();
            messages.addAll(serviceMessages.getAllMessagesOfUsers(currentUserEmail, newValue.getEmail()));
            listOfMessages.setItems(messages);
            listOfMessages.scrollTo(listOfMessages.getItems().size() - 1);
        });
    }

    private void loadChatWithMessages2(String emailOfChatter) {

        listOfMessages.getItems().clear();
        messages.clear();
        messages.addAll(serviceMessages.getAllMessagesOfUsers(currentUserEmail, emailOfChatter));
        listOfMessages.setItems(messages);
        listOfMessages.scrollTo(listOfMessages.getItems().size() - 1);


    }


    public void sendMessageToUser(ActionEvent actionEvent) {

        if (messageTextField.getText() != null && !messageTextField.getText().isEmpty()) {

            String mesaj = messageTextField.getText();
            String toUserWithEmail = friendsChatList.getSelectionModel().getSelectedItem().getEmail();
            Message mesajNou = new Message(currentUserEmail, toUserWithEmail, mesaj);
            serviceMessages.save(mesajNou);
            messages.add(mesajNou);
            listOfMessages.setItems(messages);
            messageTextField.clear();
            listOfMessages.scrollTo(listOfMessages.getItems().size() - 1);


            //send signal to server to update the list of messages of the wanted chat user
            try {
                PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true);
                out.println("Update Messages");
                //eu am trimis semnalul ca sa stii cu cine va trebui sa updatezi mesajele
                out.println(currentUserEmail);
                //updateaza mesajele clientului cu emailul:
                out.println(toUserWithEmail);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }


    private void replyToMessage() {
        listOfMessages.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) ->
        {
            if (newValue != null) {
                replyLabel.setVisible(true);
                replyLabel.setOnMouseClicked(event ->
                {
                    replyMessageToUser();
                });
            }
        });

    }

    private void replyMessageToUser() {
        if (messageTextField.getText() != null && !messageTextField.getText().isEmpty()) {
            String mesaj = "Replied to:\n" + messageTextField.getText();
            String toUserWithEmail = friendsChatList.getSelectionModel().getSelectedItem().getEmail();
            Message mesajNou = new Message(currentUserEmail, toUserWithEmail, mesaj);
            serviceMessages.save(mesajNou);
            messages.add(mesajNou);
            listOfMessages.setItems(messages);
            messageTextField.clear();
            listOfMessages.scrollTo(listOfMessages.getItems().size() - 1);

            //send signal to server to update the list of messages of the wanted chat user
            try {
                PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true);
                out.println("Update Messages");
                //eu am trimis semnalul ca sa stii cu cine va trebui sa updatezi mesajele
                out.println(currentUserEmail);
                //updateaza mesajele clientului cu emailul:
                out.println(toUserWithEmail);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }


    public void previousPage(ActionEvent actionEvent) {
        if (currentPageIndex == 0) {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("There are no previous Users");
            alert.setHeaderText(null);
            DialogPane dialogPane = alert.getDialogPane();

            VBox vbox = new VBox();

            Label label = new Label("Try going to the next page!");
            label.setStyle("-fx-text-fill: red;");

            ImageView imageView = new ImageView(new Image("D:\\school\\MAP\\demo\\src\\main\\java\\images\\exception.jpg")); // Adaugă o imagine personalizată, dacă dorești
            imageView.setFitHeight(100);
            imageView.setFitWidth(100);

            vbox.getChildren().addAll(label, imageView);
            dialogPane.setContent(vbox);
            alert.showAndWait();
        } else {
            currentPageIndex--;
            loadUsersToListPaginated();
        }
    }

    public void nextPage(ActionEvent actionEvent) {
        if ((currentPageIndex + 1) * numberOfUsersToBeShown > sizeOfUsers) {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("There are no users left");
            alert.setHeaderText(null);
            DialogPane dialogPane = alert.getDialogPane();

            VBox vbox = new VBox();

            Label label = new Label("Try going to the previous page!");
            label.setStyle("-fx-text-fill: red;");

            ImageView imageView = new ImageView(new Image("D:\\school\\MAP\\demo\\src\\main\\java\\images\\exception.jpg")); // Adaugă o imagine personalizată, dacă dorești
            imageView.setFitHeight(100);
            imageView.setFitWidth(100);

            vbox.getChildren().addAll(label, imageView);
            dialogPane.setContent(vbox);
            alert.showAndWait();
        } else {
            currentPageIndex++;
            loadUsersToListPaginated();
        }
    }
}
