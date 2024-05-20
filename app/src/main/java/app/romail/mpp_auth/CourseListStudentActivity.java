package app.romail.mpp_auth;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONArray;
import org.json.JSONException;

public class CourseListStudentActivity extends AppCompatActivity {

    String subjectName;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student_courses_list);
        // Get the Intent that started this activity and extract the string
        Intent intent = getIntent();
        String subjectId = String.valueOf(intent.getIntExtra("subjectId", 0));
        JSONArray subjects;
        try {
            subjects = new JSONArray(intent.getStringExtra("subjectList"));
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }
        subjectName = intent.getStringExtra("subjectName");

        JSONArray attendances = HttpRequest.GetRequestArray(this, "/courses/attendance/student/" + subjectId);
        if (attendances.length() == 0) {
            // No subjects found
            return;
        }

        String enrollmentId;
        try {
            enrollmentId = attendances.getJSONObject(0).getString("subjectEnrollment_id");
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }



        // Get the button from the layout
    Button newStudentAttendanceButton = findViewById(R.id.newStudentAttendance);

    // Set an OnClickListener for the button
    newStudentAttendanceButton.setOnClickListener(view -> {
        // if device supports host card emulation, start HCE service
        PackageManager packageManager = this.getPackageManager();
        if (packageManager.hasSystemFeature(PackageManager.FEATURE_NFC_HOST_CARD_EMULATION)) {
            Intent hceIntent = new Intent(this, HCEService.class);
            hceIntent.putExtra("ndefMessage", enrollmentId);
            Toast.makeText(this, "Starting NFC HCE for " + enrollmentId, Toast.LENGTH_LONG).show();
            startService(hceIntent);
        }
        else {
            Toast.makeText(this, "This device does not support NFC Host Card Emulation.", Toast.LENGTH_LONG).show();
        }
    });

}
        // Display the subjects
//        RecyclerView recyclerView = findViewById(R.id.coursesListStudent);
//        CourseListStudentAdapter customAdapter = new CourseListStudentAdapter(subjects, attendances);
//        recyclerView.setAdapter(customAdapter);
//        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
//        recyclerView.setLayoutManager(layoutManager);
//        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(recyclerView.getContext(),
//                DividerItemDecoration.VERTICAL);
//        dividerItemDecoration.setDrawable(Objects.requireNonNull(AppCompatResources.getDrawable(this, R.drawable.linear_divider)));
//        recyclerView.addItemDecoration(dividerItemDecoration);
//        customAdapter.setClickListener(this);
//    }

//    @Override
//    public void onItemClick(View view, int position) {
//        Toast.makeText(this, "You clicked " + position, Toast.LENGTH_SHORT).show();
//    }
//    }
}
