package net.osmtracker.layoutsdesigner.utils;

import android.content.Context;
import android.support.constraint.ConstraintLayout;
import android.support.constraint.ConstraintSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import net.osmtracker.layoutsdesigner.R;

import java.util.ArrayList;

public class CustomGridItemAdapter extends BaseAdapter {
    private Context context;
    private ArrayList<LayoutButtonGridItem> itemsGridList;

    public CustomGridItemAdapter(Context context, ArrayList<LayoutButtonGridItem> list){
        this.context = context;
        this.itemsGridList = list;
    }

    /**
     * How many items are in the data set represented by this Adapter.
     *
     * @return Count of items.
     */
    @Override
    public int getCount() {
        return itemsGridList.size();
    }

    /**
     * Get the data item associated with the specified position in the data set.
     *
     * @param position Position of the item whose data we want within the adapter's
     *                 data set.
     * @return The data at the specified position.
     */
    @Override
    public Object getItem(int position) {
        return itemsGridList.get(position);
    }

    /**
     * Get the row id associated with the specified position in the list.
     *
     * @param position The position of the item within the adapter's data set whose row id we want.
     * @return The id of the item at the specified position.
     */
    @Override
    public long getItemId(int position) {
        return itemsGridList.get(position).getId();
    }

    /**
     * Get a View that displays the data at the specified position in the data set. You can either
     * create a View manually or inflate it from an XML layout file. When the View is inflated, the
     * parent View (GridView, ListView...) will apply default layout parameters unless you use
     * {@link LayoutInflater#inflate(int, ViewGroup, boolean)}
     * to specify a root view and to prevent attachment to the root.
     *
     * @param position    The position of the item within the adapter's data set of the item whose view
     *                    we want.
     * @param convertView The old view to reuse, if possible. Note: You should check that this view
     *                    is non-null and of an appropriate type before using. If it is not possible to convert
     *                    this view to display the correct data, this method can create a new view.
     *                    Heterogeneous lists can specify their number of view types, so that this View is
     *                    always of the right type (see {@link #getViewTypeCount()} and
     *                    {@link #getItemViewType(int)}).
     * @param parent      The parent that this view will eventually be attached to
     * @return A View corresponding to the data at the specified position.
     */
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        ViewHolderGrid holder;

        if(convertView == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.layout_grid_item, parent, false);
            holder = new ViewHolderGrid();
            holder.txtItemName = convertView.findViewById(R.id.txt_item_grid_name);
            holder.imvItemIcon = convertView.findViewById(R.id.imv_item_grid_icon);
            convertView.setTag(holder);
        }
        else{
            holder = (ViewHolderGrid) convertView.getTag();
        }

        LayoutButtonGridItem item = (LayoutButtonGridItem) getItem(position);
        //set the name and the icon in the current grid button
        holder.txtItemName.setText(item.getItemName());

        ConstraintLayout constraintLayout = (ConstraintLayout) convertView.findViewById(R.id.parent_item_layout);
        ConstraintSet constraintSet = new ConstraintSet();
        constraintSet.clone(constraintLayout);
        if(item.getItemName().equals("") && item.getImageURI() == null){
            constraintSet.connect(R.id.imv_item_grid_icon, ConstraintSet.BOTTOM, R.id.parent_item_layout, ConstraintSet.BOTTOM, 6);
            constraintSet.applyTo(constraintLayout);
            holder.txtItemName.setVisibility(View.INVISIBLE);
            holder.imvItemIcon.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_add_circle));
        }
        else if(item.getImageURI() != null) {
            holder.imvItemIcon.setImageURI(item.getImageURI());
        }
        else if(item.getDefaultIcon() != null){
            holder.imvItemIcon.setImageDrawable(item.getDefaultIcon());
        }
        else{
            constraintSet.connect(R.id.txt_item_grid_name, ConstraintSet.TOP, R.id.parent_item_layout, ConstraintSet.TOP, 8);
            constraintSet.applyTo(constraintLayout);
            holder.imvItemIcon.setVisibility(View.INVISIBLE);
        }

        return convertView;
    }

    //simple class to stored the view of the list
    private static class ViewHolderGrid {
        private TextView txtItemName;
        private ImageView imvItemIcon;
    }
}
