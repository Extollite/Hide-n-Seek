package pl.extollite.hidenseek.listener;

import cn.nukkit.Player;
import cn.nukkit.block.Block;
import cn.nukkit.block.BlockAir;
import cn.nukkit.blockentity.BlockEntitySign;
import cn.nukkit.entity.Entity;
import cn.nukkit.event.EventHandler;
import cn.nukkit.event.Listener;
import cn.nukkit.event.block.BlockBreakEvent;
import cn.nukkit.event.block.ItemFrameDropItemEvent;
import cn.nukkit.event.entity.EntityDamageByEntityEvent;
import cn.nukkit.event.level.LevelSaveEvent;
import cn.nukkit.event.player.*;
import cn.nukkit.event.server.DataPacketReceiveEvent;
import cn.nukkit.inventory.transaction.data.UseItemData;
import cn.nukkit.item.Item;
import cn.nukkit.level.Location;
import cn.nukkit.math.BlockVector3;
import cn.nukkit.math.Vector3;
import cn.nukkit.network.protocol.InventoryTransactionPacket;
import cn.nukkit.utils.TextFormat;
import pl.extollite.hidenseek.EntityFalling;
import pl.extollite.hidenseek.FakeBlockEntity;
import pl.extollite.hidenseek.HNS;
import pl.extollite.hidenseek.data.ConfigData;
import pl.extollite.hidenseek.data.Leaderboard;
import pl.extollite.hidenseek.form.BlockPickWindow;
import pl.extollite.hidenseek.form.MapPickWindow;
import pl.extollite.hidenseek.game.Game;
import pl.extollite.hidenseek.game.Status;
import pl.extollite.hidenseek.hnsutils.HNSUtils;

public class GameListener implements Listener {
    @EventHandler
    public void onMove(PlayerMoveEvent ev) {
        if (ev.isCancelled())
            return;
        Player player = ev.getPlayer();
        Game g = HNS.getInstance().getPlayerManager().getGame(player);
        if (g != null) {
            if ((g.getStatus() == Status.RUNNING || g.getStatus() == Status.BEGINNING) && g.getHiders().contains(player)) {
                if (ev.getTo().getX() != ev.getFrom().getX() || ev.getTo().getZ() != ev.getFrom().getZ() || ev.getTo().getY() != ev.getFrom().getY()) {
                    Entity link = g.getLinkBlock().get(player);
                    if (link instanceof EntityFalling) {
                        link.setPositionAndRotation(ev.getTo(), ev.getTo().yaw, ev.getTo().pitch);
                        g.getTimers().put(player, 0);
                        player.setExperience(0, ConfigData.standTime);
                        player.sendExperience();
                    } else if (link instanceof FakeBlockEntity && (ev.getTo().getFloorX() != ev.getFrom().getFloorX() || ev.getTo().getFloorZ() != ev.getFrom().getFloorZ())) {
                        link.despawnFromAll();
                        link.close();
                        g.spawnFallBlock(player);
                        g.getPlayersBlocks().remove(ev.getFrom().getLevelBlock());
                    }
                }
            }
            if (g.getStatus() == Status.BEGINNING && g.getSeekers().contains(player)) {
                double x = (g.getMap().getVec2Seekers().getX() - ev.getTo().x);
                x *= x;
                double z = (g.getMap().getVec2Seekers().getY() - ev.getTo().z);
                z *= z;
                double distance = x + z;
                if (distance >= ConfigData.seekersRoomSize) {
                    ev.setCancelled();
                    //player.teleport(ev.getFrom());
                }
            }
        }
    }

    @EventHandler
    public void onHit(PlayerInteractEvent event) {
        if (event.isCancelled())
            return;
        Player player = event.getPlayer();
        Game g = HNS.getInstance().getPlayerManager().getGame(player);
        if (g != null && g.getStatus() == Status.RUNNING) {
            event.setCancelled();
            if (g.getSeekers().contains(player) && (event.getAction() == PlayerInteractEvent.Action.LEFT_CLICK_BLOCK || event.getAction() == PlayerInteractEvent.Action.LEFT_CLICK_AIR)) {
                processHit(player, g, event.getBlock());
            }
        }
    }

