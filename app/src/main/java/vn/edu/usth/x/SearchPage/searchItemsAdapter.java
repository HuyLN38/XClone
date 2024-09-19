package vn.edu.usth.x.SearchPage;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomsheet.BottomSheetDialog;

import java.util.List;

import vn.edu.usth.x.R;

public class searchItemsAdapter extends RecyclerView.Adapter<searchItemHolder> {
    Context context;
    List<searchItem> items;

    public searchItemsAdapter(Context context, List<searchItem> items) {
        this.context = context;
        this.items = items;
    }

    @NonNull
    @Override
    public searchItemHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.search_item, parent, false);

        ImageView threeDotBtn;

        threeDotBtn = itemView.findViewById(R.id.searchThreedot);
        threeDotBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clickOpenBottomSheetDiaglog();
            }

            private void clickOpenBottomSheetDiaglog() {
                View v = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.search_bottom_sheet, parent, false);

                BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(v.getContext());
                bottomSheetDialog.setContentView(v);
                bottomSheetDialog.show();
            }
        });
        return new searchItemHolder(itemView);
    }




    @Override
    public void onBindViewHolder(@NonNull searchItemHolder holder, int position) {
        holder.content.setText(items.get(position).getContent());
        holder.topic.setText(items.get(position).getTopic());
        holder.posts.setText(items.get(position).getPost());
    }

    @Override
    public int getItemCount() {
        return items.size();
    }
}
