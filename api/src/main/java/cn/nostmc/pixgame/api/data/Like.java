package cn.nostmc.pixgame.api.data;

public class Like extends User {

    public Long count;
    public User user;

    public Like(User user, Long count) {
        super(user.name, user.headUrl, user.id);
        this.user = user;
        this.count = count;
    }

}
