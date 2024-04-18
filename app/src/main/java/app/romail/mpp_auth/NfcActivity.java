package app.romail.mpp_auth;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.IntentFilter;
import android.nfc.NdefMessage;
import android.nfc.NfcAdapter;
import android.nfc.NdefRecord;
import android.nfc.Tag;
import android.nfc.tech.TagTechnology;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.Log;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.HexFormat;

public class NfcActivity extends AppCompatActivity {

    private NfcAdapter nfcAdapter;
    private TextView textView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE, WindowManager.LayoutParams.FLAG_SECURE);

        nfcAdapter = NfcAdapter.getDefaultAdapter(this);

        if (nfcAdapter == null) {
            Toast.makeText(this, "NFC is not available on this device", Toast.LENGTH_LONG).show();
            finish();
            return;
        }
        else {
            setContentView(R.layout.activity_nfc);
            textView = findViewById(R.id.textView);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        nfcAdapter = NfcAdapter.getDefaultAdapter(this);
        Log.e("NFC", "onResume");
        if (nfcAdapter != null) {
            Log.e("NFC", "enableForegroundDispatch");
            PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, new Intent(this, getClass()).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP), PendingIntent.FLAG_MUTABLE);
            IntentFilter[] intentFilters = new IntentFilter[]{};
            String[][] filter = {{"android.nfc.tech.NdefFormatable"}, {"android.nfc.tech.Ndef"}};
            nfcAdapter.enableForegroundDispatch(this, pendingIntent, null, filter );
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        nfcAdapter = NfcAdapter.getDefaultAdapter(this);
        Log.e("NFC", "onPause");
        if (nfcAdapter != null) {
            Log.e("NFC", "disableForegroundDispatch");
            nfcAdapter.disableForegroundDispatch(this);
        };
    };

    @SuppressLint("SetTextI18n")
    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        Log.e("NFC", "onNewIntent");
        Log.e("NFC", "Intent: " + intent.getAction());
        if (NfcAdapter.ACTION_TECH_DISCOVERED.equals(intent.getAction())) {
            Log.e("NFC", "ACTION_TECH_DISCOVERED");
            Tag tagFromIntent = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);
            if (tagFromIntent != null) {
                StringBuilder tagId = new StringBuilder();
                for (byte b : tagFromIntent.getId()) {
                    tagId.append(String.format("%02X", b));
                }
                Log.e("NFC", "Tag ID: " + tagId);
                textView.setText("Tag ID: " + tagId);
            }
        }
    }
}


