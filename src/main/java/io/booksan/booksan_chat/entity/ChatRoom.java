package io.booksan.booksan_chat.entity;

import java.util.HashMap;
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
public class ChatRoom implements Comparable<ChatRoom>{
	
  private String roomId; //채팅방 아이디
  private String name;   //채팅방 이름
  @JsonIgnore
  @Getter(AccessLevel.PRIVATE) //해당 변수는 외부에서 접근 할 수 없음  
  private Map<String, String> userMap = new HashMap<>();   //채팅방에 입장한 사용자 세션아이디에 대한 이름  

   public ChatRoom(String name) {
      this.roomId = UUID.randomUUID().toString();
      this.name = name;
   }

	@Override
	public int compareTo(ChatRoom o) {
		if (o == null) return 1;
		return name.compareToIgnoreCase(o.name);
	}
	
	public void addSession(String sessionId, String sender) {
		userMap.put(sessionId, sender);
	}
	
	public void removeSession(String sessionId) {
		userMap.remove(sessionId);
	}
	
	@JsonProperty("userCount")
	public int getUserCount() {
		return userMap.size();
	}
	
	public boolean isExistSessionId(String sessionId) {
		return userMap.containsKey(sessionId);
	}
	
	public String getSender(String sessionId) {
		return userMap.get(sessionId);
	}
  
}
