import intel.rssdk.*;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.stream.IntStream;
import java.util.stream.Stream;


public class CameraCapture {

    public static final int height = 480;
    public static final int width = 640;


    public void captureImage(){

        PXCMCapture.StreamType streamType = PXCMCapture.StreamType.STREAM_TYPE_COLOR;

        // Initalize stream
        PXCMSenseManager senseManager = PXCMSenseManager.CreateInstance();
        senseManager.EnableStream(streamType, width, height);
        senseManager.Init();

        if (senseManager.AcquireFrame(true).isError()) {
            System.out.println("error trying to capture the stream");
            return;
        }

        PXCMCapture.Sample sample = senseManager.QuerySample();

        PXCMImage.ImageData data = new PXCMImage.ImageData();
        sample.color.AcquireAccess(PXCMImage.Access.ACCESS_READ, PXCMImage.PixelFormat.PIXEL_FORMAT_RGB32, data);
        PXCMImage.ImageInfo info = sample.color.QueryInfo();

        //pitches[0] will contain the height of the image *4 Probably for RGBA spectrum
        int[] intArray = new int[data.pitches[0] / 4 * info.height];

        data.ToIntArray(0, intArray);

        printColorImage(data.pitches[0], intArray);

        senseManager.ReleaseFrame();

    }

    private void printColorImage(int pitch, int[] intArray) {
        BufferedImage bImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        bImage.setRGB(0, 0, width, height, intArray, 0, pitch / 4);

        System.out.println(bImage.toString());

        File out = new File("color.png");
        try {
            ImageIO.write(bImage, "png", out);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void getProjectionData(ArrayList<Hashtable> markers){
        PXCMSenseManager senseManager = PXCMSenseManager.CreateInstance();

        senseManager.EnableStream(PXCMCapture.StreamType.STREAM_TYPE_COLOR,width,height);
        senseManager.EnableStream(PXCMCapture.StreamType.STREAM_TYPE_DEPTH);

        pxcmStatus status = senseManager.Init();

        if(status.isError()){
            System.out.println("Unable to initialize color and depth streams");
            System.exit(-1);
        }

        PXCMCapture.Device device = senseManager.QueryCaptureManager().QueryDevice();

        PXCMProjection projection;

        status = senseManager.AcquireFrame(true);

        if (status.isError()){
            System.out.println("Error trying to acquire the frame");
            System.exit(-1);
        }

        PXCMCapture.Sample sample = senseManager.QuerySample();

        PXCMImage.ImageData data = new PXCMImage.ImageData();
        sample.depth.AcquireAccess(PXCMImage.Access.ACCESS_READ, PXCMImage.PixelFormat.PIXEL_FORMAT_DEPTH, data);

        PXCMImage.ImageInfo depthInfo = sample.depth.QueryInfo();
//
//        int[] dataArr = data.ToIntArray(0,(data.pitches[0]/ 4) * depthInfo.width);

        projection = device.CreateProjection();
        PXCMPoint3DF32[] vertices = new PXCMPoint3DF32[sample.depth.QueryInfo().width * sample.depth.QueryInfo().height];

        status = projection.QueryVertices(sample.depth, vertices);
        System.out.println("Status after Querying vertices is : "+ status);

        markers.forEach(e -> printVertices(e, vertices));

        projection.close();

        senseManager.ReleaseFrame();

    }

    private static void printVertices(Hashtable<String,Integer> marker, PXCMPoint3DF32[] vertices){
        PXCMPoint3DF32 pixel = vertices[marker.get("height")*height + marker.get("width")];
        System.out.println("The 3D Pixel Value of marker in mm is : "+ pixel);
    }
}
