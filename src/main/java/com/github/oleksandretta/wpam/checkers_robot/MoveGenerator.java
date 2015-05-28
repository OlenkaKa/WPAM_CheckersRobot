package com.github.oleksandretta.wpam.checkers_robot;

import org.ros.message.MessageFactory;

import java.util.ArrayList;
import java.util.List;

import irp6_checkers.ChessboardMove;
import irp6_checkers.ImageData;
import irp6_checkers.Point;

/**
 * Created by Aleksandra Karbarczyk
 * date: 28.05.2015
 */
public class MoveGenerator {
    public static ChessboardMove generateMove(MessageFactory messageFactory, ImageData imageData,
                                              List<FloatPoint> points) {
        ChessboardMove move = messageFactory.newFromType(ChessboardMove._TYPE);
        Point point1 = messageFactory.newFromType(Point._TYPE);
        point1.setX(1);
        point1.setY(1);
        move.setStartPos(point1);
        Point point2 = messageFactory.newFromType(Point._TYPE);
        point2.setX(3);
        point2.setY(3);
        List<Point> list2 = new ArrayList<Point>();
        list2.add(point2);
        move.setNextPos(list2);
        Point point3 = messageFactory.newFromType(Point._TYPE);
        point3.setX(2);
        point3.setY(2);
        List<Point> list3 = new ArrayList<Point>();
        list3.add(point3);
        move.setTakingPos(list3);
        return move;
    }
}
