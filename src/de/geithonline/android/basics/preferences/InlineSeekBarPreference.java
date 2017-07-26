
package de.geithonline.android.basics.preferences;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.preference.Preference;
import android.preference.PreferenceManager;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;

public class InlineSeekBarPreference extends Preference implements OnSeekBarChangeListener {
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

    private SeekBar mSeekBar;
    private TextView valueTextView;
    // Real defaults
    private final int mDefaultValue;
    private final int mMaxValue;
    private final int mMinValue;
    private final int mStepValue;
    // Current value
    private int mCurrentValue;
    private TextView minTextView;
    private TextView maxTextView;
    private TextView titleView;

    private static final int[][] states = new int[][] { //
            new int[] { android.R.attr.state_enabled }, // enabled
            new int[] { -android.R.attr.state_enabled }, // disabled
            new int[] { -android.R.attr.state_checked }, // unchecked
            new int[] { android.R.attr.state_pressed } // pressed
    };

    private static final int[] colors = new int[] { Color.WHITE, Color.argb(64, 255, 255, 255), Color.GREEN, Color.BLUE };

    private static final ColorStateList colorStateList = new ColorStateList(states, colors);

    public InlineSeekBarPreference(final Context context, final AttributeSet attrs) {
        super(context, attrs);
        mMinValue = attrs.getAttributeIntValue(PREFERENCE_NS, ATTR_MIN_VALUE, DEFAULT_MIN_VALUE);
        mMaxValue = attrs.getAttributeIntValue(PREFERENCE_NS, ATTR_MAX_VALUE, DEFAULT_MAX_VALUE);
        mStepValue = attrs.getAttributeIntValue(PREFERENCE_NS, ATTR_STEP_VALUE, DEFAULT_STEP_VALUE);
        mDefaultValue = attrs.getAttributeIntValue(ANDROID_NS, ATTR_DEFAULT_VALUE, DEFAULT_CURRENT_VALUE);
        // Get current value from preferences
        readPreferences();
    }

    @Override
    protected View onCreateView(final ViewGroup parent) {
        final LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        final View view = inflater.inflate(R.layout.inline_seekbar_preference2, parent, false);
        valueTextView = (TextView) view.findViewById(R.id.current_value);
        minTextView = (TextView) view.findViewById(R.id.min_value);
        maxTextView = (TextView) view.findViewById(R.id.max_value);
        titleView = (TextView) view.findViewById(R.id.title);

        if (titleView != null) {
            titleView.setText(getTitle());
            titleView.setTextColor(colorStateList);
            Log.i("Titleview", "" + titleView.getText());
        } else {
            Log.i("Titleview", "null");
        }

        mSeekBar = (SeekBar) view.findViewById(R.id.seekbar);
        // Log.i("InlineSeek " + getTitle().toString(), "onCreateView - current= " + mCurrentValue);
        mSeekBar.setMax(mMaxValue - mMinValue);
        mSeekBar.setProgress(mCurrentValue - mMinValue);
        mSeekBar.setOnSeekBarChangeListener(this);
        // Setup text label for current value
        valueTextView.setText(Integer.toString(mCurrentValue));
        minTextView.setText(Integer.toString(mMinValue));
        maxTextView.setText(Integer.toString(mMaxValue));
        // Get current value from preferences
        readPreferences();
        return view;
    }

    @Override
    protected void onBindView(final View view) {
        super.onBindView(view);
        // Log.i("InlineSeek " + getTitle().toString(), "onBindView - cureent " + mCurrentValue);
        readPreferences();
        mSeekBar.setProgress(mCurrentValue - mMinValue);
        // mSeekBar.setMax(mMaxValue - mMinValue);
    }

    @Override
    public void onProgressChanged(final SeekBar seek, int value, final boolean fromTouch) {
        // Update current value
        value = (Math.round(value / mStepValue)) * mStepValue;
        mSeekBar.setProgress(value);
        mCurrentValue = value + mMinValue;
        // Update label with current value
        valueTextView.setText(Integer.toString(mCurrentValue));
    }

    @Override
    protected Object onGetDefaultValue(final TypedArray a, final int index) {
        // Log.i("InlineSeek ", "onGetDefaultValue - current " + mDefaultValue);
        return mDefaultValue;
    }

    @Override
    public void onStartTrackingTouch(final SeekBar seekBar) {}

    @Override
    public void onStopTrackingTouch(final SeekBar seekBar) {
        // mValueText.setText("");
        persistPreferences();
    }

    @Override
    protected void onSetInitialValue(final boolean restoreValue, final Object defaultValue) {
        // setValue();
    }

    @Override
    public CharSequence getSummary() {
        // Log.i("InlineSeek " + getTitle().toString(), "getSummary");
        // Format summary string with current value
        String summary = "";
        if (super.getSummary() != null) {
            summary = super.getSummary().toString();
        }
        return String.format(summary, mCurrentValue);
    }

    public void persistPreferences() {
        final SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getContext());
        prefs.edit().putInt(getKey(), mCurrentValue).commit();
        notifyChanged();
        // Log.i("InlineSeek", "persistPreferences: " + mCurrentValue);
    }

    private void readPreferences() {
        final SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getContext());
        mCurrentValue = readIntegerPref(prefs, getKey(), mDefaultValue);
        // Log.i("InlineSeek", "readPreferences: " + mCurrentValue);
    }

    private static int readIntegerPref(final SharedPreferences prefs, final String key, final int defaultValue) {
        if (prefs == null) {
            return defaultValue;
        }
        return prefs.getInt(key, defaultValue);
    }

}
