0. �Ȱ�װNetworkTest_Sign_mstar.apk����Ѿ�ǩ���ģ����Թ����ܷ�����������������Ͱ����²���ǩ����apk

1. ����apk��Ҫ����ϵͳ����Ҫ����ϵͳǩ����ǩ����ʽ���£�

1.1 �ҳ�ϵͳǩ����Կ
    ϵͳ��ԿΪ��platform.pk8��platform.x509.pem
    ·���� build\target\product\security

1.2 �ҳ�ϵͳǩ������
    ����Ϊ��signApk.jar 
    ·����/out/host/linux-x86/framework/ signApk.jar

1.3 ��ʼǩ��
    ����1��2���ҵ�����ǩ��Ӧ�á�platform.pk8��platform.x509.pem��signApk.jar�ŵ��ļ����£����е�����ļ���mstarƽ̨�ģ��滻���ɡ�
    �� dos �������棬�����Ŀ¼����F:\NetworkTestSignAPK���������
java -jar signapk.jar platform.x509.pem platform.pk8 NetworkTest.apk NetworkTest_Sign.apk 

1.4 ִ���������õ�ǩ��apk��NetworkTest_Sign.apk

ע���ļ����µ�����apk
NetworkTest.apk --- ��δǩ����apk���ŵ�ƽ̨���л���������ϵͳ�ģ�
NetworkTest_Sign_mstar.apk --- ����mstarƽ̨ǩ�����ģ�����mstarϵͳ����������