package com.github.oleksandretta.wpam.checkers_robot;

import android.os.Bundle;
import org.ros.address.InetAddressFactory;
import org.ros.android.BitmapFromCompressedImage;
import org.ros.android.RosActivity;
import org.ros.android.view.RosImageView;
import org.ros.node.NodeConfiguration;
import org.ros.node.NodeMainExecutor;

public class MainActivity extends RosActivity
{
    private RosImageView<sensor_msgs.CompressedImage> image;

    public MainActivity()
    {
        super("CheckersRobot", "CheckersRobot");
    }

    /** Called when the activity is first created. */
    @SuppressWarnings("unchecked")
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        image = (RosImageView<sensor_msgs.CompressedImage>) findViewById(R.id.image);
        image.setTopicName("/usb_cam/image_raw/compressed");
        image.setMessageType(sensor_msgs.CompressedImage._TYPE);
        image.setMessageToBitmapCallable(new BitmapFromCompressedImage());
    }

    @Override
    protected void init(final NodeMainExecutor nodeMainExecutor)
    {
        NodeConfiguration nodeConfiguration =
                NodeConfiguration.newPublic(InetAddressFactory.newNonLoopback().getHostAddress(),
                        getMasterUri());
        nodeMainExecutor.execute(image, nodeConfiguration.setNodeName("android/video_view"));
    }

    /*
    @Override
    public void startMasterChooser()
    {
        // TODO
    }
    */
}
