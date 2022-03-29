package uz.mq.mybaby;

public class BabyProfile {

    int age;
    int sex;

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public int getSex() {
        return sex;
    }

    public void setSex(int sex) {
        this.sex = sex;
    }

    public BabyProfile(int age, int sex) {
        this.age = age;
        this.sex = sex;
    }
}
