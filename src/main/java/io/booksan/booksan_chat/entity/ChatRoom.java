package io.booksan.booksan_chat.entity;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class ChatRoom implements Comparable<ChatRoom> {

    private String roomId; //채팅방 아이디
    private String name;   //채팅방 이름
    private Set<String> userSet = new HashSet<>();   //채팅방에 입장한 사용자 email에 대한 정보

    public ChatRoom(String name) {
        this.roomId = UUID.randomUUID().toString();
        this.name = name;
    }

    @Override
    public int compareTo(ChatRoom o) {
        if (o == null) {
            return 1;
        }
        return name.compareToIgnoreCase(o.name);
    }

    public void addUser(String email) {
        userSet.add(email);
    }

    public void removeUser(String email) {
        userSet.remove(email);
    }

    public boolean isExistEmail(String email) {
        return userSet.contains(email);
    }

    public int getUserCount() {
        return userSet.size();
    }

}
