package io.booksan.booksan_chat.service;

import java.util.List;

import org.springframework.stereotype.Service;

import io.booksan.booksan_chat.dao.ChatDAO;
import io.booksan.booksan_chat.dto.ChatRoomDTO;
import io.booksan.booksan_chat.util.MapperUtil;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ChatService {
    private final ChatDAO chatDAO;
    private final MapperUtil mapperUtil;

    public List<ChatRoomDTO> findAllRoom(String uid) {
        throw new UnsupportedOperationException("Not supported yet.");
        //return chatDAO.findAllRoomById(uid).stream().map(room->mapperUtil.map(room,ChatRoomDTO.class)).toList();
    }

    public ChatRoomDTO createChatRoom(String name) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public ChatRoomDTO findRoomById(String roomId) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    
}
