package dev.doctor4t.trainmurdermystery.data;

import com.google.gson.Gson;
import net.minecraft.server.level.ServerPlayer;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

public class ServerMapConfig {
    private static final Gson gson = new Gson();
    private static ServerMapConfig instance;
    
    private List<MapConfig.MapEntry> maps;
    private final Path configPath = Paths.get("world", "tmm_maps.json");
    
    public static synchronized ServerMapConfig getInstance() {
        if (instance == null) {
            instance = loadOrCreateConfig();
        }
        return instance;
    }
    
    public static synchronized void reload() {
        instance = loadOrCreateConfig();
    }
    
    private static ServerMapConfig loadOrCreateConfig() {
        ServerMapConfig config = new ServerMapConfig();
        
        // 尝试从服务器配置目录加载配置
        if (Files.exists(config.configPath)) {
            try (BufferedReader reader = Files.newBufferedReader(config.configPath, StandardCharsets.UTF_8)) {
                MapConfig loadedConfig = gson.fromJson(reader, MapConfig.class);
                if (loadedConfig != null && loadedConfig.getMaps() != null) {
                    config.maps = loadedConfig.getMaps();
                    return config;
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        
        // 如果配置文件不存在或加载失败，使用默认配置并保存
        MapConfig defaultConfig = MapConfig.createDefaultConfig();
        config.maps = defaultConfig.getMaps();
        config.saveConfig();
        return config;
    }
    
    public void saveConfig() {
        try {
            // 确保配置目录存在
            Files.createDirectories(configPath.getParent());
            
            MapConfig tempConfig = new MapConfig();
            tempConfig.maps = this.maps;
            
            try (Writer writer = Files.newBufferedWriter(configPath, StandardCharsets.UTF_8)) {
                gson.toJson(tempConfig, writer);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    public List<MapConfig.MapEntry> getMaps() {
        return maps;
    }
    
    public MapConfig.MapEntry getMapById(String id) {
        if (maps != null) {
            for (MapConfig.MapEntry entry : maps) {
                if (entry.getId().equals(id)) {
                    return entry;
                }
            }
        }
        return null;
    }
}