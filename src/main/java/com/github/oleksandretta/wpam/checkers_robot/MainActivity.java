package com.github.oleksandretta.wpam.checkers_robot;

import android.os.Bundle;
import org.ros.address.InetAddressFactory;
import org.ros.android.RosActivity;
import org.ros.node.NodeConfiguration;
import org.ros.node.NodeMainExecutor;

/**
 * Created by Aleksandra Karbarczyk
 * date: 18.05.2015
 */
public class MainActivity extends RosActivity
{
    // TODO: get topic names from master chooser
    private String imageTopicName = "/usb_cam/image_raw/compressed";
    private String imageDataTopicName = "/image/data";
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
        NodeConfiguration nodeConfiguration =
                NodeConfiguration.newPublic(InetAddressFactory.newNonLoopback().getHostAddress(),
                        getMasterUri());
        nodeMainExecutor.execute(image, nodeConfiguration.setNodeName("wpam/checkers_robot"));
    }

    /*
    @Override
    public void startMasterChooser()
    {
        // TODO
    }
    */
}
