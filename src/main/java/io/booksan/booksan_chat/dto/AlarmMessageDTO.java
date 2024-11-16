package io.booksan.booksan_chat.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class AlarmMessageDTO {
    // 알람 타입 : 사용자 정보 변경 

    public enum MessageType {
        USER_INFO_UPDATE, CHAT_INFO_UPDATE
    }

    private MessageType type;
    private String target; // 대상(변경 대상자 또는 채팅방 변경)

    public AlarmMessageDTO(String target) {
        this.type = MessageType.CHAT_INFO_UPDATE;
        this.target = target;
    }

}
