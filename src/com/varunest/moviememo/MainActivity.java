package com.varunest.moviememo;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.*;
import android.view.View.OnTouchListener;
import android.view.WindowManager.LayoutParams;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;
import android.widget.Toast;
import com.crashlytics.android.Crashlytics;
import com.varunest.moviememo.Adapters.DBAdapter;
import com.varunest.moviememo.UI.DepthPageTransformer;
import com.varunest.moviememo.UI.PlaceholderFragment;
import io.fabric.sdk.android.Fabric;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.nio.channels.FileChannel;
import java.util.Locale;

public class MainActivity extends ActionBarActivity implements
		ActionBar.TabListener {

	/**
	 * The {@link android.support.v4.view.PagerAdapter} that will provide
	 * fragments for each of the sections. We use a {@link FragmentPagerAdapter}
	 * derivative, which will keep every loaded fragment in memory. If this
	 * becomes too memory intensive, it may be best to switch to a
	 * {@link android.support.v4.app.FragmentStatePagerAdapter}.
	 */
	public static SectionsPagerAdapter mSectionsPagerAdapter;
	public static DBAdapter myDb;
	public static SharedPreferences sharedpreferences;
	/**
	 * The {@link ViewPager} that will host the section contents.
	 */
	public static ViewPager mViewPager;
	public static String string_query = "";
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

}
