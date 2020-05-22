package pl.extollite.hidenseek.data;

import cn.nukkit.Player;
import cn.nukkit.entity.Attribute;
import cn.nukkit.item.Item;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import pl.extollite.hidenseek.HNS;
import pl.extollite.hidenseek.game.Game;

import java.util.Map;

@Setter
@Getter
@ToString
public class PlayerData {
	private Map<Integer, Item> inv;
	private Item[] equip;
	private Map<Integer, Item> offhand;
	private int expL;
	private int expP;
	private float health;
	private int maxhealth;
	private int food;
	private float saturation;
	private int mode;
	private Player player;

	private Game game;

	public PlayerData(Player player, Game game) {
		this.game = game;
		this.player = player;
		inv = player.getInventory().getContents();
		equip = player.getInventory().getArmorContents();
		offhand = player.getOffhandInventory().getContents();
		expL = player.getExperienceLevel();
		expP = player.getExperience();
		mode = player.getGamemode();
		food = player.getFoodData().getLevel();
		saturation = player.getFoodData().getFoodSaturationLevel();
		health = player.getHealth();
		maxhealth = player.getMaxHealth();
		player.setMaxHealth(20);
		player.setHealth(20);
		player.getInventory().clearAll();
		player.setExperience(0, 0);
	}

	public void restore(Player player) {
		if (player == null) return;
		player.getInventory().clearAll();
		player.setMovementSpeed(Player.DEFAULT_SPEED);
		player.setExperience(expP, expL);
		player.getFoodData().setLevel(food);
		player.getFoodData().setFoodSaturationLevel(saturation);
		player.getFoodData().sendFoodLevel();
		player.getInventory().setContents(inv);
		player.getInventory().setArmorContents(equip);
		player.getOffhandInventory().setContents(offhand);
		player.setGamemode(mode);
		player.sendAllInventories();
		player.invulnerable = false;
		restoreHealth(player);
	}

	private void restoreHealth(Player player) {
		float att = Attribute.getAttribute(Attribute.MAX_HEALTH).getDefaultValue();
		if (health > att) {
			HNS.getInstance().getServer().getScheduler().scheduleDelayedTask(HNS.getInstance(), () -> {
				player.setMaxHealth(maxhealth);
				player.setHealth(health);
			}, 10);
		} else {
			player.setMaxHealth(maxhealth);
			player.setHealth(health);
		}
	}
}
