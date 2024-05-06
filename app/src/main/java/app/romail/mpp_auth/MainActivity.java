package app.romail.mpp_auth;

import android.content.Intent;
import android.os.Bundle;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.datepicker.CompositeDateValidator;
import com.google.android.material.datepicker.DateValidatorPointBackward;
import com.google.android.material.datepicker.DateValidatorPointForward;
import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.android.material.datepicker.CalendarConstraints;

import org.bouncycastle.jce.provider.BouncyCastleProvider;

import java.security.Security;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE, WindowManager.LayoutParams.FLAG_SECURE);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_nfc_read);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main_layout), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;

        });


        EditText birthDate = findViewById(R.id.input_date_of_birth);
        EditText expiryDate = findViewById(R.id.input_expiration_date);
        EditText idNumber = findViewById(R.id.input_passport_number);
        CalendarConstraints.Builder constraintsBuilderRange = new CalendarConstraints.Builder();
        MaterialDatePicker.Builder<Long> builder = MaterialDatePicker.Builder.datePicker();
        builder.setTitleText("Select Date of Birth");
        // limit the date range from 1900 to today
        //builder.setSelection(MaterialDatePicker.todayInUtcMilliseconds());
        CalendarConstraints.DateValidator birthDateValidatorMin = DateValidatorPointForward.from(0);
        CalendarConstraints.DateValidator birthDateValidatorMax = DateValidatorPointBackward.before(MaterialDatePicker.todayInUtcMilliseconds());

        ArrayList<CalendarConstraints.DateValidator> listValidators =
                new ArrayList<>();
        listValidators.add(birthDateValidatorMin);
        listValidators.add(birthDateValidatorMax);

        CalendarConstraints.DateValidator validators = CompositeDateValidator.allOf(listValidators);
        constraintsBuilderRange.setValidator(validators);

        builder.setCalendarConstraints(constraintsBuilderRange.build());


        MaterialDatePicker<Long> picker = builder.build();
        birthDate.setOnClickListener(v -> picker.show(getSupportFragmentManager(), picker.toString()));
        picker.addOnPositiveButtonClickListener(selection -> birthDate.setText(picker.getHeaderText()));

        MaterialDatePicker.Builder<Long> builder2 = MaterialDatePicker.Builder.datePicker();
        builder2.setTitleText("Select Expiration Date");
        // limit the date range from today to 10 years from now

        CalendarConstraints.Builder constraintsBuilderRange2 = new CalendarConstraints.Builder();

        //builder2.setSelection(MaterialDatePicker.todayInUtcMilliseconds());
        CalendarConstraints.DateValidator expiryDateValidatorMin = DateValidatorPointForward.from(MaterialDatePicker.todayInUtcMilliseconds());
        CalendarConstraints.DateValidator expiryDateValidatorMax = DateValidatorPointBackward.before(MaterialDatePicker.todayInUtcMilliseconds() + 315569520000L);

        ArrayList<CalendarConstraints.DateValidator> listValidators2 =
                new ArrayList<>();
        listValidators2.add(expiryDateValidatorMin);
        listValidators2.add(expiryDateValidatorMax);
        CalendarConstraints.DateValidator validators2 = CompositeDateValidator.allOf(listValidators2);
        constraintsBuilderRange2.setValidator(validators2);

        builder2.setCalendarConstraints(constraintsBuilderRange2.build());

        MaterialDatePicker<Long> picker2 = builder2.build();
        expiryDate.setOnClickListener(v -> picker2.show(getSupportFragmentManager(), picker2.toString()));
        picker2.addOnPositiveButtonClickListener(selection -> expiryDate.setText(picker2.getHeaderText()));

        Button nfcButton = findViewById(R.id.button2);
        nfcButton.setOnClickListener(view -> {
            if (birthDate.getText().toString().isEmpty() || expiryDate.getText().toString().isEmpty() || idNumber.getText().toString().isEmpty()) {
                Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_LONG).show();
            } else {
                Intent intent = new Intent(this, NfcActivity.class);
                intent.putExtra("birthDate", birthDate.getText().toString());
                intent.putExtra("expiryDate", expiryDate.getText().toString());
                intent.putExtra("idNumber", idNumber.getText().toString());
                startActivity(intent);
                }
        });

        Button demoButton = findViewById(R.id.button3);
        demoButton.setOnClickListener(view -> {
            Intent dIntent = new Intent(this, NfcIdReadActivity.class);
            dIntent.putExtra(NfcIdReadActivity.KEY_FIRST_NAME, "John");
            dIntent.putExtra(NfcIdReadActivity.KEY_LAST_NAME, "Doe");
            dIntent.putExtra(NfcIdReadActivity.KEY_DATE_OF_BIRTH, "900101");
            dIntent.putExtra(NfcIdReadActivity.KEY_EXP, "251231");
            dIntent.putExtra(NfcIdReadActivity.KEY_DOCUMENT_NUMBER, "123456");
            dIntent.putExtra(NfcIdReadActivity.KEY_COUNTRY, "ROU");
            dIntent.putExtra(NfcIdReadActivity.KEY_PIN, "1234567890");
            startActivity(dIntent);
        });

        BouncyCastleProvider provider = new BouncyCastleProvider();
        Security.insertProviderAt(provider,1);
     }
}
