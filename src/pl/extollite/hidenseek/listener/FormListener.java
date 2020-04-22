package pl.extollite.hidenseek.listener;

import cn.nukkit.block.Block;
import cn.nukkit.event.EventHandler;
import cn.nukkit.event.Listener;
import cn.nukkit.event.player.PlayerFormRespondedEvent;
import cn.nukkit.form.element.ElementButton;
import cn.nukkit.form.response.FormResponseSimple;
import pl.extollite.hidenseek.HNS;
import pl.extollite.hidenseek.data.ConfigData;
import pl.extollite.hidenseek.data.Leaderboard;
import pl.extollite.hidenseek.form.BlockPickWindow;
import pl.extollite.hidenseek.form.MapPickWindow;
import pl.extollite.hidenseek.form.StatsShopWindow;
import pl.extollite.hidenseek.form.StatsWindow;
import pl.extollite.hidenseek.game.Game;
import pl.extollite.hidenseek.hnsutils.HNSUtils;

import java.util.Map;

public class FormListener implements Listener {
    public FormListener(){}

    @EventHandler
    public void onResponse(PlayerFormRespondedEvent event){
        if(event.wasClosed()){
            HNS.getInstance().getOpenedWindows().remove(event.getPlayer());
            return;
        }
        if(event.getWindow() instanceof StatsWindow){
            event.getPlayer().showFormWindow(new StatsShopWindow(event.getPlayer()));
        }
        if(event.getWindow() instanceof StatsShopWindow) {
            FormResponseSimple response = (FormResponseSimple) event.getResponse();
            ElementButton button = response.getClickedButton();
            String name = button.getText().replace("§", "&");
            if(name.equals("Abbrechen"))
                return;
            Map.Entry<Integer, String> exchange = ConfigData.shop.get(name);
            if(HNS.getInstance().getLeaderboard().removePoints(event.getPlayer(), Leaderboard.Stats.CURR_POINTS, exchange.getKey())){
                HNS.getInstance().getServer().dispatchCommand(HNS.getInstance().getServer().getConsoleSender(), exchange.getValue().replace("%player_name%", event.getPlayer().getName()));
                HNSUtils.sendMessage(event.getPlayer(), HNS.getInstance().getLanguage().getStats_shop_success().replace("%name%", name));
            } else {
                event.getPlayer().showFormWindow(new StatsShopWindow(event.getPlayer()));
                HNSUtils.sendMessage(event.getPlayer(), HNS.getInstance().getLanguage().getStats_shop_fail());
            }
        }
        Game g = HNS.getInstance().getPlayerManager().getGame(event.getPlayer());
        if(g != null){
            if(event.getWindow() instanceof BlockPickWindow){
                FormResponseSimple response = (FormResponseSimple)event.getResponse();
                ElementButton button = response.getClickedButton();
                for(Map.Entry<String, Block> entry : g.getMap().getMapBlocks().entrySet()){
                    if(entry.getValue().getName().equals(button.getText())){
                        g.getBlocks().put(event.getPlayer(), entry.getValue().clone());
                        HNS.getInstance().getOpenedWindows().remove(event.getPlayer());
                        break;
                    }
                }
            }
            if(event.getWindow() instanceof MapPickWindow){
                FormResponseSimple response = (FormResponseSimple)event.getResponse();
                ElementButton button = response.getClickedButton();
                int votes = 0;
                String[] split = button.getText().split("\n");
                if(g.getVotes().containsKey(split[0]))
                  votes = g.getVotes().get(split[0]);
                votes++;
                g.getVotes().put(split[0], votes);
                g.getVoted().add(event.getPlayer());
                HNS.getInstance().getOpenedWindows().remove(event.getPlayer());
            }
        }
    }
}
