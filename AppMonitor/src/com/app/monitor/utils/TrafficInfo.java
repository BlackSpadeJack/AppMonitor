package com.app.monitor.utils;

import android.net.TrafficStats;
import android.util.Log;

/**
 * information of network traffic
 */
public class TrafficInfo {

    private static final String LOG_TAG = "Emmagee-" + TrafficInfo.class.getSimpleName();
    private static final int UNSUPPORTED = -1;

    private String uid;

    public TrafficInfo(String uid) {
        this.uid = uid;
    }

    /**
     * get total network traffic, which is the sum of upload and download traffic.
     * 
     * @return total traffic include received and send traffic
     */
    public long getTrafficInfo() {
        // Log.i(LOG_TAG, "get traffic information");

        long rcvTraffic = UNSUPPORTED;
        long sndTraffic = UNSUPPORTED;

        // Use getUidRxBytes and getUidTxBytes to get network traffic,these API
        // return both tcp and udp usage
        rcvTraffic = TrafficStats.getUidRxBytes(Integer.parseInt(uid));
        sndTraffic = TrafficStats.getUidTxBytes(Integer.parseInt(uid));

        if (rcvTraffic == UNSUPPORTED || sndTraffic == UNSUPPORTED) {
            return UNSUPPORTED;
        } else
            return rcvTraffic + sndTraffic;
    }
}
