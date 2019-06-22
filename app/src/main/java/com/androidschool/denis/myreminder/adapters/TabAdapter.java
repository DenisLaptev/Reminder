package com.androidschool.denis.myreminder.adapters;

import android.app.Fragment;
import android.app.FragmentManager;
import android.support.v13.app.FragmentStatePagerAdapter;

import com.androidschool.denis.myreminder.fragments.CurrentTaskFragment;
import com.androidschool.denis.myreminder.fragments.DoneTaskFragment;

public class TabAdapter extends FragmentStatePagerAdapter {
    //Адаптер - это посредник между данными и их представлением.

    private int numberOfTabs;

    /**
     * @param fm
     * @deprecated
     */
    public TabAdapter(FragmentManager fm, int numberOfTabs) {
        //FragmentManager управляет фрагментами на вкладках.
        super(fm);
        this.numberOfTabs = numberOfTabs;
    }

    @Override
    public Fragment getItem(int i) {

        switch (i) {
            case 0:
                return new CurrentTaskFragment();

            case 1:
                return new DoneTaskFragment();

            default:
                return null;
        }
    }


    //Метод возвращает количество вкладок.
    @Override
    public int getCount() {
        return numberOfTabs;
    }
}
