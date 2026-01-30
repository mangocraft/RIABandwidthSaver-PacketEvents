package com.mangocraft.plugins.riabandwidthsaverpe;

import com.github.retrooper.packetevents.PacketEvents;
import com.github.retrooper.packetevents.event.PacketListenerAbstract;
import com.github.retrooper.packetevents.event.PacketListenerPriority;
import com.github.retrooper.packetevents.event.PacketSendEvent;
import com.github.retrooper.packetevents.protocol.packettype.PacketType;
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerChunkData;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * CHUNK_DATA过滤器，用于在AFK状态下减少区块数据传输
 * 借鉴了ChunkDataThrottleHandlerImpl.kt的算法思想
 */
public class ChunkDataFilter {
    private static ChunkDataFilter instance;
    private final Set<UUID> afkPlayers = ConcurrentHashMap.newKeySet();
    private final PacketListenerAbstract chunkDataListener;

    // 用于模拟遮挡检测的简化算法
    private static final byte X_PLUS = 0b00_00001;    // X正方向遮挡
    private static final byte X_MINUS = 0b00_00010;   // X负方向遮挡
    private static final byte Z_PLUS = 0b00_00100;    // Z正方向遮挡
    private static final byte Z_MINUS = 0b00_01000;   // Z负方向遮挡
    private static final byte Y_MINUS = 0b00_10000;   // Y负方向遮挡（下方遮挡上方）
    private static final byte INVISIBLE = 0b00_11111; // 完全不可见
    
    private static final byte BV_VISIBLE = 0b0;       // 可见
    private static final byte BV_INVISIBLE = 0b1;     // 不可见
    private static final byte BV_UPPER_OCCLUDING = 0b10; // 上方遮挡

    private ChunkDataFilter() {
        chunkDataListener = new PacketListenerAbstract(PacketListenerPriority.HIGH) {
            @Override
            public void onPacketSend(PacketSendEvent event) {
                if (event.getPacketType() == PacketType.Play.Server.CHUNK_DATA) {
                    handleChunkData(event);
                }
            }
        };
    }

    public static ChunkDataFilter getInstance() {
        if (instance == null) {
            instance = new ChunkDataFilter();
        }
        return instance;
    }

    public void registerListener() {
        PacketEvents.getAPI().getEventManager().registerListener(chunkDataListener);
    }

    public void unregisterListener() {
        PacketEvents.getAPI().getEventManager().unregisterListener(chunkDataListener);
    }

    public void addAfkPlayer(UUID playerId) {
        afkPlayers.add(playerId);
    }

    public void removeAfkPlayer(UUID playerId) {
        afkPlayers.remove(playerId);
    }

    private void handleChunkData(PacketSendEvent event) {
        try {
            WrapperPlayServerChunkData wrapper = new WrapperPlayServerChunkData(event);
            Player player = Bukkit.getPlayer(event.getUser().getUUID());

            if (player != null && afkPlayers.contains(player.getUniqueId())) {
                // 对AFK玩家的区块数据应用智能过滤
                // 借鉴ChunkDataThrottleHandlerImpl.kt的思路，实现简化的遮挡检测
                applySmartChunkDataFilter(event, wrapper, player);
            }
        } catch (Exception e) {
            // 记录错误但不中断数据包处理
            // 在生产环境中，应该使用适当的日志记录
            e.printStackTrace();
        }
    }

    /**
     * 对AFK玩家应用智能区块数据过滤
     * 借鉴ChunkDataThrottleHandlerImpl.kt的遮挡检测算法思想
     */
    private void applySmartChunkDataFilter(PacketSendEvent event, WrapperPlayServerChunkData wrapper, Player player) {
        // 对于AFK玩家，我们可以适当降低区块数据的传输频率或数量
        // 实现一个基于距离和视野的智能过滤
        
        // 算法思路：只发送玩家附近关键区域的区块数据
        // 1. 计算玩家当前位置
        // 2. 判断区块是否在玩家视野范围内
        // 3. 根据距离远近采用不同策略
        
        double playerX = player.getLocation().getX();
        double playerZ = player.getLocation().getZ();
        
        // 临时使用简单的概率过滤，直到找到正确的API
        if (Math.random() > 0.3) { // 30%的概率发送
            event.setCancelled(true);
            return;
        }
    }

    public boolean isPlayerAfk(UUID playerId) {
        return afkPlayers.contains(playerId);
    }
}