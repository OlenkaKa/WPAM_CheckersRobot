package com.github.oleksandretta.wpam.checkers_robot.msg;

import irp6_checkers.Point;
import org.ros.internal.message.RawMessage;

/**
 * Created by Aleksandra Karbarczyk
 * date: 21.05.2015
 */
public class CheckersPoint implements Point {

    private int x;
    private int y;

    public CheckersPoint(int x, int y) {
        this.x = x;
        this.y = y;
    }

    @Override
    public int getX() {
        return x;
    }

    @Override
    public void setX(int x) {
        this.x = x;
    }

    @Override
    public int getY() {
        return y;
    }

    @Override
    public void setY(int y) {
        this.y = y;
    }

    @Override
    public RawMessage toRawMessage() {
        return null;
    }
}
