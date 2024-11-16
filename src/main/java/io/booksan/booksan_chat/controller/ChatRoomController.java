package io.booksan.booksan_chat.controller;

import java.util.List;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import io.booksan.booksan_chat.dto.ChatRoomDTO;
import io.booksan.booksan_chat.entity.ChatRoom;
import io.booksan.booksan_chat.service.ChatService;
import io.booksan.booksan_chat.util.MapperUtil;
import io.booksan.booksan_chat.vo.ChatRoomVO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/api/chat")
@RequiredArgsConstructor
@Slf4j
public class ChatRoomController {

    private final MapperUtil mapperUtil;
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

    @PostMapping("/room/insert/{writerEmail}")
    @ResponseBody
    public ChatRoom createRoom(@RequestBody ChatRoomDTO chatRoomDTO, @PathVariable("writerEmail") String writerEmail, @AuthenticationPrincipal UserDetails userDetails) {
        String email = userDetails.getUsername();
        if (email != null) {
            return chatService.createChatRoom(mapperUtil.map(chatRoomDTO, ChatRoomVO.class), email, writerEmail);
        } else {
            return null;
        }
    }

    @GetMapping("/room/{roomId}")
    @ResponseBody
    public ChatRoom roomInfo(@PathVariable("roomId") String roomId) {
        return chatService.findRoomByRoomId(roomId);
    }

    @GetMapping("/rooms/alert/{email}")
    @ResponseBody
    public List<ChatRoomDTO> chatAlertRooms(@AuthenticationPrincipal UserDetails userDetails) {
        return chatService.getChatAlertRooms(userDetails.getUsername());
    }

    @PostMapping("/room/leave/{roomId}")
    @ResponseBody
    public String leaveRoom(@PathVariable("roomId") String roomId, @AuthenticationPrincipal UserDetails userDetails) {
        String email = userDetails.getUsername();
        if (roomId != null && email != null) {
            log.info("User {} leaving room {}", email, roomId);
            int result = chatService.userLeaveChatRoomUser(roomId, email);
            if (result == 1) {
                return "success";
            }
        }
        return "false";
    }
}
