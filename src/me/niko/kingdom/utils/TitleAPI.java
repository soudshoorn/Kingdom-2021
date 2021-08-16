package me.niko.kingdom.utils;

import java.util.Objects;

import org.bukkit.ChatColor;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;

import net.minecraft.server.v1_8_R3.IChatBaseComponent;
import net.minecraft.server.v1_8_R3.IChatBaseComponent.ChatSerializer;
import net.minecraft.server.v1_8_R3.PacketPlayOutTitle;
import net.minecraft.server.v1_8_R3.PacketPlayOutTitle.EnumTitleAction;

public class TitleAPI {

	public static void send(Player p, String message, String submessage, int in, int stay, int out) {
		PacketPlayOutTitle packetPlayOutTimes = new PacketPlayOutTitle(EnumTitleAction.TIMES, (IChatBaseComponent) null,
				in, stay, out);
		((CraftPlayer) p).getHandle().playerConnection.sendPacket(packetPlayOutTimes);
		IChatBaseComponent titleMain;
		PacketPlayOutTitle packetPlayOutTitle;
		if (message != null) {
			titleMain = ChatSerializer
					.a("{\"text\": \"" + ChatColor.translateAlternateColorCodes('&', submessage) + "\"}");
			packetPlayOutTitle = new PacketPlayOutTitle(EnumTitleAction.SUBTITLE, titleMain);
			((CraftPlayer) p).getHandle().playerConnection.sendPacket(packetPlayOutTitle);
		}

		if (submessage != null) {
			titleMain = ChatSerializer.a("{\"text\": \""
					+ ChatColor.translateAlternateColorCodes('&', (String) Objects.requireNonNull(message)) + "\"}");
			packetPlayOutTitle = new PacketPlayOutTitle(EnumTitleAction.TITLE, titleMain);
			((CraftPlayer) p).getHandle().playerConnection.sendPacket(packetPlayOutTitle);
		}

	}
}
