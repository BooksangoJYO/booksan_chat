package io.booksan.booksan_chat.service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.stereotype.Service;

import io.booksan.booksan_chat.dao.ChatDAO;
import io.booksan.booksan_chat.dto.AlarmMessageDTO;
import io.booksan.booksan_chat.dto.ChatMessageDTO;
import io.booksan.booksan_chat.dto.ChatRoomDTO;
import io.booksan.booksan_chat.entity.AlarmCountEntity;
import io.booksan.booksan_chat.entity.ChatRoom;
import io.booksan.booksan_chat.entity.ReadMessageEntity;
import io.booksan.booksan_chat.util.MapperUtil;
import io.booksan.booksan_chat.vo.ChatMessageVO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class ChatService {

    private final ChatDAO chatDAO;
    private final ChatRoomService chatRoomService;
    @Autowired
    private SimpMessageSendingOperations messagingTemplate;
    private final MapperUtil mapperUtil;

    public List<ChatRoom> findAllRoom() {
        return chatRoomService.findAllRoom();
    }

    public List<ChatRoom> findRoomByEmail(String email) {
        return chatRoomService.findRoomByEmail(email);
    }

    public ChatRoom createChatRoom(String name, String email, String writerEmail) {

        ChatRoom chatRoom = chatRoomService.createChatRoom(name, email, writerEmail);
        //채팅방의 사용자수 변경을 알린다 
        messagingTemplate.convertAndSend("/sub/alarm", new AlarmMessageDTO(""));
        log.info("*** alarm message send***");
        return chatRoom;
    }

    public ChatRoom findRoomByRoomId(String roomId) {
        return chatRoomService.findRoomByRoomId(roomId);
    }

    public void userEnterChatRoomUser(String simpDestination, String email) {
        ChatRoom chatRoom = chatRoomService.userEnterChatRoomUser(simpDestination, email);
        if (chatRoom != null) {
            if (!chatRoom.isExistEmail(email)) {
                log.info("***personal message***");
                //서버에서 클라이언트로 구독 메시지를 전달한다 
                //채팅방의 입장한 모든 사용자에게 입장으로 알린다
                messagingTemplate.convertAndSend("/sub/chat/room/" + chatRoom.getRoomId(),
                        ChatMessageDTO.enterMessage(chatRoom, email));

                //채팅방의 사용자수 변경을 알린다 
                messagingTemplate.convertAndSend("/sub/alarm", new AlarmMessageDTO(chatRoom.getRoomId()));
            }

            //해당 유저가 들어온 채팅방의 메세지를 전부 읽음 처리한다
            ReadMessageEntity readMessageEntity = new ReadMessageEntity();
            readMessageEntity.setRoomId(chatRoom.getRoomId());
            readMessageEntity.setReceiver(email);
            int result = chatDAO.updateReadMessage(readMessageEntity);
            chatDAO.updateAlarmCount(new AlarmCountEntity(email, "decrease", result));
        }
    }

    public void userLeaveChatRoomUser(String roomId, String email) {
        ChatRoom chatRoom = chatRoomService.findRoomByRoomId(roomId);
        if (chatRoom != null) {
            chatRoomService.userLeaveChatRoomUser(roomId, email);
            //서버에서 클라이언트로 구독 메시지를 전달한다
            //채팅방의 입장한 모든 사용자에게 퇴장으로 알린다
            messagingTemplate.convertAndSend("/sub/chat/room/" + chatRoom.getRoomId(),
                    ChatMessageDTO.leaveMessage(chatRoom, email));
        }
        //채팅방의 사용자수 변경을 알린다
        messagingTemplate.convertAndSend("/sub/alarm", new AlarmMessageDTO(""));
    }

    public void saveMessage(ChatMessageDTO message) {
        ChatMessageVO chatMessageVO = new ChatMessageVO();
        chatMessageVO.setContent(message.getMessage());
        chatMessageVO.setRoomId(message.getRoomId());
        chatMessageVO.setEmail(message.getSender());
        chatDAO.insertChatMessage(chatMessageVO);
        log.info("message ID " + chatMessageVO.getMessageId());
        //메세지를 읽지 않는 사람들을 등록
        chatRoomService.insertReadMessage(message.getRoomId(), message.getSender(), chatMessageVO.getMessageId());
    }

    public List<ChatMessageDTO> getMessage(String roomId) {
        List<ChatMessageVO> list = chatDAO.getMessage(roomId);
        List<ChatMessageDTO> response = new ArrayList<>();
        for (ChatMessageVO chatMessageVO : list) {
            ChatMessageDTO chatMessageDTO = new ChatMessageDTO();
            chatMessageDTO.setRoomId(roomId);
            chatMessageDTO.setMessage(chatMessageVO.getContent());
            String email = chatMessageVO.getEmail();
            chatMessageDTO.setSender(email);
            response.add(chatMessageDTO);
        }
        return response;
    }

    public List<ChatRoomDTO> getAlarmRooms(String email) {
        return chatRoomService.getAlarmRooms(email).stream()
                .map(chatRoomVO -> mapperUtil.map(chatRoomVO, ChatRoomDTO.class))
                .collect(Collectors.toList());
    }

}
