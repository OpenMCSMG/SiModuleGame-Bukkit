package cn.nostmc.pixgame.api.data;

public class Gift extends User {
    public Long id;
    public String giftName;
    public String giftUrl;
    public Long giftCount;
    public User user;

    public Gift(Long id, String giftName, String giftUrl, Long giftCount, User user) {
        super(user.name, user.headUrl, user.id);
        this.user = user;
        this.id = id;
        this.giftName = giftName;
        this.giftUrl = giftUrl;
        this.giftCount = giftCount;
    }

}
