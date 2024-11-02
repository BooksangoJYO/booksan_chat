package io.booksan.booksan_chat.config;

import java.util.Map.Entry;

import org.springframework.context.annotation.Lazy;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.stereotype.Component;

import io.booksan.booksan_chat.service.ChatService;
import lombok.extern.slf4j.Slf4j;

/*
 * Stomp에 이해 발생되는 모든 상황에 대한 리스너를 의미한다 
 * 상황 : 
 * 	메시지 전송전,
 *  메시지 전송후,
 *  메시지 전송 완료 후
 *   
 * 	메시지 수신전,
 *  메시지 수신후,
 *  메시지 수신 완료 후
 *  
 * 인증관련 작업을 하려고 한다면 해당 클래스 메시지 전송전과 메시지 수신전에 코드를 추가하면 된다
 * 
 *  
 *  이번은 ChannelInterceptor의 구현체인 ChannelInterceptorAdapter을 상속 받아 구현본다 
 *  
 *  ChatRoomDAO에서 관리하는 ChatRoom 객체와 Sompt 연결을 관리하는 sessionId를 매칭하여 관할 수 있게 기능을 추가한다
 *  
 *  
 *  
 */

@Slf4j
@Component
public class StompHandler implements ChannelInterceptor {
  @Lazy
	private final ChatService chatService;

    // 생성자 주입
    public StompHandler(@Lazy ChatService chatService) {
        this.chatService = chatService;
    }
	
    @Override
    public Message<?> preSend(Message<?> message, MessageChannel channel) {
        StompHeaderAccessor accessor = StompHeaderAccessor.wrap(message);
        
        for (Entry<String, Object> entry : message.getHeaders().entrySet()) {
          log.info("preSend() header -> {}", entry); 
        }

        if (StompCommand.CONNECT == accessor.getCommand()) { // websocket 연결요청
            String jwtToken = accessor.getFirstNativeHeader("token");
            log.info("preSend() CONNECT message = {}", message);
            log.info("preSend() CONNECT channel = {}", channel);
            log.info("preSend() CONNECT accessor = {}", accessor);
            log.info("preSend() CONNECT jwtToken = {}", jwtToken);
        } else if (StompCommand.SUBSCRIBE == accessor.getCommand()) { // 채팅룸 구독요청
        	/*
        	 * 예]  header로 전달 되는 키와 값의 종류  
						simpMessageType : SUBSCRIBE
						stompCommand : SUBSCRIBE
						nativeHeaders : {id=[sub-0], destination=[/sub/chat/room/378de7a1-4ae9-42da-9f01-34d311e1c66c]}
						simpSessionAttributes : {}
						simpHeartbeat : [J@43300a0f
						simpSubscriptionId : sub-0
						simpSessionId : 0ecozkbs
						simpDestination : /sub/chat/room/378de7a1-4ae9-42da-9f01-34d311e1c66c
						
						//구독시 추가 헤더함(사용자 이름)  
							userName : 홍길동
            // header정보에서 구독 destination정보를 얻어 채팅방에 사용자 입장
          */ 
        	String simpDestination = (String) accessor.getHeader("simpDestination");
        	String simpSessionId = (String) accessor.getHeader("simpSessionId");
        	String sender = (String) accessor.getFirstNativeHeader("sender");
        	System.out.println("sender = " + sender);
          if (simpDestination.contains("/sub/chat/room")) {
          	chatService.userEnterChatRoomUser(simpDestination, simpSessionId, sender);
          }
        } else if (StompCommand.UNSUBSCRIBE == accessor.getCommand()) { //메시지 구독 취소 //채팅방에서 나감 
        	/*
        	 * 예]  header로 전달 되는 키와 값의 종류  
        		simpMessageType : UNSUBSCRIBE
        		stompCommand : UNSUBSCRIBE
        		nativeHeaders : {id=[sub-0]}
        		simpSessionAttributes : {}
        		simpHeartbeat : [J@1e0982e5
        		simpSubscriptionId : sub-0
        		simpSessionId : sxepxewb
        		*/        	
        	
        	String simpSessionId = (String) accessor.getHeader("simpSessionId");
        	chatService.userLeaveChatRoomUser(simpSessionId);
        	
        } else if (StompCommand.DISCONNECT == accessor.getCommand()) { // Websocket 연결 종료
            // 연결이 종료된 클라이언트 sesssionId로 채팅방 id를 얻는다.
            String sessionId = (String) message.getHeaders().get("simpSessionId");
            log.info("preSend() DISCONNECTED sessionId = {}", sessionId);
            log.info("preSend() DISCONNECTED accessor = {}", accessor);
        }
        return message;
    }

}