package io.booksan.booksan_chat.entity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class ChatRoom implements Comparable<ChatRoom> {

    private String roomId; //채팅방 아이디
    private String name;   //채팅방 이름
    @JsonIgnore
    @Getter(AccessLevel.PRIVATE) //해당 변수는 외부에서 접근 할 수 없음  
    private Map<String, String> userMap = new HashMap<>();   //채팅방에 입장한 사용자 uid에 대한 정보

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

    public void addUser(String uid, String email) {
        userMap.put(uid, email);
    }

    public void removeUser(String uid) {
        userMap.remove(uid);
    }

    @JsonProperty("userMap")
    public List<String> getUserMap() {
        return new ArrayList<>(userMap.values());
    }

    public boolean isExistEmail(String email) {
        return userMap.containsValue(email);
    }

    public String getSender(String uid) {
        return userMap.get(uid);
    }

    public int getUserCount() {
        return userMap.size();
    }

    @JsonIgnore
    public List<String> getUserMapUid() {
        return new ArrayList<>(userMap.keySet());
    }

}
