package uz.mq.mybaby;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.DisplayMetrics;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Collections;

public class Utils {
    /**
     * This method converts dp unit to equivalent pixels, depending on device density.
     *
     * @param dp A value in dp (density independent pixels) unit. Which we need to convert into pixels
     * @param context Context to get resources and device specific display metrics
     * @return A float value to represent px equivalent to dp depending on device density
     */
    public static int convertDpToPixel(float dp, Context context){
        return Math.round(dp * ((float) context.getResources().getDisplayMetrics().densityDpi / DisplayMetrics.DENSITY_DEFAULT));
    }

    /**
     * This method converts device specific pixels to density independent pixels.
     *
     * @param px A value in px (pixels) unit. Which we need to convert into db
     * @param context Context to get resources and device specific display metrics
     * @return A float value to represent dp equivalent to px value
     */
    public static int convertPixelsToDp(float px, Context context){
        return Math.round(px / ((float) context.getResources().getDisplayMetrics().densityDpi / DisplayMetrics.DENSITY_DEFAULT));
    }

    static final String AB = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";
    static SecureRandom rnd = new SecureRandom();

    static String randomString(int len){
        StringBuilder sb = new StringBuilder(len);
        for(int i = 0; i < len; i++)
            sb.append(AB.charAt(rnd.nextInt(AB.length())));
        return sb.toString();
    }

    static void saveProfile(Context context, BabyProfile profile){
        SharedPreferences sharedPreferences = context.getSharedPreferences("Profile", Context.MODE_PRIVATE);
        sharedPreferences.edit().putInt("sex", profile.getSex()).apply();
        sharedPreferences.edit().putInt("age", profile.getAge()).apply();
    }

    static BabyProfile getProfile(Context context){
        SharedPreferences preferences = context.getSharedPreferences("Profile", Context.MODE_PRIVATE);
        return new BabyProfile(preferences.getInt("age", 0), preferences.getInt("sex", 0));
    }

    static void addToHistory(Context ctx, HistoryModel item){
        ArrayList<HistoryModel> historyModels;
        Gson gson = new Gson();
        SharedPreferences sharedPreference = ctx.getSharedPreferences("History", Context.MODE_PRIVATE);
        String cash_str = sharedPreference.getString("History", "empty");
        if (cash_str.equals("empty")){
            historyModels = new ArrayList<>();
            historyModels.add(item);
        }else{
            Type typeOfObjectsList = new TypeToken<ArrayList<HistoryModel>>() {}.getType();
            historyModels = gson.fromJson(cash_str, typeOfObjectsList);
            historyModels.add(item);
        }
        sharedPreference.edit().putString("History", gson.toJson(historyModels)).apply();
    }

    public static void clearHistory(Context ctx){
        SharedPreferences sharedPreference = ctx.getSharedPreferences("History", Context.MODE_PRIVATE);
        sharedPreference.edit().putString("History", "empty").apply();
    }

    public static ArrayList<HistoryModel> getHistory(Context ctx){
        ArrayList<HistoryModel> historyModels = new ArrayList<>();
        Gson gson = new Gson();
        SharedPreferences sharedPreference = ctx.getSharedPreferences("History", Context.MODE_PRIVATE);
        String cash_str = sharedPreference.getString("History", "empty");
        if (!cash_str.equals("empty")){
            Type typeOfObjectsList = new TypeToken<ArrayList<HistoryModel>>() {}.getType();
            historyModels = gson.fromJson(cash_str, typeOfObjectsList);
            Collections.reverse(historyModels);
            return historyModels;
        }else{
            return historyModels;
        }
    }

}
