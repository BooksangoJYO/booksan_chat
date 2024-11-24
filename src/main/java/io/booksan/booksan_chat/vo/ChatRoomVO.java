package io.booksan.booksan_chat.vo;

import java.util.Date;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ChatRoomVO {
    private String roomId;
    private String name;
    private int dealId;
    private Date insertDaytime;
    private String type;
    

}
