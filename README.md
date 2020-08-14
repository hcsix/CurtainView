# CurtainView Android自定义窗帘布局

特性
===

1.支持单页窗帘（左、右方向）和双页窗帘的显示；

2.支持设置进度动画执行的持续时间；



集成
===

第 1 步、在工程的 build.gradle 中添加：

```
	allprojects {
		repositories {
			...
			jcenter()
		}
	}
```
第 2 步、在应用的  build.gradle 中添加：

```
	dependencies {
	        implementation 'com.supcoder:curtain:1.0.0'
	}
```
