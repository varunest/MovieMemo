package com.varunest.moviememo.Adapters;

import java.io.File;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.support.v4.widget.CursorAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import com.varunest.moviememo.Adapters.DBAdapter;
import com.varunest.moviememo.R;

public class CustomCursorAdapter extends CursorAdapter {

	private static LayoutInflater inflater = null;

	public CustomCursorAdapter(Context context, Cursor c, boolean autoRequery) {
		super(context, c, autoRequery);
		// TODO Auto-generated constructor stub
		inflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	}

	@Override
	public View newView(Context context, Cursor cursor, ViewGroup parent) {
		// when the view will be created for first time,
		// we need to tell the adapters, how each item will look
		inflater = LayoutInflater.from(parent.getContext());
		View retView = inflater.inflate(R.layout.list_watch_row, parent, false);

		return retView;
	}

	@Override
	public void bindView(View view, Context context, Cursor cursor) {
		// here we are setting our data
		// that means, take the data from the cursor and put it in views
		TextView poster_title;
		poster_title = (TextView) view.findViewById(R.id.poster_title);
		poster_title.setText(cursor.getString(DBAdapter.COL_TITLE));
		ImageView img = (ImageView) view.findViewById(R.id.poster_thumbnail);
		
		String location = "";
		String poster = cursor.getString(DBAdapter.COL_POSTER);
		if (poster.equals("yes")) {
			if (android.os.Environment.getExternalStorageState().equals(
					android.os.Environment.MEDIA_MOUNTED)) {
				location = Environment.getExternalStorageDirectory()
						+ File.separator + "MovieMemo" + File.separator
						+ "Posters" + File.separator;
				location = location + cursor.getString(DBAdapter.COL_ID)
						+ ".jpg";
				Bitmap bmp = BitmapFactory.decodeFile(location);
				img.setImageBitmap(bmp);
			}

			else {
				img.setBackgroundResource(R.drawable.demo);
			}
		}else {
			img.setBackgroundResource(R.drawable.demo);
		}
	}

}
