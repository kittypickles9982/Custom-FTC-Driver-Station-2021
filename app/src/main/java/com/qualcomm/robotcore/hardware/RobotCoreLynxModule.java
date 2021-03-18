package com.qualcomm.robotcore.hardware;

import com.qualcomm.robotcore.util.SerialNumber;

public interface RobotCoreLynxModule extends HardwareDevice {
  String getFirmwareVersionString();
  
  int getModuleAddress();
  
  String getNullableFirmwareVersionString();
  
  SerialNumber getSerialNumber();
  
  boolean isParent();
}


/* Location:              C:\Users\Student\Desktop\APK Decompiling\com.qualcomm.ftcdriverstation_38_apps.evozi.com\classes-dex2jar.jar!\com\qualcomm\robotcore\hardware\RobotCoreLynxModule.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */