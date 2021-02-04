
package com.emilyatlee.fme;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.IOException;
import java.util.*;

public class FMe extends JavaPlugin implements Listener {

    Random rand = new Random();

    @Override
    public void onEnable() {
        getLogger().info("fMe enabled");

        getCommand("fme.reload").setExecutor(this);

        saveDefaultConfig();

        PluginManager manager = getServer().getPluginManager();
        manager.registerEvents(this, this);
    }

    @Override
    public void onDisable() {
        getLogger().info("fMe disabled");
    }

    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent event){
        Bukkit.getScheduler().scheduleSyncDelayedTask(this, new Runnable() {
            @Override
            public void run() {
                Bukkit.broadcastMessage(getResponseMessage());
            }
        }, getResponseDelay());
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if(command.getName().equalsIgnoreCase("fme.reload")){
            reloadConfig();
            sender.sendMessage("fMe configs have been reloaded");
            return true;
        }
        return false;
    }

    private int getResponseDelay() {
        int min = this.getConfig().getInt("f-minDelay");
        int max = this.getConfig().getInt("f-maxDelay");
        return rand.nextInt(max - min) + min;
    }

    private String getResponseMessage() {
        String prefix = this.getConfig().getString("f-prefix");

        Set<String> messageKeys = this.getConfig().getConfigurationSection("f-messages").getKeys(false);

        Map<Integer, String> messages = new HashMap<>();
        int totalWeight = 0;
        for(String key : messageKeys){
            int delta = this.getConfig().getInt("f-messages." + key);
            if(delta > 0) {
                totalWeight += delta;
                messages.put(totalWeight, key);
            }
        }


        int place = rand.nextInt(totalWeight+1);

        String message = "";
        for(Map.Entry<Integer, String> entry : messages.entrySet()){
            if(place <= entry.getKey()){
                message = entry.getValue();
                break;
            }
        }

        if(prefix == null){
            prefix = "";
        }

        if(!prefix.isEmpty()){
            prefix += " ";
        }

        return prefix + message;
    }
}
