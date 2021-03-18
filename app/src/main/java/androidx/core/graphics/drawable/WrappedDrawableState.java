package androidx.core.graphics.drawable;

import android.content.res.ColorStateList;
import android.content.res.Resources;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Build;

final class WrappedDrawableState extends Drawable.ConstantState {
  int mChangingConfigurations;
  
  Drawable.ConstantState mDrawableState;
  
  ColorStateList mTint = null;
  
  PorterDuff.Mode mTintMode = WrappedDrawableApi14.DEFAULT_TINT_MODE;
  
  WrappedDrawableState(WrappedDrawableState paramWrappedDrawableState) {
    if (paramWrappedDrawableState != null) {
      this.mChangingConfigurations = paramWrappedDrawableState.mChangingConfigurations;
      this.mDrawableState = paramWrappedDrawableState.mDrawableState;
      this.mTint = paramWrappedDrawableState.mTint;
      this.mTintMode = paramWrappedDrawableState.mTintMode;
    } 
  }
  
  boolean canConstantState() {
    return (this.mDrawableState != null);
  }
  
  public int getChangingConfigurations() {
    byte b;
    int i = this.mChangingConfigurations;
    Drawable.ConstantState constantState = this.mDrawableState;
    if (constantState != null) {
      b = constantState.getChangingConfigurations();
    } else {
      b = 0;
    } 
    return i | b;
  }
  
  public Drawable newDrawable() {
    return newDrawable(null);
  }
  
  public Drawable newDrawable(Resources paramResources) {
    return (Build.VERSION.SDK_INT >= 21) ? new WrappedDrawableApi21(this, paramResources) : new WrappedDrawableApi14(this, paramResources);
  }
}


/* Location:              C:\Users\Student\Desktop\APK Decompiling\com.qualcomm.ftcdriverstation_38_apps.evozi.com\classes-dex2jar.jar!\androidx\core\graphics\drawable\WrappedDrawableState.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */