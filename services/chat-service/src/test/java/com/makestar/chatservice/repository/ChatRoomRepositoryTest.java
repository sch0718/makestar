package test.java.com.makestar.chatservice.repository;

import com.makestar.chatservice.model.ChatRoom;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
public class ChatRoomRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private ChatRoomRepository chatRoomRepository;

    private ChatRoom groupChatRoom;
    private ChatRoom directChatRoom;
    private String userId1 = "user1";
    private String userId2 = "user2";
    private String userId3 = "user3";

    @BeforeEach
    public void setUp() {
        // 그룹 채팅방 생성
        groupChatRoom = ChatRoom.builder()
                .name("테스트 그룹 채팅방")
                .description("테스트용 그룹 채팅방입니다")
                .type(ChatRoom.ChatRoomType.GROUP)
                .build();
        
        groupChatRoom.addParticipant(userId1);
        groupChatRoom.addParticipant(userId2);
        groupChatRoom.addParticipant(userId3);
        entityManager.persist(groupChatRoom);

        // 1:1 채팅방 생성
        directChatRoom = ChatRoom.builder()
                .name("1:1 채팅방")
                .description("1:1 대화를 위한 채팅방입니다")
                .type(ChatRoom.ChatRoomType.DIRECT)
                .build();
        
        directChatRoom.addParticipant(userId1);
        directChatRoom.addParticipant(userId2);
        entityManager.persist(directChatRoom);

        entityManager.flush();
    }

    @Test
    @DisplayName("채팅방 저장 및 ID로 조회 테스트")
    public void testSaveAndFindById() {
        // Given
        ChatRoom newChatRoom = ChatRoom.builder()
                .name("새로운 채팅방")
                .description("새롭게 생성된 채팅방입니다")
                .type(ChatRoom.ChatRoomType.GROUP)
                .build();
        newChatRoom.addParticipant(userId1);

        // When
        ChatRoom savedChatRoom = chatRoomRepository.save(newChatRoom);
        Optional<ChatRoom> foundChatRoom = chatRoomRepository.findById(savedChatRoom.getId());

        // Then
        assertThat(foundChatRoom).isPresent();
        assertThat(foundChatRoom.get().getName()).isEqualTo("새로운 채팅방");
        assertThat(foundChatRoom.get().getDescription()).isEqualTo("새롭게 생성된 채팅방입니다");
        assertThat(foundChatRoom.get().getType()).isEqualTo(ChatRoom.ChatRoomType.GROUP);
        assertThat(foundChatRoom.get().getParticipantIds()).contains(userId1);
    }

    @Test
    @DisplayName("모든 채팅방 조회 테스트")
    public void testFindAll() {
        // When
        List<ChatRoom> allChatRooms = chatRoomRepository.findAll();

        // Then
        assertThat(allChatRooms).hasSize(2);
        assertThat(allChatRooms).extracting(ChatRoom::getName)
                .containsExactlyInAnyOrder("테스트 그룹 채팅방", "1:1 채팅방");
    }

    @Test
    @DisplayName("사용자가 참여한 채팅방 목록 조회 테스트")
    public void testFindChatRoomsByParticipantId() {
        // When
        List<ChatRoom> user1ChatRooms = chatRoomRepository.findChatRoomsByParticipantId(userId1);
        List<ChatRoom> user2ChatRooms = chatRoomRepository.findChatRoomsByParticipantId(userId2);
        List<ChatRoom> user3ChatRooms = chatRoomRepository.findChatRoomsByParticipantId(userId3);

        // Then
        assertThat(user1ChatRooms).hasSize(2);
        assertThat(user2ChatRooms).hasSize(2);
        assertThat(user3ChatRooms).hasSize(1);
        
        assertThat(user1ChatRooms).extracting(ChatRoom::getId)
                .containsExactlyInAnyOrder(groupChatRoom.getId(), directChatRoom.getId());
        assertThat(user3ChatRooms).extracting(ChatRoom::getId)
                .containsExactly(groupChatRoom.getId());
    }

    @Test
    @DisplayName("1:1 채팅방 찾기 테스트")
    public void testFindDirectChatRoomBetweenUsers() {
        // When
        List<ChatRoom> directRooms = chatRoomRepository.findDirectChatRoomBetweenUsers(userId1, userId2);
        List<ChatRoom> noDirectRooms = chatRoomRepository.findDirectChatRoomBetweenUsers(userId1, userId3);

        // Then
        assertThat(directRooms).hasSize(1);
        assertThat(directRooms.get(0).getId()).isEqualTo(directChatRoom.getId());
        assertThat(directRooms.get(0).getType()).isEqualTo(ChatRoom.ChatRoomType.DIRECT);
        assertThat(directRooms.get(0).getParticipantIds()).hasSize(2);
        assertThat(directRooms.get(0).getParticipantIds()).containsExactlyInAnyOrder(userId1, userId2);
        
        assertThat(noDirectRooms).isEmpty();
    }

    @Test
    @DisplayName("채팅방 이름으로 검색 테스트")
    public void testFindByNameContainingIgnoreCase() {
        // When
        List<ChatRoom> roomsWithGroup = chatRoomRepository.findByNameContainingIgnoreCase("그룹");
        List<ChatRoom> roomsWithTest = chatRoomRepository.findByNameContainingIgnoreCase("테스트");
        List<ChatRoom> roomsWithChat = chatRoomRepository.findByNameContainingIgnoreCase("채팅");
        List<ChatRoom> roomsWithNonExisting = chatRoomRepository.findByNameContainingIgnoreCase("존재하지않음");

        // Then
        assertThat(roomsWithGroup).hasSize(1);
        assertThat(roomsWithGroup.get(0).getId()).isEqualTo(groupChatRoom.getId());
        
        assertThat(roomsWithTest).hasSize(1);
        assertThat(roomsWithTest.get(0).getId()).isEqualTo(groupChatRoom.getId());
        
        assertThat(roomsWithChat).hasSize(2);
        assertThat(roomsWithChat).extracting(ChatRoom::getId)
                .containsExactlyInAnyOrder(groupChatRoom.getId(), directChatRoom.getId());
        
        assertThat(roomsWithNonExisting).isEmpty();
    }

    @Test
    @DisplayName("채팅방 업데이트 테스트")
    public void testUpdateChatRoom() {
        // Given
        ChatRoom chatRoomToUpdate = chatRoomRepository.findById(groupChatRoom.getId()).get();
        chatRoomToUpdate.setName("업데이트된 채팅방");
        chatRoomToUpdate.setDescription("설명이 변경되었습니다");
        
        // When
        ChatRoom updatedChatRoom = chatRoomRepository.save(chatRoomToUpdate);
        
        // Then
        assertThat(updatedChatRoom.getName()).isEqualTo("업데이트된 채팅방");
        assertThat(updatedChatRoom.getDescription()).isEqualTo("설명이 변경되었습니다");
        
        // 데이터베이스에 반영되었는지 확인
        ChatRoom foundChatRoom = entityManager.find(ChatRoom.class, groupChatRoom.getId());
        assertThat(foundChatRoom.getName()).isEqualTo("업데이트된 채팅방");
        assertThat(foundChatRoom.getDescription()).isEqualTo("설명이 변경되었습니다");
    }

    @Test
    @DisplayName("채팅방 참가자 추가 및 제거 테스트")
    public void testAddAndRemoveParticipant() {
        // Given
        String newUserId = "user4";
        ChatRoom chatRoom = chatRoomRepository.findById(groupChatRoom.getId()).get();
        
        // When - 참가자 추가
        chatRoom.addParticipant(newUserId);
        chatRoomRepository.save(chatRoom);
        
        // Then
        ChatRoom updatedChatRoom = chatRoomRepository.findById(groupChatRoom.getId()).get();
        assertThat(updatedChatRoom.getParticipantIds()).contains(newUserId);
        assertThat(updatedChatRoom.hasParticipant(newUserId)).isTrue();
        
        // When - 참가자 제거
        updatedChatRoom.removeParticipant(userId3);
        chatRoomRepository.save(updatedChatRoom);
        
        // Then
        ChatRoom finalChatRoom = chatRoomRepository.findById(groupChatRoom.getId()).get();
        assertThat(finalChatRoom.getParticipantIds()).doesNotContain(userId3);
        assertThat(finalChatRoom.hasParticipant(userId3)).isFalse();
    }

    @Test
    @DisplayName("채팅방 삭제 테스트")
    public void testDeleteChatRoom() {
        // When
        chatRoomRepository.delete(groupChatRoom);
        
        // Then
        Optional<ChatRoom> deletedChatRoom = chatRoomRepository.findById(groupChatRoom.getId());
        assertThat(deletedChatRoom).isEmpty();
        
        // 다른 채팅방은 영향 받지 않음
        Optional<ChatRoom> otherChatRoom = chatRoomRepository.findById(directChatRoom.getId());
        assertThat(otherChatRoom).isPresent();
    }
} 