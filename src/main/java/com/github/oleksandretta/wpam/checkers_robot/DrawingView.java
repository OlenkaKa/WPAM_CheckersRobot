package com.github.oleksandretta.wpam.checkers_robot;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.ImageView;

import org.ros.android.BitmapFromCompressedImage;
import org.ros.android.MessageCallable;
import org.ros.message.MessageListener;
import org.ros.namespace.GraphName;
import org.ros.node.ConnectedNode;
import org.ros.node.Node;
import org.ros.node.NodeMain;
import org.ros.node.topic.Publisher;
import org.ros.node.topic.Subscriber;

import java.util.ArrayList;
import java.util.List;

import irp6_checkers.ChessboardMove;
import irp6_checkers.ImageData;
import sensor_msgs.CompressedImage;

/**
 * Created by Aleksandra Karbarczyk
 * date: 25.05.2015
 */

/**
 * Helper class which is used to hold float type points from onTouchEvent function
 */
class FloatPoint {
    private float x;
    private float y;

    FloatPoint(float x, float y) {
        this.x = x;
        this.y = y;
    }

    public float getX() {
        return x;
    }

    public float getY() {
        return y;
    }

    public void setX(float x) {
        this.x = x;
    }

    public void setY(float y) {
        this.y = y;
    }

    @Override
    public String toString() {
        return "FloatPoint(" + x + ", " + y + ')';
    }
}

public class DrawingView extends ImageView implements NodeMain {
    // path interpretation
    MoveGenerator moveGenerator;

    // topic names
    private String imageTopicName;
    private String imageDataTopicName;
    private String moveTopicName;

    // ROS connection
    private Publisher<ChessboardMove> movePublisher;
    //private ImageData imageData;

    // scale and translation of shown image
    private float scaleX;
    private float scaleY;
    private float shiftX;
    private float shiftY;

    // drawing variables
    private Bitmap canvasBitmap;
    private Paint drawPaint;
    private Path drawPath;
    private List<FloatPoint> drawPathPoints;

    public DrawingView(Context context) {
        super(context);
        setupDrawing();
    }

    public DrawingView(Context context, AttributeSet attrs) {
        super(context, attrs);
        setupDrawing();
    }

    public DrawingView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        setupDrawing();
    }

    private void setupDrawing() {
        drawPath = new Path();
        drawPaint = new Paint();
        drawPaint.setColor(getResources().getInteger(R.integer.paint_color));
        drawPaint.setAntiAlias(true);
        drawPaint.setStrokeWidth(getResources().getInteger(R.integer.brush_size));
        drawPaint.setStyle(Paint.Style.STROKE);
        drawPaint.setStrokeJoin(Paint.Join.ROUND);
        drawPaint.setStrokeCap(Paint.Cap.ROUND);
    }

    @Override
    public GraphName getDefaultNodeName() {
        return GraphName.of("checkers_robot_image");
    }

    @Override
    public void onStart(ConnectedNode connectedNode) {
        final MessageCallable<Bitmap, CompressedImage> imageCallable = new BitmapFromCompressedImage();
        Subscriber<CompressedImage> imageSubscriber = connectedNode.newSubscriber(imageTopicName, CompressedImage._TYPE);
        imageSubscriber.addMessageListener(new MessageListener<CompressedImage>() {
            @Override
            public void onNewMessage(final CompressedImage message) {
                post(new Runnable() {
                    @Override
                    public void run() {
                        canvasBitmap = imageCallable.call(message);
                        setImageBitmap(canvasBitmap);
                    }
                });
                postInvalidate();
            }
        });
        Subscriber<ImageData> imageDataSubscriber = connectedNode.newSubscriber(imageDataTopicName, ImageData._TYPE);
        imageDataSubscriber.addMessageListener(new MessageListener<ImageData>() {
            @Override
            public void onNewMessage(final ImageData message) {
                moveGenerator.setImageData(message);
            }
        });
        movePublisher = connectedNode.newPublisher(moveTopicName, ChessboardMove._TYPE);
        moveGenerator.setMessageFactory(connectedNode.getTopicMessageFactory());
    }

    @Override
    public void onShutdown(Node node) {
    }

    @Override
    public void onShutdownComplete(Node node) {
    }

    @Override
    public void onError(Node node, Throwable throwable) {
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        float[] values = new float[9];
        getImageMatrix().getValues(values);
        scaleX = values[Matrix.MSCALE_X];
        scaleY = values[Matrix.MSCALE_Y];
        shiftX = values[Matrix.MTRANS_X];
        shiftY = values[Matrix.MTRANS_Y];
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        float touchX = event.getX();
        float touchY = event.getY();
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                drawPath.moveTo(touchX, touchY);
                drawPathPoints = new ArrayList<FloatPoint>();
                drawPathPoints.add(new FloatPoint(touchX, touchY));
                break;
            case MotionEvent.ACTION_MOVE:
                drawPath.lineTo(touchX, touchY);
                drawPathPoints.add(new FloatPoint(touchX, touchY));
                break;
            case MotionEvent.ACTION_UP:
                drawPath.lineTo(touchX, touchY);
                drawPath.reset();
                moveRobot();
                break;
            default:
                return false;
        }
        invalidate();
        return true;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.drawPath(drawPath, drawPaint);
    }

    private void moveRobot() {
        for(FloatPoint point: drawPathPoints) {
            // normalize draw points to points in original image
            point.setX((point.getX() - shiftX) / scaleX);
            point.setY((point.getY() - shiftY) / scaleY);
        }
        System.out.println("{FP} " + drawPathPoints);
        ChessboardMove msg = moveGenerator.generateMove(drawPathPoints);
        if(msg != null)
            movePublisher.publish(msg);
    }

    public void setImageTopicName(String topicName) {
        this.imageTopicName = topicName;
    }

    public void setImageDataTopicName(String topicName) {
        this.imageDataTopicName = topicName;
    }

    public void setMoveTopicName(String topicName) {
        this.moveTopicName = topicName;
    }

    public void setGameProperties(int chessboardSize, int pawnColor1, int kingColor1,
                                  int pawnColor2, int kingColor2) {
        moveGenerator = new MoveGenerator(chessboardSize, pawnColor1, kingColor1,
                pawnColor2, kingColor2);
    }
}
