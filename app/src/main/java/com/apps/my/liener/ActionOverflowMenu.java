package com.apps.my.liener;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

/**
 * Created by rahul on 26/2/17.
 */
public class ActionOverflowMenu extends LinearLayout implements View.OnClickListener {
    private static final String TAG = ActionOverflowMenu.class.getSimpleName();

    private MenuOptionListener menuOptionListener;

    private RelativeLayout.LayoutParams params;

    private boolean isOpen;

    public void setMenuOptionListener(MenuOptionListener menuOptionListener) {
        this.menuOptionListener = menuOptionListener;
    }

    public boolean isOpen() {
        return isOpen;
    }

    public void setOpen(boolean open) {
        isOpen = open;
    }

    public RelativeLayout.LayoutParams getParams() {
        return params;
    }

    public ActionOverflowMenu(Context context) {
        super(context);
        inflate(context, R.layout.browser_overflow_menu, this);
        initialize();
        setClickListeners();
    }

    private void initialize() {
        params = new RelativeLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        params.addRule(RelativeLayout.ALIGN_PARENT_END, RelativeLayout.TRUE);
        params.addRule(RelativeLayout.ALIGN_PARENT_TOP, RelativeLayout.TRUE);
        params.topMargin = 10;
        isOpen = false;
    }

    private void setClickListeners() {
        setItemClickListeners(new int[]{
                R.id.find_in_page,
                R.id.open_in,
                R.id.desktop_site
        });
    }

    @Override
    public void onClick(View view) {
        isOpen = !isOpen;
    }

    public interface MenuOptionListener {
        public void onOptionClicked(int resourceId);
    }

    private void setItemClickListeners(int[] resourceIds) {
        for (final int resourceId : resourceIds) {
            findViewById(resourceId).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    menuOptionListener.onOptionClicked(resourceId);
                }
            });
        }
    }
}
