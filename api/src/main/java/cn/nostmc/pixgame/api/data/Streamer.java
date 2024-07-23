package cn.nostmc.pixgame.api.data;

/**
 * 主播实例什么ID
 */
public class Streamer {

    public String anchorName; // 主播名
    public String roomNumber; // 房间号

    public Streamer(String anchorName, String roomNumber) {
        this.anchorName = anchorName;
        this.roomNumber = roomNumber;
    }

    @Override
    public int hashCode() {
        return anchorName.hashCode() + roomNumber.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof Streamer) {
            Streamer other = (Streamer) obj;
            return anchorName.equals(other.anchorName) && roomNumber.equals(other.roomNumber);
        } else {
            return false;
        }
    }

}