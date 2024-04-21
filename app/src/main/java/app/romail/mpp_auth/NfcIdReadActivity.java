package app.romail.mpp_auth;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
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
        imageView.setImageBitmap(image);
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

       // TextView imageView = findViewById(R.id.output_image);


    }



}
