package pl.extollite.hidenseek.form;

import cn.nukkit.Player;
import cn.nukkit.form.element.ElementButton;
import cn.nukkit.form.window.FormWindowSimple;
import pl.extollite.hidenseek.HNS;
import pl.extollite.hidenseek.data.ConfigData;
import pl.extollite.hidenseek.data.Leaderboard;
import pl.extollite.hidenseek.hnsutils.HNSUtils;
import sun.security.krb5.Config;

import java.util.Map;

public class StatsShopWindow extends FormWindowSimple {
    public StatsShopWindow(Player player){
        super(HNSUtils.colorize(HNS.getInstance().getLanguage().getStats_shop_title()),
                HNSUtils.colorize(HNS.getInstance().getLanguage().getStats_menu_content_1()).replace("%points%", String.valueOf(HNS.getInstance().getLeaderboard().getStat(player, Leaderboard.Stats.CURR_POINTS))));
        for(Map.Entry<String, Map.Entry<Integer, String>> entry : ConfigData.shop.entrySet()){
            this.addButton(new ElementButton(HNSUtils.colorize(entry.getKey())));
        }
        this.addButton(new ElementButton("Abbrechen"));
    }
}
