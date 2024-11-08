package io.booksan.booksan_chat.service;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;

import io.booksan.booksan_chat.dao.ChatDAO;
import io.booksan.booksan_chat.entity.ChatRoom;
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
                String email = chatDAO.getEmailbyUid(userChatRoomVO.getUid());
                userMap.put(userChatRoomVO.getUid(), email);
            }
            ChatRoom chatRoom = new ChatRoom(chatRoomVO.getRoomId(), chatRoomVO.getName(), userMap);
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
    public ChatRoom createChatRoom(String name, String email, String writerEmail) {
        ChatRoom chatRoom = new ChatRoom(name);
        chatRoomMap.put(chatRoom.getRoomId(), chatRoom);
        String uid = chatDAO.getUidByEmail(email);
        String writerUid = chatDAO.getUidByEmail(writerEmail);
        //만들어진 채팅방정보를 토대로 게시글 작성자를 초대
        chatRoom.addUser(uid, email);
        chatRoom.addUser(writerUid, writerEmail);

        //채팅방 정보를 db에저장
        ChatRoomVO chatRoomVO = new ChatRoomVO();
        UserChatRoomVO userChatRoomVO = new UserChatRoomVO();
        chatRoomVO.setRoomId(chatRoom.getRoomId());
        chatRoomVO.setName(name);
        chatDAO.insertChatRoom(chatRoomVO);
        userChatRoomVO.setRoomId(chatRoom.getRoomId());
        log.info("***user data11 ***" + uid);
        log.info("***user data22 ***" + writerUid);
        //구매자 정보 
        userChatRoomVO.setUid(uid);
        chatDAO.insertUserChatRoom(userChatRoomVO);
        //판매자 정보
        userChatRoomVO.setUid(writerUid);
        chatDAO.insertUserChatRoom(userChatRoomVO);

        return chatRoom;
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
            String uid = chatDAO.getUidByEmail(email);
            chatRoom.addUser(uid, email);
            UserChatRoomVO userChatRoomVO = new UserChatRoomVO();
            userChatRoomVO.setRoomId(chatRoom.getRoomId());
            userChatRoomVO.setUid(uid);
            chatDAO.insertUserChatRoom(userChatRoomVO);
        }
        return chatRoom;
    }

    //사용자가 채팅방에서 퇴장한다
    public void userLeaveChatRoomUser(String roomId, String email) {
        ChatRoom chatRoom = findRoomByRoomId(roomId);
        String uid = chatDAO.getUidByEmail(email);
        if (chatRoom != null) {
            chatRoom.removeUser(uid);
            UserChatRoomVO userChatRoomVO = new UserChatRoomVO();
            userChatRoomVO.setUid(uid);
            userChatRoomVO.setRoomId(roomId);
            chatDAO.deleteUserChatRoom(userChatRoomVO);
            if (chatRoom.getUserCount() == 0) {
                chatRoomMap.remove(roomId);
                chatDAO.deleteChatRoom(roomId);
            }

        }
    }

}
