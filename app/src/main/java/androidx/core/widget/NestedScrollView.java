package androidx.core.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.AttributeSet;
import android.util.Log;
import android.util.TypedValue;
import android.view.FocusFinder;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityRecord;
import android.view.animation.AnimationUtils;
import android.widget.EdgeEffect;
import android.widget.FrameLayout;
import android.widget.OverScroller;
import android.widget.ScrollView;
import androidx.core.view.AccessibilityDelegateCompat;
import androidx.core.view.NestedScrollingChild3;
import androidx.core.view.NestedScrollingChildHelper;
import androidx.core.view.NestedScrollingParent3;
import androidx.core.view.NestedScrollingParentHelper;
import androidx.core.view.ScrollingView;
import androidx.core.view.ViewCompat;
import androidx.core.view.accessibility.AccessibilityNodeInfoCompat;
import androidx.core.view.accessibility.AccessibilityRecordCompat;

public class NestedScrollView extends FrameLayout implements NestedScrollingParent3, NestedScrollingChild3, ScrollingView {
  private static final AccessibilityDelegate ACCESSIBILITY_DELEGATE = new AccessibilityDelegate();
  
  static final int ANIMATED_SCROLL_GAP = 250;
  
  private static final int DEFAULT_SMOOTH_SCROLL_DURATION = 250;
  
  private static final int INVALID_POINTER = -1;
  
  static final float MAX_SCROLL_FACTOR = 0.5F;
  
  private static final int[] SCROLLVIEW_STYLEABLE = new int[] { 16843130 };
  
  private static final String TAG = "NestedScrollView";
  
  private int mActivePointerId = -1;
  
  private final NestedScrollingChildHelper mChildHelper;
  
  private View mChildToScrollTo = null;
  
  private EdgeEffect mEdgeGlowBottom;
  
  private EdgeEffect mEdgeGlowTop;
  
  private boolean mFillViewport;
  
  private boolean mIsBeingDragged = false;
  
  private boolean mIsLaidOut = false;
  
  private boolean mIsLayoutDirty = true;
  
  private int mLastMotionY;
  
  private long mLastScroll;
  
  private int mLastScrollerY;
  
  private int mMaximumVelocity;
  
  private int mMinimumVelocity;
  
  private int mNestedYOffset;
  
  private OnScrollChangeListener mOnScrollChangeListener;
  
  private final NestedScrollingParentHelper mParentHelper;
  
  private SavedState mSavedState;
  
  private final int[] mScrollConsumed = new int[2];
  
  private final int[] mScrollOffset = new int[2];
  
  private OverScroller mScroller;
  
  private boolean mSmoothScrollingEnabled = true;
  
  private final Rect mTempRect = new Rect();
  
  private int mTouchSlop;
  
  private VelocityTracker mVelocityTracker;
  
  private float mVerticalScrollFactor;
  
  public NestedScrollView(Context paramContext) {
    this(paramContext, (AttributeSet)null);
  }
  
  public NestedScrollView(Context paramContext, AttributeSet paramAttributeSet) {
    this(paramContext, paramAttributeSet, 0);
  }
  
  public NestedScrollView(Context paramContext, AttributeSet paramAttributeSet, int paramInt) {
    super(paramContext, paramAttributeSet, paramInt);
    initScrollView();
    TypedArray typedArray = paramContext.obtainStyledAttributes(paramAttributeSet, SCROLLVIEW_STYLEABLE, paramInt, 0);
    setFillViewport(typedArray.getBoolean(0, false));
    typedArray.recycle();
    this.mParentHelper = new NestedScrollingParentHelper((ViewGroup)this);
    this.mChildHelper = new NestedScrollingChildHelper((View)this);
    setNestedScrollingEnabled(true);
    ViewCompat.setAccessibilityDelegate((View)this, ACCESSIBILITY_DELEGATE);
  }
  
  private void abortAnimatedScroll() {
    this.mScroller.abortAnimation();
    stopNestedScroll(1);
  }
  
  private boolean canScroll() {
    int i = getChildCount();
    boolean bool2 = false;
    boolean bool1 = bool2;
    if (i > 0) {
      View view = getChildAt(0);
      FrameLayout.LayoutParams layoutParams = (FrameLayout.LayoutParams)view.getLayoutParams();
      bool1 = bool2;
      if (view.getHeight() + layoutParams.topMargin + layoutParams.bottomMargin > getHeight() - getPaddingTop() - getPaddingBottom())
        bool1 = true; 
    } 
    return bool1;
  }
  
  private static int clamp(int paramInt1, int paramInt2, int paramInt3) {
    return (paramInt2 >= paramInt3 || paramInt1 < 0) ? 0 : ((paramInt2 + paramInt1 > paramInt3) ? (paramInt3 - paramInt2) : paramInt1);
  }
  
  private void doScrollY(int paramInt) {
    if (paramInt != 0) {
      if (this.mSmoothScrollingEnabled) {
        smoothScrollBy(0, paramInt);
        return;
      } 
      scrollBy(0, paramInt);
    } 
  }
  
  private void endDrag() {
    this.mIsBeingDragged = false;
    recycleVelocityTracker();
    stopNestedScroll(0);
    EdgeEffect edgeEffect = this.mEdgeGlowTop;
    if (edgeEffect != null) {
      edgeEffect.onRelease();
      this.mEdgeGlowBottom.onRelease();
    } 
  }
  
  private void ensureGlows() {
    if (getOverScrollMode() != 2) {
      if (this.mEdgeGlowTop == null) {
        Context context = getContext();
        this.mEdgeGlowTop = new EdgeEffect(context);
        this.mEdgeGlowBottom = new EdgeEffect(context);
        return;
      } 
    } else {
      this.mEdgeGlowTop = null;
      this.mEdgeGlowBottom = null;
    } 
  }
  
  private View findFocusableViewInBounds(boolean paramBoolean, int paramInt1, int paramInt2) {
    // Byte code:
    //   0: aload_0
    //   1: iconst_2
    //   2: invokevirtual getFocusables : (I)Ljava/util/ArrayList;
    //   5: astore #14
    //   7: aload #14
    //   9: invokeinterface size : ()I
    //   14: istore #9
    //   16: aconst_null
    //   17: astore #13
    //   19: iconst_0
    //   20: istore #6
    //   22: iload #6
    //   24: istore #7
    //   26: iload #6
    //   28: iload #9
    //   30: if_icmpge -> 250
    //   33: aload #14
    //   35: iload #6
    //   37: invokeinterface get : (I)Ljava/lang/Object;
    //   42: checkcast android/view/View
    //   45: astore #12
    //   47: aload #12
    //   49: invokevirtual getTop : ()I
    //   52: istore #8
    //   54: aload #12
    //   56: invokevirtual getBottom : ()I
    //   59: istore #10
    //   61: aload #13
    //   63: astore #11
    //   65: iload #7
    //   67: istore #5
    //   69: iload_2
    //   70: iload #10
    //   72: if_icmpge -> 233
    //   75: aload #13
    //   77: astore #11
    //   79: iload #7
    //   81: istore #5
    //   83: iload #8
    //   85: iload_3
    //   86: if_icmpge -> 233
    //   89: iload_2
    //   90: iload #8
    //   92: if_icmpge -> 107
    //   95: iload #10
    //   97: iload_3
    //   98: if_icmpge -> 107
    //   101: iconst_1
    //   102: istore #4
    //   104: goto -> 110
    //   107: iconst_0
    //   108: istore #4
    //   110: aload #13
    //   112: ifnonnull -> 126
    //   115: aload #12
    //   117: astore #11
    //   119: iload #4
    //   121: istore #5
    //   123: goto -> 233
    //   126: iload_1
    //   127: ifeq -> 140
    //   130: iload #8
    //   132: aload #13
    //   134: invokevirtual getTop : ()I
    //   137: if_icmplt -> 154
    //   140: iload_1
    //   141: ifne -> 160
    //   144: iload #10
    //   146: aload #13
    //   148: invokevirtual getBottom : ()I
    //   151: if_icmple -> 160
    //   154: iconst_1
    //   155: istore #8
    //   157: goto -> 163
    //   160: iconst_0
    //   161: istore #8
    //   163: iload #7
    //   165: ifeq -> 197
    //   168: aload #13
    //   170: astore #11
    //   172: iload #7
    //   174: istore #5
    //   176: iload #4
    //   178: ifeq -> 233
    //   181: aload #13
    //   183: astore #11
    //   185: iload #7
    //   187: istore #5
    //   189: iload #8
    //   191: ifeq -> 233
    //   194: goto -> 225
    //   197: iload #4
    //   199: ifeq -> 212
    //   202: aload #12
    //   204: astore #11
    //   206: iconst_1
    //   207: istore #5
    //   209: goto -> 233
    //   212: aload #13
    //   214: astore #11
    //   216: iload #7
    //   218: istore #5
    //   220: iload #8
    //   222: ifeq -> 233
    //   225: aload #12
    //   227: astore #11
    //   229: iload #7
    //   231: istore #5
    //   233: iload #6
    //   235: iconst_1
    //   236: iadd
    //   237: istore #6
    //   239: aload #11
    //   241: astore #13
    //   243: iload #5
    //   245: istore #7
    //   247: goto -> 26
    //   250: aload #13
    //   252: areturn
  }
  
  private float getVerticalScrollFactorCompat() {
    if (this.mVerticalScrollFactor == 0.0F) {
      TypedValue typedValue = new TypedValue();
      Context context = getContext();
      if (context.getTheme().resolveAttribute(16842829, typedValue, true)) {
        this.mVerticalScrollFactor = typedValue.getDimension(context.getResources().getDisplayMetrics());
      } else {
        throw new IllegalStateException("Expected theme to define listPreferredItemHeight.");
      } 
    } 
    return this.mVerticalScrollFactor;
  }
  
  private boolean inChild(int paramInt1, int paramInt2) {
    int i = getChildCount();
    boolean bool2 = false;
    boolean bool1 = bool2;
    if (i > 0) {
      i = getScrollY();
      View view = getChildAt(0);
      bool1 = bool2;
      if (paramInt2 >= view.getTop() - i) {
        bool1 = bool2;
        if (paramInt2 < view.getBottom() - i) {
          bool1 = bool2;
          if (paramInt1 >= view.getLeft()) {
            bool1 = bool2;
            if (paramInt1 < view.getRight())
              bool1 = true; 
          } 
        } 
      } 
    } 
    return bool1;
  }
  
  private void initOrResetVelocityTracker() {
    VelocityTracker velocityTracker = this.mVelocityTracker;
    if (velocityTracker == null) {
      this.mVelocityTracker = VelocityTracker.obtain();
      return;
    } 
    velocityTracker.clear();
  }
  
  private void initScrollView() {
    this.mScroller = new OverScroller(getContext());
    setFocusable(true);
    setDescendantFocusability(262144);
    setWillNotDraw(false);
    ViewConfiguration viewConfiguration = ViewConfiguration.get(getContext());
    this.mTouchSlop = viewConfiguration.getScaledTouchSlop();
    this.mMinimumVelocity = viewConfiguration.getScaledMinimumFlingVelocity();
    this.mMaximumVelocity = viewConfiguration.getScaledMaximumFlingVelocity();
  }
  
  private void initVelocityTrackerIfNotExists() {
    if (this.mVelocityTracker == null)
      this.mVelocityTracker = VelocityTracker.obtain(); 
  }
  
  private boolean isOffScreen(View paramView) {
    return isWithinDeltaOfScreen(paramView, 0, getHeight()) ^ true;
  }
  
  private static boolean isViewDescendantOf(View paramView1, View paramView2) {
    if (paramView1 == paramView2)
      return true; 
    ViewParent viewParent = paramView1.getParent();
    return (viewParent instanceof ViewGroup && isViewDescendantOf((View)viewParent, paramView2));
  }
  
  private boolean isWithinDeltaOfScreen(View paramView, int paramInt1, int paramInt2) {
    paramView.getDrawingRect(this.mTempRect);
    offsetDescendantRectToMyCoords(paramView, this.mTempRect);
    return (this.mTempRect.bottom + paramInt1 >= getScrollY() && this.mTempRect.top - paramInt1 <= getScrollY() + paramInt2);
  }
  
  private void onNestedScrollInternal(int paramInt1, int paramInt2, int[] paramArrayOfint) {
    int i = getScrollY();
    scrollBy(0, paramInt1);
    i = getScrollY() - i;
    if (paramArrayOfint != null)
      paramArrayOfint[1] = paramArrayOfint[1] + i; 
    this.mChildHelper.dispatchNestedScroll(0, i, 0, paramInt1 - i, null, paramInt2, paramArrayOfint);
  }
  
  private void onSecondaryPointerUp(MotionEvent paramMotionEvent) {
    int i = paramMotionEvent.getActionIndex();
    if (paramMotionEvent.getPointerId(i) == this.mActivePointerId) {
      if (i == 0) {
        i = 1;
      } else {
        i = 0;
      } 
      this.mLastMotionY = (int)paramMotionEvent.getY(i);
      this.mActivePointerId = paramMotionEvent.getPointerId(i);
      VelocityTracker velocityTracker = this.mVelocityTracker;
      if (velocityTracker != null)
        velocityTracker.clear(); 
    } 
  }
  
  private void recycleVelocityTracker() {
    VelocityTracker velocityTracker = this.mVelocityTracker;
    if (velocityTracker != null) {
      velocityTracker.recycle();
      this.mVelocityTracker = null;
    } 
  }
  
  private void runAnimatedScroll(boolean paramBoolean) {
    if (paramBoolean) {
      startNestedScroll(2, 1);
    } else {
      stopNestedScroll(1);
    } 
    this.mLastScrollerY = getScrollY();
    ViewCompat.postInvalidateOnAnimation((View)this);
  }
  
  private boolean scrollAndFocus(int paramInt1, int paramInt2, int paramInt3) {
    boolean bool1;
    NestedScrollView nestedScrollView;
    int j = getHeight();
    int i = getScrollY();
    j += i;
    boolean bool2 = false;
    if (paramInt1 == 33) {
      bool1 = true;
    } else {
      bool1 = false;
    } 
    View view2 = findFocusableViewInBounds(bool1, paramInt2, paramInt3);
    View view1 = view2;
    if (view2 == null)
      nestedScrollView = this; 
    if (paramInt2 >= i && paramInt3 <= j) {
      bool1 = bool2;
    } else {
      if (bool1) {
        paramInt2 -= i;
      } else {
        paramInt2 = paramInt3 - j;
      } 
      doScrollY(paramInt2);
      bool1 = true;
    } 
    if (nestedScrollView != findFocus())
      nestedScrollView.requestFocus(paramInt1); 
    return bool1;
  }
  
  private void scrollToChild(View paramView) {
    paramView.getDrawingRect(this.mTempRect);
    offsetDescendantRectToMyCoords(paramView, this.mTempRect);
    int i = computeScrollDeltaToGetChildRectOnScreen(this.mTempRect);
    if (i != 0)
      scrollBy(0, i); 
  }
  
  private boolean scrollToChildRect(Rect paramRect, boolean paramBoolean) {
    boolean bool;
    int i = computeScrollDeltaToGetChildRectOnScreen(paramRect);
    if (i != 0) {
      bool = true;
    } else {
      bool = false;
    } 
    if (bool) {
      if (paramBoolean) {
        scrollBy(0, i);
        return bool;
      } 
      smoothScrollBy(0, i);
    } 
    return bool;
  }
  
  private void smoothScrollBy(int paramInt1, int paramInt2, int paramInt3, boolean paramBoolean) {
    if (getChildCount() == 0)
      return; 
    if (AnimationUtils.currentAnimationTimeMillis() - this.mLastScroll > 250L) {
      View view = getChildAt(0);
      FrameLayout.LayoutParams layoutParams = (FrameLayout.LayoutParams)view.getLayoutParams();
      int i = view.getHeight();
      int j = layoutParams.topMargin;
      int k = layoutParams.bottomMargin;
      int m = getHeight();
      int n = getPaddingTop();
      int i1 = getPaddingBottom();
      paramInt1 = getScrollY();
      paramInt2 = Math.max(0, Math.min(paramInt2 + paramInt1, Math.max(0, i + j + k - m - n - i1)));
      this.mScroller.startScroll(getScrollX(), paramInt1, 0, paramInt2 - paramInt1, paramInt3);
      runAnimatedScroll(paramBoolean);
    } else {
      if (!this.mScroller.isFinished())
        abortAnimatedScroll(); 
      scrollBy(paramInt1, paramInt2);
    } 
    this.mLastScroll = AnimationUtils.currentAnimationTimeMillis();
  }
  
  public void addView(View paramView) {
    if (getChildCount() <= 0) {
      super.addView(paramView);
      return;
    } 
    throw new IllegalStateException("ScrollView can host only one direct child");
  }
  
  public void addView(View paramView, int paramInt) {
    if (getChildCount() <= 0) {
      super.addView(paramView, paramInt);
      return;
    } 
    throw new IllegalStateException("ScrollView can host only one direct child");
  }
  
  public void addView(View paramView, int paramInt, ViewGroup.LayoutParams paramLayoutParams) {
    if (getChildCount() <= 0) {
      super.addView(paramView, paramInt, paramLayoutParams);
      return;
    } 
    throw new IllegalStateException("ScrollView can host only one direct child");
  }
  
  public void addView(View paramView, ViewGroup.LayoutParams paramLayoutParams) {
    if (getChildCount() <= 0) {
      super.addView(paramView, paramLayoutParams);
      return;
    } 
    throw new IllegalStateException("ScrollView can host only one direct child");
  }
  
