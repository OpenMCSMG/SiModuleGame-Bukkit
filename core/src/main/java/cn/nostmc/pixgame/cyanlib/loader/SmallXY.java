package cn.nostmc.pixgame.cyanlib.loader;

/**
 * 至尊珍贵的LC 抽象实例化
 */
public abstract class SmallXY {
    // 定义一些基本属性
    private String name;
    private int age;
    private String position;

    // 构造函数
    public SmallXY(String name, int age, String position) {
        this.name = name;
        this.age = age;
        this.position = position;
    }

    // 抽象方法，必须在子类中实现
    public abstract void performDuty();

    // 具体方法，可以直接使用
    public void introduce() {
        System.out.println("Hello, my name is " + name + ", I am " + age + " years old and I hold the position of " + position + ".");
    }

    // Getter 和 Setter 方法
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public String getPosition() {
        return position;
    }

    public void setPosition(String position) {
        this.position = position;
    }
}