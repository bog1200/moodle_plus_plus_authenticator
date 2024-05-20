package app.romail.mpp_auth;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.content.res.AppCompatResources;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Objects;

public class CourseDetailsActivity extends AppCompatActivity implements CourseDetailsAdapter.ItemClickListener {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_course_details);
        // Get the Intent that started this activity and extract the string
        Intent intent = getIntent();
        String courseId = String.valueOf(intent.getIntExtra("courseId",0));
        String subjectName = intent.getStringExtra("subjectName");

        TextView subjectNameView = findViewById(R.id.subjectName);
        subjectNameView.setText(subjectName);

        JSONArray attendances = HttpRequest.GetRequestArray(this, "/courses/attendance/course/"+courseId);
        if (attendances.length() == 0) {
            // No courses found
            return;
        }
        JSONArray students = new JSONArray();
        for (int i = 0; i < attendances.length(); i++) {
            try {
                JSONObject student = HttpRequest.GetRequest(this, "/student/getByEnrollmentId/"+attendances.getJSONObject(i).getString("subjectEnrollment_id"));
                student.put("attendanceDate", attendances.getJSONObject(i).getString("date"));
                students.put(student);
            } catch (JSONException e) {
                throw new RuntimeException(e);
            }
        }
        Button buttonNfcReceive = findViewById(R.id.startAttendanceButton);
        buttonNfcReceive.setOnClickListener(v->{
            Intent intentNfc = new Intent(this, NfcReceive.class);
            intentNfc.putExtra("courseId", courseId);
            startActivity(intentNfc);
        });
        // Display the attendances
        RecyclerView recyclerView = findViewById(R.id.courseDetailsList);
        CourseDetailsAdapter customAdapter = new CourseDetailsAdapter(students);
        recyclerView.setAdapter(customAdapter);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(recyclerView.getContext(),
                DividerItemDecoration.VERTICAL);
        dividerItemDecoration.setDrawable(Objects.requireNonNull(AppCompatResources.getDrawable(this, R.drawable.linear_divider)));
        recyclerView.addItemDecoration(dividerItemDecoration);
        customAdapter.setClickListener(this);
    }

    @Override
    public void onItemClick(View view, int position) {
        Toast.makeText(this, "You clicked " + position, Toast.LENGTH_SHORT).show();
    }
}
