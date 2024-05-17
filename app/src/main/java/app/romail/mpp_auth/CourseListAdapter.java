package app.romail.mpp_auth;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TableRow;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.sql.Timestamp;

public class CourseListAdapter extends RecyclerView.Adapter<CourseListAdapter.ViewHolder> {

    private final JSONArray localDataSet;
    private ItemClickListener listener;

    public interface ItemClickListener {
        void onItemClick(View view, int position);
    }
    public void setClickListener(ItemClickListener itemClickListener) {
        this.listener = itemClickListener;
    }

    /**
     * Provide a reference to the type of views that you are using
     * (custom ViewHolder)
     */
    public class ViewHolder extends RecyclerView.ViewHolder  implements View.OnClickListener {

//        private final CardView cardView;

        private final TextView startDate;
        private final TextView endDate;
        private final TextView courseAttendances_ids;

        private final TableRow tableRow;
        private final TextView textView;

        public ViewHolder(View view) {
            super(view);
            // Define click listener for the ViewHolder's View
            Log.d("ViewHolder", "ViewHolder: ");
            Log.d("ViewHolder", "ViewHolder: "+ view);

            textView = view.findViewById(R.id.textView);
            startDate = view.findViewById(R.id.startDate);
            endDate = view.findViewById(R.id.endDate);
            courseAttendances_ids = view.findViewById(R.id.courseAttendances_ids);
            tableRow = view.findViewById(R.id.coursesTable);
            tableRow.setOnClickListener(this);
//            cardView = (CardView) view.findViewById(R.id.cardView);
//            cardView.setOnClickListener(this);
        }

        public TextView getTextView() {
            return textView;
        }

//        public CardView getCardView() {
//            return cardView;
//        }

        public TableRow getTableRow() {
            return tableRow;
        }

        public TextView getStartDate() {
            return startDate;
        }

        public TextView getEndDate() {
            return endDate;
        }

        public TextView getCourseAttendances_ids() {
            return courseAttendances_ids;
        }

        @Override
        public void onClick(View v) {
            if (listener != null) {
                try {
                    JSONObject subject = localDataSet.getJSONObject(getAdapterPosition());
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
     * @param dataSet String[] containing the data to populate views to be used
     * by RecyclerView
     */
    public CourseListAdapter(JSONArray dataSet) {
        localDataSet = dataSet;
    }

    // Create new views (invoked by the layout manager)
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        // Create a new view, which defines the UI of the list item
        View view = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.courses_row_item, viewGroup, false);

        return new ViewHolder(view);
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(ViewHolder viewHolder, final int position) {

        // Get element from your dataset at this position and replace the
        // contents of the view with that element
        try {
            JSONObject course = localDataSet.getJSONObject(position);
            Timestamp startT = new Timestamp(course.getLong("startDate"));
            Timestamp endT = new Timestamp(course.getLong("endDate"));
            viewHolder.getStartDate().setText(startT.toString());
            viewHolder.getEndDate().setText(endT.toString());
            int count = course.getJSONArray("courseAttendances_ids").length();
            viewHolder.getCourseAttendances_ids().setText(String.valueOf(count));
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }
    }
    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return localDataSet.length();
    }


}