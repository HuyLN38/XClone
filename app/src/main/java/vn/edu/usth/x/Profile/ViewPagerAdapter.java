package vn.edu.usth.x.Profile;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

public class ViewPagerAdapter extends FragmentStateAdapter {

    public ViewPagerAdapter(@NonNull FragmentActivity fragmentActivity) {
        super(fragmentActivity);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        // Trả về fragment tương ứng với từng tab
        switch (position) {
            case 0:
                return new PostsFragment();
            case 1:
                return new RepliesFragment();
            case 2:
                return new HighlightsFragment();
            case 3:
                return new ArticlesFragment();
            default:
                return new MediaFragment();
        }
    }

    @Override
    public int getItemCount() {
        // Trả về số lượng các tab
        return 5;
    }
}

