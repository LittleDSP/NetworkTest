0. 先安装NetworkTest_Sign_mstar.apk这个已经签名的，试试功能能否正常，如果不正常就按以下步骤签名下apk

1. 由于apk需要重启系统，需要加入系统签名，签名方式如下：

1.1 找出系统签名密钥
    系统密钥为：platform.pk8和platform.x509.pem
    路径： build\target\product\security

1.2 找出系统签名工具
    工具为：signApk.jar 
    路径：/out/host/linux-x86/framework/ signApk.jar

1.3 开始签名
    将第1、2步找到的无签名应用、platform.pk8、platform.x509.pem和signApk.jar放到文件夹下，现有的相关文件是mstar平台的，替换即可。
    打开 dos 操作界面，进入该目录，如F:\NetworkTestSignAPK，输入命令：
java -jar signapk.jar platform.x509.pem platform.pk8 NetworkTest.apk NetworkTest_Sign.apk 

1.4 执行完命令后得到签名apk：NetworkTest_Sign.apk

注：文件夹下的两个apk
NetworkTest.apk --- 是未签名的apk，放到平台运行会重启不了系统的；
NetworkTest_Sign_mstar.apk --- 是在mstar平台签过名的，放在mstar系统上运行正常