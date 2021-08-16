package me.niko.kingdom.utils.menu.menu.menus;

import java.beans.ConstructorProperties;
import java.util.HashMap;
import java.util.Map;

import org.bukkit.Material;
import org.bukkit.entity.Player;

import me.niko.kingdom.utils.menu.menu.Button;
import me.niko.kingdom.utils.menu.menu.Menu;
import me.niko.kingdom.utils.menu.menu.buttons.BooleanButton;
import me.niko.kingdom.utils.menu.util.Callback;

public class ConfirmMenu extends Menu {
	
	private String title;
	private Callback<Boolean> response;

	@Override
	public Map<Integer, Button> getButtons(Player player) {
		HashMap<Integer, Button> buttons = new HashMap<Integer, Button>();
		for (int i = 0; i < 9; ++i) {
			if (i == 3) {
				buttons.put(i, new BooleanButton(true, this.response));
			} else if (i == 5) {
				buttons.put(i, new BooleanButton(false, this.response));
			} else {
				buttons.put(i, Button.placeholder(Material.STAINED_GLASS_PANE, (byte) 14, new String[0]));
			}
		}
		return buttons;
	}

	@Override
	public String getTitle(Player player) {
		return this.title;
	}

	@ConstructorProperties({ "title", "response" })
	public ConfirmMenu(String title, Callback<Boolean> response) {
		this.title = title;
		this.response = response;
	}
}
