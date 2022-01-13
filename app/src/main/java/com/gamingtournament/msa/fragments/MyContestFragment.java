package com.gamingtournament.msa.fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.fragment.app.FragmentStatePagerAdapter;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;
import androidx.viewpager2.widget.ViewPager2;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.gamingtournament.msa.R;
import com.google.android.material.tabs.TabLayout;

public class MyContestFragment extends Fragment {

    private TabLayout tabLayout;
    private ViewPager viewPager;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_mycontest, container, false);

        tabLayout = view.findViewById(R.id.tabLayout);
        viewPager = view.findViewById(R.id.viewPager2);


        tabLayout.addTab(tabLayout.newTab().setText("BGMI"));
        tabLayout.addTab(tabLayout.newTab().setText("Pubg Lite"));
        tabLayout.addTab(tabLayout.newTab().setText("Free Fire"));
        tabLayout.addTab(tabLayout.newTab().setText("COC"));

        viewPager.setAdapter(new PagerAdapter(getChildFragmentManager(),tabLayout.getTabCount()));
        viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
        tabLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                viewPager.setCurrentItem(tab.getPosition());

            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

        return view;
    }

    public class PagerAdapter extends FragmentStatePagerAdapter {
        int mNumofTabs;

        public PagerAdapter(@NonNull FragmentManager fm, int NumofTabs) {
            super(fm);
            this.mNumofTabs = NumofTabs;
        }


        @NonNull
        @Override
        public Fragment getItem(int position) {

            switch (position){

                case 1:
                    return new PubgliteFragment();
                case 2:
                    return new FreeFireFragment();
                case 3:
                    return new ClashOfClanFragment();
            }

            return new PubgFragment();
        }

        @Override
        public int getCount() {
            return mNumofTabs;
        }
    }

}



















