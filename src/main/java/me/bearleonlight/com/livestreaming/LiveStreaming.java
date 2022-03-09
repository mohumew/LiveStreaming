package me.bearleonlight.com.livestreaming;

import me.bearleonlight.com.livestreaming.event.LiveStreamingProtect;
import net.luckperms.api.LuckPerms;
import net.luckperms.api.model.user.User;
import net.luckperms.api.node.Node;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

public final class LiveStreaming extends JavaPlugin {
    LiveStreamingProtect liveStreamingProtect;
    LuckPerms luckPerms;
    private Map<UUID, PlayerStatus> playerStatusMap = new LinkedHashMap<>();

    @Override
    public void onEnable() {
        // Plugin startup logic
        /*try {
            luckPerms = LuckPermsProvider.get();
        }catch (IllegalStateException e){
            this.getLogger().info("沒有找到LuckPerms");
            this.setEnabled(false);
        }*/
        RegisteredServiceProvider<LuckPerms> provider = Bukkit.getServicesManager().getRegistration(LuckPerms.class);
        if (provider != null) {
            luckPerms = provider.getProvider();
        }
        liveStreamingProtect = new LiveStreamingProtect(this.getServer());
        getServer().getPluginManager().registerEvents(new me.bearleonlight.com.livestreaming.listener.LiveStreamingProtect(), this);
        liveStreamingProtect.TimeCheck(this);
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("該命令只有玩家可以使用");
            return true;
        }
        Player p = (Player) sender;
        User user = luckPerms.getUserManager().getUser(p.getUniqueId());
        if (!CheckPermission(p, "LiveStreaming", true)) return true;
        String arg;
        switch (args.length) {
            case 1:
                arg = args[0];
                if (arg.toUpperCase().matches(".*(ON|ENABLE|START).*")) {
                    if (setLiveStreamingStatus(p.getUniqueId(), true)) {
                        p.sendMessage("開啟直播模式");
                    } else {
                        p.sendMessage("您已開啟直播模式了");
                    }
                    ;

                    return true;
                } else if (arg.toUpperCase().matches(".*(OFF|DISABLE|START).*")) {
                    if (setLiveStreamingStatus(p.getUniqueId(), false)) {
                        p.sendMessage("關閉直播模式");
                    } else {
                        p.sendMessage("您已關閉直播模式了");
                    }
                    ;
                }
                return true;
            case 2:
                arg = args[1];
                switch (args[0]) {
                    case "Protect": {
                        if (arg.equalsIgnoreCase("on")) {
                            if (setProtect(p.getUniqueId(), true)) {
                                p.sendMessage("開啟直播保護模式");
                            } else {
                                p.sendMessage("您需要先開起直播模式");
                            }

                            return true;
                        } else if (arg.equalsIgnoreCase("off")) {
                            setProtect(p.getUniqueId(), false);
                            p.sendMessage("關閉直播保護模式");
                            return true;
                        }
                        return true;
                    }
                }
                return true;
        }
        return true;
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String alias, @NotNull String[] args) {
        List<String> list = new LinkedList<>();
        if (!command.getName().equalsIgnoreCase("LiveStreaming")) return null;
        if (!(sender instanceof Player)) {
            return list;
        }

        Player p = (Player) sender;
        if (!CheckPermission(p, "LiveStreaming", false)) return list;
        String arg;
        switch (args.length) {
            case 1:
                if (CheckPermission(p, "LiveStreaming.on", false)) list.add("on");
                if (CheckPermission(p, "LiveStreaming.off", false)) list.add("off");
                if (CheckPermission(p, "LiveStreaming.Protect", false)) list.add("Protect");
                break;
            case 2:
                switch (args[0]) {
                    case "Protect":
                        if (CheckPermission(p, "LiveStreaming.Protect.on", false)) list.add("on");
                        if (CheckPermission(p, "LiveStreaming.Protect.off", false)) list.add("off");
                        break;
                }
                break;
        }
        return list;

    }

    public boolean setLiveStreamingStatus(UUID uuid, boolean Status) {
        PlayerStatus playerStatus;
        if (playerStatusMap.containsKey(uuid)) {
            playerStatus = playerStatusMap.get(uuid);

        } else {
            playerStatus = new PlayerStatus(uuid);
        }
        if (playerStatus.isLiveStreaming() == Status) return false;
        if (playerStatus.isLiveStreamingProtect()) {
            setProtect(uuid, false);
        }
        playerStatus.setLiveStreaming(Status);
        User user = luckPerms.getUserManager().getUser(uuid);
        user.data().add(Node.builder("group.1-stream").value(Status).build());
        luckPerms.getUserManager().saveUser(user);
        playerStatusMap.put(uuid, playerStatus);
        return true;
    }

    public boolean setProtect(UUID uuid, boolean Status) {
        PlayerStatus playerStatus;
        if (playerStatusMap.containsKey(uuid)) {
            playerStatus = playerStatusMap.get(uuid);
        } else {
            playerStatus = new PlayerStatus(uuid);
        }
        if (!playerStatus.isLiveStreaming()) {
            return false;
        }
        playerStatus.setLiveStreamingProtect(Status);
        if (Status) {
            liveStreamingProtect.addPlayer(uuid, playerStatus.getLiveStreamingRange());
        } else {
            liveStreamingProtect.delPlayer(uuid);
        }
        playerStatusMap.put(uuid, playerStatus);
        return true;
    }

    public boolean CheckPermission(Player p, String permission, boolean sendMessage) {
        if (p.hasPermission(permission)) {
            return true;
        } else {
            if (sendMessage == true) p.sendMessage("您沒有權限!");
            return false;
        }
    }

}
