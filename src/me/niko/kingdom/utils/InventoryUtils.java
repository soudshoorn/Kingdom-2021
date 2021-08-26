package me.niko.kingdom.utils;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Base64;

import org.bukkit.inventory.ItemStack;
import org.bukkit.util.io.BukkitObjectInputStream;
import org.bukkit.util.io.BukkitObjectOutputStream;
import org.yaml.snakeyaml.external.biz.base64Coder.Base64Coder;

public class InventoryUtils {
	
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
	
	public static String toBase64(final ItemStack itemstack) throws IOException {
            final ByteArrayOutputStream io = new ByteArrayOutputStream();
            final BukkitObjectOutputStream os = new BukkitObjectOutputStream((OutputStream)io);
            os.writeObject((Object)itemstack);
            os.flush();
            final byte[] serializedObject = io.toByteArray();
           return new String(Base64.getEncoder().encode(serializedObject));
    }
    
    public static ItemStack fromBase64(final String string) throws ClassNotFoundException, IOException {
            final byte[] serializedObject = Base64.getDecoder().decode(string);
            final ByteArrayInputStream in = new ByteArrayInputStream(serializedObject);
            final BukkitObjectInputStream is = new BukkitObjectInputStream((InputStream)in);
            return (ItemStack)is.readObject();
    }
}
