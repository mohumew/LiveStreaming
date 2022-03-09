package me.bearleonlight.com.livestreaming.listener;

import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockCanBuildEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.persistence.PersistentDataType;

public class LiveStreamingProtect implements Listener {
    @EventHandler(priority = EventPriority.HIGHEST)
    private void onPlayerInteractEvent(PlayerInteractEvent event) {
        if (event.getPlayer().getPersistentDataContainer().get(me.bearleonlight.com.livestreaming.event.LiveStreamingProtect.namespacedKey, PersistentDataType.DOUBLE) == 1.0) {
            event.setCancelled(true);
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    private void onBlockCanBuildEvent(BlockCanBuildEvent event) {
        if (event.getPlayer().getPersistentDataContainer().get(me.bearleonlight.com.livestreaming.event.LiveStreamingProtect.namespacedKey, PersistentDataType.DOUBLE) == 1.0) {
            event.setBuildable(false);
        }
    }


}
