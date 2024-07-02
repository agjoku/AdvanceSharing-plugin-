package agjoku.avancementsharing;


import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerAdvancementDoneEvent;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashSet;
import java.util.Set;

public class AdvancementSharing extends JavaPlugin implements Listener {
    private final Set<String> advancementsTracked = new HashSet<>();

    @Override
    public void onEnable() {
        // プラグインが有効化されたときにリスナーを登録します
        getServer().getPluginManager().registerEvents(this, this);
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
        String csvFile = Paths.get(getDataFolder().getPath(), "advancements.csv").toString();
        File file = new File(csvFile);
        boolean isNewFile = !file.exists();

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(csvFile, true))) {
            if (isNewFile) {
                writer.write("PlayerName,AdvancementName,Date,Time"); // ヘッダー行を書く
                writer.newLine();
            }

            LocalDateTime now = LocalDateTime.now();
            String date = now.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
            String time = now.format(DateTimeFormatter.ofPattern("HH:mm:ss"));

            writer.write(playerName + "," + advancementName + "," + date + "," + time);
            writer.newLine();
            getLogger().info("Recorded advancement: " + advancementName + " by " + playerName + " on " + date + " at " + time);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
