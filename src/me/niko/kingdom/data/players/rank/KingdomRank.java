package me.niko.kingdom.data.players.rank;

import java.util.Comparator;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import me.niko.kingdom.data.KingdomHandler;

@Getter @Setter @AllArgsConstructor
public class KingdomRank {
	
	private String identifier = "";
	private String name = "";
	private String prefix = "";
}
