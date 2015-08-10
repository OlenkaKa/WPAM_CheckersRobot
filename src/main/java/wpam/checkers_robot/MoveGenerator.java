package wpam.checkers_robot;

import org.ros.message.MessageFactory;

import java.util.ArrayList;
import java.util.List;

import irp6_checkers.ChessboardMove;
import irp6_checkers.ColorPoint;
import irp6_checkers.ImageData;
import irp6_checkers.Point;

import static java.lang.Math.abs;

/**
 * Created by Aleksandra Karbarczyk
 * date: 28.05.2015
 */
public class MoveGenerator {

    private Chessboard chessboard;
    private MessageFactory messageFactory;

    public MoveGenerator(int chessboardSize, int pawn1, int king1, int pawn2, int king2) {
        chessboard = new Chessboard(chessboardSize, pawn1, king1, pawn2, king2);
    }

    public ChessboardMove generateMove(List<FloatPoint> points) {
        // TODO: checker end move on another checker
        List<Point> blackFields = chessboard.getChessboardBlackFields(points, messageFactory);
        if(blackFields.isEmpty() || blackFields.size() == 1 ||
                chessboard.getFieldContent(blackFields.get(0)) == ColorPoint.COLOR_OTHER)
            return null;

        ChessboardMove move = messageFactory.newFromType(ChessboardMove._TYPE);
        Point startPoint = messageFactory.newFromType(Point._TYPE);
        startPoint.setX(blackFields.get(0).getX());
        startPoint.setY(blackFields.get(0).getY());
        move.setStartPos(startPoint);

        Point lastPoint = startPoint;
        List<Point> takingList = new ArrayList<Point>();
        List<Point> nextList = new ArrayList<Point>();
        for(int i = 1; i < blackFields.size(); ++i) {
            Point point = blackFields.get(i);
            if(chessboard.getFieldContent(point) != ColorPoint.COLOR_OTHER) {
                if(i+1 >= blackFields.size())
                    break;
                Point takingPoint = messageFactory.newFromType(Point._TYPE);
                takingPoint.setX(point.getX());
                takingPoint.setY(point.getY());
                takingList.add(takingPoint);
            }
            else {
                if(i+1 >= blackFields.size()
                        || abs(blackFields.get(i+1).getX() - lastPoint.getX()) != abs(blackFields.get(i+1).getY()) - lastPoint.getY()) {
                    Point nextPoint = messageFactory.newFromType(Point._TYPE);
                    nextPoint.setX(point.getX());
                    nextPoint.setY(point.getY());
                    nextList.add(nextPoint);
                    lastPoint = nextPoint;
                }
            }
        }
        if(nextList.isEmpty() && !takingList.isEmpty())
            return null;

        move.setTakingPos(takingList);
        move.setNextPos(nextList);
        return move;

        // TEST MOVE
        /*
        ChessboardMove move = messageFactory.newFromType(ChessboardMove._TYPE);

        Point point1 = messageFactory.newFromType(Point._TYPE);
        point1.setX(2);
        point1.setY(6);
        move.setStartPos(point1);

        List<Point> next = new ArrayList<Point>();
        Point pointn1 = messageFactory.newFromType(Point._TYPE);
        pointn1.setX(4);
        pointn1.setY(4);
        next.add(pointn1);
        Point pointn2 = messageFactory.newFromType(Point._TYPE);
        pointn2.setX(2);
        pointn2.setY(2);
        next.add(pointn2);
        Point pointn3 = messageFactory.newFromType(Point._TYPE);
        pointn3.setX(4);
        pointn3.setY(0);
        next.add(pointn3);
        move.setNextPos(next);

        List<Point> taking = new ArrayList<Point>();
        Point pointt1 = messageFactory.newFromType(Point._TYPE);
        pointt1.setX(3);
        pointt1.setY(5);
        taking.add(pointt1);
        Point pointt2 = messageFactory.newFromType(Point._TYPE);
        pointt2.setX(3);
        pointt2.setY(3);
        taking.add(pointt2);
        Point pointt3 = messageFactory.newFromType(Point._TYPE);
        pointt3.setX(3);
        pointt3.setY(1);
        taking.add(pointt3);
        move.setTakingPos(taking);
        return move;
        */

    }

    public void setMessageFactory(MessageFactory messageFactory) {
        this.messageFactory = messageFactory;
    }

    public void setImageData(ImageData imageData) {
        chessboard.setImageData(imageData, messageFactory);
    }
}
