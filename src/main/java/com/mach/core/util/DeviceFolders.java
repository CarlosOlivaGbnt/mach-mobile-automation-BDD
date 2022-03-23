package com.mach.core.util;

import java.util.Arrays;
import java.util.List;

import static com.mach.core.pageobject.Screen.MACH_BUNDLE_ID;

public class DeviceFolders {
    private static final List<String> QR_FOLDER_NAME = Arrays.asList("/storage/emulated/0/Pictures/", "/storage/emulated/0/Android/data/" + MACH_BUNDLE_ID + "/files/Pictures/QRMACH");

    private DeviceFolders(){
    }

    public static List<String> androidQR(){
        return QR_FOLDER_NAME;
    }
}
