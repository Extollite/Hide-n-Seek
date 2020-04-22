package pl.extollite.hidenseek.task;


import cn.nukkit.Player;
import pl.extollite.hidenseek.HNS;
import pl.extollite.hidenseek.data.ConfigData;
import pl.extollite.hidenseek.data.Leaderboard;
import pl.extollite.hidenseek.game.Game;
import pl.extollite.hidenseek.game.Status;
import pl.extollite.hidenseek.hnsutils.HNSUtils;

public class TimerTask implements Runnable {

	private int remainingtime;
	private int id;
	private Game game;

	public TimerTask(Game g, int time) {
		this.remainingtime = time;
		this.game = g;
		this.id = HNS.getInstance().getServer().getScheduler().scheduleRepeatingTask(HNS.getInstance(), this, 20).getTaskId();
	}
	
	@Override
	public void run() {
		if (game == null || ( game.getStatus() != Status.RUNNING)) stop(); //A quick null check!

		if (this.remainingtime < 10) {
			stop();
			game.stop(false);
		} else {
			String minutes = String.valueOf(remainingtime/60);
			String seconds = String.valueOf(remainingtime%60);
			String timer = (minutes.length() == 1 ? "0"+minutes : minutes)+":"+(seconds.length() == 1 ? "0"+seconds : seconds);
			game.updateTimers(HNS.getInstance().getLanguage().getGame_timer().replace("%timer%", timer));
			if(game.getTime() - remainingtime == 60){
				for(Player p : game.getHiders()){
					p.getInventory().addItem(ConfigData.hidersItem.clone());
					p.sendAllInventories();
					HNS.getInstance().getLeaderboard().addStat(p, Leaderboard.Stats.POINTS, 1);
					HNS.getInstance().getLeaderboard().addStat(p, Leaderboard.Stats.CURR_POINTS, 1);
					HNSUtils.sendMessage(p, HNS.getInstance().getLanguage().getGame_gained_points().replace("%minute%", String.valueOf(1)).replace("%points%", String.valueOf(1)));
				}
			} else if( (game.getTime() - remainingtime) %60 == 0){
				int mult = (game.getTime() - remainingtime)/60;
				for(Player p : game.getHiders()){
					HNS.getInstance().getLeaderboard().addStat(p, Leaderboard.Stats.POINTS, mult);
					HNS.getInstance().getLeaderboard().addStat(p, Leaderboard.Stats.CURR_POINTS, mult);
					HNSUtils.sendMessage(p, HNS.getInstance().getLanguage().getGame_gained_points().replace("%minute%", String.valueOf(mult)).replace("%points%", String.valueOf(mult)));
				}
			}
		}
		remainingtime--;
	}
	
	public void stop() {
		HNS.getInstance().getServer().getScheduler().cancelTask(id);
	}

	public int getRemainingtime() {
		return remainingtime;
	}
}
