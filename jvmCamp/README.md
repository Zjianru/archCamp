## 内容
> 作业

1.自己写一个简单的 HelloNum.java，里面需要涉及基本类型，四则运行，if 和 for，然后自己分析一下对应的字节码

2.自定义一个 Classloader，加载一个 Hello.xlass 文件，执行 hello 方法，此文件内容是一个 Hello.class 文件所有字节（x=255-x）处理后的文件

3.画一张图，展示 Xmx、Xms、Xmn、Meta、DirectMemory、Xss 这些内存参数的关系

4.检查一下自己维护的业务系统的 JVM 参数配置，用 jstat 和 jstack、jmap 查看一下详情，并且自己独立分析一下大概情况，思考有没有不合理的地方，如何改进

5.本机使用 G1 GC 启动一个程序，仿照课上案例分析一下 JVM 情况


## 操作步骤


### 1

1. 编写代码
2. 编译代码, 执行命令： `javac -g HelloNum.java`
3. 查看反编译的代码
   1. 可以安装并使用 idea 的 jclasslib 插件, 选中文件, 选择 `View --> Show Bytecode With jclasslib` 即可
   2. 或者直接通过命令行工具 javap, 执行命令: `javap -c -v -p -l HelloNum.class`
4.  分析相关的字节码

### 3

- Xms 设置堆内存的初始值
- Xmx 设置堆内存的最大值
- Xmn 设置堆内存中的年轻代的最大值
- Meta 区不属于堆内存, 归属为非堆
- DirectMemory 直接内存, 属于 JVM 内存中开辟出来的本地内存空间。
- Xss设置的是单个线程栈的最大空间;

JVM进程空间中的内存一般来说包括以下这些部分:

- 堆内存(Xms ~ Xmx) = 年轻代(~Xmn) + 老年代
- 非堆 = Meta + CodeCache + ...
- Native内存 = 直接内存 + Native + ...
- 栈内存 = n * Xss

另外，注意区分规范与实现的区别, 需要根据具体实现以及版本, 才能确定。 一般来说，我们的目的是为了排查故障和诊断问题，大致弄清楚这些参数和空间的关系即可。 具体设置时还需要留一些冗余量。


### 4.

这个是具体案例分析, 请各位同学自己分析。

比如我们一个生产系统应用的启动参数为:

```
JAVA_OPTS=-Xmx200g -Xms200g -XX:+UnlockExperimentalVMOptions -XX:+UseZGC -XX:ZCollectionInterval=30 -XX:ZAllocationSpikeTolerance=5 -XX:ReservedCodeCacheSize=2g -XX:InitialCodeCacheSize=2g -XX:ConcGCThreads=8 -XX:ParallelGCThreads=16
```

另一个系统的启动参数为:

```
JAVA_OPTS=-Xmx4g -Xms4g -XX:+UseG1GC -XX:MaxGCPauseMillis=50
```

具体如何设置, 需要考虑的因素包括:

- 系统容量: 业务规模, 并发, 成本预算; 需要兼顾性能与成本;
- 延迟要求: 最坏情况下能接受多少时间的延迟尖刺。
- 吞吐量:  根据业务特征来确定, 比如, 网关, 大数据底层平台, 批处理作业系统, 在线实时应用, 他们最重要的需求不一样。
- 系统架构: 比如拆分为小内存更多节点, 还是大内存少量节点。
- 其他...


### 5.

例如使用以下命令:

```
# 编译
javac -g GCLogAnalysis.java
# JDK8 启动程序
java -Xmx2g -Xms2g -XX:+UseG1GC -verbose:gc -XX:+PrintGCDateStamps -XX:+PrintGCDetails -Xloggc:gc.log  GCLogAnalysis
```

尝试使用课程中介绍的各种工具JDK命令行和图形工具来进行分析。

其中 [GCLogAnalysis.java](src/main/java/com/cz/camp/jvm/GCLogAnalysis.java) 文件

## 几个命令用法
### 1、十六进制方式查看文件
`hexdump -C Hello.class` 
输出：`00000000  ca fe ba be 00 00 00 34  00 1c 0a 00 06 00 0e 09`

可以看到magic number： `cafe babe`，
以及`00 00 00 34`，十六进制34=十进制3*16+4=52，这是jdk8，如果是jdk11则是55，十六进制37.

### 2、Base64方式编码文件
`base64 Hello.class`
### 3、显示JVM默认参数
```
java -XX:+PrintFlagsFinal -version 

java -XX:+PrintFlagsFinal -version | grep -F " Use" | grep -F "GC "

java -XX:+PrintFlagsFinal -version | grep MaxNewSize

```

### 4、切换不同jdk
```
jenv shell 1.8
jenv shell 11
```
显示所有jdk
```
jenv versions
```

## 更多资料

更多中英文的技术文章和参考资料: <https://github.com/cncounter/translation>