    private boolean processHit(Player player, Game g, Vector3 block){
        if (g.getPlayersBlocks().containsKey(block) && g.getTimers().get(player) > 0) {
            Player p = g.getPlayersBlocks().remove(block);
            Entity link = g.getLinkBlock().get(p);
            link.despawnFromAll();
            link.close();
            g.spawnFallBlock(p);
            return false;
        }
        if (g.getTimers().containsKey(player) && g.getTimers().get(player) > 0) {
            int time = g.getTimers().get(player);
            time--;
            player.setExperience(0, time);
            player.sendExperience();
            g.getTimers().put(player, time);
            return true;
        } else if (!g.getTimers().containsKey(player)) {
            g.getTimers().put(player, 10);
            return true;
        }
        return true;
    }

    @EventHandler
    public void onItemUse(PlayerInteractEvent event) {
        if (event.isCancelled() || event.getItem() == null)
            return;
        Player player = event.getPlayer();
        Game g = HNS.getInstance().getPlayerManager().getGame(player);
        if (g != null && (g.getStatus() == Status.COUNTDOWN || g.getStatus() == Status.WAITING || g.getStatus() == Status.BEGINNING)) {
            event.setCancelled();
            if (event.getItem().equals(ConfigData.blockPick) && !HNS.getInstance().getOpenedWindows().containsKey(player)) {
                HNS.getInstance().getOpenedWindows().put(player, player.showFormWindow(new BlockPickWindow(g, player)));
            } else if (event.getItem().equals(ConfigData.mapPick) && !HNS.getInstance().getOpenedWindows().containsKey(player)) {
                if (g.getVoted().contains(player)) {
                    player.sendMessage(HNSUtils.colorize(HNS.getInstance().getLanguage().getMap_voted()));
                    return;
                }
                HNS.getInstance().getOpenedWindows().put(player, player.showFormWindow(new MapPickWindow(g, player)));
            }
        }
    }

    /*@EventHandler
    private void onPlayerClickLobby(PlayerInteractEvent event) {
        Player p = event.getPlayer();
        if (event.getAction().equals(PlayerInteractEvent.Action.RIGHT_CLICK_BLOCK) || event.getAction().equals(PlayerInteractEvent.Action.LEFT_CLICK_BLOCK)) {
            Block b = event.getBlock();
            if (!(b.getLevel().getBlockEntity(b) instanceof BlockEntitySign))
                return;
            BlockEntitySign sign = (BlockEntitySign) b.getLevel().getBlockEntity(b);
            if (sign == null) {
                return;
            }
            if (sign.getText().length == 4 && sign.getText()[0].equals(HNSUtils.colorize(HNS.getInstance().getLanguage().getLine_1()))) {
                Game game = HNS.getInstance().getGame(TextFormat.clean(sign.getText()[3]));
                if (game == null) {
                    HNSUtils.sendMessage(p, HNS.getInstance().getLanguage().getCmd_delete_noexist());
                } else {
                    if (p.getInventory().getItemInHand().getId() == 0) {
                        game.join(p);
                    } else {
                        HNSUtils.sendMessage(p, HNS.getInstance().getLanguage().getListener_sign_click_hand());
                    }
                }
            }
        }
    }*/

    @EventHandler
    public void onAttack(EntityDamageByEntityEvent event) {
        if (event.isCancelled())
            return;
        if (event.getDamager() instanceof Player && event.getEntity() instanceof Player) {
            Player damager = (Player) event.getDamager();
            Player attacked = (Player) event.getEntity();
            Game g1 = HNS.getInstance().getPlayerManager().getGame(damager);
            Game g2 = HNS.getInstance().getPlayerManager().getGame(attacked);
            if (g1 == null)
                return;
            if (g2 == null)
                return;
            if (g1.getSeekers().contains(damager) && g1.getSeekers().contains(attacked))
                event.setCancelled();
            if (g1.getHiders().contains(damager) && g1.getHiders().contains(attacked))
                event.setCancelled();
            if (attacked.getHealth() <= event.getFinalDamage()) {
                event.setCancelled();
                attacked.getInventory().clearAll();
                HNS.getInstance().getLeaderboard().addStat(damager, Leaderboard.Stats.POINTS, 1);
                HNS.getInstance().getLeaderboard().addStat(damager, Leaderboard.Stats.CURR_POINTS, 1);
                if(g1.getSeekers().contains(damager)){
                    g1.msgAll(HNS.getInstance().getLanguage().getGame_player_dead().replace("%seeker%", damager.getName()).replace("%hider%", attacked.getName()).replace("%block%", g1.getBlocks().get(attacked).getName()));
                }
                processDeath(attacked, g1);
            }
        }
    }

    private void processDeath(Player player, Game game) {
        player.setHealth(20);
        HNS.getInstance().getServer().getScheduler().scheduleDelayedTask(HNS.getInstance(), () -> {
            game.leave(player, true);
        }, 1);
    }

