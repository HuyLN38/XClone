package vn.edu.usth.x.InboxPage;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import vn.edu.usth.x.R;

public class SettingsInboxFragment extends Fragment {
    private CheckBox checkBox1, checkBox2, checkBox3;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_settings_inbox, container, false);

        // Initialize checkboxes
        checkBox1 = view.findViewById(R.id.no_one);
        checkBox2 = view.findViewById(R.id.verified);
        checkBox3 = view.findViewById(R.id.everyone);

        // Set up listeners
        checkBox1.setOnCheckedChangeListener(onCheckedChangeListener);
        checkBox2.setOnCheckedChangeListener(onCheckedChangeListener);
        checkBox3.setOnCheckedChangeListener(onCheckedChangeListener);

        // Replace FrameLayout with InboxSettingsTopBarFragment
        FragmentTransaction transaction = getChildFragmentManager().beginTransaction();
        transaction.replace(R.id.home_toolbar, new InboxSettingsTopBarFragment());
        transaction.commit();

        return view;
    }

    private final CompoundButton.OnCheckedChangeListener onCheckedChangeListener = new CompoundButton.OnCheckedChangeListener() {
        @Override
        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
            if (isChecked) {
                if (buttonView == checkBox1) {
                    checkBox2.setChecked(false);
                    checkBox3.setChecked(false);
                } else if (buttonView == checkBox2) {
                    checkBox1.setChecked(false);
                    checkBox3.setChecked(false);
                } else if (buttonView == checkBox3) {
                    checkBox1.setChecked(false);
                    checkBox2.setChecked(false);
                }
            }
        }
    };

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        FragmentTransaction transaction = requireActivity().getSupportFragmentManager().beginTransaction();
        transaction.setCustomAnimations(R.anim.enter_from_left, R.anim.exit_to_right);
        transaction.commit();
    }
}