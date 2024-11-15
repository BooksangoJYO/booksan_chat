package io.booksan.booksan_chat.config;

import java.security.Principal;
import java.util.Map;

import org.springframework.context.annotation.Lazy;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.stereotype.Component;

import io.booksan.booksan_chat.service.ChatService;
import io.booksan.booksan_chat.util.TokenChecker;
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

    private final TokenChecker tokenChecker;

    @Lazy
    private final ChatService chatService;

    private static class UserPrincipal implements Principal {

        private final String email;

        public UserPrincipal(String email) {
            this.email = email;
        }

        @Override
        public String getName() {
            return email;
        }
    }

    public StompHandler(@Lazy ChatService chatService, TokenChecker tokenChecker) {
        this.chatService = chatService;
        this.tokenChecker = tokenChecker;
    }

    @Override
    public Message<?> preSend(Message<?> message, MessageChannel channel) {
        StompHeaderAccessor accessor = StompHeaderAccessor.wrap(message);
        if (StompCommand.CONNECT == accessor.getCommand()) {
            //최초 연결 시에만 토큰 검증 및 사용자 인증 수행
            String accessToken = accessor.getFirstNativeHeader("accessToken");
            log.info("accessToken" + accessToken);
            Map<String, Object> response = tokenChecker.tokenCheck(accessToken);
            log.info("***** return data ***" + response.toString());
            log.info("token access and email" + response.toString());
            if ((Boolean) response.get("status")) {
                //인증 성공 시 Principal 설정 및 세션에 사용자 정보 저장
                String email = (String) response.get("email");
                Principal principal = new UserPrincipal(email);
                accessor.setUser(principal);
                accessor.getSessionAttributes().put("USER_EMAIL", email);
                log.info("New WebSocket Connection - User: {}", email);
            } else {
                log.error("Connection attempt with no email");
                throw new IllegalArgumentException("No email provided");
            }
        } else {
            // CONNECT 이외의 요청에서는 세션에서 사용자 정보를 가져옴
            Principal user = accessor.getUser();
            if (user == null) {
                String sessionEmail = (String) accessor.getSessionAttributes().get("USER_EMAIL");
                if (sessionEmail != null) {
                    accessor.setUser(new UserPrincipal(sessionEmail));
                }
            }
        }

        // 각 Command 별 처리
        switch (accessor.getCommand()) {
            case SUBSCRIBE:
                handleSubscribe(accessor);
                break;
            case UNSUBSCRIBE:
                handleUnsubscribe(accessor);
                break;
            case DISCONNECT:
                handleDisconnect(message);
                break;
            default:
                break;
        }

        return message;
    }

    private void handleSubscribe(StompHeaderAccessor accessor) {
        String simpDestination = (String) accessor.getHeader("simpDestination");
        Principal user = accessor.getUser();

        if (simpDestination != null && simpDestination.contains("/sub/chat/room") && user != null) {
            log.info("User {} subscribing to {}", user.getName(), simpDestination);
            chatService.userEnterChatRoomUser(simpDestination, user.getName());
        }
    }

    private void handleUnsubscribe(StompHeaderAccessor accessor) {
        log.info("***unsubScribe***");
    }

    private void handleDisconnect(Message<?> message) {
        String sessionId = (String) message.getHeaders().get("simpSessionId");
        log.info("WebSocket Connection Closed - Session: {}", sessionId);
    }
}
