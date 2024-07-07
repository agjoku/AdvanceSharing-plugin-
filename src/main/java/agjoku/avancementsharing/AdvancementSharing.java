package agjoku.avancementsharing;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerAdvancementDoneEvent;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.*;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashSet;
import java.util.Set;

public class AdvancementSharing extends JavaPlugin implements Listener {
    private final Set<String> advancementsTracked = new HashSet<>();
    private String csvOutputPath;

    @Override
    public void onEnable() {
        if (!getDataFolder().exists()) {
            getDataFolder().mkdirs();
        }
        // プラグインが有効化されたときにリスナーを登録します
        getServer().getPluginManager().registerEvents(this, this);
        this.saveDefaultConfig();

        csvOutputPath = getConfig().getString("csv-output-path", "advancements.csv");
    }

    @EventHandler
    public void onPlayerAchievementAwarded(PlayerAdvancementDoneEvent event) {
        String advancementName = event.getAdvancement().getKey().toString();
        String playerName = event.getPlayer().getName();

        if(advancementsTracked.contains(advancementName) || advancementName.startsWith("minecraft:recipes/")) {
            return;
        }

        advancementsTracked.add(advancementName);
        writeAdvancementToCsv(playerName, advancementName);

    }

    private void writeAdvancementToCsv(String playerName, String advancementName) {

            LocalDateTime now = LocalDateTime.now();
            String date = now.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
            String time = now.format(DateTimeFormatter.ofPattern("HH:mm:ss"));
            String data = playerName + "," + advancementName + "," + date + "," + time + "\n";

            writeCSVToFile(Paths.get(getDataFolder().getPath(), "advancements.csv"), data, advancementName);
            writeCSVToFile(Paths.get(csvOutputPath), data, advancementName);

            getLogger().info("Recorded advancement: " + advancementName + " by " + playerName + " on " + date + " at " + time);
    }

    private void writeCSVToFile(Path paths, String data, String advancementName) {
        File file = paths.toFile();
        boolean isNewFile = !file.exists();


        try (BufferedWriter writer = new BufferedWriter(new FileWriter(file, true))) {
            if (isNewFile) {
                writer.write("PlayerName,AdvancementName,Date,Time"); // ヘッダー行を書く
                writer.newLine();
            }

            if(!isAdvancementRecorded(file, advancementName)){
                writer.write(data);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private boolean isAdvancementRecorded(File file, String advancementName) {
        if (!file.exists()) {
            return false;
        }

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length >= 2 && parts[1].equals(advancementName)) {
                    return true; // すでに記録されている
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return false;
    }
}
