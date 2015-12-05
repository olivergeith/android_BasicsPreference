package de.geithonline.android.basics.preferences;

import android.content.Context;
import android.preference.ListPreference;
import android.preference.Preference;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;

public class CoolListPreference extends ListPreference {

	public CoolListPreference(final Context context, final AttributeSet attrs) {
		super(context, attrs);

		// setSummary(getEntry());
	}

	@Override
	protected View onCreateView(final ViewGroup parent) {
		setOnPreferenceChangeListener(new OnPreferenceChangeListener() {

			@Override
			public boolean onPreferenceChange(final Preference preference, final Object newValue) {
				setSummary("" + newValue);
				return true;
			}
		});
		setSummary(getEntry());
		return super.onCreateView(parent);
	}

}
