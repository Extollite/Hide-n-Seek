package pl.extollite.hidenseek.data;

import cn.nukkit.utils.Config;
import lombok.Getter;
import pl.extollite.hidenseek.HNS;
import java.util.List;

@Getter
public class Language {
    //General Translations
    private final String prefix;
    private final String cmd_no_permission;

    //Game Message Translations
    private final String player_joined_game;
    private final String player_left_game;
    private final String game_started;
    private final String game_join;
    private final String game_countdown;
    private final String game_almost_over;
    private final String game_ending_minsec;
    private final String game_ending_min;
    private final String game_ending_sec;
    private final String game_teleport;
    private final String players_to_start;
    private final String arena_not_ready;
    private final String game_full;
    private final String game_seekers_won;
    private final String game_hiders_won;
    private final String game_player_dead;
    private final String game_gained_points;

    private final String gamestart_countdown;

    private final String game_timer;

    // Command Translations
    private final List<String> cmd_admin_usage;


    // Admin Command Translations
    private final List<String> cmd_usage;

    private final String cmd_sign_set;

    private final String cmd_handler_nocmd;
    private final String cmd_handler_playing;

    private final String cmd_create_minmax;
    private final String cmd_create_created;

    private final String cmd_spawn_set;

    private final String cmd_lobby_set;

    private final String cmd_exit_set;

    private final String cmd_delete_attempt;
    private final String cmd_delete_kicking;
    private final String cmd_delete_deleted;
    private final String cmd_delete_failed;
    private final String cmd_delete_noexist;

    private final String cmd_join_in_game;

    private final String cmd_leave_left;
    private final String cmd_leave_not_in_game;

    private final String cmd_toggle_locked;
    private final String cmd_toggle_unlocked;

    private final String cmd_start_starting;
    private final String cmd_stop_all;
    private final String cmd_stop_arena;
    private final String cmd_stop_noexist;

    // Status Translations
    private final String status_running;
    private final String status_stopped;
    private final String status_ready;
    private final String status_waiting;
    private final String status_broken;
    private final String status_rollback;
    private final String status_not_ready;
    private final String status_beginning;
    private final String status_countdown;
    private final String status_final;

    //Lobby Sign Translations
    private final String line_1;
    private final String line_2;
    private final String line_3;
    private final String line_4;

    //Block Translations
    private final String block_menu_title;
    private final String block_menu_content;

    //Map Translations
    private final String map_menu_title;
    private final String map_menu_content;
    private final String map_menu_votes;
    private final String map_voted;
    private final String map_won;

    //Stats Translations
    private final String stats_menu_title;
    private final String stats_menu_content_1;
    private final String stats_menu_content_2;
    private final String stats_menu_content_3;
    private final String stats_button;

    private final String stats_shop_title;
    private final String stats_shop_content;
    private final String stats_shop_success;
    private final String stats_shop_fail;

    //Roam Translations
    private final String hide_game_started;
    private final String hide_time;
    private final String hide_finished;
    private final String hide_action_bar;

    private final String listener_not_running;
    private final String listener_no_edit_block;
    private final String listener_no_interact;
    private final String listener_sign_click_hand;

