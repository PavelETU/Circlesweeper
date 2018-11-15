package com.wordpress.lonelytripblog.circlesminesweeper.utils;

import android.os.Build;
import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

final public class FullWindowUtils {

    private FullWindowUtils() {
    }

    public static void enterFullScreenMode(@NonNull Window window) {
        if (sdkOlderWhenKitKat()) {
            setFullscreenFlag(window);
        } else {
            enterImmersiveSticky(window);
        }
    }

    private static boolean sdkOlderWhenKitKat() {
        return Build.VERSION.SDK_INT < Build.VERSION_CODES.KITKAT;
    }

    private static void setFullscreenFlag(@NonNull Window window) {
        window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
    }

    @RequiresApi(Build.VERSION_CODES.KITKAT)
    private static void enterImmersiveSticky(@NonNull Window window) {
        final View decorView = window.getDecorView();
        decorView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                | View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_FULLSCREEN);
    }
}
