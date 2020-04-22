package pl.extollite.hidenseek.task;


import cn.nukkit.Player;
import cn.nukkit.level.Sound;
import pl.extollite.hidenseek.HNS;
import pl.extollite.hidenseek.data.ConfigData;
import pl.extollite.hidenseek.form.BlockPickWindow;
import pl.extollite.hidenseek.form.MapPickWindow;
import pl.extollite.hidenseek.game.Game;
import pl.extollite.hidenseek.hnsutils.HNSUtils;

public class StartingTask implements Runnable {

    private int timer;
    private int id;
    private Game game;

    public StartingTask(Game g) {
        this.timer = 25;
        this.game = g;
        HNSUtils.broadcast(HNS.getInstance().getLanguage().getGame_started().replace("%arena%", g.getName()), g.getHiders());
        HNSUtils.broadcast(HNS.getInstance().getLanguage().getGame_join().replace("%arena%", g.getName()), g.getHiders());

        this.id = HNS.getInstance().getServer().getScheduler().scheduleDelayedRepeatingTask(HNS.getInstance(), this, 5 * 20, 20).getTaskId();
    }

    @Override
    public void run() {

        if (timer <= 0) {
            game.startHide();
            stop();
        } else if(timer == 20){
            randomMap();
        }
        else if (timer > 6 && timer % 5 == 0) {
            game.msgAll(HNS.getInstance().getLanguage().getGame_countdown().replace("%timer%", String.valueOf(timer)));
        } else if(timer == 6){
            game.teleportAll();
        } else if(timer <= 5){
            game.titleAll(String.valueOf(timer));
            for(Player p : game.getHiders()){
                p.getLevel().addSound(p, Sound.RANDOM_TOAST, 1, 1, p);
            }
            for(Player p : game.getSeekers()){
                p.getLevel().addSound(p, Sound.RANDOM_TOAST, 1, 1, p);
            }
        } else if(timer < 15){
            if(timer <= 10){
                for(Player p : game.getHiders()){
                    p.getLevel().addSound(p, Sound.RANDOM_CLICK, 1, 1, p);
                }
            }
        }
        game.actionBarAll(HNS.getInstance().getLanguage().getGamestart_countdown().replace("%timer%", String.valueOf(timer)));
        timer--;
    }

    public void stop() {
        HNS.getInstance().getServer().getScheduler().cancelTask(id);
    }

    public int getTimer() {
        return timer;
    }

    public void randomMap(){
        game.randomMap();
        for(Player player : game.getHiders()){
            player.getInventory().setItem(0, ConfigData.blockPick.clone());
            HNS.getInstance().getOpenedWindows().put(player, player.showFormWindow(new BlockPickWindow(game, player)));
            player.sendAllInventories();
        }
    }
}
