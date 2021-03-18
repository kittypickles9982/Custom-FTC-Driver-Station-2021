package androidx.core.app;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Parcelable;
import android.text.Html;
import android.text.Spanned;
import android.util.Log;
import android.view.ActionProvider;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ShareActionProvider;
import androidx.core.util.Preconditions;
import java.util.ArrayList;

public final class ShareCompat {
  public static final String EXTRA_CALLING_ACTIVITY = "androidx.core.app.EXTRA_CALLING_ACTIVITY";
  
  public static final String EXTRA_CALLING_ACTIVITY_INTEROP = "android.support.v4.app.EXTRA_CALLING_ACTIVITY";
  
  public static final String EXTRA_CALLING_PACKAGE = "androidx.core.app.EXTRA_CALLING_PACKAGE";
  
  public static final String EXTRA_CALLING_PACKAGE_INTEROP = "android.support.v4.app.EXTRA_CALLING_PACKAGE";
  
  private static final String HISTORY_FILENAME_PREFIX = ".sharecompat_";
  
  public static void configureMenuItem(Menu paramMenu, int paramInt, IntentBuilder paramIntentBuilder) {
    MenuItem menuItem = paramMenu.findItem(paramInt);
    if (menuItem != null) {
      configureMenuItem(menuItem, paramIntentBuilder);
      return;
    } 
    StringBuilder stringBuilder = new StringBuilder();
    stringBuilder.append("Could not find menu item with id ");
    stringBuilder.append(paramInt);
    stringBuilder.append(" in the supplied menu");
    throw new IllegalArgumentException(stringBuilder.toString());
  }
  
  public static void configureMenuItem(MenuItem paramMenuItem, IntentBuilder paramIntentBuilder) {
    ShareActionProvider shareActionProvider;
    ActionProvider actionProvider = paramMenuItem.getActionProvider();
    if (!(actionProvider instanceof ShareActionProvider)) {
      shareActionProvider = new ShareActionProvider(paramIntentBuilder.getContext());
    } else {
      shareActionProvider = shareActionProvider;
    } 
    StringBuilder stringBuilder = new StringBuilder();
    stringBuilder.append(".sharecompat_");
    stringBuilder.append(paramIntentBuilder.getContext().getClass().getName());
    shareActionProvider.setShareHistoryFileName(stringBuilder.toString());
    shareActionProvider.setShareIntent(paramIntentBuilder.getIntent());
    paramMenuItem.setActionProvider((ActionProvider)shareActionProvider);
    if (Build.VERSION.SDK_INT < 16 && !paramMenuItem.hasSubMenu())
      paramMenuItem.setIntent(paramIntentBuilder.createChooserIntent()); 
  }
  
  public static ComponentName getCallingActivity(Activity paramActivity) {
    Intent intent = paramActivity.getIntent();
    ComponentName componentName2 = paramActivity.getCallingActivity();
    ComponentName componentName1 = componentName2;
    if (componentName2 == null)
      componentName1 = getCallingActivity(intent); 
    return componentName1;
  }
  
  static ComponentName getCallingActivity(Intent paramIntent) {
    ComponentName componentName2 = (ComponentName)paramIntent.getParcelableExtra("androidx.core.app.EXTRA_CALLING_ACTIVITY");
    ComponentName componentName1 = componentName2;
    if (componentName2 == null)
      componentName1 = (ComponentName)paramIntent.getParcelableExtra("android.support.v4.app.EXTRA_CALLING_ACTIVITY"); 
    return componentName1;
  }
  
  public static String getCallingPackage(Activity paramActivity) {
    Intent intent = paramActivity.getIntent();
    String str2 = paramActivity.getCallingPackage();
    String str1 = str2;
    if (str2 == null) {
      str1 = str2;
      if (intent != null)
        str1 = getCallingPackage(intent); 
    } 
    return str1;
  }
  
  static String getCallingPackage(Intent paramIntent) {
    String str2 = paramIntent.getStringExtra("androidx.core.app.EXTRA_CALLING_PACKAGE");
    String str1 = str2;
    if (str2 == null)
      str1 = paramIntent.getStringExtra("android.support.v4.app.EXTRA_CALLING_PACKAGE"); 
    return str1;
  }
  
  public static class IntentBuilder {
    private ArrayList<String> mBccAddresses;
    
    private ArrayList<String> mCcAddresses;
    
    private CharSequence mChooserTitle;
    
    private final Context mContext;
    
    private final Intent mIntent;
    
    private ArrayList<Uri> mStreams;
    
    private ArrayList<String> mToAddresses;
    
