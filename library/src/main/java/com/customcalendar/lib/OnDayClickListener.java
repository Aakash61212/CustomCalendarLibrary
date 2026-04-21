package com.customcalendar.lib;

/**
 * Callback fired when the user taps a date cell.
 */
public interface OnDayClickListener {
    /**
     * @param dateKey  "DD-MM-YYYY"  e.g. "21-08-2026"
     * @param day      The CalendarDay data attached to this date, or null if none was set.
     * @param dayOfMonth  Raw day number (1-31)
     * @param month    0-based month (0=Jan … 11=Dec)
     * @param year     Full year e.g. 2026
     */
    void onDayClicked(String dateKey, CalendarDay day,
                      int dayOfMonth, int month, int year);
}
