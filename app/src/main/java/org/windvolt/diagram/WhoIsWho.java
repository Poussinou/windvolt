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

import android.graphics.drawable.Drawable;
import android.media.AudioManager;
import android.media.ToneGenerator;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import org.windvolt.R;
import org.windvolt.diagram.model.DiagramModel;
import org.windvolt.diagram.model.DiagramStore;

public class WhoIsWho extends AppCompatActivity {

    DiagramStore store;

    ImageView diagram_symbol;
    TextView diagram_title;
    TextView diagram_subject;

    Drawable roundbox;

    LinearLayout diagram_space;
    WebView diagram_web;

    String focus_id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.diagram_boxtree);

        roundbox = getResources().getDrawable(R.drawable.gui_roundbox);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            //actionBar.setDisplayHomeAsUpEnabled(true);

            String title = getString(R.string.page1_hello); // values
            actionBar.setTitle(title);
        }




        // diagram elements
        diagram_symbol = (ImageView) findViewById(R.id.diagram_symbol);

        diagram_title = (TextView) findViewById(R.id.diagram_title);
        diagram_subject = (TextView) findViewById(R.id.diagram_subject);

        diagram_space = (LinearLayout) findViewById(R.id.diagram_space);
        diagram_web = (WebView) findViewById(R.id.diagram_web);


        // listeners

        // use onBackPressed()
        //diagram_symbol.setOnClickListener(doLevelUp);

        diagram_title.setOnClickListener(doOpenFocus);


        // create the store
        store = new DiagramStore();
        createStoreData();

        // start diagram
        setFocus(store.getRootId());

    }


    private void setFocus(String id) {
        focus_id = id;
        DiagramModel focus = store.findModel(id);


        doLevelUp.setId(id);
        doOpenFocus.setId(id);


        String html = getString(Integer.parseInt(focus.getAdress())); // values
        //html = "<p>" + html + "</p>";
        diagram_web.loadDataWithBaseURL(null, html, "text/html", "utf-8", null);

        // set focus title and subject
        diagram_title.setText("  " + focus.getTitle());
        diagram_subject.setText("  " + focus.getSubject());


        // remove current children
        diagram_space.removeAllViews();


        // add focus children
        String children = focus.getChildren();
        if (!children.isEmpty()) {
            String[] children_ = children.split(",");

            int size = children_.length;

            for (int c=0; c<size; c++) {
                String c_id = children_[c];
                if (!c_id.isEmpty()) {
                    createChild(focus, c_id);
                }
            }//child
        }//children
    }


    /*
     * public void createChild(DiagramModel parent, String id)
     * creates view to display child
     */
    public void createChild(DiagramModel parent, String id) {
        DiagramModel child = store.findModel(id);

        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.HORIZONTAL);


        layout.setBackground(roundbox);
        layout.setPadding(8, 8, 8, 8);


        ImageView image = new ImageView(this);
        int res = Integer.parseInt(child.getSymbol());
        image.setImageResource(res);

        TextView text = new TextView(this);
        text.setTextAppearance(this, R.style.TextAppearance_MaterialComponents_Headline4);

        text.setText("  " + child.getTitle());

        layout.addView(image);
        layout.addView(text);
        layout.setOnClickListener(new SetFocus(text, id));

        diagram_space.addView(layout);
    }




    private void createStoreData() {
        int symbol = R.drawable.windvolt_small;


        String root = store.addChild("", "windvolt", "Windenergie",
                symbol, R.string.windvolt);



        String netz = store.addChild(root, "Netzbetreiber", "Die deutschen Netzbetreiber",
                symbol, R.string.net_0);



        String n0 = store.addChild(netz, "50Herz", "50Herz",
                symbol, R.string.net_50herz);
        String n1 = store.addChild(netz, "Amprion", "Amprion",
                symbol, R.string.net_ampirion);
        String n2 = store.addChild(netz, "Tennet TSO", "Tennet TSO",
                symbol, R.string.net_tennet);
        String n3 = store.addChild(netz, "Transnet BW", "transnet",
                R.drawable.windvolt_small, R.string.net_transnet);





        String konzern = store.addChild(root, "Stromanbieter", "Die deutschen Stromanbieter",
                R.drawable.windvolt_small, R.string.com_0);



        String k1 = store.addChild(konzern, "konventioneller Strom", "Fossile, Atom",
                R.drawable.windvolt_small, R.string.com_conventional);

        String k10 = store.addChild(k1, "RWE", "Rheinisch-WEstfälische Energiebetriebe",
                R.drawable.windvolt_small, R.string.com_rwe);
        String k11 = store.addChild(k1, "eon", "EON",
                R.drawable.windvolt_small, R.string.com_eon);
        String k12 = store.addChild(k1, "OVAG", "Oberhessische Versorgung Aktiengesellschaft",
                R.drawable.windvolt_small, R.string.com_ovag);



        String k2 = store.addChild(konzern, "Ökoanbieter", "Ökostrom",
                R.drawable.windvolt_small, R.string.com_ecology);

        String k20 = store.addChild(k2, "Lichtblick", "Lichtblick erneuerbare",
                R.drawable.windvolt_small, R.string.com_lichtblick);

    }

        /* mindmap
        RelativeLayout.LayoutParams diagramLayout = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT);
        diagram = new BoxTreeLayout(this);
        setContentView(diagram, diagramLayout);

        private class BoxTreeLayout extends RelativeLayout {
        protected void dispatchDraw(Canvas canvas) {

        canvas.drawLine(0, 0, 20, 20, paint);
        canvas.drawLine(20, 20, 20, 0, paint);
        canvas.drawLine(20, 20, 0, 20, paint);


        // draw connections
        int size = getChildCount();

        super.dispatchDraw(canvas);


       RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(w, h);
        params.width = 320;
        params.height = 100;
        params.leftMargin = (int) (rx + tab * Math.cos(Math.toRadians(angle)));
        params.topMargin = (int) (ry + tab * Math.sin(Math.toRadians(angle)));

        diagram.addView(l, params);
         */


    @Override
    public void onBackPressed() {
        DiagramModel parent = store.findParent(focus_id);
        if (null == parent) {
            super.onBackPressed();
        } else {
            ToneGenerator beep = new ToneGenerator(AudioManager.STREAM_ALARM, 80);
            beep.startTone(ToneGenerator.TONE_CDMA_ANSWER, 200);

            String parent_id = parent.getId();
            setFocus(parent_id);
        }
    }

    private class SetFocus implements View.OnClickListener {
        View view;
        String id = "";
        public SetFocus(View set_view, String set_id) { view = set_view; id = set_id; }

        @Override
        public void onClick(View view) {

            ToneGenerator beep = new ToneGenerator(AudioManager.STREAM_ALARM, 80);
            beep.startTone(ToneGenerator.TONE_CDMA_ANSWER, 200);


            setFocus(id);
        }
    }


    private LevelUpFocus doLevelUp = new LevelUpFocus();
    private class LevelUpFocus implements View.OnClickListener {
        String id = "";
        public void setId(String set_id) { id = set_id; }
        @Override

        public void onClick(View v) {
            DiagramModel parent = store.findParent(id);
            if (null != parent) {
                ToneGenerator beep = new ToneGenerator(AudioManager.STREAM_ALARM, 80);
                beep.startTone(ToneGenerator.TONE_CDMA_ANSWER, 200);

                String parent_id = parent.getId();
                setFocus(parent_id);
            }
        }
    }

    private OpenFocus doOpenFocus = new OpenFocus();
    private class OpenFocus implements View.OnClickListener {
        String id = "";
        public void setId(String set_id) { id = set_id; }

        @Override
        public void onClick(View v) {
        }
    }
}