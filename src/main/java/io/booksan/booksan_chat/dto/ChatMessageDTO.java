package io.booksan.booksan_chat.dto;

import io.booksan.booksan_chat.entity.ChatRoom;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ChatMessageDTO {
  // 메시지 타입 : 입장, 채팅, 나가기
  public enum MessageType {
      ENTER, TALK, LEAVE
  }

  private MessageType type; // 메시지 타입
  private String roomId; // 방번호
  private String sender; // 메시지 보낸사람
  private String message; // 메시지
  
  //입장 메시지 객체를 생성한다 
  public static ChatMessageDTO enterMessage(ChatRoom chatRoom, String sender) {
  	ChatMessageDTO result = ChatMessageDTO
			  		.builder()
			  		.type(MessageType.ENTER)
			  		.roomId(chatRoom.getRoomId())
			  		.sender(sender)
			  		.build();
  	result.typeProcess();
  	return result;
  }

  //입장 메시지 객체를 생성한다 
  public static ChatMessageDTO leaveMessage(ChatRoom chatRoom, String sender) {
  	ChatMessageDTO result = ChatMessageDTO
			  		.builder()
			  		.type(MessageType.LEAVE)
			  		.roomId(chatRoom.getRoomId())
			  		.sender(sender)
			  		.build();
  	result.typeProcess();
  	return result;
  }
  
  public void typeProcess() {
  	switch(type) {
  	case ENTER:
  		message = sender + "님이 입장하셨습니다.";
  		sender = "[알림]";
  		break;
  	case LEAVE:
  		message = sender + "님이 퇴장하셨습니다.";
  		sender = "[알림]";
  		break;
  	default:
  	}
  	
  }
}