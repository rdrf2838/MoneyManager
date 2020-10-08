package com.example.moneymanager.mainActivity;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.example.moneymanager.R;
import com.example.moneymanager.utility.Helper;
import com.example.moneymanager.utility.Payment;

import java.util.ArrayList;
import java.util.List;

public class MainActivityAdapter extends RecyclerView.Adapter<MainActivityAdapter.MainActivityViewHolder> implements Filterable {
    Context _context;
    List<Payment> _payments;
    List<Payment> _paymentsFull;
    List<Payment> _paymentsSelected = new ArrayList<Payment>();
    boolean _multiSelect = false;

    public MainActivityAdapter(List<Payment> payments, Context context) {
        _payments = payments;
        _paymentsFull = new ArrayList<>(payments);
        _context = context;
    }

    public void setPayments(List<Payment> payments) {
        _payments = payments;
        _paymentsFull = new ArrayList<>(payments);
    }

    public List<Payment> getPayments() {
        return _payments;
    }

    public List<Payment> getPaymentsSelected() {
        return _paymentsSelected;
    }

    public class MainActivityViewHolder extends RecyclerView.ViewHolder {
        TextView money, description, date;
        ConstraintLayout constraintLayout;

        public MainActivityViewHolder(@NonNull View itemView) {
            super(itemView);
            money = itemView.findViewById(R.id.textView4);
            description = itemView.findViewById(R.id.textView5);
            date = itemView.findViewById(R.id.textView6);
            constraintLayout = itemView.findViewById(R.id.constraintLayoutMainRow);
        }
    }

    @NonNull
    @Override
    public MainActivityViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(_context);
        View view = inflater.inflate(R.layout.main_row, parent, false);
        return new MainActivityViewHolder(view);
    }

    void selectItem(MainActivityViewHolder holder, Payment payment) {
        if(_paymentsSelected.contains(payment)) {
            _paymentsSelected.remove(payment);
            holder.money.setAlpha(1.0f);
            holder.description.setAlpha(1.0f);
            holder.date.setAlpha(1.0f);
        } else {
            _paymentsSelected.add(payment);
            holder.money.setAlpha(0.3f);
            holder.description.setAlpha(0.3f);
            holder.date.setAlpha(0.3f);
        }
    }

    void clearSelectedItems() {
        _multiSelect = false;
        _paymentsSelected.clear();
    }

    @Override
    public void onBindViewHolder(@NonNull final MainActivityAdapter.MainActivityViewHolder holder, int position) {
        final Payment payment = _payments.get(position);
        holder.money.setText(String.valueOf(payment.get_cost()));
        holder.description.setText(payment.get_description());
        holder.date.setText(Helper.unixToDMY(payment.get_unixtime()));
        holder.money.setAlpha(1.0f);
        holder.description.setAlpha(1.0f);
        holder.date.setAlpha(1.0f);
        holder.constraintLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(_multiSelect) {
                    if(_paymentsSelected.size() == 1 && _paymentsSelected.get(0) == payment) {
                        _multiSelect = false;
                    }
                    selectItem(holder, payment);
                } else {
                    //could add activity to edit payments
                }
            }
        });
        holder.constraintLayout.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                if(!_multiSelect) {
                    _multiSelect = true;
                    selectItem(holder, payment);
                }
                return true;
            }
        });

    }

    @Override
    public int getItemCount() {
        return _payments.size();
    }

    @Override
    public Filter getFilter() {
        return paymentFilter;
    }

    private Filter paymentFilter = new Filter() {

        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            List<Payment> filteredList = new ArrayList<Payment>();
            if(constraint == null || constraint.length() == 0) {
                filteredList.addAll(_paymentsFull);
            } else {
                String filterPattern = constraint.toString().toLowerCase().trim();
                for(Payment payment : _paymentsFull) {
                    if(payment.get_dmytime().contains(filterPattern) || payment.get_description().contains(filterPattern)) {
                        filteredList.add(payment);
                    }
                }
            }
            FilterResults results = new FilterResults();
            results.values = filteredList;
            return results;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            _payments.clear();
            _payments.addAll((List)results.values);
            notifyDataSetChanged();
        }
    };

}
