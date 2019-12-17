package io.ibnus.mrdoctor.Common;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

/**
 * Created by papu on 4/12/2018.
 */

public class Common
{
   // public static User currentuser;

    public static final String USER_KEY ="User";
    public static final String DOCTOR_KEY = "Doctor";
    public static final String USER_PASS = "Pass";
    public static final String DOCTOR_PASS = "Dpass";


    public static boolean isConnectedToInternet(Context context)
    {
        ConnectivityManager connectivityManager = (ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);

        if (connectivityManager !=null)
        {
            NetworkInfo[] info = connectivityManager.getAllNetworkInfo();
            if (info != null)
            {
                for (int i = 0; i < info.length;i++)
                {
                    if (info[i].getState() == NetworkInfo.State.CONNECTED)
                        return true;
                }
            }
        }
        return false;
    }


}
