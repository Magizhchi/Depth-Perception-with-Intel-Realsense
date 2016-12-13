import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.awt.image.Raster;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.IntStream;


public class ImageUtil {
    private final int THRESHOLD_VALUE = 20;

    public ArrayList getPixelCoordinates(String path){
        ArrayList<Hashtable<String, Integer>> markers = new ArrayList<>();
        BufferedImage img = readImage(path);
        Raster raster = getRasterDataForImage(img);

        extractFirstMarker(markers, raster);
        extractSecondMarker(markers, raster);

        System.out.println(markers);

        return markers;
    }

    private void extractSecondMarker(ArrayList<Hashtable<String, Integer>> markerPixels, Raster raster) {
        int iSum = 0;
        int jSum = 0;
        int iCount = 0;
        int jCount = 0;

        for (int i = raster.getHeight()-1; i >= raster.getHeight()/2 ; i--){
            for (int j = 0; j < raster.getWidth(); j++) {
                if (getAmplitudeValueForPixel(j,i, raster) < THRESHOLD_VALUE){
                    iSum += i;
                    jSum += j;
                    iCount++;
                    jCount++;
                }
            }
        }

        Hashtable markerData = new Hashtable();
        markerData.put("height", iSum/iCount);
        markerData.put("width", jSum/jCount);
        markerPixels.add(markerData);
    }

    private void extractFirstMarker(ArrayList<Hashtable<String, Integer>> markerPixels, Raster raster) {
        int iSum = 0;
        int jSum = 0;
        int iCount = 0;
        int jCount = 0;
        for (int i = 0; i < raster.getHeight()/2; i++) {
            for (int j = 0; j < raster.getWidth(); j++) {
                if (getAmplitudeValueForPixel(j,i, raster) < THRESHOLD_VALUE){
                    iSum += i;
                    jSum += j;
                    iCount++;
                    jCount++;
                }
            }
        }

        Hashtable markerData = new Hashtable();
        markerData.put("height", iSum/iCount);
        markerData.put("width", jSum/jCount);
        markerPixels.add(markerData);
    }

    private int getAmplitudeValueForPixel(int i, int j, Raster raster) {
        try {
            int sum = 0;
            int numOfBands = raster.getNumBands();
            for (int k = 0; k < numOfBands; k++) {
                sum += raster.getSample(i, j, k);
            }
            return sum / numOfBands;
        } catch (Exception ex){
            System.out.println("Error Occured for : "+ i +" , " + j);
        }
        return 250;
    }

    private Raster getRasterDataForImage(BufferedImage img) {
        return img.getData();
    }

    private BufferedImage readImage(String path) {
        BufferedImage img = null;
        try {
            File imageFile = new File(path);
            img = ImageIO.read(imageFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return img;
    }
}
