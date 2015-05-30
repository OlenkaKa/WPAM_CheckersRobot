package com.github.oleksandretta.wpam.checkers_robot;

import android.os.Bundle;

import org.ros.address.InetAddressFactory;
import org.ros.android.RosActivity;
import org.ros.node.NodeConfiguration;
import org.ros.node.NodeMainExecutor;

import irp6_checkers.ColorPoint;

/**
 * Created by Aleksandra Karbarczyk
 * date: 18.05.2015
 */
public class MainActivity extends RosActivity {
    // game properties
    private int chessboardSize = 8;
    private int pawnColor1 = ColorPoint.COLOR_GREEN;
    private int kingColor1 = ColorPoint.COLOR_BLUE;
    private int pawnColor2 = ColorPoint.COLOR_RED;
    private int kingColor2 = ColorPoint.COLOR_YELLOW;

    // topics names
    private String imageTopicName = "/image/compressed";
    private String imageDataTopicName = "/image_data";
    private String moveTopicName = "/move";

    private DrawingView image;

    public MainActivity()
    {
        super("CheckersRobot", "CheckersRobot");
    }

    /** Called when the activity is first created. */
    @SuppressWarnings("unchecked")
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        image = (DrawingView) findViewById(R.id.image);
    }

    @Override
    protected void init(final NodeMainExecutor nodeMainExecutor) {
        image.setImageTopicName(imageTopicName);
        image.setImageDataTopicName(imageDataTopicName);
        image.setMoveTopicName(moveTopicName);
        image.setGameProperties(chessboardSize, pawnColor1, kingColor1, pawnColor2, kingColor2);
        NodeConfiguration nodeConfiguration =
                NodeConfiguration.newPublic(InetAddressFactory.newNonLoopback().getHostAddress(),
                        getMasterUri());
        nodeMainExecutor.execute(image, nodeConfiguration.setNodeName("wpam/checkers_robot"));
    }

    /*
    @Override
    public void startMasterChooser()
    {
        Preconditions.checkState(getMasterUri() == null);
        super.startActivityForResult(new Intent(this, GameOptions.class), 0);
    }
    */
}
