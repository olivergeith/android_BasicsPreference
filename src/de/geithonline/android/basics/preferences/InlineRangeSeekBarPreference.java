
package de.geithonline.android.basics.preferences;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.preference.Preference;
import android.preference.PreferenceManager;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import de.geithonline.android.basics.widgets.rangeseekbar.RangeSeekBar;
import de.geithonline.android.basics.widgets.rangeseekbar.RangeSeekBar.OnRangeSeekBarChangeListener;

public final class InlineRangeSeekBarPreference extends Preference {

    // Namespaces to read attributes
    // http://schemas.android.com/apk/lib/de.geithonline.android.basics.preferences
    private static final String PREFERENCE_NS = "http://schemas.android.com/apk/lib/de.geithonline.android.basics.preferences";
    // private static final String ANDROID_NS = "http://schemas.android.com/apk/res/android";

    // Real defaults
    private final int absoluteMaxValue;
    private final int absoluteMinValue;
    private final int stepValue;

    // Current value
    private int currentMinValue = 0;
    private int currentMaxValue = 0;

    // View elements
    private RangeSeekBar<Integer> rangeSeekBar;
    private final String keyMinValue;
    private final String keyMaxValue;
    private final int defaultMaxValue;
    private final int defaultMinValue;
    private TextView titleView;
    private static final int[][] states = new int[][] { //
            new int[] { android.R.attr.state_enabled }, // enabled
            new int[] { -android.R.attr.state_enabled }, // disabled
            new int[] { -android.R.attr.state_checked }, // unchecked
            new int[] { android.R.attr.state_pressed } // pressed
    };

    private static final int[] colors = new int[] { Color.WHITE, Color.argb(64, 255, 255, 255), Color.GREEN, Color.BLUE };
    private static final ColorStateList colorStateList = new ColorStateList(states, colors);

    public InlineRangeSeekBarPreference(final Context context, final AttributeSet attrs) {
        super(context, attrs);
        // Read parameters from attributes
        defaultMinValue = attrs.getAttributeIntValue(PREFERENCE_NS, "defaultMinValue", 0);
        defaultMaxValue = attrs.getAttributeIntValue(PREFERENCE_NS, "defaultMaxValue", 100);
        absoluteMinValue = attrs.getAttributeIntValue(PREFERENCE_NS, "absoluteMinValue", 0);
        absoluteMaxValue = attrs.getAttributeIntValue(PREFERENCE_NS, "absoluteMaxValue", 100);
        stepValue = attrs.getAttributeIntValue(PREFERENCE_NS, "step", 1);
        keyMinValue = attrs.getAttributeValue(PREFERENCE_NS, "keyMinValue");
        keyMaxValue = attrs.getAttributeValue(PREFERENCE_NS, "keyMaxValue");
        // Get current value from preferences
        readPreferences();
    }

    @Override
    protected View onCreateView(final ViewGroup parent) {
        final LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        final View view = inflater.inflate(R.layout.inline_range_seekbar_preference, parent, false);
        readPreferences();
        rangeSeekBar = (RangeSeekBar<Integer>) view.findViewById(R.id.rangebar);

        titleView = (TextView) view.findViewById(R.id.title);

        if (titleView != null) {
            titleView.setText(getTitle());
            titleView.setTextColor(colorStateList);
            Log.i("Titleview", "" + titleView.getText());
        } else {
            Log.i("Titleview", "null");
        }

        rangeSeekBar.setRangeValues(absoluteMinValue, absoluteMaxValue, stepValue);
        rangeSeekBar.setSelectedMinValue(currentMinValue);
        rangeSeekBar.setSelectedMaxValue(currentMaxValue);
        rangeSeekBar.setOnRangeSeekBarChangeListener(new OnRangeSeekBarChangeListener<Integer>() {
            @Override
            public void onRangeSeekBarValuesChanged(final RangeSeekBar<Integer> bar, final Integer minValue, final Integer maxValue) {
                currentMinValue = minValue.intValue();
                currentMaxValue = maxValue.intValue();
                persistPreferences();
            }
        });
        return view;
    }

    private String generateValueString() {
        if (currentMinValue == currentMaxValue) {
            return "" + currentMinValue;
        }
        return currentMinValue + " -> " + currentMaxValue;
    }

    public void persistPreferences() {
        final SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getContext());
        prefs.edit().putInt(keyMinValue, currentMinValue).commit();
        prefs.edit().putInt(keyMaxValue, currentMaxValue).commit();
        Log.i("RangeSeekBarPreference", "persistPreferences: " + generateValueString());
        notifyChanged();
    }

    private void readPreferences() {
        final SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getContext());
        currentMinValue = readIntegerPref(prefs, keyMinValue, defaultMinValue);
        currentMaxValue = readIntegerPref(prefs, keyMaxValue, defaultMaxValue);
        Log.i("RangeSeekBarPreference", "readPreferences: " + generateValueString());
    }

    private static int readIntegerPref(final SharedPreferences prefs, final String key, final int defaultValue) {
        if (prefs == null) {
            return defaultValue;
        }
        return prefs.getInt(key, defaultValue);
    }

    @Override
    public CharSequence getSummary() {
        // Format summary string with current value
        final String value = generateValueString();
        return String.format("%1$s", value);
    }
}