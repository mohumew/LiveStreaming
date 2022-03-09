package me.bearleonlight.com.livestreaming;

import java.util.UUID;

public class PlayerStatus {
    private UUID PlayerUUID;
    private boolean LiveStreaming = false;
    private boolean LiveStreamingProtect = false;
    private int LiveStreamingProtectRange = 15;

    public PlayerStatus(UUID playerUUID) {
        this.setPlayerUUID(playerUUID);
    }

    public UUID getPlayerUUID() {
        return PlayerUUID;
    }

    public void setPlayerUUID(UUID playerUUID) {
        PlayerUUID = playerUUID;
    }

    public boolean isLiveStreaming() {
        return LiveStreaming;
    }

    public void setLiveStreaming(boolean liveStreaming) {
        LiveStreaming = liveStreaming;
    }

    public boolean isLiveStreamingProtect() {
        return LiveStreamingProtect;
    }

    public void setLiveStreamingProtect(boolean liveStreamingProtect) {
        LiveStreamingProtect = liveStreamingProtect;
    }

    public int getLiveStreamingRange() {
        return LiveStreamingProtectRange;
    }

    public void setLiveStreamingRange(int liveStreamingRange) {
        LiveStreamingProtectRange = liveStreamingRange;
    }
}
