package com.vuforia;

public class CameraCalibration {
  protected boolean swigCMemOwn;
  
  private long swigCPtr;
  
  protected CameraCalibration(long paramLong, boolean paramBoolean) {
    this.swigCMemOwn = paramBoolean;
    this.swigCPtr = paramLong;
  }
  
  protected static long getCPtr(CameraCalibration paramCameraCalibration) {
    return (paramCameraCalibration == null) ? 0L : paramCameraCalibration.swigCPtr;
  }
  
  protected void delete() {
    // Byte code:
    //   0: aload_0
    //   1: monitorenter
    //   2: aload_0
    //   3: getfield swigCPtr : J
    //   6: lconst_0
    //   7: lcmp
    //   8: ifeq -> 35
    //   11: aload_0
    //   12: getfield swigCMemOwn : Z
    //   15: ifeq -> 30
    //   18: aload_0
    //   19: iconst_0
    //   20: putfield swigCMemOwn : Z
    //   23: aload_0
    //   24: getfield swigCPtr : J
    //   27: invokestatic delete_CameraCalibration : (J)V
    //   30: aload_0
    //   31: lconst_0
    //   32: putfield swigCPtr : J
    //   35: aload_0
    //   36: monitorexit
    //   37: return
    //   38: astore_1
    //   39: aload_0
    //   40: monitorexit
    //   41: aload_1
    //   42: athrow
    // Exception table:
    //   from	to	target	type
    //   2	30	38	finally
    //   30	35	38	finally
  }
  
  public boolean equals(Object paramObject) {
    boolean bool = paramObject instanceof CameraCalibration;
    boolean bool2 = false;
    boolean bool1 = bool2;
    if (bool) {
      bool1 = bool2;
      if (((CameraCalibration)paramObject).swigCPtr == this.swigCPtr)
        bool1 = true; 
    } 
    return bool1;
  }
  
  protected void finalize() {
    delete();
  }
  
  public Vec4F getDistortionParameters() {
    return new Vec4F(VuforiaJNI.CameraCalibration_getDistortionParameters(this.swigCPtr, this), true);
  }
  
  public Vec2F getFieldOfViewRads() {
    return new Vec2F(VuforiaJNI.CameraCalibration_getFieldOfViewRads(this.swigCPtr, this), true);
  }
  
  public Vec2F getFocalLength() {
    return new Vec2F(VuforiaJNI.CameraCalibration_getFocalLength(this.swigCPtr, this), true);
  }
  
  public Vec2F getPrincipalPoint() {
    return new Vec2F(VuforiaJNI.CameraCalibration_getPrincipalPoint(this.swigCPtr, this), true);
  }
  
  public Vec2F getSize() {
    return new Vec2F(VuforiaJNI.CameraCalibration_getSize(this.swigCPtr, this), true);
  }
}


/* Location:              C:\Users\Student\Desktop\APK Decompiling\com.qualcomm.ftcdriverstation_38_apps.evozi.com\classes-dex2jar.jar!\com\vuforia\CameraCalibration.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */