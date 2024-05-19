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

import java.util.List;
import java.util.Objects;

public class SubjectListActivity extends AppCompatActivity implements SubjectListAdapter.ItemClickListener {
    JSONArray subjects;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_subjects_list);
        // Get the Intent that started this activity and extract the string

        List<String> roles = HttpRequest.getUserRole(this);
        String userId = HttpRequest.getAccountFromToken(this).toString();


        if(roles.contains("ROLE_STUDENT")){
            // Get the student ID from the token
            subjects = HttpRequest.GetRequestArray(this, "/subject/student/" + userId);
        } else if(roles.contains("ROLE_TEACHER")){
            subjects = HttpRequest.GetRequestArray(this, "/subject/teacher/" + userId);
         } else subjects = null;

        if (subjects.length() == 0) {
            // No subjects found
            return;
        }
        // Display the subjects
        RecyclerView recyclerView = findViewById(R.id.subjectsList);
        SubjectListAdapter customAdapter = new SubjectListAdapter(subjects);
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
    public void onItemClick(View view, int position, String subjectName) {
        Toast.makeText(this, "You clicked " + position, Toast.LENGTH_SHORT).show();
        //TODO: check if the user is a student or a teacher. If it is a student, open the student course list, if it is a teacher, open the teacher course list

        if(HttpRequest.getUserRole(this).contains("ROLE_STUDENT")){
//            Intent intent = new Intent(this, CourseListStudentActivity.class);
//            intent.putExtra("subjectId", position);
//            intent.putExtra("subjectName", subjectName);
//            intent.putExtra("subjectList", subjects.toString());
//      startActivity(intent);
        } else if(HttpRequest.getUserRole(this).contains("ROLE_TEACHER")){
            Intent intent = new Intent(this, CourseListActivity.class);
            intent.putExtra("subjectId", position);
            intent.putExtra("subjectName", subjectName);
            startActivity(intent);
        }

//        Intent intent = new Intent(this, CourseListActivity.class);
//        intent.putExtra("subjectId", position);
//        intent.putExtra("subjectName", subjectName);
//        startActivity(intent);
//        finish();
    }
}
