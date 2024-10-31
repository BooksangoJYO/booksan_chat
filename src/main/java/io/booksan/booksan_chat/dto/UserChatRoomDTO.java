package io.booksan.booksan_chat.dto;

import java.util.Date;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserChatRoomDTO {
    private String roomId;
    private String uid;
    private Date insertDaytime;
    private Date exitDaytime;
}