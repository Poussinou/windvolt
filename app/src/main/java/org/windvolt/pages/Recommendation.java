/*
    This file is part of windvolt.org.

    Copyright (c) 2020 Max Sumer

    This program is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with this program.  If not, see <http://www.gnu.org/licenses/>.
*/
package org.windvolt.pages;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.preference.PreferenceManager;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import org.windvolt.R;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;

public class Recommendation extends Fragment {

    final String geo_delim = " / ";

    final int LOAD_NOT_AVAILABLE = -1;
    final int LOAD_NOT_RECOMMENDED = 0;
    final int LOAD_SMART_DEVICES = 1;
    final int LOAD_MORE_DEVICES = 10;
    final int LOAD_MANY_DEVICES = 11;

    ArrayList<String> names = new ArrayList<>();
    ArrayList<String> allnames = new ArrayList<>();

    AutoCompleteTextView location;
    TextView display, geodata;
 
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        // load from settings
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getContext());


        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.recommendation, container, false);

        // UPDATE RECOMMENDATION
        // TODO calculate recommendation
        setRecommendation(view, LOAD_NOT_AVAILABLE);
        ImageView icon = view.findViewById(R.id.recommendation_image);

        FloatingActionButton services_open = view.findViewById(R.id.services_open);
        services_open.setOnClickListener(services);

        // UPDATE LOCATION AND GEODATA
        // display location
        display = view.findViewById(R.id.location_display);

        // load location name
        String loc = sharedPreferences.getString("location_input", "");
        if (loc.isEmpty()) { loc = getString(R.string.location_notice); } // location_notice

        display.setText(loc);
        display.setOnClickListener(toggler);



        // load geodata
        geodata = view.findViewById(R.id.location_geodata);

        String longitude = sharedPreferences.getString("location_longitude", "");
        String latitude = sharedPreferences.getString("location_latitude", "");

        displayLocation(longitude, latitude);


        return view;
    }

    public void setRecommendation(View view, int state) {

        // display recommendation
        TextView recommend = view.findViewById(R.id.recommendation_text);

        switch (state) {
            case LOAD_NOT_RECOMMENDED:
                recommend.setText("laden nur falls nötig");
                break;

            case LOAD_SMART_DEVICES:
                recommend.setText("kleine Geräte laden");
                break;

            case LOAD_MORE_DEVICES:
                recommend.setText("große Geräte laden");
                break;

            case LOAD_MANY_DEVICES:
                recommend.setText("alle Geräte laden");
                break;

            default:
                recommend.setText("keine Empfehlung verfügbar");
        }
    }




    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);



        // location
        location = view.findViewById(R.id.location_chooser);

        location.setText("");
        location.clearListSelection();

        location.setThreshold(1); //will start working from first character
        location.setTextColor(Color.BLACK); // must

        // adapt location
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(
                getContext(), R.layout.location_item, names
        );
        location.setAdapter(adapter); //setting the adapter data into the AutoCompleteTextView

        location.setOnItemClickListener(clicker);
        //location.setOnItemSelectedListener(selector);
    }


    private View.OnClickListener services = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getContext());

            String longitude = sharedPreferences.getString("location_longitude", "");
            String latitude = sharedPreferences.getString("location_latitude", "");

            String geo = "open service for: " + longitude + ":" + latitude;
            Toast.makeText(getContext(), geo, Toast.LENGTH_SHORT).show();

            String url = "https://www.windy.com/?";
            url += latitude;
            url += ",";
            url += longitude;
            url += ",";
            url += "10";

            getContext().startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(url)));

        }
    };

    private View.OnClickListener toggler = new View.OnClickListener() {
        @Override
        public void onClick(View v) {

            // load from list
            loadLocations();

            // toogle visibilty

            display.setVisibility(View.GONE);
            geodata.setVisibility(View.GONE);

            location.setVisibility(View.VISIBLE);
        }
    };


    // listener

    private AdapterView.OnItemClickListener clicker = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

            SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getContext());
            SharedPreferences.Editor editor = sharedPreferences.edit();

            // display location
            String loc = location.getText().toString();

            editor.putString("location_input", loc);
            editor.apply();

            display.setText(loc);


            // get longitude, latitude
            String[] geo = findLocation(loc).split(":");

            String longitude = geo[0];
            String latitude = geo[1];


            // display geodata
            editor.putString("location_longitude", longitude);
            editor.apply();

            editor.putString("location_latitude", latitude);
            editor.apply();

            displayLocation(longitude, latitude);

            names.clear();
            allnames.clear();

            // toogle visibilty
            location.setVisibility(View.GONE);

            display.setVisibility(View.VISIBLE);
            geodata.setVisibility(View.VISIBLE);

            String location_saved = getString(R.string.location_saved); // location_saved
            Toast.makeText(getContext(), location_saved, Toast.LENGTH_SHORT).show();
        }
    };

    /*
    private AdapterView.OnItemSelectedListener selector = new AdapterView.OnItemSelectedListener() {
        @Override
        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
            Toast.makeText(getContext(), "select", Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onNothingSelected(AdapterView<?> parent) {
            Toast.makeText(getContext(), "nothing select", Toast.LENGTH_SHORT).show();
        }
    };
     */



    private String findLocation(String loc) {
        String longitude = "";
        String latitude = "";

        for (int geo=0; geo<allnames.size(); geo++) {
            String[] fullgeo = allnames.get(geo).split(";");

            if (loc.equals(fullgeo[0])) {
                String[] coordinates = fullgeo[2].split(",");

                longitude = coordinates[0];
                latitude = coordinates[1];
                String altitude = coordinates[2];

                /*
                android.location.Location location;
                Double dlongitude = Location.convert(longitude);
                Double dlatitude = Location.convert(latitude);
                Double daltitude = Location.convert(altitude);
                 */
            }

        }

        return longitude + ":" + latitude;
    }

    private void displayLocation(String longitude, String latitude) {
        if ("" == longitude) { longitude = "-"; }
        if ("" == latitude) { latitude = "-"; }

        geodata.setText("Breite: " + latitude + "  Länge: " + longitude);
    }



    private void loadLocations() {
        // load stations
        InputStream inputStream = getResources().openRawResource(R.raw.stations2);
        try {

            int size = inputStream.available();
            byte[] station_list = new byte[size];
            inputStream.read(station_list);
            inputStream.close();


            String allstations = new String(station_list);
            String[] stations = allstations.split("\\|");

            for (int i=0; i<stations.length; i++){

                allnames.add(stations[i]);

                String[] values = stations[i].split(";");
                String name = values[0];

                names.add(name);
            }

            Collections.sort(names, null);


        } catch (IOException e) {
            // could not read stations

        }
    }

    //class
}