  public boolean arrowScroll(int paramInt) {
    View view2 = findFocus();
    View view1 = view2;
    if (view2 == this)
      view1 = null; 
    view2 = FocusFinder.getInstance().findNextFocus((ViewGroup)this, view1, paramInt);
    int i = getMaxScrollAmount();
    if (view2 != null && isWithinDeltaOfScreen(view2, i, getHeight())) {
      view2.getDrawingRect(this.mTempRect);
      offsetDescendantRectToMyCoords(view2, this.mTempRect);
      doScrollY(computeScrollDeltaToGetChildRectOnScreen(this.mTempRect));
      view2.requestFocus(paramInt);
    } else {
      int j;
      if (paramInt == 33 && getScrollY() < i) {
        j = getScrollY();
      } else {
        j = i;
        if (paramInt == 130) {
          j = i;
          if (getChildCount() > 0) {
            view2 = getChildAt(0);
            FrameLayout.LayoutParams layoutParams = (FrameLayout.LayoutParams)view2.getLayoutParams();
            j = Math.min(view2.getBottom() + layoutParams.bottomMargin - getScrollY() + getHeight() - getPaddingBottom(), i);
          } 
        } 
      } 
      if (j == 0)
        return false; 
      if (paramInt != 130)
        j = -j; 
      doScrollY(j);
    } 
    if (view1 != null && view1.isFocused() && isOffScreen(view1)) {
      paramInt = getDescendantFocusability();
      setDescendantFocusability(131072);
      requestFocus();
      setDescendantFocusability(paramInt);
    } 
    return true;
  }
  
  public int computeHorizontalScrollExtent() {
    return super.computeHorizontalScrollExtent();
  }
  
  public int computeHorizontalScrollOffset() {
    return super.computeHorizontalScrollOffset();
  }
  
  public int computeHorizontalScrollRange() {
    return super.computeHorizontalScrollRange();
  }
  
  public void computeScroll() {
    // Byte code:
    //   0: aload_0
    //   1: getfield mScroller : Landroid/widget/OverScroller;
    //   4: invokevirtual isFinished : ()Z
    //   7: ifeq -> 11
    //   10: return
    //   11: aload_0
    //   12: getfield mScroller : Landroid/widget/OverScroller;
    //   15: invokevirtual computeScrollOffset : ()Z
    //   18: pop
    //   19: aload_0
    //   20: getfield mScroller : Landroid/widget/OverScroller;
    //   23: invokevirtual getCurrY : ()I
    //   26: istore_2
    //   27: iload_2
    //   28: aload_0
    //   29: getfield mLastScrollerY : I
    //   32: isub
    //   33: istore_1
    //   34: aload_0
    //   35: iload_2
    //   36: putfield mLastScrollerY : I
    //   39: aload_0
    //   40: getfield mScrollConsumed : [I
    //   43: astore #6
    //   45: iconst_0
    //   46: istore_3
    //   47: aload #6
    //   49: iconst_1
    //   50: iconst_0
    //   51: iastore
    //   52: aload_0
    //   53: iconst_0
    //   54: iload_1
    //   55: aload #6
    //   57: aconst_null
    //   58: iconst_1
    //   59: invokevirtual dispatchNestedPreScroll : (II[I[II)Z
    //   62: pop
    //   63: iload_1
    //   64: aload_0
    //   65: getfield mScrollConsumed : [I
    //   68: iconst_1
    //   69: iaload
    //   70: isub
    //   71: istore_2
    //   72: aload_0
    //   73: invokevirtual getScrollRange : ()I
    //   76: istore #4
    //   78: iload_2
    //   79: istore_1
    //   80: iload_2
    //   81: ifeq -> 153
    //   84: aload_0
    //   85: invokevirtual getScrollY : ()I
    //   88: istore_1
    //   89: aload_0
    //   90: iconst_0
    //   91: iload_2
    //   92: aload_0
    //   93: invokevirtual getScrollX : ()I
    //   96: iload_1
    //   97: iconst_0
    //   98: iload #4
    //   100: iconst_0
    //   101: iconst_0
    //   102: iconst_0
    //   103: invokevirtual overScrollByCompat : (IIIIIIIIZ)Z
    //   106: pop
    //   107: aload_0
    //   108: invokevirtual getScrollY : ()I
    //   111: iload_1
    //   112: isub
    //   113: istore_1
    //   114: iload_2
    //   115: iload_1
    //   116: isub
    //   117: istore_2
    //   118: aload_0
    //   119: getfield mScrollConsumed : [I
    //   122: astore #6
    //   124: aload #6
    //   126: iconst_1
    //   127: iconst_0
    //   128: iastore
    //   129: aload_0
    //   130: iconst_0
    //   131: iload_1
    //   132: iconst_0
    //   133: iload_2
    //   134: aload_0
    //   135: getfield mScrollOffset : [I
    //   138: iconst_1
    //   139: aload #6
    //   141: invokevirtual dispatchNestedScroll : (IIII[II[I)V
    //   144: iload_2
    //   145: aload_0
    //   146: getfield mScrollConsumed : [I
    //   149: iconst_1
    //   150: iaload
    //   151: isub
    //   152: istore_1
    //   153: iload_1
    //   154: ifeq -> 254
    //   157: aload_0
    //   158: invokevirtual getOverScrollMode : ()I
    //   161: istore #5
    //   163: iload #5
    //   165: ifeq -> 183
    //   168: iload_3
    //   169: istore_2
    //   170: iload #5
    //   172: iconst_1
    //   173: if_icmpne -> 185
    //   176: iload_3
    //   177: istore_2
    //   178: iload #4
    //   180: ifle -> 185
    //   183: iconst_1
    //   184: istore_2
    //   185: iload_2
    //   186: ifeq -> 250
    //   189: aload_0
    //   190: invokespecial ensureGlows : ()V
    //   193: iload_1
    //   194: ifge -> 225
    //   197: aload_0
    //   198: getfield mEdgeGlowTop : Landroid/widget/EdgeEffect;
    //   201: invokevirtual isFinished : ()Z
    //   204: ifeq -> 250
    //   207: aload_0
    //   208: getfield mEdgeGlowTop : Landroid/widget/EdgeEffect;
    //   211: aload_0
    //   212: getfield mScroller : Landroid/widget/OverScroller;
    //   215: invokevirtual getCurrVelocity : ()F
    //   218: f2i
    //   219: invokevirtual onAbsorb : (I)V
    //   222: goto -> 250
    //   225: aload_0
    //   226: getfield mEdgeGlowBottom : Landroid/widget/EdgeEffect;
    //   229: invokevirtual isFinished : ()Z
    //   232: ifeq -> 250
    //   235: aload_0
    //   236: getfield mEdgeGlowBottom : Landroid/widget/EdgeEffect;
    //   239: aload_0
    //   240: getfield mScroller : Landroid/widget/OverScroller;
    //   243: invokevirtual getCurrVelocity : ()F
    //   246: f2i
    //   247: invokevirtual onAbsorb : (I)V
    //   250: aload_0
    //   251: invokespecial abortAnimatedScroll : ()V
    //   254: aload_0
    //   255: getfield mScroller : Landroid/widget/OverScroller;
    //   258: invokevirtual isFinished : ()Z
    //   261: ifne -> 269
    //   264: aload_0
    //   265: invokestatic postInvalidateOnAnimation : (Landroid/view/View;)V
    //   268: return
    //   269: aload_0
    //   270: iconst_1
    //   271: invokevirtual stopNestedScroll : (I)V
    //   274: return
  }
  
  protected int computeScrollDeltaToGetChildRectOnScreen(Rect paramRect) {
    int i = getChildCount();
    boolean bool = false;
    if (i == 0)
      return 0; 
    int m = getHeight();
    int j = getScrollY();
    int k = j + m;
    int n = getVerticalFadingEdgeLength();
    i = j;
    if (paramRect.top > 0)
      i = j + n; 
    View view = getChildAt(0);
    FrameLayout.LayoutParams layoutParams = (FrameLayout.LayoutParams)view.getLayoutParams();
    if (paramRect.bottom < view.getHeight() + layoutParams.topMargin + layoutParams.bottomMargin) {
      j = k - n;
    } else {
      j = k;
    } 
    if (paramRect.bottom > j && paramRect.top > i) {
      if (paramRect.height() > m) {
        i = paramRect.top - i;
      } else {
        i = paramRect.bottom - j;
      } 
      return Math.min(i + 0, view.getBottom() + layoutParams.bottomMargin - k);
    } 
    k = bool;
    if (paramRect.top < i) {
      k = bool;
      if (paramRect.bottom < j) {
        if (paramRect.height() > m) {
          i = 0 - j - paramRect.bottom;
        } else {
          i = 0 - i - paramRect.top;
        } 
        k = Math.max(i, -getScrollY());
      } 
    } 
    return k;
  }
  
  public int computeVerticalScrollExtent() {
    return super.computeVerticalScrollExtent();
  }
  
  public int computeVerticalScrollOffset() {
    return Math.max(0, super.computeVerticalScrollOffset());
  }
  
  public int computeVerticalScrollRange() {
    int j = getChildCount();
    int i = getHeight() - getPaddingBottom() - getPaddingTop();
    if (j == 0)
      return i; 
    View view = getChildAt(0);
    FrameLayout.LayoutParams layoutParams = (FrameLayout.LayoutParams)view.getLayoutParams();
    j = view.getBottom() + layoutParams.bottomMargin;
    int k = getScrollY();
    int m = Math.max(0, j - i);
    if (k < 0)
      return j - k; 
    i = j;
    if (k > m)
      i = j + k - m; 
    return i;
  }
  
