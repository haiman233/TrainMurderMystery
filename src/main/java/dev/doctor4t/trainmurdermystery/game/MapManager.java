package dev.doctor4t.trainmurdermystery.game;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import dev.doctor4t.trainmurdermystery.TMM;
import dev.doctor4t.trainmurdermystery.cca.AreasWorldComponent;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.phys.Vec3;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class MapManager {
    private static final Gson gson = new Gson();
    private static final Random random = new Random();
    
    /**
     * 保存当前地图配置到指定的地图文件
     * @param serverWorld 服务器世界
     * @param mapName 地图名称
     * @return 是否成功保存
     */
    public static boolean saveCurrentMap(ServerLevel serverWorld, String mapName) {
        try {
            // 获取AreasWorldComponent中的当前配置
            AreasWorldComponent areas = AreasWorldComponent.KEY.get(serverWorld);
            
            // 创建地图目录
            Path mapsDirPath = Paths.get(serverWorld.getServer().getServerDirectory().toString(), "world", "maps");
            File mapsDir = mapsDirPath.toFile();
            if (!mapsDir.exists()) {
                mapsDir.mkdirs();
            }
            
            // 构建地图配置文件路径
            Path mapConfigPath = Paths.get(mapsDirPath.toString(), mapName + ".json");
            File mapConfigFile = mapConfigPath.toFile();
            
            // 创建JSON对象并填充当前地图配置
            JsonObject jsonObject = new JsonObject();
            
            // 保存出生点位置
            jsonObject.addProperty("spawnX", areas.getSpawnPos().pos.x());
            jsonObject.addProperty("spawnY", areas.getSpawnPos().pos.y());
            jsonObject.addProperty("spawnZ", areas.getSpawnPos().pos.z());
            jsonObject.addProperty("spawnYaw", areas.getSpawnPos().yaw);
            jsonObject.addProperty("spawnPitch", areas.getSpawnPos().pitch);
            
            // 保存观战者出生点位置
            jsonObject.addProperty("spectatorSpawnX", areas.getSpectatorSpawnPos().pos.x());
            jsonObject.addProperty("spectatorSpawnY", areas.getSpectatorSpawnPos().pos.y());
            jsonObject.addProperty("spectatorSpawnZ", areas.getSpectatorSpawnPos().pos.z());
            jsonObject.addProperty("spectatorSpawnYaw", areas.getSpectatorSpawnPos().yaw);
            jsonObject.addProperty("spectatorSpawnPitch", areas.getSpectatorSpawnPos().pitch);
            
            // 保存准备区域
            jsonObject.addProperty("readyAreaMinX", areas.getReadyArea().minX);
            jsonObject.addProperty("readyAreaMinY", areas.getReadyArea().minY);
            jsonObject.addProperty("readyAreaMinZ", areas.getReadyArea().minZ);
            jsonObject.addProperty("readyAreaMaxX", areas.getReadyArea().maxX);
            jsonObject.addProperty("readyAreaMaxY", areas.getReadyArea().maxY);
            jsonObject.addProperty("readyAreaMaxZ", areas.getReadyArea().maxZ);
            
            // 保存游戏区域偏移
            jsonObject.addProperty("playAreaOffsetX", areas.getPlayAreaOffset().x());
            jsonObject.addProperty("playAreaOffsetY", areas.getPlayAreaOffset().y());
            jsonObject.addProperty("playAreaOffsetZ", areas.getPlayAreaOffset().z());
            
            // 保存游戏区域
            jsonObject.addProperty("playAreaMinX", areas.getPlayArea().minX);
            jsonObject.addProperty("playAreaMinY", areas.getPlayArea().minY);
            jsonObject.addProperty("playAreaMinZ", areas.getPlayArea().minZ);
            jsonObject.addProperty("playAreaMaxX", areas.getPlayArea().maxX);
            jsonObject.addProperty("playAreaMaxY", areas.getPlayArea().maxY);
            jsonObject.addProperty("playAreaMaxZ", areas.getPlayArea().maxZ);
            
            // 保存重置模板区域
            jsonObject.addProperty("resetTemplateAreaMinX", areas.getResetTemplateArea().minX);
            jsonObject.addProperty("resetTemplateAreaMinY", areas.getResetTemplateArea().minY);
            jsonObject.addProperty("resetTemplateAreaMinZ", areas.getResetTemplateArea().minZ);
            jsonObject.addProperty("resetTemplateAreaMaxX", areas.getResetTemplateArea().maxX);
            jsonObject.addProperty("resetTemplateAreaMaxY", areas.getResetTemplateArea().maxY);
            jsonObject.addProperty("resetTemplateAreaMaxZ", areas.getResetTemplateArea().maxZ);
            
            // 保存重置粘贴区域
            jsonObject.addProperty("resetPasteAreaMinX", areas.getResetPasteArea().minX);
            jsonObject.addProperty("resetPasteAreaMinY", areas.getResetPasteArea().minY);
            jsonObject.addProperty("resetPasteAreaMinZ", areas.getResetPasteArea().minZ);
            jsonObject.addProperty("resetPasteAreaMaxX", areas.getResetPasteArea().maxX);
            jsonObject.addProperty("resetPasteAreaMaxY", areas.getResetPasteArea().maxY);
            jsonObject.addProperty("resetPasteAreaMaxZ", areas.getResetPasteArea().maxZ);
            
            // 保存房间数量
            jsonObject.addProperty("roomCount", areas.getRoomCount());
            
            // 保存房间位置
            JsonObject roomPositionsObj = new JsonObject();
            for (int i = 1; i <= areas.getRoomCount(); i++) {
                Vec3 roomPos = areas.getRoomPosition(i);
                if (roomPos != null) {
                    JsonObject posObj = new JsonObject();
                    posObj.addProperty("x", roomPos.x());
                    posObj.addProperty("y", roomPos.y());
                    posObj.addProperty("z", roomPos.z());
                    roomPositionsObj.add(String.valueOf(i), posObj);
                }
            }
            jsonObject.add("roomPositions", roomPositionsObj);
            
            // 写入文件
            FileWriter writer = new FileWriter(mapConfigFile);
            gson.toJson(jsonObject, writer);
            writer.close();
            
            TMM.LOGGER.info("Successfully saved map: " + mapName);
            return true;
        } catch (Exception e) {
            TMM.LOGGER.error("Failed to save map: " + mapName, e);
            return false;
        }
    }
    
    /**
     * 加载指定的地图配置
     * @param serverWorld 服务器世界
     * @param mapName 地图名称
     * @return 是否成功加载
     */
    public static boolean loadMap(ServerLevel serverWorld, String mapName) {
        try {
            // 构建地图配置文件路径
            Path mapConfigPath = Paths.get(serverWorld.getServer().getServerDirectory().toString(), "world", "maps", mapName + ".json");
            File mapConfigFile = mapConfigPath.toFile();
            
            // 检查地图配置文件是否存在
            if (!mapConfigFile.exists()) {
                TMM.LOGGER.warn("Map configuration file does not exist: " + mapConfigFile.getAbsolutePath());
                return false;
            }
            
            // 获取AreasWorldComponent
            AreasWorldComponent areas = AreasWorldComponent.KEY.get(serverWorld);
            
            // 读取JSON文件
            FileReader reader = new FileReader(mapConfigFile);
            JsonObject jsonObject = JsonParser.parseReader(reader).getAsJsonObject();
            reader.close();
            
            // 应用配置到AreasWorldComponent
            if (jsonObject.has("spawnX") && jsonObject.has("spawnY") && jsonObject.has("spawnZ")) {
                areas.setSpawnPos(new AreasWorldComponent.PosWithOrientation(
                        jsonObject.get("spawnX").getAsDouble(),
                        jsonObject.get("spawnY").getAsDouble(),
                        jsonObject.get("spawnZ").getAsDouble(),
                        jsonObject.has("spawnYaw") ? jsonObject.get("spawnYaw").getAsFloat() : 0f,
                        jsonObject.has("spawnPitch") ? jsonObject.get("spawnPitch").getAsFloat() : 0f
                ));
            }
            
            if (jsonObject.has("spectatorSpawnX") && jsonObject.has("spectatorSpawnY") && jsonObject.has("spectatorSpawnZ")) {
                areas.setSpectatorSpawnPos(new AreasWorldComponent.PosWithOrientation(
                        jsonObject.get("spectatorSpawnX").getAsDouble(),
                        jsonObject.get("spectatorSpawnY").getAsDouble(),
                        jsonObject.get("spectatorSpawnZ").getAsDouble(),
                        jsonObject.has("spectatorSpawnYaw") ? jsonObject.get("spectatorSpawnYaw").getAsFloat() : 0f,
                        jsonObject.has("spectatorSpawnPitch") ? jsonObject.get("spectatorSpawnPitch").getAsFloat() : 0f
                ));
            }
            
            if (jsonObject.has("readyAreaMinX") && jsonObject.has("readyAreaMinY") && jsonObject.has("readyAreaMinZ") &&
                jsonObject.has("readyAreaMaxX") && jsonObject.has("readyAreaMaxY") && jsonObject.has("readyAreaMaxZ")) {
                areas.setReadyArea(new net.minecraft.world.phys.AABB(
                        jsonObject.get("readyAreaMinX").getAsDouble(),
                        jsonObject.get("readyAreaMinY").getAsDouble(),
                        jsonObject.get("readyAreaMinZ").getAsDouble(),
                        jsonObject.get("readyAreaMaxX").getAsDouble(),
                        jsonObject.get("readyAreaMaxY").getAsDouble(),
                        jsonObject.get("readyAreaMaxZ").getAsDouble()
                ));
            }
            
            if (jsonObject.has("playAreaOffsetX") && jsonObject.has("playAreaOffsetY") && jsonObject.has("playAreaOffsetZ")) {
                areas.setPlayAreaOffset(new Vec3(
                        jsonObject.get("playAreaOffsetX").getAsDouble(),
                        jsonObject.get("playAreaOffsetY").getAsDouble(),
                        jsonObject.get("playAreaOffsetZ").getAsDouble()
                ));
            }
            
            if (jsonObject.has("playAreaMinX") && jsonObject.has("playAreaMinY") && jsonObject.has("playAreaMinZ") &&
                jsonObject.has("playAreaMaxX") && jsonObject.has("playAreaMaxY") && jsonObject.has("playAreaMaxZ")) {
                areas.setPlayArea(new net.minecraft.world.phys.AABB(
                        jsonObject.get("playAreaMinX").getAsDouble(),
                        jsonObject.get("playAreaMinY").getAsDouble(),
                        jsonObject.get("playAreaMinZ").getAsDouble(),
                        jsonObject.get("playAreaMaxX").getAsDouble(),
                        jsonObject.get("playAreaMaxY").getAsDouble(),
                        jsonObject.get("playAreaMaxZ").getAsDouble()
                ));
            }
            
            if (jsonObject.has("resetTemplateAreaMinX") && jsonObject.has("resetTemplateAreaMinY") && jsonObject.has("resetTemplateAreaMinZ") &&
                jsonObject.has("resetTemplateAreaMaxX") && jsonObject.has("resetTemplateAreaMaxY") && jsonObject.has("resetTemplateAreaMaxZ")) {
                areas.setResetTemplateArea(new net.minecraft.world.phys.AABB(
                        jsonObject.get("resetTemplateAreaMinX").getAsDouble(),
                        jsonObject.get("resetTemplateAreaMinY").getAsDouble(),
                        jsonObject.get("resetTemplateAreaMinZ").getAsDouble(),
                        jsonObject.get("resetTemplateAreaMaxX").getAsDouble(),
                        jsonObject.get("resetTemplateAreaMaxY").getAsDouble(),
                        jsonObject.get("resetTemplateAreaMaxZ").getAsDouble()
                ));
            }
            
            if (jsonObject.has("resetPasteAreaMinX") && jsonObject.has("resetPasteAreaMinY") && jsonObject.has("resetPasteAreaMinZ") &&
                jsonObject.has("resetPasteAreaMaxX") && jsonObject.has("resetPasteAreaMaxY") && jsonObject.has("resetPasteAreaMaxZ")) {
                areas.setResetPasteArea(new net.minecraft.world.phys.AABB(
                        jsonObject.get("resetPasteAreaMinX").getAsDouble(),
                        jsonObject.get("resetPasteAreaMinY").getAsDouble(),
                        jsonObject.get("resetPasteAreaMinZ").getAsDouble(),
                        jsonObject.get("resetPasteAreaMaxX").getAsDouble(),
                        jsonObject.get("resetPasteAreaMaxY").getAsDouble(),
                        jsonObject.get("resetPasteAreaMaxZ").getAsDouble()
                ));
            }
            
            if (jsonObject.has("roomCount")) {
                areas.setRoomCount(jsonObject.get("roomCount").getAsInt());
            }
            
            if (jsonObject.has("roomPositions")) {
                JsonObject roomPositionsObj = jsonObject.getAsJsonObject("roomPositions");
                areas.getRoomPositions().clear();
                for (String key : roomPositionsObj.keySet()) {
                    try {
                        int roomNumber = Integer.parseInt(key);
                        JsonObject posObj = roomPositionsObj.getAsJsonObject(key);
                        Vec3 position = new Vec3(
                                posObj.get("x").getAsDouble(),
                                posObj.get("y").getAsDouble(),
                                posObj.get("z").getAsDouble()
                        );
                        areas.getRoomPositions().put(roomNumber, position);
                    } catch (NumberFormatException e) {
                        TMM.LOGGER.warn("Invalid room number in map config: " + key);
                    }
                }
            }
            
            // 同步到客户端
            areas.sync();
            
            TMM.LOGGER.info("Successfully loaded map: " + mapName);
            return true;
        } catch (Exception e) {
            TMM.LOGGER.error("Failed to load map: " + mapName, e);
            return false;
        }
    }
    
    /**
     * 随机加载一个可用的地图配置
     * @param serverWorld 服务器世界
     * @return 是否成功加载随机地图
     */
    public static boolean loadRandomMap(ServerLevel serverWorld) {
        List<String> availableMaps = getAvailableMaps(serverWorld);
        
        if (availableMaps.isEmpty()) {
            TMM.LOGGER.warn("No maps available to load randomly");
            return false;
        }
        
        // 随机选择一个地图
        String randomMap = availableMaps.get(random.nextInt(availableMaps.size()));
        TMM.LOGGER.info("Randomly selected map: " + randomMap);
        
        return loadMap(serverWorld, randomMap);
    }
    
    /**
     * 获取所有可用的地图列表
     * @param serverWorld 服务器世界
     * @return 可用地图名称列表
     */
    public static List<String> getAvailableMaps(ServerLevel serverWorld) {
        List<String> maps = new ArrayList<>();
        
        try {
            Path mapsDirPath = Paths.get(serverWorld.getServer().getServerDirectory().toString(), "world", "maps");
            File mapsDir = mapsDirPath.toFile();
            
            if (mapsDir.exists() && mapsDir.isDirectory()) {
                File[] files = mapsDir.listFiles((dir, name) -> name.toLowerCase().endsWith(".json"));
                if (files != null) {
                    for (File file : files) {
                        String fileName = file.getName();
                        String mapName = fileName.substring(0, fileName.length() - 5); // 移除.json后缀
                        maps.add(mapName);
                    }
                }
            }
        } catch (Exception e) {
            TMM.LOGGER.error("Failed to list available maps", e);
        }
        
        return maps;
    }
}