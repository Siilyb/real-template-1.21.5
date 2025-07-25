package com.real.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.real.real;
import net.fabricmc.loader.api.FabricLoader;

import java.awt.Color;
import java.io.*;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;

public class ConfigManager {
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static final File CONFIG_FILE = new File(FabricLoader.getInstance().getConfigDir().toFile(), "real-mod-config.json");
    
    public static class BlockHighlighterConfig {
        public Map<String, String> keywords = new HashMap<>(); // keyword -> color hex
        public boolean enabled = false;
        public int scanRadius = 20;
        public int refreshInterval = 60; // ticks
        
        public BlockHighlighterConfig() {
            // 默认关键词和颜色
            keywords.put("flowering", "#3CDCFF");
            keywords.put("diamond", "#00FFFF");
        }
    }
    
    public static class EntityHighlighterConfig {
        public Map<String, String> entityTypes = new HashMap<>(); // entity type -> color hex
        public boolean enabled = false;
        public int scanRadius = 20;
        public int refreshInterval = 60; // ticks
        public boolean showLabels = true;
        
        public EntityHighlighterConfig() {
            // 默认实体类型和颜色
            entityTypes.put("zombie", "#FF0000");
            entityTypes.put("skeleton", "#FFFFFF");
            entityTypes.put("cow", "#8B4513");
            entityTypes.put("pig", "#FFC0CB");
            entityTypes.put("sheep", "#F0F8FF");
            entityTypes.put("chicken", "#FFFF00");
        }
    }
    
    public static class KeyBindingConfig {
        public String toggleBlockHighlighter = "P";
        public String toggleEntityHighlighter = "E";
        public String openConfigMenu = "O";
    }
    
    public static class ModConfig {
        public BlockHighlighterConfig blockHighlighter = new BlockHighlighterConfig();
        public EntityHighlighterConfig entityHighlighter = new EntityHighlighterConfig();
        public KeyBindingConfig keyBindings = new KeyBindingConfig();
    }
    
    private static ModConfig config = new ModConfig();
    
    public static ModConfig getConfig() {
        return config;
    }
    
    public static void loadConfig() {
        if (!CONFIG_FILE.exists()) {
            saveConfig();
            return;
        }
        
        try (FileReader reader = new FileReader(CONFIG_FILE)) {
            Type type = new TypeToken<ModConfig>(){}.getType();
            ModConfig loadedConfig = GSON.fromJson(reader, type);
            if (loadedConfig != null) {
                config = loadedConfig;
            }
        } catch (IOException e) {
            real.LOGGER.error("Failed to load config", e);
        }
    }
    
    public static void saveConfig() {
        try (FileWriter writer = new FileWriter(CONFIG_FILE)) {
            GSON.toJson(config, writer);
        } catch (IOException e) {
            real.LOGGER.error("Failed to save config", e);
        }
    }
    
    public static Color getColorFromHex(String hex) {
        try {
            return Color.decode(hex);
        } catch (NumberFormatException e) {
            return new Color(60, 220, 255, 200); // 默认颜色
        }
    }
    
    public static String getHexFromColor(Color color) {
        return String.format("#%02X%02X%02X", color.getRed(), color.getGreen(), color.getBlue());
    }
} 