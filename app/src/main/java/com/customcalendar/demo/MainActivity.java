package com.customcalendar.demo;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.customcalendar.lib.CalendarDay;
import com.customcalendar.lib.CustomCalendarView;

import java.util.HashMap;
import java.util.Map;

/**
 * Demo Activity – shows how to use CustomCalendarView.
 *
 * Layout: activity_main.xml  (see res/layout/)
 */
public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        CustomCalendarView calendar = findViewById(R.id.calendarView);

        // ── 1. Navigate to August 2026 (month is 0-based: 7 = August) ──────────
        calendar.goTo(2026, 7);

        // ── 2. Set labels on specific dates ─────────────────────────────────────

        // Single call per date
        calendar.setDayData("01-08-2026",
                CalendarDay.of("01-08-2026")
                        .label("7 Visits")
                        .labelColorHex("#9C27B0"));

        calendar.setDayData("02-08-2026",
                CalendarDay.of("02-08-2026")
                        .label("Claim Now")
                        .labelColorHex("#E91E8C"));

        calendar.setDayData("09-08-2026",
                CalendarDay.of("09-08-2026")
                        .label("Claim Now")
                        .labelColorHex("#E91E8C"));

        calendar.setDayData("15-08-2026",
                CalendarDay.of("15-08-2026")
                        .label("Independence\nDay")
                        .labelColorHex("#F44336")
                        .dateTextColorHex("#F44336"));

        calendar.setDayData("18-08-2026",
                CalendarDay.of("18-08-2026")
                        .label("Claim Now")
                        .labelColorHex("#E91E8C"));

        calendar.setDayData("21-08-2026",
                CalendarDay.of("21-08-2026")
                        .label("Claimed")
                        .labelColorHex("#4CAF50")
                        .bgColorHex("#E8F5E9"));

        calendar.setDayData("22-08-2026",
                CalendarDay.of("22-08-2026")
                        .label("Claimed")
                        .labelColorHex("#4CAF50")
                        .bgColorHex("#E8F5E9"));

        calendar.setDayData("24-08-2026",
                CalendarDay.of("24-08-2026")
                        .label("Claim Now")
                        .labelColorHex("#E91E8C")
                        .bgColorHex("#FCE4EC"));

        calendar.setDayData("25-08-2026",
                CalendarDay.of("25-08-2026")
                        .label("4 Visits")
                        .labelColorHex("#9C27B0")
                        .bgColorHex("#F3E5F5"));

        calendar.setDayData("25-08-2026",
                CalendarDay.of("25-08-2026")
                        .label("Claimed")
                        .labelColorHex("#4CAF50")
                        .bgColorHex("#E8F5E9"));

        calendar.setDayData("28-08-2026",
                CalendarDay.of("28-08-2026")
                        .label("1 Visit")
                        .labelColorHex("#9C27B0"));

        calendar.setDayData("29-08-2026",
                CalendarDay.of("29-08-2026")
                        .label("Claim Now")
                        .labelColorHex("#E91E8C"));

        // ── 3. (Optional) Bulk-set via Map ────────────────────────────────────
        // Map<String, CalendarDay> bulk = new HashMap<>();
        // bulk.put("05-08-2026", CalendarDay.of("05-08-2026").label("Holiday").labelColorHex("#FF9800"));
        // calendar.setDayDataMap(bulk);

        // ── 4. Customise accent colours at runtime ────────────────────────────
        // Use the light purple/violet from the reference image
        calendar.setSelectedDayBgColor(Color.parseColor("#7C4DFF"));
        calendar.setTodayBgColor(Color.parseColor("#EDE7F6"));

        // ── 5. Handle date taps → open DetailActivity ─────────────────────────
        calendar.setOnDayClickListener((dateKey, day, dayOfMonth, month, year) -> {
            // dateKey is always in "DD-MM-YYYY" format
            Intent intent = new Intent(this, DetailActivity.class);
            intent.putExtra(DetailActivity.EXTRA_DATE_KEY, dateKey);
            intent.putExtra(DetailActivity.EXTRA_DAY,   dayOfMonth);
            intent.putExtra(DetailActivity.EXTRA_MONTH, month);        // 0-based
            intent.putExtra(DetailActivity.EXTRA_YEAR,  year);
            if (day != null) {
                intent.putExtra(DetailActivity.EXTRA_LABEL, day.getLabelText());
            }
            startActivity(intent);
        });
    }
}
