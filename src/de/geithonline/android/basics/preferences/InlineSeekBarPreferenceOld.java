
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
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;

public class InlineSeekBarPreferenceOld extends Preference implements OnSeekBarChangeListener {
    private static final String PREFERENCE_NS = "http://schemas.android.com/apk/lib/de.geithonline.android.basics.preferences";
    private static final String ANDROID_NS = "http://schemas.android.com/apk/res/android";

    // Default values for defaults
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

    private boolean zoomed = false;
    private final int mMinZoomValue;
    private final int mMaxZoomValue;
    private final int mStepZoomValue;

    private static final int[][] states = new int[][] { //
            new int[] { android.R.attr.state_enabled }, // enabled
            new int[] { -android.R.attr.state_enabled }, // disabled
            new int[] { -android.R.attr.state_checked }, // unchecked
            new int[] { android.R.attr.state_pressed } // pressed
    };

    private static final int[] colors = new int[] { Color.WHITE, Color.argb(64, 255, 255, 255), Color.GREEN, Color.BLUE };

    private static final ColorStateList colorStateList = new ColorStateList(states, colors);

    public InlineSeekBarPreferenceOld(final Context context, final AttributeSet attrs) {
        super(context, attrs);
        mMinValue = attrs.getAttributeIntValue(PREFERENCE_NS, "minValue", 0);
        mMaxValue = attrs.getAttributeIntValue(PREFERENCE_NS, "maxValue", 100);
        mStepValue = attrs.getAttributeIntValue(PREFERENCE_NS, "stepValue", 1);
        mMinZoomValue = attrs.getAttributeIntValue(PREFERENCE_NS, "minZoomValue", mMinValue);
        mMaxZoomValue = attrs.getAttributeIntValue(PREFERENCE_NS, "maxZoomValue", mMaxValue);
        mStepZoomValue = attrs.getAttributeIntValue(PREFERENCE_NS, "stepZoomValue", mStepValue);
        mDefaultValue = attrs.getAttributeIntValue(ANDROID_NS, "defaultValue", 50);
        // Get current value from preferences
        readPreferences();
    }

    private int getAccentColor() {
        final TypedValue tValue = new TypedValue();
        getContext().getTheme().resolveAttribute(android.R.attr.colorAccent, tValue, true);
        if (tValue.type >= TypedValue.TYPE_FIRST_COLOR_INT && tValue.type <= TypedValue.TYPE_LAST_COLOR_INT) {
            // windowBackground is a color
            // Log.i("RangeSeekBar", "Theme AccentColor found");
            return tValue.data;
        } else {
            // Log.i("RangeSeekBar", "Theme AccentColor not found");
            return Color.WHITE;
        }

    }

    private ColorStateList getColorStateListAccentColor() {
        final int acc = getAccentColor();
        final int accTrans = Color.argb(64, Color.red(acc), Color.green(acc), Color.blue(acc));
        final int[] colors = new int[] { acc, accTrans, Color.GREEN, Color.BLUE };
        final ColorStateList colorStateList = new ColorStateList(states, colors);
        return colorStateList;
    }

    @Override
    protected View onCreateView(final ViewGroup parent) {
        final LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        final View view = inflater.inflate(R.layout.inline_seekbar_preference2, parent, false);
        valueTextView = (TextView) view.findViewById(R.id.current_value);
        minTextView = (TextView) view.findViewById(R.id.min_value);
        maxTextView = (TextView) view.findViewById(R.id.max_value);
        titleView = (TextView) view.findViewById(R.id.title);

        // setting accent color to valuetextview
        valueTextView.setTextColor(getColorStateListAccentColor());

        if (titleView != null) {
            titleView.setText(getTitle());
            titleView.setTextColor(colorStateList);
        } else {}

        mSeekBar = (SeekBar) view.findViewById(R.id.seekbar);

        valueTextView.setClickable(true);
        valueTextView.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(final View v) {
                zoom(!zoomed);
            }
        });
        // Log.i("InlineSeek " + getTitle().toString(), "onCreateView - current= " + mCurrentValue);
        mSeekBar.setOnSeekBarChangeListener(this);
        // Setup text label for current value
        valueTextView.setText(Integer.toString(mCurrentValue));
        zoom(zoomed);
        setProgressBarAndLabel(mCurrentValue);

        // Get current value from preferences
        readPreferences();
        return view;
    }

    private void zoom(final boolean zoomed) {
        Log.i("InlineSeek", "Zooming: " + zoomed);
        this.zoomed = zoomed;
        if (zoomed == true) {
            // zooming
            maxTextView.setText(Integer.toString(mMaxZoomValue));
            minTextView.setText(Integer.toString(mMinZoomValue));
            if (mCurrentValue > mMaxZoomValue) {
                mCurrentValue = mMaxZoomValue;
            }
            if (mCurrentValue < mMinZoomValue) {
                mCurrentValue = mMinZoomValue;
            }
            Log.i("zoomin on ", "current = " + mCurrentValue);
            setProgressBarAndLabel(mCurrentValue);
        } else {
            // zooming out
            maxTextView.setText(Integer.toString(mMaxValue));
            minTextView.setText(Integer.toString(mMinValue));
            setProgressBarAndLabel(mCurrentValue);
        }

    }

    private void setProgressBarAndLabel(final int val) {
        if (zoomed) {
            mSeekBar.setMax(mMaxZoomValue - mMinZoomValue);
            mSeekBar.setProgress(val - mMinZoomValue);
        } else {
            mSeekBar.setMax(mMaxValue - mMinValue);
            mSeekBar.setProgress(val - mMinValue);
        }
        valueTextView.setText(Integer.toString(mCurrentValue));
    }

    @Override
    protected void onBindView(final View view) {
        super.onBindView(view);
        // Log.i("InlineSeek " + getTitle().toString(), "onBindView - cureent " + mCurrentValue);
        readPreferences();
        setProgressBarAndLabel(mCurrentValue);
        // mSeekBar.setMax(mMaxValue - mMinValue);
    }

    @Override
    public void onProgressChanged(final SeekBar seek, int value, final boolean fromTouch) {
        // Update current value
        if (zoomed) {
            // snap value to step-grid
            value = (Math.round(value / mStepZoomValue)) * mStepZoomValue;
            mCurrentValue = value + mMinZoomValue;
        } else {
            // snap value to step-grid
            value = (Math.round(value / mStepValue)) * mStepValue;
            mCurrentValue = value + mMinValue;
        }
        setProgressBarAndLabel(mCurrentValue);
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
