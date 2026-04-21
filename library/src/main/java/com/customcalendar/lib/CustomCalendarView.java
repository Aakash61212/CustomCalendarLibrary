package com.customcalendar.lib;

import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.RippleDrawable;
import android.graphics.drawable.StateListDrawable;
import android.os.Build;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.widget.GridLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.cardview.widget.CardView;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

/**
 * CustomCalendarView – modern monthly calendar for Android (Java).
 *
 * <h3>Quick-start</h3>
 * <pre>
 * CustomCalendarView cal = findViewById(R.id.calendarView);
 *
 * // Jump to a month (month is 0-based: 0=Jan … 11=Dec)
 * cal.goTo(2026, 7);   // August 2026
 *
 * // Add a label + coloured circle on a specific date  ("DD-MM-YYYY")
 * cal.setDayData("21-08-2026",
 *     CalendarDay.of("21-08-2026")
 *         .label("Claimed")
 *         .labelColorHex("#4CAF50")
 *         .bgColorHex("#E8F5E9"));
 *
 * // Open another screen when a date is tapped
 * cal.setOnDayClickListener((dateKey, day, d, m, y) -> {
 *     Intent i = new Intent(this, DetailActivity.class);
 *     i.putExtra("dateKey", dateKey);
 *     startActivity(i);
 * });
 * </pre>
 */
public class CustomCalendarView extends LinearLayout {

    // ── Colour defaults ───────────────────────────────────────────────────────
    private int colorHeaderText     = Color.parseColor("#1A1A2E");
    private int colorArrow          = Color.parseColor("#7C4DFF");
    private int colorWeekdayText    = Color.parseColor("#9E9E9E");
    private int colorSundayText     = Color.parseColor("#F44336");
    private int colorDayText        = Color.parseColor("#212121");
    private int colorOtherMonthText = Color.parseColor("#BDBDBD");
    private int colorTodayBg        = Color.parseColor("#EDE7F6");
    private int colorTodayText      = Color.parseColor("#7C4DFF");
    private int colorSelectedBg     = Color.parseColor("#7C4DFF");
    private int colorSelectedText   = Color.WHITE;
    private int colorCardBg         = Color.WHITE;
    private float cardCornerRadius;   // in pixels (set in constructor after dp is known)
    private float cardElevation;

    // ── State ─────────────────────────────────────────────────────────────────
    private final Calendar displayedMonth = Calendar.getInstance();
    private final Calendar today          = Calendar.getInstance();
    private String selectedDateKey = null;
    private final Map<String, CalendarDay> dayMap = new HashMap<>();
    private OnDayClickListener clickListener;

    // ── View refs ─────────────────────────────────────────────────────────────
    private TextView   tvMonthTitle;
    private GridLayout gridDays;
    private final float dp;

    // ─────────────────────────────────────────────────────────────────────────
    public CustomCalendarView(Context ctx)                              { this(ctx,null); }
    public CustomCalendarView(Context ctx, AttributeSet a)              { this(ctx,a,0); }
    public CustomCalendarView(Context ctx, AttributeSet a, int def)     {
        super(ctx, a, def);
        dp = ctx.getResources().getDisplayMetrics().density;
        cardCornerRadius = 24 * dp;
        cardElevation    =  8 * dp;
        applyAttrs(ctx, a);
        buildLayout();
    }

    // ─────────────────────────────────────────────────────────────────────────
    private void applyAttrs(Context ctx, AttributeSet attrs) {
        if (attrs == null) return;
        TypedArray a = ctx.obtainStyledAttributes(attrs, R.styleable.CustomCalendarView);
        colorCardBg       = a.getColor(R.styleable.CustomCalendarView_ccv_cardBgColor,          colorCardBg);
        colorHeaderText   = a.getColor(R.styleable.CustomCalendarView_ccv_headerTextColor,       colorHeaderText);
        colorArrow        = a.getColor(R.styleable.CustomCalendarView_ccv_arrowColor,            colorArrow);
        colorWeekdayText  = a.getColor(R.styleable.CustomCalendarView_ccv_weekdayTextColor,      colorWeekdayText);
        colorDayText      = a.getColor(R.styleable.CustomCalendarView_ccv_dayTextColor,          colorDayText);
        colorTodayBg      = a.getColor(R.styleable.CustomCalendarView_ccv_todayBgColor,          colorTodayBg);
        colorTodayText    = a.getColor(R.styleable.CustomCalendarView_ccv_todayTextColor,        colorTodayText);
        colorSelectedBg   = a.getColor(R.styleable.CustomCalendarView_ccv_selectedDayBgColor,    colorSelectedBg);
        colorSelectedText = a.getColor(R.styleable.CustomCalendarView_ccv_selectedDayTextColor,  colorSelectedText);
        cardCornerRadius  = a.getDimension(R.styleable.CustomCalendarView_ccv_cardCornerRadius,  cardCornerRadius);
        cardElevation     = a.getDimension(R.styleable.CustomCalendarView_ccv_cardElevation,     cardElevation);
        a.recycle();
    }

