package com.saceone.tfg.Adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.saceone.tfg.R;
import com.saceone.tfg.Classes.TagRoom;

import java.util.List;

/**
 * Created by ASUS on 11/03/2016.
 */
public class TagRoomAdapter extends RecyclerView.Adapter<TagRoomAdapter.TagRoomViewHolder> {

    private List<TagRoom> tagRoomList;
    private static Context context;

    public TagRoomAdapter(List<TagRoom> tagRoomList, Context context) {
        this.tagRoomList = tagRoomList;
        this.context = context;
    }

    @Override
    public int getItemCount() {
        return tagRoomList.size();
    }

    @Override
    public void onBindViewHolder(TagRoomViewHolder mTagRoomViewHolder, int i) {
        final TagRoom mTagRoom = tagRoomList.get(i);
        mTagRoomViewHolder.txt_room.setText(String.valueOf(mTagRoom.getRoom()));
        if(mTagRoom.getTag()==null){
            mTagRoomViewHolder.txt_tag.setText("Tag no disponible");
            mTagRoomViewHolder.iv_warning.setVisibility(View.VISIBLE);
            mTagRoomViewHolder.iv_warning.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Toast.makeText(context,"No hay TAG asociado a la habitación "+mTagRoom.getRoom()+".",Toast.LENGTH_SHORT).show();
                }
            });
        }
        else{
            mTagRoomViewHolder.txt_tag.setText("Tag: "+mTagRoom.getTag());
            mTagRoomViewHolder.iv_warning.setVisibility(View.GONE);
        }
    }

    @Override
    public TagRoomViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View itemView = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.cv_tag_room, viewGroup, false);
        return new TagRoomViewHolder(itemView);
    }


    public static class TagRoomViewHolder extends RecyclerView.ViewHolder{

        protected ImageView iv_warning;
        protected TextView txt_tag;
        protected TextView txt_room;

        public TagRoomViewHolder(View v) {
            super(v);
            iv_warning = (ImageView) v.findViewById(R.id.iv_tagroom_cv_warning);
            txt_tag =  (TextView) v.findViewById(R.id.txt_cv_tag);
            txt_room = (TextView)  v.findViewById(R.id.txt_cv_room);
        }
    }
}
