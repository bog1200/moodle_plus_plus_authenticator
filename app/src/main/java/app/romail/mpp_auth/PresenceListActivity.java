package app.romail.mpp_auth;

import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.List;

public class PresenceListActivity extends AppCompatActivity {

    private List<String> students;
    private ArrayAdapter<String> adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_presence_list);

        students = new ArrayList<>();
        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, students);

        ListView listView = findViewById(R.id.listView);
        listView.setAdapter(adapter);

        Button startButton = findViewById(R.id.startButton);
        startButton.setOnClickListener(view -> startPresenceList());
    }

    public void startPresenceList() {
        // Add your logic here to start the presence list
        // For example, you might want to fetch the list of students from a database
    }

    public void markPresent(String studentName) {
        // Add your logic here to mark a student as present
        // For example, you might want to add the student's name to the list
        students.add(studentName);
        adapter.notifyDataSetChanged();
    }
}