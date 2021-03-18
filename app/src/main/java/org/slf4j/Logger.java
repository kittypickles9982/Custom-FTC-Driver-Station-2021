package org.slf4j;

public interface Logger {
  public static final String ROOT_LOGGER_NAME = "ROOT";
  
  void debug(String paramString);
  
  void debug(String paramString, Object paramObject);
  
  void debug(String paramString, Object paramObject1, Object paramObject2);
  
  void debug(String paramString, Throwable paramThrowable);
  
  void debug(String paramString, Object... paramVarArgs);
  
  void debug(Marker paramMarker, String paramString);
  
  void debug(Marker paramMarker, String paramString, Object paramObject);
  
  void debug(Marker paramMarker, String paramString, Object paramObject1, Object paramObject2);
  
  void debug(Marker paramMarker, String paramString, Throwable paramThrowable);
  
  void debug(Marker paramMarker, String paramString, Object... paramVarArgs);
  
  void error(String paramString);
  
  void error(String paramString, Object paramObject);
  
  void error(String paramString, Object paramObject1, Object paramObject2);
  
  void error(String paramString, Throwable paramThrowable);
  
  void error(String paramString, Object... paramVarArgs);
  
  void error(Marker paramMarker, String paramString);
  
  void error(Marker paramMarker, String paramString, Object paramObject);
  
  void error(Marker paramMarker, String paramString, Object paramObject1, Object paramObject2);
  
  void error(Marker paramMarker, String paramString, Throwable paramThrowable);
  
  void error(Marker paramMarker, String paramString, Object... paramVarArgs);
  
  String getName();
  
  void info(String paramString);
  
  void info(String paramString, Object paramObject);
  
  void info(String paramString, Object paramObject1, Object paramObject2);
  
  void info(String paramString, Throwable paramThrowable);
  
  void info(String paramString, Object... paramVarArgs);
  
  void info(Marker paramMarker, String paramString);
  
  void info(Marker paramMarker, String paramString, Object paramObject);
  
  void info(Marker paramMarker, String paramString, Object paramObject1, Object paramObject2);
  
  void info(Marker paramMarker, String paramString, Throwable paramThrowable);
  
  void info(Marker paramMarker, String paramString, Object... paramVarArgs);
  
  boolean isDebugEnabled();
  
  boolean isDebugEnabled(Marker paramMarker);
  
  boolean isErrorEnabled();
  
  boolean isErrorEnabled(Marker paramMarker);
  
  boolean isInfoEnabled();
  
  boolean isInfoEnabled(Marker paramMarker);
  
  boolean isTraceEnabled();
  
  boolean isTraceEnabled(Marker paramMarker);
  
  boolean isWarnEnabled();
  
  boolean isWarnEnabled(Marker paramMarker);
  
  void trace(String paramString);
  
  void trace(String paramString, Object paramObject);
  
  void trace(String paramString, Object paramObject1, Object paramObject2);
  
  void trace(String paramString, Throwable paramThrowable);
  
  void trace(String paramString, Object... paramVarArgs);
  
  void trace(Marker paramMarker, String paramString);
  
  void trace(Marker paramMarker, String paramString, Object paramObject);
  
  void trace(Marker paramMarker, String paramString, Object paramObject1, Object paramObject2);
  
  void trace(Marker paramMarker, String paramString, Throwable paramThrowable);
  
  void trace(Marker paramMarker, String paramString, Object... paramVarArgs);
  
  void warn(String paramString);
  
  void warn(String paramString, Object paramObject);
  
  void warn(String paramString, Object paramObject1, Object paramObject2);
  
  void warn(String paramString, Throwable paramThrowable);
  
  void warn(String paramString, Object... paramVarArgs);
  
  void warn(Marker paramMarker, String paramString);
  
  void warn(Marker paramMarker, String paramString, Object paramObject);
  
  void warn(Marker paramMarker, String paramString, Object paramObject1, Object paramObject2);
  
  void warn(Marker paramMarker, String paramString, Throwable paramThrowable);
  
  void warn(Marker paramMarker, String paramString, Object... paramVarArgs);
}


/* Location:              C:\Users\Student\Desktop\APK Decompiling\com.qualcomm.ftcdriverstation_38_apps.evozi.com\classes-dex2jar.jar!\org\slf4j\Logger.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */