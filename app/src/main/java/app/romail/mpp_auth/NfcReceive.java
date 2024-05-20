package app.romail.mpp_auth;

import android.content.Intent;
import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.nfc.tech.Ndef;
import android.os.Bundle;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.provider.Settings;
import android.util.Log;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONException;
import org.json.JSONObject;

public class NfcReceive  extends AppCompatActivity implements NfcAdapter.ReaderCallback{

    // SO I RECEIVE FROM THE NFC TAG: subjectEnrollment_id
    // i take course_id from the general common memory in the database
    // and then i generate the data on the spot when the request arrives

    TextView readResult;
    private NfcAdapter mNfcAdapter;
    String dumpExportString = "";
    String tagIdString = "";
    String tagTypeString = "";
    //cu studentEnrollment si id-ul serverului trimit dupa pe server ce trb
    private static final int REQUEST_PERMISSION_WRITE_EXTERNAL_STORAGE = 100;
   String courseId;

        @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE, WindowManager.LayoutParams.FLAG_SECURE);
            setContentView(R.layout.activity_nfc_processing); // replace with your actual layout

        // Initialize NFC Adapter
        mNfcAdapter = NfcAdapter.getDefaultAdapter(this);
            Intent intentNfc = getIntent();
            courseId = intentNfc.getStringExtra("courseId");


        if (mNfcAdapter == null) {
            Toast.makeText(this, "This device does not support NFC.", Toast.LENGTH_LONG).show();
            finish();
            return;
        }

        if (!mNfcAdapter.isEnabled()) {
            Toast.makeText(this, "NFC is turned off.", Toast.LENGTH_LONG).show();
            startActivity(new Intent(Settings.ACTION_NFC_SETTINGS));
        } else {
            // Enable reader mode with the flags you need
            mNfcAdapter.enableReaderMode(this, this,
                    NfcAdapter.FLAG_READER_NFC_A | NfcAdapter.FLAG_READER_SKIP_NDEF_CHECK,
                    null);
        }
    }

    // This method is running in another thread when a card is discovered
    // !!!! This method cannot cannot direct interact with the UI Thread
    // Use `runOnUiThread` method to change the UI from this method

    // i need to create a new attendance, i need to create a new one the fields:
    @Override
public void onTagDiscovered(Tag tag) {
    Ndef ndef = Ndef.get(tag);
    if (ndef != null) {
        Vibrator vibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);
        vibrator.vibrate(VibrationEffect.createOneShot(1000, VibrationEffect.DEFAULT_AMPLITUDE));
        NdefMessage ndefMessage = ndef.getCachedNdefMessage();
        NdefRecord[] records = ndefMessage.getRecords();
        for (NdefRecord record : records) {
            byte[] payload = record.getPayload();
            String subjectEnrollmentId = new String(payload);
            Log.d("NFC", "onTagDiscovered: " + subjectEnrollmentId);

            // Generate data
            JSONObject data = new JSONObject();
            try {
                data.put("subjectEnrollment_id", subjectEnrollmentId.substring(3));
                data.put("course_id", courseId);
                data.put("date", System.currentTimeMillis());
                // Add any other data you need to generate

                // Send data to the server
                // imi trb course_id, subjectEnrollment_id, si data

                HttpRequest.PostRequest(this, "/courses/attendance/new", data);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }
}


    @Override
    public void onResume() {
        super.onResume();

        if (mNfcAdapter != null) {

            Bundle options = new Bundle();
            // Work around for some broken Nfc firmware implementations that poll the card too fast
            options.putInt(NfcAdapter.EXTRA_READER_PRESENCE_CHECK_DELAY, 250);

            // Enable ReaderMode for all types of card and disable platform sounds
            // the option NfcAdapter.FLAG_READER_SKIP_NDEF_CHECK is NOT set
            // to get the data of the tag afer reading
            mNfcAdapter.enableReaderMode(this,
                    this,
                    NfcAdapter.FLAG_READER_NFC_A |
                            NfcAdapter.FLAG_READER_NFC_B |
                            NfcAdapter.FLAG_READER_NFC_F |
                            NfcAdapter.FLAG_READER_NFC_V |
                            NfcAdapter.FLAG_READER_NFC_BARCODE |
                            NfcAdapter.FLAG_READER_NO_PLATFORM_SOUNDS,
                    options);
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        if (mNfcAdapter != null)
            mNfcAdapter.disableReaderMode(this);
    }




}