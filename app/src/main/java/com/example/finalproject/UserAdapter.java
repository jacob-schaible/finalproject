package com.example.finalproject;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.example.finalproject.model.User;

import java.util.List;

public class UserAdapter extends RecyclerView.Adapter<UserAdapter.ViewHolder> {

    private static final String TAG = "UserAdapter";

    private List<User> localDataSet;

    /**
     * Provide a reference to the type of views that you are using
     * (custom ViewHolder).
     */
    public static class ViewHolder extends RecyclerView.ViewHolder {
        private final TextView rowTextView;

        public ViewHolder(View view) {
            super(view);
            view.setOnClickListener(v -> {
                Log.d(TAG, "Element " + getAdapterPosition() + " clicked.");

            });
            rowTextView = (TextView) view.findViewById(R.id.row_text_view);
        }

        public TextView getRowTextView() { return rowTextView; }
    }

    /**
     * Initialize the dataset of the Adapter.
     *
     * @param dataSet List<User> containing the data to populate views to be used
     * by RecyclerView.
     */
    public UserAdapter(List<User> dataSet) { localDataSet = dataSet; }

    // Create new views (invoked by the layout manager)
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        // Create a new view, which defines the UI of the list item
        View view = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.text_row_item, viewGroup, false);

        return new ViewHolder(view);
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(ViewHolder viewHolder, final int position) {
        Log.d(TAG, "Element " + position + " set.");
        // Get element from your dataset at this position and replace the
        // contents of the view with that element
        viewHolder.getRowTextView().setText(localDataSet.get(position).toString());
    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return localDataSet.size();
    }
}
