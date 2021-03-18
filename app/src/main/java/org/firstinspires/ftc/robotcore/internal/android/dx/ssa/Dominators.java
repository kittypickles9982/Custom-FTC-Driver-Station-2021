package org.firstinspires.ftc.robotcore.internal.android.dx.ssa;

import java.util.ArrayList;
import java.util.BitSet;
import java.util.HashSet;

public final class Dominators {
  private final ArrayList<SsaBasicBlock> blocks;
  
  private final DomFront.DomInfo[] domInfos;
  
  private final DFSInfo[] info;
  
  private final SsaMethod meth;
  
  private final boolean postdom;
  
  private final ArrayList<SsaBasicBlock> vertex;
  
  private Dominators(SsaMethod paramSsaMethod, DomFront.DomInfo[] paramArrayOfDomInfo, boolean paramBoolean) {
    this.meth = paramSsaMethod;
    this.domInfos = paramArrayOfDomInfo;
    this.postdom = paramBoolean;
    ArrayList<SsaBasicBlock> arrayList = paramSsaMethod.getBlocks();
    this.blocks = arrayList;
    this.info = new DFSInfo[arrayList.size() + 2];
    this.vertex = new ArrayList<SsaBasicBlock>();
  }
  
  private void compress(SsaBasicBlock paramSsaBasicBlock) {
    DFSInfo dFSInfo = this.info[paramSsaBasicBlock.getIndex()];
    if ((this.info[dFSInfo.ancestor.getIndex()]).ancestor != null) {
      ArrayList<SsaBasicBlock> arrayList = new ArrayList();
      HashSet<SsaBasicBlock> hashSet = new HashSet();
      arrayList.add(paramSsaBasicBlock);
      while (!arrayList.isEmpty()) {
        int i = arrayList.size() - 1;
        paramSsaBasicBlock = arrayList.get(i);
        DFSInfo dFSInfo1 = this.info[paramSsaBasicBlock.getIndex()];
        SsaBasicBlock ssaBasicBlock1 = dFSInfo1.ancestor;
        DFSInfo dFSInfo2 = this.info[ssaBasicBlock1.getIndex()];
        if (hashSet.add(ssaBasicBlock1) && dFSInfo2.ancestor != null) {
          arrayList.add(ssaBasicBlock1);
          continue;
        } 
        arrayList.remove(i);
        if (dFSInfo2.ancestor == null)
          continue; 
        ssaBasicBlock1 = dFSInfo2.rep;
        SsaBasicBlock ssaBasicBlock2 = dFSInfo1.rep;
        if ((this.info[ssaBasicBlock1.getIndex()]).semidom < (this.info[ssaBasicBlock2.getIndex()]).semidom)
          dFSInfo1.rep = ssaBasicBlock1; 
        dFSInfo1.ancestor = dFSInfo2.ancestor;
      } 
    } 
  }
  
  private SsaBasicBlock eval(SsaBasicBlock paramSsaBasicBlock) {
    DFSInfo dFSInfo = this.info[paramSsaBasicBlock.getIndex()];
    if (dFSInfo.ancestor == null)
      return paramSsaBasicBlock; 
    compress(paramSsaBasicBlock);
    return dFSInfo.rep;
  }
  
  private BitSet getPreds(SsaBasicBlock paramSsaBasicBlock) {
    return this.postdom ? paramSsaBasicBlock.getSuccessors() : paramSsaBasicBlock.getPredecessors();
  }
  
  private BitSet getSuccs(SsaBasicBlock paramSsaBasicBlock) {
    return this.postdom ? paramSsaBasicBlock.getPredecessors() : paramSsaBasicBlock.getSuccessors();
  }
  
  public static Dominators make(SsaMethod paramSsaMethod, DomFront.DomInfo[] paramArrayOfDomInfo, boolean paramBoolean) {
    Dominators dominators = new Dominators(paramSsaMethod, paramArrayOfDomInfo, paramBoolean);
    dominators.run();
    return dominators;
  }
  
