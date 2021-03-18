package androidx.cursoradapter.widget;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

public class SimpleCursorAdapter extends ResourceCursorAdapter {
  private CursorToStringConverter mCursorToStringConverter;
  
  protected int[] mFrom;
  
  String[] mOriginalFrom;
  
  private int mStringConversionColumn = -1;
  
  protected int[] mTo;
  
  private ViewBinder mViewBinder;
  
  @Deprecated
  public SimpleCursorAdapter(Context paramContext, int paramInt, Cursor paramCursor, String[] paramArrayOfString, int[] paramArrayOfint) {
    super(paramContext, paramInt, paramCursor);
    this.mTo = paramArrayOfint;
    this.mOriginalFrom = paramArrayOfString;
    findColumns(paramCursor, paramArrayOfString);
  }
  
  public SimpleCursorAdapter(Context paramContext, int paramInt1, Cursor paramCursor, String[] paramArrayOfString, int[] paramArrayOfint, int paramInt2) {
    super(paramContext, paramInt1, paramCursor, paramInt2);
    this.mTo = paramArrayOfint;
    this.mOriginalFrom = paramArrayOfString;
    findColumns(paramCursor, paramArrayOfString);
  }
  
  private void findColumns(Cursor paramCursor, String[] paramArrayOfString) {
    if (paramCursor != null) {
      int j = paramArrayOfString.length;
      int[] arrayOfInt = this.mFrom;
      if (arrayOfInt == null || arrayOfInt.length != j)
        this.mFrom = new int[j]; 
      for (int i = 0; i < j; i++)
        this.mFrom[i] = paramCursor.getColumnIndexOrThrow(paramArrayOfString[i]); 
    } else {
      this.mFrom = null;
    } 
  }
  
  public void bindView(View paramView, Context paramContext, Cursor paramCursor) {
    ViewBinder viewBinder = this.mViewBinder;
    int[] arrayOfInt1 = this.mTo;
    int j = arrayOfInt1.length;
    int[] arrayOfInt2 = this.mFrom;
    int i;
    for (i = 0; i < j; i++) {
      View view = paramView.findViewById(arrayOfInt1[i]);
      if (view != null) {
        boolean bool;
        if (viewBinder != null) {
          bool = viewBinder.setViewValue(view, paramCursor, arrayOfInt2[i]);
        } else {
          bool = false;
        } 
        if (!bool) {
          String str2 = paramCursor.getString(arrayOfInt2[i]);
          String str1 = str2;
          if (str2 == null)
            str1 = ""; 
          if (view instanceof TextView) {
            setViewText((TextView)view, str1);
          } else if (view instanceof ImageView) {
            setViewImage((ImageView)view, str1);
          } else {
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append(view.getClass().getName());
            stringBuilder.append(" is not a ");
            stringBuilder.append(" view that can be bounds by this SimpleCursorAdapter");
            throw new IllegalStateException(stringBuilder.toString());
          } 
        } 
      } 
    } 
  }
  
  public void changeCursorAndColumns(Cursor paramCursor, String[] paramArrayOfString, int[] paramArrayOfint) {
    this.mOriginalFrom = paramArrayOfString;
    this.mTo = paramArrayOfint;
    findColumns(paramCursor, paramArrayOfString);
    changeCursor(paramCursor);
  }
  
  public CharSequence convertToString(Cursor paramCursor) {
    CursorToStringConverter cursorToStringConverter = this.mCursorToStringConverter;
    if (cursorToStringConverter != null)
      return cursorToStringConverter.convertToString(paramCursor); 
    int i = this.mStringConversionColumn;
    return (i > -1) ? paramCursor.getString(i) : super.convertToString(paramCursor);
  }
  
  public CursorToStringConverter getCursorToStringConverter() {
    return this.mCursorToStringConverter;
  }
  
  public int getStringConversionColumn() {
    return this.mStringConversionColumn;
  }
  
  public ViewBinder getViewBinder() {
    return this.mViewBinder;
  }
  
  public void setCursorToStringConverter(CursorToStringConverter paramCursorToStringConverter) {
    this.mCursorToStringConverter = paramCursorToStringConverter;
  }
  
  public void setStringConversionColumn(int paramInt) {
    this.mStringConversionColumn = paramInt;
  }
  
  public void setViewBinder(ViewBinder paramViewBinder) {
    this.mViewBinder = paramViewBinder;
  }
  
  public void setViewImage(ImageView paramImageView, String paramString) {
    try {
      paramImageView.setImageResource(Integer.parseInt(paramString));
      return;
    } catch (NumberFormatException numberFormatException) {
      paramImageView.setImageURI(Uri.parse(paramString));
      return;
    } 
  }
  
  public void setViewText(TextView paramTextView, String paramString) {
    paramTextView.setText(paramString);
  }
  
  public Cursor swapCursor(Cursor paramCursor) {
    findColumns(paramCursor, this.mOriginalFrom);
    return super.swapCursor(paramCursor);
  }
  
  public static interface CursorToStringConverter {
    CharSequence convertToString(Cursor param1Cursor);
  }
  
  public static interface ViewBinder {
    boolean setViewValue(View param1View, Cursor param1Cursor, int param1Int);
  }
}


/* Location:              C:\Users\Student\Desktop\APK Decompiling\com.qualcomm.ftcdriverstation_38_apps.evozi.com\classes-dex2jar.jar!\androidx\cursoradapter\widget\SimpleCursorAdapter.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */