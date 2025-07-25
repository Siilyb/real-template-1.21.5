package com.real.features;

import com.real.config.ConfigManager;
import com.real.util.WorldRenderUtils;
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderContext;
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderEvents;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import net.minecraft.registry.Registries;
import net.minecraft.util.Identifier;

import java.awt.Color;
import java.util.*;

public class EntityHighlighter {
    private static boolean enabled = false;
    private static Set<Entity> trackedEntities = new HashSet<>();
    private static int tickCounter = 0;
    
    public static void initialize() {
        WorldRenderEvents.LAST.register(EntityHighlighter::onWorldRender);
    }
    
    public static void toggle() {
        enabled = !enabled;
        if (!enabled) {
            trackedEntities.clear();
        }
    }
    
    public static boolean isEnabled() {
        return enabled;
    }
    
    public static void setEnabled(boolean enabled) {
        EntityHighlighter.enabled = enabled;
        if (!enabled) {
            trackedEntities.clear();
        }
    }
    
    private static void onWorldRender(WorldRenderContext context) {
        if (!enabled) return;
        
        MinecraftClient client = MinecraftClient.getInstance();
        if (client.player == null || client.world == null) return;
        
        ConfigManager.EntityHighlighterConfig config = ConfigManager.getConfig().entityHighlighter;
        
        // 每隔一定时间刷新实体列表
        tickCounter++;
        if (tickCounter >= config.refreshInterval) {
            tickCounter = 0;
            refreshTrackedEntities();
        }
        
        // 绘制所有跟踪的实体
        for (Entity entity : trackedEntities) {
            if (entity.isRemoved()) continue;
            
            String entityName = getEntityName(entity);
            Color color = getColorForEntity(entityName);
            Color colorFilling=new Color(color.getRed(),color.getGreen(),color.getBlue(),30);
            // 获取实体碰撞箱
            Box boundingBox = entity.getBoundingBox();
            
            // 绘制线框
            WorldRenderUtils.drawWireFrame(context, boundingBox, color, 2.0f, false);

            //绘制填充

            WorldRenderUtils.drawBox(context,boundingBox.getMinPos().getX(),boundingBox.getMinPos().getY(),boundingBox.getMinPos().getZ(),
                    boundingBox.getLengthX(), boundingBox.getLengthY(), boundingBox.getLengthZ(), colorFilling,false);
            
            // 如果启用了标签显示，绘制实体名称
            if (config.showLabels) {
                Vec3d entityPos = entity.getPos().add(0, entity.getHeight() + 0.5, 0);
                WorldRenderUtils.drawText(
                    net.minecraft.text.Text.literal(entityName),
                    context,
                    entityPos,
                    false,
                    1.0f
                );
            }
            //shulker距离计算

            if(client.player.squaredDistanceTo(entity)<=100){
                if(entityName.equals("shulker")){

                    Vec3d shulkerPos = entity.getPos().add(0, entity.getHeight() + 1.5, 0);
                    if(client.player.distanceTo(entity)>=5.5&&client.player.distanceTo(entity)<=7.5){
                        WorldRenderUtils.drawText(
                                net.minecraft.text.Text.literal(String.valueOf(String.format("%.2f",  client.player.distanceTo(entity)))).formatted(Formatting.GREEN),
                                context,
                                shulkerPos,
                                false,
                                2.0f
                        );
                    }
                    else{
                        WorldRenderUtils.drawText(
                                net.minecraft.text.Text.literal(String.valueOf(String.format("%.2f",  client.player.distanceTo(entity)))),
                                context,
                                shulkerPos,
                                false,
                                2.0f
                        );
                    }

                }
            }

        }
    }
    
    private static void refreshTrackedEntities() {
        MinecraftClient client = MinecraftClient.getInstance();
        if (client.player == null || client.world == null) return;
        
        ConfigManager.EntityHighlighterConfig config = ConfigManager.getConfig().entityHighlighter;
        trackedEntities.clear();
        
        Vec3d playerPos = client.player.getPos();
        
        // 扫描周围的实体
        for (Entity entity : client.world.getEntitiesByClass(Entity.class, 
            new Box(playerPos.subtract(config.scanRadius, config.scanRadius, config.scanRadius),
                    playerPos.add(config.scanRadius, config.scanRadius, config.scanRadius)),
            e -> true)) {
            
            // 跳过玩家自己
            if (entity == client.player) continue;
            
            String entityName = getEntityName(entity);
            
            // 检查是否匹配关键词
            for (String keyword : config.entityTypes.keySet()) {
                if (entityName.toLowerCase().contains(keyword.toLowerCase())) {
                    trackedEntities.add(entity);
                    break;
                }
            }
        }
    }
    
    private static String getEntityName(Entity entity) {
        EntityType<?> entityType = entity.getType();
        Identifier entityId = Registries.ENTITY_TYPE.getId(entityType);
        return entityId.getPath();
    }
    
    private static Color getColorForEntity(String entityName) {
        ConfigManager.EntityHighlighterConfig config = ConfigManager.getConfig().entityHighlighter;
        
        // 寻找匹配的关键词
        for (Map.Entry<String, String> entry : config.entityTypes.entrySet()) {
            if (entityName.toLowerCase().contains(entry.getKey().toLowerCase())) {
                return ConfigManager.getColorFromHex(entry.getValue());
            }
        }
        
        // 默认颜色
        return new Color(255, 255, 255, 200);
    }
} 