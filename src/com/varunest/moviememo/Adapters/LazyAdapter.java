package com.varunest.moviememo.Adapters;

import android.app.Activity;
import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import com.varunest.moviememo.MainActivity;
import com.varunest.moviememo.R;
import com.varunest.moviememo.UI.PlaceholderFragment;

import java.util.ArrayList;
import java.util.HashMap;

public class LazyAdapter extends BaseAdapter {

    private Activity activity;
    private ArrayList<HashMap<String, String>> data;
    private static LayoutInflater inflater = null;

    public LazyAdapter(PlaceholderFragment a,
                       ArrayList<HashMap<String, String>> d) {
        activity = a.getActivity();
        data = d;
        inflater = (LayoutInflater) activity
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    public int getCount() {
        return data.size();
    }

    public Object getItem(int position) {
        return position;
    }

    public long getItemId(int position) {
        return position;
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        View vi = convertView;
        if (convertView == null)
            vi = inflater.inflate(R.layout.list_row, null);
        ImageView icon = (ImageView) vi.findViewById(R.id.filmIcon);
        if (position % 2 == 0) {
            icon.setImageResource(R.drawable.search_icon_black);
        } else {
            icon.setImageResource(R.drawable.search_icon);
        }
        TextView title = (TextView) vi.findViewById(R.id.movieTitle); // title
        TextView year = (TextView) vi.findViewById(R.id.year); // year of
        // the movie

        HashMap<String, String> movie = new HashMap<String, String>();
        movie = data.get(position);
        String id = movie.get("imdbID");
        Cursor c = MainActivity.myDb.getRow(id);
        TextView watched = (TextView) vi.findViewById(R.id.watched_text);
        ImageView watch = (ImageView) vi.findViewById(R.id.star_watch);
        if (c.moveToFirst()) {
            String w = c.getString(MainActivity.myDb.COL_WATCHED);
            String s = c.getString(MainActivity.myDb.COL_WATCH);
            if (w.equals("yes")) {
                watched.setText("Watched");
            }
            else{
                watched.setText("");
            }
            if (s.equals("yes")) {
                watch.setVisibility(View.VISIBLE);
            }
            else{
                watch.setVisibility(View.GONE);
            }

        } else {
            watched.setText("");
            watch.setVisibility(View.GONE);

        }
        c.close();

        // Setting all values in listview
        String mt = movie.get("Title");
        mt = mt.replaceAll("&amp;", "&");
        title.setText(mt);
        year.setText(movie.get("Year"));

        return vi;
    }
}// lazyadapter ends here
