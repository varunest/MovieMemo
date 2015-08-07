package com.varunest.moviememo.UI;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.*;
import com.varunest.moviememo.Adapters.CustomCursorAdapter;
import com.varunest.moviememo.Adapters.DBAdapter;
import com.varunest.moviememo.Adapters.LazyAdapter;
import com.varunest.moviememo.MainActivity;
import com.varunest.moviememo.Parsers.XMLParser;
import com.varunest.moviememo.R;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * A placeholder fragment containing a simple view.
 */
public class PlaceholderFragment extends Fragment {
    /**
     * The fragment argument representing the section number for this
     * fragment.
     */
    private static final String ARG_SECTION_NUMBER = "section_number";
    GridView gv;
    ListView lv;

    /**
     * Returns a new instance of this fragment for the given section number.
     */
    public static PlaceholderFragment newInstance(int sectionNumber) {
        PlaceholderFragment fragment = new PlaceholderFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_SECTION_NUMBER, sectionNumber);
        fragment.setArguments(args);
        return fragment;
    }

    public PlaceholderFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = null;
        final ArrayList<HashMap<String, String>> menuItems = new ArrayList<HashMap<String, String>>();
        int section = getArguments().getInt(ARG_SECTION_NUMBER);
        switch (section) {
            case 1:
                if (MainActivity.string_query.equals("")) {

                    rootView = inflater.inflate(R.layout.fragment_pre_search,
                            container, false);
                } else {

                    rootView = inflater.inflate(R.layout.fragment_search,
                            container, false);

                    MainActivity.string_query = MainActivity.string_query.replaceAll(" ", "%20");

                    ConnectivityManager connMgr = (ConnectivityManager) getActivity()
                            .getSystemService(Context.CONNECTIVITY_SERVICE);
                    NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();

                    if (networkInfo != null && networkInfo.isConnected()) {

                        XMLParser parser = new XMLParser();
                        String xml = "";
                        xml = parser.getXmlFromUrl("http://www.omdbapi.com/?s="
                                + MainActivity.string_query + "&r=XML");
                        // getting dom object
                        MainActivity.string_query = "";
                        Log.e("hello", xml);

                        if (xml.equals("")) {
                            Log.e("hello", "connection timeout");
                            rootView = inflater.inflate(
                                    R.layout.fragment_pre_search, container,
                                    false);
                            ImageButton search = (ImageButton) rootView
                                    .findViewById(R.id.searchButton);
                            ImageButton nosearch = (ImageButton) rootView
                                    .findViewById(R.id.tryAgain);
                            search.setVisibility(View.GONE);
                            nosearch.setVisibility(View.VISIBLE);
                            Toast.makeText(
                                    getActivity().getApplicationContext(),
                                    "Connection Timed Out!", Toast.LENGTH_SHORT)
                                    .show();
                        } else {
                            Document doc = parser.getDomElement(xml);
                            String MOVIE = "Movie";
                            String MOVIE_NAME = "Title";
                            String YEAR = "Year";
                            String ID = "imdbID";
                            String TYPE = "Type";
                            NodeList nroot = doc.getElementsByTagName("root");
                            Element eroot = (Element) nroot.item(0);
                            String response = eroot.getAttribute("response");

                            if (response.equals("True")) {
                                NodeList nl = doc.getElementsByTagName(MOVIE);
                                // looping through all item nodes <item>
                                for (int i = 0; i < nl.getLength(); i++) {
                                    // creating new HashMap
                                    HashMap<String, String> map = new HashMap<String, String>();
                                    Element e = (Element) nl.item(i);
                                    String t = e.getAttribute(TYPE);

                                    if (t.equals("movie")) {
                                        map.put(MOVIE_NAME,
                                                e.getAttribute(MOVIE_NAME));
                                        map.put(YEAR, e.getAttribute(YEAR));

                                        map.put(ID, e.getAttribute(ID));
                                        map.put(TYPE, e.getAttribute(TYPE));
                                        // adding HashList to ArrayList
                                        menuItems.add(map);
                                    }

                                }
                                ListView list = (ListView) rootView
                                        .findViewById(R.id.search_list);

                                // Getting adapter by passing xml data ArrayList
                                LazyAdapter adapter = new LazyAdapter(this, menuItems);
                                list.setAdapter(adapter);

                                list.setOnItemClickListener(new AdapterView.OnItemClickListener() {

                                    public void onItemClick(
                                            AdapterView<?> parent, View view,
                                            int position, long id) {
                                        //
                                        //
                                        //
                                        HashMap<String, String> md = new HashMap<String, String>();
                                        md = menuItems.get(position);
                                        String imdb_id = md.get("imdbID");
                                        String detail_url = "http://www.omdbapi.com/?i="
                                                + imdb_id + "&r=xml&plot=full";
                                        Log.e("hello", detail_url);
                                        ConnectivityManager connMgr = (ConnectivityManager) getActivity()
                                                .getSystemService(
                                                        Context.CONNECTIVITY_SERVICE);
                                        NetworkInfo networkInfo = connMgr
                                                .getActiveNetworkInfo();
                                        if (networkInfo != null
                                                && networkInfo.isConnected()) {
                                            displayDetail dd = new displayDetail();
                                            dd.execute(detail_url);
                                        } else {
                                            Toast.makeText(
                                                    getActivity()
                                                            .getApplicationContext(),
                                                    "No Internet Connection!",
                                                    Toast.LENGTH_SHORT).show();
                                        }

                                    }
                                });
                            } else {
                                rootView = inflater.inflate(
                                        R.layout.fragment_pre_search,
                                        container, false);
                                ImageButton search = (ImageButton) rootView
                                        .findViewById(R.id.searchButton);
                                ImageButton nosearch = (ImageButton) rootView
                                        .findViewById(R.id.noSearchButton);
                                search.setVisibility(View.GONE);
                                nosearch.setVisibility(View.VISIBLE);

                            }
                            //
                        }

                    } else {
                        // display error
                        rootView = inflater.inflate(
                                R.layout.fragment_pre_search, container, false);
                        Toast.makeText(getActivity().getApplicationContext(),
                                "No Internet Connection!", Toast.LENGTH_SHORT)
                                .show();
                    }

                }
                break;
            // watch list page
            case 2:
                rootView = inflater.inflate(R.layout.fragment_watch_list,
                        container, false);
                lv = (ListView) rootView.findViewById(R.id.watch_list_lv);
                gv = (GridView) rootView.findViewById(R.id.watch_list);

                if (MainActivity.sharedpreferences.getBoolean(getString(R.string.view_type),
                        true)) {
                    lv.setVisibility(View.GONE);
                    gv.setVisibility(View.VISIBLE);
                    gv.setOnItemClickListener(new AdapterView.OnItemClickListener() {

                        @Override
                        public void onItemClick(AdapterView<?> parent,
                                                View view, int position, long id) {
                            Log.d("hello", "clicked on item: " + position + " "
                                    + id);
                            showDetails(id, 1);
                        }

                    });

                    gv.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {

                        @Override
                        public boolean onItemLongClick(AdapterView<?> parent,
                                                       View view, int position, long id) {
                            // TODO Auto-generated method stub
                            Cursor sharecursor;
                            sharecursor = MainActivity.myDb.getRowByCursorID(id);
                            if (sharecursor.moveToFirst()) {
                                String Data = "Checkout the movie :\n\n";
                                String title = sharecursor
                                        .getString(MainActivity.myDb.COL_TITLE) + "\n";
                                String starred = "Starred by : "
                                        + sharecursor
                                        .getString(MainActivity.myDb.COL_ACTORS)
                                        + "\n";
                                String rated = "Rated : "
                                        + sharecursor
                                        .getString(MainActivity.myDb.COL_RATING)
                                        + "\n";
                                String url = "Imdb Link : http://www.imdb.com/title/"
                                        + sharecursor.getString(MainActivity.myDb.COL_ID)
                                        + "/?ref_=nv_sr_1";
                                Intent sendIntent = new Intent();
                                sendIntent.setAction(Intent.ACTION_SEND);
                                sendIntent.putExtra(Intent.EXTRA_TEXT, Data
                                        + title + starred + rated + url);
                                sendIntent.setType("text/plain");
                                startActivity(Intent.createChooser(sendIntent,
                                        getResources()
                                                .getText(R.string.send_to)));
                            }

                            return false;
                        }

                    });
                    final ImageView img = (ImageView) rootView
                            .findViewById(R.id.empty_image);
                    new Handler().post(new Runnable() {
                        @Override
                        public void run() {
                            Cursor cursor = null;
                            String sort = MainActivity.sharedpreferences.getString(
                                    getString(R.string.sort), "name");
                            Log.e("database", "watch list " + sort);
                            cursor = MainActivity.myDb.getAllWatchRows(sort);
                            if (cursor.moveToFirst()) {
                                img.setVisibility(View.GONE);
                                CustomCursorAdapter customAdapter = new CustomCursorAdapter(
                                        getActivity(), cursor, false);
                                gv.setAdapter(customAdapter);
                            } else {
                                // show empty watch list layout
                                img.setVisibility(View.VISIBLE);
                            }
                        }
                    });
                } else {

                    gv.setVisibility(View.GONE);
                    lv.setVisibility(View.VISIBLE);
                    lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {

                        @Override
                        public void onItemClick(AdapterView<?> parent,
                                                View view, int position, long id) {
                            Log.d("hello", "clicked on item: " + position + " "
                                    + id);
                            showDetails(id, 1);
                        }
                    });

                    lv.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {

                        @Override
                        public boolean onItemLongClick(AdapterView<?> parent,
                                                       View view, int position, long id) {
                            // TODO Auto-generated method stub
                            Cursor sharecursor;
                            sharecursor = MainActivity.myDb.getRowByCursorID(id);
                            if (sharecursor.moveToFirst()) {
                                String Data = "Checkout the movie :\n\n";
                                String title = sharecursor
                                        .getString(MainActivity.myDb.COL_TITLE) + "\n";
                                String starred = "Starred by : "
                                        + sharecursor
                                        .getString(MainActivity.myDb.COL_ACTORS)
                                        + "\n";
                                String rated = "Rated : "
                                        + sharecursor
                                        .getString(MainActivity.myDb.COL_RATING)
                                        + "\n";
                                String url = "Imdb Link : http://www.imdb.com/title/"
                                        + sharecursor.getString(MainActivity.myDb.COL_ID)
                                        + "/?ref_=nv_sr_1";
                                Intent sendIntent = new Intent();
                                sendIntent.setAction(Intent.ACTION_SEND);
                                sendIntent.putExtra(Intent.EXTRA_TEXT, Data
                                        + title + starred + rated + url);
                                sendIntent.setType("text/plain");
                                startActivity(Intent.createChooser(sendIntent,
                                        getResources()
                                                .getText(R.string.send_to)));
                            }

                            return false;
                        }

                    });
                    final ImageView img = (ImageView) rootView
                            .findViewById(R.id.empty_image);
                    new Handler().post(new Runnable() {
                        @Override
                        public void run() {
                            Cursor cursor = null;
                            String sort = MainActivity.sharedpreferences.getString(
                                    getString(R.string.sort), "name");
                            Log.e("database", "watch list " + sort);
                            cursor = MainActivity.myDb.getAllWatchRows(sort);
                            if (cursor.moveToFirst()) {
                                img.setVisibility(View.GONE);
                                CustomCursorAdapterListView customAdapterlv = new CustomCursorAdapterListView(
                                        getActivity(), cursor, false);
                                lv.setAdapter(customAdapterlv);
                            } else {
                                // show empty watch list layout
                                img.setVisibility(View.VISIBLE);
                            }
                        }
                    });
                }

                break;
            // watched page
            case 3:
                rootView = inflater.inflate(R.layout.fragment_watch_list,
                        container, false);
                lv = (ListView) rootView.findViewById(R.id.watch_list_lv);
                gv = (GridView) rootView.findViewById(R.id.watch_list);

                if (MainActivity.sharedpreferences.getBoolean(getString(R.string.view_type),
                        true)) {
                    lv.setVisibility(View.GONE);
                    gv.setVisibility(View.VISIBLE);
                    gv.setOnItemClickListener(new AdapterView.OnItemClickListener() {

                        @Override
                        public void onItemClick(AdapterView<?> parent,
                                                View view, int position, long id) {
                            Log.d("hello", "clicked on item: " + position + " "
                                    + id);
                            showDetails(id, 2);
                        }
                    });
                    gv.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {

                        @Override
                        public boolean onItemLongClick(AdapterView<?> parent,
                                                       View view, int position, long id) {
                            // TODO Auto-generated method stub
                            Cursor sharecursor;
                            sharecursor = MainActivity.myDb.getRowByCursorID(id);
                            if (sharecursor.moveToFirst()) {
                                String Data = "Checkout the movie :\n\n";
                                String title = sharecursor
                                        .getString(MainActivity.myDb.COL_TITLE) + "\n";
                                String starred = "Starred by : "
                                        + sharecursor
                                        .getString(MainActivity.myDb.COL_ACTORS)
                                        + "\n";
                                String rated = "Rated : "
                                        + sharecursor
                                        .getString(MainActivity.myDb.COL_RATING)
                                        + "\n";
                                String url = "Imdb Link : http://www.imdb.com/title/"
                                        + sharecursor.getString(MainActivity.myDb.COL_ID)
                                        + "/?ref_=nv_sr_1";
                                Intent sendIntent = new Intent();
                                sendIntent.setAction(Intent.ACTION_SEND);
                                sendIntent.putExtra(Intent.EXTRA_TEXT, Data
                                        + title + starred + rated + url);
                                sendIntent.setType("text/plain");
                                startActivity(Intent.createChooser(sendIntent,
                                        getResources()
                                                .getText(R.string.send_to)));
                            }

                            return false;
                        }

                    });
                    final ImageView img2 = (ImageView) rootView
                            .findViewById(R.id.empty_image);
                    new Handler().post(new Runnable() {
                        @Override
                        public void run() {
                            Cursor cursor = null;
                            String sort = MainActivity.sharedpreferences.getString(
                                    getString(R.string.sort), "name");
                            Log.e("database", "watch list " + sort);
                            cursor = MainActivity.myDb.getAllWatchedRows(sort);
                            if (cursor.moveToFirst()) {
                                img2.setVisibility(View.GONE);
                                CustomCursorAdapter customAdapter2 = new CustomCursorAdapter(
                                        getActivity(), cursor, false);
                                gv.setAdapter(customAdapter2);
                            } else {
                                // show empty watch list layout
                                img2.setVisibility(View.VISIBLE);
                            }
                        }
                    });
                } else {

                    gv.setVisibility(View.GONE);
                    lv.setVisibility(View.VISIBLE);
                    lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {

                        @Override
                        public void onItemClick(AdapterView<?> parent,
                                                View view, int position, long id) {
                            Log.d("hello", "clicked on item: " + position + " "
                                    + id);
                            showDetails(id, 2);
                        }
                    });
                    lv.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {

                        @Override
                        public boolean onItemLongClick(AdapterView<?> parent,
                                                       View view, int position, long id) {
                            // TODO Auto-generated method stub
                            Cursor sharecursor;
                            sharecursor = MainActivity.myDb.getRowByCursorID(id);
                            if (sharecursor.moveToFirst()) {
                                String Data = "Checkout the movie :\n\n";
                                String title = sharecursor
                                        .getString(MainActivity.myDb.COL_TITLE) + "\n";
                                String starred = "Starred by : "
                                        + sharecursor
                                        .getString(MainActivity.myDb.COL_ACTORS)
                                        + "\n";
                                String rated = "Rated : "
                                        + sharecursor
                                        .getString(MainActivity.myDb.COL_RATING)
                                        + "\n";
                                String url = "Imdb Link : http://www.imdb.com/title/"
                                        + sharecursor.getString(MainActivity.myDb.COL_ID)
                                        + "/?ref_=nv_sr_1";
                                Intent sendIntent = new Intent();
                                sendIntent.setAction(Intent.ACTION_SEND);
                                sendIntent.putExtra(Intent.EXTRA_TEXT, Data
                                        + title + starred + rated + url);
                                sendIntent.setType("text/plain");
                                startActivity(Intent.createChooser(sendIntent,
                                        getResources()
                                                .getText(R.string.send_to)));
                            }

                            return false;
                        }

                    });
                    final ImageView img = (ImageView) rootView
                            .findViewById(R.id.empty_image);
                    new Handler().post(new Runnable() {
                        @Override
                        public void run() {
                            Cursor cursor = null;
                            String sort = MainActivity.sharedpreferences.getString(
                                    getString(R.string.sort), "name");
                            Log.e("database", "watch list " + sort);
                            cursor = MainActivity.myDb.getAllWatchedRows(sort);
                            if (cursor.moveToFirst()) {
                                img.setVisibility(View.GONE);
                                CustomCursorAdapterListView customAdapterlv2 = new CustomCursorAdapterListView(
                                        getActivity(), cursor, false);
                                lv.setAdapter(customAdapterlv2);
                            } else {
                                // show empty watch list layout
                                img.setVisibility(View.VISIBLE);
                            }
                        }
                    });
                }

                break;
        }
        return rootView;
    }

    protected void showDetails(final long id, final int page) {
        // TODO Auto-generated method stub
        Dialog detail_dialog = new Dialog(getActivity(),
                R.style.translucent);
        detail_dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        detail_dialog.setContentView(R.layout.detail_layout);
        // Setting movie name
        TextView movieName = (TextView) detail_dialog
                .findViewById(R.id.movieName);
        final Cursor cdetail = MainActivity.myDb.getRowByCursorID(id);
        movieName.setText(cdetail.getString(MainActivity.myDb.COL_TITLE));
        // setting details
        final RatingBar ratingBar = (RatingBar) detail_dialog
                .findViewById(R.id.ratingBar);
        Float urating = cdetail.getFloat(MainActivity.myDb.COL_USER_RATING);
        ratingBar.setRating(urating);
        ratingBar
                .setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
                    public void onRatingChanged(RatingBar ratingBar,
                                                float rating, boolean fromUser) {
                        MainActivity.myDb.updateUrating(rating, id);
                    }
                });
        TextView smallDetail = (TextView) detail_dialog
                .findViewById(R.id.smallDetails);
        String genre = cdetail.getString(MainActivity.myDb.COL_GENRE);
        genre = genre.replaceAll(",", " |");
        String s_detail = cdetail.getString(MainActivity.myDb.COL_RUNTIME) + " - "
                + genre + " - " + cdetail.getString(MainActivity.myDb.COL_RELEASED)
                + " (" + cdetail.getString(MainActivity.myDb.COL_COUNTRY) + ")";
        smallDetail.setText(s_detail);
        String poster = cdetail.getString(DBAdapter.COL_POSTER);
        ImageView img = (ImageView) detail_dialog.findViewById(R.id.poster);
        String location;
        if (poster.equals("yes")) {
            if (android.os.Environment.getExternalStorageState().equals(
                    android.os.Environment.MEDIA_MOUNTED)) {
                location = Environment.getExternalStorageDirectory()
                        + File.separator + "MovieMemo" + File.separator
                        + "Posters" + File.separator;
                location = location + cdetail.getString(DBAdapter.COL_ID)
                        + ".jpg";
                Bitmap bmp = BitmapFactory.decodeFile(location);
                img.setImageBitmap(bmp);
            }

            else {
                img.setBackgroundResource(R.drawable.demo);
            }
        }
        // setting rating
        TextView rateValue = (TextView) detail_dialog
                .findViewById(R.id.rateValue);
        rateValue.setText(cdetail.getString(MainActivity.myDb.COL_RATING));

        TextView director = (TextView) detail_dialog
                .findViewById(R.id.director);
        TextView writer = (TextView) detail_dialog
                .findViewById(R.id.writer);
        TextView stars = (TextView) detail_dialog.findViewById(R.id.star);
        TextView awards = (TextView) detail_dialog.findViewById(R.id.award);
        TextView plot = (TextView) detail_dialog
                .findViewById(R.id.plotText);
        director.setText(cdetail.getString(MainActivity.myDb.COL_DIRECTOR));
        writer.setText(cdetail.getString(MainActivity.myDb.COL_WRITER));
        stars.setText(cdetail.getString(MainActivity.myDb.COL_ACTORS));
        awards.setText(cdetail.getString(MainActivity.myDb.COL_AWARDS));
        plot.setText(cdetail.getString(MainActivity.myDb.COL_PLOT));
        ImageButton watchlist = (ImageButton) detail_dialog
                .findViewById(R.id.watchList);

        if (cdetail.moveToFirst()) {
            String watch = cdetail.getString(MainActivity.myDb.COL_WATCH);
            if (watch.equals("yes")) {
                watchlist
                        .setBackgroundResource(R.drawable.unwatch_selector);
            } else if (watch.equals("no")) {
                watchlist.setBackgroundResource(R.drawable.watch_selector);

            }
        } else {
            watchlist.setBackgroundResource(R.drawable.watch_selector);
        }
        watchlist.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MainActivity.myDb.toggleWatch(id, ratingBar.getRating());
                MainActivity.mViewPager.setAdapter(MainActivity.mSectionsPagerAdapter);
                MainActivity.mViewPager.setCurrentItem(page);

                Cursor c = MainActivity.myDb.getRowByCursorID(id);
                if (c.moveToFirst()) {
                    String watch = c.getString(MainActivity.myDb.COL_WATCH);
                    Log.e("hello", watch);
                    if (watch.equals("yes")) {
                        v.setBackgroundResource(R.drawable.unwatch_selector);
                    } else if (watch.equals("no")) {
                        v.setBackgroundResource(R.drawable.watch_selector);

                    }
                } else {
                    v.setBackgroundResource(R.drawable.watch_selector);
                }
                c.close();
            }
        });// watch list
        ImageButton watchedlist = (ImageButton) detail_dialog
                .findViewById(R.id.watchedList);
        if (cdetail.moveToFirst()) {
            String watched = cdetail.getString(MainActivity.myDb.COL_WATCHED);
            if (watched.equals("yes")) {
                watchedlist
                        .setBackgroundResource(R.drawable.watched_selector);
            } else if (watched.equals("no")) {
                watchedlist
                        .setBackgroundResource(R.drawable.unwatched_selector);

            }

        } else {
            watchedlist
                    .setBackgroundResource(R.drawable.unwatched_selector);
        }

        watchedlist.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // adding to watched list
                MainActivity.myDb.toggleWatched(id,
                        ratingBar.getRating());
                MainActivity.mViewPager.setAdapter(MainActivity.mSectionsPagerAdapter);
                MainActivity.mViewPager.setCurrentItem(page);
                Cursor cwatched = MainActivity.myDb.getRowByCursorID(id);
                if (cwatched.moveToFirst()) {
                    String watched = cwatched.getString(MainActivity.myDb.COL_WATCHED);
                    Log.e("database", watched);
                    if (watched.equals("yes")) {
                        v.setBackgroundResource(R.drawable.watched_selector);
                    } else if (watched.equals("no")) {
                        v.setBackgroundResource(R.drawable.unwatched_selector);

                    }
                } else {
                    v.setBackgroundResource(R.drawable.unwatched_selector);
                }
                cwatched.close();

            }
        });// watched list
        cdetail.close();
        detail_dialog.show();

    }

    @Override
    public View getView() {
        // TODO Auto-generated method stub
        return super.getView();
    }

    // function that returns bitmap image from url
    public Bitmap getBitmapFromURL(String imageUrl) {
        try {
            URL url = new URL(imageUrl);
            HttpURLConnection connection = (HttpURLConnection) url
                    .openConnection();
            connection.setDoInput(true);
            connection.connect();
            InputStream input = connection.getInputStream();
            Bitmap myBitmap = BitmapFactory.decodeStream(input);
            return myBitmap;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    // async task
    private class displayDetail extends AsyncTask<String, Integer, Void>
            implements DialogInterface.OnDismissListener {
        Document doc;
        Element movie_element;
        Bitmap img = null;
        NodeList node1;
        String poster_url;
        ProgressDialog progressDialog = null;
        String xml = "";

        public displayDetail() {
            // rootView = v;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            // code here
            progressDialog = new ProgressDialog(getActivity(),
                    R.style.progressdialog);
            progressDialog.setMessage("GETTING DETAILS");
            progressDialog.setCancelable(true);
            progressDialog.setOnDismissListener(this);
            progressDialog.show();
        }

        @Override
        public void onDismiss(DialogInterface dialog) {
            this.cancel(true);
        }

        @Override
        protected Void doInBackground(String... params) {
            String URL = params[0];
            XMLParser parser = new XMLParser(); // the parser create as seen
            // in the Gist from GitHub
            xml = parser.getXmlFromUrl(URL); // getting XML from URL
            doc = parser.getDomElement(xml); // getting DOM element
            node1 = doc.getElementsByTagName("movie");
            movie_element = (Element) node1.item(0);
            poster_url = movie_element.getAttribute("poster");
            if (!poster_url.equals("")) {
                img = getBitmapFromURL(poster_url);
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            progressDialog.dismiss();
            Dialog detail_dialog = new Dialog(getActivity(),
                    R.style.translucent);
            detail_dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            detail_dialog.setContentView(R.layout.detail_layout);
            if (img != null) {
                // setting movie poster
                ImageView poster = (ImageView) detail_dialog
                        .findViewById(R.id.poster);
                poster.setImageBitmap(img);
            }
            // Setting movie name
            TextView movieName = (TextView) detail_dialog
                    .findViewById(R.id.movieName);
            movieName.setText(movie_element.getAttribute("title"));
            // setting details
            TextView smallDetail = (TextView) detail_dialog
                    .findViewById(R.id.smallDetails);
            String genre = movie_element.getAttribute("genre");
            genre = genre.replaceAll(",", " |");
            String s_detail = movie_element.getAttribute("runtime") + " - "
                    + genre + " - "
                    + movie_element.getAttribute("released") + " ("
                    + movie_element.getAttribute("country") + ")";
            smallDetail.setText(s_detail);

            // setting rating
            TextView rateValue = (TextView) detail_dialog
                    .findViewById(R.id.rateValue);
            rateValue.setText(movie_element.getAttribute("imdbRating"));

            TextView director = (TextView) detail_dialog
                    .findViewById(R.id.director);
            TextView writer = (TextView) detail_dialog
                    .findViewById(R.id.writer);
            TextView stars = (TextView) detail_dialog
                    .findViewById(R.id.star);
            TextView awards = (TextView) detail_dialog
                    .findViewById(R.id.award);
            TextView plot = (TextView) detail_dialog
                    .findViewById(R.id.plotText);

            director.setText(movie_element.getAttribute("director"));
            writer.setText(movie_element.getAttribute("writer"));
            stars.setText(movie_element.getAttribute("actors"));
            awards.setText(movie_element.getAttribute("awards"));
            plot.setText(movie_element.getAttribute("plot"));
            ImageButton watchlist = (ImageButton) detail_dialog
                    .findViewById(R.id.watchList);

            Cursor c = MainActivity.myDb.getRow(movie_element.getAttribute("imdbID"));
            final RatingBar ratingBar = (RatingBar) detail_dialog
                    .findViewById(R.id.ratingBar);

            if (c.moveToFirst()) {
                Float urating = c.getFloat(MainActivity.myDb.COL_USER_RATING);
                ratingBar.setRating(urating);
                final long id = c.getLong(MainActivity.myDb.COL_CURSOR_ID);
                ratingBar
                        .setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
                            public void onRatingChanged(
                                    RatingBar ratingBar, float rating,
                                    boolean fromUser) {
                                MainActivity.myDb.updateUrating(rating,
                                        id);

                            }
                        });
                String watch = c.getString(MainActivity.myDb.COL_WATCH);
                Log.e("hello", watch);
                if (watch.equals("yes")) {
                    watchlist
                            .setBackgroundResource(R.drawable.unwatch_selector);
                } else if (watch.equals("no")) {
                    watchlist
                            .setBackgroundResource(R.drawable.watch_selector);

                }
            } else {
                watchlist.setBackgroundResource(R.drawable.watch_selector);
            }
            c.close();

            watchlist.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String id = movie_element.getAttribute("imdbID");
                    String title = movie_element.getAttribute("title");
                    String year = movie_element.getAttribute("year");
                    String genre = movie_element.getAttribute("genre");
                    String released = movie_element
                            .getAttribute("released");
                    String runtime = movie_element.getAttribute("runtime");
                    String director = movie_element
                            .getAttribute("director");
                    String writer = movie_element.getAttribute("writer");
                    String actors = movie_element.getAttribute("actors");
                    String plot = movie_element.getAttribute("plot");
                    String country = movie_element.getAttribute("country");
                    String awards = movie_element.getAttribute("awards");
                    String rating = movie_element
                            .getAttribute("imdbRating");
                    String poster = "no";
                    Float urating = ratingBar.getRating();
                    if (img != null) {
                        poster = downloadImage(img, id);
                    }

                    MainActivity.myDb.insertRow(0, id, title, year, genre, released,
                            runtime, director, writer, actors, plot,
                            country, awards, rating, poster, urating);
                    MainActivity.mViewPager.setAdapter(MainActivity.mSectionsPagerAdapter);
                    Cursor c = MainActivity.myDb.getRow(movie_element
                            .getAttribute("imdbID"));
                    if (c.moveToFirst()) {
                        String watch = c.getString(MainActivity.myDb.COL_WATCH);
                        Log.e("hello", watch);
                        if (watch.equals("yes")) {
                            v.setBackgroundResource(R.drawable.unwatch_selector);
                        } else if (watch.equals("no")) {
                            v.setBackgroundResource(R.drawable.watch_selector);

                        }
                    } else {
                        v.setBackgroundResource(R.drawable.watch_selector);
                    }
                    c.close();
                }
            });// watch list
            ImageButton watchedlist = (ImageButton) detail_dialog
                    .findViewById(R.id.watchedList);
            Cursor d = MainActivity.myDb.getRow(movie_element.getAttribute("imdbID"));
            if (d.moveToFirst()) {
                String watched = d.getString(MainActivity.myDb.COL_WATCHED);
                if (watched.equals("yes")) {
                    watchedlist
                            .setBackgroundResource(R.drawable.watched_selector);
                } else if (watched.equals("no")) {
                    watchedlist
                            .setBackgroundResource(R.drawable.unwatched_selector);

                }

            } else {
                watchedlist
                        .setBackgroundResource(R.drawable.unwatched_selector);
            }
            d.close();

            watchedlist.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // adding to watched list
                    String id = movie_element.getAttribute("imdbID");
                    String title = movie_element.getAttribute("title");
                    String year = movie_element.getAttribute("year");
                    String genre = movie_element.getAttribute("genre");
                    String released = movie_element
                            .getAttribute("released");
                    String runtime = movie_element.getAttribute("runtime");
                    String director = movie_element
                            .getAttribute("director");
                    String writer = movie_element.getAttribute("writer");
                    String actors = movie_element.getAttribute("actors");
                    String plot = movie_element.getAttribute("plot");
                    String country = movie_element.getAttribute("country");
                    String awards = movie_element.getAttribute("awards");
                    String rating = movie_element
                            .getAttribute("imdbRating");
                    String poster = "no";
                    if (img != null) {
                        poster = downloadImage(img, id);
                    }
                    Float ratevalue =ratingBar.getRating();

                    MainActivity.myDb.insertRow(1, id, title, year, genre, released,
                            runtime, director, writer, actors, plot,
                            country, awards, rating, poster, ratevalue);

                    MainActivity.mViewPager.setAdapter(MainActivity.mSectionsPagerAdapter);

                    Cursor c = MainActivity.myDb.getRow(movie_element
                            .getAttribute("imdbID"));
                    if (c.moveToFirst()) {
                        String watched = c.getString(MainActivity.myDb.COL_WATCHED);
                        String s = Integer.toString(c.getInt(16));
                        Log.e("database", s);
                        if (watched.equals("yes")) {
                            v.setBackgroundResource(R.drawable.watched_selector);
                        } else if (watched.equals("no")) {
                            v.setBackgroundResource(R.drawable.unwatched_selector);

                        }
                    } else {
                        v.setBackgroundResource(R.drawable.unwatched_selector);
                    }
                    c.close();
                }
            });// watched list
            detail_dialog.show();
        }

        protected String downloadImage(Bitmap bitmap, String id) {
            // TODO Auto-generated method stub
            String poster = "no";
            if (android.os.Environment.getExternalStorageState().equals(
                    android.os.Environment.MEDIA_MOUNTED)) {
                File newD = new File(
                        Environment.getExternalStorageDirectory()
                                + File.separator + "MovieMemo"
                                + File.separator + "Posters");
                if (!newD.exists()) {
                    newD.mkdirs();
                    File noMedia =  new File(newD.getAbsolutePath() + "/.nomedia");
                    try {
                        noMedia.createNewFile();
                    } catch (IOException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                }

                ByteArrayOutputStream bytes = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.JPEG, 80, bytes);

                // you can create a new file name "test.jpg" in sdcard
                // folder.
                File f = new File(newD + File.separator + id + ".jpg");
                try {
                    f.createNewFile();

                    // write the bytes in file
                    FileOutputStream fo = new FileOutputStream(f);
                    fo.write(bytes.toByteArray());

                    // remember close de FileOutput
                    fo.close();
                    poster = "yes";
                } catch (Exception e) {

                }
            }

            return poster;
        }

    }// detail async ends here
}// placeholderfragment ends here
