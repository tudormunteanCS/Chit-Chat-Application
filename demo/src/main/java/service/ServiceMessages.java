package service;

import domain.Message;
import repository.MessageDBRepository;
import repository.Repository;

import java.util.ArrayList;

public class ServiceMessages {
    private final MessageDBRepository messageDBRepository = new MessageDBRepository("jdbc:postgresql://localhost:5432/dbMAP", "postgres", "telefon99");
    public ArrayList<Message> getAllMessagesOfUsers(String currentUserEmail, String userToChatEmail) {
        return messageDBRepository.getAllMesagesOfUsers(currentUserEmail,userToChatEmail);
    }

    public void save(Message mesajNou) {
        messageDBRepository.save(mesajNou);
    }
}
