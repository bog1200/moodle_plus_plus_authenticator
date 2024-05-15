package app.romail.mpp_auth;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.WindowManager;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.auth0.android.jwt.JWT;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE, WindowManager.LayoutParams.FLAG_SECURE);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        Intent loginIntent = new Intent(this, LoginActivity.class);


        SharedPreferences sharedPreferences = this.getSharedPreferences("MPP_AUTH", MODE_PRIVATE);
        if (!sharedPreferences.contains("accessToken") || !sharedPreferences.contains("refreshToken")) {
            startActivity(loginIntent);
            return;
        }
        JWT accessToken = new JWT(sharedPreferences.getString("accessToken", ""));
        if (accessToken.isExpired(0)) {
            boolean ref = HttpRequest.refreshToken(this);
            if (!ref) {
                Toast.makeText(this, "Login expired. Please login again", Toast.LENGTH_LONG).show();
                startActivity(loginIntent);
                return;
            }
            else {
                accessToken = new JWT(sharedPreferences.getString("accessToken", ""));
            }
        }
        Toast.makeText(this, "Welcome " + accessToken.getSubject(), Toast.LENGTH_LONG).show();






    }

}
