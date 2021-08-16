package me.niko.kingdom.chat;

import java.util.ArrayList;

import org.bukkit.entity.Player;

import lombok.Getter;
import lombok.Setter;

public class ChatHandler {
	
	@Getter @Setter private boolean muted;
	@Getter private ArrayList<Player> chatSpy;
	
	public ChatHandler() {
		this.muted = false;
		this.chatSpy = new ArrayList<>();
	}

}
