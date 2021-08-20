package me.niko.kingdom.mount;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.Sound;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftLivingEntity;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Horse;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.io.BukkitObjectInputStream;
import org.bukkit.util.io.BukkitObjectOutputStream;
import org.yaml.snakeyaml.external.biz.base64Coder.Base64Coder;

import lombok.Getter;
import me.niko.kingdom.Kingdom;
import me.niko.kingdom.data.KingdomHandler;
import me.niko.kingdom.data.players.KingdomPlayer;
import me.niko.kingdom.utils.ConfigUtils;
import me.niko.kingdom.utils.TitleAPI;
import net.minecraft.server.v1_8_R3.AttributeInstance;
import net.minecraft.server.v1_8_R3.EntityInsentient;
import net.minecraft.server.v1_8_R3.GenericAttributes;

public class HorseHandler {

	public static HashMap<UUID, String> donkeyIv = new HashMap();
	@Getter public static HashMap<UUID, Horse> horseSpawned = new HashMap<>();
	@Getter public static HashMap<UUID, Integer> mountingTimer = new HashMap();

	public void WriteDonkeyIvFile() {
		File file = new File(Kingdom.getInstance().getDataFolder(), "horses.yml");
		FileConfiguration fileConfig = YamlConfiguration.loadConfiguration(file);

		if (!file.exists()) {
			try {
				fileConfig.save(file);
			} catch (IOException ex) {
				ex.printStackTrace();
			}
		}
	}

	public FileConfiguration getDonkeyFile() {
		File file = new File(Kingdom.getInstance().getDataFolder(), "horses.yml");

		return YamlConfiguration.loadConfiguration(file);
	}

	public void saveInventory() {
		File sourceFile = new File(Kingdom.getInstance().getDataFolder(), "horses.yml");
		FileConfiguration donkeyC = this.getDonkeyFile();

		for (Entry<UUID, String> entry : donkeyIv.entrySet()) {
			donkeyC.set(entry.getKey().toString(), entry.getValue());
		}

		try {
			donkeyC.save(sourceFile);
		} catch (IOException ex) {
			ex.printStackTrace();
		}
	}

	public void saveInventory(Player p) {
		File sourceFile = new File(Kingdom.getInstance().getDataFolder(), "horses.yml");
		FileConfiguration donkeyC = this.getDonkeyFile();

		donkeyC.set(p.getUniqueId().toString(), donkeyIv.get(p.getUniqueId()));

		try {
			donkeyC.save(sourceFile);
		} catch (IOException ex) {
			ex.printStackTrace();
		}

	}

	public void loadInventory() {
		FileConfiguration donkeyC = this.getDonkeyFile();

		for (String string : donkeyC.getKeys(false)) {
			OfflinePlayer player = Bukkit.getOfflinePlayer(UUID.fromString(string));

			donkeyIv.put(player.getUniqueId(), donkeyC.getString(string));
		}
	}

	public boolean isEnemiesNearby(Player player) {
		boolean isNearby = false;
		double range = Kingdom.getInstance().getConfig().getDouble("donkey.enemyRadiusCheck");
		List<Entity> eInRange = player.getNearbyEntities(range, range, range);

		for (Entity entity : eInRange) {
			if (!(entity instanceof Player)) {
				continue;
			}

			Player target = (Player) entity;

			KingdomPlayer kingdomPlayer = KingdomHandler.getKingdomPlayer(player);
			KingdomPlayer kingdomTarget = KingdomHandler.getKingdomPlayer(target);

			if (kingdomPlayer.getKingdom() == null || kingdomTarget.getKingdom() == null) {
				continue;
			}

			if (!KingdomHandler.isSimiliarKingdom(kingdomPlayer.getKingdom(), kingdomTarget.getKingdom())) {
				isNearby = true;
				break;
			}
		}

		return isNearby;
	}

	public void mountCountdown(Player player) {
		if (mountingTimer.containsKey(player.getUniqueId())) {
			if (mountingTimer.get(player.getUniqueId()) > 0) {
				return;
			}

			mountingTimer.replace(player.getUniqueId(), 0);
		} else {
			mountingTimer.put(player.getUniqueId(), 0);
		}

		int seconds = Kingdom.getInstance().getConfig().getInt("donkey.countdown");

		new BukkitRunnable() {
			public void run() {
				if (mountingTimer.containsKey(player.getUniqueId()) && player.isOnline()) {
					mountingTimer.replace(player.getUniqueId(), (Integer) mountingTimer.get(player.getUniqueId()) + 1);

					if (mountingTimer.get(player.getUniqueId()) < seconds) {
						TitleAPI.send(player, ChatColor.YELLOW + ((Integer) mountingTimer.get(player.getUniqueId())).toString(), "",
								1, 1, 1);
						// ef.mountTimerCount(p, 0.9F);
						player.playSound(player.getLocation(), Sound.NOTE_PLING, 5.0F, 0.9F);
					} else {
						TitleAPI.send(player, ChatColor.GREEN + ((Integer) mountingTimer.get(player.getUniqueId())).toString(), "", 1,
								1, 1);
						// ef.mountTimerCount(p, 1.2F);
						player.playSound(player.getLocation(), Sound.NOTE_PLING, 5.0F, 1.2F);
					}

					if (mountingTimer.get(player.getUniqueId()) >= seconds) {
						mountingTimer.remove(player.getUniqueId());
						mountUp(player);
						cancel();
					}
				} else {
					if (mountingTimer.containsKey(player.getUniqueId())) {
						mountingTimer.remove(player.getUniqueId());
					}

					if (player.isOnline()) {
						player.sendMessage(ConfigUtils.getFormattedValue("messages.mount.on_cooldown"));
						// ef.playDenySound(p);
						player.playSound(player.getLocation(), Sound.VILLAGER_NO, 5.0F, 1.3F);
					}

					this.cancel();
				}
			}
		}.runTaskTimer(Kingdom.getInstance(), 20L, 20L);
	}

