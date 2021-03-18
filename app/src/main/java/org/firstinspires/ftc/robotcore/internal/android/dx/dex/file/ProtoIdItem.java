package org.firstinspires.ftc.robotcore.internal.android.dx.dex.file;

import org.firstinspires.ftc.robotcore.internal.android.dx.rop.cst.CstString;
import org.firstinspires.ftc.robotcore.internal.android.dx.rop.type.Prototype;
import org.firstinspires.ftc.robotcore.internal.android.dx.rop.type.StdTypeList;
import org.firstinspires.ftc.robotcore.internal.android.dx.rop.type.Type;
import org.firstinspires.ftc.robotcore.internal.android.dx.rop.type.TypeList;
import org.firstinspires.ftc.robotcore.internal.android.dx.util.AnnotatedOutput;
import org.firstinspires.ftc.robotcore.internal.android.dx.util.Hex;

public final class ProtoIdItem extends IndexedItem {
  private TypeListItem parameterTypes;
  
  private final Prototype prototype;
  
  private final CstString shortForm;
  
  public ProtoIdItem(Prototype paramPrototype) {
    if (paramPrototype != null) {
      TypeListItem typeListItem;
      this.prototype = paramPrototype;
      this.shortForm = makeShortForm(paramPrototype);
      StdTypeList stdTypeList = paramPrototype.getParameterTypes();
      if (stdTypeList.size() == 0) {
        stdTypeList = null;
      } else {
        typeListItem = new TypeListItem((TypeList)stdTypeList);
      } 
      this.parameterTypes = typeListItem;
      return;
    } 
    throw new NullPointerException("prototype == null");
  }
  
  private static CstString makeShortForm(Prototype paramPrototype) {
    StdTypeList stdTypeList = paramPrototype.getParameterTypes();
    int j = stdTypeList.size();
    StringBuilder stringBuilder = new StringBuilder(j + 1);
    stringBuilder.append(shortFormCharFor(paramPrototype.getReturnType()));
    for (int i = 0; i < j; i++)
      stringBuilder.append(shortFormCharFor(stdTypeList.getType(i))); 
    return new CstString(stringBuilder.toString());
  }
  
  private static char shortFormCharFor(Type paramType) {
    char c1 = paramType.getDescriptor().charAt(0);
    char c = c1;
    if (c1 == '[')
      c = 'L'; 
    return c;
  }
  
  public void addContents(DexFile paramDexFile) {
    StringIdsSection stringIdsSection = paramDexFile.getStringIds();
    TypeIdsSection typeIdsSection = paramDexFile.getTypeIds();
    MixedItemSection mixedItemSection = paramDexFile.getTypeLists();
    typeIdsSection.intern(this.prototype.getReturnType());
    stringIdsSection.intern(this.shortForm);
    TypeListItem typeListItem = this.parameterTypes;
    if (typeListItem != null)
      this.parameterTypes = mixedItemSection.<TypeListItem>intern(typeListItem); 
  }
  
  public ItemType itemType() {
    return ItemType.TYPE_PROTO_ID_ITEM;
  }
  
  public int writeSize() {
    return 12;
  }
  
  public void writeTo(DexFile paramDexFile, AnnotatedOutput paramAnnotatedOutput) {
    int i = paramDexFile.getStringIds().indexOf(this.shortForm);
    int j = paramDexFile.getTypeIds().indexOf(this.prototype.getReturnType());
    int k = OffsettedItem.getAbsoluteOffsetOr0(this.parameterTypes);
    if (paramAnnotatedOutput.annotates()) {
      StringBuilder stringBuilder1 = new StringBuilder();
      stringBuilder1.append(this.prototype.getReturnType().toHuman());
      stringBuilder1.append(" proto(");
      StdTypeList stdTypeList = this.prototype.getParameterTypes();
      int n = stdTypeList.size();
      for (int m = 0; m < n; m++) {
        if (m != 0)
          stringBuilder1.append(", "); 
        stringBuilder1.append(stdTypeList.getType(m).toHuman());
      } 
      stringBuilder1.append(")");
      StringBuilder stringBuilder2 = new StringBuilder();
      stringBuilder2.append(indexString());
      stringBuilder2.append(' ');
      stringBuilder2.append(stringBuilder1.toString());
      paramAnnotatedOutput.annotate(0, stringBuilder2.toString());
      stringBuilder1 = new StringBuilder();
      stringBuilder1.append("  shorty_idx:      ");
      stringBuilder1.append(Hex.u4(i));
      stringBuilder1.append(" // ");
      stringBuilder1.append(this.shortForm.toQuoted());
      paramAnnotatedOutput.annotate(4, stringBuilder1.toString());
      stringBuilder1 = new StringBuilder();
      stringBuilder1.append("  return_type_idx: ");
      stringBuilder1.append(Hex.u4(j));
      stringBuilder1.append(" // ");
      stringBuilder1.append(this.prototype.getReturnType().toHuman());
      paramAnnotatedOutput.annotate(4, stringBuilder1.toString());
      stringBuilder1 = new StringBuilder();
      stringBuilder1.append("  parameters_off:  ");
      stringBuilder1.append(Hex.u4(k));
      paramAnnotatedOutput.annotate(4, stringBuilder1.toString());
    } 
    paramAnnotatedOutput.writeInt(i);
    paramAnnotatedOutput.writeInt(j);
    paramAnnotatedOutput.writeInt(k);
  }
}


/* Location:              C:\Users\Student\Desktop\APK Decompiling\com.qualcomm.ftcdriverstation_38_apps.evozi.com\classes-dex2jar.jar!\org\firstinspires\ftc\robotcore\internal\android\dx\dex\file\ProtoIdItem.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */