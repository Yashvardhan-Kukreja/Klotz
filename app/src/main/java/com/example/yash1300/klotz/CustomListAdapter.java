package com.example.yash1300.klotz;

import android.content.Context;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

/**
 * Created by Yash 1300 on 14-10-2017.
 */

public class CustomListAdapter extends ArrayAdapter {
List<Transaction> transactions;

    public CustomListAdapter(@NonNull Context context, List<Transaction> transactions) {
        super(context, R.layout.custom_list_item, transactions);
        this.transactions = transactions;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        LayoutInflater layoutInflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View v = layoutInflater.inflate(R.layout.custom_list_item, parent,false);
        TextView event = v.findViewById(R.id.transactionEvent);
        TextView date = v.findViewById(R.id.transactionDate);
        TextView amount = v.findViewById(R.id.transactionAmount);

        event.setText(transactions.get(position).getTransactionName());
        date.setText(transactions.get(position).getTransactionDate());
        amount.setText(transactions.get(position).getTransactionAmount());
        if (transactions.get(position).getChoice() == 0){
            amount.setTextColor(Color.parseColor("#9e0101"));
        } else {
            amount.setTextColor(Color.parseColor("#00b100"));
        }
        return v;
    }
}