  private void run() {
    int j;
    SsaBasicBlock ssaBasicBlock;
    if (this.postdom) {
      ssaBasicBlock = this.meth.getExitBlock();
    } else {
      ssaBasicBlock = this.meth.getEntryBlock();
    } 
    if (ssaBasicBlock != null) {
      this.vertex.add(ssaBasicBlock);
      (this.domInfos[ssaBasicBlock.getIndex()]).idom = ssaBasicBlock.getIndex();
    } 
    DfsWalker dfsWalker = new DfsWalker();
    this.meth.forEachBlockDepthFirst(this.postdom, dfsWalker);
    int k = this.vertex.size() - 1;
    int i = k;
    while (true) {
      j = 2;
      if (i >= 2) {
        SsaBasicBlock ssaBasicBlock1 = this.vertex.get(i);
        DFSInfo dFSInfo = this.info[ssaBasicBlock1.getIndex()];
        BitSet bitSet = getPreds(ssaBasicBlock1);
        for (j = bitSet.nextSetBit(0); j >= 0; j = bitSet.nextSetBit(j + 1)) {
          SsaBasicBlock ssaBasicBlock2 = this.blocks.get(j);
          if (this.info[ssaBasicBlock2.getIndex()] != null) {
            int m = (this.info[eval(ssaBasicBlock2).getIndex()]).semidom;
            if (m < dFSInfo.semidom)
              dFSInfo.semidom = m; 
          } 
        } 
        (this.info[((SsaBasicBlock)this.vertex.get(dFSInfo.semidom)).getIndex()]).bucket.add(ssaBasicBlock1);
        dFSInfo.ancestor = dFSInfo.parent;
        ArrayList<SsaBasicBlock> arrayList = (this.info[dFSInfo.parent.getIndex()]).bucket;
        while (!arrayList.isEmpty()) {
          SsaBasicBlock ssaBasicBlock2 = arrayList.remove(arrayList.size() - 1);
          SsaBasicBlock ssaBasicBlock3 = eval(ssaBasicBlock2);
          if ((this.info[ssaBasicBlock3.getIndex()]).semidom < (this.info[ssaBasicBlock2.getIndex()]).semidom) {
            (this.domInfos[ssaBasicBlock2.getIndex()]).idom = ssaBasicBlock3.getIndex();
            continue;
          } 
          (this.domInfos[ssaBasicBlock2.getIndex()]).idom = dFSInfo.parent.getIndex();
        } 
        i--;
        continue;
      } 
      break;
    } 
    while (j <= k) {
      SsaBasicBlock ssaBasicBlock1 = this.vertex.get(j);
      if ((this.domInfos[ssaBasicBlock1.getIndex()]).idom != ((SsaBasicBlock)this.vertex.get((this.info[ssaBasicBlock1.getIndex()]).semidom)).getIndex()) {
        DomFront.DomInfo domInfo = this.domInfos[ssaBasicBlock1.getIndex()];
        DomFront.DomInfo[] arrayOfDomInfo = this.domInfos;
        domInfo.idom = (arrayOfDomInfo[(arrayOfDomInfo[ssaBasicBlock1.getIndex()]).idom]).idom;
      } 
      j++;
    } 
  }
  
  private static final class DFSInfo {
    public SsaBasicBlock ancestor;
    
    public ArrayList<SsaBasicBlock> bucket = new ArrayList<SsaBasicBlock>();
    
    public SsaBasicBlock parent;
    
    public SsaBasicBlock rep;
    
    public int semidom;
  }
  
  private class DfsWalker implements SsaBasicBlock.Visitor {
    private int dfsNum = 0;
    
    private DfsWalker() {}
    
    public void visitBlock(SsaBasicBlock param1SsaBasicBlock1, SsaBasicBlock param1SsaBasicBlock2) {
      Dominators.DFSInfo dFSInfo = new Dominators.DFSInfo();
      int i = this.dfsNum + 1;
      this.dfsNum = i;
      dFSInfo.semidom = i;
      dFSInfo.rep = param1SsaBasicBlock1;
      dFSInfo.parent = param1SsaBasicBlock2;
      Dominators.this.vertex.add(param1SsaBasicBlock1);
      Dominators.this.info[param1SsaBasicBlock1.getIndex()] = dFSInfo;
    }
  }
}


/* Location:              C:\Users\Student\Desktop\APK Decompiling\com.qualcomm.ftcdriverstation_38_apps.evozi.com\classes-dex2jar.jar!\org\firstinspires\ftc\robotcore\internal\android\dx\ssa\Dominators.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */