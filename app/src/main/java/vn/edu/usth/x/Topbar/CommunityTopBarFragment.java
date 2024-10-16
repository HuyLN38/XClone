package vn.edu.usth.x.Topbar;

import android.content.Context;
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
import vn.edu.usth.x.R;
import vn.edu.usth.x.Utils.UserFunction;

public class CommunityTopBarFragment extends Fragment {
    private DrawerLayout drawerLayout;
    private static final String PREFS_NAME = "UserPrefs";
    private static final String USER_ID_KEY = "userId";
    private ImageView avatar;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if (context instanceof HomeFragment) {
            drawerLayout = ((HomeFragment) context).getDrawerLayout();
        } else {
            drawerLayout = null;
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.topbar_community, container, false);

        avatar = view.findViewById(R.id.avatar);
        avatar.setOnClickListener(v -> {
            if (drawerLayout != null) {
                drawerLayout.openDrawer(GravityCompat.START);
            }
        });

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

        return view;
    }
}