package io.booksan.booksan_chat.vo;

import java.util.Date;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ChatRoomVO {
    private String roomId;
    private String name;
    private Date insertDaytime;
    private String type;

}