	public void mountUp(Player player) {
		FileConfiguration conf = Kingdom.getInstance().getConfig();

		double speed = conf.getDouble("donkey.speed");
		double jump = conf.getDouble("donkey.jump");
		double health = conf.getDouble("donkey.health");

		Horse horse = (Horse) player.getWorld().spawn(player.getLocation(), Horse.class);
		KingdomPlayer kingdomPlayer = KingdomHandler.getKingdomPlayer(player);

		horse.setCustomName(player.getName() + " "
				+ (kingdomPlayer.getKingdom() != null ? kingdomPlayer.getKingdom().getDisplayName() : "Default"));
		horse.setCustomNameVisible(false);
		horse.setMaxHealth(health);
		horse.setTamed(true);
		horse.setOwner(player);
		horse.setRemoveWhenFarAway(true);

		AttributeInstance attributes = ((EntityInsentient) ((CraftLivingEntity) horse).getHandle())
				.getAttributeInstance(GenericAttributes.MOVEMENT_SPEED);
		attributes.setValue(speed);

		horse.setJumpStrength(jump);
		horse.setPassenger(player);
		horse.setAdult();
		horse.setHealth(health);
		horse.setCarryingChest(true);
		horse.setBreed(false);
		horse.getInventory().setSaddle(new ItemStack(Material.SADDLE));
		horse.setMetadata("unNatural", new FixedMetadataValue(Kingdom.getInstance(), true));
		horse.setMetadata("kingdom", new FixedMetadataValue(Kingdom.getInstance(), kingdomPlayer.getKingdom()));

		if (donkeyIv.containsKey(player.getUniqueId())) {
			ItemStack[] items = null;

			try {
				items = itemStackArrayFromBase64((String) donkeyIv.get(player.getUniqueId()));
			} catch (Exception ex) {
				ex.printStackTrace();
			}

			if (items != null) {
				for (int i = 0; i < items.length; ++i) {
					horse.getInventory().setItem(i, items[i]);
				}
			}
		}

		if (horseSpawned.containsKey(player.getUniqueId())) {
			Horse h2 = (Horse) horseSpawned.get(player.getUniqueId());
			if (!h2.isDead()) {
				h2.remove();
			}

			horseSpawned.replace(player.getUniqueId(), horse);
		} else {
			horseSpawned.put(player.getUniqueId(), horse);
		}

		// ef.mountUp(player);
		player.playSound(player.getLocation(), Sound.ANVIL_USE, 5.0F, 1.0F);
		player.sendMessage(ConfigUtils.getFormattedValue("messages.mount.mounted"));
	}

	public static String itemStackArrayToBase64(ItemStack[] items) throws IllegalStateException {
		try {
			ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
			BukkitObjectOutputStream dataOutput = new BukkitObjectOutputStream(outputStream);
			dataOutput.writeInt(items.length);

			for (int i = 0; i < items.length; ++i) {
				dataOutput.writeObject(items[i]);
			}

			dataOutput.close();
			return Base64Coder.encodeLines(outputStream.toByteArray());
		} catch (Exception ex) {
			throw new IllegalStateException("Not able to convert item stacks to base64.", ex);
		}
	}

	public static ItemStack[] itemStackArrayFromBase64(String data) throws IOException {
		try {
			ByteArrayInputStream inputStream = new ByteArrayInputStream(Base64Coder.decodeLines(data));
			BukkitObjectInputStream dataInput = new BukkitObjectInputStream(inputStream);
			ItemStack[] items = new ItemStack[dataInput.readInt()];

			for (int i = 0; i < items.length; ++i) {
				items[i] = (ItemStack) dataInput.readObject();
			}

			dataInput.close();
			return items;
		} catch (ClassNotFoundException ex) {
			throw new IOException("IOException", ex);
		}
	}
	
	public static void saveAndRemove() {
		(new HorseHandler()).saveInventory();
		
		for(Entry<UUID, Horse> entry : HorseHandler.getHorseSpawned().entrySet()) {
        	Horse horse = entry.getValue();
        	
        	if(horse.isDead()) {
        		horse.remove();
        	}
        }
	}
}
