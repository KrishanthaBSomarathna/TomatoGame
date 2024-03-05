package com.example.tomatogame.fragment;

import android.view.FrameStats;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

public class FragmentViewAdapter extends FragmentStateAdapter {
    public FragmentViewAdapter(@NonNull FragmentActivity fragmentActivity) {
        super(fragmentActivity);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        switch (position){
            case 1:
                return new SaveProgress();
            default:
                return new PlayInstruction();

        }
    }

    @Override
    public int getItemCount() {
        return 2;
    }
}
