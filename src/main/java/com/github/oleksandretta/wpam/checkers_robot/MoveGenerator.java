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
    private final int CHESSBOARD_SIZE;
    private final int FIRST_PAWN;
    private final int FIRST_KING;
    private final int SECOND_PAWN;
    private final int SECOND_KING;
    private MessageFactory messageFactory;

    // TODO: now temporary variables...
    private ImageData imageData;
    private int maxX;
    private int maxY;
    private int minX;
    private int minY;

    public MoveGenerator(int chessboardSize, int pawn1, int king1, int pawn2, int king2) {
        CHESSBOARD_SIZE = chessboardSize;
        FIRST_PAWN = pawn1;
        FIRST_KING = king1;
        SECOND_PAWN = pawn2;
        SECOND_KING = king2;
    }

    public ChessboardMove generateMove(List<FloatPoint> points) {
        // TODO
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

    /*
    private List<Point> getVisitedFields(List<FloatPoint> points) {
        List<Point> result = new ArrayList<Point>();
        final float fieldLengthX = (maxX - minX) / CHESSBOARD_SIZE;
        final float fieldLengthY = (maxY - minY) / CHESSBOARD_SIZE;
        for(FloatPoint fp: points) {
            float deltaX = minX;
            float deltaY = minY;
            int fieldX = -1;
            int fieldY = -1;
            while (fieldX < CHESSBOARD_SIZE && deltaX < fp.getX()) {
                fieldX += 1;
                deltaX += fieldLengthX;
            }
            while (fieldY < CHESSBOARD_SIZE && deltaY < fp.getY()) {
                fieldY += 1;
                deltaY += fieldLengthY;
            }
            if (fieldX > -1 && fieldX < CHESSBOARD_SIZE && fieldY > -1 && fieldY < CHESSBOARD_SIZE) {
                Point chessboardPoint = messageFactory.newFromType(Point._TYPE);
                chessboardPoint.setY(fieldX);
                chessboardPoint.setY(fieldY);
                result.add(chessboardPoint);
            }
        }
        return result;
    }
    */

    public void setMessageFactory(MessageFactory messageFactory) {
        this.messageFactory = messageFactory;
    }

    public void setImageData(ImageData imageData) {
        this.imageData = imageData;
        maxX = imageData.getMaxCorner().getX();
        maxY = imageData.getMaxCorner().getY();
        minX = imageData.getMinCorner().getX();
        minY = imageData.getMinCorner().getY();
    }
}
