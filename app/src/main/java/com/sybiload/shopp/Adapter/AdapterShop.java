package com.sybiload.shopp.Adapter;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.sybiload.shopp.ActivityShop;
import com.sybiload.shopp.Database.Item.DatabaseItem;
import com.sybiload.shopp.Item;
import com.sybiload.shopp.List;
import com.sybiload.shopp.Misc;
import com.sybiload.shopp.R;
import com.sybiload.shopp.Static;

import java.util.ArrayList;

public class AdapterShop extends RecyclerView.Adapter<AdapterShop.ViewHolder>
{
    DatabaseItem database;
    Context ctx;

    public AdapterShop(Context ctx)
    {
        this.ctx = ctx;
        database = new DatabaseItem(ctx, Static.currentList.getDatabase());

        Static.currentList.sortShop();
        Static.currentList.sortShopDone();
    }

    public class ViewHolder extends RecyclerView.ViewHolder
    {
        public TextView txtHeader;
        public TextView txtFooter;
        public ImageView imageViewItemIcon;

        public ViewHolder(View v)
        {
            super(v);

            txtHeader = (TextView) v.findViewById(R.id.textViewShopFirstLine);
            txtFooter = (TextView) v.findViewById(R.id.textViewShopSecondLine);
            imageViewItemIcon = (ImageView) v.findViewById(R.id.imageViewShopItemIcon);
        }
    }

    public void remove(Item myItem)
    {
        // set new attributes
        myItem.toShop(false);
        myItem.setDescription(null);

        // update database
        database.open();
        database.updateByName(myItem.getName(), myItem);
        database.close();

        // remove item from itemShop
        int position = Static.currentList.itemShop.indexOf(myItem);
        Static.currentList.itemShop.remove(position);

        // add item to itemAvailable
        Static.currentList.itemAvailable.add(myItem);

        notifyItemRemoved(position);
    }

    public void done(Item myItem)
    {
        // set new attributes
        if (!myItem.isDone())
            myItem.done(true);
        else
            myItem.done(false);

        // update database
        database.open();
        database.updateByName(myItem.getName(), myItem);
        database.close();

        // get current position of the item
        int position = Static.currentList.itemShop.indexOf(myItem);

        // sort itemShop
        Static.currentList.sortShop();
        Static.currentList.sortShopDone();

        // move the item to the new position thanks to the previous position that we stored
        notifyItemMoved(position, Static.currentList.itemShop.indexOf(myItem));
    }


    public void update(Item oldItem, Item newItem)
    {
        // update database

        database.open();
        database.updateByName(oldItem.getName(), newItem);
        database.close();


        int position = Static.currentList.itemShop.indexOf(oldItem);

        // prevent the 'not update on set list' bug
        Static.currentList.itemShop.set(position, newItem);

        // if there is a bug somewhere, be sure it's here !

        notifyItemChanged(position);

        // possibility to remove this for the simple variable 'position' ?
        Item it = Static.currentList.itemShop.get(position);

        Static.currentList.sortShop();
        Static.currentList.sortShopDone();

        int orf = Static.currentList.itemShop.indexOf(it);


        notifyItemMoved(position, orf);
    }


    @Override
    public AdapterShop.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType)
    {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_shop, parent, false);

        ViewHolder vh = new ViewHolder(v);
        return vh;
    }
    
    

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position)
    {
        if (Static.currentList.itemShop.get(position).isToShop())
        {
            final Item myItem = Static.currentList.itemShop.get(position);

            holder.txtHeader.setText(myItem.getName());

            Item it = myItem;

            // when adapter is refreshing, check if the current row has a description or not and adapt the layout
            if (it.getDescription() == null || it.getDescription().equals(""))
            {
                holder.txtFooter.setVisibility(View.GONE);
                holder.txtHeader.setLayoutParams(new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.MATCH_PARENT));
            }
            else
            {
                holder.txtFooter.setText(myItem.getDescription());
                holder.txtHeader.setLayoutParams(new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT));
                holder.txtFooter.setVisibility(View.VISIBLE);
            }


            // when there is a long click on a row, either remove item if the toolbar of the activity is not opened, or hide the toolbar if it is opened. More details below
            holder.itemView.setOnLongClickListener(new View.OnLongClickListener()
            {
                @Override
                public boolean onLongClick(View v)
                {

                    if(ctx instanceof ActivityShop){
                        // absolute bullshit
                        ActivityShop.currentItem = myItem;
                        ActivityShop.currentAdapter = AdapterShop.this;
                        ((ActivityShop)ctx).barAction();
                    }

                    return true;
                }
            });

            // if current row is done, change the color or the icon and the style of the text
            if (myItem.isDone())
            {
                holder.txtHeader.setPaintFlags(holder.txtHeader.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
                holder.txtFooter.setPaintFlags(holder.txtFooter.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
                holder.imageViewItemIcon.setColorFilter(Color.parseColor("#78909C"));
            }
            else
            {
                holder.txtHeader.setPaintFlags(holder.txtHeader.getPaintFlags() & (~ Paint.STRIKE_THRU_TEXT_FLAG));
                holder.txtFooter.setPaintFlags(holder.txtFooter.getPaintFlags() & (~ Paint.STRIKE_THRU_TEXT_FLAG));
                holder.imageViewItemIcon.setColorFilter(Color.parseColor("#2196F3"));
            }

            // when doing a click on the row, put it done
            holder.itemView.setOnClickListener(new OnClickListener()
            {
                @Override
                public void onClick(View v)
                {
                    done(myItem);

                    if (myItem.isDone())
                    {
                        holder.txtHeader.setPaintFlags(holder.txtHeader.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
                        holder.txtFooter.setPaintFlags(holder.txtFooter.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
                        holder.imageViewItemIcon.setColorFilter(Color.parseColor("#78909C"));
                    }
                    else
                    {
                        holder.txtHeader.setPaintFlags(holder.txtHeader.getPaintFlags() & (~ Paint.STRIKE_THRU_TEXT_FLAG));
                        holder.txtFooter.setPaintFlags(holder.txtFooter.getPaintFlags() & (~ Paint.STRIKE_THRU_TEXT_FLAG));
                        holder.imageViewItemIcon.setColorFilter(Color.parseColor("#2196F3"));
                    }
                }
            });

            holder.imageViewItemIcon.setImageDrawable(ctx.getResources().getDrawable(myItem.getIcon()));

            // when doing a long click on the icon of a row, open the edit toolbar in the parent activity
            holder.imageViewItemIcon.setOnLongClickListener(new View.OnLongClickListener()
            {
                @Override
                public boolean onLongClick(View v)
                {

                    // prevent user from deleting item and editing it at the same time
                    if (!ActivityShop.toolbarOpened)
                        remove(myItem);
                    else
                    {
                        if(ctx instanceof ActivityShop)
                            ((ActivityShop)ctx).barAction();
                    }

                    return true;
                }
            });

        }
    }

    @Override
    public int getItemCount()
    {
        return Static.currentList.itemShop.size();
    }

}
