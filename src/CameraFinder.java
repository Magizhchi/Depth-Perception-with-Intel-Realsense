import intel.rssdk.PXCMCapture;
import intel.rssdk.PXCMSession;

import java.util.EnumSet;

public class CameraFinder {
    public boolean checkR200Camera(){

        PXCMSession session = PXCMSession.CreateInstance();
        PXCMSession.ImplDesc desc = new PXCMSession.ImplDesc();
        PXCMSession.ImplDesc outDesc = new PXCMSession.ImplDesc();

        desc.group = EnumSet.of(PXCMSession.ImplGroup.IMPL_GROUP_SENSOR);
        desc.subgroup = EnumSet.of(PXCMSession.ImplSubgroup.IMPL_SUBGROUP_VIDEO_CAPTURE);

        for (int i = 0; ; i++) {
             if (session.QueryImpl(desc, i , outDesc).isError())
                 break;

            PXCMCapture capture = new PXCMCapture();

            if (session.CreateImpl(outDesc, capture).isError())
                continue;

            for (int j = 0; ; j++) {
                PXCMCapture.DeviceInfo info = new PXCMCapture.DeviceInfo();

                if (capture.QueryDeviceInfo(j, info).isError())
                    break;

                if (info.name.equals("Intel(R) RealSense(TM) 3D Camera R200")){
                    return true;
                }
            }
        }
        return false;
    }
}
