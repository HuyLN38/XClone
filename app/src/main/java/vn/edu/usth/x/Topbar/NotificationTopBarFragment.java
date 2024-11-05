package vn.edu.usth.x.Topbar;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;

import vn.edu.usth.x.HomeFragment;
import vn.edu.usth.x.Login.Data.AvatarManager;
import vn.edu.usth.x.Login.Data.UserManager;
import vn.edu.usth.x.NotificationPage.NotificationSettings;
import vn.edu.usth.x.R;
import vn.edu.usth.x.Utils.UserFunction;

public class NotificationTopBarFragment extends Fragment {
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
        View view = inflater.inflate(R.layout.topbar_notification, container, false);

        ImageView avatar = view.findViewById(R.id.avatar);
        avatar.setOnClickListener(v -> {
            if (drawerLayout != null) {
                drawerLayout.openDrawer(GravityCompat.START);
            }
        });

        Context context = getContext();
        if (context != null) {
            AvatarManager.getInstance(context)
                    .getAvatar(UserFunction.getUserId(context))
                    .thenAccept(bitmap -> {
                        if (bitmap != null) {Glide.with(context)
                                .load(bitmap)
                                .into(avatar);
                        } else {
                            Glide.with(context)
                                    .load(R.drawable.avatar3)
                                    .into(avatar);
                        }
                    });
            }
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