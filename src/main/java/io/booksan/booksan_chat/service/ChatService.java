package io.booksan.booksan_chat.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.stereotype.Service;

import io.booksan.booksan_chat.dto.AlarmMessageDTO;
import io.booksan.booksan_chat.dto.ChatMessageDTO;
import io.booksan.booksan_chat.entity.ChatRoom;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ChatService {
  private final ChatRoomService chatRoomService;
 @Autowired
    private SimpMessageSendingOperations messagingTemplate;
    
  
	public List<ChatRoom> findAllRoom() {
		return chatRoomService.findAllRoom();
	}

	public ChatRoom createChatRoom(String name) {
        
		ChatRoom chatRoom = chatRoomService.createChatRoom(name);
        //채팅방의 사용자수 변경을 알린다 
        messagingTemplate.convertAndSend("/sub/alarm", new AlarmMessageDTO(""));
            
        return chatRoom;
    }

	public ChatRoom findRoomById(String roomId) {
		return chatRoomService.findRoomById(roomId);
	}

	public void userEnterChatRoomUser(String simpDestination, String sessionId, String sender) {
		ChatRoom chatRoom = chatRoomService.userEnterChatRoomUser(simpDestination, sessionId, sender);
		if (chatRoom != null) {
	    //서버에서 클라이언트로 구독 메시지를 전달한다 
			//채팅방의 입장한 모든 사용자에게 입장으로 알린다
	    messagingTemplate.convertAndSend("/sub/chat/room/" + chatRoom.getRoomId()
	    	, ChatMessageDTO.enterMessage(chatRoom, sender));

	    //채팅방의 사용자수 변경을 알린다 
	    messagingTemplate.convertAndSend("/sub/alarm", new AlarmMessageDTO(chatRoom.getRoomId()));
		}
	}

	public void userLeaveChatRoomUser(String sessionId) {
		ChatRoom chatRoom = chatRoomService.findRoomSessionId(sessionId);
		if (chatRoom != null) {
			String sender = chatRoom.getSender(sessionId);
			System.out.println("userLeaveChatRoomUser() sender : " + sender);
			chatRoomService.userLeaveChatRoomUser(sessionId);
	    //서버에서 클라이언트로 구독 메시지를 전달한다 
			//채팅방의 입장한 모든 사용자에게 퇴장으로 알린다
	    messagingTemplate.convertAndSend("/sub/chat/room/" + chatRoom.getRoomId()
	    	, ChatMessageDTO.leaveMessage(chatRoom, sender));
		}
    //채팅방의 사용자수 변경을 알린다 
    messagingTemplate.convertAndSend("/sub/alarm", new AlarmMessageDTO(""));
	}
}
