package pl.extollite.hidenseek.listener;

import cn.nukkit.Player;
import cn.nukkit.event.EventHandler;
import cn.nukkit.event.EventPriority;
import cn.nukkit.event.Listener;
import cn.nukkit.event.player.PlayerCommandPreprocessEvent;
import pl.extollite.hidenseek.HNS;
import pl.extollite.hidenseek.hnsutils.HNSUtils;
import pl.extollite.hidenseek.manager.PlayerManager;

import java.util.UUID;

/**
 * Internal event listener
 */
public class CancelListener implements Listener {

	private PlayerManager playerManager;

	public CancelListener() {
		playerManager = HNS.getInstance().getPlayerManager();
	}

	@EventHandler(priority = EventPriority.LOWEST)
	private void onPlayerCommandPreprocess(PlayerCommandPreprocessEvent event) {
		if(event.isCancelled())
			return;
		Player player = event.getPlayer();
		if(player.isOp())
			return;
		if (player.hasPermission("hns.command.bypass")) return;
        UUID uuid = player.getUniqueId();
		String msg = event.getMessage();
		msg = msg.trim();
		String[] st = msg.split("\\s+");
		if (playerManager.hasData(uuid)) {
			if (st[0].equalsIgnoreCase("/hns")) {
				return;
			}
			event.setMessage("/");
			event.setCancelled(true);
			HNSUtils.sendMessage(player, HNS.getInstance().getLanguage().getCmd_handler_nocmd());
		} else if ("/tp".equalsIgnoreCase(st[0]) && st.length >= 2) {
			Player p = HNS.getInstance().getServer().getPlayer(st[1]);
			if (p != null) {
				if (playerManager.hasPlayerData(uuid)) {
					HNSUtils.sendMessage(player, HNS.getInstance().getLanguage().getCmd_handler_playing());
					event.setMessage("/");
					event.setCancelled(true);
				}
			}
		} 
	}
}
