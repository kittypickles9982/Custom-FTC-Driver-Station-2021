package com.qualcomm.robotcore.hardware;

public interface LightSensor extends HardwareDevice {
  void enableLed(boolean paramBoolean);
  
  double getLightDetected();
  
  double getRawLightDetected();
  
  double getRawLightDetectedMax();
  
  String status();
}


/* Location:              C:\Users\Student\Desktop\APK Decompiling\com.qualcomm.ftcdriverstation_38_apps.evozi.com\classes-dex2jar.jar!\com\qualcomm\robotcore\hardware\LightSensor.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */