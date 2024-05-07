package app.romail.mpp_auth;

import android.nfc.cardemulation.HostApduService;
import android.os.Bundle;
import android.util.Log;

public class HCEService extends HostApduService {

    private static final String TAG = "MyHostApduService";
    private static final String MY_UID = "F0123456";
    private static final String RESPONSE_SUCCESS = "good";

    private static final Byte[] ADPU_CUSTOM_GET_UID = new Byte[]{
            (byte) 0x80, // CLA	- Class - Class of instruction (00 - for inter-industry, 80 - for proprietary)
            (byte) 0x01, // INS	- Instruction - Instruction code (01 - (Custom) Get UID)
            (byte) 0x00, // P1	- Parameter 1 - Instruction parameter 1 (CA - for Get UID)
            (byte) 0x00 // P2	- Parameter 2 - Instruction parameter
    };

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d(TAG, "Service started");
    }

    @Override
    public byte[] processCommandApdu(byte[] commandApdu, Bundle extras) {
        String hexCommandApdu = bytesToHex(commandApdu);
        if (hexCommandApdu.startsWith(MY_UID)) {
            Log.d(TAG, "Received command: " + hexCommandApdu);
            return RESPONSE_SUCCESS.getBytes();
        }
        return new byte[]{(byte) 0x6A, (byte) 0x81};
    }

    @Override
    public void onDeactivated(int reason) {
        Log.d(TAG, "Service deactivated");
    }

    private String bytesToHex(byte[] bytes) {
        StringBuilder sb = new StringBuilder();
        for (byte aByte : bytes) {
            sb.append(String.format("%02X", aByte));
        }
        return sb.toString();
    }
}