    // ─────────────────────────────────────────────────────────────────────────
    private void buildLayout() {
        removeAllViews();
        setOrientation(VERTICAL);
        setBackgroundColor(Color.TRANSPARENT);

        CardView card = new CardView(getContext());
        card.setCardBackgroundColor(colorCardBg);
        card.setRadius(cardCornerRadius);
        card.setCardElevation(cardElevation);
        card.setUseCompatPadding(true);

        LinearLayout inner = new LinearLayout(getContext());
        inner.setOrientation(VERTICAL);
        int p = px(16);
        inner.setPadding(p, p, p, p);

        inner.addView(buildHeader());
        inner.addView(buildWeekdayRow());

        gridDays = new GridLayout(getContext());
        gridDays.setColumnCount(7);
        inner.addView(gridDays);

        card.addView(inner);
        addView(card);

        renderMonth();
    }

    // ── Header ────────────────────────────────────────────────────────────────
    private View buildHeader() {
        LinearLayout row = new LinearLayout(getContext());
        row.setOrientation(HORIZONTAL);
        row.setGravity(Gravity.CENTER_VERTICAL);
        row.setPadding(0, 0, 0, px(8));

        tvMonthTitle = new TextView(getContext());
        tvMonthTitle.setTextSize(18);
        tvMonthTitle.setTypeface(null, Typeface.BOLD);
        tvMonthTitle.setTextColor(colorHeaderText);
        tvMonthTitle.setGravity(Gravity.CENTER);
        LinearLayout.LayoutParams lp =
                new LinearLayout.LayoutParams(0, LayoutParams.WRAP_CONTENT, 1f);
        tvMonthTitle.setLayoutParams(lp);

        row.addView(arrowBtn("‹", v -> navigateMonth(-1)));
        row.addView(tvMonthTitle);
        row.addView(arrowBtn("›", v -> navigateMonth(+1)));
        return row;
    }

    private TextView arrowBtn(String symbol, OnClickListener l) {
        TextView tv = new TextView(getContext());
        tv.setText(symbol);
        tv.setTextSize(24);
        tv.setTextColor(colorArrow);
        tv.setTypeface(null, Typeface.BOLD);
        tv.setGravity(Gravity.CENTER);
        tv.setPadding(px(14), px(2), px(14), px(2));
        tv.setClickable(true);
        tv.setFocusable(true);
        tv.setBackground(makeRipple(Color.TRANSPARENT));
        tv.setOnClickListener(l);
        return tv;
    }

    // ── Weekday labels ────────────────────────────────────────────────────────
    private View buildWeekdayRow() {
        GridLayout g = new GridLayout(getContext());
        g.setColumnCount(7);
        String[] labels = {"Sun","Mon","Tue","Wed","Thu","Fri","Sat"};
        for (int i = 0; i < 7; i++) {
            TextView tv = new TextView(getContext());
            tv.setText(labels[i]);
            tv.setTextSize(11);
            tv.setTypeface(null, Typeface.BOLD);
            tv.setTextColor(i == 0 ? colorSundayText : colorWeekdayText);
            tv.setGravity(Gravity.CENTER);
            GridLayout.LayoutParams glp = new GridLayout.LayoutParams();
            glp.columnSpec = GridLayout.spec(i, 1f);
            glp.width  = 0;
            glp.height = px(36);
            tv.setLayoutParams(glp);
            g.addView(tv);
        }
        return g;
    }

    // ── Render month grid ─────────────────────────────────────────────────────
    private void renderMonth() {
        tvMonthTitle.setText(
                new SimpleDateFormat("MMMM", Locale.getDefault())
                        .format(displayedMonth.getTime()));

        gridDays.removeAllViews();

        int year  = displayedMonth.get(Calendar.YEAR);
        int month = displayedMonth.get(Calendar.MONTH); // 0-based

        Calendar first = (Calendar) displayedMonth.clone();
        first.set(Calendar.DAY_OF_MONTH, 1);
        int startDow    = first.get(Calendar.DAY_OF_WEEK) - 1; // 0=Sun
        int daysInMonth = first.getActualMaximum(Calendar.DAY_OF_MONTH);

        Calendar prev = (Calendar) displayedMonth.clone();
        prev.add(Calendar.MONTH, -1);
        int daysInPrev  = prev.getActualMaximum(Calendar.DAY_OF_MONTH);

        int totalCells  = (int)(Math.ceil((startDow + daysInMonth) / 7.0)) * 7;
        int nextCounter = 1;

        for (int cell = 0; cell < totalCells; cell++) {
            int col = cell % 7;
            int dayNum;
            boolean isCurrent;

            if (cell < startDow) {
                dayNum    = daysInPrev - startDow + cell + 1;
                isCurrent = false;
            } else if (cell < startDow + daysInMonth) {
                dayNum    = cell - startDow + 1;
                isCurrent = true;
            } else {
                dayNum    = nextCounter++;
                isCurrent = false;
            }

            String dateKey = isCurrent
                    ? String.format(Locale.US, "%02d-%02d-%04d", dayNum, month + 1, year)
                    : null;

            boolean isToday = isCurrent
                    && year   == today.get(Calendar.YEAR)
                    && month  == today.get(Calendar.MONTH)
                    && dayNum == today.get(Calendar.DAY_OF_MONTH);

            boolean isSelected = dateKey != null && dateKey.equals(selectedDateKey);
            CalendarDay data   = dateKey != null ? dayMap.get(dateKey) : null;

            gridDays.addView(buildDayCell(dayNum, col, isCurrent, isToday, isSelected, data, dateKey));
        }
    }

    // ── Single day cell ───────────────────────────────────────────────────────
    private View buildDayCell(int dayNum, int col, boolean isCurrent,
                               boolean isToday, boolean isSelected,
                               CalendarDay data, String dateKey) {

        LinearLayout wrapper = new LinearLayout(getContext());
        wrapper.setOrientation(VERTICAL);
        wrapper.setGravity(Gravity.TOP | Gravity.CENTER_HORIZONTAL);

        GridLayout.LayoutParams glp = new GridLayout.LayoutParams();
        glp.columnSpec = GridLayout.spec(col, 1f);
        glp.width  = 0;
        glp.height = GridLayout.LayoutParams.WRAP_CONTENT;
        glp.setMargins(0, px(3), 0, px(3));
        wrapper.setLayoutParams(glp);
        wrapper.setPadding(px(2), px(5), px(2), px(5));

        // Circle
        int sz = px(36);
        LinearLayout circle = new LinearLayout(getContext());
        circle.setGravity(Gravity.CENTER);
        circle.setLayoutParams(new LinearLayout.LayoutParams(sz, sz));

        int circleBg = Color.TRANSPARENT;
        if      (isSelected)                                  circleBg = (data != null && data.hasBgColor()) ? data.getBgColor() : colorSelectedBg;
        else if (isToday)                                     circleBg = colorTodayBg;
        else if (data != null && data.hasBgColor())           circleBg = data.getBgColor();
        circle.setBackground(ovalDrawable(circleBg));

        // Date number
        TextView tvDay = new TextView(getContext());
        tvDay.setText(String.format(Locale.US, "%02d", dayNum));
        tvDay.setTextSize(13);
        tvDay.setGravity(Gravity.CENTER);

        int textColor;
        if      (isSelected)                                  textColor = (data != null && data.hasDateTextColor()) ? data.getDateTextColor() : colorSelectedText;
        else if (isToday)                                     textColor = colorTodayText;
        else if (data != null && data.hasDateTextColor())     textColor = data.getDateTextColor();
        else if (!isCurrent)                                  textColor = colorOtherMonthText;
        else if (col == 0)                                    textColor = colorSundayText;
        else                                                  textColor = colorDayText;

        tvDay.setTextColor(textColor);
        tvDay.setTypeface(null, (isToday || isSelected) ? Typeface.BOLD : Typeface.NORMAL);
        circle.addView(tvDay);
        wrapper.addView(circle);

        // Label beneath date
        if (isCurrent && data != null && data.hasLabel()) {
            TextView tvLabel = new TextView(getContext());
            tvLabel.setText(data.getLabelText());
            tvLabel.setTextSize(8);
            tvLabel.setTextColor(data.getLabelColor());
            tvLabel.setGravity(Gravity.CENTER);
            tvLabel.setSingleLine(true);
            tvLabel.setEllipsize(TextUtils.TruncateAt.END);
            LinearLayout.LayoutParams llp =
                    new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
            llp.topMargin = px(2);
            tvLabel.setLayoutParams(llp);
            wrapper.addView(tvLabel);
        }

        // Click
        if (isCurrent && dateKey != null && (data == null || data.isClickable())) {
            final String dk = dateKey;
            final CalendarDay dd = data;
            final int dn = dayNum;
            final int m  = Integer.parseInt(dk.substring(3,5)) - 1;
            final int y  = Integer.parseInt(dk.substring(6));
            wrapper.setClickable(true);
            wrapper.setFocusable(true);
            wrapper.setBackground(makeRipple(Color.TRANSPARENT));
            wrapper.setOnClickListener(v -> {
                selectedDateKey = dk;
                renderMonth();
                if (clickListener != null) clickListener.onDayClicked(dk, dd, dn, m, y);
            });
        }

        return wrapper;
    }

    // ── Navigation ────────────────────────────────────────────────────────────
    private void navigateMonth(int delta) {
        displayedMonth.add(Calendar.MONTH, delta);
        renderMonth();
    }

    // ── Drawable helpers ──────────────────────────────────────────────────────
    private GradientDrawable ovalDrawable(int color) {
        GradientDrawable gd = new GradientDrawable();
        gd.setShape(GradientDrawable.OVAL);
        gd.setColor(color);
        return gd;
    }

    private android.graphics.drawable.Drawable makeRipple(int base) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            return new RippleDrawable(
                    ColorStateList.valueOf(Color.parseColor("#22000000")),
                    new ColorDrawable(base), null);
        }
        StateListDrawable sl = new StateListDrawable();
        sl.addState(new int[]{ android.R.attr.state_pressed },
                new ColorDrawable(Color.parseColor("#22000000")));
        sl.addState(new int[]{}, new ColorDrawable(base));
        return sl;
    }

    private int px(float dpVal) { return Math.round(dpVal * dp); }

    // ─────────────────────────────────────────────────────────────────────────
    //  PUBLIC API
    // ─────────────────────────────────────────────────────────────────────────

    /** Set data for one date.  dateKey = "DD-MM-YYYY" e.g. "21-08-2026" */
    public CustomCalendarView setDayData(String dateKey, CalendarDay day) {
        dayMap.put(dateKey, day);
        renderMonth();
        return this;
    }

    /** Set data for many dates at once. */
    public CustomCalendarView setDayDataMap(Map<String, CalendarDay> map) {
        dayMap.putAll(map);
        renderMonth();
        return this;
    }

    /** Remove data for one date. */
    public CustomCalendarView clearDayData(String dateKey) {
        dayMap.remove(dateKey);
        renderMonth();
        return this;
    }

    /** Clear all date data. */
    public CustomCalendarView clearAllDayData() {
        dayMap.clear();
        renderMonth();
        return this;
    }

    /**
     * Navigate to a specific month/year.
     * @param year  full year  e.g. 2026
     * @param month 0-based month (0=Jan … 11=Dec)
     */
    public CustomCalendarView goTo(int year, int month) {
        displayedMonth.set(Calendar.YEAR, year);
        displayedMonth.set(Calendar.MONTH, month);
        displayedMonth.set(Calendar.DAY_OF_MONTH, 1);
        renderMonth();
        return this;
    }

    /** Highlight a date programmatically. dateKey = "DD-MM-YYYY" */
    public CustomCalendarView selectDate(String dateKey) {
        selectedDateKey = dateKey;
        renderMonth();
        return this;
    }

    /** Change the accent colour used for the selected day background. */
    public CustomCalendarView setSelectedDayBgColor(int color) {
        colorSelectedBg = color;
        renderMonth();
        return this;
    }

    /** Change "today" highlight background colour. */
    public CustomCalendarView setTodayBgColor(int color) {
        colorTodayBg = color;
        renderMonth();
        return this;
    }

    /** Register a click listener for date taps. */
    public CustomCalendarView setOnDayClickListener(OnDayClickListener listener) {
        this.clickListener = listener;
        return this;
    }

    /** Force a complete layout rebuild after colour changes. */
    public void rebuildLayout() { buildLayout(); }
}
