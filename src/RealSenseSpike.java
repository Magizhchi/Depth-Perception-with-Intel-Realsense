
import intel.rssdk.*;

import javax.swing.*;
import java.awt.event.*;
import java.awt.image.*;
import java.awt.*;

import java.util.EnumSet;

public class RealSenseSpike {
    public static void main(String[] args) {
        PrintConnectedDevices();

        DisplayCameraFeed();
    }

    private static void DisplayCameraFeed() {
        PXCMSenseManager senseManager = PXCMSenseManager.CreateInstance();

        senseManager.EnableStream(PXCMCapture.StreamType.STREAM_TYPE_COLOR, 640, 480);
        senseManager.EnableStream(PXCMCapture.StreamType.STREAM_TYPE_DEPTH);
        senseManager.EnableStream(PXCMCapture.StreamType.STREAM_TYPE_RIGHT);
        senseManager.EnableStream(PXCMCapture.StreamType.STREAM_TYPE_LEFT);

        pxcmStatus sts = senseManager.Init();

        System.out.println(sts);

        PXCMCapture.Device device = senseManager.QueryCaptureManager().QueryDevice();
        PXCMCapture.Device.StreamProfileSet profiles = new PXCMCapture.Device.StreamProfileSet();
        device.QueryStreamProfileSet(profiles);

        int lWidth = profiles.left.imageInfo.width;
        int rWidth = profiles.right.imageInfo.width;
        int dWidth = profiles.depth.imageInfo.width;

        int lHeight = profiles.left.imageInfo.height;
        int rHeight = profiles.right.imageInfo.height;
        int dHeight = profiles.depth.imageInfo.height;

        Listener listener = new Listener();

        RealSenseSpike c_raw = new RealSenseSpike();
        DrawFrame c_df = new DrawFrame(640, 480);
        JFrame cframe = new JFrame("Intel(R) RealSenseSpike(TM) SDK - Color Stream");
        cframe.addWindowListener(listener);
        cframe.setSize(640, 480);
        cframe.add(c_df);
        cframe.setVisible(true);

        RealSenseSpike d_raw = new RealSenseSpike();
        DrawFrame d_df=new DrawFrame(dWidth, dHeight);
        JFrame dframe= new JFrame("Intel(R) RealSenseSpike(TM) SDK - Depth Stream");
        dframe.addWindowListener(listener);
        dframe.setSize(dWidth, dHeight);
        dframe.add(d_df);
        dframe.setVisible(true);

        RealSenseSpike r_raw = new RealSenseSpike();
        DrawFrame r_df=new DrawFrame(rWidth, rHeight);
        JFrame rframe= new JFrame("Intel(R) RealSenseSpike(TM) SDK - Right Stream");
        rframe.addWindowListener(listener);
        rframe.setSize(rWidth, rHeight);
        rframe.add(r_df);
        rframe.setVisible(true);

        RealSenseSpike l_raw = new RealSenseSpike();
        DrawFrame l_df=new DrawFrame(lWidth, lHeight);
        JFrame lframe= new JFrame("Intel(R) RealSenseSpike(TM) SDK - Left Stream");
        lframe.addWindowListener(listener);
        lframe.setSize(lWidth, lHeight);
        lframe.add(l_df);
        lframe.setVisible(true);

        if (sts == pxcmStatus.PXCM_STATUS_NO_ERROR) {

            while (listener.exit == false) {
                sts = senseManager.AcquireFrame(true);

                if (sts == pxcmStatus.PXCM_STATUS_NO_ERROR) {
                    PXCMCapture.Sample sample = senseManager.QuerySample();

                    if (sample.color != null) {
                        PXCMImage.ImageData cData = new PXCMImage.ImageData();
                        sts = sample.color.AcquireAccess(PXCMImage.Access.ACCESS_READ, PXCMImage.PixelFormat.PIXEL_FORMAT_RGB32, cData);
                        if (sts.compareTo(pxcmStatus.PXCM_STATUS_NO_ERROR) < 0) {
                            System.out.println("Failed to AcquireAccess of color image data");
                            System.exit(3);
                        }

                        int cBuff[] = new int[cData.pitches[0] / 4 * 480];

                        cData.ToIntArray(0, cBuff);
                        c_df.image.setRGB(0, 0, 640, 480, cBuff, 0, cData.pitches[0] / 4);
                        c_df.repaint();
                        sts = sample.color.ReleaseAccess(cData);

                        if (sts.compareTo(pxcmStatus.PXCM_STATUS_NO_ERROR) < 0) {
                            System.out.println("Failed to ReleaseAccess of color image data");
                            System.exit(3);
                        }
                    }

                    if (sample.depth != null)
                    {
                        PXCMImage.ImageData dData = new PXCMImage.ImageData();
                        sample.depth.AcquireAccess(PXCMImage.Access.ACCESS_READ,PXCMImage.PixelFormat.PIXEL_FORMAT_RGB32, dData);
                        if (sts.compareTo(pxcmStatus.PXCM_STATUS_NO_ERROR)<0)
                        {
                            System.out.println ("Failed to AcquireAccess of depth image data");
                            System.exit(3);
                        }

                        int dBuff[] = new int[dData.pitches[0]/4 * dHeight];
                        dData.ToIntArray(0, dBuff);
                        d_df.image.setRGB (0, 0, dWidth, dHeight, dBuff, 0, dData.pitches[0]/4);
                        d_df.repaint();
                        sts = sample.depth.ReleaseAccess(dData);
                        if (sts.compareTo(pxcmStatus.PXCM_STATUS_NO_ERROR)<0)
                        {
                            System.out.println ("Failed to ReleaseAccess of depth image data");
                            System.exit(3);
                        }
                    }

                    if(sample.right != null) {
                        PXCMImage.ImageData rData = new PXCMImage.ImageData();
                        sts = sample.right.AcquireAccess(PXCMImage.Access.ACCESS_READ, PXCMImage.PixelFormat.PIXEL_FORMAT_RGB32, rData);
                        if (sts.compareTo(pxcmStatus.PXCM_STATUS_NO_ERROR) < 0){
                            System.out.println("Failed to acquireAccess of depth image data");
                            System.exit(3);
                        }

                        int rBuff[] = new int[rData.pitches[0]/4 * rHeight];
                        rData.ToIntArray(0, rBuff);
                        r_df.image.setRGB(0,0,rWidth, rHeight, rBuff,0,rData.pitches[0]/4);
                        r_df.repaint();
                        sts = sample.right.ReleaseAccess(rData);

                        if (sts.compareTo(pxcmStatus.PXCM_STATUS_NO_ERROR) < 0){
                            System.out.println("Failed to releaseAccess of depth image data");
                            System.exit(3);
                        }
                    }

                    if(sample.left != null) {
                        PXCMImage.ImageData lData = new PXCMImage.ImageData();
                        sts = sample.left.AcquireAccess(PXCMImage.Access.ACCESS_READ, PXCMImage.PixelFormat.PIXEL_FORMAT_RGB32, lData);
                        if (sts.compareTo(pxcmStatus.PXCM_STATUS_NO_ERROR) < 0){
                            System.out.println("Failed to acquireAccess of depth image data");
                            System.exit(3);
                        }

                        int lBuff[] = new int[lData.pitches[0]/4 * lHeight];
                        lData.ToIntArray(0, lBuff);
                        l_df.image.setRGB(0,0,lWidth, lHeight, lBuff,0,lData.pitches[0]/4);
                        l_df.repaint();
                        sts = sample.left.ReleaseAccess(lData);

                        if (sts.compareTo(pxcmStatus.PXCM_STATUS_NO_ERROR) < 0){
                            System.out.println("Failed to releaseAccess of depth image data");
                            System.exit(3);
                        }
                    }
                } else {
                    System.out.println("Failed to acquire frame");
                }

                senseManager.ReleaseFrame();
            }
        } else {
            System.out.println("Failed to initialize!");
        }

        senseManager.close();
        cframe.dispose();
    }

