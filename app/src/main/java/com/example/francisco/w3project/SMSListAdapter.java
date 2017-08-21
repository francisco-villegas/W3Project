package com.example.francisco.w3project;

import android.content.Context;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.example.francisco.w3project.models.SMS;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

public class SMSListAdapter extends RecyclerView.Adapter<SMSListAdapter.ViewHolder> {

    private static final String TAG = "SMSListAdapter";
    ArrayList<SMS> smsList;
    Context context;

    public SMSListAdapter(ArrayList<SMS> smsList ) {
        this.smsList = smsList;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Log.d(TAG, "onCreateViewHolder: ");
        context = parent.getContext();
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_list_messages, parent, false);
        return new ViewHolder(view);
    }

    private int lastPosition = -1;
    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {

        if(position > lastPosition){
            //Animation animation = AnimationUtils
        }

        Log.d(TAG, "onBindViewHolder: ");
        final SMS sms = smsList.get(position);
        holder.tvNumber.setText(sms.getNumber());
        holder.tvMessage.setText(sms.getMessage());

        holder.scroll.setOnTouchListener(new View.OnTouchListener() {

            public boolean onTouch(View v, MotionEvent event) {
                // Disallow the touch request for parent scroll on touch of child view
                if(holder.scroll.getChildAt(0).getHeight() > holder.scroll_parent.getMeasuredHeight()) {
                    v.getParent().requestDisallowInterceptTouchEvent(true);
                    return false;
                }
                return true;
            }
        });

    }

    @Override
    public int getItemCount() {
        //Log.d(TAG, "getItemCount: "+smsList.size());
        return smsList.size();
    }

    public void UpdateData(SMS sms){
        smsList.add(0,sms);
        notifyDataSetChanged();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        @Nullable
        @BindView(R.id.tvNumber)
        TextView tvNumber;

        @Nullable
        @BindView(R.id.tvMessage)
        TextView tvMessage;

        @Nullable
        @BindView(R.id.scroll)
        ScrollView scroll;

        @Nullable
        @BindView(R.id.scroll_parent)
        FrameLayout scroll_parent;

        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