  public boolean dispatchKeyEvent(KeyEvent paramKeyEvent) {
    return (super.dispatchKeyEvent(paramKeyEvent) || executeKeyEvent(paramKeyEvent));
  }
  
  public boolean dispatchNestedFling(float paramFloat1, float paramFloat2, boolean paramBoolean) {
    return this.mChildHelper.dispatchNestedFling(paramFloat1, paramFloat2, paramBoolean);
  }
  
  public boolean dispatchNestedPreFling(float paramFloat1, float paramFloat2) {
    return this.mChildHelper.dispatchNestedPreFling(paramFloat1, paramFloat2);
  }
  
  public boolean dispatchNestedPreScroll(int paramInt1, int paramInt2, int[] paramArrayOfint1, int[] paramArrayOfint2) {
    return dispatchNestedPreScroll(paramInt1, paramInt2, paramArrayOfint1, paramArrayOfint2, 0);
  }
  
  public boolean dispatchNestedPreScroll(int paramInt1, int paramInt2, int[] paramArrayOfint1, int[] paramArrayOfint2, int paramInt3) {
    return this.mChildHelper.dispatchNestedPreScroll(paramInt1, paramInt2, paramArrayOfint1, paramArrayOfint2, paramInt3);
  }
  
  public void dispatchNestedScroll(int paramInt1, int paramInt2, int paramInt3, int paramInt4, int[] paramArrayOfint1, int paramInt5, int[] paramArrayOfint2) {
    this.mChildHelper.dispatchNestedScroll(paramInt1, paramInt2, paramInt3, paramInt4, paramArrayOfint1, paramInt5, paramArrayOfint2);
  }
  
  public boolean dispatchNestedScroll(int paramInt1, int paramInt2, int paramInt3, int paramInt4, int[] paramArrayOfint) {
    return this.mChildHelper.dispatchNestedScroll(paramInt1, paramInt2, paramInt3, paramInt4, paramArrayOfint);
  }
  
  public boolean dispatchNestedScroll(int paramInt1, int paramInt2, int paramInt3, int paramInt4, int[] paramArrayOfint, int paramInt5) {
    return this.mChildHelper.dispatchNestedScroll(paramInt1, paramInt2, paramInt3, paramInt4, paramArrayOfint, paramInt5);
  }
  
  public void draw(Canvas paramCanvas) {
    // Byte code:
    //   0: aload_0
    //   1: aload_1
    //   2: invokespecial draw : (Landroid/graphics/Canvas;)V
    //   5: aload_0
    //   6: getfield mEdgeGlowTop : Landroid/widget/EdgeEffect;
    //   9: ifnull -> 385
    //   12: aload_0
    //   13: invokevirtual getScrollY : ()I
    //   16: istore #9
    //   18: aload_0
    //   19: getfield mEdgeGlowTop : Landroid/widget/EdgeEffect;
    //   22: invokevirtual isFinished : ()Z
    //   25: istore #11
    //   27: iconst_0
    //   28: istore #6
    //   30: iload #11
    //   32: ifne -> 196
    //   35: aload_1
    //   36: invokevirtual save : ()I
    //   39: istore #10
    //   41: aload_0
    //   42: invokevirtual getWidth : ()I
    //   45: istore_2
    //   46: aload_0
    //   47: invokevirtual getHeight : ()I
    //   50: istore #8
    //   52: iconst_0
    //   53: iload #9
    //   55: invokestatic min : (II)I
    //   58: istore #7
    //   60: getstatic android/os/Build$VERSION.SDK_INT : I
    //   63: bipush #21
    //   65: if_icmplt -> 83
    //   68: aload_0
    //   69: invokevirtual getClipToPadding : ()Z
    //   72: ifeq -> 78
    //   75: goto -> 83
    //   78: iconst_0
    //   79: istore_3
    //   80: goto -> 102
    //   83: iload_2
    //   84: aload_0
    //   85: invokevirtual getPaddingLeft : ()I
    //   88: aload_0
    //   89: invokevirtual getPaddingRight : ()I
    //   92: iadd
    //   93: isub
    //   94: istore_2
    //   95: aload_0
    //   96: invokevirtual getPaddingLeft : ()I
    //   99: iconst_0
    //   100: iadd
    //   101: istore_3
    //   102: iload #8
    //   104: istore #5
    //   106: iload #7
    //   108: istore #4
    //   110: getstatic android/os/Build$VERSION.SDK_INT : I
    //   113: bipush #21
    //   115: if_icmplt -> 156
    //   118: iload #8
    //   120: istore #5
    //   122: iload #7
    //   124: istore #4
    //   126: aload_0
    //   127: invokevirtual getClipToPadding : ()Z
    //   130: ifeq -> 156
    //   133: iload #8
    //   135: aload_0
    //   136: invokevirtual getPaddingTop : ()I
    //   139: aload_0
    //   140: invokevirtual getPaddingBottom : ()I
    //   143: iadd
    //   144: isub
    //   145: istore #5
    //   147: iload #7
    //   149: aload_0
    //   150: invokevirtual getPaddingTop : ()I
    //   153: iadd
    //   154: istore #4
    //   156: aload_1
    //   157: iload_3
    //   158: i2f
    //   159: iload #4
    //   161: i2f
    //   162: invokevirtual translate : (FF)V
    //   165: aload_0
    //   166: getfield mEdgeGlowTop : Landroid/widget/EdgeEffect;
    //   169: iload_2
    //   170: iload #5
    //   172: invokevirtual setSize : (II)V
    //   175: aload_0
    //   176: getfield mEdgeGlowTop : Landroid/widget/EdgeEffect;
    //   179: aload_1
    //   180: invokevirtual draw : (Landroid/graphics/Canvas;)Z
    //   183: ifeq -> 190
    //   186: aload_0
    //   187: invokestatic postInvalidateOnAnimation : (Landroid/view/View;)V
    //   190: aload_1
    //   191: iload #10
    //   193: invokevirtual restoreToCount : (I)V
    //   196: aload_0
    //   197: getfield mEdgeGlowBottom : Landroid/widget/EdgeEffect;
    //   200: invokevirtual isFinished : ()Z
    //   203: ifne -> 385
    //   206: aload_1
    //   207: invokevirtual save : ()I
    //   210: istore #10
    //   212: aload_0
    //   213: invokevirtual getWidth : ()I
    //   216: istore #4
    //   218: aload_0
    //   219: invokevirtual getHeight : ()I
    //   222: istore #7
    //   224: aload_0
    //   225: invokevirtual getScrollRange : ()I
    //   228: iload #9
    //   230: invokestatic max : (II)I
    //   233: iload #7
    //   235: iadd
    //   236: istore #8
    //   238: getstatic android/os/Build$VERSION.SDK_INT : I
    //   241: bipush #21
    //   243: if_icmplt -> 259
    //   246: iload #6
    //   248: istore_3
    //   249: iload #4
    //   251: istore_2
    //   252: aload_0
    //   253: invokevirtual getClipToPadding : ()Z
    //   256: ifeq -> 279
    //   259: iload #4
    //   261: aload_0
    //   262: invokevirtual getPaddingLeft : ()I
    //   265: aload_0
    //   266: invokevirtual getPaddingRight : ()I
    //   269: iadd
    //   270: isub
    //   271: istore_2
    //   272: iconst_0
    //   273: aload_0
    //   274: invokevirtual getPaddingLeft : ()I
    //   277: iadd
    //   278: istore_3
    //   279: iload #8
    //   281: istore #5
    //   283: iload #7
    //   285: istore #4
    //   287: getstatic android/os/Build$VERSION.SDK_INT : I
    //   290: bipush #21
    //   292: if_icmplt -> 333
    //   295: iload #8
    //   297: istore #5
    //   299: iload #7
    //   301: istore #4
    //   303: aload_0
    //   304: invokevirtual getClipToPadding : ()Z
    //   307: ifeq -> 333
    //   310: iload #7
    //   312: aload_0
    //   313: invokevirtual getPaddingTop : ()I
    //   316: aload_0
    //   317: invokevirtual getPaddingBottom : ()I
    //   320: iadd
    //   321: isub
    //   322: istore #4
    //   324: iload #8
    //   326: aload_0
    //   327: invokevirtual getPaddingBottom : ()I
    //   330: isub
    //   331: istore #5
    //   333: aload_1
    //   334: iload_3
    //   335: iload_2
    //   336: isub
    //   337: i2f
    //   338: iload #5
    //   340: i2f
    //   341: invokevirtual translate : (FF)V
    //   344: aload_1
    //   345: ldc_w 180.0
    //   348: iload_2
    //   349: i2f
    //   350: fconst_0
    //   351: invokevirtual rotate : (FFF)V
    //   354: aload_0
    //   355: getfield mEdgeGlowBottom : Landroid/widget/EdgeEffect;
    //   358: iload_2
    //   359: iload #4
    //   361: invokevirtual setSize : (II)V
    //   364: aload_0
    //   365: getfield mEdgeGlowBottom : Landroid/widget/EdgeEffect;
    //   368: aload_1
    //   369: invokevirtual draw : (Landroid/graphics/Canvas;)Z
    //   372: ifeq -> 379
    //   375: aload_0
    //   376: invokestatic postInvalidateOnAnimation : (Landroid/view/View;)V
    //   379: aload_1
    //   380: iload #10
    //   382: invokevirtual restoreToCount : (I)V
    //   385: return
  }
  
