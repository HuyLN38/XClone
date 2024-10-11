package vn.edu.usth.x.InboxPage;

import android.os.Bundle;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import vn.edu.usth.x.R;

public class MessageActivity extends AppCompatActivity {

    private TextView textView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message);

        textView = findViewById(R.id.textMessageView);

        // Get the user name and ID from intent
        String userName = getIntent().getStringExtra("USER_NAME");
        String userId = getIntent().getStringExtra("USER_ID");

        if (userName != null) {
            textView.setText("Messaging: " + userName);
        }

        // Use userId to implement the actual messaging logic here
    }
}