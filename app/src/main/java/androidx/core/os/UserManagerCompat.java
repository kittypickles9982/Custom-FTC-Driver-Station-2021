package androidx.core.os;

import android.content.Context;
import android.os.Build;
import android.os.UserManager;

public class UserManagerCompat {
  public static boolean isUserUnlocked(Context paramContext) {
    return (Build.VERSION.SDK_INT >= 24) ? ((UserManager)paramContext.getSystemService(UserManager.class)).isUserUnlocked() : true;
  }
}


/* Location:              C:\Users\Student\Desktop\APK Decompiling\com.qualcomm.ftcdriverstation_38_apps.evozi.com\classes-dex2jar.jar!\androidx\core\os\UserManagerCompat.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */