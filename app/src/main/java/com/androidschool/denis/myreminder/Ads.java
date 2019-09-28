package com.androidschool.denis.myreminder;

import android.app.Activity;
import android.view.View;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;


public class Ads {

    public static void showBottomBanner(final Activity activity) {

        final AdView banner = (AdView) activity.findViewById(R.id.banner);
        AdRequest adRequest = new AdRequest.Builder().build();
        banner.loadAd(adRequest);

        //Если нет интернета, и баннер не загрузился, то активити будет занимать весь экран
        banner.setAdListener(new AdListener() {

            @Override
            public void onAdLoaded() {
                super.onAdLoaded();
                setupContentViewPadding(activity, banner.getHeight());
            }
        });
    }

    //метод подвигает нижний край эрана на высоту = высоте баннера
    public static void setupContentViewPadding(Activity activity, int margin) {
        View view = activity.findViewById(R.id.coordinator);
        view.setPadding(view.getPaddingLeft(), view.getPaddingTop(), view.getPaddingRight(), margin);
    }
}
