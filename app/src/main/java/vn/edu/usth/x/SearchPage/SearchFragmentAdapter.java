package vn.edu.usth.x.SearchPage;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

public class SearchFragmentAdapter extends FragmentStateAdapter {

    public SearchFragmentAdapter(@NonNull FragmentActivity fragmentActivity) {
        super(fragmentActivity);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        switch (position){
            case 0: return new PageForYou();
            case 1: return new PageTrending();
            case 2: return new PageNews();
            case 3: return new PageSports();
            case 4: return new PageEntertainment();
            default: return new PageForYou();
        }
    }

    @Override
    public int getItemCount() {
        return 5;
    }
}