package io.booksan.booksan_chat.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import io.booksan.booksan_chat.dto.ChatMessageDTO;
import io.booksan.booksan_chat.service.ChatService;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Controller
public class ChatController {

    private final SimpMessageSendingOperations messagingTemplate;
    private final ChatService chatService;

    @MessageMapping("/chat/message")
    public void message(ChatMessageDTO message) {

        System.out.println("서버에서 메시지 수신 message " + message.toString());

        //서버에서 클라이언트로 구독 메시지를 전달한다 
        //채팅방의 모든 메시지에 대한 로그를 기록하려고 하면 이부분에서 기록하면 된다
        messagingTemplate.convertAndSend("/sub/chat/room/" + message.getRoomId(), message);
        chatService.saveMessage(message);
    }

    @RequestMapping("/api/chat/prevMessage/{roomId}")
    public ResponseEntity<List<ChatMessageDTO>> getPrevMessage(@PathVariable("roomId") String roomId) {
        return ResponseEntity.ok(chatService.getMessage(roomId));

    }
}
