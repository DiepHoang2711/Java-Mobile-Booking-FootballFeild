package com.example.football_field_booking.adapters;

import android.os.Bundle;
import android.widget.Switch;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.Lifecycle;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.example.football_field_booking.fragments.ListBookingOfAFieldFragment;
import com.example.football_field_booking.fragments.ListRateAndCommentForOwnerFragment;

public class TabLayoutFragmentAdapter extends FragmentStateAdapter {

    private Bundle bundle;

    public Bundle getBundle() {
        return bundle;
    }

    public void setBundle(Bundle bundle) {
        this.bundle = bundle;
    }

    public TabLayoutFragmentAdapter(@NonNull FragmentManager fragmentManager, @NonNull Lifecycle lifecycle) {
        super(fragmentManager, lifecycle);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        ListBookingOfAFieldFragment listBookingOfAFieldFragment = new ListBookingOfAFieldFragment();
        listBookingOfAFieldFragment.setArguments(bundle);
        switch (position) {
            case 0:
                return listBookingOfAFieldFragment;
            case 1:
                ListRateAndCommentForOwnerFragment listRateAndCommentForOwnerFragment = new ListRateAndCommentForOwnerFragment();
                listRateAndCommentForOwnerFragment.setArguments(bundle);
                return listRateAndCommentForOwnerFragment;
        }
        return listBookingOfAFieldFragment;
    }

    @Override
    public int getItemCount() {
        return 2;
    }
}
