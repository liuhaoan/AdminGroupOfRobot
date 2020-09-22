package pers.liuhaoan.jvtcGroupOfRobot.enums;

public enum WeekEnum {
    ONE(1), TWO(2), THREE(3), FOUR(4), FAVE(5), SIX(6), SUN(0);
    private int week;

    WeekEnum(int week) {
        this.week = week;
    }

    public int show() {
        return week;
    }
}
