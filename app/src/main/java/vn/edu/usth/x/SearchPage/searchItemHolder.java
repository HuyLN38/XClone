package vn.edu.usth.x.SearchPage;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import vn.edu.usth.x.R;


public class searchItemHolder extends RecyclerView.ViewHolder{
    TextView topic;
    TextView content;
    TextView posts;
    ImageView threedot;

    public searchItemHolder(@NonNull View itemView) {
        super(itemView);
        topic = itemView.findViewById(R.id.searchTopic);
        content = itemView.findViewById(R.id.searchContent);
        posts = itemView.findViewById(R.id.searchPosts);
        threedot = itemView.findViewById(R.id.searchThreedot);
    }


}
