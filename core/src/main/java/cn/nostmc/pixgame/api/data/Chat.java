package cn.nostmc.pixgame.api.data;

public class Chat extends User {

    public User user;
    public String context;

    public Chat(User user, String context) {
        super(user.name, user.headUrl, user.id);
        this.user = user;
        this.context = context;
    }

}
