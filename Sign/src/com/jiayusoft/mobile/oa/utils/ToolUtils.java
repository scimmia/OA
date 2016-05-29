package com.jiayusoft.mobile.oa.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.wifi.WifiManager;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.telephony.TelephonyManager;

import java.io.UnsupportedEncodingException;

/**
 * Created by ASUS on 2014/9/29.
 */
public class ToolUtils {
    private static synchronized String getMachineUUID(Context context){
        String uuid;
        WifiManager wm = (WifiManager)context.getSystemService(Context.WIFI_SERVICE);
        String wifiId = wm.getConnectionInfo().getMacAddress();

        final String androidId = Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID);
        try {
            if (wifiId!=null){
                uuid = java.util.UUID.nameUUIDFromBytes(wifiId.getBytes("utf8")).toString();
            }else if (androidId!=null && !"9774d56d682e549c".equals(androidId)) {
                uuid = java.util.UUID.nameUUIDFromBytes(androidId.getBytes("utf8")).toString();
            } else {
                final String deviceId = ((TelephonyManager) context.getSystemService( Context.TELEPHONY_SERVICE )).getDeviceId();
                uuid = deviceId!=null ? java.util.UUID.nameUUIDFromBytes(deviceId.getBytes("utf8")).toString() : java.util.UUID.randomUUID().toString();
            }
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
        return uuid;
    }

    public static String getUUID(Context context){
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        String uuid = sharedPreferences.getString("MachineUUID",null);
        if (uuid==null|| uuid.length()==0){
            uuid = getMachineUUID(context);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putString("MachineUUID",uuid);
            editor.commit();
        }
        return uuid;
    }
}
