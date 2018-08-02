package com.remainder.events.unotifier.Adapters;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import com.remainder.events.unotifier.EventActivity;
import com.remainder.events.unotifier.Helpers.CohortExpiry;
import com.remainder.events.unotifier.R;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class CohortExpiryAdapter extends RecyclerView.Adapter<CohortExpiryAdapter.CohortHolder> {
    Context context;
    ArrayList<CohortExpiry> eventArrayList=new ArrayList<>();
    public CohortExpiryAdapter(Context context,ArrayList<CohortExpiry> eventArrayList)
    {
        this.context=context;
        this.eventArrayList=eventArrayList;
    }
    @Override
    public CohortExpiryAdapter.CohortHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        context=parent.getContext();
        LayoutInflater inflater=LayoutInflater.from(context);
        View v=inflater.inflate(R.layout.event_card,parent,false);
        CohortExpiryAdapter.CohortHolder eventHolder=new CohortExpiryAdapter.CohortHolder(v);
        return eventHolder;
    }

    @Override
    public void onBindViewHolder(final CohortExpiryAdapter.CohortHolder holder, final int position) {
        holder.bind(position);
        holder.cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(context,EventActivity.class);
                intent.putExtra("cohort",eventArrayList.get(position));
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return eventArrayList.size();
    }
    public class CohortHolder extends RecyclerView.ViewHolder
    {
        TextView summary,date,noofstu;
        CardView cardView;
        ImageButton imageButton;
        public CohortHolder(View itemView) {
            super(itemView);
            summary=(TextView)itemView.findViewById(R.id.summary_card);
            date=(TextView)itemView.findViewById(R.id.date_card);
            noofstu=(TextView)itemView.findViewById(R.id.number_card);
            cardView=(CardView)itemView.findViewById(R.id.card_view);
        }

        public void bind(int id)
        {
            summary.setText(eventArrayList.get(id).getCohortId()+" ("+eventArrayList.get(id).getNanodegree()+")");
            List<String> list= Arrays.asList(eventArrayList.get(id).getEmailList().split(","));
            date.setText(eventArrayList.get(id).getExpiryDate());
            if(eventArrayList.get(id).getEmailList()!=null)
                noofstu.setText(list.size()+"");
            else
                noofstu.setText(0+"");
        }
    }
}
