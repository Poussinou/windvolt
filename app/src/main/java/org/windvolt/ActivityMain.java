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
package org.windvolt;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import org.windvolt.story.StoryOfWindvolt;
import org.windvolt.system.About;
import org.windvolt.system.DeviceManagement;
import org.windvolt.system.Settings;

public class ActivityMain extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.app_coordinator);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        // edit settings
        if (id == R.id.action_settings) {

            startActivity(new Intent(this, Settings.class));
            return true;
        }

        // edit devices
        if (id == R.id.action_devices) {

            startActivity(new Intent(this, DeviceManagement.class));
            return true;
        }

        // show story
        if (id == R.id.action_story) {

            startActivity(new Intent(this, StoryOfWindvolt.class));
            return true;
        }

        // show about
        if (id == R.id.action_about) {

            startActivity(new Intent(this, About.class));
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

}