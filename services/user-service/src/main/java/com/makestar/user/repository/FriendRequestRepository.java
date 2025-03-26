package com.makestar.user.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.makestar.commons.model.FriendRequest;
import com.makestar.commons.model.User;

import java.util.List;
import java.util.Optional;

/**
 * 친구 요청 정보에 대한 데이터베이스 접근을 담당하는 리포지토리 인터페이스입니다.
 * JpaRepository를 상속받아 기본적인 CRUD 작업과 페이징, 정렬 기능을 제공합니다.
 */
@Repository
public interface FriendRequestRepository extends JpaRepository<FriendRequest, String> {
    
    /**
     * 특정 사용자가 보낸 모든 친구 요청을 조회합니다.
     * @param sender 친구 요청을 보낸 사용자
     * @return 해당 사용자가 보낸 친구 요청 목록
     */
    List<FriendRequest> findBySender(User sender);
    
    /**
     * 특정 사용자가 받은 모든 친구 요청을 조회합니다.
     * @param receiver 친구 요청을 받은 사용자
     * @return 해당 사용자가 받은 친구 요청 목록
     */
    List<FriendRequest> findByReceiver(User receiver);
    
    /**
     * 특정 사용자가 보낸 친구 요청들 중 특정 상태의 요청들을 조회합니다.
     * @param sender 친구 요청을 보낸 사용자
     * @param status 조회할 친구 요청 상태
     * @return 조건에 맞는 친구 요청 목록
     */
    List<FriendRequest> findBySenderAndStatus(User sender, FriendRequest.FriendRequestStatus status);
    
    /**
     * 특정 사용자가 받은 친구 요청들 중 특정 상태의 요청들을 조회합니다.
     * @param receiver 친구 요청을 받은 사용자
     * @param status 조회할 친구 요청 상태
     * @return 조건에 맞는 친구 요청 목록
     */
    List<FriendRequest> findByReceiverAndStatus(User receiver, FriendRequest.FriendRequestStatus status);
    
    /**
     * 두 사용자 간의 친구 요청을 조회합니다.
     * @param sender 친구 요청을 보낸 사용자
     * @param receiver 친구 요청을 받은 사용자
     * @return 두 사용자 간의 친구 요청 (Optional)
     */
    Optional<FriendRequest> findBySenderAndReceiver(User sender, User receiver);
    
    /**
     * 두 사용자 ID 간의 친구 요청을 조회합니다.
     * 요청자와 수신자의 순서에 관계없이 두 사용자 간의 친구 요청을 찾습니다.
     * 
     * @param userId1 첫 번째 사용자 ID
     * @param userId2 두 번째 사용자 ID
     * @return 두 사용자 간의 친구 요청 (Optional)
     */
    @Query("SELECT fr FROM FriendRequest fr WHERE " +
           "(fr.sender.id = :userId1 AND fr.receiver.id = :userId2) OR " +
           "(fr.sender.id = :userId2 AND fr.receiver.id = :userId1)")
    Optional<FriendRequest> findBetweenUsers(@Param("userId1") String userId1, @Param("userId2") String userId2);
    
    /**
     * 두 사용자 간의 친구 요청이 존재하는지 확인합니다.
     * @param sender 친구 요청을 보낸 사용자
     * @param receiver 친구 요청을 받은 사용자
     * @return 친구 요청 존재 여부
     */
    boolean existsBySenderAndReceiver(User sender, User receiver);
} 