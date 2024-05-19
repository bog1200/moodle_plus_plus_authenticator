package app.romail.mpp_auth;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TableRow;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.sql.Timestamp;

public class CourseListStudentAdapter extends RecyclerView.Adapter<CourseListStudentAdapter.ViewHolder> {

    private final JSONArray localSubjectDataSet;
    private ItemClickListener listener;

    public interface ItemClickListener {
        void onItemClick(View view, int position);
    }

    public void setClickListener(ItemClickListener itemClickListener) {
        this.listener = itemClickListener;
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private final TextView startDate;
        private final TextView endDate;
        private final CheckBox presence;

        private final TableRow tableRow;
        private final TextView textView;

        public ViewHolder(View view) {
            super(view);
            // Define click listener for the ViewHolder's View
            Log.d("ViewHolder", "ViewHolder: ");
            Log.d("ViewHolder", "ViewHolder: " + view);

            textView = view.findViewById(R.id.textView);
            startDate = view.findViewById(R.id.startDate);
            endDate = view.findViewById(R.id.endDate);
            presence = view.findViewById(R.id.presence);
            tableRow = view.findViewById(R.id.coursesTable);
            tableRow.setOnClickListener(this);
        }

        public TextView getTextView() {
            return textView;
        }

        public TextView getStartDate() {
            return startDate;
        }

        public TextView getEndDate() {
            return endDate;
        }

        public CheckBox getPresence() {
            return presence;
        }

        public TableRow getTableRow() {
            return tableRow;
        }

        public void onClick(View v) {
            if (listener != null) {
                try {
                    JSONObject subject = localSubjectDataSet.getJSONObject(getAdapterPosition());
                    listener.onItemClick(v, subject.getInt("id"));
                } catch (JSONException e) {
                    throw new RuntimeException(e);
                }
            }
        }
    }

    /**
     * Initialize the dataset of the Adapter
     *
     * @param subjectDataSet     String[] containing the data to populate views to be used
     *                    by RecyclerView
     * @param attendances
     */
    public CourseListStudentAdapter(JSONArray subjectDataSet, JSONArray attendances) {
        localSubjectDataSet = subjectDataSet;


    }

    // Create new views (invoked by the layout manager)
    @NonNull
    @Override
    public CourseListStudentAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        View view = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.activity_student_courses_list, viewGroup, false);
        return new ViewHolder(view);
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, final int position) {
        try {
            JSONObject course = localSubjectDataSet.getJSONObject(position);
            Timestamp startT = new Timestamp(course.getLong("startDate"));
            Timestamp endT = new Timestamp(course.getLong("endDate"));
            boolean isPresent = course.getBoolean("presence");
            viewHolder.getPresence().setChecked(isPresent);
            viewHolder.getStartDate().setText(startT.toString());
            viewHolder.getEndDate().setText(endT.toString());
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public int getItemCount() {
        return localSubjectDataSet.length();
    }
}

