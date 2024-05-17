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

public class SubjectListAdapter extends RecyclerView.Adapter<SubjectListAdapter.ViewHolder> {

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

        private final TextView courseName;
        private final TextView courseDescription;
        private final TextView courseCode;

        private final TableRow tableRow;

        public ViewHolder(View view) {
            super(view);
            // Define click listener for the ViewHolder's View
            Log.d("ViewHolder", "ViewHolder: ");
            Log.d("ViewHolder", "ViewHolder: "+ view);

            courseName = view.findViewById(R.id.courseName);
            courseDescription = view.findViewById(R.id.courseDescription);
            courseCode = view.findViewById(R.id.courseCode);
            tableRow = view.findViewById(R.id.subjectsTable);
            tableRow.setOnClickListener(this);
            //textView = (TextView) view.findViewById(R.id.textView);
//            cardView = (CardView) view.findViewById(R.id.cardView);
//            cardView.setOnClickListener(this);
        }

//        public CardView getCardView() {
//            return cardView;
//        }

        public TextView getCourseName() {
            return courseName;
        }

        public TextView getCourseDescription() {
            return courseDescription;
        }

        public TextView getCourseCode() {
            return courseCode;
        }

        public TableRow getTableRow() {
            return tableRow;
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
    public SubjectListAdapter(JSONArray dataSet) {
        localDataSet = dataSet;
    }

    // Create new views (invoked by the layout manager)
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        // Create a new view, which defines the UI of the list item
        View view = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.subjects_row_item, viewGroup, false);

        return new ViewHolder(view);
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(ViewHolder viewHolder, final int position) {
        Log.d("SubjectListAdapter", "onBindViewHolder called for position "+position);
        // Get element from your dataset at this position and replace the
        // contents of the view with that element
        try {
            JSONObject course = localDataSet.getJSONObject(position);
            viewHolder.getCourseName().setText(course.getString("name"));
            viewHolder.getCourseDescription().setText(course.getString("description"));
            viewHolder.getCourseCode().setText(course.getString("code"));

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