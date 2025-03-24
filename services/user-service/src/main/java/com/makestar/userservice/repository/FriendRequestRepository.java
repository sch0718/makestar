package com.makestar.userservice.repository;

import com.makestar.userservice.model.FriendRequest;
import com.makestar.userservice.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface FriendRequestRepository extends JpaRepository<FriendRequest, String> {
    
    List<FriendRequest> findBySender(User sender);
    
    List<FriendRequest> findByReceiver(User receiver);
    
    List<FriendRequest> findBySenderAndStatus(User sender, FriendRequest.FriendRequestStatus status);
    
    List<FriendRequest> findByReceiverAndStatus(User receiver, FriendRequest.FriendRequestStatus status);
    
    Optional<FriendRequest> findBySenderAndReceiver(User sender, User receiver);
    
    @Query("SELECT fr FROM FriendRequest fr WHERE " +
           "(fr.sender.id = :userId1 AND fr.receiver.id = :userId2) OR " +
           "(fr.sender.id = :userId2 AND fr.receiver.id = :userId1)")
    Optional<FriendRequest> findBetweenUsers(@Param("userId1") String userId1, @Param("userId2") String userId2);
    
    boolean existsBySenderAndReceiver(User sender, User receiver);
} 