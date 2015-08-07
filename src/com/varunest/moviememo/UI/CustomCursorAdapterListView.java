package com.varunest.moviememo.UI;

import java.io.File;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.os.Environment;
import android.support.v4.widget.CursorAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import com.varunest.moviememo.Adapters.DBAdapter;
import com.varunest.moviememo.R;

public class CustomCursorAdapterListView extends CursorAdapter {

	private static LayoutInflater inflater = null;

	public CustomCursorAdapterListView(Context context, Cursor c,
			boolean autoRequery) {
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
		View retView = inflater.inflate(R.layout.list_row, parent, false);

		return retView;
	}

	@Override
	public void bindView(View view, Context context, Cursor cursor) {
		// here we are setting our data
		// that means, take the data from the cursor and put it in views
		TextView poster_title;
		poster_title = (TextView) view.findViewById(R.id.movieTitle);
		poster_title.setText(cursor.getString(DBAdapter.COL_TITLE));
		ImageView img = (ImageView) view.findViewById(R.id.filmIcon);
		TextView year = (TextView) view.findViewById(R.id.year);
		year.setText(cursor.getString(DBAdapter.COL_YEAR));
		ImageView star = (ImageView) view.findViewById(R.id.star_watch);
		star.setVisibility(View.INVISIBLE);

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
				Bitmap output = Bitmap.createBitmap(bmp.getWidth(),
						bmp.getHeight(), Config.ARGB_8888);
				Canvas canvas = new Canvas(output);

				final Paint paint = new Paint();
				final Rect rect = new Rect(0, 0, bmp.getWidth(),
						bmp.getHeight());

				paint.setAntiAlias(true);
				canvas.drawARGB(0, 0, 0, 0);
				canvas.drawCircle(bmp.getWidth() / 2, bmp.getHeight() / 2,
						bmp.getWidth() / 2, paint);
				paint.setXfermode(new PorterDuffXfermode(Mode.SRC_IN));
				canvas.drawBitmap(bmp, rect, rect, paint);
				img.setImageBitmap(output);
			}

			else {
				img.setBackgroundResource(R.drawable.democ);
			}
		} if(poster.equals("no")) {
			img.setBackgroundResource(R.drawable.democ);
		}
	}

}
