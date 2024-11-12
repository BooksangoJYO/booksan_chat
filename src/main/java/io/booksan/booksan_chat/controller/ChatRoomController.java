package io.booksan.booksan_chat.controller;

import java.util.List;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import io.booksan.booksan_chat.dto.ChatRoomDTO;
import io.booksan.booksan_chat.entity.ChatRoom;
import io.booksan.booksan_chat.service.ChatService;
import io.booksan.booksan_chat.util.TokenChecker;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/api/chat")
@RequiredArgsConstructor
@Slf4j
public class ChatRoomController {

    private final TokenChecker tokenChecker;
    private final ChatService chatService;

    @GetMapping("/rooms")
    @ResponseBody
    public List<ChatRoom> rooms(@AuthenticationPrincipal UserDetails userDetails) {
        String email = userDetails.getUsername();
        if (email != null) {
            return chatService.findRoomByEmail(email);
        }

        return null;
    }

    @PostMapping("/room/insert/{name}/{writerEmail}")
    @ResponseBody
    public ChatRoom createRoom(@PathVariable("name") String name, @PathVariable("writerEmail") String writerEmail, @AuthenticationPrincipal UserDetails userDetails) {
        String email = userDetails.getUsername();
        if (email != null) {
            return chatService.createChatRoom(name, email, writerEmail);
        } else {
            return null;
        }
    }

    @GetMapping("/room/{roomId}")
    @ResponseBody
    public ChatRoom roomInfo(@PathVariable("roomId") String roomId) {
        return chatService.findRoomByRoomId(roomId);
    }

    @GetMapping("/rooms/alarm/{email}")
    @ResponseBody
    public List<ChatRoomDTO> alarmRooms(@AuthenticationPrincipal UserDetails userDetails) {
        return chatService.getAlarmRooms(userDetails.getUsername());
    }

}
