package io.booksan.booksan_chat.dto;

import java.util.Date;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ChatMessageDTO {
    private int messageId;
    private String roomId;
    private String uid;
    private String content;
    private Date insertDaytime;
}
