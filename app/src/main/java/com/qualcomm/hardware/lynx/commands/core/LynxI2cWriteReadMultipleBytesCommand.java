package com.qualcomm.hardware.lynx.commands.core;

import com.qualcomm.hardware.lynx.LynxModuleIntf;
import com.qualcomm.hardware.lynx.commands.LynxDatagram;
import com.qualcomm.hardware.lynx.commands.standard.LynxAck;
import com.qualcomm.robotcore.hardware.I2cAddr;
import com.qualcomm.robotcore.hardware.configuration.LynxConstants;
import java.nio.ByteBuffer;

public class LynxI2cWriteReadMultipleBytesCommand extends LynxDekaInterfaceCommand<LynxAck> {
  public static final int cbPayload = 4;
  
  public static final int cbPayloadFirst = 1;
  
  public static final int cbPayloadLast = 100;
  
  private byte cbToRead;
  
  private byte i2cAddr7Bit;
  
  private byte i2cBus;
  
  private byte i2cStartAddr;
  
  public LynxI2cWriteReadMultipleBytesCommand(LynxModuleIntf paramLynxModuleIntf) {
    super(paramLynxModuleIntf);
  }
  
  public LynxI2cWriteReadMultipleBytesCommand(LynxModuleIntf paramLynxModuleIntf, int paramInt1, I2cAddr paramI2cAddr, int paramInt2, int paramInt3) {
    this(paramLynxModuleIntf);
    LynxConstants.validateI2cBusZ(paramInt1);
    if (paramInt3 >= 1 && paramInt3 <= 100) {
      this.i2cBus = (byte)paramInt1;
      this.i2cAddr7Bit = (byte)paramI2cAddr.get7Bit();
      this.cbToRead = (byte)paramInt3;
      this.i2cStartAddr = (byte)paramInt2;
      return;
    } 
    throw new IllegalArgumentException(String.format("illegal payload length: %d", new Object[] { Integer.valueOf(paramInt3) }));
  }
  
  public void fromPayloadByteArray(byte[] paramArrayOfbyte) {
    ByteBuffer byteBuffer = ByteBuffer.wrap(paramArrayOfbyte).order(LynxDatagram.LYNX_ENDIAN);
    this.i2cBus = byteBuffer.get();
    this.i2cAddr7Bit = byteBuffer.get();
    this.cbToRead = byteBuffer.get();
    this.i2cStartAddr = byteBuffer.get();
  }
  
  public byte[] toPayloadByteArray() {
    ByteBuffer byteBuffer = ByteBuffer.allocate(4).order(LynxDatagram.LYNX_ENDIAN);
    byteBuffer.put(this.i2cBus);
    byteBuffer.put(this.i2cAddr7Bit);
    byteBuffer.put(this.cbToRead);
    byteBuffer.put(this.i2cStartAddr);
    return byteBuffer.array();
  }
}


/* Location:              C:\Users\Student\Desktop\APK Decompiling\com.qualcomm.ftcdriverstation_38_apps.evozi.com\classes-dex2jar.jar!\com\qualcomm\hardware\lynx\commands\core\LynxI2cWriteReadMultipleBytesCommand.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */