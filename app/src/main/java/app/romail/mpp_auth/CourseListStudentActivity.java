package app.romail.mpp_auth;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.content.res.AppCompatResources;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.Objects;

public class CourseListStudentActivity extends AppCompatActivity implements CourseListStudentAdapter.ItemClickListener{

    String subjectName;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student_courses_list);
        // Get the Intent that started this activity and extract the string
        Intent intent = getIntent();
        String subjectId = String.valueOf(intent.getIntExtra("subjectId",0));
        JSONArray subjects;
        try {
            subjects = new JSONArray(intent.getStringExtra("subjectList"));
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }
        subjectName = intent.getStringExtra("subjectName");

        JSONArray attendances = HttpRequest.GetRequestArray(this, "/courses/attendance/student/"+subjectId);
        if (attendances.length() == 0) {
            // No subjects found
            return;
        }
        // Display the subjects
        RecyclerView recyclerView = findViewById(R.id.coursesListStudent);
        CourseListStudentAdapter customAdapter = new CourseListStudentAdapter(subjects, attendances);
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
