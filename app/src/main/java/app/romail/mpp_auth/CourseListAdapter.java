package app.romail.mpp_auth;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class CourseListAdapter extends RecyclerView.Adapter<CourseListAdapter.ViewHolder> {

    private JSONArray localDataSet;
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

        private final CardView cardView;
        private final TextView textView;

        public ViewHolder(View view) {
            super(view);
            // Define click listener for the ViewHolder's View
            Log.d("ViewHolder", "ViewHolder: ");
            Log.d("ViewHolder", "ViewHolder: "+view.toString());

            textView = (TextView) view.findViewById(R.id.textView);
            cardView = (CardView) view.findViewById(R.id.cardView);
            cardView.setOnClickListener(this);
        }

        public TextView getTextView() {
            return textView;
        }

        public CardView getCardView() {
            return cardView;
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

        // Get element from your dataset at this position and replace the
        // contents of the view with that element
        try {
            viewHolder.getTextView().setText(localDataSet.get(position).toString());

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