package com.github.oleksandretta.wpam.checkers_robot;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Path;
import android.util.AttributeSet;
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
    private CompressedImage image;

    private Subscriber<CompressedImage> imageSubscriber;
    private Subscriber<ImageData> imageDataSubscriber;
    private Publisher<ChessboardMove> movePublisher;

    private MessageCallable<Bitmap, CompressedImage> imageCallable;

    public DrawingView(Context context) {
        super(context);
    }

    public DrawingView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public DrawingView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);

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
        imageCallable = new BitmapFromCompressedImage();
        imageSubscriber = connectedNode.newSubscriber(imageTopicName, CompressedImage._TYPE);
        imageSubscriber.addMessageListener(new MessageListener<CompressedImage>() {
            @Override
            public void onNewMessage(final CompressedImage message) {
                post(new Runnable() {
                    @Override
                    public void run() {
                        image = message;
                        setImageBitmap(imageCallable.call(message));
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
                System.out.println("[Ola] Receive imageData");
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
}
