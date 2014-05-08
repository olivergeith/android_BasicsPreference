package de.geithonline.android.basics.preferences;

import android.content.Context;
import android.preference.DialogPreference;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;

/**
 * @author geith
 * 
 *         How to use: Import lib and in preferences xml: <br>
 * 
 *         <PreferenceScreen xmlns:android="http://schemas.android.com/apk/res/android"<br>
 *         xmlns:seekbarpreference="http://schemas.android.com/apk/lib/de.geithonline.android.basics.preferences"><br>
 * 
 *         <de.geithonline.android.basics.preferences.SeekBarPreference <br>
 *         android:key="anzahlPatterns"<br>
 *         android:title="@string/pattern_anzahlPatterns"<br>
 *         seekbarpreference:minValue="10"<br>
 *         seekbarpreference:maxValue="1500"<br>
 *         android:summary="Draw %1$d Patterns" <br>
 *         android:defaultValue="1000"<br>
 *         android:dialogMessage="@string/pattern_anzahlPatterns" /><br>
 * 
 * 
 * 
 */
public final class SeekBarPreference extends DialogPreference implements OnSeekBarChangeListener {

	// Namespaces to read attributes
	// http://schemas.android.com/apk/lib/de.geithonline.android.basics.preferences
	private static final String PREFERENCE_NS = "http://schemas.android.com/apk/lib/de.geithonline.android.basics.preferences";
	private static final String ANDROID_NS = "http://schemas.android.com/apk/res/android";

	// Attribute names
	private static final String ATTR_DEFAULT_VALUE = "defaultValue";
	private static final String ATTR_MIN_VALUE = "minValue";
	private static final String ATTR_MAX_VALUE = "maxValue";
	private static final String ATTR_STEP_VALUE = "stepValue";

	// Default values for defaults
	private static final int DEFAULT_CURRENT_VALUE = 50;
	private static final int DEFAULT_MIN_VALUE = 0;
	private static final int DEFAULT_MAX_VALUE = 100;
	private static final int DEFAULT_STEP_VALUE = 1;

	// Real defaults
	private final int mDefaultValue;
	private final int mMaxValue;
	private final int mMinValue;
	private final int mStepValue;

	// Current value
	private int mCurrentValue;

	// View elements
	private SeekBar mSeekBar;
	private TextView mValueText;

	public SeekBarPreference(final Context context, final AttributeSet attrs) {
		super(context, attrs);

		// Read parameters from attributes
		mMinValue = attrs.getAttributeIntValue(PREFERENCE_NS, ATTR_MIN_VALUE, DEFAULT_MIN_VALUE);
		mMaxValue = attrs.getAttributeIntValue(PREFERENCE_NS, ATTR_MAX_VALUE, DEFAULT_MAX_VALUE);
		mStepValue = attrs.getAttributeIntValue(PREFERENCE_NS, ATTR_STEP_VALUE, DEFAULT_STEP_VALUE);
		mDefaultValue = attrs.getAttributeIntValue(ANDROID_NS, ATTR_DEFAULT_VALUE, DEFAULT_CURRENT_VALUE);
		// Log.i("SEEKBAR", "minValue=" + mMinValue);
		// Log.i("SEEKBAR", "maxValue=" + mMaxValue);
	}

	@Override
	protected View onCreateDialogView() {
		// Get current value from preferences
		mCurrentValue = getPersistedInt(mDefaultValue);

		// Inflate layout
		final LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		final View view = inflater.inflate(R.layout.seek_bar_preference, null);

		// Setup minimum and maximum text labels
		((TextView) view.findViewById(R.id.min_value)).setText(Integer.toString(mMinValue));
		((TextView) view.findViewById(R.id.max_value)).setText(Integer.toString(mMaxValue));

		// Setup SeekBar
		mSeekBar = (SeekBar) view.findViewById(R.id.seek_bar);
		mSeekBar.setMax(mMaxValue - mMinValue);
		mSeekBar.setProgress(mCurrentValue - mMinValue);
		mSeekBar.setOnSeekBarChangeListener(this);

		// Setup text label for current value
		mValueText = (TextView) view.findViewById(R.id.current_value);
		mValueText.setText(Integer.toString(mCurrentValue));

		return view;
	}

	@Override
	protected void onDialogClosed(final boolean positiveResult) {
		super.onDialogClosed(positiveResult);

		// Return if change was cancelled
		if (!positiveResult) {
			return;
		}

		// Persist current value if needed
		if (shouldPersist()) {
			persistInt(mCurrentValue);
		}

		// Notify activity about changes (to update preference summary line)
		notifyChanged();
	}

	@Override
	public CharSequence getSummary() {
		// Format summary string with current value
		String summary = "";
		if (super.getSummary() != null) {
			summary = super.getSummary().toString();
		}
		final int value = getPersistedInt(mDefaultValue);
		return String.format(summary, value);
	}

	@Override
	public void onProgressChanged(final SeekBar seek, int value, final boolean fromTouch) {
		// Update current value
		value = (Math.round(value / mStepValue)) * mStepValue;
		mSeekBar.setProgress(value);
		mCurrentValue = value + mMinValue;
		// Update label with current value
		mValueText.setText(Integer.toString(mCurrentValue));
	}

	@Override
	public void onStartTrackingTouch(final SeekBar seek) {
		// Not used
	}

	@Override
	public void onStopTrackingTouch(final SeekBar seek) {
		// Not used
	}
}