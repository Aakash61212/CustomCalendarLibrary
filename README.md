# CustomCalendarView – Android Java Library

A modern, fully customisable monthly calendar widget for Android, written in pure Java.

---

## Project Structure

```
CustomCalendarLibrary/
├── library/                         ← The reusable library module
│   └── src/main/
│       ├── java/com/customcalendar/lib/
│       │   ├── CustomCalendarView.java   ← The main View
│       │   ├── CalendarDay.java          ← Data model for a single date
│       │   └── OnDayClickListener.java  ← Click callback interface
│       └── res/values/attrs.xml         ← XML attributes
│
├── app/                             ← Demo app showing all features
│   └── src/main/java/com/customcalendar/demo/
│       ├── MainActivity.java            ← Calendar setup + data
│       └── DetailActivity.java          ← Screen opened on date tap
│
└── settings.gradle
```

---

## 1. Add to your project

Copy the `library/` folder into your project root, then in `settings.gradle`:
```groovy
include ':library'
```
In your app's `build.gradle`:
```groovy
dependencies {
    implementation project(':library')
    implementation 'androidx.cardview:cardview:1.0.0'
}
```

---

## 2. Add to XML layout

```xml
<com.customcalendar.lib.CustomCalendarView
    android:id="@+id/calendarView"
    android:layout_width="match_parent"
    android:layout_height="wrap_content"
    app:ccv_cardBgColor="#FFFFFF"
    app:ccv_cardCornerRadius="20dp"
    app:ccv_cardElevation="6dp"
    app:ccv_headerTextColor="#1A1A2E"
    app:ccv_arrowColor="#7C4DFF"
    app:ccv_weekdayTextColor="#9E9E9E"
    app:ccv_dayTextColor="#212121"
    app:ccv_todayBgColor="#EDE7F6"
    app:ccv_todayTextColor="#7C4DFF"
    app:ccv_selectedDayBgColor="#7C4DFF"
    app:ccv_selectedDayTextColor="#FFFFFF" />
```

---

## 3. Set label text below a date

Date key format is always **"DD-MM-YYYY"**, e.g. `"21-08-2026"`.

```java
CustomCalendarView cal = findViewById(R.id.calendarView);

// Navigate to August 2026 (month is 0-based: 7 = August)
cal.goTo(2026, 7);

// Add a green "Claimed" label on the 21st
cal.setDayData("21-08-2026",
    CalendarDay.of("21-08-2026")
        .label("Claimed")              // text shown below the date circle
        .labelColorHex("#4CAF50")      // label text colour
        .bgColorHex("#E8F5E9"));       // circle background colour (optional)

// Add a pink "Claim Now" label on the 24th
cal.setDayData("24-08-2026",
    CalendarDay.of("24-08-2026")
        .label("Claim Now")
        .labelColorHex("#E91E8C")
        .bgColorHex("#FCE4EC"));

// Add a red date text on Independence Day
cal.setDayData("15-08-2026",
    CalendarDay.of("15-08-2026")
        .label("Independence Day")
        .labelColorHex("#F44336")
        .dateTextColorHex("#F44336")); // also colour the date number itself
```

### Bulk-set many dates at once

```java
Map<String, CalendarDay> data = new HashMap<>();
data.put("05-08-2026", CalendarDay.of("05-08-2026").label("Holiday").labelColorHex("#FF9800"));
data.put("10-08-2026", CalendarDay.of("10-08-2026").label("Meeting").labelColorHex("#2196F3"));
cal.setDayDataMap(data);
```

---

## 4. Handle date taps → open another Activity

```java
cal.setOnDayClickListener((dateKey, day, dayOfMonth, month, year) -> {
    // dateKey  = "21-08-2026"
    // day      = the CalendarDay object (or null if no data was set)
    // dayOfMonth / month (0-based) / year  are raw integers

    Intent intent = new Intent(this, DetailActivity.class);
    intent.putExtra("dateKey", dateKey);
    intent.putExtra("label",   day != null ? day.getLabelText() : "");
    startActivity(intent);
});
```

---

## 5. Change colours at runtime (Java)

```java
cal.setSelectedDayBgColor(Color.parseColor("#FF4081")); // hot pink selection
cal.setTodayBgColor(Color.parseColor("#FFF9C4"));       // yellow today highlight
```

---

## 6. CalendarDay builder — all options

| Method | Description |
|---|---|
| `.label(String text)` | Text shown below the date number |
| `.labelColor(int color)` | Label colour (int) |
| `.labelColorHex(String hex)` | Label colour (hex string) |
| `.bgColor(int color)` | Date circle background colour |
| `.bgColorHex(String hex)` | Date circle background colour (hex) |
| `.dateTextColor(int color)` | Date number colour |
| `.dateTextColorHex(String hex)` | Date number colour (hex) |
| `.clickable(boolean)` | Whether this date is tappable (default `true`) |

---

## 7. XML Attributes Reference

| Attribute | Type | Description |
|---|---|---|
| `ccv_cardBgColor` | color | Card background |
| `ccv_cardCornerRadius` | dimension | Card rounded corners |
| `ccv_cardElevation` | dimension | Card shadow |
| `ccv_headerTextColor` | color | Month/year title |
| `ccv_arrowColor` | color | Navigation arrow colour |
| `ccv_weekdayTextColor` | color | Mon–Sat header text |
| `ccv_dayTextColor` | color | Normal date number |
| `ccv_todayBgColor` | color | Today circle background |
| `ccv_todayTextColor` | color | Today date number |
| `ccv_selectedDayBgColor` | color | Selected date circle |
| `ccv_selectedDayTextColor` | color | Selected date number |

---

## 8. Public API summary

```java
cal.goTo(int year, int month)                          // navigate to month (month 0-based)
cal.selectDate(String dateKey)                         // highlight a date
cal.setDayData(String dateKey, CalendarDay day)        // add/update one date
cal.setDayDataMap(Map<String,CalendarDay> map)         // bulk update
cal.clearDayData(String dateKey)                       // remove one date
cal.clearAllDayData()                                  // remove all
cal.setSelectedDayBgColor(int color)                   // change accent colour
cal.setTodayBgColor(int color)                         // change today colour
cal.setOnDayClickListener(OnDayClickListener listener) // date tap callback
cal.rebuildLayout()                                    // force full redraw
```

---

## Requirements

- **minSdk**: 21 (Android 5.0 Lollipop)
- **Java**: 1.8+
- **Dependencies**: `androidx.cardview:cardview:1.0.0`, `androidx.appcompat:appcompat:1.6.1`
