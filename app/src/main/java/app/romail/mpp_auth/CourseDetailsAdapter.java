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

public class CourseDetailsAdapter extends RecyclerView.Adapter<CourseDetailsAdapter.ViewHolder> {

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

        private final TextView firstName;

        private final TextView lastName;
        private final TextView attendanceDate;

        private final TableRow tableRow;
        private final TextView textView;

        public ViewHolder(View view) {
            super(view);
            // Define click listener for the ViewHolder's View
            Log.d("ViewHolder", "ViewHolder: ");
            Log.d("ViewHolder", "ViewHolder: "+ view);

            textView = view.findViewById(R.id.textView);
            firstName = view.findViewById(R.id.firstName);
            lastName = view.findViewById(R.id.lastName);
            attendanceDate = view.findViewById(R.id.attendanceDate);
            tableRow = view.findViewById(R.id.attendanceTable);
            tableRow.setOnClickListener(this);
//            cardView = (CardView) view.findViewById(R.id.cardView);
//            cardView.setOnClickListener(this);
        }

//        public CardView getCardView() {
//            return cardView;
//        }

        public TextView getTextView() {
            return textView;
        }

        public TextView getFirstName() {
            return firstName;
        }

        public TextView getLastName() {
            return lastName;
        }
        public TextView getAttendanceDate() {
            return attendanceDate;
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
    public CourseDetailsAdapter(JSONArray dataSet) {
        localDataSet = dataSet;
    }

    // Create new views (invoked by the layout manager)
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        // Create a new view, which defines the UI of the list item
        View view = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.attendance_row_item, viewGroup, false);

        return new ViewHolder(view);
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(ViewHolder viewHolder, final int position) {
        Log.d("SubjectListAdapter", "onBindViewHolder called for position "+position);
        // Get element from your dataset at this position and replace the
        // contents of the view with that element
        try {
            JSONObject attendance = localDataSet.getJSONObject(position);
            viewHolder.getFirstName().setText(attendance.getString("firstName"));
            viewHolder.getLastName().setText(attendance.getString("lastName"));
            Timestamp timestamp = new Timestamp(attendance.getLong("attendanceDate"));
            viewHolder.getAttendanceDate().setText(timestamp.toString());
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
