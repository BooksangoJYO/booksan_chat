package io.booksan.booksan_chat.config;

import java.util.Map;

import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.stereotype.Component;

import io.booksan.booksan_chat.service.ChatService;
import lombok.RequiredArgsConstructor;
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
@RequiredArgsConstructor
public class StompHandler implements ChannelInterceptor {  // ChannelInterceptorAdapter 대신 ChannelInterceptor 사용

    private final ChatService chatService;

    @Override
    public Message<?> preSend(Message<?> message, MessageChannel channel) {
        StompHeaderAccessor accessor = StompHeaderAccessor.wrap(message);
        
        // 로깅을 try-catch로 감싸서 안전하게 처리
        try {
            for (Map.Entry<String, Object> entry : message.getHeaders().entrySet()) {
                log.info("preSend() header -> {}", entry);
            }
        } catch (Exception e) {
            log.error("Header logging failed", e);
        }

        if (accessor.getCommand() == null) {
            return message;
        }

        switch (accessor.getCommand()) {
            case CONNECT:
                handleConnect(accessor);
                break;
            case SUBSCRIBE:
                handleSubscribe(accessor);
                break;
            case UNSUBSCRIBE:
                handleUnsubscribe(accessor);
                break;
            case DISCONNECT:
                handleDisconnect(message);
                break;
        }

        return message;
    }

    private void handleConnect(StompHeaderAccessor accessor) {
        String jwtToken = accessor.getFirstNativeHeader("token");
        log.info("preSend() CONNECT jwtToken = {}", jwtToken);
    }

    private void handleSubscribe(StompHeaderAccessor accessor) {
        String simpDestination = accessor.getDestination();
        String simpSessionId = accessor.getSessionId();
        String sender = accessor.getFirstNativeHeader("sender");
        
        if (simpDestination != null && simpDestination.contains("/sub/chat/room")) {
            //chatService.insertUserChatRoom();
        }
    }

    private void handleUnsubscribe(StompHeaderAccessor accessor) {
        String simpSessionId = accessor.getSessionId();
        if (simpSessionId != null) {
            //chatService.deleteUserChatRoom(simpSessionId);
        }
    }

    private void handleDisconnect(Message<?> message) {
        String sessionId = (String) message.getHeaders().get("simpSessionId");
        log.info("preSend() DISCONNECTED sessionId = {}", sessionId);
    }
}