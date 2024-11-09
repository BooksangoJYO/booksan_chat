package io.booksan.booksan_chat.dao;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;

import io.booksan.booksan_chat.entity.AlarmCountEntity;
import io.booksan.booksan_chat.entity.ReadMessageEntity;
import io.booksan.booksan_chat.vo.ChatMessageVO;
import io.booksan.booksan_chat.vo.ChatRoomVO;
import io.booksan.booksan_chat.vo.UserChatRoomVO;

@Mapper
public interface ChatDAO {

    String getNicknameByEmail(String email);

    //참여중인 채팅방 목록
    List<ChatRoomVO> findAllRoomById(String uid);

    List<ChatRoomVO> findAllRoom();

    List<UserChatRoomVO> findUsersByRoomId(String roomId);

    //채팅방 생성
    int insertChatRoom(ChatRoomVO chatRoomVO);

    //채팅방 제거
    int deleteChatRoom(String sessionId);

    //사용자가 채팅방에 입장한다
    int insertUserChatRoom(UserChatRoomVO userChatRoomVO);

    //사용자가 채팅방에서 나간다
    int deleteUserChatRoom(UserChatRoomVO userChatRoomVO);

    //메시지등록
    int insertChatMessage(ChatMessageVO chatMessageVO);

    List<ChatMessageVO> getMessage(String roomId);

    String getUidByEmail(String email);

    String getEmailbyUid(String uid);

    int insertReadMessage(ReadMessageEntity readMessageEntity);

    int updateReadMessage(ReadMessageEntity readMessageEntity);

    int updateAlarmCount(AlarmCountEntity alarmCountEntity);

    List<ChatRoomVO> getAlarmRooms(String uid);

}
