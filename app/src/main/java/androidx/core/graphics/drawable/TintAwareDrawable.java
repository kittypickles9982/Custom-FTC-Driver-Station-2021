package androidx.core.graphics.drawable;

import android.content.res.ColorStateList;
import android.graphics.PorterDuff;

public interface TintAwareDrawable {
  void setTint(int paramInt);
  
  void setTintList(ColorStateList paramColorStateList);
  
  void setTintMode(PorterDuff.Mode paramMode);
}


/* Location:              C:\Users\Student\Desktop\APK Decompiling\com.qualcomm.ftcdriverstation_38_apps.evozi.com\classes-dex2jar.jar!\androidx\core\graphics\drawable\TintAwareDrawable.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */