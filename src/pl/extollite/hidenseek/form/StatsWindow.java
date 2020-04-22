package pl.extollite.hidenseek.form;

import cn.nukkit.Player;
import cn.nukkit.form.element.ElementButton;
import cn.nukkit.form.window.FormWindowSimple;
import pl.extollite.hidenseek.HNS;
import pl.extollite.hidenseek.data.Leaderboard;
import pl.extollite.hidenseek.hnsutils.HNSUtils;

public class StatsWindow extends FormWindowSimple {
    public StatsWindow(Player player){
        super(HNSUtils.colorize(HNS.getInstance().getLanguage().getStats_menu_title()),
                HNSUtils.colorize(HNS.getInstance().getLanguage().getStats_menu_content_1()).replace("%points%", String.valueOf(HNS.getInstance().getLeaderboard().getStat(player, Leaderboard.Stats.POINTS)))+"\n"+
                        HNSUtils.colorize(HNS.getInstance().getLanguage().getStats_menu_content_2()).replace("%points%", String.valueOf(HNS.getInstance().getLeaderboard().getStat(player, Leaderboard.Stats.CURR_POINTS)))+"\n"+
                        HNSUtils.colorize(HNS.getInstance().getLanguage().getStats_menu_content_3()).replace("%games%", String.valueOf(HNS.getInstance().getLeaderboard().getStat(player, Leaderboard.Stats.GAMES)))
                );
        this.addButton(new ElementButton(HNSUtils.colorize(HNS.getInstance().getLanguage().getStats_button())));
    }
}
