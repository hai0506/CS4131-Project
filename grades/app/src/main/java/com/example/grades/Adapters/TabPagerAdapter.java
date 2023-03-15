package com.example.grades.Adapters;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import com.example.grades.Fragments.DiscoverFragment;
import com.example.grades.Fragments.SavedFragment;

public class TabPagerAdapter extends FragmentPagerAdapter {
    int tabCount;

    public TabPagerAdapter(@NonNull FragmentManager fm, int numberOfTabs) {
        super(fm, FragmentPagerAdapter.BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT);
        this.tabCount= numberOfTabs;
    }
    @NonNull
    @Override
    public Fragment getItem(int position) {
        switch (position){
            case 0: return new SavedFragment();
            case 1: return new DiscoverFragment();
            default: return null;
        }

    }

    @Override
    public int getCount() {
        return tabCount;
    }
}
