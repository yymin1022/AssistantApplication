package io.tqeil.assistant;

import android.content.Context;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;

class ChatList{
    public int type;
    public String msg;
}

public class ChatListAdapter extends BaseAdapter{

    private ArrayList<ChatList> m_List = new ArrayList<>();

    public ChatListAdapter(){
        super();
    }

    public void add(String _msg, int _type){
       ChatList chatList = new ChatList();
       chatList.msg = _msg;
       chatList.type = _type;

       m_List.add(chatList);
    }

    @Override
    public int getCount(){
        return m_List.size();
    }

    @Override
    public Object getItem(int position){
        return m_List.get(position);
    }

    @Override
    public long getItemId(int position){
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent){
        final Context context = parent.getContext();

        TextView text;
        CustomHolder holder;
        LinearLayout layout;
        View viewRight;
        View viewLeft;

        if(convertView == null){
            LayoutInflater inflater =(LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.list_item_chat, parent, false);

            layout    = convertView.findViewById(R.id.layout);
            text    = convertView.findViewById(R.id.chatText);
            viewRight    = convertView.findViewById(R.id.chatRight);
            viewLeft    = convertView.findViewById(R.id.chatLeft);

            holder = new CustomHolder();
            holder.m_TextView = text;
            holder.layout = layout;
            holder.viewRight = viewRight;
            holder.viewLeft = viewLeft;
            convertView.setTag(holder);
        }else{
            holder =(CustomHolder)convertView.getTag();
            text = holder.m_TextView;
            layout = holder.layout;
            viewRight = holder.viewRight;
            viewLeft = holder.viewLeft;
        }

        text.setText(m_List.get(position).msg);

        switch(m_List.get(position).type){
            case 0:
                text.setBackgroundResource(R.drawable.left);
                layout.setGravity(Gravity.LEFT);
                viewRight.setVisibility(View.GONE);
                viewLeft.setVisibility(View.GONE);
                break;
            case 1:
                text.setBackgroundResource(R.drawable.right);
                layout.setGravity(Gravity.RIGHT);
                viewRight.setVisibility(View.GONE);
                viewLeft.setVisibility(View.GONE);
                break;
        }
        return convertView;
    }

    private class CustomHolder{
        TextView m_TextView;
        LinearLayout layout;
        View viewRight;
        View viewLeft;
    }
}
