package vn.edu.usth.x.Topbar;

import android.os.Bundle;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import androidx.fragment.app.FragmentTransaction;

import vn.edu.usth.x.InboxPage.SettingsInboxFragment;
import vn.edu.usth.x.R;

public class InboxTopBar extends Fragment {

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

        return view;
    }
}