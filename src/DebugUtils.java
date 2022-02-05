
import javax.management.MBeanServer;

import java.lang.management.ManagementFactory;

import java.io.IOException;

import java.util.HashMap;
import java.util.Map;

import jdk.management.jfr.FlightRecorderMXBean;

public class DebugUtils {
  
    /*  
     * java 12 won't work with this as it doesn't have the hot fixes (https://bugs.openjdk.java.net/browse/JDK-8225694)
     */
    public static String dumpJFR_mxBean() {
        MBeanServer server = ManagementFactory.getPlatformMBeanServer();
        FlightRecorderMXBean mxBean;
        Map<String, String> recordingOptions = getPredefinedRecordingOptions();

        try {
            mxBean = ManagementFactory.newPlatformMXBeanProxy(server,
                                                              "jdk.management.jfr:type=FlightRecorder",
                                                              FlightRecorderMXBean.class);
            long recId = mxBean.newRecording();
            mxBean.setRecordingOptions(recId, recordingOptions);
            mxBean.startRecording(recId);

        } catch (IOException e1) {
            e1.printStackTrace();
        }

        return recordingOptions.get("destination");
    }  

    /*
     * destination doesn't work in java9: https://bugs.openjdk.java.net/browse/JDK-8225694
     * This needs to be revisted past java11  
     */
    private static Map<String, String> getPredefinedRecordingOptions() {
        return new HashMap<>() {
            {
                put("name", "recording");
                put("maxAge", "60 s");
                put("maxSize", Long.toString(1024 * 1024 * 1024));
                put("dumpOnExit", "true");
                put("disk", "true");
                put("duration", "60 s");
                put("destination", "RECORDING.jfr");
            }
        };
    }
}
  
