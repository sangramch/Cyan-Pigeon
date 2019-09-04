package com.sangramjit.projects.chatsapp.utils;

import android.content.Context;
import android.telephony.TelephonyManager;

public class getCountryCode {
    public static String getCountryCode(Context context){
        String iso=null;

        TelephonyManager telephonyManager=(TelephonyManager) context.getSystemService(context.TELEPHONY_SERVICE);
        if (telephonyManager.getNetworkCountryIso()!=null) {
            if(!telephonyManager.getNetworkCountryIso().equals("")){
                iso=telephonyManager.getNetworkCountryIso();
            }
        }

        return Iso2Phone.getPhone(iso);
    }
}