    @EventHandler
    public void onDeath(PlayerDeathEvent event) {
        Player player = event.getEntity();
        Game g = HNS.getInstance().getPlayerManager().getGame(player);
        if (g != null) {
            event.setCancelled();
            if(player.getLastDamageCause().getEntity() instanceof Player){
                event.setDrops(new Item[0]);
                Player killer = (Player)player.getLastDamageCause().getEntity();
                g.msgAll(HNS.getInstance().getLanguage().getGame_player_dead().replace("%seeker%", killer.getName()).replace("%hider%", player.getName()).replace("%block%", g.getBlocks().get(player).getName()));
                HNS.getInstance().getLeaderboard().addStat(killer, Leaderboard.Stats.POINTS, 1);
                HNS.getInstance().getLeaderboard().addStat(killer, Leaderboard.Stats.CURR_POINTS, 1);
            }
            processDeath(player, g);
        }
    }

    @EventHandler
    public void onBreak(BlockBreakEvent event) {
        if (event.isCancelled())
            return;
        Player player = event.getPlayer();
        Game g = HNS.getInstance().getPlayerManager().getGame(player);
        if (g != null) {
            event.setCancelled();
        }
    }

    @EventHandler
    public void onLeft(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        Game g = HNS.getInstance().getPlayerManager().getGame(player);
        if (g != null) {
            if (g.getExit() != null && g.getExit().getLevel() != null)
                player.teleportImmediate(g.getExit());
            else
                player.teleportImmediate(Location.fromObject(HNS.getInstance().getServer().getDefaultLevel().getSafeSpawn(), HNS.getInstance().getServer().getDefaultLevel()));
            g.leave(player, false);
        }
    }

    @EventHandler
    private void onDrop(PlayerDropItemEvent event) {
        Player p = event.getPlayer();
        Game g = HNS.getInstance().getPlayerManager().getGame(p);
        if (g != null) {
            event.setCancelled();
        }
    }

    @EventHandler
    private void onChat(PlayerChatEvent event) {
        Player p = event.getPlayer();
        Game g = HNS.getInstance().getPlayerManager().getGame(p);
        if (g != null) {
            if (g.getHiders().contains(p)) {
                for (Player s : g.getSeekers())
                    event.getRecipients().remove(s);
            }
            if (g.getSeekers().contains(p)) {
                for (Player h : g.getHiders())
                    event.getRecipients().remove(h);
            }
        }
    }

    @EventHandler
    private void onFood(PlayerFoodLevelChangeEvent event){
        Player p = event.getPlayer();
        Game g = HNS.getInstance().getPlayerManager().getGame(p);
        if (g != null) {
            event.setCancelled();
        }
    }

    @EventHandler // Prevent players breaking item frames
    private void onBreakItemFrame(ItemFrameDropItemEvent event) {
        Player p = event.getPlayer();
        Game g = HNS.getInstance().getPlayerManager().getGame(p);
        if (g != null) {
            event.setCancelled();
        }
    }

    @EventHandler
    public void onDataPacket(DataPacketReceiveEvent event) {
        if (event.isCancelled())
            return;
        Player player = event.getPlayer();
        Game g = HNS.getInstance().getPlayerManager().getGame(player);
        if (g == null)
            return;
        if (event.getPacket() instanceof InventoryTransactionPacket) {
            InventoryTransactionPacket transactionPacket = (InventoryTransactionPacket) event.getPacket();

            switch (transactionPacket.transactionType) {
                case InventoryTransactionPacket.TYPE_USE_ITEM:
                    UseItemData useItemData = (UseItemData) transactionPacket.transactionData;

                    BlockVector3 blockVector = useItemData.blockPos;

                    int type = useItemData.actionType;
                    switch (type) {
                        case InventoryTransactionPacket.USE_ITEM_ACTION_CLICK_BLOCK:
                            if (g.getPlayersBlocks().containsKey(blockVector.asVector3())) {
                                event.setCancelled();
                            }
                            return;
                        case InventoryTransactionPacket.USE_ITEM_ACTION_BREAK_BLOCK:
                            if (g.getPlayersBlocks().containsKey(blockVector.asVector3())) {
                                if(g.getSeekers().contains(player) && !processHit(player, g, blockVector.asVector3())){
                                    return;
                                }
                                Player p = g.getPlayersBlocks().get(blockVector.asVector3());
                                g.getLinkBlock().get(p).spawnTo(player);
                                event.setCancelled();
                            }
                            return;
                        default:
                            return;
                    }
            }
        }
    }
}
