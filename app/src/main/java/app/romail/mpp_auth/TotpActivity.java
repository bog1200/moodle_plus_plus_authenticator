package app.romail.mpp_auth;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.util.Log;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONObject;

import java.util.concurrent.ExecutorService;

import dev.samstevens.totp.code.CodeGenerator;
import dev.samstevens.totp.code.DefaultCodeGenerator;
import dev.samstevens.totp.time.SystemTimeProvider;
import dev.samstevens.totp.time.TimeProvider;

public class TotpActivity extends AppCompatActivity {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_totp);
        Log.d("TotpActivity", "onCreate: ");
        Long userId = HttpRequest.getAccountFromToken(this);

        JSONObject account = HttpRequest.GetRequest(this, "https://mpp.romail.app/api/v1/account/"+userId);
        TextView name = findViewById(R.id.totpName);
        TextView totp = findViewById(R.id.totpCode);
        ProgressBar progressBar = findViewById(R.id.totpProgressBar);

        JSONObject totpObject = HttpRequest.GetRequest(this, "https://mpp.romail.app/api/v1/account/me/accountSecret");
        try {
            name.setText(account.getString("username"));
           // surname.setText(account.getString("surname"));
            CodeGenerator generator = new DefaultCodeGenerator();
            TimeProvider timeProvider = new SystemTimeProvider();
            ExecutorService executor = java.util.concurrent.Executors.newSingleThreadExecutor();
            executor.submit(() -> {
                try {
                    while (true) {
                        Thread.sleep(1000);
                        runOnUiThread(() -> {
                            try {
                                String actualCode = generator.generate(totpObject.getString("secret"),  Math.floorDiv(timeProvider.getTime(), 30));
                                totp.setText(actualCode);
                                progressBar.setProgress(100 - (int)  (timeProvider.getTime() % 30) * 100 / 30);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        });
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                    });
        } catch (Exception e) {
            e.printStackTrace();
        }



    }
}