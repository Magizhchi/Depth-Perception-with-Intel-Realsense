public class RealSense {
    public static void main(String[] args) {
        CameraFinder r200 = new CameraFinder();
        if (!r200.checkR200Camera())
            System.exit(-1);

    }
}
