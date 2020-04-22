package pl.extollite.hidenseek.task;

import cn.nukkit.Player;
import cn.nukkit.level.Sound;
import pl.extollite.hidenseek.HNS;
import pl.extollite.hidenseek.game.Game;
import pl.extollite.hidenseek.hnsutils.HNSUtils;

public class HideTask implements Runnable {

    private Game game;
    private int id;
    private int time;

    public HideTask(Game g) {
        this.time = g.getHideTime();
        this.game = g;
        for (Player p : g.getHiders()) {
            HNSUtils.sendMessage(p, HNS.getInstance().getLanguage().getHide_game_started());
            HNSUtils.sendMessage(p, HNS.getInstance().getLanguage().getHide_time().replace("%hide%", String.valueOf(g.getHideTime())));
            p.setHealth(20);
            p.getFoodData().setLevel(20);
            p.getFoodData().sendFoodLevel();
            g.unFreeze(p);
        }
        for (Player p : g.getSeekers()) {
            HNSUtils.sendMessage(p, HNS.getInstance().getLanguage().getHide_game_started());
            HNSUtils.sendMessage(p, HNS.getInstance().getLanguage().getHide_time().replace("%hide%", String.valueOf(g.getHideTime())));
            p.setHealth(20);
            p.getFoodData().setLevel(20);
            p.getFoodData().sendFoodLevel();
            g.unFreeze(p);
        }
        this.id = HNS.getInstance().getServer().getScheduler().scheduleRepeatingTask(HNS.getInstance(), this, 20).getTaskId();
    }

    @Override
    public void run() {
        if (time == 0) {
            game.msgAll(HNS.getInstance().getLanguage().getHide_finished());
			for(Player p : game.getHiders()){
				p.getLevel().addSound(p, Sound.BLOCK_BELL_HIT, 1, 1, p);
			}
			for(Player p : game.getSeekers()){
				p.getLevel().addSound(p, Sound.BLOCK_BELL_HIT, 1, 1, p);
			}
            game.startGame();
            stop();
        } else if (time < 5) {
			for(Player p : game.getHiders()){
				p.getLevel().addSound(p, Sound.RANDOM_CLICK, 1, 1, p);
			}
			for(Player p : game.getSeekers()){
				p.getLevel().addSound(p, Sound.RANDOM_CLICK, 1, 1, p);
			}
        }
        game.updateTimers(HNS.getInstance().getLanguage().getHide_action_bar().replace("%time%", String.valueOf(time)));
        time--;
    }

    public void stop() {
        HNS.getInstance().getServer().getScheduler().cancelTask(id);
    }
}
