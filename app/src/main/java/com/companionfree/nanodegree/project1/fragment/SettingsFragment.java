package com.companionfree.nanodegree.project1.fragment;

import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.util.Xml;

import com.companionfree.nanodegree.project1.R;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Laptop on 7/26/2015
 */
public class SettingsFragment extends PreferenceFragment implements Preference.OnPreferenceClickListener,
        Preference.OnPreferenceChangeListener {

    private CheckBoxPreference notificationPreference;
    public static final String PREF_COUNTRY_CODE = "pref_country_code";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.preferences);

        Preference countryCode = findPreference("pref_country");
        countryCode.setOnPreferenceClickListener(this);

    // TODO necessary??  Might change preference automatically
        notificationPreference = (CheckBoxPreference) findPreference("pref_notification");
        notificationPreference.setOnPreferenceChangeListener(this);

    }

    @Override
    public boolean onPreferenceClick(Preference preference) {

        XmlPullParser xmlpullparser = Xml.newPullParser();

        InputStream in_s;
        ArrayList<Country> countries = new ArrayList<>();
        try {
            in_s = getActivity().getApplicationContext().getAssets().open("countries.xml");
            xmlpullparser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false);
            xmlpullparser.setInput(in_s, null);

            countries = parseXML(xmlpullparser);
        } catch (XmlPullParserException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e) { // TODO work out these for display?
            e.printStackTrace();
        }


        String[] countryNames = new String[countries.size()];
        int x=0;
        for (Country country : countries) {
            countryNames[x] = country.name;
            x++;
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        final ArrayList<Country> finalCountries = countries;
        builder.setTitle(R.string.pref_country)
                .setItems(countryNames, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                // The 'which' argument contains the index position
                // of the selected item
                Country country = finalCountries.get(which);
                SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
                SharedPreferences.Editor edit = prefs.edit();
                edit.putString(PREF_COUNTRY_CODE, country.code);
                edit.apply();
            }
        });
        builder.create().show();
        return false;
    }




    private ArrayList<Country> parseXML(XmlPullParser parser) throws Exception {
        ArrayList<Country> countriesList = null;
        int eventType = parser.getEventType();
        Country currentProduct = null;

        while (eventType != XmlPullParser.END_DOCUMENT){
            String name;
            switch (eventType){
                case XmlPullParser.START_DOCUMENT:
                    countriesList = new ArrayList<>();
                    break;
                case XmlPullParser.START_TAG:
                    name = parser.getName();
                    if (name.equals("country")){
                        Map<String,String> attributes = getAttributes(parser);
                        currentProduct = new Country(attributes.get("name"), attributes.get("code"));
                    }
                    break;
                case XmlPullParser.END_TAG:
                    name = parser.getName();
                    if (name.equalsIgnoreCase("country") && currentProduct != null){
                        if (countriesList != null) {
                            countriesList.add(currentProduct);
                        }

                    }
            }
            eventType = parser.next();
        }
        return countriesList;
    }


    private Map<String,String> getAttributes(XmlPullParser parser) throws Exception {
        Map<String,String> attrs;
        int acount=parser.getAttributeCount();
        if(acount != -1) {
            Log.d(getClass().getSimpleName(), "Attributes for [" + parser.getName() + "]");
            attrs = new HashMap<>(acount);
            for(int x=0;x<acount;x++) {
                Log.d(getClass().getSimpleName(),"\t["+parser.getAttributeName(x)+"]=" +
                        "["+parser.getAttributeValue(x)+"]");
                attrs.put(parser.getAttributeName(x), parser.getAttributeValue(x));
            }
        }
        else {
            throw new Exception("Required entity attributes missing"); //TODO need to display the error
        }
        return attrs;
    }

    @Override
    public boolean onPreferenceChange(Preference preference, Object newValue) {
        if (preference == notificationPreference) {

            return true;
        }
        return false;
    }


    class Country {
        public String code;
        public String name;

        private Country(String name, String code) {
            this.code = code;
            this.name = name;
        }
    }

}
