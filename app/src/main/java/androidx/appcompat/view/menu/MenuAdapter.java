package androidx.appcompat.view.menu;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import java.util.ArrayList;

public class MenuAdapter extends BaseAdapter {
  MenuBuilder mAdapterMenu;
  
  private int mExpandedIndex = -1;
  
  private boolean mForceShowIcon;
  
  private final LayoutInflater mInflater;
  
  private final int mItemLayoutRes;
  
  private final boolean mOverflowOnly;
  
  public MenuAdapter(MenuBuilder paramMenuBuilder, LayoutInflater paramLayoutInflater, boolean paramBoolean, int paramInt) {
    this.mOverflowOnly = paramBoolean;
    this.mInflater = paramLayoutInflater;
    this.mAdapterMenu = paramMenuBuilder;
    this.mItemLayoutRes = paramInt;
    findExpandedIndex();
  }
  
  void findExpandedIndex() {
    MenuItemImpl menuItemImpl = this.mAdapterMenu.getExpandedItem();
    if (menuItemImpl != null) {
      ArrayList<MenuItemImpl> arrayList = this.mAdapterMenu.getNonActionItems();
      int j = arrayList.size();
      for (int i = 0; i < j; i++) {
        if ((MenuItemImpl)arrayList.get(i) == menuItemImpl) {
          this.mExpandedIndex = i;
          return;
        } 
      } 
    } 
    this.mExpandedIndex = -1;
  }
  
  public MenuBuilder getAdapterMenu() {
    return this.mAdapterMenu;
  }
  
  public int getCount() {
    ArrayList<MenuItemImpl> arrayList;
    if (this.mOverflowOnly) {
      arrayList = this.mAdapterMenu.getNonActionItems();
    } else {
      arrayList = this.mAdapterMenu.getVisibleItems();
    } 
    return (this.mExpandedIndex < 0) ? arrayList.size() : (arrayList.size() - 1);
  }
  
  public boolean getForceShowIcon() {
    return this.mForceShowIcon;
  }
  
  public MenuItemImpl getItem(int paramInt) {
    ArrayList<MenuItemImpl> arrayList;
    if (this.mOverflowOnly) {
      arrayList = this.mAdapterMenu.getNonActionItems();
    } else {
      arrayList = this.mAdapterMenu.getVisibleItems();
    } 
    int j = this.mExpandedIndex;
    int i = paramInt;
    if (j >= 0) {
      i = paramInt;
      if (paramInt >= j)
        i = paramInt + 1; 
    } 
    return arrayList.get(i);
  }
  
  public long getItemId(int paramInt) {
    return paramInt;
  }
  
  public View getView(int paramInt, View paramView, ViewGroup paramViewGroup) {
    boolean bool;
    View view = paramView;
    if (paramView == null)
      view = this.mInflater.inflate(this.mItemLayoutRes, paramViewGroup, false); 
    int j = getItem(paramInt).getGroupId();
    int i = paramInt - 1;
    if (i >= 0) {
      i = getItem(i).getGroupId();
    } else {
      i = j;
    } 
    ListMenuItemView listMenuItemView = (ListMenuItemView)view;
    if (this.mAdapterMenu.isGroupDividerEnabled() && j != i) {
      bool = true;
    } else {
      bool = false;
    } 
    listMenuItemView.setGroupDividerEnabled(bool);
    MenuView.ItemView itemView = (MenuView.ItemView)view;
    if (this.mForceShowIcon)
      listMenuItemView.setForceShowIcon(true); 
    itemView.initialize(getItem(paramInt), 0);
    return view;
  }
  
  public void notifyDataSetChanged() {
    findExpandedIndex();
    super.notifyDataSetChanged();
  }
  
  public void setForceShowIcon(boolean paramBoolean) {
    this.mForceShowIcon = paramBoolean;
  }
}


/* Location:              C:\Users\Student\Desktop\APK Decompiling\com.qualcomm.ftcdriverstation_38_apps.evozi.com\classes-dex2jar.jar!\androidx\appcompat\view\menu\MenuAdapter.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */