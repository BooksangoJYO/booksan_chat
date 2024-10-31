package io.booksan.booksan_chat.controller;

import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.web.bind.annotation.RequestMapping;

import io.booksan.booksan_chat.dto.ChatMessageDTO;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@RequestMapping("/api/chat")
public class ChatController {
    private final SimpMessageSendingOperations messagingTemplate;

    @MessageMapping("/message")
    public void message(ChatMessageDTO message) {

        System.out.println("서버에서 메시지 수신 message " + message.toString());

        //서버에서 클라이언트로 구독 메시지를 전달한다 
        //채팅방의 모든 메시지에 대한 로그를 기록하려고 하면 이부분에서 기록하면 된다
        messagingTemplate.convertAndSend("/sub/chat/room/" + message.getRoomId(), message);
    }

}
