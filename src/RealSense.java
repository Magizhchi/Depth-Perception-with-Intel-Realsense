
import intel.rssdk.*;

import java.util.EnumSet;

public class RealSense {
    public static void main(String[] args) {
        PrintConnectedDevices();
    }

    private static void PrintConnectedDevices() {
        PXCMSession session = PXCMSession.CreateInstance();
        PXCMSession.ImplDesc desc = new PXCMSession.ImplDesc();
        PXCMSession.ImplDesc outDesc = new PXCMSession.ImplDesc();

        desc.group = EnumSet.of(PXCMSession.ImplGroup.IMPL_GROUP_SENSOR);
        desc.subgroup = EnumSet.of(PXCMSession.ImplSubgroup.IMPL_SUBGROUP_VIDEO_CAPTURE);

        int numDevices = 0;
        for (int i = 0;; i++) {
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
