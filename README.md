SShadow

![效果图](https://github.com/android-open-libs/SShadow/blob/main/demo.jpg)

#### How to

To get a Git project into your build:

Step 1. Add the JitPack repository to your build file

Add it in your root build.gradle at the end of repositories:

```
allprojects {
    repositories {
        ...
        maven { url 'https://jitpack.io' }
    }
}
```

Step 2. Add the dependency

```
dependencies {
    implementation 'com.github.android-open-libs:SShadow:v1.0.1'
}
```


#### 效果一

采用 `library/src/main/res/drawable/shadow_gradual_bg.xml` 设置为背景

#### 效果二

```
<sing.shadow.ShadowLayout
    android:layout_width="wrap_content"
    android:layout_height="wrap_content"
    app:yc_bottomShow="true"
    app:yc_cornerRadius="12dp"
    app:yc_dx="0dp"
    app:yc_dy="0dp"
    app:yc_leftShow="true"
    app:yc_rightShow="true"
    app:yc_shadowColor="#5500ff11"
    app:yc_shadowLimit="12dp"
    app:yc_topShow="true">

</sing.shadow.ShadowLayout>
```

属性 | 说明
-- | --
yc_cornerRadius | 阴影的圆角大小
yc_shadowLimit | 阴影的扩散范围(也可以理解为扩散程度)
yc_shadowColor | 阴影颜色
yc_dx | x轴的偏移量
yc_dy | y轴的偏移量
yc_leftShow | 左边是否显示阴影
yc_rightShow | 右边是否显示阴影
yc_topShow | 上边是否显示阴影
yc_bottomShow | 下面是否显示阴影

#### 效果三

```
<sing.shadow.ShadowView
    android:id="@+id/shadow_view"
    android:layout_width="match_parent"
    android:layout_height="wrap_content"
    android:layout_margin="12dp"
    android:foreground="?attr/selectableItemBackground"
    app:shadowMargin="0dp"
    app:shadowMarginTop="0dp"
    app:shadowMarginLeft="0dp"
    app:shadowMarginRight="0dp"
    app:shadowMarginBottom="0dp"
    app:cornerRadius="10dp"
    app:cornerRadiusTL="0dp"
    app:cornerRadiusTR="0dp"
    app:cornerRadiusBL="0dp"
    app:cornerRadiusBR="0dp"
    app:foregroundColor="@color/white"
    app:shadowColor="#123123"
    app:shadowDx="0.0"
    app:shadowDy="0.0"
    app:shadowRadius="10dp"
    app:backgroundColor="@color/white">

    ...

</sing.shadow.ShadowView>
```

属性 | 说明
-- | --
shadowMargin | 阴影四周边距
shadowMarginTop | 阴影上边边距
shadowMarginLeft | 阴影左边边距
shadowMarginRight | 阴影右边边距
shadowMarginBottom | 阴影下边边距
cornerRadius | 内容四周弧度
cornerRadiusTL | 内容上左弧度
cornerRadiusTR | 内容上右弧度
cornerRadiusBL | 内容下左弧度
cornerRadiusBR | 内容下右弧度
foregroundColor | 前景颜色
shadowColor | 阴影颜色
shadowDx | x轴的偏移量
shadowDy | y轴的偏移量
shadowRadius | 阴影弧度
backgroundColor | 背景颜色

也可以用代码设置

```kotlin
shadowCardView.setShadowRadius(20F)
shadowCardView.setShadowMargin(20, 20, 20, 20)
shadowCardView.setShadowMarginLeft(20)
shadowCardView.setShadowMarginTop(20)
shadowCardView.setShadowMarginRight(20)
shadowCardView.setShadowMarginBottom(20)
shadowCardView.setCornerRadius(20, 20, 20, 20)
shadowCardView.setCornerRadiusTL(20F)
shadowCardView.setCornerRadiusTR(20F)
shadowCardView.setCornerRadiusBR(20F)
shadowCardView.setCornerRadiusBL(20F)
shadowCardView.setShadowDx(20F)
shadowCardView.setShadowDy(20F)
shadowCardView.setShadowColor(Color.parseColor("#f44336"))
shadowCardView.setForegroundColor(Color.parseColor("#f44336"))
shadowCardView.setBackgroundColor(Color.parseColor("#f44336"))
```
