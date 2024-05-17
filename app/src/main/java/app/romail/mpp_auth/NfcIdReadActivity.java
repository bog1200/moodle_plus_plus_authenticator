package app.romail.mpp_auth;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONObject;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class NfcIdReadActivity extends AppCompatActivity {
    public static final String KEY_FIRST_NAME = "first_name";
    public static final String KEY_LAST_NAME = "last_name";
    public static final String KEY_DATE_OF_BIRTH = "date_of_birth";
    public static final String KEY_EXP = "exp";
    public static final String KEY_DOCUMENT_NUMBER = "id_number";

    public static final String KEY_COUNTRY = "country";

    public static final String KEY_PIN = "pin";
    public static final String KEY_IMAGE = "image";


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE, WindowManager.LayoutParams.FLAG_SECURE);
        //OnBackPressedDispatcher onBackPressedDispatcher = getOnBackPressedDispatcher();
        setContentView(R.layout.activity_nfc_results);
        Intent intent = getIntent();
        String firstName = intent.getStringExtra(KEY_FIRST_NAME);
        String lastName = intent.getStringExtra(KEY_LAST_NAME);
        String dateOfBirth = intent.getStringExtra(KEY_DATE_OF_BIRTH);
        String exp = intent.getStringExtra(KEY_EXP);
        String idNumber = intent.getStringExtra(KEY_DOCUMENT_NUMBER);
        String country = intent.getStringExtra(KEY_COUNTRY);
        String pin = intent.getStringExtra(KEY_PIN);
        Bitmap image = intent.getParcelableExtra(KEY_IMAGE);



        TextView firstNameView = findViewById(R.id.output_first_name);
        firstNameView.setText(firstName);
        TextView lastNameView = findViewById(R.id.output_last_name);
        lastNameView.setText(lastName);
//        TextView expView = findViewById(R.id.output_expiration_date);
//        TextView idNumberView = findViewById(R.id.output_passport_number);
        TextView countryView = findViewById(R.id.output_nationality);
        countryView.setText(country);
        TextView pinView = findViewById(R.id.output_personal_code);
        pinView.setText(pin);
        ImageView imageView = findViewById(R.id.view_photo);
        if (image != null) imageView.setImageBitmap(image);
        else imageView.setImageResource(R.drawable.photo);
        TextView expView = findViewById(R.id.output_expiry_date);
        SimpleDateFormat IdFormat = new SimpleDateFormat("yyMMdd");
        DateFormat outputFormat = DateFormat.getDateInstance();
        String expDateString;
        Date expDate = null;
        try {
            expDate = IdFormat.parse(exp);
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
        expView.setText(outputFormat.format(expDate));

        // if device supports host card emulation, start HCE service
        PackageManager packageManager = this.getPackageManager();
        if (packageManager.hasSystemFeature(PackageManager.FEATURE_NFC_HOST_CARD_EMULATION)) {
            Intent hceIntent = new Intent(this, HCEService.class);
            assert country != null;
            hceIntent.putExtra("loginid", country.concat(idNumber));
            startService(hceIntent);
        }
        else {
            Toast.makeText(this, "This device does not support NFC Host Card Emulation.", Toast.LENGTH_LONG).show();
        }
        Button loginButton = findViewById(R.id.loginButton);
        loginButton.setOnClickListener(view -> {
            boolean loggedIn = HttpRequest.IdAuthRequest(this, country, pin);
            if (loggedIn){
                Long account = HttpRequest.getAccountFromToken(this);



                JSONObject accountGET = HttpRequest.GetRequest(this,"/account/".concat(String.valueOf(account)));
                Log.d("AccountAPI", accountGET.toString());
                Intent accountIntent = new Intent(this, MainActivity.class);
                startActivity(accountIntent);

            }
            else {
                Toast.makeText(this, "No account found for this ID.", Toast.LENGTH_LONG).show();
            }
        });
    }

    @SuppressLint("MissingSuperCall")
    @Override
    public void onBackPressed() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Do you want to cancel the login?");
        builder.setCancelable(false);
        builder.setPositiveButton("Yes", (dialog, which) -> {
            //exit
            finish();
        });
        builder.setNegativeButton("No", (dialog, which) -> {
            dialog.cancel();
        });
    }



}
