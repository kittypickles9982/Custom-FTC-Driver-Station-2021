package com.qualcomm.robotcore.hardware;

public interface NormalizedColorSensor extends HardwareDevice {
  float getGain();
  
  NormalizedRGBA getNormalizedColors();
  
  void setGain(float paramFloat);
}


/* Location:              C:\Users\Student\Desktop\APK Decompiling\com.qualcomm.ftcdriverstation_38_apps.evozi.com\classes-dex2jar.jar!\com\qualcomm\robotcore\hardware\NormalizedColorSensor.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */