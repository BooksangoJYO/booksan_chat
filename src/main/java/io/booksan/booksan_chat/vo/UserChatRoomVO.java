package io.booksan.booksan_chat.vo;

import java.util.Date;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserChatRoomVO {
    private String roomId;
    private String uid;
    private Date insertDaytime;
    private Date exitDaytime;
}