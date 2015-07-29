package com.companionfree.nanodegree.project1.fragment;

import android.content.DialogInterface;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.support.v7.app.AlertDialog;
import android.util.Xml;
import android.widget.ProgressBar;

import com.companionfree.nanodegree.project1.R;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Locale;

import butterknife.InjectView;

/**
 * Created by Laptop on 7/26/2015
 */
public class SettingsFragment extends PreferenceFragment implements Preference.OnPreferenceClickListener{

    private Preference countryCode;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.preferences);

        countryCode = findPreference("pref_country");
        countryCode.setOnPreferenceClickListener(this);

    }

    @Override
    public boolean onPreferenceClick(Preference preference) {

        XmlPullParser xmlpullparser = Xml.newPullParser();

        InputStream in_s = null;
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
        }


        String[] countryNames = new String[countries.size()];
        int x=0;
        for (Country country : countries) {
            countryNames[x] = country.name;
            x++;
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(R.string.pref_country)
                .setItems(countryNames, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                // The 'which' argument contains the index position
                // of the selected item
            }
        });
        builder.create().show();
        return false;
    }

    private ArrayList<Country> parseXML(XmlPullParser parser) throws XmlPullParserException,IOException
    {
        ArrayList<Country> countriesList = null;
        int eventType = parser.getEventType();
        Country currentProduct = null;

        while (eventType != XmlPullParser.END_DOCUMENT){
            String name = null;
            switch (eventType){
                case XmlPullParser.START_DOCUMENT:
                    countriesList = new ArrayList<>();
                    break;
                case XmlPullParser.START_TAG:
                    name = parser.getName();
                    if (name.equals("country")){
                        currentProduct = new Country();
                    } else if (currentProduct != null){
                        if (name.equals("code")){
                            currentProduct.code = parser.nextText();
                        } else if (name.equals("name")){
                            currentProduct.name = parser.nextText();
                        }
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


    class Country {
        public String code;
        public String name;
    }

}
