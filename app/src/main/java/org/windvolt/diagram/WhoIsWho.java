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
package org.windvolt.diagram;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.content.res.AppCompatResources;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.android.material.snackbar.Snackbar;

import org.windvolt.R;

public class WhoIsWho extends AppCompatActivity {

    Drawable icon;
    OrganigramLayout diagram;

    int w = 480;
    int h = 720;

    final int tab = 240;

    TextView root;
    int rx, ry;

    private class OrganigramLayout extends RelativeLayout {
        Paint paint;

        public OrganigramLayout(Context context) {
            super(context);
            paint = new Paint();
            paint.setStrokeWidth(4);

        }


        /*
        draw connections
         */
        protected void dispatchDraw(Canvas canvas) {


            // test
            paint.setColor(Color.BLUE);

            //canvas.drawLine(0, 0, w, h, paint);





            int size = getChildCount();
            for (int v = 1; v < size; v++) {
                View tv = getChildAt(v);

                int x1 = tv.getLeft();
                int y1 = tv.getTop() + tv.getHeight()/2;

                canvas.drawLine(rx, ry, x1, y1, paint);
            }



            super.dispatchDraw(canvas);
        }
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            //actionBar.setDisplayHomeAsUpEnabled(true);

            actionBar.setTitle(getString(R.string.devices_action));
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            Display display = getWindowManager().getDefaultDisplay();
            DisplayMetrics displayMetrics = new DisplayMetrics();

            display.getRealMetrics(displayMetrics);

            w = displayMetrics.widthPixels;
            h = displayMetrics.heightPixels;
        }


        icon = AppCompatResources.getDrawable(this, R.drawable.gui_roundbox);





        diagram = new OrganigramLayout(this);
        RelativeLayout.LayoutParams diagramLayout = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT);

        setContentView(diagram, diagramLayout);


        root = addRoot("POWER", 20, 40);

        //TextView tv0 = addChild(" Ampirion ", "apirina.de",15);
        addChild(w + "/" + h, "apirina.de",15);

        addChild(" Tennet TSO ", "tennet.de",45);
        addChild(" TransnetBW ", "transnet.de",-15);

        addChild(" 50Hertz ", "50hertz.de", -45);


    }













    private TextView addRoot(String name, int pw, int ph) {
        TextView tv = new TextView(this);
        tv.setText(name);

        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(w, h);
        params.width = 100;
        params.height = 40;

        rx = pw * w / 100;
        ry = ph * h / 100;

        params.leftMargin = rx - params.width;
        params.topMargin = ry - params.height/2;


        diagram.addView(tv, params);

        return tv;
    }

    private TextView addChild(String name, String action, int angle) {

        TextView tv = new TextView(this);
        tv.setText(name);
        tv.setContentDescription(action);

        tv.setBackground(icon);
        //tv.setBackgroundColor(Color.LTGRAY);

        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(w, h);
        params.width = 160;
        params.height = 40;


        params.leftMargin = (int) (rx + tab * Math.cos(Math.toRadians(angle)));
        params.topMargin = (int) (ry + tab * Math.sin(Math.toRadians(angle)));

        diagram.addView(tv, params);

        tv.setOnClickListener(new OnClick(action));

        return tv;
    }


    class OnClick implements View.OnClickListener {

        String action;

        public OnClick(String set_action) { action = set_action; }
        @Override
        public void onClick(View v) {

            Snackbar.make(diagram, action, Snackbar.LENGTH_SHORT).show();
        }
    }

}