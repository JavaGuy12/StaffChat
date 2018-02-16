package me.itguy12.staffchat;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.util.ArrayList;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Sound;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.plugin.java.JavaPlugin;

public class Main extends JavaPlugin implements Listener{

	//LIB
	String prefix;
	String scEnabled;
	String scDisabled;
	String chatFormat;
	
	//END LIB
	
	public ArrayList<String> inStaffChat = new ArrayList<>();
	
	
	public void onEnable() {
		Bukkit.getServer().getPluginManager().registerEvents(this, this);
		prefix = ChatColor.translateAlternateColorCodes('&', getConfig().getString("prefix"));
		scEnabled = ChatColor.translateAlternateColorCodes('&', getConfig().getString("staff-chat-enabled"));
		scDisabled = ChatColor.translateAlternateColorCodes('&', getConfig().getString("staff-chat-disabled"));
		chatFormat = getConfig().getString("chat-format");
		
		File f = new File(getDataFolder(), "config.yml");
		   if (!getDataFolder().exists())
               getDataFolder().mkdir();
           if (!f.exists()) {
               try (InputStream in = getResource("config.yml")) {
                   Files.copy(in, f.toPath());
               } catch (IOException e) {
            	   	Bukkit.getServer().getLogger().severe("COULD NOT LOAD CONFIG!!!");
            	   	e.printStackTrace();
               }
           }
	}
	
	@EventHandler
	public void onPlayerChat(AsyncPlayerChatEvent e) {
		if(inStaffChat.contains(e.getPlayer().getName())) {
			staffChat(e.getMessage(), e.getPlayer());
			e.setCancelled(true);
		}
	}
	
	public boolean onCommand(CommandSender cs, Command cmd, String str, String[] args) {
		if(cmd.getName().equalsIgnoreCase("sc")) {
			if(!(cs instanceof Player)) {
				cs.sendMessage(ChatColor.RED + "Only players can use this command.");
				 return true;
			}
			
			Player p = (Player) cs;
			
			if(p.hasPermission("staffchat.use")) {
				if(inStaffChat.contains(p.getName())){
					inStaffChat.remove(p.getName());
					p.sendMessage(prefix + scDisabled);
					return true;
				}else {
					inStaffChat.add(p.getName());
					p.sendMessage(prefix + scEnabled); 
					return true;
				}
			}else {
				p.sendMessage(ChatColor.RED + "No permission.");
				return true;
			}
		}
		return false;
	}
	
	
	private void staffChat(String message, Player p) {
		String msg = chatFormat.replaceAll("%player%", p.getName()).replaceAll("%message%", message);
		msg = ChatColor.translateAlternateColorCodes('&', msg);
		msg = prefix + msg;
		for (Player pl : Bukkit.getOnlinePlayers()) {
			if(pl.hasPermission("staffchat.use")) {
				pl.sendMessage(msg);
				pl.playSound(pl.getLocation(), Sound.ORB_PICKUP, 1F, 1F);
			}
		}
	}
	
}
