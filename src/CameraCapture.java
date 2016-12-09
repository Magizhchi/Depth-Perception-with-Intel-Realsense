import intel.rssdk.PXCMCapture;
import intel.rssdk.PXCMImage;
import intel.rssdk.PXCMSenseManager;
import intel.rssdk.pxcmStatus;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.stream.Stream;


public class CameraCapture {

    public final int height = 480;
    public final int width = 640;


    public void captureImage(String imageType){

        PXCMCapture.StreamType streamType = PXCMCapture.StreamType.STREAM_TYPE_COLOR;

        PXCMSenseManager senseManager = PXCMSenseManager.CreateInstance();

        senseManager.EnableStream(streamType, width, height);

        pxcmStatus sts = senseManager.Init();

        if (senseManager.AcquireFrame(true).isError())
            return;

        PXCMCapture.Sample sample = senseManager.QuerySample();

        PXCMImage.ImageData data = new PXCMImage.ImageData();
        sample.color.AcquireAccess(PXCMImage.Access.ACCESS_READ, PXCMImage.PixelFormat.PIXEL_FORMAT_RGB32, data);
//        PXCMImage.ImageInfo info = sample.color.QueryInfo();
//
//        System.out.println(info.height);
//        System.out.println(info.width);

        int[] intArray = new int[data.pitches[0] / 4 *480];

        data.ToIntArray(0, intArray);

        BufferedImage bImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        bImage.setRGB(0, 0, width, height, intArray, 0, data.pitches[0] / 4);

        System.out.println(bImage.toString());

//        File out = new File("test.jpg");
//        try {
//            ImageIO.write(bImage, "jpg", out);
//        } catch (IOException e) {
//            e.printStackTrace();
//        }

        senseManager.ReleaseFrame();

    }
}
