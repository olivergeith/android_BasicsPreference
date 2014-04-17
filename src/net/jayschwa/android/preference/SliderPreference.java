/*
 * Copyright 2012 Jay Weisskopf
 *
 * Licensed under the MIT License (see LICENSE.txt)
 */

package net.jayschwa.android.preference;

import android.content.Context;
import android.content.res.TypedArray;
import android.preference.DialogPreference;
import android.util.AttributeSet;
import android.view.View;
import android.widget.SeekBar;
import android.widget.TextView;

/**
 * @author Jay Weisskopf
 */
public class SliderPreference extends DialogPreference {

	protected final static int SEEKBAR_RESOLUTION = 100;

	protected float mValue;
	protected int mSeekBarValue;

	/**
	 * @param context
	 * @param attrs
	 */
	public SliderPreference(final Context context, final AttributeSet attrs) {
		super(context, attrs);
		setup(context, attrs);
	}

	/**
	 * @param context
	 * @param attrs
	 * @param defStyle
	 */
	public SliderPreference(final Context context, final AttributeSet attrs, final int defStyle) {
		super(context, attrs, defStyle);
		setup(context, attrs);
	}

	private void setup(final Context context, final AttributeSet attrs) {
		setDialogLayoutResource(R.layout.slider_preference_dialog);
		final TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.SliderPreference);
		a.recycle();
	}

	@Override
	protected Object onGetDefaultValue(final TypedArray a, final int index) {
		return a.getFloat(index, 0);
	}

	@Override
	protected void onSetInitialValue(final boolean restoreValue, final Object defaultValue) {
		setValue(restoreValue ? getPersistedFloat(mValue) : (Float) defaultValue);
	}

	@Override
	public CharSequence getSummary() {
		return super.getSummary();
	}

	@Override
	public void setSummary(final CharSequence summary) {
		super.setSummary(summary);
	}

	public float getValue() {
		return mValue;
	}

	public void setValue(float value) {
		value = Math.max(0, Math.min(value, 1)); // clamp to [0, 1]
		if (shouldPersist()) {
			persistFloat(value);
		}
		if (value != mValue) {
			mValue = value;
			notifyChanged();
		}
		setSummary("Value = " + mValue);
	}

	@Override
	protected View onCreateDialogView() {
		mSeekBarValue = (int) (mValue * SEEKBAR_RESOLUTION);
		final View view = super.onCreateDialogView();
		final TextView message = (TextView) view.findViewById(android.R.id.message);
		final SeekBar seekbar = (SeekBar) view.findViewById(R.id.slider_preference_seekbar);
		message.setText("Value: " + mSeekBarValue);
		seekbar.setMax(SEEKBAR_RESOLUTION);
		seekbar.setProgress(mSeekBarValue);
		seekbar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {

			@Override
			public void onStopTrackingTouch(final SeekBar seekBar) {
			}

			@Override
			public void onStartTrackingTouch(final SeekBar seekBar) {
			}

			@Override
			public void onProgressChanged(final SeekBar seekBar, final int progress, final boolean fromUser) {
				if (fromUser) {
					mSeekBarValue = progress;
					message.setText("Value: " + mSeekBarValue);
				}
			}
		});
		return view;
	}

	@Override
	protected void onDialogClosed(final boolean positiveResult) {
		final float newValue = (float) mSeekBarValue / SEEKBAR_RESOLUTION;
		if (positiveResult && callChangeListener(newValue)) {
			setValue(newValue);
		}
		super.onDialogClosed(positiveResult);
	}
}
