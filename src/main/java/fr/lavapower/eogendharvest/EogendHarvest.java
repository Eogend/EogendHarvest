package fr.lavapower.eogendharvest;

import fr.lavapower.eogendharvest.listener.HarvestListener;
import org.bukkit.plugin.java.JavaPlugin;

public class EogendHarvest extends JavaPlugin {
    @Override
    public void onEnable() {
        getServer().getPluginManager().registerEvents(new HarvestListener(), this);
    }
}
