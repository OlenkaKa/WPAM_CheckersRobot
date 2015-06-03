package com.github.oleksandretta.wpam.checkers_robot;

import org.ros.message.MessageFactory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import irp6_checkers.ColorPoint;
import irp6_checkers.ImageData;
import irp6_checkers.Point;

/**
 * Created by Aleksandra Karbarczyk
 * date: 30.05.2015
 */
public class Chessboard {
    private final int CHESSBOARD_SIZE;
    private final int FIRST_PAWN;
    private final int FIRST_KING;
    private final int SECOND_PAWN;
    private final int SECOND_KING;
    private Map<Point, Integer> checkers;

    // TODO: now temporary variables...
    private ImageData imageData;
    private int maxX;
    private int maxY;
    private int minX;
    private int minY;

    public Chessboard(int chessboardSize, int pawn1, int king1, int pawn2, int king2) {
        CHESSBOARD_SIZE = chessboardSize;
        FIRST_PAWN = pawn1;
        FIRST_KING = king1;
        SECOND_PAWN = pawn2;
        SECOND_KING = king2;
        checkers = new HashMap<Point, Integer>();
    }

    public void setImageData(ImageData imageData, MessageFactory messageFactory) {
        this.imageData = imageData;
        maxX = imageData.getMaxCorner().getX();
        maxY = imageData.getMaxCorner().getY();
        minX = imageData.getMinCorner().getX();
        minY = imageData.getMinCorner().getY();

        checkers = new HashMap<Point, Integer>();
        final float fieldLengthX = (maxX - minX) / CHESSBOARD_SIZE;
        final float fieldLengthY = (maxY - minY) / CHESSBOARD_SIZE;
        for(ColorPoint cp: imageData.getCheckerFields()) {
            float deltaX = minX;
            float deltaY = minY;
            int fieldX = -1;
            int fieldY = -1;
            while (deltaX < cp.getX() && cp.getX() > minX) {
                fieldX += 1;
                deltaX += fieldLengthX;
            }
            while (deltaY < cp.getY() && cp.getY() > minY) {
                fieldY += 1;
                deltaY += fieldLengthY;
            }
            fieldY = CHESSBOARD_SIZE - 1 - fieldY;
            if (fieldX > -1 && fieldX < CHESSBOARD_SIZE && fieldY > -1 && fieldY < CHESSBOARD_SIZE
                    && ((fieldX+fieldY) % 2 == 0)) {
                if(cp.getColor() != ColorPoint.COLOR_OTHER) {
                    Point chessboardPoint = messageFactory.newFromType(Point._TYPE);
                    chessboardPoint.setX(fieldX);
                    chessboardPoint.setY(fieldY);
                    checkers.put(chessboardPoint, cp.getColor());
                }
            }
        }
    }

    public List<Point> getChessboardBlackFields(List<FloatPoint> points, MessageFactory messageFactory) {
        List<Point> result = new ArrayList<Point>();
        final float fieldLengthX = (maxX - minX) / CHESSBOARD_SIZE;
        final float fieldLengthY = (maxY - minY) / CHESSBOARD_SIZE;
        for(FloatPoint fp: points) {
            float deltaX = minX;
            float deltaY = minY;
            int fieldX = -1;
            int fieldY = -1;
            while (deltaX < fp.getX() && fp.getX() > minX) {
                fieldX += 1;
                deltaX += fieldLengthX;
            }
            while (deltaY < fp.getY() && fp.getY() > minY) {
                fieldY += 1;
                deltaY += fieldLengthY;
            }
            fieldY = CHESSBOARD_SIZE - 1 - fieldY;
            if (fieldX > -1 && fieldX < CHESSBOARD_SIZE && fieldY > -1 && fieldY < CHESSBOARD_SIZE
                    && ((fieldX+fieldY) % 2 == 0)) {
                if(result.isEmpty()
                        || result.get(result.size() - 1).getX() != fieldX
                        || result.get(result.size() - 1).getY() != fieldY) {
                    Point chessboardPoint = messageFactory.newFromType(Point._TYPE);
                    chessboardPoint.setX(fieldX);
                    chessboardPoint.setY(fieldY);
                    result.add(chessboardPoint);
                }
            }
        }
        return result;
    }

    public int getFieldContent(Point chessboardField) {
        Integer result =  checkers.get(chessboardField);
        if(result == null)
            return ColorPoint.COLOR_OTHER;
        return result;
    }
}
