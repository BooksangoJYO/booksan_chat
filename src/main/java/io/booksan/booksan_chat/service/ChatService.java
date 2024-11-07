package io.booksan.booksan_chat.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.stereotype.Service;

import io.booksan.booksan_chat.dao.ChatDAO;
import io.booksan.booksan_chat.dto.AlarmMessageDTO;
import io.booksan.booksan_chat.dto.ChatMessageDTO;
import io.booksan.booksan_chat.entity.ChatRoom;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class ChatService {

    private final ChatDAO chatDAO;
    private final ChatRoomService chatRoomService;
    @Autowired
    private SimpMessageSendingOperations messagingTemplate;
    private Map<String, List<ChatMessageDTO>> messageMap = new HashMap<>();

    public List<ChatRoom> findAllRoom() {
        return chatRoomService.findAllRoom();
    }

    public List<ChatRoom> findRoomByEmail(String email) {
        return chatRoomService.findRoomByEmail(email);
    }

    public ChatRoom createChatRoom(String name, String email, String writerEmail) {

        ChatRoom chatRoom = chatRoomService.createChatRoom(name, email, writerEmail);
        //채팅방의 사용자수 변경을 알린다 
        messagingTemplate.convertAndSend("/sub/alarm", new AlarmMessageDTO(""));
        log.info("*** alarm message send***");
        return chatRoom;
    }

    public ChatRoom findRoomByRoomId(String roomId) {
        return chatRoomService.findRoomByRoomId(roomId);
    }

    public List<ChatRoom> findRoomByUid(String email) {
        return chatRoomService.findRoomByEmail(email);
    }

    public void userEnterChatRoomUser(String simpDestination, String email) {

        ChatRoom chatRoom = chatRoomService.userEnterChatRoomUser(simpDestination, email);
        String sender = chatDAO.getNicknameByEmail(email);
        if (chatRoom != null) {
            if (!chatRoom.isExistEmail(email)) {
                log.info("***personal message***");
                //서버에서 클라이언트로 구독 메시지를 전달한다 
                //채팅방의 입장한 모든 사용자에게 입장으로 알린다
                messagingTemplate.convertAndSend("/sub/chat/room/" + chatRoom.getRoomId(),
                        ChatMessageDTO.enterMessage(chatRoom, sender));

                //채팅방의 사용자수 변경을 알린다 
                messagingTemplate.convertAndSend("/sub/alarm", new AlarmMessageDTO(chatRoom.getRoomId()));
            } else {
                // // 개인에게만 이전 메시지 전송 (convertAndSendToUser 사용)
                // log.info("***private message***");
                // messagingTemplate.convertAndSendToUser(
                //         email, // 특정 사용자
                //         "/sub/chat/room/" + chatRoom.getRoomId(),
                //         ChatMessageDTO.leaveMessage(chatRoom, sender)
                // );
            }
        }
    }

    public void userLeaveChatRoomUser(String roomId, String email) {
        ChatRoom chatRoom = chatRoomService.findRoomByRoomId(roomId);
        String sender = chatDAO.getNicknameByEmail(email);
        if (chatRoom != null) {
            chatRoomService.userLeaveChatRoomUser(roomId, email);
            //서버에서 클라이언트로 구독 메시지를 전달한다 
            //채팅방의 입장한 모든 사용자에게 퇴장으로 알린다
            messagingTemplate.convertAndSend("/sub/chat/room/" + chatRoom.getRoomId(),
                    ChatMessageDTO.leaveMessage(chatRoom, sender));
        }
        //채팅방의 사용자수 변경을 알린다 
        messagingTemplate.convertAndSend("/sub/alarm", new AlarmMessageDTO(""));
    }

    public void saveMessage(ChatMessageDTO message) {
        if (messageMap.containsKey(message.getRoomId())) {
            messageMap.get(message.getRoomId()).add(message);
        } else {
            List<ChatMessageDTO> messageList = new ArrayList<>();
            messageList.add(message);
            messageMap.put(message.getRoomId(), messageList);
        }
    }

    public List<ChatMessageDTO> getMessage(String roomId) {
        return messageMap.get(roomId);
    }
}
