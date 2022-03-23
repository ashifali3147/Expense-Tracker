package dev.webguru.expensetracker2;

import android.content.Context;
import android.content.Intent;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class CustomAdapter extends RecyclerView.Adapter<CustomAdapter.MyViewHolder> {
    ArrayList<User> userList;
    Context context;

    public CustomAdapter(Context context, ArrayList<User> userList) {
        this.context = context;
        this.userList = userList;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // infalte the item Layout
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.rowlayout, parent, false);
        // set the view's size, margins, paddings and layout parameters
        MyViewHolder vh = new MyViewHolder(v); // pass the view to View Holder
        return vh;
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, final int position) {
        // set the data in items
        User u = userList.get(position);
        holder.eid.setText("" + u.getId());
        holder.name.setText(u.getName());
        holder.email.setText(u.getEmail());

        holder.itemView.setTag(""+u.getId());
        holder.itemView.setId(position);
        // implement setOnClickListener event on item view.
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // display a toast with person name on item click
                Toast.makeText(context, userList.get(position).getName() + "\nPosition: " + (position + 1) , Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public int getItemCount() {
        return userList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder
            implements View.OnCreateContextMenuListener,
            MenuItem.OnMenuItemClickListener {
        TextView eid;// init the item view's
        TextView name;
        TextView email;
        int rec_id = 0;
        int position = -1;

        public MyViewHolder(View itemView) {
            super(itemView);

            // get the reference of item view's
            eid = (TextView) itemView.findViewById(R.id.eid);
            name = (TextView) itemView.findViewById(R.id.name);
            email = (TextView) itemView.findViewById(R.id.email);

            itemView.setOnCreateContextMenuListener(this);
        }

        @Override
        public void onCreateContextMenu(ContextMenu contextMenu, View view, ContextMenu.ContextMenuInfo contextMenuInfo) {
            contextMenu.setHeaderTitle("Select The Action");
            MenuItem addItem = contextMenu.add(0, 0, 0, "Modify"); //groupId, itemId, order, title
            addItem.setOnMenuItemClickListener(this);
            MenuItem deleteItem = contextMenu.add(0, 1, 1, "Delete");
            deleteItem.setOnMenuItemClickListener(this);
            // Get the userid on which item was selected
            rec_id = Integer.parseInt(view.getTag().toString());
            position = view.getId();
        }

        @Override
        public boolean onMenuItemClick(MenuItem menuItem) {
            //Toast.makeText(context, "You clicked on Modify for Item #" + rec_id, Toast.LENGTH_LONG).show();
            switch (menuItem.getItemId()) {
                case 0:
                    //Toast.makeText(context, "You clicked on Modify for Item #" + rec_id, Toast.LENGTH_LONG).show();
                    Intent modifyRecord = new Intent(context, AddUserActivity.class);
                    modifyRecord.putExtra("rec_id", rec_id);
                    context.startActivity(modifyRecord);
                    return true;
                case 1:
                    //Toast.makeText(context, "You clicked on Delete for Item #" + position, Toast.LENGTH_LONG).show();
                    delete_rec();
                    return true;
            }
            return false;
        }

        private void delete_rec() {
            DBAdapter db = new DBAdapter(context);
            //---delete a contact---
            db.open();
            if (db.deleteContact(rec_id)) {
                Toast.makeText(context, "Delete successful.", Toast.LENGTH_LONG).show();
                userList.remove(position);
                notifyDataSetChanged();
            } else {
                Toast.makeText(context, "Delete failed.", Toast.LENGTH_LONG).show();
            }
            db.close();
        }
    }
}