    private IntentBuilder(Context param1Context, ComponentName param1ComponentName) {
      this.mContext = (Context)Preconditions.checkNotNull(param1Context);
      Intent intent = (new Intent()).setAction("android.intent.action.SEND");
      this.mIntent = intent;
      intent.putExtra("androidx.core.app.EXTRA_CALLING_PACKAGE", param1Context.getPackageName());
      this.mIntent.putExtra("android.support.v4.app.EXTRA_CALLING_PACKAGE", param1Context.getPackageName());
      this.mIntent.putExtra("androidx.core.app.EXTRA_CALLING_ACTIVITY", (Parcelable)param1ComponentName);
      this.mIntent.putExtra("android.support.v4.app.EXTRA_CALLING_ACTIVITY", (Parcelable)param1ComponentName);
      this.mIntent.addFlags(524288);
    }
    
    private void combineArrayExtra(String param1String, ArrayList<String> param1ArrayList) {
      byte b;
      String[] arrayOfString1 = this.mIntent.getStringArrayExtra(param1String);
      if (arrayOfString1 != null) {
        b = arrayOfString1.length;
      } else {
        b = 0;
      } 
      String[] arrayOfString2 = new String[param1ArrayList.size() + b];
      param1ArrayList.toArray(arrayOfString2);
      if (arrayOfString1 != null)
        System.arraycopy(arrayOfString1, 0, arrayOfString2, param1ArrayList.size(), b); 
      this.mIntent.putExtra(param1String, arrayOfString2);
    }
    
    private void combineArrayExtra(String param1String, String[] param1ArrayOfString) {
      byte b;
      Intent intent = getIntent();
      String[] arrayOfString1 = intent.getStringArrayExtra(param1String);
      if (arrayOfString1 != null) {
        b = arrayOfString1.length;
      } else {
        b = 0;
      } 
      String[] arrayOfString2 = new String[param1ArrayOfString.length + b];
      if (arrayOfString1 != null)
        System.arraycopy(arrayOfString1, 0, arrayOfString2, 0, b); 
      System.arraycopy(param1ArrayOfString, 0, arrayOfString2, b, param1ArrayOfString.length);
      intent.putExtra(param1String, arrayOfString2);
    }
    
    public static IntentBuilder from(Activity param1Activity) {
      return from((Context)Preconditions.checkNotNull(param1Activity), param1Activity.getComponentName());
    }
    
    private static IntentBuilder from(Context param1Context, ComponentName param1ComponentName) {
      return new IntentBuilder(param1Context, param1ComponentName);
    }
    
    public IntentBuilder addEmailBcc(String param1String) {
      if (this.mBccAddresses == null)
        this.mBccAddresses = new ArrayList<String>(); 
      this.mBccAddresses.add(param1String);
      return this;
    }
    
    public IntentBuilder addEmailBcc(String[] param1ArrayOfString) {
      combineArrayExtra("android.intent.extra.BCC", param1ArrayOfString);
      return this;
    }
    
    public IntentBuilder addEmailCc(String param1String) {
      if (this.mCcAddresses == null)
        this.mCcAddresses = new ArrayList<String>(); 
      this.mCcAddresses.add(param1String);
      return this;
    }
    
    public IntentBuilder addEmailCc(String[] param1ArrayOfString) {
      combineArrayExtra("android.intent.extra.CC", param1ArrayOfString);
      return this;
    }
    
    public IntentBuilder addEmailTo(String param1String) {
      if (this.mToAddresses == null)
        this.mToAddresses = new ArrayList<String>(); 
      this.mToAddresses.add(param1String);
      return this;
    }
    
    public IntentBuilder addEmailTo(String[] param1ArrayOfString) {
      combineArrayExtra("android.intent.extra.EMAIL", param1ArrayOfString);
      return this;
    }
    
    public IntentBuilder addStream(Uri param1Uri) {
      Uri uri = (Uri)this.mIntent.getParcelableExtra("android.intent.extra.STREAM");
      if (this.mStreams == null && uri == null)
        return setStream(param1Uri); 
      if (this.mStreams == null)
        this.mStreams = new ArrayList<Uri>(); 
      if (uri != null) {
        this.mIntent.removeExtra("android.intent.extra.STREAM");
        this.mStreams.add(uri);
      } 
      this.mStreams.add(param1Uri);
      return this;
    }
    
    public Intent createChooserIntent() {
      return Intent.createChooser(getIntent(), this.mChooserTitle);
    }
    
    Context getContext() {
      return this.mContext;
    }
    
    public Intent getIntent() {
      ArrayList<String> arrayList1 = this.mToAddresses;
      if (arrayList1 != null) {
        combineArrayExtra("android.intent.extra.EMAIL", arrayList1);
        this.mToAddresses = null;
      } 
      arrayList1 = this.mCcAddresses;
      if (arrayList1 != null) {
        combineArrayExtra("android.intent.extra.CC", arrayList1);
        this.mCcAddresses = null;
      } 
      arrayList1 = this.mBccAddresses;
      if (arrayList1 != null) {
        combineArrayExtra("android.intent.extra.BCC", arrayList1);
        this.mBccAddresses = null;
      } 
      ArrayList<Uri> arrayList = this.mStreams;
      boolean bool = true;
      if (arrayList == null || arrayList.size() <= 1)
        bool = false; 
      boolean bool1 = "android.intent.action.SEND_MULTIPLE".equals(this.mIntent.getAction());
      if (!bool && bool1) {
        this.mIntent.setAction("android.intent.action.SEND");
        arrayList = this.mStreams;
        if (arrayList != null && !arrayList.isEmpty()) {
          this.mIntent.putExtra("android.intent.extra.STREAM", (Parcelable)this.mStreams.get(0));
        } else {
          this.mIntent.removeExtra("android.intent.extra.STREAM");
        } 
        this.mStreams = null;
      } 
      if (bool && !bool1) {
        this.mIntent.setAction("android.intent.action.SEND_MULTIPLE");
        arrayList = this.mStreams;
        if (arrayList != null && !arrayList.isEmpty()) {
          this.mIntent.putParcelableArrayListExtra("android.intent.extra.STREAM", this.mStreams);
        } else {
          this.mIntent.removeExtra("android.intent.extra.STREAM");
        } 
      } 
      return this.mIntent;
    }
    
    public IntentBuilder setChooserTitle(int param1Int) {
      return setChooserTitle(this.mContext.getText(param1Int));
    }
    
    public IntentBuilder setChooserTitle(CharSequence param1CharSequence) {
      this.mChooserTitle = param1CharSequence;
      return this;
    }
    
    public IntentBuilder setEmailBcc(String[] param1ArrayOfString) {
      this.mIntent.putExtra("android.intent.extra.BCC", param1ArrayOfString);
      return this;
    }
    
    public IntentBuilder setEmailCc(String[] param1ArrayOfString) {
      this.mIntent.putExtra("android.intent.extra.CC", param1ArrayOfString);
      return this;
    }
    
    public IntentBuilder setEmailTo(String[] param1ArrayOfString) {
      if (this.mToAddresses != null)
        this.mToAddresses = null; 
      this.mIntent.putExtra("android.intent.extra.EMAIL", param1ArrayOfString);
      return this;
    }
    
    public IntentBuilder setHtmlText(String param1String) {
      this.mIntent.putExtra("android.intent.extra.HTML_TEXT", param1String);
      if (!this.mIntent.hasExtra("android.intent.extra.TEXT"))
        setText((CharSequence)Html.fromHtml(param1String)); 
      return this;
    }
    
    public IntentBuilder setStream(Uri param1Uri) {
      if (!"android.intent.action.SEND".equals(this.mIntent.getAction()))
        this.mIntent.setAction("android.intent.action.SEND"); 
      this.mStreams = null;
      this.mIntent.putExtra("android.intent.extra.STREAM", (Parcelable)param1Uri);
      return this;
    }
    
    public IntentBuilder setSubject(String param1String) {
      this.mIntent.putExtra("android.intent.extra.SUBJECT", param1String);
      return this;
    }
    
    public IntentBuilder setText(CharSequence param1CharSequence) {
      this.mIntent.putExtra("android.intent.extra.TEXT", param1CharSequence);
      return this;
    }
    
    public IntentBuilder setType(String param1String) {
      this.mIntent.setType(param1String);
      return this;
    }
    
    public void startChooser() {
      this.mContext.startActivity(createChooserIntent());
    }
  }
  
  public static class IntentReader {
    private static final String TAG = "IntentReader";
    
    private final ComponentName mCallingActivity;
    
    private final String mCallingPackage;
    
    private final Context mContext;
    
    private final Intent mIntent;
    
    private ArrayList<Uri> mStreams;
    
    private IntentReader(Context param1Context, Intent param1Intent) {
      this.mContext = (Context)Preconditions.checkNotNull(param1Context);
      this.mIntent = (Intent)Preconditions.checkNotNull(param1Intent);
      this.mCallingPackage = ShareCompat.getCallingPackage(param1Intent);
      this.mCallingActivity = ShareCompat.getCallingActivity(param1Intent);
    }
    
    public static IntentReader from(Activity param1Activity) {
      return from((Context)Preconditions.checkNotNull(param1Activity), param1Activity.getIntent());
    }
    
    private static IntentReader from(Context param1Context, Intent param1Intent) {
      return new IntentReader(param1Context, param1Intent);
    }
    
    private static void withinStyle(StringBuilder param1StringBuilder, CharSequence param1CharSequence, int param1Int1, int param1Int2) {
      while (param1Int1 < param1Int2) {
        char c = param1CharSequence.charAt(param1Int1);
        if (c == '<') {
          param1StringBuilder.append("&lt;");
        } else if (c == '>') {
          param1StringBuilder.append("&gt;");
        } else if (c == '&') {
          param1StringBuilder.append("&amp;");
        } else if (c > '~' || c < ' ') {
          param1StringBuilder.append("&#");
          param1StringBuilder.append(c);
          param1StringBuilder.append(";");
        } else if (c == ' ') {
          while (true) {
            int i = param1Int1 + 1;
            if (i < param1Int2 && param1CharSequence.charAt(i) == ' ') {
              param1StringBuilder.append("&nbsp;");
              param1Int1 = i;
              continue;
            } 
            break;
          } 
          param1StringBuilder.append(' ');
        } else {
          param1StringBuilder.append(c);
        } 
        param1Int1++;
      } 
    }
    
    public ComponentName getCallingActivity() {
      return this.mCallingActivity;
    }
    
    public Drawable getCallingActivityIcon() {
      if (this.mCallingActivity == null)
        return null; 
      PackageManager packageManager = this.mContext.getPackageManager();
      try {
        return packageManager.getActivityIcon(this.mCallingActivity);
      } catch (android.content.pm.PackageManager.NameNotFoundException nameNotFoundException) {
        Log.e("IntentReader", "Could not retrieve icon for calling activity", (Throwable)nameNotFoundException);
        return null;
      } 
    }
    
    public Drawable getCallingApplicationIcon() {
      if (this.mCallingPackage == null)
        return null; 
      PackageManager packageManager = this.mContext.getPackageManager();
      try {
        return packageManager.getApplicationIcon(this.mCallingPackage);
      } catch (android.content.pm.PackageManager.NameNotFoundException nameNotFoundException) {
        Log.e("IntentReader", "Could not retrieve icon for calling application", (Throwable)nameNotFoundException);
        return null;
      } 
    }
    
    public CharSequence getCallingApplicationLabel() {
      if (this.mCallingPackage == null)
        return null; 
      PackageManager packageManager = this.mContext.getPackageManager();
      try {
        return packageManager.getApplicationLabel(packageManager.getApplicationInfo(this.mCallingPackage, 0));
      } catch (android.content.pm.PackageManager.NameNotFoundException nameNotFoundException) {
        Log.e("IntentReader", "Could not retrieve label for calling application", (Throwable)nameNotFoundException);
        return null;
      } 
    }
    
    public String getCallingPackage() {
      return this.mCallingPackage;
    }
    
    public String[] getEmailBcc() {
      return this.mIntent.getStringArrayExtra("android.intent.extra.BCC");
    }
    
    public String[] getEmailCc() {
      return this.mIntent.getStringArrayExtra("android.intent.extra.CC");
    }
    
    public String[] getEmailTo() {
      return this.mIntent.getStringArrayExtra("android.intent.extra.EMAIL");
    }
    
    public String getHtmlText() {
      String str2 = this.mIntent.getStringExtra("android.intent.extra.HTML_TEXT");
      String str1 = str2;
      if (str2 == null) {
        CharSequence charSequence = getText();
        if (charSequence instanceof Spanned)
          return Html.toHtml((Spanned)charSequence); 
        str1 = str2;
        if (charSequence != null) {
          if (Build.VERSION.SDK_INT >= 16)
            return Html.escapeHtml(charSequence); 
          StringBuilder stringBuilder = new StringBuilder();
          withinStyle(stringBuilder, charSequence, 0, charSequence.length());
          str1 = stringBuilder.toString();
        } 
      } 
      return str1;
    }
    
    public Uri getStream() {
      return (Uri)this.mIntent.getParcelableExtra("android.intent.extra.STREAM");
    }
    
    public Uri getStream(int param1Int) {
      if (this.mStreams == null && isMultipleShare())
        this.mStreams = this.mIntent.getParcelableArrayListExtra("android.intent.extra.STREAM"); 
      ArrayList<Uri> arrayList = this.mStreams;
      if (arrayList != null)
        return arrayList.get(param1Int); 
      if (param1Int == 0)
        return (Uri)this.mIntent.getParcelableExtra("android.intent.extra.STREAM"); 
      StringBuilder stringBuilder = new StringBuilder();
      stringBuilder.append("Stream items available: ");
      stringBuilder.append(getStreamCount());
      stringBuilder.append(" index requested: ");
      stringBuilder.append(param1Int);
      throw new IndexOutOfBoundsException(stringBuilder.toString());
    }
    
    public int getStreamCount() {
      throw new RuntimeException("d2j fail translate: java.lang.RuntimeException: can not merge I and Z\r\n\tat com.googlecode.dex2jar.ir.TypeClass.merge(TypeClass.java:100)\r\n\tat com.googlecode.dex2jar.ir.ts.TypeTransformer$TypeRef.updateTypeClass(TypeTransformer.java:174)\r\n\tat com.googlecode.dex2jar.ir.ts.TypeTransformer$TypeAnalyze.provideAs(TypeTransformer.java:780)\r\n\tat com.googlecode.dex2jar.ir.ts.TypeTransformer$TypeAnalyze.enexpr(TypeTransformer.java:659)\r\n\tat com.googlecode.dex2jar.ir.ts.TypeTransformer$TypeAnalyze.exExpr(TypeTransformer.java:719)\r\n\tat com.googlecode.dex2jar.ir.ts.TypeTransformer$TypeAnalyze.exExpr(TypeTransformer.java:703)\r\n\tat com.googlecode.dex2jar.ir.ts.TypeTransformer$TypeAnalyze.s1stmt(TypeTransformer.java:810)\r\n\tat com.googlecode.dex2jar.ir.ts.TypeTransformer$TypeAnalyze.sxStmt(TypeTransformer.java:840)\r\n\tat com.googlecode.dex2jar.ir.ts.TypeTransformer$TypeAnalyze.analyze(TypeTransformer.java:206)\r\n\tat com.googlecode.dex2jar.ir.ts.TypeTransformer.transform(TypeTransformer.java:44)\r\n\tat com.googlecode.d2j.dex.Dex2jar$2.optimize(Dex2jar.java:162)\r\n\tat com.googlecode.d2j.dex.Dex2Asm.convertCode(Dex2Asm.java:414)\r\n\tat com.googlecode.d2j.dex.ExDex2Asm.convertCode(ExDex2Asm.java:42)\r\n\tat com.googlecode.d2j.dex.Dex2jar$2.convertCode(Dex2jar.java:128)\r\n\tat com.googlecode.d2j.dex.Dex2Asm.convertMethod(Dex2Asm.java:509)\r\n\tat com.googlecode.d2j.dex.Dex2Asm.convertClass(Dex2Asm.java:406)\r\n\tat com.googlecode.d2j.dex.Dex2Asm.convertDex(Dex2Asm.java:422)\r\n\tat com.googlecode.d2j.dex.Dex2jar.doTranslate(Dex2jar.java:172)\r\n\tat com.googlecode.d2j.dex.Dex2jar.to(Dex2jar.java:272)\r\n\tat com.googlecode.dex2jar.tools.Dex2jarCmd.doCommandLine(Dex2jarCmd.java:108)\r\n\tat com.googlecode.dex2jar.tools.BaseCmd.doMain(BaseCmd.java:288)\r\n\tat com.googlecode.dex2jar.tools.Dex2jarCmd.main(Dex2jarCmd.java:32)\r\n");
    }
    
    public String getSubject() {
      return this.mIntent.getStringExtra("android.intent.extra.SUBJECT");
    }
    
    public CharSequence getText() {
      return this.mIntent.getCharSequenceExtra("android.intent.extra.TEXT");
    }
    
    public String getType() {
      return this.mIntent.getType();
    }
    
    public boolean isMultipleShare() {
      return "android.intent.action.SEND_MULTIPLE".equals(this.mIntent.getAction());
    }
    
    public boolean isShareIntent() {
      String str = this.mIntent.getAction();
      return ("android.intent.action.SEND".equals(str) || "android.intent.action.SEND_MULTIPLE".equals(str));
    }
    
    public boolean isSingleShare() {
      return "android.intent.action.SEND".equals(this.mIntent.getAction());
    }
  }
}


/* Location:              C:\Users\Student\Desktop\APK Decompiling\com.qualcomm.ftcdriverstation_38_apps.evozi.com\classes-dex2jar.jar!\androidx\core\app\ShareCompat.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */