package io.booksan.booksan_chat.controller;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import io.booksan.booksan_chat.entity.ChatRoom;
import io.booksan.booksan_chat.service.ChatRoomService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/api/chat") 
@RequiredArgsConstructor
@Slf4j
public class ChatRoomController {
    private final ChatRoomService chatService;

    @GetMapping("/rooms")
	@ResponseBody
	public List<ChatRoom> rooms() {
		return chatService.findAllRoom();
	}

    @PostMapping("/room/insert/{name}")
	@ResponseBody
	public ChatRoom createRoom(@PathVariable("name") String name) {
		log.info("이름은 :"+name);
		return chatService.createChatRoom(name);
	}

    @GetMapping("/room/{roomId}")
	@ResponseBody
	public ChatRoom roomInfo(@PathVariable("roomId") String roomId) {
		return chatService.findRoomById(roomId);
	}

}