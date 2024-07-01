package agjoku.avancementsharing;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerAdvancementDoneEvent;
import org.bukkit.plugin.java.JavaPlugin;

public class AdvancementSharing extends JavaPlugin implements Listener {

    @Override
    public void onEnable() {
        // プラグインが有効化されたときにリスナーを登録します
        getServer().getPluginManager().registerEvents(this, this);
    }

    @EventHandler
    public void onPlayerAdvancementDone(PlayerAdvancementDoneEvent event) {
        // プレイヤー名を取得
        String playerName = event.getPlayer().getName();
        // 進捗名を取得
        String advancementName = event.getAdvancement().getKey().getKey();

        if(advancementName.startsWith("recipes/")) {
            return;
        }

        for (Player onlinePlayer : Bukkit.getOnlinePlayers()){
            onlinePlayer.sendMessage(playerName + "が進捗" + advancementName + "を達成しました");
        }
    }
}
