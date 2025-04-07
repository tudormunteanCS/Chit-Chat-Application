package service;

import domain.RequestedFriend;
import domain.RequestingFriend;
import repository.PendingFriendsDBRepository;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.Collection;

public class ServicePendingFriends {
    private final PendingFriendsDBRepository pendingFriendsDBRepository = new PendingFriendsDBRepository ("jdbc:postgresql://localhost:5432/dbMAP", "postgres", "telefon99");

    public void addPendingFriendship(Long fromId, Long toId) {

        pendingFriendsDBRepository.save(fromId,toId);





    }

    public ArrayList<RequestedFriend> findAllMyRequestedFriends(long myId) {
        return pendingFriendsDBRepository.findAllMyRequestedFriends(myId);
    }

    public ArrayList<RequestingFriend> findAllRequestingFriends(Long myId) {
        return pendingFriendsDBRepository.findAllMyRequestingFriends(myId);
    }

    public void updateStatusToApproved(long idFriendReqGetter,long idFriendReqSender) {
        pendingFriendsDBRepository.updateStatusToApproved(idFriendReqGetter,idFriendReqSender);
    }
    public void updateStatusToDeclined(long idFriendReqGetter,long idFriendReqSender) {
        pendingFriendsDBRepository.updateStatusToDeclined(idFriendReqGetter,idFriendReqSender);
    }
}

