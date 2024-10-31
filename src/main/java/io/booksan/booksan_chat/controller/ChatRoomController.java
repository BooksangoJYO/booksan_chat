package io.booksan.booksan_chat.controller;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import io.booksan.booksan_chat.dto.ChatRoomDTO;
import io.booksan.booksan_chat.service.ChatService;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/chat") 
@RequiredArgsConstructor
public class ChatRoomController {
    private final ChatService chatService;

    @GetMapping("/rooms")
	@ResponseBody
	public List<ChatRoomDTO> rooms() {
		return chatService.findAllRoom("aaaa");
	}

    @PostMapping("/room")
	@ResponseBody
	public ChatRoomDTO createRoom(@RequestParam String name) {
		return chatService.createChatRoom(name);
	}

    @GetMapping("/room/{roomId}")
	@ResponseBody
	public ChatRoomDTO roomInfo(@PathVariable String roomId) {
		return chatService.findRoomById(roomId);
	}
}
