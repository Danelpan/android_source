基础开发框架包：
1 介绍
  这是一个快速开发android基础应用的框架包，该框架的的目的是提高软件开发速度，从而不去关心里面一些
  基础的实现过程。该框架中，包含了一个基础BaseActivity和BaseFragment、联网请求、图片加载缓存，以及
  一些基础的工具类，可以自行的区发现
  
2 BaseActivity
  BaseActivity是一个继承于FragmentActivity，也就是当开发版本小于4.0，那么必须引入扩展包。
  在该基类中，引入了一个基于activity生命周期的线程队列，所有线程的创建过程跟随其中，销毁时，该activity
  创建的线程相应的会被销毁
  
3 BaseFragment同BaseActivity

4 联网请求
  在该框架源码包中得联网是一个同步过程，使用它，必须自行创建线程，具体的线程使用可参见BaseActivity
  
5 图片缓存
  该图片缓存，扩展了图片存储和加载过程，该过程中是透明的，具体的使用过程参见其配置方法

6 工具类
  包含了Bitmap工具类，文件工具类，缓存工具类
