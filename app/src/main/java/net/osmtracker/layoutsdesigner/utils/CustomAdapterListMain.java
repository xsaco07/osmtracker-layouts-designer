package net.osmtracker.layoutsdesigner.utils;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import net.osmtracker.layoutsdesigner.R;

import java.util.ArrayList;

public class CustomAdapterListMain extends BaseAdapter {

    private Context context;
    private ArrayList<ItemListMain> listItems;

    public CustomAdapterListMain(Context context, ArrayList<ItemListMain> listItems){
        this.context = context;
        this.listItems = listItems;
    }

    @Override
    public int getCount() {
        return listItems.size();
    }

    @Override
    public Object getItem(int i) {
        return listItems.get(i);
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        //makes a references to a class that will stored the views of the current list
        ViewHolderMain holder;

        //then, with this we optimize the scrolling of the listview
        if(view == null){
            //inflate the custom appearance item
            view = LayoutInflater.from(context).inflate(R.layout.item_list_main, null);
            holder = new ViewHolderMain();
            holder.name = (TextView) view.findViewById(R.id.item_layout_name);
            holder.description = (TextView) view.findViewById(R.id.item_layout_description);
            view.setTag(holder);
        }
        else{
            holder = (ViewHolderMain) view.getTag();
        }

        //instance the custom item in the list
        ItemListMain itemListMain = (ItemListMain) getItem(i);

        //put the title and description of the item
        holder.name.setText(itemListMain.getLayoutCreatedName());
        holder.description.setText(itemListMain.getLayoutCreatedDescription());

        return view;
    }

    //simple class to stored the view of the list
    private static class ViewHolderMain {
        private TextView name;
        private TextView description;
    }
}
