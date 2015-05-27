package com.github.oleksandretta.wpam.checkers_robot;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Point;
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
public class DrawingView extends ImageView implements NodeMain {
    private String imageTopicName;
    private String imageDataTopicName;
    private String moveTopicName;

    private ImageData imageData;
    //private MessageCallable<Bitmap, CompressedImage> imageCallable;

    private Subscriber<CompressedImage> imageSubscriber;
    private Subscriber<ImageData> imageDataSubscriber;
    private Publisher<ChessboardMove> movePublisher;

    private Bitmap canvasBitmap;
    private Paint drawPaint;
    private Path drawPath;
    private List<Point> drawPathPoints;
    private int paintColor = 0xFF660000;

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
        drawPaint.setColor(paintColor);
        drawPaint.setAntiAlias(true);
        drawPaint.setStrokeWidth(getResources().getInteger(R.integer.brush_size));
        drawPaint.setStyle(Paint.Style.STROKE);
        drawPaint.setStrokeJoin(Paint.Join.ROUND);
        drawPaint.setStrokeCap(Paint.Cap.ROUND);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.drawPath(drawPath, drawPaint);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        float touchX = event.getX();
        float touchY = event.getY();
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                drawPath.moveTo(touchX, touchY);
                drawPathPoints = new ArrayList<Point>();
                drawPathPoints.add(new Point((int)touchX, (int)touchY));
                break;
            case MotionEvent.ACTION_MOVE:
                drawPath.lineTo(touchX, touchY);
                drawPathPoints.add(new Point((int) touchX, (int) touchY));
                break;
            case MotionEvent.ACTION_UP:
                drawPath.lineTo(touchX, touchY);
                drawPath.reset();
                generateMove();
                break;
            default:
                return false;
        }
        invalidate();
        return true;
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

    @Override
    public GraphName getDefaultNodeName() {
        return GraphName.of("checkers_robot_image");
    }

    @Override
    public void onStart(ConnectedNode connectedNode) {
        final MessageCallable<Bitmap, CompressedImage> imageCallable = new BitmapFromCompressedImage();
        imageSubscriber = connectedNode.newSubscriber(imageTopicName, CompressedImage._TYPE);
        imageSubscriber.addMessageListener(new MessageListener<CompressedImage>() {
            @Override
            public void onNewMessage(final CompressedImage message) {
                System.out.println("[OLKA]");
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
        imageDataSubscriber = connectedNode.newSubscriber(imageDataTopicName, ImageData._TYPE);
        imageDataSubscriber.addMessageListener(new MessageListener<ImageData>() {
            @Override
            public void onNewMessage(final ImageData message) {
                imageData = message;
            }
        });
        movePublisher = connectedNode.newPublisher(moveTopicName, ChessboardMove._TYPE);
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

    private void generateMove() {
        System.out.println("[Ola]" + drawPathPoints);
    }
}
