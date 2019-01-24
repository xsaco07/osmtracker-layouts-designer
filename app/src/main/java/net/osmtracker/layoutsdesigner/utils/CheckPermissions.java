package net.osmtracker.layoutsdesigner.utils;

import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;

public class CheckPermissions {

    /**
     * This method check if certain permission is denied or granted, and then returns the result (if is denied then the app needs to make the request)
     * @param context = the current context where the permission is needed
     * @param permissionToCheck = string with the permission to check (example: Manifest.permission.READ_EXTERNAL_STORAGE)
     * @return true if the permission is denied or false if is granted
     */
    public static boolean isPermissionDenied(Context context, String permissionToCheck){
        Log.i("CheckPermissions", "Verifying if the permission is denied");
        return ContextCompat.checkSelfPermission(context, permissionToCheck) != PackageManager.PERMISSION_GRANTED;
    }

    /**
     * This method check if we need to show a more explain dialog to user because he denied the permission
     * @param activity = the current activity where the permission is needed
     * @param permissionToCheck = string with the permission denied
     * @return true if is needed make a better explanation or false is not
     */
    public static boolean needsToExplainToUser(Activity activity, String permissionToCheck){
        Log.i("CheckPermissions", "Verifying if wee need a better explanation");
        return ActivityCompat.shouldShowRequestPermissionRationale(activity, permissionToCheck);
    }

    /**
     * This method makes a request with the specified permission and code request
     * @param activity = the current activity where we need to make the request
     * @param permissionToRequest = string with the permission to make the request
     * @param REQUEST_PERMISSION_CODE = a integer number that act like an id to the current request
     */
    public static void makePermissionRequest(Activity activity, String permissionToRequest, int REQUEST_PERMISSION_CODE) {
        ActivityCompat.requestPermissions(activity, new String[]{permissionToRequest}, REQUEST_PERMISSION_CODE);
    }
}
