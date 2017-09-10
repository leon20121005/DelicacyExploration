package com.example.leon.delicacyexploration;

import android.os.Bundle;
import android.preference.EditTextPreference;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.preference.SwitchPreference;
import android.widget.Toast;
import android.content.SharedPreferences;

//Created by leon on 2017/9/11.

public class SettingsFragment extends PreferenceFragment
{
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        // Load the preferences from an XML resource
        addPreferencesFromResource(R.xml.preferences);

        SwitchPreference enableCustomIPSwitch = (SwitchPreference) findPreference(getString(R.string.custom_ip_enable_key));

        if (enableCustomIPSwitch != null)
        {
            enableCustomIPSwitch.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener()
            {
                @Override
                public boolean onPreferenceChange(Preference preference, Object newValue)
                {
                    boolean isEnableCustomIP = (Boolean) newValue;

                    if (!isEnableCustomIP)
                    {
                        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
                        SharedPreferences.Editor editor = preferences.edit();
                        editor.remove(getString(R.string.custom_ip_key));
                        editor.apply();

                        EditTextPreference editTextPreference = (EditTextPreference) findPreference(getString(R.string.custom_ip_key));
                        editTextPreference.setEnabled(false);

                        Toast.makeText(getActivity(), "使用預設伺服器IP", Toast.LENGTH_SHORT).show();
                    }
                    else
                    {
                        EditTextPreference editCustomIPPreference = (EditTextPreference) findPreference(getString(R.string.custom_ip_key));
                        editCustomIPPreference.setEnabled(true);
                    }
                    return true;
                }
            });
        }

        EditTextPreference editCustomIPPreference = (EditTextPreference) findPreference(getString(R.string.custom_ip_key));
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
        editCustomIPPreference.setEnabled(preferences.getBoolean(getString(R.string.custom_ip_enable_key), false));
    }
}
