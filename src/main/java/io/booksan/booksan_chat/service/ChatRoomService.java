package io.booksan.booksan_chat.service;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Collectors;

import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;

import io.booksan.booksan_chat.dao.ChatDAO;
import io.booksan.booksan_chat.entity.ChatRoom;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class ChatRoomService {
    private final ChatDAO chatDAO;

    private final String subChatRoomPath = "/sub/chat/room/";
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

    public ChatRoom findRoomById(String id) {
        return chatRoomMap.get(id);
    }

    public ChatRoom createChatRoom(String name) {
        ChatRoom chatRoom = new ChatRoom(name);
        chatRoomMap.put(chatRoom.getRoomId(), chatRoom);
        return chatRoom;
    }

    //사용자가 채팅방에 입장한다
    public ChatRoom userEnterChatRoomUser(String path, String sessionId, String sender) {
        String newRoomId = path.substring(subChatRoomPath.length());
        ChatRoom chatRoom = chatRoomMap.get(newRoomId);
        if (chatRoom != null) {
            chatRoom.addSession(sessionId, sender);
        }
        return chatRoom;
    }

    public ChatRoom findRoomSessionId(String sessionId) {
        for (Entry<String, ChatRoom> entry : chatRoomMap.entrySet()) {
            if (entry.getValue().isExistSessionId(sessionId)) {
                return entry.getValue();
            }
        }
        return null;
    }

    //사용자가 채팅방에서 퇴장한다
    public void userLeaveChatRoomUser(String sessionId) {
        ChatRoom chatRoom = findRoomSessionId(sessionId);
        if (chatRoom != null) {
            chatRoom.removeSession(sessionId);
        }
    }
}
