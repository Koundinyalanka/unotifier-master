package com.remainder.events.unotifier.Adapters;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;

public class EventGroupAdapter extends RecyclerView.Adapter<EventGroupAdapter.EventGroupHolder> {

    @Override
    public EventGroupHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return null;
    }

    @Override
    public void onBindViewHolder(EventGroupHolder holder, int position) {

    }

    @Override
    public int getItemCount() {
        return 0;
    }

    public class EventGroupHolder extends RecyclerView.ViewHolder
    {
        public EventGroupHolder(View itemView) {
            super(itemView);
        }
    }
}
