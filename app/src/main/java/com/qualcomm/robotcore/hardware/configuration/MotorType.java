package com.qualcomm.robotcore.hardware.configuration;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import org.firstinspires.ftc.robotcore.external.navigation.Rotation;

@Deprecated
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE})
public @interface MotorType {
  double achieveableMaxRPMFraction() default 0.85D;
  
  double gearing();
  
  double maxRPM();
  
  String name();
  
  Rotation orientation() default Rotation.CW;
  
  double ticksPerRev();
  
  String xmlTag();
}


/* Location:              C:\Users\Student\Desktop\APK Decompiling\com.qualcomm.ftcdriverstation_38_apps.evozi.com\classes-dex2jar.jar!\com\qualcomm\robotcore\hardware\configuration\MotorType.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */