package com.customcalendar.lib;

import android.graphics.Color;

/**
 * Represents a single day with optional label text, background color, and text color.
 * Use dateKey format "DD-MM-YYYY" e.g. "21-08-2026"
 */
public class CalendarDay {

    private final String dateKey; // "DD-MM-YYYY"
    private String labelText;     // Text shown below the date number
    private int labelColor;       // Color of the label text
    private int bgColor;          // Background circle color (-1 = none)
    private int dateTextColor;    // Color of the date number (-1 = default)
    private boolean isClickable;

    public CalendarDay(String dateKey) {
        this.dateKey = dateKey;
        this.labelText = null;
        this.labelColor = Color.parseColor("#E91E8C");
        this.bgColor = -1;
        this.dateTextColor = -1;
        this.isClickable = true;
    }

    public static CalendarDay of(String dateKey) {
        return new CalendarDay(dateKey);
    }

    public CalendarDay label(String text) {
        this.labelText = text;
        return this;
    }

    public CalendarDay labelColor(int color) {
        this.labelColor = color;
        return this;
    }

    public CalendarDay labelColorHex(String hex) {
        this.labelColor = Color.parseColor(hex);
        return this;
    }

    public CalendarDay bgColor(int color) {
        this.bgColor = color;
        return this;
    }

    public CalendarDay bgColorHex(String hex) {
        this.bgColor = Color.parseColor(hex);
        return this;
    }

    public CalendarDay dateTextColor(int color) {
        this.dateTextColor = color;
        return this;
    }

    public CalendarDay dateTextColorHex(String hex) {
        this.dateTextColor = Color.parseColor(hex);
        return this;
    }

    public CalendarDay clickable(boolean clickable) {
        this.isClickable = clickable;
        return this;
    }

    // ── Getters ──────────────────────────────────────────────────────────────

    public String getDateKey()      { return dateKey; }
    public String getLabelText()    { return labelText; }
    public int    getLabelColor()   { return labelColor; }
    public int    getBgColor()      { return bgColor; }
    public int    getDateTextColor(){ return dateTextColor; }
    public boolean isClickable()    { return isClickable; }
    public boolean hasLabel()       { return labelText != null && !labelText.isEmpty(); }
    public boolean hasBgColor()     { return bgColor != -1; }
    public boolean hasDateTextColor(){ return dateTextColor != -1; }
}