  public boolean executeKeyEvent(KeyEvent paramKeyEvent) {
    View view;
    this.mTempRect.setEmpty();
    boolean bool3 = canScroll();
    boolean bool1 = false;
    boolean bool2 = false;
    char c = '';
    if (!bool3) {
      bool1 = bool2;
      if (isFocused()) {
        bool1 = bool2;
        if (paramKeyEvent.getKeyCode() != 4) {
          View view1 = findFocus();
          view = view1;
          if (view1 == this)
            view = null; 
          view = FocusFinder.getInstance().findNextFocus((ViewGroup)this, view, 130);
          bool1 = bool2;
          if (view != null) {
            bool1 = bool2;
            if (view != this) {
              bool1 = bool2;
              if (view.requestFocus(130))
                bool1 = true; 
            } 
          } 
        } 
      } 
      return bool1;
    } 
    if (view.getAction() == 0) {
      int i = view.getKeyCode();
      if (i != 19) {
        if (i != 20) {
          if (i != 62)
            return false; 
          if (view.isShiftPressed())
            c = '!'; 
          pageScroll(c);
          return false;
        } 
        return !view.isAltPressed() ? arrowScroll(130) : fullScroll(130);
      } 
      if (!view.isAltPressed())
        return arrowScroll(33); 
      bool1 = fullScroll(33);
    } 
    return bool1;
  }
  
  public void fling(int paramInt) {
    if (getChildCount() > 0) {
      this.mScroller.fling(getScrollX(), getScrollY(), 0, paramInt, 0, 0, -2147483648, 2147483647, 0, 0);
      runAnimatedScroll(true);
    } 
  }
  
  public boolean fullScroll(int paramInt) {
    int i;
    if (paramInt == 130) {
      i = 1;
    } else {
      i = 0;
    } 
    int j = getHeight();
    this.mTempRect.top = 0;
    this.mTempRect.bottom = j;
    if (i) {
      i = getChildCount();
      if (i > 0) {
        View view = getChildAt(i - 1);
        FrameLayout.LayoutParams layoutParams = (FrameLayout.LayoutParams)view.getLayoutParams();
        this.mTempRect.bottom = view.getBottom() + layoutParams.bottomMargin + getPaddingBottom();
        Rect rect = this.mTempRect;
        rect.top = rect.bottom - j;
      } 
    } 
    return scrollAndFocus(paramInt, this.mTempRect.top, this.mTempRect.bottom);
  }
  
  protected float getBottomFadingEdgeStrength() {
    if (getChildCount() == 0)
      return 0.0F; 
    View view = getChildAt(0);
    FrameLayout.LayoutParams layoutParams = (FrameLayout.LayoutParams)view.getLayoutParams();
    int i = getVerticalFadingEdgeLength();
    int j = getHeight();
    int k = getPaddingBottom();
    j = view.getBottom() + layoutParams.bottomMargin - getScrollY() - j - k;
    return (j < i) ? (j / i) : 1.0F;
  }
  
  public int getMaxScrollAmount() {
    return (int)(getHeight() * 0.5F);
  }
  
  public int getNestedScrollAxes() {
    return this.mParentHelper.getNestedScrollAxes();
  }
  
  int getScrollRange() {
    int j = getChildCount();
    int i = 0;
    if (j > 0) {
      View view = getChildAt(0);
      FrameLayout.LayoutParams layoutParams = (FrameLayout.LayoutParams)view.getLayoutParams();
      i = Math.max(0, view.getHeight() + layoutParams.topMargin + layoutParams.bottomMargin - getHeight() - getPaddingTop() - getPaddingBottom());
    } 
    return i;
  }
  
  protected float getTopFadingEdgeStrength() {
    if (getChildCount() == 0)
      return 0.0F; 
    int i = getVerticalFadingEdgeLength();
    int j = getScrollY();
    return (j < i) ? (j / i) : 1.0F;
  }
  
  public boolean hasNestedScrollingParent() {
    return hasNestedScrollingParent(0);
  }
  
  public boolean hasNestedScrollingParent(int paramInt) {
    return this.mChildHelper.hasNestedScrollingParent(paramInt);
  }
  
  public boolean isFillViewport() {
    return this.mFillViewport;
  }
  
  public boolean isNestedScrollingEnabled() {
    return this.mChildHelper.isNestedScrollingEnabled();
  }
  
  public boolean isSmoothScrollingEnabled() {
    return this.mSmoothScrollingEnabled;
  }
  
  protected void measureChild(View paramView, int paramInt1, int paramInt2) {
    ViewGroup.LayoutParams layoutParams = paramView.getLayoutParams();
    paramView.measure(getChildMeasureSpec(paramInt1, getPaddingLeft() + getPaddingRight(), layoutParams.width), View.MeasureSpec.makeMeasureSpec(0, 0));
  }
  
  protected void measureChildWithMargins(View paramView, int paramInt1, int paramInt2, int paramInt3, int paramInt4) {
    ViewGroup.MarginLayoutParams marginLayoutParams = (ViewGroup.MarginLayoutParams)paramView.getLayoutParams();
    paramView.measure(getChildMeasureSpec(paramInt1, getPaddingLeft() + getPaddingRight() + marginLayoutParams.leftMargin + marginLayoutParams.rightMargin + paramInt2, marginLayoutParams.width), View.MeasureSpec.makeMeasureSpec(marginLayoutParams.topMargin + marginLayoutParams.bottomMargin, 0));
  }
  
  public void onAttachedToWindow() {
    super.onAttachedToWindow();
    this.mIsLaidOut = false;
  }
  
  public boolean onGenericMotionEvent(MotionEvent paramMotionEvent) {
    if ((paramMotionEvent.getSource() & 0x2) != 0) {
      if (paramMotionEvent.getAction() != 8)
        return false; 
      if (!this.mIsBeingDragged) {
        float f = paramMotionEvent.getAxisValue(9);
        if (f != 0.0F) {
          int j = (int)(f * getVerticalScrollFactorCompat());
          int i = getScrollRange();
          int k = getScrollY();
          j = k - j;
          if (j < 0) {
            i = 0;
          } else if (j <= i) {
            i = j;
          } 
          if (i != k) {
            super.scrollTo(getScrollX(), i);
            return true;
          } 
        } 
      } 
    } 
    return false;
  }
  
  public boolean onInterceptTouchEvent(MotionEvent paramMotionEvent) {
    ViewParent viewParent;
    int i = paramMotionEvent.getAction();
    if (i == 2 && this.mIsBeingDragged)
      return true; 
    i &= 0xFF;
    if (i != 0) {
      if (i != 1)
        if (i != 2) {
          if (i != 3) {
            if (i == 6)
              onSecondaryPointerUp(paramMotionEvent); 
            return this.mIsBeingDragged;
          } 
        } else {
          i = this.mActivePointerId;
          if (i != -1) {
            StringBuilder stringBuilder;
            int j = paramMotionEvent.findPointerIndex(i);
            if (j == -1) {
              stringBuilder = new StringBuilder();
              stringBuilder.append("Invalid pointerId=");
              stringBuilder.append(i);
              stringBuilder.append(" in onInterceptTouchEvent");
              Log.e("NestedScrollView", stringBuilder.toString());
            } else {
              i = (int)stringBuilder.getY(j);
              if (Math.abs(i - this.mLastMotionY) > this.mTouchSlop && (0x2 & getNestedScrollAxes()) == 0) {
                this.mIsBeingDragged = true;
                this.mLastMotionY = i;
                initVelocityTrackerIfNotExists();
                this.mVelocityTracker.addMovement((MotionEvent)stringBuilder);
                this.mNestedYOffset = 0;
                viewParent = getParent();
                if (viewParent != null)
                  viewParent.requestDisallowInterceptTouchEvent(true); 
              } 
            } 
          } 
          return this.mIsBeingDragged;
        }  
      this.mIsBeingDragged = false;
      this.mActivePointerId = -1;
      recycleVelocityTracker();
      if (this.mScroller.springBack(getScrollX(), getScrollY(), 0, 0, 0, getScrollRange()))
        ViewCompat.postInvalidateOnAnimation((View)this); 
      stopNestedScroll(0);
    } else {
      i = (int)viewParent.getY();
      if (!inChild((int)viewParent.getX(), i)) {
        this.mIsBeingDragged = false;
        recycleVelocityTracker();
      } else {
        this.mLastMotionY = i;
        this.mActivePointerId = viewParent.getPointerId(0);
        initOrResetVelocityTracker();
        this.mVelocityTracker.addMovement((MotionEvent)viewParent);
        this.mScroller.computeScrollOffset();
        this.mIsBeingDragged = this.mScroller.isFinished() ^ true;
        startNestedScroll(2, 0);
      } 
    } 
    return this.mIsBeingDragged;
  }
  
