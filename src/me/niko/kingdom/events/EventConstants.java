package me.niko.kingdom.events;

import java.util.ArrayList;

import lombok.Getter;
import me.niko.kingdom.events.bountyhunters.BountyHunters;
import me.niko.kingdom.events.breakthecore.BreakTheCore;
import me.niko.kingdom.events.conquest.Conquest;
import me.niko.kingdom.events.koth.Koth;
import me.niko.kingdom.events.war.WarHandler;


public class EventConstants {
	
	@Getter public static ArrayList<BountyHunters> activeBountyHunters;
	@Getter public static ArrayList<BreakTheCore> activeBTCs;
	@Getter public static ArrayList<Conquest> activeConquests;
	@Getter public static ArrayList<Koth> activeKoths;

	public EventConstants() {
		activeBountyHunters = new ArrayList<>();
		activeBTCs = new ArrayList<>();
		activeConquests = new ArrayList<>();
		activeKoths = new ArrayList<>();
	}

	public void stopAll() {
		for(BountyHunters bountyHunters : this.activeBountyHunters) {
			bountyHunters.stop();
		}
		
		for(BreakTheCore breakTheCore : this.activeBTCs) {
			breakTheCore.stop();
			breakTheCore.save();
		}
		
		for(Conquest conquest : this.activeConquests) {
			conquest.stop();
		}
		
		for(Koth koth : this.activeKoths) {
			koth.stop();
		}
		
		if(WarHandler.isEnabled()) {
			WarHandler.stop();
		}
	}
	
	public boolean isEventsRunning() {
		return activeBountyHunters.size() != 0 || activeBTCs.size() != 0 || activeConquests.size() != 0 || activeKoths.size() != 0;
	}
}
