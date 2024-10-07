package vn.edu.usth.x.Topbar;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import androidx.fragment.app.FragmentTransaction;

import com.bumptech.glide.Glide;

import vn.edu.usth.x.HomeFragment;
import vn.edu.usth.x.InboxPage.SettingsInboxFragment;
import vn.edu.usth.x.R;
import vn.edu.usth.x.Utils.UserFunction;

public class InboxTopBar extends Fragment {

    private DrawerLayout drawerLayout;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if (context instanceof HomeFragment) {
            drawerLayout = ((HomeFragment) context).getDrawerLayout();
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.topbar_inbox, container, false);

        // Set up the click listener and fragment transaction
        ImageView settingsInbox = view.findViewById(R.id.settings_inbox);
        settingsInbox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Fragment settings = new SettingsInboxFragment(); // Replace with your fragment class
                FragmentTransaction transaction = getParentFragmentManager().beginTransaction();
                transaction.setCustomAnimations(R.anim.enter_from_right, R.anim.exit_to_left);
                transaction.replace(R.id.drawer_layout, settings);
                transaction.addToBackStack(null);
                transaction.commit();
            }
        });


        ImageView avatar = view.findViewById(R.id.avatar);

        Context context = getContext();
        if (context != null) {
            UserFunction.getAvatar(context, new UserFunction.AvatarCallback() {
                @Override
                public void onSuccess(Bitmap avatarBitmap) {
                    Glide.with(context)
                            .load(avatarBitmap)
                            .into(avatar);
                }

                @Override
                public void onFailure(String errorMessage) {
                    Log.e("SearchTopBarFragment", errorMessage);
                }
            });
        }
        avatar.setOnClickListener(v -> {
            if (drawerLayout != null) {
                drawerLayout.openDrawer(GravityCompat.START);
            }
        });

        return view;
    }
}