  protected void onLayout(boolean paramBoolean, int paramInt1, int paramInt2, int paramInt3, int paramInt4) {
    super.onLayout(paramBoolean, paramInt1, paramInt2, paramInt3, paramInt4);
    paramInt1 = 0;
    this.mIsLayoutDirty = false;
    View view = this.mChildToScrollTo;
    if (view != null && isViewDescendantOf(view, (View)this))
      scrollToChild(this.mChildToScrollTo); 
    this.mChildToScrollTo = null;
    if (!this.mIsLaidOut) {
      if (this.mSavedState != null) {
        scrollTo(getScrollX(), this.mSavedState.scrollPosition);
        this.mSavedState = null;
      } 
      if (getChildCount() > 0) {
        view = getChildAt(0);
        FrameLayout.LayoutParams layoutParams = (FrameLayout.LayoutParams)view.getLayoutParams();
        paramInt1 = view.getMeasuredHeight() + layoutParams.topMargin + layoutParams.bottomMargin;
      } 
      int i = getPaddingTop();
      int j = getPaddingBottom();
      paramInt3 = getScrollY();
      paramInt1 = clamp(paramInt3, paramInt4 - paramInt2 - i - j, paramInt1);
      if (paramInt1 != paramInt3)
        scrollTo(getScrollX(), paramInt1); 
    } 
    scrollTo(getScrollX(), getScrollY());
    this.mIsLaidOut = true;
  }
  
  protected void onMeasure(int paramInt1, int paramInt2) {
    super.onMeasure(paramInt1, paramInt2);
    if (!this.mFillViewport)
      return; 
    if (View.MeasureSpec.getMode(paramInt2) == 0)
      return; 
    if (getChildCount() > 0) {
      View view = getChildAt(0);
      FrameLayout.LayoutParams layoutParams = (FrameLayout.LayoutParams)view.getLayoutParams();
      paramInt2 = view.getMeasuredHeight();
      int i = getMeasuredHeight() - getPaddingTop() - getPaddingBottom() - layoutParams.topMargin - layoutParams.bottomMargin;
      if (paramInt2 < i)
        view.measure(getChildMeasureSpec(paramInt1, getPaddingLeft() + getPaddingRight() + layoutParams.leftMargin + layoutParams.rightMargin, layoutParams.width), View.MeasureSpec.makeMeasureSpec(i, 1073741824)); 
    } 
  }
  
  public boolean onNestedFling(View paramView, float paramFloat1, float paramFloat2, boolean paramBoolean) {
    if (!paramBoolean) {
      dispatchNestedFling(0.0F, paramFloat2, true);
      fling((int)paramFloat2);
      return true;
    } 
    return false;
  }
  
  public boolean onNestedPreFling(View paramView, float paramFloat1, float paramFloat2) {
    return dispatchNestedPreFling(paramFloat1, paramFloat2);
  }
  
  public void onNestedPreScroll(View paramView, int paramInt1, int paramInt2, int[] paramArrayOfint) {
    onNestedPreScroll(paramView, paramInt1, paramInt2, paramArrayOfint, 0);
  }
  
  public void onNestedPreScroll(View paramView, int paramInt1, int paramInt2, int[] paramArrayOfint, int paramInt3) {
    dispatchNestedPreScroll(paramInt1, paramInt2, paramArrayOfint, (int[])null, paramInt3);
  }
  
  public void onNestedScroll(View paramView, int paramInt1, int paramInt2, int paramInt3, int paramInt4) {
    onNestedScrollInternal(paramInt4, 0, (int[])null);
  }
  
  public void onNestedScroll(View paramView, int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5) {
    onNestedScrollInternal(paramInt4, paramInt5, (int[])null);
  }
  
  public void onNestedScroll(View paramView, int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5, int[] paramArrayOfint) {
    onNestedScrollInternal(paramInt4, paramInt5, paramArrayOfint);
  }
  
  public void onNestedScrollAccepted(View paramView1, View paramView2, int paramInt) {
    onNestedScrollAccepted(paramView1, paramView2, paramInt, 0);
  }
  
  public void onNestedScrollAccepted(View paramView1, View paramView2, int paramInt1, int paramInt2) {
    this.mParentHelper.onNestedScrollAccepted(paramView1, paramView2, paramInt1, paramInt2);
    startNestedScroll(2, paramInt2);
  }
  
  protected void onOverScrolled(int paramInt1, int paramInt2, boolean paramBoolean1, boolean paramBoolean2) {
    super.scrollTo(paramInt1, paramInt2);
  }
  
  protected boolean onRequestFocusInDescendants(int paramInt, Rect paramRect) {
    int i;
    View view;
    if (paramInt == 2) {
      i = 130;
    } else {
      i = paramInt;
      if (paramInt == 1)
        i = 33; 
    } 
    if (paramRect == null) {
      view = FocusFinder.getInstance().findNextFocus((ViewGroup)this, null, i);
    } else {
      view = FocusFinder.getInstance().findNextFocusFromRect((ViewGroup)this, paramRect, i);
    } 
    return (view == null) ? false : (isOffScreen(view) ? false : view.requestFocus(i, paramRect));
  }
  
  protected void onRestoreInstanceState(Parcelable paramParcelable) {
    if (!(paramParcelable instanceof SavedState)) {
      super.onRestoreInstanceState(paramParcelable);
      return;
    } 
    SavedState savedState = (SavedState)paramParcelable;
    super.onRestoreInstanceState(savedState.getSuperState());
    this.mSavedState = savedState;
    requestLayout();
  }
  
  protected Parcelable onSaveInstanceState() {
    SavedState savedState = new SavedState(super.onSaveInstanceState());
    savedState.scrollPosition = getScrollY();
    return (Parcelable)savedState;
  }
  
  protected void onScrollChanged(int paramInt1, int paramInt2, int paramInt3, int paramInt4) {
    super.onScrollChanged(paramInt1, paramInt2, paramInt3, paramInt4);
    OnScrollChangeListener onScrollChangeListener = this.mOnScrollChangeListener;
    if (onScrollChangeListener != null)
      onScrollChangeListener.onScrollChange(this, paramInt1, paramInt2, paramInt3, paramInt4); 
  }
  
  protected void onSizeChanged(int paramInt1, int paramInt2, int paramInt3, int paramInt4) {
    super.onSizeChanged(paramInt1, paramInt2, paramInt3, paramInt4);
    View view = findFocus();
    if (view != null) {
      if (this == view)
        return; 
      if (isWithinDeltaOfScreen(view, 0, paramInt4)) {
        view.getDrawingRect(this.mTempRect);
        offsetDescendantRectToMyCoords(view, this.mTempRect);
        doScrollY(computeScrollDeltaToGetChildRectOnScreen(this.mTempRect));
      } 
    } 
  }
  
  public boolean onStartNestedScroll(View paramView1, View paramView2, int paramInt) {
    return onStartNestedScroll(paramView1, paramView2, paramInt, 0);
  }
  
  public boolean onStartNestedScroll(View paramView1, View paramView2, int paramInt1, int paramInt2) {
    return ((paramInt1 & 0x2) != 0);
  }
  
  public void onStopNestedScroll(View paramView) {
    onStopNestedScroll(paramView, 0);
  }
  
  public void onStopNestedScroll(View paramView, int paramInt) {
    this.mParentHelper.onStopNestedScroll(paramView, paramInt);
    stopNestedScroll(paramInt);
  }
  
