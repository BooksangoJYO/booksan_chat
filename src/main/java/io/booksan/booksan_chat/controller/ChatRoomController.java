package io.booksan.booksan_chat.controller;

import java.util.List;
import java.util.Map;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
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
    public List<ChatRoom> rooms(@RequestHeader Map<String, String> headers) {
        String accessToken = headers.get("accesstoken");
        String refreshToken = headers.get("refreshtoken");
        log.info("****accesstoken" + accessToken);
        Map<String, Object> response = tokenChecker.tokenCheck(accessToken, refreshToken);
        if ((Boolean) response.get("status")) {
            return chatService.findRoomByEmail((String) response.get("email"));
        }

        return null;
    }

    @PostMapping("/room/insert/{name}/{writerEmail}")
    @ResponseBody
    public ChatRoom createRoom(@PathVariable("name") String name, @PathVariable("writerEmail") String writerEmail, @RequestHeader Map<String, String> headers) {
        String accessToken = headers.get("accesstoken");
        String refreshToken = headers.get("refreshtoken");
        Map<String, Object> response = tokenChecker.tokenCheck(accessToken, refreshToken);
        if ((Boolean) response.get("status")) {
            return chatService.createChatRoom(name, (String) response.get("email"), writerEmail);
        } else {
            return null;
        }
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

    @GetMapping("/rooms/alarm/{email}")
    @ResponseBody
    public List<ChatRoomDTO> alarmRooms(@PathVariable("email") String email) {
        return chatService.getAlarmRooms(email);
    }

}
