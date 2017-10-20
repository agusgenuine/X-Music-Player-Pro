package com.riseapps.xmusic.executor.RecycleViewAdapters;

import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.riseapps.xmusic.R;
import com.riseapps.xmusic.executor.Interfaces.FragmentTransitionListener;
import com.riseapps.xmusic.model.Pojo.Genre;
import com.riseapps.xmusic.view.Fragment.ScrollingFragment;
import com.riseapps.xmusic.view.activity.MainActivity;

public class GenreAdapter extends RecyclerView.Adapter {

    private Cursor dataCursor;
    private ScrollingFragment scrollingFragment;
    private FragmentTransitionListener fragmentTransitionListener;
    Context c;

    public GenreAdapter(Context context, RecyclerView recyclerView, Cursor cursor) {

        dataCursor = cursor;
        LinearLayoutManager linearLayoutManager = (LinearLayoutManager) recyclerView
                .getLayoutManager();
        c = context;
    }


    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        RecyclerView.ViewHolder vh = null;
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());

        View v = inflater.inflate(R.layout.genre_row, parent, false);
        vh = new GenreViewHolder(v, c);

        return vh;

    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, int position) {
        dataCursor.moveToPosition(position);
        String genre = dataCursor.getString(dataCursor.getColumnIndex(MediaStore.Audio.Genres.NAME));
        int id = dataCursor.getInt(dataCursor.getColumnIndex(MediaStore.Audio.Genres._ID));

        ((GenreViewHolder) holder).name.setText(genre);
        ((GenreViewHolder) holder).genre = new Genre(genre, id);

    }

    public void setFragmentTransitionListener(FragmentTransitionListener fragmentTransitionListener) {
        this.fragmentTransitionListener = fragmentTransitionListener;
    }

    public Cursor swapCursor(Cursor cursor) {
        if (dataCursor == cursor) {
            return null;
        }
        Cursor oldCursor = dataCursor;
        this.dataCursor = cursor;
        if (cursor != null) {
            this.notifyDataSetChanged();
        }
        return oldCursor;
    }


    @Override
    public int getItemCount() {
        return (dataCursor == null) ? 0 : dataCursor.getCount();
    }

    class GenreViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        ImageView imageView;
        TextView name;
        Context ctx;
        CardView cardView;

        Genre genre;

        GenreViewHolder(View v, Context context) {
            super(v);
            this.ctx = context;

            name = (TextView) v.findViewById(R.id.name);
            imageView = (ImageView) v.findViewById(R.id.genre_art_card);
            cardView = (CardView) v.findViewById(R.id.genre_list_card);
            cardView.setOnClickListener(this);
        }

        private void fragmentJump(Genre genre) {
            scrollingFragment = new ScrollingFragment();
            Bundle bundle = new Bundle();
            bundle.putLong("ID", genre.getId());
            bundle.putString("Name", genre.getName());
            bundle.putString("Action", "Genres");
            scrollingFragment.setArguments(bundle);
            fragmentTransitionListener.onFragmentTransition(scrollingFragment);
            switchContent(R.id.drawerLayout, scrollingFragment);
        }

        public void switchContent(int id, Fragment fragment) {
            if (c == null)
                return;
            if (c instanceof MainActivity) {
                MainActivity mainActivity = (MainActivity) c;
                mainActivity.switchContent(id, fragment);
            }

        }


        @Override
        public void onClick(View v) {
            fragmentJump(genre);
        }
    }

}




