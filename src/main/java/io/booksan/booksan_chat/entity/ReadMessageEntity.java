package io.booksan.booksan_chat.entity;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class ReadMessageEntity {

    private String roomId;
    private String sender;
    private String receiver;
    private int messageId;

}
