package com.varunest.moviememo;

import com.crashlytics.android.Crashlytics;
import io.fabric.sdk.android.Fabric;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.CoreConnectionPNames;
import org.apache.http.params.HttpParams;
import org.apache.http.util.EntityUtils;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.DialogInterface.OnDismissListener;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.StrictMode;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.ContextThemeWrapper;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.WindowManager.LayoutParams;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RatingBar;
import android.widget.RatingBar.OnRatingBarChangeListener;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;
import android.widget.Toast;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;

public class MainActivity extends ActionBarActivity implements
		ActionBar.TabListener {

	/**
	 * The {@link android.support.v4.view.PagerAdapter} that will provide
	 * fragments for each of the sections. We use a {@link FragmentPagerAdapter}
	 * derivative, which will keep every loaded fragment in memory. If this
	 * becomes too memory intensive, it may be best to switch to a
	 * {@link android.support.v4.app.FragmentStatePagerAdapter}.
	 */
	static SectionsPagerAdapter mSectionsPagerAdapter;
	static DBAdapter myDb;
	static SharedPreferences sharedpreferences;
	/**
	 * The {@link ViewPager} that will host the section contents.
	 */
	static ViewPager mViewPager;
	static String string_query = "";
	EditText query;

	// on search button click
	public void onSearch(View v) {
		final Dialog dsearch = new Dialog(this);
		dsearch.requestWindowFeature(Window.FEATURE_NO_TITLE);
		dsearch.setContentView(R.layout.search_dialog);
		dsearch.show();
		dsearch.getWindow().setSoftInputMode(
				LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
		query = (EditText) dsearch.findViewById(R.id.query);
		ImageView searchimg = (ImageView) dsearch
				.findViewById(R.id.search_image_query);
		searchimg.setOnTouchListener(new OnTouchListener() {

			@Override
			public boolean onTouch(View v, MotionEvent event) {
				// TODO Auto-generated method stub
				mViewPager.setCurrentItem(0);
				string_query = query.getText().toString();
				InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
				imm.hideSoftInputFromWindow(query.getWindowToken(), 0);
				mViewPager.setAdapter(mSectionsPagerAdapter);
				dsearch.dismiss();
				return true;
			}

		});

		query.setOnEditorActionListener(new OnEditorActionListener() {
			@Override
			public boolean onEditorAction(TextView v, int actionId,
					KeyEvent event) {

				if (actionId == EditorInfo.IME_ACTION_DONE) {
					mViewPager.setCurrentItem(0);
					string_query = query.getText().toString();
					InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
					imm.hideSoftInputFromWindow(query.getWindowToken(), 0);
					mViewPager.setAdapter(mSectionsPagerAdapter);
					dsearch.dismiss();
					return true;
				}
				return false;
			}
		});

		return;
	}

	private void openDB() {
		myDb = new DBAdapter(this);
		myDb.open();
	}

	@Override
	protected void onPause() {
		super.onPause();
	}

	@Override
	protected void onResume() {
		super.onResume();
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();

		myDb.close();
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Fabric.with(this, new Crashlytics());
		setContentView(R.layout.activity_main);
		openDB();
		sharedpreferences = getPreferences(Context.MODE_PRIVATE);
		// Set up the action bar.
		final ActionBar actionBar = getSupportActionBar();
		actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);

		// Create the adapter that will return a fragment for each of the three
		// primary sections of the activity.
		mSectionsPagerAdapter = new SectionsPagerAdapter(
				getSupportFragmentManager());

		// Set up the ViewPager with the sections adapter.
		mViewPager = (ViewPager) findViewById(R.id.pager);
		mViewPager.setPageTransformer(true, new DepthPageTransformer());
		mViewPager.setAdapter(mSectionsPagerAdapter);

		// When swiping between different sections, select the corresponding
		// tab. We can also use ActionBar.Tab#select() to do this if we have
		// a reference to the Tab.
		mViewPager
				.setOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
					@Override
					public void onPageSelected(int position) {
						actionBar.setSelectedNavigationItem(position);
					}
				});

		// For each of the sections in the app, add a tab to the action bar.
		for (int i = 0; i < mSectionsPagerAdapter.getCount(); i++) {
			// Create a tab with text corresponding to the page title defined by
			// the adapter. Also specify this Activity object, which implements
			// the TabListener interface, as the callback (listener) for when
			// this tab is selected.
			actionBar.addTab(actionBar.newTab()
					.setText(mSectionsPagerAdapter.getPageTitle(i))
					.setTabListener(this));

		}

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {

		getMenuInflater().inflate(R.menu.search_menu, menu);
		MenuItem mi = menu.findItem(R.id.change_view);
		boolean gridview = sharedpreferences.getBoolean(
				getString(R.string.view_type), true);
		if (gridview) {
			mi.setIcon(R.drawable.ic_action_view_as_list);
		} else {
			mi.setIcon(R.drawable.ic_action_view_as_grid);
		}
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.

		switch (item.getItemId()) {
		
		case R.id.about:
			Intent i= new Intent(this,about.class);
			startActivity(i);
			return true;

		case R.id.change_view:

			boolean gridview = sharedpreferences.getBoolean(
					getString(R.string.view_type), true);
			if (gridview) {
				SharedPreferences.Editor editor = sharedpreferences.edit();
				editor.putBoolean(getString(R.string.view_type), false);
				editor.commit();
				item.setIcon(R.drawable.ic_action_view_as_grid);

			} else {
				SharedPreferences.Editor editor = sharedpreferences.edit();
				editor.putBoolean(getString(R.string.view_type), true);
				editor.commit();
				item.setIcon(R.drawable.ic_action_view_as_list);

			}
			int page = mViewPager.getCurrentItem();
			mViewPager.setAdapter(mSectionsPagerAdapter);
			mViewPager.setCurrentItem(page);
			return true;

		case R.id.sort:
			String sort = sharedpreferences.getString(getString(R.string.sort),
					"name");
			Log.e("database", sort);
			int checkedItem = 0;
			if (sort.equals("name")) {
				checkedItem = 0;
			} else if (sort.equals("added")) {
				checkedItem = 1;
			} else if (sort.equals("imdbrating")) {
				checkedItem = 2;
			} else if (sort.equals("urating")) {
				checkedItem = 3;
			}
			LayoutInflater inflater = getLayoutInflater();
			ContextThemeWrapper ctw = new ContextThemeWrapper(this,
					R.style.Theme_Dialog_Translucent);
			AlertDialog.Builder sort_dialog = new AlertDialog.Builder(ctw);
			View view1 = inflater.inflate(R.layout.custom_title, null);
			sort_dialog.setCustomTitle(view1);

			// Set the dialog title
			// sort_dialog.setTitle(R.string.sort_title).
			// Specify the list array, the items to be selected by default (null
			// for none),
			// and the listener through which to receive callbacks when items
			// are selected
			sort_dialog.setSingleChoiceItems(R.array.sortArray, checkedItem,
					new OnClickListener() {

						@Override
						public void onClick(DialogInterface dialog, int which) {
							SharedPreferences.Editor editor = sharedpreferences
									.edit();
							String choice = "name";
							// TODO Auto-generated method stub
							if (which == 0) {
								editor.putString(getString(R.string.sort),
										choice);

							} else if (which == 1) {
								choice = "added";
								editor.putString(getString(R.string.sort),
										choice);
							} else if (which == 2) {
								choice = "imdbrating";
								editor.putString(getString(R.string.sort),
										choice);
							} else if (which == 3) {
								choice = "urating";
								editor.putString(getString(R.string.sort),
										choice);
							}
							editor.commit();
							int i = mViewPager.getCurrentItem();
							mViewPager.setAdapter(mSectionsPagerAdapter);
							mViewPager.setCurrentItem(i);
							dialog.cancel();
						}

					});
			sort_dialog.show();
			return true;
		case R.id.backup:

			AlertDialog.Builder builder = new AlertDialog.Builder(this);

			builder.setMessage("Do you want to overwrite the previous Backup ?");

			builder.setPositiveButton("YES",
					new DialogInterface.OnClickListener() {

						public void onClick(DialogInterface dialog, int which) {
							// Do nothing but close the dialog
							backup();
							dialog.dismiss();
						}

					});

			builder.setNegativeButton("NO",
					new DialogInterface.OnClickListener() {

						@Override
						public void onClick(DialogInterface dialog, int which) {
							// Do nothing
							dialog.dismiss();
						}
					});
			builder.show();
			return true;

		case R.id.restore:
			AlertDialog.Builder builder2 = new AlertDialog.Builder(this);

			builder2.setMessage("All your current data will be deleted. Are you Sure?");

			builder2.setPositiveButton("YES",
					new DialogInterface.OnClickListener() {

						public void onClick(DialogInterface dialog, int which) {
							// Do nothing but close the dialog
							restore();
							dialog.dismiss();
						}

					});

			builder2.setNegativeButton("NO",
					new DialogInterface.OnClickListener() {

						@Override
						public void onClick(DialogInterface dialog, int which) {
							// Do nothing
							dialog.dismiss();
						}
					});
			builder2.show();

			return true;

		case R.id.search:
			View view = findViewById(R.id.searchButton);
			onSearch(view);
			return true;
		}

		return super.onOptionsItemSelected(item);
	}

	@Override
	public void onBackPressed() {
		// TODO Auto-generated method stub
		super.onBackPressed();
		finish();
	}

	public void restore() {
		try {
			File sd = Environment.getExternalStorageDirectory();
			if (sd.canWrite()) {
				String currentDBPath = sd + "/MovieMemo/Backup";
				String backupDBPath = "data/data/com.varunest.moviememo/databases/MyDb.db";
				File currentDB = null;
				File dir = new File(currentDBPath);
				File backupDB = new File(backupDBPath);
				int i = mViewPager.getCurrentItem();

				if (dir.exists()) {
					currentDB = new File(dir, "DataBase.db");
					FileChannel src = new FileInputStream(currentDB)
							.getChannel();
					FileChannel dst = new FileOutputStream(backupDB)
							.getChannel();
					dst.transferFrom(src, 0, src.size());
					src.close();
					dst.close();
					myDb.close();
					myDb.open();
					mViewPager.setAdapter(mSectionsPagerAdapter);
					mViewPager.setCurrentItem(i);
					Toast.makeText(getApplicationContext(),
							"Import Successful", Toast.LENGTH_LONG).show();
				} else {
					// toast here
					Toast.makeText(getApplicationContext(), "No Backup Found",
							Toast.LENGTH_LONG).show();
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void backup() {
		// TODO Auto-generated method stub
		try {
			File sd = Environment.getExternalStorageDirectory();
			if (sd.canWrite()) {
				String currentDBPath = "data/data/com.varunest.moviememo/databases/MyDb.db";
				File currentDB = new File(currentDBPath);
				File dir = new File(sd + File.separator + "MovieMemo"
						+ File.separator + "Backup");
				if (!dir.exists()) {
					dir.mkdir();
				}
				File backupDB = new File(dir, "DataBase.db");

				if (currentDB.exists()) {
					FileChannel src = new FileInputStream(currentDB)
							.getChannel();
					FileChannel dst = new FileOutputStream(backupDB)
							.getChannel();
					dst.transferFrom(src, 0, src.size());
					src.close();
					dst.close();
					Toast.makeText(getApplicationContext(),
							"Backup Successful", Toast.LENGTH_LONG).show();
				} else {
					Toast.makeText(getApplicationContext(), "Unable to Export",
							Toast.LENGTH_LONG).show();
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	@Override
	public void onTabSelected(ActionBar.Tab tab,
			FragmentTransaction fragmentTransaction) {
		// When the given tab is selected, switch to the corresponding page in
		// the ViewPager.
		mViewPager.setCurrentItem(tab.getPosition());
		if (tab.getPosition() == 0) {
			if (sharedpreferences.getBoolean("help1", true)) {

				final Dialog dialog = new Dialog(this);
				dialog.setCancelable(false);
			    dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
			    dialog.setContentView(R.layout.help0);
			    final Window window = dialog.getWindow();
			    window.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.MATCH_PARENT);
			    window.clearFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
			    window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
			    dialog.show();
			    ImageView okay = (ImageView)dialog.findViewById(R.id.okay0);
			    okay.setOnTouchListener(new OnTouchListener(){

					@Override
					public boolean onTouch(View v, MotionEvent event) {
						// TODO Auto-generated method stub
						SharedPreferences.Editor editor = sharedpreferences.edit();
						editor.putBoolean("help1", false);
						editor.commit();
						dialog.dismiss();
						return false;
					}
			    	
			    });
				
			}
			
		}
		if (tab.getPosition() == 1) {
			if (sharedpreferences.getBoolean("help2", true)) {
				final Dialog dialog = new Dialog(this);
				dialog.setCancelable(false);
			    dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
			    dialog.setContentView(R.layout.help1);
			    final Window window = dialog.getWindow();
			    window.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.MATCH_PARENT);
			    window.clearFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
			    window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
			    dialog.show();
			    ImageView okay = (ImageView)dialog.findViewById(R.id.okay1);
			    okay.setOnTouchListener(new OnTouchListener(){

					@Override
					public boolean onTouch(View v, MotionEvent event) {
						// TODO Auto-generated method stub
						SharedPreferences.Editor editor = sharedpreferences.edit();
						editor.putBoolean("help2", false);
						editor.commit();
						dialog.dismiss();
						return false;
					}
			    	
			    });
			}
			
		}

	}

	@Override
	public void onTabUnselected(ActionBar.Tab tab,
			FragmentTransaction fragmentTransaction) {
	}

	@Override
	public void onTabReselected(ActionBar.Tab tab,
			FragmentTransaction fragmentTransaction) {
	}

	public class DepthPageTransformer implements ViewPager.PageTransformer {
		private static final float MIN_SCALE = 0.75f;

		public void transformPage(View view, float position) {
			int pageWidth = view.getWidth();

			if (position < -1) { // [-Infinity,-1)
				// This page is way off-screen to the left.
				view.setAlpha(0);

			} else if (position <= 0) { // [-1,0]
				// Use the default slide transition when moving to the left page
				view.setAlpha(1);
				view.setTranslationX(0);
				view.setScaleX(1);
				view.setScaleY(1);

			} else if (position <= 1) { // (0,1]
				// Fade the page out.
				view.setAlpha(1 - position);

				// Counteract the default slide transition
				view.setTranslationX(pageWidth * -position);

				// Scale the page down (between MIN_SCALE and 1)
				float scaleFactor = MIN_SCALE + (1 - MIN_SCALE)
						* (1 - Math.abs(position));
				view.setScaleX(scaleFactor);
				view.setScaleY(scaleFactor);

			} else { // (1,+Infinity]
				// This page is way off-screen to the right.
				view.setAlpha(0);
			}
		}
	}

	/**
	 * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
	 * one of the sections/tabs/pages.
	 */
	public class SectionsPagerAdapter extends FragmentPagerAdapter {

		public SectionsPagerAdapter(FragmentManager fm) {
			super(fm);
		}

		@Override
		public Fragment getItem(int position) {
			// getItem is called to instantiate the fragment for the given page.
			// Return a PlaceholderFragment (defined as a static inner class
			// below).
			return PlaceholderFragment.newInstance(position + 1);
		}

		@Override
		public int getCount() {
			// Show 3 total pages.
			return 3;
		}

		@Override
		public CharSequence getPageTitle(int position) {
			Locale l = Locale.getDefault();
			switch (position) {
			case 0:
				return getString(R.string.title_section1).toUpperCase(l);
			case 1:
				return getString(R.string.title_section2).toUpperCase(l);
			case 2:
				return getString(R.string.title_section3).toUpperCase(l);
			}
			return null;
		}
	}

	/**
	 * A placeholder fragment containing a simple view.
	 */
	public static class PlaceholderFragment extends Fragment {
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
				if (string_query.equals("")) {

					rootView = inflater.inflate(R.layout.fragment_pre_search,
							container, false);
				} else {

					rootView = inflater.inflate(R.layout.fragment_search,
							container, false);

					string_query = string_query.replaceAll(" ", "%20");

					ConnectivityManager connMgr = (ConnectivityManager) getActivity()
							.getSystemService(Context.CONNECTIVITY_SERVICE);
					NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();

					if (networkInfo != null && networkInfo.isConnected()) {

						XMLParser parser = new XMLParser();
						String xml = "";
						xml = parser.getXmlFromUrl("http://www.omdbapi.com/?s="
								+ string_query + "&r=XML");
						// getting dom object
						string_query = "";
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
								LazyAdapter adapter = new LazyAdapter(this,
										menuItems);
								list.setAdapter(adapter);

								list.setOnItemClickListener(new OnItemClickListener() {

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

				if (sharedpreferences.getBoolean(getString(R.string.view_type),
						true)) {
					lv.setVisibility(View.GONE);
					gv.setVisibility(View.VISIBLE);
					gv.setOnItemClickListener(new OnItemClickListener() {

						@Override
						public void onItemClick(AdapterView<?> parent,
								View view, int position, long id) {
							Log.d("hello", "clicked on item: " + position + " "
									+ id);
							showDetails(id, 1);
						}

					});

					gv.setOnItemLongClickListener(new OnItemLongClickListener() {

						@Override
						public boolean onItemLongClick(AdapterView<?> parent,
								View view, int position, long id) {
							// TODO Auto-generated method stub
							Cursor sharecursor;
							sharecursor = myDb.getRowByCursorID(id);
							if (sharecursor.moveToFirst()) {
								String Data = "Checkout the movie :\n\n";
								String title = sharecursor
										.getString(myDb.COL_TITLE) + "\n";
								String starred = "Starred by : "
										+ sharecursor
												.getString(myDb.COL_ACTORS)
										+ "\n";
								String rated = "Rated : "
										+ sharecursor
												.getString(myDb.COL_RATING)
										+ "\n";
								String url = "Imdb Link : http://www.imdb.com/title/"
										+ sharecursor.getString(myDb.COL_ID)
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
							String sort = sharedpreferences.getString(
									getString(R.string.sort), "name");
							Log.e("database", "watch list " + sort);
							cursor = myDb.getAllWatchRows(sort);
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
					lv.setOnItemClickListener(new OnItemClickListener() {

						@Override
						public void onItemClick(AdapterView<?> parent,
								View view, int position, long id) {
							Log.d("hello", "clicked on item: " + position + " "
									+ id);
							showDetails(id, 1);
						}
					});

					lv.setOnItemLongClickListener(new OnItemLongClickListener() {

						@Override
						public boolean onItemLongClick(AdapterView<?> parent,
								View view, int position, long id) {
							// TODO Auto-generated method stub
							Cursor sharecursor;
							sharecursor = myDb.getRowByCursorID(id);
							if (sharecursor.moveToFirst()) {
								String Data = "Checkout the movie :\n\n";
								String title = sharecursor
										.getString(myDb.COL_TITLE) + "\n";
								String starred = "Starred by : "
										+ sharecursor
												.getString(myDb.COL_ACTORS)
										+ "\n";
								String rated = "Rated : "
										+ sharecursor
												.getString(myDb.COL_RATING)
										+ "\n";
								String url = "Imdb Link : http://www.imdb.com/title/"
										+ sharecursor.getString(myDb.COL_ID)
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
							String sort = sharedpreferences.getString(
									getString(R.string.sort), "name");
							Log.e("database", "watch list " + sort);
							cursor = myDb.getAllWatchRows(sort);
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

				if (sharedpreferences.getBoolean(getString(R.string.view_type),
						true)) {
					lv.setVisibility(View.GONE);
					gv.setVisibility(View.VISIBLE);
					gv.setOnItemClickListener(new OnItemClickListener() {

						@Override
						public void onItemClick(AdapterView<?> parent,
								View view, int position, long id) {
							Log.d("hello", "clicked on item: " + position + " "
									+ id);
							showDetails(id, 2);
						}
					});
					gv.setOnItemLongClickListener(new OnItemLongClickListener() {

						@Override
						public boolean onItemLongClick(AdapterView<?> parent,
								View view, int position, long id) {
							// TODO Auto-generated method stub
							Cursor sharecursor;
							sharecursor = myDb.getRowByCursorID(id);
							if (sharecursor.moveToFirst()) {
								String Data = "Checkout the movie :\n\n";
								String title = sharecursor
										.getString(myDb.COL_TITLE) + "\n";
								String starred = "Starred by : "
										+ sharecursor
												.getString(myDb.COL_ACTORS)
										+ "\n";
								String rated = "Rated : "
										+ sharecursor
												.getString(myDb.COL_RATING)
										+ "\n";
								String url = "Imdb Link : http://www.imdb.com/title/"
										+ sharecursor.getString(myDb.COL_ID)
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
							String sort = sharedpreferences.getString(
									getString(R.string.sort), "name");
							Log.e("database", "watch list " + sort);
							cursor = myDb.getAllWatchedRows(sort);
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
					lv.setOnItemClickListener(new OnItemClickListener() {

						@Override
						public void onItemClick(AdapterView<?> parent,
								View view, int position, long id) {
							Log.d("hello", "clicked on item: " + position + " "
									+ id);
							showDetails(id, 2);
						}
					});
					lv.setOnItemLongClickListener(new OnItemLongClickListener() {

						@Override
						public boolean onItemLongClick(AdapterView<?> parent,
								View view, int position, long id) {
							// TODO Auto-generated method stub
							Cursor sharecursor;
							sharecursor = myDb.getRowByCursorID(id);
							if (sharecursor.moveToFirst()) {
								String Data = "Checkout the movie :\n\n";
								String title = sharecursor
										.getString(myDb.COL_TITLE) + "\n";
								String starred = "Starred by : "
										+ sharecursor
												.getString(myDb.COL_ACTORS)
										+ "\n";
								String rated = "Rated : "
										+ sharecursor
												.getString(myDb.COL_RATING)
										+ "\n";
								String url = "Imdb Link : http://www.imdb.com/title/"
										+ sharecursor.getString(myDb.COL_ID)
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
							String sort = sharedpreferences.getString(
									getString(R.string.sort), "name");
							Log.e("database", "watch list " + sort);
							cursor = myDb.getAllWatchedRows(sort);
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
			final Cursor cdetail = myDb.getRowByCursorID(id);
			movieName.setText(cdetail.getString(myDb.COL_TITLE));
			// setting details
			final RatingBar ratingBar = (RatingBar) detail_dialog
					.findViewById(R.id.ratingBar);
			Float urating = cdetail.getFloat(myDb.COL_USER_RATING);
			ratingBar.setRating(urating);
			ratingBar
					.setOnRatingBarChangeListener(new OnRatingBarChangeListener() {
						public void onRatingChanged(RatingBar ratingBar,
								float rating, boolean fromUser) {
							myDb.updateUrating(rating, id);
						}
					});
			TextView smallDetail = (TextView) detail_dialog
					.findViewById(R.id.smallDetails);
			String genre = cdetail.getString(myDb.COL_GENRE);
			genre = genre.replaceAll(",", " |");
			String s_detail = cdetail.getString(myDb.COL_RUNTIME) + " - "
					+ genre + " - " + cdetail.getString(myDb.COL_RELEASED)
					+ " (" + cdetail.getString(myDb.COL_COUNTRY) + ")";
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
			rateValue.setText(cdetail.getString(myDb.COL_RATING));

			TextView director = (TextView) detail_dialog
					.findViewById(R.id.director);
			TextView writer = (TextView) detail_dialog
					.findViewById(R.id.writer);
			TextView stars = (TextView) detail_dialog.findViewById(R.id.star);
			TextView awards = (TextView) detail_dialog.findViewById(R.id.award);
			TextView plot = (TextView) detail_dialog
					.findViewById(R.id.plotText);
			director.setText(cdetail.getString(myDb.COL_DIRECTOR));
			writer.setText(cdetail.getString(myDb.COL_WRITER));
			stars.setText(cdetail.getString(myDb.COL_ACTORS));
			awards.setText(cdetail.getString(myDb.COL_AWARDS));
			plot.setText(cdetail.getString(myDb.COL_PLOT));
			ImageButton watchlist = (ImageButton) detail_dialog
					.findViewById(R.id.watchList);

			if (cdetail.moveToFirst()) {
				String watch = cdetail.getString(myDb.COL_WATCH);
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
					myDb.toggleWatch(id, ratingBar.getRating());
					mViewPager.setAdapter(mSectionsPagerAdapter);
					mViewPager.setCurrentItem(page);

					Cursor c = myDb.getRowByCursorID(id);
					if (c.moveToFirst()) {
						String watch = c.getString(myDb.COL_WATCH);
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
				String watched = cdetail.getString(myDb.COL_WATCHED);
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
					myDb.toggleWatched(id,
							ratingBar.getRating());
					mViewPager.setAdapter(mSectionsPagerAdapter);
					mViewPager.setCurrentItem(page);
					Cursor cwatched = myDb.getRowByCursorID(id);
					if (cwatched.moveToFirst()) {
						String watched = cwatched.getString(myDb.COL_WATCHED);
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
				implements OnDismissListener {
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

				Cursor c = myDb.getRow(movie_element.getAttribute("imdbID"));
				final RatingBar ratingBar = (RatingBar) detail_dialog
						.findViewById(R.id.ratingBar);

				if (c.moveToFirst()) {
					Float urating = c.getFloat(myDb.COL_USER_RATING);
					ratingBar.setRating(urating);
					final long id = c.getLong(myDb.COL_CURSOR_ID);
					ratingBar
							.setOnRatingBarChangeListener(new OnRatingBarChangeListener() {
								public void onRatingChanged(
										RatingBar ratingBar, float rating,
										boolean fromUser) {
									myDb.updateUrating(rating,
											id);

								}
							});
					String watch = c.getString(myDb.COL_WATCH);
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

						myDb.insertRow(0, id, title, year, genre, released,
								runtime, director, writer, actors, plot,
								country, awards, rating, poster, urating);
						mViewPager.setAdapter(mSectionsPagerAdapter);
						Cursor c = myDb.getRow(movie_element
								.getAttribute("imdbID"));
						if (c.moveToFirst()) {
							String watch = c.getString(myDb.COL_WATCH);
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
				Cursor d = myDb.getRow(movie_element.getAttribute("imdbID"));
				if (d.moveToFirst()) {
					String watched = d.getString(myDb.COL_WATCHED);
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

						myDb.insertRow(1, id, title, year, genre, released,
								runtime, director, writer, actors, plot,
								country, awards, rating, poster, ratevalue);

						mViewPager.setAdapter(mSectionsPagerAdapter);

						Cursor c = myDb.getRow(movie_element
								.getAttribute("imdbID"));
						if (c.moveToFirst()) {
							String watched = c.getString(myDb.COL_WATCHED);
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

	public static class XMLParser {
		// This function makes httprequest and returns xml response
		public String getXmlFromUrl(String url) {
			String xml = "";
			Log.i("hello", "inside getxmlfromurl");

			try {
				// defaultHttpClient
				StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder()
						.permitAll().build();
				StrictMode.setThreadPolicy(policy);
				DefaultHttpClient httpClient = new DefaultHttpClient();
				HttpPost httpPost = new HttpPost(url);
				// set http params
				HttpParams params = httpClient.getParams();
				params.setParameter(CoreConnectionPNames.CONNECTION_TIMEOUT,
						8000);
				params.setParameter(CoreConnectionPNames.SO_TIMEOUT, 8000);
				httpClient.setParams(params);
				HttpResponse httpResponse = httpClient.execute(httpPost);

				HttpEntity httpEntity = httpResponse.getEntity();
				xml = EntityUtils.toString(httpEntity);

			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			} catch (ClientProtocolException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}

			// return XML
			return xml;
		}

		// getting dom object from xml
		public Document getDomElement(String xml) {
			Document doc = null;
			DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
			try {

				DocumentBuilder db = dbf.newDocumentBuilder();

				InputSource is = new InputSource();
				is.setCharacterStream(new StringReader(xml));
				doc = db.parse(is);

			} catch (ParserConfigurationException e) {
				Log.e("Error: ", e.getMessage());
				return null;
			} catch (SAXException e) {
				Log.e("Error: ", e.getMessage());
				return null;
			} catch (IOException e) {
				Log.e("Error: ", e.getMessage());
				return null;
			}
			// return DOM
			return doc;
		}

		public String getValue(Element item, String str) {
			NodeList n = item.getElementsByTagName(str);
			return this.getElementValue(n.item(0));
		}

		public final String getElementValue(Node elem) {
			Node child;
			if (elem != null) {
				if (elem.hasChildNodes()) {
					for (child = elem.getFirstChild(); child != null; child = child
							.getNextSibling()) {
						if (child.getNodeType() == Node.TEXT_NODE) {
							return child.getNodeValue();
						}
					}
				}
			}
			return "";
		}
	}// XMLparser ends here

	public static class LazyAdapter extends BaseAdapter {

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
			Cursor c = myDb.getRow(id);
			TextView watched = (TextView) vi.findViewById(R.id.watched_text);
			ImageView watch = (ImageView) vi.findViewById(R.id.star_watch);
			if (c.moveToFirst()) {
				String w = c.getString(myDb.COL_WATCHED);
				String s = c.getString(myDb.COL_WATCH);
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

}
