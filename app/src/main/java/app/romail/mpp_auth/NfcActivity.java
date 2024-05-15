package app.romail.mpp_auth;

import android.annotation.SuppressLint;
import android.app.PendingIntent;
import android.content.Intent;
import android.graphics.Bitmap;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.nfc.tech.IsoDep;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import net.sf.scuba.smartcards.CardService;

import org.apache.commons.io.IOUtils;
import org.bouncycastle.asn1.ASN1InputStream;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.ASN1Set;
import org.bouncycastle.asn1.x509.Certificate;
import org.jmrtd.BACKey;
import org.jmrtd.BACKeySpec;
import org.jmrtd.PassportService;
import org.jmrtd.lds.CardAccessFile;
import org.jmrtd.lds.ChipAuthenticationPublicKeyInfo;
import org.jmrtd.lds.PACEInfo;
import org.jmrtd.lds.SODFile;
import org.jmrtd.lds.SecurityInfo;
import org.jmrtd.lds.icao.DG14File;
import org.jmrtd.lds.icao.DG1File;
import org.jmrtd.lds.icao.DG2File;
import org.jmrtd.lds.iso19794.FaceImageInfo;
import org.jmrtd.lds.iso19794.FaceInfo;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.InputStream;
import java.security.KeyStore;
import java.security.MessageDigest;
import java.security.Signature;
import java.security.cert.CertPath;
import java.security.cert.CertPathValidator;
import java.security.cert.CertificateFactory;
import java.security.cert.PKIXParameters;
import java.security.cert.X509Certificate;
import java.security.spec.MGF1ParameterSpec;
import java.security.spec.PSSParameterSpec;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class NfcActivity extends AppCompatActivity {

    private NfcAdapter nfcAdapter;
    private BACKey bacKey;
//    private TextView textView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE, WindowManager.LayoutParams.FLAG_SECURE);
        Intent intent = getIntent();
        Log.d("NFC", "onCreate");
        Log.d("NFC", "Intent: DOB: " + intent.getStringExtra("birthDate"));
        Log.d("NFC", "Intent: EXP: " + intent.getStringExtra("expiryDate"));
        Log.d("NFC", "Intent: NO: " + intent.getStringExtra("idNumber"));

        //convert birthDate to yyyy-MM-dd
        @SuppressLint("SimpleDateFormat") SimpleDateFormat originalFormat = new SimpleDateFormat("dd MMM yyyy");
        @SuppressLint("SimpleDateFormat") SimpleDateFormat targetFormat = new SimpleDateFormat("yyMMdd");
        String birthDateString;
        String expiryDateString;
        if (intent.getStringExtra("birthDate") == null || intent.getStringExtra("expiryDate") == null || intent.getStringExtra("idNumber") == null) {
            startActivity(new Intent(this, LoginActivity.class));
        }
        try {
           Date birthDate = originalFormat.parse(Objects.requireNonNull(intent.getStringExtra("birthDate")));
            assert birthDate != null;
            birthDateString = targetFormat.format(birthDate);
           Date expiryDate = originalFormat.parse(Objects.requireNonNull(intent.getStringExtra("expiryDate")));
            assert expiryDate != null;
            expiryDateString = targetFormat.format(expiryDate);
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }

        Log.d("NFC", "birthDateFormatted: " + birthDateString);
        Log.d("NFC", "expiryDateFormatted: " + expiryDateString);

        bacKey = new BACKey(
                Objects.requireNonNull(intent.getStringExtra("idNumber")),
                birthDateString,
                expiryDateString
        );

        nfcAdapter = NfcAdapter.getDefaultAdapter(this);

        if (nfcAdapter == null) {
            Toast.makeText(this, "NFC is not available on this device", Toast.LENGTH_LONG).show();
            finish();
            return;
        }
        else {
            setContentView(R.layout.activity_nfc_processing);
           // textView = findViewById(R.id.textView);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        nfcAdapter = NfcAdapter.getDefaultAdapter(this);
        Log.d("NFC", "onResume");
        if (nfcAdapter != null) {
            Log.d("NFC", "enableForegroundDispatch");
            PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, new Intent(this, getClass()).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP), PendingIntent.FLAG_MUTABLE);
            String[][] filter = {{"android.nfc.tech.IsoDep"}};
            nfcAdapter.enableForegroundDispatch(this, pendingIntent, null, filter );
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        nfcAdapter = NfcAdapter.getDefaultAdapter(this);
        Log.d("NFC", "onPause");
        if (nfcAdapter != null) {
            Log.d("NFC", "disableForegroundDispatch");
            nfcAdapter.disableForegroundDispatch(this);
        }
    }


    @Override
    protected void onNewIntent(@NonNull Intent intent) {
        super.onNewIntent(intent);
        Log.d("NFC", "onNewIntent");
        Log.d("NFC", "Intent: " + intent.getAction());
        if (NfcAdapter.ACTION_TECH_DISCOVERED.equals(intent.getAction())) {
            Log.d("NFC", "ACTION_TECH_DISCOVERED");
            Tag tagFromIntent = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);
            if (tagFromIntent == null){
                Log.e("NFC", "tagFromIntent is null");
                return;
            }
            else {
                Log.d("NFC", "tagFromIntent is not null");
                String[] techList = tagFromIntent.getTechList();
                for (String tech : techList) {
                    Log.d("NFC", "tech: " + tech);
                }
                if (Arrays.asList(techList).contains("android.nfc.tech.IsoDep")) {
                    IsoDep isoDep = IsoDep.get(tagFromIntent);
                    if (isoDep != null) {
                        TextView textView = findViewById(R.id.textView);
                        textView.setVisibility(View.GONE);
                        LinearLayout layout = findViewById(R.id.loading_layout);
                        layout.setVisibility(View.VISIBLE);
                        NFCReader nfcReader = new NFCReader(isoDep, bacKey);
                        nfcReader.execute();
                    }
                }


            }
        }
    }



    @SuppressLint("StaticFieldLeak") //TODO: Add a progress bar
    private class NFCReader extends AsyncTask<Void, Void, Exception> {
        private static final String TAG = "NFCReader";

        private IsoDep isoDep;
        private BACKeySpec bacKey;

        private DG1File dg1File;
        private DG2File dg2File;
        private DG14File dg14File;
        private SODFile sodFile;
        private String imageBase64;
        private Bitmap bitmap;
        private boolean chipAuthSucceeded = false;
        private boolean passiveAuthSuccess = false;
        private byte[] dg14Encoded;

        public NFCReader(IsoDep isoDep, BACKeySpec bacKey) {
            Log.d(TAG, "NFCReader: ");
            Log.d(TAG, "BAC: " + bacKey);
            this.isoDep = isoDep;
            this.bacKey = bacKey;
        }

        @Override
        protected Exception doInBackground(Void... params) {
            Log.d(TAG, "doInBackground: ");
            try {
                Log.d(TAG, "Connecting to NFC ID...");
                isoDep.setTimeout(20000);
                Log.d(TAG, "Timeout set to 20 seconds");
                isoDep.connect();
                CardService cardService = CardService.getInstance(isoDep);
                Log.d(TAG, "CardService created");

                cardService.open();
                Log.d(TAG, "CardService opened");
                PassportService service = new PassportService(
                        cardService,
                        PassportService.NORMAL_MAX_TRANCEIVE_LENGTH,
                        PassportService.DEFAULT_MAX_BLOCKSIZE,
                        false,
                        false
                );
                service.open();
                Log.d(TAG, "PassportService opened");
                boolean paceSucceeded = false;
                try {
                    Log.d(TAG, "Trying PACE...");
                    InputStream cardAccessInputStream = service.getInputStream(PassportService.EF_CARD_ACCESS);
                    CardAccessFile cardAccessFile = new CardAccessFile(cardAccessInputStream);
                    for (SecurityInfo securityInfo : cardAccessFile.getSecurityInfos()) {
                        if (securityInfo instanceof PACEInfo) {
                            service.doPACE(
                                    bacKey,
                                    securityInfo.getObjectIdentifier(),
                                    PACEInfo.toParameterSpec(((PACEInfo) securityInfo).getParameterId()),
                                    null
                            );
                            Log.d(TAG, "PACE succeeded");
                            paceSucceeded = true;
                        }
                    }
                } catch (Exception e) {
                    Log.w(TAG, e);
                }
                service.sendSelectApplet(paceSucceeded);
                if (!paceSucceeded) {
                    Log.d(TAG, "PACE failed, trying BAC...");
                    try {
                        service.getInputStream(PassportService.EF_COM).read();
                    } catch (Exception e) {
                        service.doBAC(bacKey);
                    }
                }

                Log.d(TAG, "Reading files...");

                InputStream dg1In = service.getInputStream(PassportService.EF_DG1);
                dg1File = new DG1File(dg1In);
                Log.d(TAG, "DG1 read");
                InputStream dg2In = service.getInputStream(PassportService.EF_DG2);
                dg2File = new DG2File(dg2In);
                Log.d(TAG, "DG2 read");
                InputStream sodIn = service.getInputStream(PassportService.EF_SOD);
                sodFile = new SODFile(sodIn);
                Log.d(TAG, "SOD read");

                doChipAuth(service);
                doPassiveAuth();

                List<FaceImageInfo> allFaceImageInfo = new ArrayList<>();
                for (FaceInfo faceInfo : dg2File.getFaceInfos()) {
                    allFaceImageInfo.addAll(faceInfo.getFaceImageInfos());
                }
                if (!allFaceImageInfo.isEmpty()) {
                    FaceImageInfo faceImageInfo = allFaceImageInfo.get(0);
                    int imageLength = faceImageInfo.getImageLength();
                    DataInputStream dataInputStream = new DataInputStream(faceImageInfo.getImageInputStream());
                    byte[] buffer = new byte[imageLength];
                    Log.d(TAG, "Reading image...");
                    dataInputStream.readFully(buffer, 0, imageLength);
                    ByteArrayInputStream inputStream = new ByteArrayInputStream(buffer, 0, imageLength);
                    bitmap = ImageUtil.decodeImage(null, faceImageInfo.getMimeType(), inputStream);
                    imageBase64 = Base64.encodeToString(buffer, Base64.DEFAULT);
                    Log.d(TAG, "Image read");
                }
            } catch (Exception e) {
                return e;
            }
            return null;
        }

        private void doChipAuth(PassportService service) {
            Log.d(TAG, "Starting Chip Authentication...");
            try {
                InputStream dg14In = service.getInputStream(PassportService.EF_DG14);
                dg14Encoded = IOUtils.toByteArray(dg14In);
                ByteArrayInputStream dg14InByte = new ByteArrayInputStream(dg14Encoded);
                dg14File = new DG14File(dg14InByte);
                Log.d(TAG, "DG14 read");
                for (SecurityInfo securityInfo : dg14File.getSecurityInfos()) {
                    Log.d(TAG, "SecurityInfo: " + securityInfo);
                    if (securityInfo instanceof ChipAuthenticationPublicKeyInfo) {
                        service.doEACCA(
                                ((ChipAuthenticationPublicKeyInfo) securityInfo).getKeyId(),
                                ChipAuthenticationPublicKeyInfo.ID_CA_ECDH_AES_CBC_CMAC_256,
                                securityInfo.getObjectIdentifier(),
                                ((ChipAuthenticationPublicKeyInfo) securityInfo).getSubjectPublicKey()
                        );
                        Log.d(TAG, "Chip Auth succeeded");
                        chipAuthSucceeded = true;
                    }
                }
            } catch (Exception e) {
                Log.w(TAG, e);
            }
        }

        private void doPassiveAuth() {
            Log.d(TAG, "Starting Passive Authentication...");
            try {
                MessageDigest digest = MessageDigest.getInstance(sodFile.getDigestAlgorithm());
                Map<Integer, byte[]> dataHashes = sodFile.getDataGroupHashes();
                byte[] dg14Hash = chipAuthSucceeded ? digest.digest(dg14Encoded) : new byte[0];
                byte[] dg1Hash = digest.digest(dg1File.getEncoded());
                byte[] dg2Hash = digest.digest(dg2File.getEncoded());

                if (Arrays.equals(dg1Hash, dataHashes.get(1)) && Arrays.equals(dg2Hash, dataHashes.get(2))
                        && (!chipAuthSucceeded || Arrays.equals(dg14Hash, dataHashes.get(14)))) {
                    // open("ICAOMaster"));
                    ASN1InputStream asn1InputStream = new ASN1InputStream(getAssets().open("masterList"));
                    KeyStore keystore = KeyStore.getInstance(KeyStore.getDefaultType());
                    keystore.load(null, null);
                    CertificateFactory cf = CertificateFactory.getInstance("X.509");

                    ASN1Primitive p;
                    while ((p = asn1InputStream.readObject()) != null) {
                        ASN1Sequence asn1 = ASN1Sequence.getInstance(p);
                        if (asn1 == null || asn1.size() == 0) {
                            throw new IllegalArgumentException("Null or empty sequence passed.");
                        }
                        if (asn1.size() != 2) {
                            throw new IllegalArgumentException("Incorrect sequence size: " + asn1.size());
                        }
                        ASN1Set certSet = ASN1Set.getInstance(asn1.getObjectAt(1));
                        for (int i = 0; i < certSet.size(); i++) {
                            Certificate certificate = Certificate.getInstance(certSet.getObjectAt(i));
                            byte[] pemCertificate = certificate.getEncoded();
                            X509Certificate javaCertificate = (X509Certificate) cf.generateCertificate(new ByteArrayInputStream(pemCertificate));
                            keystore.setCertificateEntry(Integer.toString(i), javaCertificate);
                        }
                    }

                    List<X509Certificate> docSigningCertificates = sodFile.getDocSigningCertificates();
                    for (X509Certificate docSigningCertificate : docSigningCertificates) {
                        docSigningCertificate.checkValidity();
                    }

                    CertPath cp = cf.generateCertPath(docSigningCertificates);
                    PKIXParameters pkixParameters = new PKIXParameters(keystore);
                    pkixParameters.setRevocationEnabled(false);
                    CertPathValidator cpv = CertPathValidator.getInstance(CertPathValidator.getDefaultType());
                    cpv.validate(cp, pkixParameters);
                    String sodDigestEncryptionAlgorithm = sodFile.getDocSigningCertificate().getSigAlgName();
                    boolean isSSA = false;
                    if ("SSAwithRSA/PSS".equals(sodDigestEncryptionAlgorithm)) {
                        sodDigestEncryptionAlgorithm = "SHA256withRSA/PSS";
                        isSSA = true;
                    }
                    Signature sign = Signature.getInstance(sodDigestEncryptionAlgorithm);
                    if (isSSA) {
                        sign.setParameter(new PSSParameterSpec("SHA-256", "MGF1", MGF1ParameterSpec.SHA256, 32, 1));
                    }
                    sign.initVerify(sodFile.getDocSigningCertificate());
                    sign.update(sodFile.getEContent());
                    passiveAuthSuccess = sign.verify(sodFile.getEncryptedDigest());
                    if (passiveAuthSuccess) {
                        Log.d(TAG, "Passive Auth succeeded");
                    }
                }
            } catch (Exception e) {
                Log.w(TAG, e);
            }
        }

        @Override
protected void onPostExecute(Exception e) {
            super.onPostExecute(e);
            if (e != null) {
                Log.e(TAG, "Error reading NFC: ", e);
                Toast.makeText(NfcActivity.this, "Error reading NFC: " + e.getMessage(), Toast.LENGTH_LONG).show();
                finish();
            } else {
                Log.d(TAG, "NFC read successfully");
                Intent intent = new Intent(NfcActivity.this, NfcIdReadActivity.class);
               intent.putExtra(NfcIdReadActivity.KEY_FIRST_NAME, dg1File.getMRZInfo().getSecondaryIdentifier().replace("<", " "));
                intent.putExtra(NfcIdReadActivity.KEY_LAST_NAME, dg1File.getMRZInfo().getPrimaryIdentifier().replace("<", " "));
                intent.putExtra(NfcIdReadActivity.KEY_DATE_OF_BIRTH, dg1File.getMRZInfo().getDateOfBirth());
                intent.putExtra(NfcIdReadActivity.KEY_EXP, dg1File.getMRZInfo().getDateOfExpiry());
                intent.putExtra(NfcIdReadActivity.KEY_DOCUMENT_NUMBER, dg1File.getMRZInfo().getDocumentNumber());
                intent.putExtra(NfcIdReadActivity.KEY_COUNTRY, dg1File.getMRZInfo().getNationality());
                intent.putExtra(NfcIdReadActivity.KEY_PIN, dg1File.getMRZInfo().getPersonalNumber());
                float ratio = (float) (320.0 / bitmap.getHeight());
                int newWidth = (int) (bitmap.getWidth() * ratio);
                int newHeight = (int) (bitmap.getHeight() * ratio);
                Bitmap scaledBitmap = Bitmap.createScaledBitmap(bitmap, newWidth, newHeight, true);
                intent.putExtra(NfcIdReadActivity.KEY_IMAGE, scaledBitmap);
                startActivity(intent);
            }
        }
    }
}






