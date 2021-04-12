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

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.content.res.AppCompatResources;

import com.google.android.material.snackbar.Snackbar;

import org.windvolt.R;

public class BusinessModel extends AppCompatActivity {

    Drawable icon;
    StructogramLayout diagram;

    int w = 480;
    int h = 720;

    final int tab = 40;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            //actionBar.setDisplayHomeAsUpEnabled(true);

            String title = getString(R.string.page2_hello); // values
            actionBar.setTitle(title);
        }

        icon = AppCompatResources.getDrawable(this, R.drawable.gui_roundbox);


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            Display display = getWindowManager().getDefaultDisplay();
            DisplayMetrics displayMetrics = new DisplayMetrics();

            display.getRealMetrics(displayMetrics);

            w = displayMetrics.widthPixels;
            h = displayMetrics.heightPixels;
        }


        diagram = new StructogramLayout(this);
        RelativeLayout.LayoutParams diagramLayout = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT);

        setContentView(diagram, diagramLayout);



        {
            addFrame(1, "Producer", "producer");
            addFrame(2, "Distributor", "distributor");
            addFrame(3, "Trader", "trader");
            addFrame(4, "Seller", "seller");
            addFrame(5, "Consumer", "consumer");
        }


        //diagram.invalidate();


    }


    private class StructogramLayout extends RelativeLayout {
        Paint paint;

        public StructogramLayout(Context context) {
            super(context);
            paint = new Paint();
        }


        /*
        draw arrows
         */
        protected void dispatchDraw(Canvas canvas) {

            /* diagram symbol */
            paint.setColor(Color.RED);
            paint.setStrokeWidth(8);

            canvas.drawLine(0, 0, 20, 20, paint);
            canvas.drawLine(20, 20, 20, 0, paint);
            canvas.drawLine(20, 20, 0, 20, paint);



            /* draw connections */
            paint.setColor(Color.BLACK);
            paint.setStrokeWidth(4);

            int size = this.getChildCount();
            for (int v = 0; v < size-1; v++) {
                View v0 = getChildAt(v);
                View v1 = getChildAt(v+1);

                int w = v0.getRight() - v0.getLeft();
                int x0 = v0.getLeft() + w/2;
                int x1 = x0;

                int y0 = v0.getBottom();
                int y1 = v1.getTop();


                canvas.drawLine(x0, y0, x1, y1, paint);
                canvas.drawLine(x1 - 8, y1 - 8, x1, y1, paint);
                canvas.drawLine(x1 + 8, y1 - 8, x1, y1, paint);

            } // for





            /* draw loop connection */

            paint.setColor(Color.BLUE);

            if (size > 2) {

                // draw stub with tip
                View v0 = getChildAt(0);
                int x0 = v0.getRight();
                int h0 = v0.getBottom()-v0.getTop();
                int y0 = v0.getTop() + h0/2;

                canvas.drawLine(x0, y0, x0 + tab, y0, paint);
                canvas.drawLine(x0, y0, x0 + 8, y0 - 8, paint);
                canvas.drawLine(x0, y0, x0 + 8, y0 + 8, paint);


                // draw stub
                View v1 = getChildAt(3);
                int x1 = v1.getRight();
                int h1 = v1.getBottom()-v1.getTop();
                int y1 = v1.getTop() + h1/2;

                canvas.drawLine(x1, y1, x1 + tab, y1, paint);


                // connect stubs
                canvas.drawLine(x0 + tab, y0, x1 + tab, y1, paint);
            }



            super.dispatchDraw(canvas);
        }
    }

    /* --------------------------------windvolt-------------------------------- */

    private void addFrame(int position, String name, String url) {
        TextView tv = new TextView(this);

        tv.setText("\n " + name);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            tv.setTextAppearance(R.style.TextAppearance_AppCompat_Body2);
        }
        tv.setContentDescription(url);
        tv.setBackground(icon);
        tv.setGravity(1);

        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(w, h);
        params.leftMargin = (w-240)/2;
        params.topMargin = 40 + 120 * position;
        params.width = 240;
        params.height = 80;

        diagram.addView(tv, params);

        tv.setOnClickListener(new OnClick(url));
    }

    class OnClick implements View.OnClickListener {

        String action;

        public OnClick(String set_action) { action = set_action; }
        @Override
        public void onClick(View view) {
            Snackbar.make(view, action, Snackbar.LENGTH_SHORT).show();
        }
    }

}