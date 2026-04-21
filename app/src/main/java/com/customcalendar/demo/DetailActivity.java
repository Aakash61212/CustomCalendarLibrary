package com.customcalendar.demo;

import android.graphics.Color;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.text.DateFormatSymbols;
import java.util.Locale;

/**
 * Displays details for a tapped calendar date.
 * Receives Extras:
 *   EXTRA_DATE_KEY  "DD-MM-YYYY"
 *   EXTRA_DAY       int  day of month
 *   EXTRA_MONTH     int  0-based month
 *   EXTRA_YEAR      int  full year
 *   EXTRA_LABEL     String (optional) label text on that date
 */
public class DetailActivity extends AppCompatActivity {

    public static final String EXTRA_DATE_KEY = "dateKey";
    public static final String EXTRA_DAY      = "day";
    public static final String EXTRA_MONTH    = "month";
    public static final String EXTRA_YEAR     = "year";
    public static final String EXTRA_LABEL    = "label";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // ── Build a simple UI programmatically ────────────────────────────────
        LinearLayout root = new LinearLayout(this);
        root.setOrientation(LinearLayout.VERTICAL);
        root.setBackgroundColor(Color.parseColor("#F5F3FF"));
        root.setGravity(Gravity.CENTER);
        int dp = (int) getResources().getDisplayMetrics().density;

        // Back button
        TextView btnBack = new TextView(this);
        btnBack.setText("← Back");
        btnBack.setTextSize(14);
        btnBack.setTextColor(Color.parseColor("#7C4DFF"));
        btnBack.setPadding(24*dp, 16*dp, 24*dp, 0);
        btnBack.setOnClickListener(v -> finish());
        LinearLayout.LayoutParams backLp =
                new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT);
        btnBack.setLayoutParams(backLp);
        root.addView(btnBack);

        // Spacer
        View spacer = new View(this);
        spacer.setLayoutParams(new LinearLayout.LayoutParams(1, 0, 1f));
        root.addView(spacer);

        // Card
        androidx.cardview.widget.CardView card = new androidx.cardview.widget.CardView(this);
        card.setCardBackgroundColor(Color.WHITE);
        card.setRadius(24 * dp);
        card.setCardElevation(12 * dp);
        card.setUseCompatPadding(true);
        LinearLayout.LayoutParams cardLp =
                new LinearLayout.LayoutParams((int)(320*dp), LinearLayout.LayoutParams.WRAP_CONTENT);
        cardLp.setMargins(24*dp, 0, 24*dp, 0);
        card.setLayoutParams(cardLp);

        LinearLayout cardInner = new LinearLayout(this);
        cardInner.setOrientation(LinearLayout.VERTICAL);
        cardInner.setGravity(Gravity.CENTER);
        cardInner.setPadding(32*dp, 40*dp, 32*dp, 40*dp);

        // Date circle
        LinearLayout circle = new LinearLayout(this);
        circle.setGravity(Gravity.CENTER);
        int sz = 80 * dp;
        circle.setLayoutParams(new LinearLayout.LayoutParams(sz, sz));
        android.graphics.drawable.GradientDrawable gd = new android.graphics.drawable.GradientDrawable();
        gd.setShape(android.graphics.drawable.GradientDrawable.OVAL);
        gd.setColor(Color.parseColor("#7C4DFF"));
        circle.setBackground(gd);

        // Read extras
        String dateKey  = getIntent().getStringExtra(EXTRA_DATE_KEY);
        int    day      = getIntent().getIntExtra(EXTRA_DAY,   1);
        int    month    = getIntent().getIntExtra(EXTRA_MONTH, 0);
        int    year     = getIntent().getIntExtra(EXTRA_YEAR,  2026);
        String label    = getIntent().getStringExtra(EXTRA_LABEL);

        TextView tvDay = new TextView(this);
        tvDay.setText(String.format(Locale.US, "%02d", day));
        tvDay.setTextSize(28);
        tvDay.setTypeface(null, android.graphics.Typeface.BOLD);
        tvDay.setTextColor(Color.WHITE);
        tvDay.setGravity(Gravity.CENTER);
        circle.addView(tvDay);
        cardInner.addView(circle);

        spacerH(cardInner, 20*dp);

        String monthName = new DateFormatSymbols().getMonths()[month];
        addDetailText(cardInner, monthName + " " + year, 22, true,
                Color.parseColor("#1A1A2E"), 0);

        if (dateKey != null) {
            addDetailText(cardInner, dateKey, 13, false,
                    Color.parseColor("#9E9E9E"), 4*dp);
        }

        if (label != null && !label.isEmpty()) {
            spacerH(cardInner, 16*dp);

            // Label chip
            TextView chip = new TextView(this);
            chip.setText(label);
            chip.setTextSize(13);
            chip.setTextColor(Color.parseColor("#4CAF50"));
            chip.setPadding(16*dp, 8*dp, 16*dp, 8*dp);
            chip.setGravity(Gravity.CENTER);
            android.graphics.drawable.GradientDrawable chipBg =
                    new android.graphics.drawable.GradientDrawable();
            chipBg.setShape(android.graphics.drawable.GradientDrawable.RECTANGLE);
            chipBg.setCornerRadius(20*dp);
            chipBg.setColor(Color.parseColor("#E8F5E9"));
            chip.setBackground(chipBg);
            cardInner.addView(chip);
        }

        spacerH(cardInner, 24*dp);

        // Info line
        addDetailText(cardInner,
                "Tap back to return to the calendar.",
                12, false, Color.parseColor("#BDBDBD"), 0);

        card.addView(cardInner);
        root.addView(card);

        // Bottom spacer
        View spacer2 = new View(this);
        spacer2.setLayoutParams(new LinearLayout.LayoutParams(1, 0, 1f));
        root.addView(spacer2);

        setContentView(root);
        if (getSupportActionBar() != null) getSupportActionBar().hide();
    }

    private void addDetailText(LinearLayout parent, String text, int sp,
                                boolean bold, int color, int topMarginPx) {
        TextView tv = new TextView(this);
        tv.setText(text);
        tv.setTextSize(sp);
        tv.setTypeface(null, bold ? android.graphics.Typeface.BOLD : android.graphics.Typeface.NORMAL);
        tv.setTextColor(color);
        tv.setGravity(Gravity.CENTER);
        LinearLayout.LayoutParams lp =
                new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT);
        lp.topMargin = topMarginPx;
        tv.setLayoutParams(lp);
        parent.addView(tv);
    }

    private void spacerH(LinearLayout parent, int heightPx) {
        View v = new View(parent.getContext());
        v.setLayoutParams(new LinearLayout.LayoutParams(1, heightPx));
        parent.addView(v);
    }
}
