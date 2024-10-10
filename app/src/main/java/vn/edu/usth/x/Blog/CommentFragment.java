package vn.edu.usth.x.Blog;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.bumptech.glide.Glide;

import vn.edu.usth.x.R;
import vn.edu.usth.x.Utils.UserFunction;

public class CommentFragment extends Fragment {

    private ImageView avatarImageView;
    private TextView usernameTextView;

    private TextView tweetLinkTextView;
    private TextView responseUsernameView;


    private TextView tweetTextView;

    private TextView timeTextView;
    private ImageView tweetImageView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_comment, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Khởi tạo các view
        avatarImageView = view.findViewById(R.id.tweet_avatar);
        usernameTextView = view.findViewById(R.id.tweet_name);
        tweetLinkTextView = view.findViewById(R.id.tweet_username);
        responseUsernameView = view.findViewById(R.id.response_username);
        tweetTextView = view.findViewById(R.id.tweet_text);
        timeTextView = view.findViewById(R.id.tweet_time);
        tweetImageView = view.findViewById(R.id.tweet_image);

        Button cancelButton = view.findViewById(R.id.comment_cancel);
        Button postButton = view.findViewById(R.id.comment_post);

        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentManager fragmentManager = requireActivity().getSupportFragmentManager();
                fragmentManager.popBackStack();
            }
        });

        postButton.setOnClickListener(v -> {
            // Handle post action
        });

        //set avatar of current user
        ImageView avatar = view.findViewById(R.id.user_avatar);

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
                    Log.e("Error Avatar: ", errorMessage);
                }
            });
        }

        // Lấy và hiển thị dữ liệu từ Bundle
        Bundle args = getArguments();
        if (args != null) {
            String username = args.getString("username");
            String tweetLink = args.getString("tweetLink");
            String tweetText = args.getString("tweetText");
            String time = args.getString("time");

            usernameTextView.setText(username);
            tweetLinkTextView.setText(tweetLink);
            tweetTextView.setText(tweetText);
            timeTextView.setText(time);
            responseUsernameView.setText(tweetLink);

            byte[] avatarByteArray = args.getByteArray("avatar");
            if (avatarByteArray != null) {
                Bitmap avatarBitmap = BitmapFactory.decodeByteArray(avatarByteArray, 0, avatarByteArray.length);
                avatarImageView.setImageBitmap(avatarBitmap);
            }

            byte[] imageByteArray = args.getByteArray("tweetImage");
            if (imageByteArray != null) {
                Bitmap imageBitmap = BitmapFactory.decodeByteArray(imageByteArray, 0, imageByteArray.length);
                tweetImageView.setImageBitmap(imageBitmap);
                tweetImageView.setVisibility(View.VISIBLE);
            } else {
                tweetImageView.setVisibility(View.GONE);
            }
        }
    }
}