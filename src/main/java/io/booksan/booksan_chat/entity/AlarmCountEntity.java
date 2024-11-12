package io.booksan.booksan_chat.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class AlarmCountEntity {

    private String email;
    private String action;
    private int alarmCount;
}
