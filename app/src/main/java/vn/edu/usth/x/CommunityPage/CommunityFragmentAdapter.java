package vn.edu.usth.x.CommunityPage;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

public class CommunityFragmentAdapter extends FragmentStateAdapter {

    public CommunityFragmentAdapter(@NonNull FragmentActivity fragmentActivity) {
        super(fragmentActivity);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        switch (position){
            case 0: return new HomeFragment();
            case 1: return new ExploreFragment();
            default: return new CommunityFragment();
        }
    }

    @Override
    public int getItemCount() {
        return 2;
    }
}