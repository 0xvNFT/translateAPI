package com.dichthuatjun88binh.jun88.utils;

import static com.dichthuatjun88binh.jun88.utils.TranslatorConstants.position;
import static com.dichthuatjun88binh.jun88.utils.TranslatorConstants.source_l;
import static com.dichthuatjun88binh.jun88.utils.TranslatorConstants.source_t;
import static com.dichthuatjun88binh.jun88.utils.TranslatorConstants.target_l;
import static com.dichthuatjun88binh.jun88.utils.TranslatorConstants.target_t;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.chauthai.swipereveallayout.SwipeRevealLayout;
import com.chauthai.swipereveallayout.ViewBinderHelper;
import com.dichthuatjun88binh.jun88.R;
import com.dichthuatjun88binh.jun88.activites.DetailsHistory;
import com.dichthuatjun88binh.jun88.model.DataBaseModel;

import java.util.List;


public class TranslatorAdapter extends RecyclerView.Adapter<TranslatorAdapter.ViewHolder> {

    public final ViewBinderHelper viewBinderHelper = new ViewBinderHelper();
    private final List<DataBaseModel> list;
    public int positionSwipe = -1;
    Context context;
    ViewHolder holder;
    DataBaseModel model;
    ItemListener itemListener;

    public TranslatorAdapter(Context context, List<DataBaseModel> list) {
        this.context = context;
        this.list = list;
    }

    public void setItemListener(ItemListener itemListener) {
        this.itemListener = itemListener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(context).inflate(R.layout.row_item, parent, false));
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        this.holder = holder;
        model = this.list.get(position);
        holder.source_lang.setText(model.getSource_language_jun());
        holder.source_text.setText(model.getSource_language_txt());
        holder.target_lang.setText(model.getTarget_language());
        holder.target_text.setText(model.getTarget_language_txt());
        viewBinderHelper.setOpenOnlyOne(true);
        viewBinderHelper.bind(holder.swipeRevealLayout, String.valueOf(position));
        holder.swipeRevealLayout.setSwipeListener(new SwipeRevealLayout.SwipeListener() {
            @Override
            public void onClosed(SwipeRevealLayout view) {

            }

            @Override
            public void onOpened(SwipeRevealLayout view) {
                positionSwipe = holder.getAdapterPosition();
            }

            @Override
            public void onSlide(SwipeRevealLayout view, float slideOffset) {

            }
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public void removeItem(int position) {
        list.remove(position);
        notifyItemRemoved(position);
        notifyItemRangeChanged(position, list.size());
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        TextView source_lang;
        TextView source_text;
        TextView target_lang;
        TextView target_text;
        SwipeRevealLayout swipeRevealLayout;
        RelativeLayout layoutDelete;
        LinearLayout layoutMain;

        ViewHolder(View v) {
            super(v);
            source_lang = v.findViewById(R.id.source_lang);
            source_text = v.findViewById(R.id.source_text);
            target_lang = v.findViewById(R.id.target_lang);
            target_text = v.findViewById(R.id.target_text);
            swipeRevealLayout = v.findViewById(R.id.swipeLayout);
            layoutDelete = v.findViewById(R.id.layoutDelete);
            layoutMain = v.findViewById(R.id.layoutMain);

            layoutMain.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    model = list.get(ViewHolder.this.getAdapterPosition());
                    source_l = model.getSource_language_jun();
                    source_t = model.getSource_language_txt();
                    target_l = model.getTarget_language();
                    target_t = model.getTarget_language_txt();
                    position = model.getId();
                    Intent intent = new Intent(context, DetailsHistory.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    context.startActivity(intent);
                    //ShowLargAdd();
                }
            });

            layoutDelete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    itemListener.onDelete(ViewHolder.this.getAdapterPosition());
//                    removeItem(ViewHolder.this.getAdapterPosition());
                }
            });
        }

    }
}