    public Language() {
        Config langFile = new Config(HNS.getInstance().getDataFolder() + "/language.yml", Config.YAML);
        if (langFile.getAll().isEmpty()) {
            langFile.load(HNS.getInstance().getResource("language.yml"));
            langFile.save();
        }
        prefix = langFile.getString("prefix");
        player_joined_game = langFile.getString("player-joined-game");
        player_left_game = langFile.getString("player-left-game");
        game_started = langFile.getString("game-started");
        game_join = langFile.getString("game-join");
        game_countdown = langFile.getString("game-countdown");
        game_almost_over = langFile.getString("game-almost-over");
        game_ending_minsec = langFile.getString("game-ending-minsec");
        game_ending_min = langFile.getString("game-ending-min");
        game_ending_sec = langFile.getString("game-ending-sec");
        game_teleport = langFile.getString("game-teleport");
        game_player_dead = langFile.getString("game-player-dead");
        game_gained_points = langFile.getString("game-gained-points");

        players_to_start = langFile.getString("players-to-start");
        arena_not_ready = langFile.getString("arena-not-ready");
        game_full = langFile.getString("game-full");
        game_seekers_won = langFile.getString("game-won-seekers");
        game_hiders_won = langFile.getString("game-won-hiders");

        cmd_usage = langFile.getStringList("cmd-usage");

        cmd_no_permission = langFile.getString("cmd-no-permission");
        cmd_admin_usage = langFile.getStringList("cmd-admin-usage");

        cmd_sign_set = langFile.getString("cmd-sign-set");

        cmd_exit_set = langFile.getString("cmd-exit-set");

        cmd_handler_nocmd = langFile.getString("cmd-handler-nocmd");
        cmd_handler_playing = langFile.getString("cmd-handler-playing");
        cmd_create_minmax = langFile.getString("cmd-create-minmax");
        cmd_create_created = langFile.getString("cmd-create-created");

        cmd_spawn_set = langFile.getString("cmd-spawn-set");

        cmd_lobby_set = langFile.getString("cmd-lobby-set");

        cmd_delete_attempt = langFile.getString("cmd-delete-attempt");
        cmd_delete_kicking = langFile.getString("cmd-delete-kicking");
        cmd_delete_deleted = langFile.getString("cmd-delete-deleted");
        cmd_delete_failed = langFile.getString("cmd-delete-failed");
        cmd_delete_noexist = langFile.getString("cmd-delete-noexist");

        cmd_join_in_game = langFile.getString("cmd-join-in-game");

        cmd_leave_left = langFile.getString("cmd-leave-left");
        cmd_leave_not_in_game = langFile.getString("cmd-leave-not-in-game");

        cmd_start_starting = langFile.getString("cmd-start-starting");
        cmd_stop_all = langFile.getString("cmd-stop-all");
        cmd_stop_arena = langFile.getString("cmd-stop-arena");
        cmd_stop_noexist = langFile.getString("cmd-stop-noexist");

        cmd_toggle_unlocked = langFile.getString("cmd-toggle-unlocked");
        cmd_toggle_locked = langFile.getString("cmd-toggle-locked");

        status_running = langFile.getString("status-running");
        status_stopped = langFile.getString("status-stopped");
        status_ready = langFile.getString("status-ready");
        status_waiting = langFile.getString("status-waiting");
        status_broken = langFile.getString("status-broken");
        status_rollback = langFile.getString("status-rollback");
        status_not_ready = langFile.getString("status-notready");
        status_beginning = langFile.getString("status-beginning");
        status_countdown = langFile.getString("status-countdown");
        status_final = langFile.getString("status-final");

        line_1 = langFile.getString("lobby-sign.line-1");
        line_2 = langFile.getString("lobby-sign.line-2");
        line_3 = langFile.getString("lobby-sign.line-3");
        line_4 = langFile.getString("lobby-sign.line-4");

        block_menu_title = langFile.getString("block-menu-title");
        block_menu_content = langFile.getString("block-menu-content");

        map_menu_title = langFile.getString("map-menu-title");
        map_menu_content = langFile.getString("map-menu-content");
        map_menu_votes = langFile.getString("map-menu-votes");
        map_voted = langFile.getString("map-voted");
        map_won = langFile.getString("map-won");

        stats_menu_title = langFile.getString("stats-menu-title");
        stats_menu_content_1 = langFile.getString("stats-menu-content-1");
        stats_menu_content_2 = langFile.getString("stats-menu-content-2");
        stats_menu_content_3 = langFile.getString("stats-menu-content-3");
        stats_button = langFile.getString("stats-button");

        stats_shop_title = langFile.getString("stats-shop-title");
        stats_shop_content = langFile.getString("stats-shop-content");
        stats_shop_success = langFile.getString("stats-shop-success");
        stats_shop_fail = langFile.getString("stats-shop-fail");

        hide_game_started = langFile.getString("hide-game-started");
        hide_time = langFile.getString("hide-time");
        hide_finished = langFile.getString("hide-finished");
        hide_action_bar = langFile.getString("hide-action-bar");

        gamestart_countdown = langFile.getString("gamestart-countdown");

        game_timer = langFile.getString("game-timer");

        listener_not_running = langFile.getString("listener-not-running");
        listener_no_edit_block = langFile.getString("listener-no-edit-block");
        listener_no_interact = langFile.getString("listener-no-interact");
        listener_sign_click_hand = langFile.getString("listener-sign-click-hand");
    }
}
