package io.booksan.booksan_chat.controller;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import io.booksan.booksan_chat.entity.ChatRoom;
import io.booksan.booksan_chat.service.ChatService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/api/chat")
@RequiredArgsConstructor
@Slf4j
public class ChatRoomController {

    private final ChatService chatService;

    @GetMapping("/rooms/{email}")
    @ResponseBody
    public List<ChatRoom> rooms(@PathVariable("email") String email) {
        log.info("***email****");
        return chatService.findRoomByEmail(email);
    }

    @PostMapping("/room/insert/{name}/{email}/{writerEmail}")
    @ResponseBody
    public ChatRoom createRoom(@PathVariable("name") String name, @PathVariable("email") String email, @PathVariable("writerEmail") String writerEmail) {
        log.info("이름은 :" + name);
        return chatService.createChatRoom(name, email, writerEmail);
    }

    @GetMapping("/room/{roomId}")
    @ResponseBody
    public ChatRoom roomInfo(@PathVariable("roomId") String roomId) {
        return chatService.findRoomByRoomId(roomId);
    }

    @GetMapping("/room/inviteRooms/{email}")
    public String getMethodName(@PathVariable("email") String email) {
        return new String();
    }

}
