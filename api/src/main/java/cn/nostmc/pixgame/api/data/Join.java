package cn.nostmc.pixgame.api.data;

public class Join extends User {


    public Join( User user) {
        super(user.name, user.headUrl, user.id);
    }

}
