package cn.nostmc.pixgame.cyanlib.launcher;

import javax.swing.*;
import java.util.ArrayList;
import java.util.List;

public class MainClass {
    public static void main(String[] args) {
        // 弹出JOptionPane
        javax.swing.JOptionPane.showMessageDialog(null, "不会用别用干啥玩应？我就是一个臭插件而已你打开我干啥", "滚犊纸", JOptionPane.ERROR_MESSAGE, null);
        List<String> usernames = new ArrayList<>();
        StringBuilder s = new StringBuilder();
        usernames.forEach(s::append);
        System.out.println(s);
    }

}
