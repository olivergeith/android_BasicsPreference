
package de.geithonline.android.basics.preferences;

import java.util.StringTokenizer;

import android.content.Context;
import android.preference.DialogPreference;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;
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
    private static final String ANDROID_NS = "http://schemas.android.com/apk/res/android";

    // Real defaults
    private final String mDefaultValue;
    private final int absolutMaxValue;
    private final int absoluteMinValue;
    private final int stepValue;

    // Current value
    private String mCurrentValue;
    private int currentMinValue;
    private int currentMaxValue;

    // View elements
    private RangeSeekBar<Integer> rangeSeekBar;
    private TextView mValueText;

    public RangeSeekBarPreference(final Context context, final AttributeSet attrs) {
        super(context, attrs);

        // Read parameters from attributes
        absoluteMinValue = attrs.getAttributeIntValue(PREFERENCE_NS, "absoluteMinValue", 0);
        absolutMaxValue = attrs.getAttributeIntValue(PREFERENCE_NS, "absoluteMaxValue", 100);
        stepValue = attrs.getAttributeIntValue(PREFERENCE_NS, "step", 1);
        mDefaultValue = attrs.getAttributeValue(ANDROID_NS, "10-90");
        // Log.i("SEEKBAR", "minValue=" + mMinValue);
        // Log.i("SEEKBAR", "maxValue=" + mMaxValue);
    }

    @SuppressWarnings("unchecked")
    @Override
    protected View onCreateDialogView() {
        // Get current value from preferences
        mCurrentValue = getPersistedString(mDefaultValue);
        initSelectedValues(mCurrentValue);

        // Inflate layout
        final LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        final View view = inflater.inflate(R.layout.range_seek_bar_preference, null);

        // Setup minimum and maximum text labels
        ((TextView) view.findViewById(R.id.min_value)).setText(Integer.toString(absoluteMinValue));
        ((TextView) view.findViewById(R.id.max_value)).setText(Integer.toString(absolutMaxValue));
        mValueText = (TextView) view.findViewById(R.id.current_value);
        rangeSeekBar = (RangeSeekBar<Integer>) view.findViewById(R.id.rangebar);

        rangeSeekBar.setRangeValues(absoluteMinValue, absolutMaxValue, stepValue);
        rangeSeekBar.setSelectedMinValue(currentMinValue);
        rangeSeekBar.setSelectedMaxValue(currentMaxValue);
        // Sets the display values of the indices
        rangeSeekBar.setOnRangeSeekBarChangeListener(new OnRangeSeekBarChangeListener<Integer>() {

            @Override
            public void onRangeSeekBarValuesChanged(final RangeSeekBar<Integer> bar, final Integer minValue, final Integer maxValue) {
                mValueText.setText(minValue + "-" + maxValue);
                mCurrentValue = minValue + "-" + maxValue;
                currentMinValue = minValue.intValue();
                currentMaxValue = maxValue.intValue();
            }

        });

        // Setup text label for current value
        mValueText.setText(rangeSeekBar.getSelectedMinValue() + "-" + rangeSeekBar.getSelectedMaxValue());

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
            persistString(mCurrentValue);
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
        final String value = getPersistedString(mDefaultValue);
        return String.format(summary, value);
    }

    private void initSelectedValues(final String currentValue) {
        // Tokenizer initialisieren
        final StringTokenizer tokenizer = new StringTokenizer(currentValue, "-", false);
        // Daten lesen in einen Vector einlesen
        if (tokenizer.countTokens() == 2) {
            try {
                currentMinValue = Integer.parseInt(tokenizer.nextToken());
                currentMaxValue = Integer.parseInt(tokenizer.nextToken());
            } catch (final NumberFormatException e) {
                Log.i("initSelectedValues", "Read String was " + currentValue);
                return;
            }
        }
    }

}