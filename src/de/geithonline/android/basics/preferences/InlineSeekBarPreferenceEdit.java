
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
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnFocusChangeListener;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;

public class InlineSeekBarPreferenceEdit extends Preference implements OnSeekBarChangeListener, OnEditorActionListener, OnFocusChangeListener {
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
    private EditText editView;

    private static final int[][] states = new int[][] { //
            new int[] { android.R.attr.state_enabled }, // enabled
            new int[] { -android.R.attr.state_enabled }, // disabled
            new int[] { -android.R.attr.state_checked }, // unchecked
            new int[] { android.R.attr.state_pressed } // pressed
    };

    private static final int[] colors = new int[] { Color.WHITE, Color.argb(64, 255, 255, 255), Color.GREEN, Color.BLUE };

    private static final ColorStateList colorStateList = new ColorStateList(states, colors);

    public InlineSeekBarPreferenceEdit(final Context context, final AttributeSet attrs) {
        super(context, attrs);
        mMinValue = attrs.getAttributeIntValue(PREFERENCE_NS, ATTR_MIN_VALUE, DEFAULT_MIN_VALUE);
        mMaxValue = attrs.getAttributeIntValue(PREFERENCE_NS, ATTR_MAX_VALUE, DEFAULT_MAX_VALUE);
        mStepValue = attrs.getAttributeIntValue(PREFERENCE_NS, ATTR_STEP_VALUE, DEFAULT_STEP_VALUE);
        mDefaultValue = attrs.getAttributeIntValue(ANDROID_NS, ATTR_DEFAULT_VALUE, DEFAULT_CURRENT_VALUE);
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
        final View view = inflater.inflate(R.layout.inline_seekbar_preference_edit, parent, false);
        minTextView = (TextView) view.findViewById(R.id.min_value);
        maxTextView = (TextView) view.findViewById(R.id.max_value);
        titleView = (TextView) view.findViewById(R.id.title);
        editView = (EditText) view.findViewById(R.id.editValue);
        // setting accent color to valuetextview
        editView.setTextColor(getColorStateListAccentColor());
        editView.setMaxLines(1);
        if (titleView != null) {
            titleView.setText(getTitle());
            titleView.setTextColor(colorStateList);
        } else {}

        mSeekBar = (SeekBar) view.findViewById(R.id.seekbar);
        // Log.i("InlineSeek " + getTitle().toString(), "onCreateView - current= " + mCurrentValue);
        mSeekBar.setMax(mMaxValue - mMinValue);
        mSeekBar.setProgress(mCurrentValue - mMinValue);
        // register listeners
        mSeekBar.setOnSeekBarChangeListener(this);
        editView.setOnEditorActionListener(this);
        editView.setOnFocusChangeListener(this);

        // Setup text label for current value
        editView.setText(Integer.toString(mCurrentValue));
        editView.setText(Integer.toString(mCurrentValue));
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
    }

    @Override
    public void onFocusChange(final View v, final boolean hasFocus) {
        Log.i("EditMode", "Focus = " + hasFocus);
        if (!hasFocus) {
            // calculate();
        }
    }

    @Override
    public boolean onEditorAction(final TextView v, final int actionId, final KeyEvent event) {
        Log.i("EditText", "actionId = " + actionId);

        // if (event != null && (event.getKeyCode() == KeyEvent.KEYCODE_ENTER)) {
        if (actionId == EditorInfo.IME_ACTION_DONE) {
            int val = Integer.parseInt(editView.getText().toString());
            if (val > mMaxValue) {
                val = mMaxValue;
            }
            if (val < mMinValue) {
                val = mMinValue;
            }
            mCurrentValue = val;
            mSeekBar.setProgress(mCurrentValue - mMinValue);

            Log.i("EditText", "Value = " + mCurrentValue);
            // enableEditMode(false);

            persistPreferences();
            return true;
        }
        return false;
    }

    private void enableKeyboard(final EditText editText, final boolean show) {
        final InputMethodManager imm = (InputMethodManager) getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        if (show) {
            imm.showSoftInputFromInputMethod(editText.getWindowToken(), 0);
        } else {
            imm.hideSoftInputFromWindow(editText.getWindowToken(), 0);
        }

    }

    @Override
    public void onProgressChanged(final SeekBar seek, int value, final boolean fromTouch) {
        // Update current value
        value = (Math.round(value / mStepValue)) * mStepValue;
        mSeekBar.setProgress(value);
        mCurrentValue = value + mMinValue;
        // Update label with current value
        editView.setText(Integer.toString(mCurrentValue));
        editView.setText(Integer.toString(mCurrentValue));
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