  public boolean onTouchEvent(MotionEvent paramMotionEvent) {
    initVelocityTrackerIfNotExists();
    int i = paramMotionEvent.getActionMasked();
    if (i == 0)
      this.mNestedYOffset = 0; 
    MotionEvent motionEvent = MotionEvent.obtain(paramMotionEvent);
    motionEvent.offsetLocation(0.0F, this.mNestedYOffset);
    if (i != 0) {
      if (i != 1) {
        if (i != 2) {
          if (i != 3) {
            if (i != 5) {
              if (i == 6) {
                onSecondaryPointerUp(paramMotionEvent);
                this.mLastMotionY = (int)paramMotionEvent.getY(paramMotionEvent.findPointerIndex(this.mActivePointerId));
              } 
            } else {
              i = paramMotionEvent.getActionIndex();
              this.mLastMotionY = (int)paramMotionEvent.getY(i);
              this.mActivePointerId = paramMotionEvent.getPointerId(i);
            } 
          } else {
            if (this.mIsBeingDragged && getChildCount() > 0 && this.mScroller.springBack(getScrollX(), getScrollY(), 0, 0, 0, getScrollRange()))
              ViewCompat.postInvalidateOnAnimation((View)this); 
            this.mActivePointerId = -1;
            endDrag();
          } 
        } else {
          StringBuilder stringBuilder;
          int j = paramMotionEvent.findPointerIndex(this.mActivePointerId);
          if (j == -1) {
            stringBuilder = new StringBuilder();
            stringBuilder.append("Invalid pointerId=");
            stringBuilder.append(this.mActivePointerId);
            stringBuilder.append(" in onTouchEvent");
            Log.e("NestedScrollView", stringBuilder.toString());
          } else {
            int m = (int)stringBuilder.getY(j);
            int k = this.mLastMotionY - m;
            i = k;
            if (!this.mIsBeingDragged) {
              i = k;
              if (Math.abs(k) > this.mTouchSlop) {
                ViewParent viewParent = getParent();
                if (viewParent != null)
                  viewParent.requestDisallowInterceptTouchEvent(true); 
                this.mIsBeingDragged = true;
                if (k > 0) {
                  i = k - this.mTouchSlop;
                } else {
                  i = k + this.mTouchSlop;
                } 
              } 
            } 
            k = i;
            if (this.mIsBeingDragged) {
              i = k;
              if (dispatchNestedPreScroll(0, k, this.mScrollConsumed, this.mScrollOffset, 0)) {
                i = k - this.mScrollConsumed[1];
                this.mNestedYOffset += this.mScrollOffset[1];
              } 
              this.mLastMotionY = m - this.mScrollOffset[1];
              int n = getScrollY();
              m = getScrollRange();
              k = getOverScrollMode();
              if (k == 0 || (k == 1 && m > 0)) {
                k = 1;
              } else {
                k = 0;
              } 
              if (overScrollByCompat(0, i, 0, getScrollY(), 0, m, 0, 0, true) && !hasNestedScrollingParent(0))
                this.mVelocityTracker.clear(); 
              int i1 = getScrollY() - n;
              int[] arrayOfInt = this.mScrollConsumed;
              arrayOfInt[1] = 0;
              dispatchNestedScroll(0, i1, 0, i - i1, this.mScrollOffset, 0, arrayOfInt);
              i1 = this.mLastMotionY;
              arrayOfInt = this.mScrollOffset;
              this.mLastMotionY = i1 - arrayOfInt[1];
              this.mNestedYOffset += arrayOfInt[1];
              if (k != 0) {
                i -= this.mScrollConsumed[1];
                ensureGlows();
                k = n + i;
                if (k < 0) {
                  EdgeEffectCompat.onPull(this.mEdgeGlowTop, i / getHeight(), stringBuilder.getX(j) / getWidth());
                  if (!this.mEdgeGlowBottom.isFinished())
                    this.mEdgeGlowBottom.onRelease(); 
                } else if (k > m) {
                  EdgeEffectCompat.onPull(this.mEdgeGlowBottom, i / getHeight(), 1.0F - stringBuilder.getX(j) / getWidth());
                  if (!this.mEdgeGlowTop.isFinished())
                    this.mEdgeGlowTop.onRelease(); 
                } 
                EdgeEffect edgeEffect = this.mEdgeGlowTop;
                if (edgeEffect != null && (!edgeEffect.isFinished() || !this.mEdgeGlowBottom.isFinished()))
                  ViewCompat.postInvalidateOnAnimation((View)this); 
              } 
            } 
          } 
        } 
      } else {
        velocityTracker = this.mVelocityTracker;
        velocityTracker.computeCurrentVelocity(1000, this.mMaximumVelocity);
        i = (int)velocityTracker.getYVelocity(this.mActivePointerId);
        if (Math.abs(i) >= this.mMinimumVelocity) {
          i = -i;
          float f = i;
          if (!dispatchNestedPreFling(0.0F, f)) {
            dispatchNestedFling(0.0F, f, true);
            fling(i);
          } 
        } else if (this.mScroller.springBack(getScrollX(), getScrollY(), 0, 0, 0, getScrollRange())) {
          ViewCompat.postInvalidateOnAnimation((View)this);
        } 
        this.mActivePointerId = -1;
        endDrag();
      } 
    } else {
      if (getChildCount() == 0)
        return false; 
      int j = this.mScroller.isFinished() ^ true;
      this.mIsBeingDragged = j;
      if (j != 0) {
        ViewParent viewParent = getParent();
        if (viewParent != null)
          viewParent.requestDisallowInterceptTouchEvent(true); 
      } 
      if (!this.mScroller.isFinished())
        abortAnimatedScroll(); 
      this.mLastMotionY = (int)velocityTracker.getY();
      this.mActivePointerId = velocityTracker.getPointerId(0);
      startNestedScroll(2, 0);
    } 
    VelocityTracker velocityTracker = this.mVelocityTracker;
    if (velocityTracker != null)
      velocityTracker.addMovement(motionEvent); 
    motionEvent.recycle();
    return true;
  }
  
  boolean overScrollByCompat(int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5, int paramInt6, int paramInt7, int paramInt8, boolean paramBoolean) {
    boolean bool1;
    int k = getOverScrollMode();
    int i = computeHorizontalScrollRange();
    int j = computeHorizontalScrollExtent();
    boolean bool2 = false;
    if (i > j) {
      i = 1;
    } else {
      i = 0;
    } 
    if (computeVerticalScrollRange() > computeVerticalScrollExtent()) {
      j = 1;
    } else {
      j = 0;
    } 
    if (k == 0 || (k == 1 && i != 0)) {
      i = 1;
    } else {
      i = 0;
    } 
    if (k == 0 || (k == 1 && j != 0)) {
      j = 1;
    } else {
      j = 0;
    } 
    paramInt3 += paramInt1;
    if (i == 0) {
      paramInt1 = 0;
    } else {
      paramInt1 = paramInt7;
    } 
    paramInt4 += paramInt2;
    if (j == 0) {
      paramInt2 = 0;
    } else {
      paramInt2 = paramInt8;
    } 
    paramInt7 = -paramInt1;
    paramInt1 += paramInt5;
    paramInt5 = -paramInt2;
    paramInt2 += paramInt6;
    if (paramInt3 > paramInt1) {
      paramBoolean = true;
    } else if (paramInt3 < paramInt7) {
      paramBoolean = true;
      paramInt1 = paramInt7;
    } else {
      paramBoolean = false;
      paramInt1 = paramInt3;
    } 
    if (paramInt4 > paramInt2) {
      bool1 = true;
    } else if (paramInt4 < paramInt5) {
      bool1 = true;
      paramInt2 = paramInt5;
    } else {
      bool1 = false;
      paramInt2 = paramInt4;
    } 
    if (bool1 && !hasNestedScrollingParent(1))
      this.mScroller.springBack(paramInt1, paramInt2, 0, 0, 0, getScrollRange()); 
    onOverScrolled(paramInt1, paramInt2, paramBoolean, bool1);
    if (!paramBoolean) {
      paramBoolean = bool2;
      return bool1 ? true : paramBoolean;
    } 
    return true;
  }
  
  public boolean pageScroll(int paramInt) {
    int i;
    if (paramInt == 130) {
      i = 1;
    } else {
      i = 0;
    } 
    int j = getHeight();
    if (i) {
      this.mTempRect.top = getScrollY() + j;
      i = getChildCount();
      if (i > 0) {
        View view = getChildAt(i - 1);
        FrameLayout.LayoutParams layoutParams = (FrameLayout.LayoutParams)view.getLayoutParams();
        i = view.getBottom() + layoutParams.bottomMargin + getPaddingBottom();
        if (this.mTempRect.top + j > i)
          this.mTempRect.top = i - j; 
      } 
    } else {
      this.mTempRect.top = getScrollY() - j;
      if (this.mTempRect.top < 0)
        this.mTempRect.top = 0; 
    } 
    Rect rect = this.mTempRect;
    rect.bottom = rect.top + j;
    return scrollAndFocus(paramInt, this.mTempRect.top, this.mTempRect.bottom);
  }
  
  public void requestChildFocus(View paramView1, View paramView2) {
    if (!this.mIsLayoutDirty) {
      scrollToChild(paramView2);
    } else {
      this.mChildToScrollTo = paramView2;
    } 
    super.requestChildFocus(paramView1, paramView2);
  }
  
  public boolean requestChildRectangleOnScreen(View paramView, Rect paramRect, boolean paramBoolean) {
    paramRect.offset(paramView.getLeft() - paramView.getScrollX(), paramView.getTop() - paramView.getScrollY());
    return scrollToChildRect(paramRect, paramBoolean);
  }
  
  public void requestDisallowInterceptTouchEvent(boolean paramBoolean) {
    if (paramBoolean)
      recycleVelocityTracker(); 
    super.requestDisallowInterceptTouchEvent(paramBoolean);
  }
  
  public void requestLayout() {
    this.mIsLayoutDirty = true;
    super.requestLayout();
  }
  
