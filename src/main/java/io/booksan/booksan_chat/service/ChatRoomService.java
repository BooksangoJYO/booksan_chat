package io.booksan.booksan_chat.service;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;

import io.booksan.booksan_chat.dao.ChatDAO;
import io.booksan.booksan_chat.entity.ChatAlertEntity;
import io.booksan.booksan_chat.entity.ChatRoom;
import io.booksan.booksan_chat.entity.ReadMessageEntity;
import io.booksan.booksan_chat.vo.ChatRoomVO;
import io.booksan.booksan_chat.vo.UserChatRoomVO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class ChatRoomService {

    private final ChatDAO chatDAO;

    private final String subChatRoomPath = "/sub/chat/room/";
    private final String userSubChatRoomPath = "/user/sub/chat/room/";
    private Map<String, ChatRoom> chatRoomMap;

    @EventListener(ContextRefreshedEvent.class)
    public void init() {
        chatRoomMap = new LinkedHashMap<>();
        List<ChatRoomVO> allRoomList = chatDAO.findAllRoom();
        for (ChatRoomVO chatRoomVO : allRoomList) {
            Map<String, String> userMap = new HashMap<>();
            List<UserChatRoomVO> userList = chatDAO.findUsersByRoomId(chatRoomVO.getRoomId());
            for (UserChatRoomVO userChatRoomVO : userList) {
                userMap.put(userChatRoomVO.getEmail(), userChatRoomVO.getUserType());
            }
            ChatRoom chatRoom = new ChatRoom(chatRoomVO.getRoomId(), chatRoomVO.getName(), chatRoomVO.getDealId(), userMap);
            chatRoomMap.put(chatRoom.getRoomId(), chatRoom);
        }
    }

    public List<ChatRoom> findAllRoom() {
        // 채팅방 이름 순으로 반환
        log.info("채팅방에 대한 정보" + chatRoomMap.toString());
        return chatRoomMap.values().stream().sorted().collect(Collectors.toList());
    }

    public ChatRoom findRoomByRoomId(String roomId) {
        return chatRoomMap.get(roomId);
    }

    public List<ChatRoom> findRoomByEmail(String email) {
        return chatRoomMap.values().stream()
                .filter(chatRoom -> chatRoom.isExistEmail(email))
                .collect(Collectors.toList());
    }

    //게시글작성자와 연결하기 위한 채팅방 생성
    public ChatRoom createChatRoom(ChatRoomVO chatRoomVO, String email, String writerEmail) {
        Optional<ChatRoom> existingRoom = findCommonChatRoom(chatRoomMap, email, writerEmail, chatRoomVO.getName());
        if (existingRoom.isPresent()) {
            return existingRoom.get();
        } else {
            ChatRoom chatRoom = new ChatRoom(chatRoomVO.getName());
            chatRoom.setDealId(chatRoomVO.getDealId());
            chatRoomMap.put(chatRoom.getRoomId(), chatRoom);
            //만들어진 채팅방정보를 토대로 게시글 작성자를 초대
            chatRoom.addUser(email, "customer");
            chatRoom.addUser(writerEmail, "seller");
            //채팅방 정보를 db에저장
            UserChatRoomVO userChatRoomVO = new UserChatRoomVO();
            chatRoomVO.setRoomId(chatRoom.getRoomId());
            chatDAO.insertChatRoom(chatRoomVO);
            userChatRoomVO.setRoomId(chatRoom.getRoomId());
            //구매자 정보
            userChatRoomVO.setEmail(email);
            userChatRoomVO.setUserType("customer");
            chatDAO.insertUserChatRoom(userChatRoomVO);
            //판매자 정보
            userChatRoomVO.setEmail(writerEmail);
            userChatRoomVO.setUserType("seller");
            chatDAO.insertUserChatRoom(userChatRoomVO);

            return chatRoom;
        }
    }

    public Optional<ChatRoom> findCommonChatRoom(Map<String, ChatRoom> chatRoomMap,
            String user1Email,
            String user2Email,
            String roomName) {
        return chatRoomMap.values().stream()
                .filter(room -> room.getUserMap().keySet().contains(user1Email)
                && room.getUserMap().keySet().contains(user2Email)
                && room.getName().equals(roomName))
                .findAny();
    }

    //사용자가 채팅방에 입장한다
    public ChatRoom userEnterChatRoomUser(String path, String email) {;
        String newRoomId;
        if (path.startsWith("/user")) {
            newRoomId = path.substring(userSubChatRoomPath.length());
        } else {
            newRoomId = path.substring(subChatRoomPath.length());
        }
        ChatRoom chatRoom = chatRoomMap.get(newRoomId);
        if (chatRoom != null && !chatRoom.isExistEmail(email)) {
            chatRoom.addUser(email, "customer");
            UserChatRoomVO userChatRoomVO = new UserChatRoomVO();
            userChatRoomVO.setRoomId(chatRoom.getRoomId());
            userChatRoomVO.setEmail(email);
            userChatRoomVO.setUserType("customer");
            chatDAO.insertUserChatRoom(userChatRoomVO);
        }
        return chatRoom;
    }

    //사용자가 채팅방에서 퇴장한다
    public void userLeaveChatRoomUser(String roomId, String email) {
        ChatRoom chatRoom = findRoomByRoomId(roomId);
        if (chatRoom != null && chatRoom.getUserMap().keySet().contains(email)) {
            chatRoom.removeUser(email);
            UserChatRoomVO userChatRoomVO = new UserChatRoomVO();
            userChatRoomVO.setEmail(email);
            userChatRoomVO.setRoomId(roomId);
            chatDAO.deleteUserChatRoom(userChatRoomVO);
            if (chatRoom.getUserCount() == 0) {
                chatRoomMap.remove(roomId);
                chatDAO.deleteChatRoom(roomId);
            }

        }
    }

    public void insertReadMessage(String roomId, String email, int messageId) {
        ChatRoom chatRoom = chatRoomMap.get(roomId);
        if (chatRoom != null && chatRoom.getUserCount() != 0) {
            for (String userEmail : chatRoom.getUserMap().keySet()) {
                if (!userEmail.equals(email)) {
                    ReadMessageEntity readMessageEntity = new ReadMessageEntity();
                    readMessageEntity.setRoomId(roomId);
                    readMessageEntity.setMessageId(messageId);
                    readMessageEntity.setSender(email);
                    readMessageEntity.setReceiver(userEmail);
                    chatDAO.insertReadMessage(readMessageEntity);
                    chatDAO.updateChatAlert(new ChatAlertEntity(userEmail, "increase", 0));
                }
            }
        }
    }

    List<ChatRoomVO> getChatAlertRooms(String email) {
        return chatDAO.getChatAlertRooms(email);
    }

    List<ChatRoom> findRoomByDealId(String email, int dealId) {
        List<ChatRoom> list = chatRoomMap.values().stream()
                .filter(chatRoom -> chatRoom.getDealId() == dealId && chatRoom.getUserMap().keySet().contains(email))
                .collect(Collectors.toList());
        return list;
    }

}
