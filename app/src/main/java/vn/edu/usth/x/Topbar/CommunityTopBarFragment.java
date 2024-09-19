package vn.edu.usth.x.Topbar;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;

import vn.edu.usth.x.HomeFragment;
import vn.edu.usth.x.NotificationPage.NotificationSettings;
import vn.edu.usth.x.R;

public class CommunityTopBarFragment extends Fragment {
    private DrawerLayout drawerLayout;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if (context instanceof HomeFragment) {
            drawerLayout = ((HomeFragment) context).getDrawerLayout();
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.topbar_community, container, false);

        ImageView avatar = view.findViewById(R.id.avatar);
        avatar.setOnClickListener(v -> {
            if (drawerLayout != null) {
                drawerLayout.openDrawer(GravityCompat.START);
            }
        });

        try {
            @SuppressLint({"MissingInflatedId", "LocalSuppress"})
            ImageView settingsNotification = view.findViewById(R.id.settings_notification);
            settingsNotification.setOnClickListener(v -> {
                Intent intent = new Intent(getActivity(), NotificationSettings.class);
                startActivity(intent);
            });
        } catch (Exception ignored) {

        }


        return view;
    }
}