  public void scrollTo(int paramInt1, int paramInt2) {
    if (getChildCount() > 0) {
      View view = getChildAt(0);
      FrameLayout.LayoutParams layoutParams = (FrameLayout.LayoutParams)view.getLayoutParams();
      int i2 = getWidth();
      int i3 = getPaddingLeft();
      int i4 = getPaddingRight();
      int i5 = view.getWidth();
      int i6 = layoutParams.leftMargin;
      int i7 = layoutParams.rightMargin;
      int i = getHeight();
      int j = getPaddingTop();
      int k = getPaddingBottom();
      int m = view.getHeight();
      int n = layoutParams.topMargin;
      int i1 = layoutParams.bottomMargin;
      paramInt1 = clamp(paramInt1, i2 - i3 - i4, i5 + i6 + i7);
      paramInt2 = clamp(paramInt2, i - j - k, m + n + i1);
      if (paramInt1 != getScrollX() || paramInt2 != getScrollY())
        super.scrollTo(paramInt1, paramInt2); 
    } 
  }
  
  public void setFillViewport(boolean paramBoolean) {
    if (paramBoolean != this.mFillViewport) {
      this.mFillViewport = paramBoolean;
      requestLayout();
    } 
  }
  
  public void setNestedScrollingEnabled(boolean paramBoolean) {
    this.mChildHelper.setNestedScrollingEnabled(paramBoolean);
  }
  
  public void setOnScrollChangeListener(OnScrollChangeListener paramOnScrollChangeListener) {
    this.mOnScrollChangeListener = paramOnScrollChangeListener;
  }
  
  public void setSmoothScrollingEnabled(boolean paramBoolean) {
    this.mSmoothScrollingEnabled = paramBoolean;
  }
  
  public boolean shouldDelayChildPressedState() {
    return true;
  }
  
  public final void smoothScrollBy(int paramInt1, int paramInt2) {
    smoothScrollBy(paramInt1, paramInt2, 250, false);
  }
  
  public final void smoothScrollBy(int paramInt1, int paramInt2, int paramInt3) {
    smoothScrollBy(paramInt1, paramInt2, paramInt3, false);
  }
  
  public final void smoothScrollTo(int paramInt1, int paramInt2) {
    smoothScrollTo(paramInt1, paramInt2, 250, false);
  }
  
  public final void smoothScrollTo(int paramInt1, int paramInt2, int paramInt3) {
    smoothScrollTo(paramInt1, paramInt2, paramInt3, false);
  }
  
  void smoothScrollTo(int paramInt1, int paramInt2, int paramInt3, boolean paramBoolean) {
    smoothScrollBy(paramInt1 - getScrollX(), paramInt2 - getScrollY(), paramInt3, paramBoolean);
  }
  
  void smoothScrollTo(int paramInt1, int paramInt2, boolean paramBoolean) {
    smoothScrollTo(paramInt1, paramInt2, 250, paramBoolean);
  }
  
  public boolean startNestedScroll(int paramInt) {
    return startNestedScroll(paramInt, 0);
  }
  
  public boolean startNestedScroll(int paramInt1, int paramInt2) {
    return this.mChildHelper.startNestedScroll(paramInt1, paramInt2);
  }
  
  public void stopNestedScroll() {
    stopNestedScroll(0);
  }
  
  public void stopNestedScroll(int paramInt) {
    this.mChildHelper.stopNestedScroll(paramInt);
  }
  
  static class AccessibilityDelegate extends AccessibilityDelegateCompat {
    public void onInitializeAccessibilityEvent(View param1View, AccessibilityEvent param1AccessibilityEvent) {
      boolean bool;
      super.onInitializeAccessibilityEvent(param1View, param1AccessibilityEvent);
      NestedScrollView nestedScrollView = (NestedScrollView)param1View;
      param1AccessibilityEvent.setClassName(ScrollView.class.getName());
      if (nestedScrollView.getScrollRange() > 0) {
        bool = true;
      } else {
        bool = false;
      } 
      param1AccessibilityEvent.setScrollable(bool);
      param1AccessibilityEvent.setScrollX(nestedScrollView.getScrollX());
      param1AccessibilityEvent.setScrollY(nestedScrollView.getScrollY());
      AccessibilityRecordCompat.setMaxScrollX((AccessibilityRecord)param1AccessibilityEvent, nestedScrollView.getScrollX());
      AccessibilityRecordCompat.setMaxScrollY((AccessibilityRecord)param1AccessibilityEvent, nestedScrollView.getScrollRange());
    }
    
    public void onInitializeAccessibilityNodeInfo(View param1View, AccessibilityNodeInfoCompat param1AccessibilityNodeInfoCompat) {
      super.onInitializeAccessibilityNodeInfo(param1View, param1AccessibilityNodeInfoCompat);
      NestedScrollView nestedScrollView = (NestedScrollView)param1View;
      param1AccessibilityNodeInfoCompat.setClassName(ScrollView.class.getName());
      if (nestedScrollView.isEnabled()) {
        int i = nestedScrollView.getScrollRange();
        if (i > 0) {
          param1AccessibilityNodeInfoCompat.setScrollable(true);
          if (nestedScrollView.getScrollY() > 0) {
            param1AccessibilityNodeInfoCompat.addAction(AccessibilityNodeInfoCompat.AccessibilityActionCompat.ACTION_SCROLL_BACKWARD);
            param1AccessibilityNodeInfoCompat.addAction(AccessibilityNodeInfoCompat.AccessibilityActionCompat.ACTION_SCROLL_UP);
          } 
          if (nestedScrollView.getScrollY() < i) {
            param1AccessibilityNodeInfoCompat.addAction(AccessibilityNodeInfoCompat.AccessibilityActionCompat.ACTION_SCROLL_FORWARD);
            param1AccessibilityNodeInfoCompat.addAction(AccessibilityNodeInfoCompat.AccessibilityActionCompat.ACTION_SCROLL_DOWN);
          } 
        } 
      } 
    }
    
    public boolean performAccessibilityAction(View param1View, int param1Int, Bundle param1Bundle) {
      if (super.performAccessibilityAction(param1View, param1Int, param1Bundle))
        return true; 
      NestedScrollView nestedScrollView = (NestedScrollView)param1View;
      if (!nestedScrollView.isEnabled())
        return false; 
      if (param1Int != 4096)
        if (param1Int != 8192 && param1Int != 16908344) {
          if (param1Int != 16908346)
            return false; 
        } else {
          param1Int = nestedScrollView.getHeight();
          int k = nestedScrollView.getPaddingBottom();
          int m = nestedScrollView.getPaddingTop();
          param1Int = Math.max(nestedScrollView.getScrollY() - param1Int - k - m, 0);
          if (param1Int != nestedScrollView.getScrollY()) {
            nestedScrollView.smoothScrollTo(0, param1Int, true);
            return true;
          } 
          return false;
        }  
      param1Int = nestedScrollView.getHeight();
      int i = nestedScrollView.getPaddingBottom();
      int j = nestedScrollView.getPaddingTop();
      param1Int = Math.min(nestedScrollView.getScrollY() + param1Int - i - j, nestedScrollView.getScrollRange());
      if (param1Int != nestedScrollView.getScrollY()) {
        nestedScrollView.smoothScrollTo(0, param1Int, true);
        return true;
      } 
      return false;
    }
  }
  
  public static interface OnScrollChangeListener {
    void onScrollChange(NestedScrollView param1NestedScrollView, int param1Int1, int param1Int2, int param1Int3, int param1Int4);
  }
  
  static class SavedState extends View.BaseSavedState {
    public static final Parcelable.Creator<SavedState> CREATOR = new Parcelable.Creator<SavedState>() {
        public NestedScrollView.SavedState createFromParcel(Parcel param2Parcel) {
          return new NestedScrollView.SavedState(param2Parcel);
        }
        
        public NestedScrollView.SavedState[] newArray(int param2Int) {
          return new NestedScrollView.SavedState[param2Int];
        }
      };
    
    public int scrollPosition;
    
    SavedState(Parcel param1Parcel) {
      super(param1Parcel);
      this.scrollPosition = param1Parcel.readInt();
    }
    
    SavedState(Parcelable param1Parcelable) {
      super(param1Parcelable);
    }
    
    public String toString() {
      StringBuilder stringBuilder = new StringBuilder();
      stringBuilder.append("HorizontalScrollView.SavedState{");
      stringBuilder.append(Integer.toHexString(System.identityHashCode(this)));
      stringBuilder.append(" scrollPosition=");
      stringBuilder.append(this.scrollPosition);
      stringBuilder.append("}");
      return stringBuilder.toString();
    }
    
    public void writeToParcel(Parcel param1Parcel, int param1Int) {
      super.writeToParcel(param1Parcel, param1Int);
      param1Parcel.writeInt(this.scrollPosition);
    }
  }
  
  class null implements Parcelable.Creator<SavedState> {
    public NestedScrollView.SavedState createFromParcel(Parcel param1Parcel) {
      return new NestedScrollView.SavedState(param1Parcel);
    }
    
    public NestedScrollView.SavedState[] newArray(int param1Int) {
      return new NestedScrollView.SavedState[param1Int];
    }
  }
}


/* Location:              C:\Users\Student\Desktop\APK Decompiling\com.qualcomm.ftcdriverstation_38_apps.evozi.com\classes-dex2jar.jar!\androidx\core\widget\NestedScrollView.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */