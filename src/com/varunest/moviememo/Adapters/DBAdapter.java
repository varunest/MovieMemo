// ------------------------------------ DBADapter.java ---------------------------------------------

// TODO: Change the package to match your project.
package com.varunest.moviememo.Adapters;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

// TO USE:
// Change the package (at top) to match your project.
// Search for "TODO", and make the appropriate changes.
public class DBAdapter {

	// db fields
	private static final String TAG = "DBAdapter";
	public static final String YEAR = "year";
	public static final String TITLE = "title";
	public static final String GENRE = "genre";
	public static final String RELEASED = "released";
	public static final String RUNTIME = "runtime";
	public static final String DIRECTOR = "director";
	public static final String WRITER = "writer";
	public static final String ACTORS = "actors";
	public static final String PLOT = "plot";
	public static final String COUNTRY = "country";
	public static final String AWARDS = "awards";
	public static final String RATING = "imdbRating";
	public static final String ID = "imdbID";
	public static final String POSTER = "poster";
	public static final String WATCH = "watch";
	public static final String WATCHED = "watched";
	public static final String CURSOR_ID = "_id";
	public static final String USER_RATING = "uRating";

	// col numbers
	public static final int COL_ID = 0;
	public static final int COL_TITLE = 1;
	public static final int COL_YEAR = 2;
	public static final int COL_GENRE = 3;
	public static final int COL_RELEASED = 4;
	public static final int COL_RUNTIME = 5;
	public static final int COL_DIRECTOR = 6;
	public static final int COL_WRITER = 7;
	public static final int COL_ACTORS = 8;
	public static final int COL_PLOT = 9;
	public static final int COL_COUNTRY = 10;
	public static final int COL_AWARDS = 11;
	public static final int COL_RATING = 12;
	public static final int COL_POSTER = 13;
	public static final int COL_WATCH = 14;
	public static final int COL_WATCHED = 15;
	public static final int COL_CURSOR_ID = 16;
	public static final int COL_USER_RATING = 17;

	public static final String[] ALL_KEYS = new String[] { ID, TITLE, YEAR,
			GENRE, RELEASED, RUNTIME, DIRECTOR, WRITER, ACTORS, PLOT, COUNTRY,
			AWARDS, RATING, POSTER, WATCH, WATCHED, CURSOR_ID, USER_RATING };

	// DB info: it's name, and the table we are using (just one).
	public static final String DATABASE_NAME = "MyDb.db";
	public static final String DATABASE_TABLE = "movie";
	// Track DB version if a new version of your app changes the format.
	public static final int DATABASE_VERSION = 1;

	private static final String DATABASE_CREATE_SQL = "create table "
			+ DATABASE_TABLE + " (" + ID + " text, " + TITLE + " text, " + YEAR
			+ " text, " + GENRE + " text, " + RELEASED + " text, " + RUNTIME
			+ " text, " + DIRECTOR + " text, " + WRITER + " text, " + ACTORS
			+ " text, " + PLOT + " text, " + COUNTRY + " text, " + AWARDS
			+ " text, " + RATING + " text, " + POSTER + " text, " + WATCH
			+ " text, " + WATCHED + " text, " + CURSOR_ID
			+ " integer primary key autoincrement, " + USER_RATING + " float);";

	// Context of application who uses us.
	private final Context context;

	private DatabaseHelper myDBHelper;
	private SQLiteDatabase db;

	// ///////////////////////////////////////////////////////////////////
	// Public methods:
	// ///////////////////////////////////////////////////////////////////

	public DBAdapter(Context ctx) {
		this.context = ctx;
		myDBHelper = new DatabaseHelper(context);
	}

	// Open the database connection.
	public DBAdapter open() {
		db = myDBHelper.getWritableDatabase();
		return this;
	}

	// Close the database connection.
	public void close() {
		myDBHelper.close();
	}

	public void toggleWatch(long id, Float urating) {
		Cursor c = getRowByCursorID(id);
		ContentValues movie = new ContentValues();
		if (c.getString(COL_WATCH).equals("no")) {
			movie.put(WATCH, "yes");
			movie.put(USER_RATING, urating);
			db.update(DATABASE_TABLE, movie, "_id = '" + id + "'", null);
		} else if (c.getString(COL_WATCH).equals("yes")) {
			movie.put(WATCH, "no");
			movie.put(USER_RATING, urating);

			db.update(DATABASE_TABLE, movie, "_id = '" + id + "'", null);
		}
	}

	public void toggleWatched(long id, Float urating) {
		Cursor c = getRowByCursorID(id);
		ContentValues movie = new ContentValues();
		if (c.getString(COL_WATCHED).equals("no")) {
			movie.put(WATCHED, "yes");
			movie.put(USER_RATING, urating);

			db.update(DATABASE_TABLE, movie, "_id = '" + id + "'", null);
		} else if (c.getString(COL_WATCHED).equals("yes")) {
			movie.put(WATCHED, "no");
			movie.put(USER_RATING, urating);

			db.update(DATABASE_TABLE, movie, "_id = '" + id + "'", null);
		}

	}
	
	public void updateUrating(Float urating,long id){
		Cursor c = getRowByCursorID(id);
		ContentValues movie = new ContentValues();
		movie.put(USER_RATING, urating);
		db.update(DATABASE_TABLE, movie, "_id = '" + id + "'", null);
	}
	// Add a new set of values to the database.
	public void insertRow(int check, String id, String title, String year,
			String genre, String released, String runtime, String director,
			String writer, String actors, String plot, String country,
			String awards, String rating, String poster, Float urating) {

		// Create row's data:
		ContentValues movie = new ContentValues();
		if (check == 0) {
			Cursor c = getRow(id);
			if (c.moveToFirst()) {
				if (c.getString(COL_WATCH).equals("yes")
						&& c.getString(COL_WATCHED).equals("no")) {
					db.delete(DATABASE_TABLE, "imdbID=" + "'" + id + "'", null);
				} else if (c.getString(COL_WATCH).equals("yes")
						&& c.getString(COL_WATCHED).equals("yes")) {
					movie.put(WATCH, "no");
					db.update(DATABASE_TABLE, movie, "imdbID = '" + id + "'",
							null);
				} else {
					movie.put(WATCH, "yes");
					db.update(DATABASE_TABLE, movie, "imdbID = '" + id + "'",
							null);
				}

			} else {
				movie.put(ID, id);
				movie.put(TITLE, title);
				movie.put(YEAR, year);
				movie.put(GENRE, genre);
				movie.put(RELEASED, released);
				movie.put(RUNTIME, runtime);
				movie.put(DIRECTOR, director);
				movie.put(WRITER, writer);
				movie.put(ACTORS, actors);
				movie.put(PLOT, plot);
				movie.put(COUNTRY, country);
				movie.put(AWARDS, awards);
				movie.put(RATING, rating);
				movie.put(POSTER, poster);
				movie.put(WATCH, "yes");
				movie.put(WATCHED, "no");
				movie.put(USER_RATING, urating);
				db.insert(DATABASE_TABLE, null, movie);

			}
			c.close();
		}
		if (check == 1) {
			Cursor c = getRow(id);
			if (c.moveToFirst()) {
				if (c.getString(COL_WATCHED).equals("yes")
						&& c.getString(COL_WATCH).equals("no")) {
					db.delete(DATABASE_TABLE, "imdbID=" + "'" + id + "'", null);
				} else if (c.getString(COL_WATCHED).equals("yes")
						&& c.getString(COL_WATCH).equals("yes")) {
					movie.put(WATCHED, "no");
					db.update(DATABASE_TABLE, movie, "imdbID = '" + id + "'",
							null);
				} else {
					movie.put(WATCHED, "yes");
					db.update(DATABASE_TABLE, movie, "imdbID = '" + id + "'",
							null);
				}

			} else {
				movie.put(ID, id);
				movie.put(TITLE, title);
				movie.put(YEAR, year);
				movie.put(GENRE, genre);
				movie.put(RELEASED, released);
				movie.put(RUNTIME, runtime);
				movie.put(DIRECTOR, director);
				movie.put(WRITER, writer);
				movie.put(ACTORS, actors);
				movie.put(PLOT, plot);
				movie.put(COUNTRY, country);
				movie.put(AWARDS, awards);
				movie.put(RATING, rating);
				movie.put(POSTER, poster);
				movie.put(WATCH, "no");
				movie.put(WATCHED, "yes");
				movie.put(USER_RATING, urating);
				db.insert(DATABASE_TABLE, null, movie);

			}
			c.close();
		}

	}

	// Delete a row from the database, by rowId (primary key)
	public boolean deleteRow(String rowId) {
		String where = ID + "=" + rowId;
		return db.delete(DATABASE_TABLE, where, null) != 0;
	}

	public void deleteAll() {
		Cursor c = getAllRows("name");
		long rowId = c.getColumnIndexOrThrow(ID);
		if (c.moveToFirst()) {
			do {
				deleteRow(c.getString((int) rowId));
			} while (c.moveToNext());
		}
		c.close();
	}

	// Return all to watch movies in the database.
	public Cursor getAllWatchRows(String sort) {
		String where = WATCH + "= 'yes'";
		String orderby = TITLE;
		String groupby = TITLE;
		Cursor c = null;
		if (sort.equals("name")) {
			c = db.query(DATABASE_TABLE, ALL_KEYS, where, null, groupby,null,
					orderby + " ASC");
		} else if (sort.equals("added")) {
			orderby = CURSOR_ID;
			c = db.query(DATABASE_TABLE, ALL_KEYS, where, null, groupby, null,
					orderby + " DESC");
		} else if (sort.equals("imdbrating")) {
			orderby = RATING;
			c = db.query(DATABASE_TABLE, ALL_KEYS, where, null, groupby, null,
					orderby + " DESC");
		} else if (sort.equals("urating")) {
			orderby = USER_RATING;
			c = db.query(DATABASE_TABLE, ALL_KEYS, where, null, groupby, null,
					orderby + " DESC");
		}

		if (c != null) {
			c.moveToFirst();
		}
		return c;
	}

	// Return all to watched movies in the database.
	public Cursor getAllWatchedRows(String sort) {
		String where = WATCHED + "= 'yes'";
		String orderby = TITLE;
		String groupby = TITLE;

		Cursor c = null;
		if (sort.equals("name")) {
			c = db.query(DATABASE_TABLE, ALL_KEYS, where, null, groupby, null,
					orderby + " ASC");
		} else if (sort.equals("added")) {
			orderby = CURSOR_ID;
			c = db.query(DATABASE_TABLE, ALL_KEYS, where, null, groupby, null,
					orderby + " DESC");
		} else if (sort.equals("imdbrating")) {
			orderby = RATING;
			c = db.query(DATABASE_TABLE, ALL_KEYS, where, null, groupby, null,
					orderby + " DESC");
		} else if (sort.equals("urating")) {
			orderby = USER_RATING;
			c = db.query(DATABASE_TABLE, ALL_KEYS, where, null, groupby, null,
					orderby + " DESC");
		}

		if (c != null) {
			c.moveToFirst();
		}
		return c;
	}

	// Return all data in the database.
	public Cursor getAllRows(String sort) {

		String where = null;
		String orderby = TITLE;
		String groupby = TITLE;

		Cursor c = null;
		if (sort.equals("name")) {
			c = db.query(DATABASE_TABLE, ALL_KEYS, where, null, groupby, null,
					orderby + " ASC");
		} else if (sort.equals("added")) {
			orderby = CURSOR_ID;
			c = db.query(DATABASE_TABLE, ALL_KEYS, where, null, groupby, null,
					orderby + " DESC");
		} else if (sort.equals("imdbrating")) {
			orderby = RATING;
			c = db.query(DATABASE_TABLE, ALL_KEYS, where, null, groupby, null,
					orderby + " DESC");
		} else if (sort.equals("urating")) {
			orderby = USER_RATING;
			c = db.query(DATABASE_TABLE, ALL_KEYS, where, null, groupby, null,
					orderby + " DESC");
		}

		if (c != null) {
			c.moveToFirst();
		}
		return c;
	}

	// Get a specific row (by rowId)
	public Cursor getRow(String rowid) {
		Cursor c = db.rawQuery("SELECT * FROM movie WHERE imdbID ='" + rowid
				+ "'", null);
		if (c != null) {
			c.moveToFirst();
		}
		return c;
	}

	// Get a specific row (by CursorId)
	public Cursor getRowByCursorID(long rowid) {
		Cursor c = db.rawQuery(
				"SELECT * FROM movie WHERE _id ='" + rowid + "'", null);
		if (c != null) {
			c.moveToFirst();
		}
		return c;
	}

	// ///////////////////////////////////////////////////////////////////
	// Private Helper Classes:
	// ///////////////////////////////////////////////////////////////////

	/**
	 * Private class which handles database creation and upgrading. Used to
	 * handle low-level database access.
	 */
	private static class DatabaseHelper extends SQLiteOpenHelper {
		DatabaseHelper(Context context) {
			super(context, DATABASE_NAME, null, DATABASE_VERSION);
		}

		@Override
		public void onCreate(SQLiteDatabase _db) {
			_db.execSQL(DATABASE_CREATE_SQL);
		}

		@Override
		public void onUpgrade(SQLiteDatabase _db, int oldVersion, int newVersion) {
			Log.w(TAG, "Upgrading application's database from version "
					+ oldVersion + " to " + newVersion
					+ ", which will destroy all old data!");

			// Destroy old database:
			_db.execSQL("DROP TABLE IF EXISTS " + DATABASE_TABLE);

			// Recreate new database:
			onCreate(_db);
		}
	}
}
