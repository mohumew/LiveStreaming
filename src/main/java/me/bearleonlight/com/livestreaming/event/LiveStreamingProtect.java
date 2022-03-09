package me.bearleonlight.com.livestreaming.event;

import net.kyori.adventure.text.Component;
import org.bukkit.NamespacedKey;
import org.bukkit.Server;
import org.bukkit.entity.Player;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.Plugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.*;

public class LiveStreamingProtect {
    public static NamespacedKey namespacedKey = NamespacedKey.fromString("LiveStreaming");
    public Map<UUID, Integer> LiveStreamingProtectList = new LinkedHashMap<>();
    public List<UUID> LiveStreamingProtectBypassList = new LinkedList<>();
    protected Server server;

    public LiveStreamingProtect(Server server) {
        this.server = server;
    }

    public void addPBypass(UUID uuid, Integer integer) {
        if (LiveStreamingProtectBypassList.contains(uuid)) return;
        LiveStreamingProtectBypassList.add(uuid);
    }

    public void delPBypass(UUID uuid, Integer integer) {
        if (!LiveStreamingProtectBypassList.contains(uuid)) return;
        LiveStreamingProtectBypassList.remove(uuid);
    }

    public void addPlayer(UUID uuid, Integer integer) {
        if (LiveStreamingProtectList.containsKey(uuid)) return;
        LiveStreamingProtectList.put(uuid, integer);
    }

    public void upPlayer(UUID uuid, Integer integer) {
        LiveStreamingProtectList.put(uuid, integer);
    }

    public void delPlayer(UUID uuid) {
        if (!LiveStreamingProtectList.containsKey(uuid)) return;
        LiveStreamingProtectList.remove(uuid);
    }

    public void TimeCheck(Plugin plugin) {
        new BukkitRunnable() {
            @Override
            public void run() {
                LiveStreamingProtectList.forEach((uuid, integer) -> {
                    List<Player> players1 = new LinkedList<>();
                    List<Player> players2 = new LinkedList<>();
                    server.getPlayer(uuid).getLocation().getNearbyPlayers(integer.doubleValue() + 8).forEach(player -> {
                        if (LiveStreamingProtectBypassList.contains(player.getUniqueId())) return;
                        if (!player.hasPermission("LiveStreaming.Protect.bypass") && !player.hasPermission("group.1-stream"))
                            players2.add(player);
                    });
                    server.getPlayer(uuid).getLocation().getNearbyPlayers(integer.doubleValue()).forEach(player -> {
                        if (LiveStreamingProtectBypassList.contains(player.getUniqueId())) return;
                        if (!player.hasPermission("LiveStreaming.Protect.bypass") && !player.hasPermission("group.1-stream"))
                            players1.add(player);
                    });
                    players2.removeAll(players1);
                    Component component = Component.translatable("你即將進入VT直播範圍，進入後將受到管制");
                    players2.forEach(player -> {
                        player.sendActionBar(component);
                    });
                    PotionEffect potionEffect = PotionEffectType.INVISIBILITY.createEffect(80, 1);
                    players1.forEach(player -> {
                        player.addPotionEffect(potionEffect);
                        player.getPersistentDataContainer().set(namespacedKey, PersistentDataType.DOUBLE, 1.0);
                    });
                });
            }
        }.runTaskTimer(plugin, 0L, 40L);
    }
}
