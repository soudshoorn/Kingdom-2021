package me.niko.kingdom.mysql;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.scheduler.BukkitRunnable;

import lombok.Getter;
import lombok.Setter;
import me.niko.kingdom.Kingdom;

public class MySQLManager {
	
	@Getter @Setter private static Connection connection;
	@Getter public String host, password, username, database;
	@Getter public int port;

	private String STATS_TABLE_NAME = "kingdom_stats";

	public MySQLManager(String host, String password, String username, int port, String database) {
		this.host = host;
		this.password = password;
		this.username = username;
		this.port = port;
		this.database = database;
	}

	public void mysqlSetup() {
		this.host = Kingdom.getInstance().getConfig().getString("mysql.login.host");
		this.database = Kingdom.getInstance().getConfig().getString("mysql.login.database");
		this.port = Kingdom.getInstance().getConfig().getInt("mysql.login.port");
		this.username = Kingdom.getInstance().getConfig().getString("mysql.login.username");
		this.password = Kingdom.getInstance().getConfig().getString("mysql.login.password");
		
		try {
			if(getConnection() != null && getConnection().isClosed()) {
				return;
			}
				
			Class.forName("com.mysql.jdbc.Driver");
			setConnection(DriverManager.getConnection("jdbc:mysql://" 
					+ this.host + ":" + this.port + "/" + this.database + "?autoReconnect=true", 
					this.username, this.password));
			Bukkit.getConsoleSender().sendMessage(ChatColor.GREEN + "[Kingdom] Connected to the database. [MySQL]");
				
			createTables();
		} catch (SQLException e) {
			Bukkit.getConsoleSender().sendMessage(ChatColor.RED + "[Kingdom] Could not connect to the MySQL");
		} catch (ClassNotFoundException e) {
			Bukkit.getConsoleSender().sendMessage(ChatColor.RED + "[Kingdom] Could not connect to the MySQL");
		}
	}
	
	public void createTables() {
		try {
			PreparedStatement statement = getConnection().prepareStatement("CREATE TABLE IF NOT EXISTS " + STATS_TABLE_NAME + "("
					+ "    uuid VARCHAR(255),"
					+ "    kingdom VARCHAR(255),"
					+ "    guild VARCHAR(255),"
					+ "    kingdom_rank INT,"
					+ "    influence INT,"
					+ "    kills INT,"
					+ "    deaths INT"
					+ ")");
			
			statement.execute();
			statement.close();
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}
	
	public boolean playerExists(UUID uuid) {
		try {
			PreparedStatement statement = getConnection().prepareStatement("SELECT * FROM " + STATS_TABLE_NAME + " WHERE uuid=?");
			
			statement.setString(1, uuid.toString());
			
			ResultSet resultSet = statement.executeQuery();
			
			if(resultSet.next()) {
				statement.close();
				return true;
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		
		return false;
	}
	
	public void updatePlayer(UUID uuid, String kingdom, String guild, int kingdom_rank, int influence, int kills, int deaths) {
		new BukkitRunnable() {

			@Override
			public void run() {
				try {
					if(playerExists(uuid)) {
						PreparedStatement updateStatement = getConnection().prepareStatement("UPDATE " + STATS_TABLE_NAME + " SET kingdom=?,guild=?,kingdom_rank=?,influence=?,kills=?,deaths=? WHERE uuid=?");
						
						updateStatement.setString(1, kingdom);
						updateStatement.setString(2, guild);
						updateStatement.setInt(3, kingdom_rank);
						updateStatement.setInt(4, influence);
						updateStatement.setInt(5, kills);
						updateStatement.setInt(6, deaths);
						updateStatement.setString(7, uuid.toString());
						
						int a = updateStatement.executeUpdate();
												
						updateStatement.close();
						
					} else {
						PreparedStatement insertPreparedStatement = getConnection().prepareStatement("INSERT INTO " + STATS_TABLE_NAME + " (uuid,kingdom,guild,kingdom_rank,influence,kills,deaths) VALUE (?,?,?,?,?,?,?)");
						
						insertPreparedStatement.setString(1, uuid.toString());
						insertPreparedStatement.setString(2, kingdom);
						insertPreparedStatement.setString(3, guild);
						insertPreparedStatement.setInt(4, kingdom_rank);
						insertPreparedStatement.setInt(5, influence);
						insertPreparedStatement.setInt(6, kills);
						insertPreparedStatement.setInt(7, deaths);

						insertPreparedStatement.executeUpdate();
						
						insertPreparedStatement.close();
					}
				} catch (Exception ex) {
					ex.printStackTrace();
				}
			}
		}.runTaskAsynchronously(Kingdom.getInstance());	
	}
	
}