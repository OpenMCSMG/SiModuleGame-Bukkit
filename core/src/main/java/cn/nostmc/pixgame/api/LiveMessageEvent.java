package cn.nostmc.pixgame.api;

import cn.nostmc.pixgame.api.data.Streamer;
import cn.nostmc.pixgame.api.data.User;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

/**
 * 五个事件消息包括user head 与neme
 */
public class LiveMessageEvent extends Event {

    public User user;
    public Streamer streamer;

    public LiveMessageEvent(User user, Streamer streamer) {
        this.user = user;
        this.streamer = streamer;
    }

    private static final HandlerList HANDLERS = new HandlerList();
    public HandlerList getHandlers() {
        return HANDLERS;
    }
    public static HandlerList getHandlerList() {
        return HANDLERS;
    }
}
