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

import java.util.Objects;

public class CourseListActivity extends AppCompatActivity implements SubjectListAdapter.ItemClickListener {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_subjects_list);
        // Get the Intent that started this activity and extract the string
        Intent intent = getIntent();
        String subjectId = String.valueOf(intent.getIntExtra("subjectId",0));

         JSONArray subjects = HttpRequest.GetRequestArray(this, "/course/getBySubject/"+subjectId);
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
    public void onItemClick(View view, int position) {
        Toast.makeText(this, "You clicked " + position, Toast.LENGTH_SHORT).show();
    }
}
