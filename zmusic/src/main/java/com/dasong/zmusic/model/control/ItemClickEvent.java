package com.dasong.zmusic.model.control;

import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.dasong.zmusic.R;
import com.dasong.zmusic.model.adapter.BaseRecAdapter;
import com.dasong.zmusic.model.adapter.BaseRecViewHolder;
import com.dasong.zmusic.model.adapter.ShowPageRecAdapter;
import com.dasong.zmusic.model.msg.OnItemClickMsg;

import org.greenrobot.eventbus.EventBus;

/**
 * Created by dason on 2017/9/16 0016.
 */

public class ItemClickEvent{

    public static<Adapter extends BaseRecAdapter,Holder extends BaseRecViewHolder> void post(RecyclerView list,Adapter adapter,View itemView,int newPos){
        if(adapter.isCheckedPosition != newPos){
            View oldView = list.getChildAt(adapter.isCheckedPosition);
            if(oldView != null){
                Holder holder =  (Holder) list.getChildViewHolder(oldView);
                TextView olditem_name = holder.itemView.findViewById(R.id.item_name);
                TextView olditem_art_ablum = holder.itemView.findViewById(R.id.item_art_album);
                olditem_name.setTextColor(itemView.getContext().getResources().getColor(R.color.textColor));
                olditem_art_ablum.setTextColor(itemView.getContext().getResources().getColor(R.color.textColor));
            }
            TextView item_name = itemView.findViewById(R.id.item_name);
            TextView item_art_ablum = itemView.findViewById(R.id.item_art_album);
            item_name.setTextColor(itemView.getContext().getResources().getColor(R.color.colorPrimary));
            item_art_ablum.setTextColor(itemView.getContext().getResources().getColor(R.color.colorPrimary));
            adapter.isCheckedPosition = newPos;
            OnItemClickMsg msg = new OnItemClickMsg();
            msg.setPosition(newPos);
            EventBus.getDefault().post(msg);
        }
    }

    public static<Adapter extends BaseRecAdapter,Holder extends BaseRecViewHolder> void post(RecyclerView list,Adapter adapter,View itemView,int musicPos,int listPos){
        if(adapter.isCheckedPosition != listPos){
            View oldView = list.getChildAt(adapter.isCheckedPosition);
            if(oldView != null){
                Holder holder =  (Holder) list.getChildViewHolder(oldView);
                TextView olditem_name = holder.itemView.findViewById(R.id.item_name);
                TextView olditem_art_ablum = holder.itemView.findViewById(R.id.item_art_album);
                olditem_name.setTextColor(itemView.getContext().getResources().getColor(R.color.textColor));
                olditem_art_ablum.setTextColor(itemView.getContext().getResources().getColor(R.color.textColor));
            }
            TextView item_name = itemView.findViewById(R.id.item_name);
            TextView item_art_ablum = itemView.findViewById(R.id.item_art_album);
            item_name.setTextColor(itemView.getContext().getResources().getColor(R.color.colorPrimary));
            item_art_ablum.setTextColor(itemView.getContext().getResources().getColor(R.color.colorPrimary));
            adapter.isCheckedPosition = listPos;
            OnItemClickMsg msg = new OnItemClickMsg();
            msg.setPosition(musicPos);
            EventBus.getDefault().post(msg);
        }
    }
}
