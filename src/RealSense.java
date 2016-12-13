import java.util.ArrayList;
import java.util.Hashtable;

public class RealSense {
    public static void main(String[] args) {
        CameraFinder r200 = new CameraFinder();
        if (!r200.checkR200Camera())
            System.exit(-1);

        CameraCapture capture = new CameraCapture();
        capture.captureImage();

        ImageUtil imgUtil = new ImageUtil();
        ArrayList markers = imgUtil.getPixelCoordinates("color.png");

        capture.getProjectionData(markers);

    }
}
