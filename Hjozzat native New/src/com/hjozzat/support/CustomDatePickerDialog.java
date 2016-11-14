package com.hjozzat.support;

import java.util.Calendar;

import android.app.DatePickerDialog;
import android.content.Context;
import android.util.Log;
import android.widget.DatePicker;

public class CustomDatePickerDialog extends DatePickerDialog {

	private boolean mIgnoreEvent = false, mignorsunday = true;

	public int mYear, mMonth, mDate, maxYear, maxMonth, maxDay, minYear,
			minMonth, minDay;
	public static int dateflag = 0;
	public static int dateflag2 = 0;

	public CustomDatePickerDialog(Context context, OnDateSetListener callBack,
			int year, int monthOfYear, int dayOfMonth, int maxYear,
			int maxMonth, int maxDay) {
		super(context, callBack, year, monthOfYear, dayOfMonth);
		// TODO Auto-generated constructor stub
		dateflag = 0;
		dateflag2 = 1;
		mYear = year;
		mMonth = monthOfYear;
		mDate = dayOfMonth;

		Log.i("Hello World ::", "Please Its Updating At Every time");

		this.maxYear = maxYear;
		this.maxMonth = maxMonth;
		this.maxDay = maxDay;
		this.minYear = year;
		this.minMonth = monthOfYear;
		this.minDay = dayOfMonth;

	}

	@Override
	public void onDateChanged(DatePicker view, int year, int month, int day) {
		// TODO Auto-generated method stub
		super.onDateChanged(view, year, month, day);
		dateflag = 1;
		dateflag2 = 1;
		if (!mIgnoreEvent) {
			mIgnoreEvent = true;
			if (year > maxYear || month > maxMonth && year == maxYear
					|| day > maxDay && year == maxYear && month == maxMonth) {
				mYear = maxYear;
				mMonth = maxMonth;
				mDate = maxDay;
				view.updateDate(maxYear, maxMonth, maxDay);
			} else if (year < minYear || month < minMonth && year == minYear
					|| day < minDay && year == minYear && month == minMonth) {
				mYear = minYear;
				mMonth = minMonth;
				mDate = minDay;
				view.updateDate(minYear, minMonth, minDay);
			} else {
				Calendar cal = Calendar.getInstance();
				cal.set(Calendar.YEAR, year);
				cal.set(Calendar.MONTH, month);
				cal.set(Calendar.DATE, day);
				if (cal.get(Calendar.DAY_OF_WEEK) == Calendar.SATURDAY) {

					if (mignorsunday) {
						cal.add(Calendar.DATE, 1);
						mYear = cal.get(Calendar.YEAR);
						mMonth = cal.get(Calendar.MONTH);
						mDate = cal.get(Calendar.DATE);
						mignorsunday = false;
					} else if (!mignorsunday) {
						mignorsunday = true;
						cal.add(Calendar.DATE, -1);
						mYear = cal.get(Calendar.YEAR);
						mMonth = cal.get(Calendar.MONTH);
						mDate = cal.get(Calendar.DATE);
					}
				} else {
					mYear = year;
					mMonth = month;
					mDate = day;
				}
				view.updateDate(mYear, mMonth, mDate);
			}
			mIgnoreEvent = false;
		}
	}

	public int getSelectedYear() {
		return mYear;
	}

	public int getSelectedMonth() {
		return mMonth;
	}

	public int getSelectedDate() {
		return mDate;
	}
}