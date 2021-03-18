package androidx.appcompat.widget;

import android.os.Build;
import android.view.View;

public class TooltipCompat {
  public static void setTooltipText(View paramView, CharSequence paramCharSequence) {
    if (Build.VERSION.SDK_INT >= 26) {
      paramView.setTooltipText(paramCharSequence);
      return;
    } 
    TooltipCompatHandler.setTooltipText(paramView, paramCharSequence);
  }
}


/* Location:              C:\Users\Student\Desktop\APK Decompiling\com.qualcomm.ftcdriverstation_38_apps.evozi.com\classes-dex2jar.jar!\androidx\appcompat\widget\TooltipCompat.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */