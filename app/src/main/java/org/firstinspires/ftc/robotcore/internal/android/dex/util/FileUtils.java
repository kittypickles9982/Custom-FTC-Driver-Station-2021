package org.firstinspires.ftc.robotcore.internal.android.dex.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

public final class FileUtils {
  public static boolean hasArchiveSuffix(String paramString) {
    return (paramString.endsWith(".zip") || paramString.endsWith(".jar") || paramString.endsWith(".apk"));
  }
  
  public static byte[] readFile(File paramFile) {
    if (paramFile.exists()) {
      if (paramFile.isFile()) {
        if (paramFile.canRead()) {
          long l = paramFile.length();
          int i = (int)l;
          if (i == l) {
            byte[] arrayOfByte = new byte[i];
            try {
              StringBuilder stringBuilder4;
              FileInputStream fileInputStream = new FileInputStream(paramFile);
              int j = 0;
              while (i > 0) {
                int k = fileInputStream.read(arrayOfByte, j, i);
                if (k != -1) {
                  j += k;
                  i -= k;
                  continue;
                } 
                stringBuilder4 = new StringBuilder();
                stringBuilder4.append(paramFile);
                stringBuilder4.append(": unexpected EOF");
                throw new RuntimeException(stringBuilder4.toString());
              } 
              fileInputStream.close();
              return (byte[])stringBuilder4;
            } catch (IOException iOException) {
              StringBuilder stringBuilder4 = new StringBuilder();
              stringBuilder4.append(paramFile);
              stringBuilder4.append(": trouble reading");
              throw new RuntimeException(stringBuilder4.toString(), iOException);
            } 
          } 
          StringBuilder stringBuilder3 = new StringBuilder();
          stringBuilder3.append(paramFile);
          stringBuilder3.append(": file too long");
          throw new RuntimeException(stringBuilder3.toString());
        } 
        StringBuilder stringBuilder2 = new StringBuilder();
        stringBuilder2.append(paramFile);
        stringBuilder2.append(": file not readable");
        throw new RuntimeException(stringBuilder2.toString());
      } 
      StringBuilder stringBuilder1 = new StringBuilder();
      stringBuilder1.append(paramFile);
      stringBuilder1.append(": not a file");
      throw new RuntimeException(stringBuilder1.toString());
    } 
    StringBuilder stringBuilder = new StringBuilder();
    stringBuilder.append(paramFile);
    stringBuilder.append(": file not found");
    throw new RuntimeException(stringBuilder.toString());
  }
  
  public static byte[] readFile(String paramString) {
    return readFile(new File(paramString));
  }
}


/* Location:              C:\Users\Student\Desktop\APK Decompiling\com.qualcomm.ftcdriverstation_38_apps.evozi.com\classes-dex2jar.jar!\org\firstinspires\ftc\robotcore\internal\android\de\\util\FileUtils.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */