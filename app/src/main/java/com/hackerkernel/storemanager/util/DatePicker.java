package com.hackerkernel.storemanager.util;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.widget.Toast;

import com.hackerkernel.storemanager.R;

import java.util.Calendar;

public class DatePicker extends DialogFragment implements DatePickerDialog.OnDateSetListener{

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        return new DatePickerDialog(getActivity(),this,year,month,day);
    }

    @Override
    public void onDateSet(android.widget.DatePicker view, int year, int monthOfYear, int dayOfMonth) {
        String tag = getTag();
        if(tag.equals("fromDate")){
            Toast.makeText(getActivity(),new StringBuilder().append("FROM date ").append(dayOfMonth).append("-").append(monthOfYear).append("-").append(year),Toast.LENGTH_LONG).show();
        }else if(tag.equals("toDate")){
            Toast.makeText(getActivity(),new StringBuilder().append("TO date ").append(dayOfMonth).append("-").append(monthOfYear).append("-").append(year),Toast.LENGTH_LONG).show();
        }
    }
}
