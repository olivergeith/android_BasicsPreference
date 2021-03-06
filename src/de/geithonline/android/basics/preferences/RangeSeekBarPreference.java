
package de.geithonline.android.basics.preferences;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.preference.DialogPreference;
import android.preference.PreferenceManager;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import de.geithonline.android.basics.widgets.rangeseekbar.RangeSeekBar;
import de.geithonline.android.basics.widgets.rangeseekbar.RangeSeekBar.OnRangeSeekBarChangeListener;

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
public final class RangeSeekBarPreference extends DialogPreference {

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

    public RangeSeekBarPreference(final Context context, final AttributeSet attrs) {
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

    @SuppressLint("InflateParams")
    @SuppressWarnings("unchecked")
    @Override
    protected View onCreateDialogView() {
        // reading the things to show
        readPreferences();

        // Inflate layout
        final LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        final View view = inflater.inflate(R.layout.range_seek_bar_preference, null);
        rangeSeekBar = (RangeSeekBar<Integer>) view.findViewById(R.id.rangebar);
        rangeSeekBar.setRangeValues(absoluteMinValue, absoluteMaxValue, stepValue);
        rangeSeekBar.setSelectedMinValue(currentMinValue);
        rangeSeekBar.setSelectedMaxValue(currentMaxValue);
        rangeSeekBar.setOnRangeSeekBarChangeListener(new OnRangeSeekBarChangeListener<Integer>() {
            @Override
            public void onRangeSeekBarValuesChanged(final RangeSeekBar<Integer> bar, final Integer minValue, final Integer maxValue) {
                currentMinValue = minValue.intValue();
                currentMaxValue = maxValue.intValue();
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

    @Override
    protected void onDialogClosed(final boolean positiveResult) {
        super.onDialogClosed(positiveResult);

        // Return if change was cancelled
        if (!positiveResult) {
            return;
        }
        if (shouldPersist()) {
            persistPreferences();
        }
        // Notify activity about changes (to update preference summary line)
        notifyChanged();
    }

    public void persistPreferences() {
        final SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getContext());
        prefs.edit().putInt(keyMinValue, currentMinValue).commit();
        prefs.edit().putInt(keyMaxValue, currentMaxValue).commit();
        // Log.i("RangeSeekBarPreference", "persistPreferences: " + generateValueString());
    }

    private void readPreferences() {
        final SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getContext());
        currentMinValue = readIntegerPref(prefs, keyMinValue, defaultMinValue);
        currentMaxValue = readIntegerPref(prefs, keyMaxValue, defaultMaxValue);
        // Log.i("RangeSeekBarPreference", "readPreferences: " + generateValueString());
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
        String summary = "";
        if (super.getSummary() != null) {
            summary = super.getSummary().toString();
        }
        final String value = generateValueString();
        return String.format(summary, value);
    }
}