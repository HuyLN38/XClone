package vn.edu.usth.x.community;

import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import vn.edu.usth.x.R;

public class CommunityFragment extends Fragment {
    private Button btnHome,btnExplore;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_community, container, false);

        btnHome = view.findViewById(R.id.btnhome);
        btnExplore = view.findViewById(R.id.btnexplore);

        loadFragment(new ExploreFragment());

        btnHome.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                loadFragment(new Cm_HomeFragment());
            }
        });

        btnExplore.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                loadFragment(new ExploreFragment());
            }
        });

        return view;
    }

    private void loadFragment(Fragment fragment) {
        FragmentManager fragmentManager = getParentFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.cm_parentfragment, fragment);
        fragmentTransaction.commit(); // Commit the transaction
    }
}
