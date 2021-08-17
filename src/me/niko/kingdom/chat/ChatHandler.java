package me.niko.kingdom.chat;

import java.util.ArrayList;
import java.util.UUID;

import lombok.Getter;
import lombok.Setter;

public class ChatHandler {
	
	@Getter @Setter private boolean muted;
	@Getter private ArrayList<UUID> chatSpy;
	
	public ChatHandler() {
		this.muted = false;
		this.chatSpy = new ArrayList<>();
	}

}
