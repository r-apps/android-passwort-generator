package de.rubeen.apps.android.passwortgenerator;

import android.content.Context;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.TextView;
import de.rubeen.apps.android.passwortgenerator.logic.IPasswordObject;

import java.util.List;

public class CatalogAdapter extends BaseExpandableListAdapter {
    private List<IPasswordObject> passwordObjects;
    private Context context;

    public CatalogAdapter(Context context) {
        this.context = context;
        this.passwordObjects = History.getInstance().getPasswordObjects();
    }

    @Override
    public int getGroupCount() {
        return passwordObjects.size();
    }

    @Override
    public int getChildrenCount(int i) {
        return 1;
    }

    @Override
    public Object getGroup(int i) {
        return passwordObjects.get(i);
    }

    @Override
    public Object getChild(int i, int i1) {
        return passwordObjects.get(i);
    }

    @Override
    public long getGroupId(int i) {
        return i;
    }

    @Override
    public long getChildId(int i, int i1) {
        return i1;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    @Override
    public View getGroupView(final int groupPos, final boolean isExpanded, View view, final ViewGroup parentView) {
        IPasswordObject passwordObject = (IPasswordObject)getGroup(groupPos);
        String headerTitle = passwordObject.getTitle();
        if (view == null) {
            LayoutInflater inflater = (LayoutInflater) this.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = inflater.inflate(R.layout.listview_catalog_header, null);
        }

        TextView textView = view.findViewById(R.id.expandedCatalogListHeader);
        textView.setTypeface(null, Typeface.BOLD);

        textView.setText(headerTitle);

        return view;
    }

    @Override
    public View getChildView(final int groupPos, final int childPos, final boolean isLastChild, View view, final ViewGroup parentView) {
        String childText = ((IPasswordObject) getGroup(groupPos)).getPassword();
        if (view == null) {
            LayoutInflater inflater = (LayoutInflater) this.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = inflater.inflate(R.layout.listview_catalog_item, null);
        }

        TextView textView = view.findViewById(R.id.expandedHistoryListItem);
        textView.setText(childText);

        return view;
    }

    @Override
    public boolean isChildSelectable(int i, int i1) {
        return true;
    }
}
