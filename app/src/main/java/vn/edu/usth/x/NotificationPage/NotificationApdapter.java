package vn.edu.usth.x.NotificationPage;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import vn.edu.usth.x.HomePage.HomeFollowing;
import vn.edu.usth.x.HomePage.HomeForYou;

public class NotificationApdapter extends FragmentStateAdapter {

    public NotificationApdapter(@NonNull FragmentActivity fragmentActivity) {
        super(fragmentActivity);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        switch (position){
            case 0: return new NotificationAll();
            case 1: return new NotificationVerified();
            case 2: return new NotificationMentions();

            default: return new NotificationAll();
        }
    }

    @Override
    public int getItemCount() {
        return 3;
    }
}
