package com.example.sharan.iotsmartchain.newDesignTiTo;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.example.sharan.iotsmartchain.R;
import com.wafflecopter.multicontactpicker.ContactResult;

import java.util.ArrayList;

public class ContactsAdapter extends BaseAdapter {
    public static final String TAG = ContactsAdapter.class.getSimpleName();

    private Context context;
    private ContactResult contactResult = null;
    private ArrayList<ContactResult> contactResultList = new ArrayList<>();

    public ContactsAdapter(Context context, ArrayList<ContactResult> contactResults) {
        this.context = context;
        this.contactResultList = contactResults;
    }

    @Override
    public int getCount() {
        return 0;
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater layoutInflater = null;

        layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        convertView = layoutInflater.inflate(R.layout.row_peron_contact, parent, false);

        return convertView;
    }
}
