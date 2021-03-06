package com.example.finalproject;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.example.finalproject.model.Person;
import com.squareup.picasso.Picasso;

import java.util.List;

public class PersonAdapter extends RecyclerView.Adapter<PersonAdapter.ViewHolder> {
    private static final String TAG = "UserAdapter";
    private static ClickListener clickListener;

    private List<Person> localDataSet;

    /**
     * Provide a reference to the type of views that you are using
     * (custom ViewHolder).
     */
    public static class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private final TextView rowTextView;
        private final ImageView rowThumbnail;

        public ViewHolder(View view) {
            super(view);
            view.setOnClickListener(this);
            rowTextView = view.findViewById(R.id.row_text_view);
            rowThumbnail = view.findViewById(R.id.row_thumbnail);
        }

        public TextView getRowTextView() { return rowTextView; }
        public ImageView getRowThumbnail() { return rowThumbnail; }

        @Override
        public void onClick(View view) {
            clickListener.onItemClick(getAdapterPosition(), view);
        }
    }

    /**
     * Initialize the dataset of the Adapter.
     *
     * @param dataSet List<Person> containing the data to populate views to be used
     * by RecyclerView.
     */
    public PersonAdapter(List<Person> dataSet) { localDataSet = dataSet; }

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
        Person person = localDataSet.get(position);
        viewHolder.getRowTextView().setText(person.getName());
        if (person.getAvatarUrl() != null && !person.getAvatarUrl().isEmpty())
            Picasso.get().load(person.getAvatarUrl()).into(viewHolder.getRowThumbnail());
    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return localDataSet.size();
    }

    public static void setOnItemClickListener(ClickListener clickListener) {
        PersonAdapter.clickListener = clickListener;
    }

    public interface ClickListener {
        void onItemClick(int position, View view);
    }
}