    private static void PrintConnectedDevices() {
        PXCMSession session = PXCMSession.CreateInstance();
        PXCMSession.ImplDesc desc = new PXCMSession.ImplDesc();
        PXCMSession.ImplDesc outDesc = new PXCMSession.ImplDesc();

        desc.group = EnumSet.of(PXCMSession.ImplGroup.IMPL_GROUP_SENSOR);
        desc.subgroup = EnumSet.of(PXCMSession.ImplSubgroup.IMPL_SUBGROUP_VIDEO_CAPTURE);

        int numDevices = 0;
        for (int i = 0; ; i++) {
            if (session.QueryImpl(desc, i, outDesc).isError())
                break;

            PXCMCapture capture = new PXCMCapture();
            if (session.CreateImpl(outDesc, capture).isError())
                continue;

            for (int j = 0; ; j++) {
                PXCMCapture.DeviceInfo info = new PXCMCapture.DeviceInfo();
                if (capture.QueryDeviceInfo(j, info).isError())
                    break;

                System.out.println(info.name);
                numDevices++;
            }
        }

        System.out.println("Found " + numDevices + " devices");
    }
}

class Listener extends WindowAdapter {
    public boolean exit = false;

    @Override
    public void windowClosing(WindowEvent e) {
        exit = true;
    }
}


class DrawFrame extends Component {
    public BufferedImage image;

    public DrawFrame(int width, int height) {
        image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
    }

    public void paint(Graphics g) {
        ((Graphics2D) g).drawImage(image, 0, 0, null);
    }
}
