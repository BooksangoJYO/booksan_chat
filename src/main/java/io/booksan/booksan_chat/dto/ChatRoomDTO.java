package io.booksan.booksan_chat.dto;

import java.util.Date;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ChatRoomDTO {
    private String roomId;
    private String name;
    private Date insertDaytime;
    private String type;

}
