package io.booksan.booksan_chat.service;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;

import io.booksan.booksan_chat.dao.ChatDAO;
import io.booksan.booksan_chat.entity.ChatRoom;
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
        }
        return chatRoom;
    }

    // public ChatRoom findRoomSessionId(String sessionId) {
    //     for (Entry<String, ChatRoom> entry : chatRoomMap.entrySet()) {
    //         if (entry.getValue().isExistSessionId(sessionId)) {
    //             return entry.getValue();
    //         }
    //     }
    //     return null;
    // }
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
