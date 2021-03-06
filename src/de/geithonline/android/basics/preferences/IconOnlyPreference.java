package de.geithonline.android.basics.preferences;

import android.content.Context;
import android.graphics.Bitmap;
import android.preference.Preference;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

public class IconOnlyPreference extends Preference {

	private ImageView mImageView;

	private Bitmap mPhoto;
	private View view;

	public IconOnlyPreference(final Context context, final AttributeSet attrs) {
		super(context, attrs);
	}

	@Override
	protected View onCreateView(final ViewGroup parent) {
		final LayoutInflater li = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		if (view == null) {
			view = li.inflate(R.layout.icon_only_preference, parent, false);
			mImageView = (ImageView) view.findViewById(R.id.icon_only_view);
		}
		if (mImageView != null && mPhoto != null) {
			mImageView.setImageBitmap(mPhoto);
		}

		return view;

	}

	public void setImage(final Bitmap bitmap) {
		mPhoto = bitmap;
		if (mImageView != null) {
			mImageView.setImageBitmap(mPhoto);
		} else {
			Log.e("IconOnlyView", "mImageView == " + mImageView);
		}
	}
}