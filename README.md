# Introduction #
I have implemented this **Zabbix monitoring plug-in** for **Standard and Enterprise (J2EE)Java applications, and Java application Servers(Weblogic, JBoss etc..)**  using **Apache Log4j** logging framework to transmit J2EE Application server/Webapp log exceptions   and errors to Zabbix monitoring systems. As a result, Administrators or developers will be able to know about their applications issues on production without having to dig into the logs; in addition actions can be taken by implementing triggers to notify critical errors based on log message string patterns. In fact, Log4j is an open source framework which separates log error layout pattern(message content) from their destination(Appender) that may be the standard output(Console: stdout), File(text file) or   a remote socket. In other words, error messages can be formatted independently from their destination.

# Log4J Details and Zabbix Appender Context #
In fact, Log4j allows to filter error messages according to their error level : **information, warning, error, fatal and log**. As a result, messages pattern may be different for distinct error levels and destinations which are the **appenders**.
Log4j has some ready-to-use appenders like Console, File and Socket appenders. But, it has a pluggable interface so that anyone can develop its own appender to process log messages and take actions upon specific needs.

Hence, I develop a Log4J appender that processes log messages from a java applications in order to be able to send it to a **Zabbix Server**. **Zabbix** is an opensource enterprise class and distributed monitoring system with a lot of features, ease of configuration/deployment and monitoring templates for Linux, Solaris, Windows, MacOS X,HP Printers, Some Cisco Routers, Network Switches and so forth.

Therefore, with the help of this appender any infrastructure using Zabbix as their main monitoring systems can be warned in real time about errors from java applications one would need to monitor without going through the logs for troubleshooting. This appender is called ZabbixAppender and distributed under GNU Public Licence version 2 and can be used freely according the licence terms.

# Zabbix Appender Requirements #
We assume you have a full and functional installation of Zabbix monitoring system to be able to Proceed. Otherwise, this plugin won't be usable. In addition, make sure that you have already your monitoring hosts created in Zabbix PHP Web Frontend. Moreover, you should have **JRE(Java Runtime Environment)** properly setup. Most current Linux distribution have a **JRE** configured by default. For windows, you may be required to download Java Runtime Environment(JRE) from the official web site of Oracle knowing that you already have a java application(Servlet, Standard Java Application, Weblogic Server, JBOSS ) properly setup to work with Log4J with a **property** or **XML** file for this purpose.

## Summary of Requirements ##
The summary of the required components needed by Zabbix Appender plug-in:
  * **Zabbix Server**
  * **JRE(Java Runtime Environment)**
  * **Java Application** with **Log4j** enabled via a **XML** or **Property** file

# Zabbix Appender Configuration #
Some works around are needed before having zabbix running. First of all, Zabbix Appender plugin is a Zabbix Client, that is, Zabbix communication protocol is implemented inside in order to be able to send java log4j errors(monitoring data) captured from remote hosts to Zabbix server which will then make it viewable instantly in Zabbix Monitoring Dashboards.

Zabbix has two monitoring modes
  * **Passive mode** : Zabbix Server collects monitoring information from Zabbix Agents(Push Mode)
  * **Active Mode**  : Zabbix Agent sends the monitoring data to the Server asynchronously without any request(Pull Mode) from it.

Zabbix Appender works in active mode because it delevers itself the monitoring data
without any request from Zabbix Server. And it sends logs errors to Zabbix Server at the instant time they are captured by Log4j logging framework.

First of all, you will have to create an Monitoring Item for a host in Zabbix Frontend. You have to remember the monitoring Item created. For example you can create a monitoring Item in Zabbix Frontend with : **CONFIGURATION-->HOST**. Then click on **ITEM**, then on **CREATE NEW ITEM**. Afterward, enter a name for this Item(Ex: java.logerror). And, on the **AGENT TYPE**, select **Active Agent** from the list. Then on the Item Type, click the drop downlist , choose Log for the newly created item type. After these step, only Log4j Configuration is needed to finalize the setup of Zabbix Appender Plugin.

This step is mandatory for Zabbix Appender plug-in to work.As a consequence, you have to configure Log4j properties or XML file. For clarification purpose of explanation, we use a log4j property file. The same is also usable with an XML with appropriate entries. As a result, configure the entries as follow by replacing the value according to your Zabbix installation and networking configs in **log4j.properties** file:

```
log4j.rootLogger = DEBUG, FILE, CONSOLE, REMOTE
log4j.appender.FILE = org.apache.log4j.FileAppender
log4j.appender.FILE.file = log.txt
log4j.appender.FILE.layout = org.apache.log4j.PatternLayout
log4j.appender.FILE.layout.ConversionPattern=key.log [%d{MMM dd HH:mm:ss}] %-5p (%F:%L) - %m%n
log4j.appender.CONSOLE = org.apache.log4j.ConsoleAppender
log4j.appender.CONSOLE.layout = org.apache.log4j.PatternLayout
log4j.appender.CONSOLE.layout.ConversionPattern=[%d{MMM dd HH:mm:ss}] %-5p (%F:%L) - %m%n_
```


**Configurations needed by Log4J to attach Zabbix Appender to your Log4J Installation**
```
log4j.appender.REMOTE = zabbix.agent.ZabbixAppender
log4j.appender.REMOTE.layout = org.apache.log4j.PatternLayout
log4j.appender.REMOTE.layout.ConversionPattern = [%d{MMM dd HH:mm:ss}] Zabbix_Appender - %m%n
log4j.appender.REMOTE.host = 192.168.0.11
log4j.appender.REMOTE.port = 10051
log4j.appender.REMOTE.agent=weblogic_server
log4j.appender.REMOTE.key = key.syslog
log4j.appender.REMOTE.domain = Weblogic_Domain
```

Meaning of mandatory parameters needed in the **log4j.properties** file for Zabbix Appender:

  * **log4j.appender.REMOTE=zabbix.agent.ZabbixAppender** : Zabbix Agent lass file in the the Jar file downloaded. You must configure your classpath to include the path of the Jar file. Put the jar file in the classpath environment variable.In Linux with **export CLASSPATH=$CLASSPATH:ZABBIXAPPENDER\_FULLPATH** or in Windows with System properties and Advanced Properties, then modify **CLASSPATH** entry to the full path name of  ZabbixAppender location in the filesystem.
  * **log4j.appender.REMOTE.host**  : Zabbix server Host IP address or DNS name.
  * **log4j.appender.REMOTE.port=10051** : the port on which Zabbix Server daemon is listening. By default, it's 10051.
  * **log4j.appender.REMOTE.agent=weblogic\_server** : The hostname as it was configured  the Zabbix Webfrontend in the liste of host. This name is used by Zabbix server to be able to identify the issuer of the monitoring data. In other words, it is the hostname created in zabbix webfrontend. The name should be the same as the one in Zabbix Webfrontend.
  * **log4j.appender.REMOTE.key=java.logerror** : This is the item created to hold that error messages for the host. This item will be viewed in the monitoring screen for this host. And the log message will be shown.
  * **log4j.appender.REMOTE.domain=Weblogic\_Domain** : this parameter is optional and can be omitted. You can use it to specify manually the domain from which the error provides. This is for Weblogic Server monitoring purpose as a regard to Weblogic Administrators.


Then you need to restart the Standard Java Application or Java Application Server(Weblogic Server, JBOSS) so that it can reload **log4j.properties** file in memory.
Then you can try to  trigger some errors and then login to Zabbix screen to monitor the special item entry created for java application log errors monitoring. If it doesn't work, try to look if your classpath is configured with the full pathname of ZabbixAppender you downloaded. Therefore, review previous steps and configurations.

# Conclusion and Zabbix Critics #
If your installation works successfully, then you will be able to view log errors from java applications or java application server the time they are generated without having to browse log file for specific entries. This eases troubleshooting and helps to be proactive in system troubleshooting because we are aware of application misbehaviour at the exact time. I hope that you found it useful. For your information, Zabbix doesn't use any encryption mechanism in its application protocol, if you want to secure your monitoring data against sniffing or spoofing, you should can use **Openvpn** in both zabbix server and zabbix agents so that traffic are routed through an encrypted and authenticated channel via a virtual network interface(tun0 or tap0). As a result, monitoring will be protected from sniffers and data spoofing. As a permanent fix, I would suggest to Zabbix team to incorporate natively strong Encryption and Authentication mechanisms. In addition, an attacker can use Zabbix Plugin to spoof monitoring as if it was a real data by placing an existing item key and hostname. If you want to test it yourself, try an existing key name for example **proc.num**, then place any random value included a real agent name so that you can see the risk it involves if an attacker tries to harm your monitoring system.

Don't bear in mind that this tool is meant for hacking purposes, but As I have a master in Cryptology and Computer Security, I know the risk it involves when information is not encrypted. As network sniffer are legitimate tools used by network administrators for troubleshooting purpose, it can be used also to harm a system. So, I am not responsible for the use you make of this Zabbix Appender. I only need to catch your attention upon security. Features without security is a big risk.

ZabbixAppender is a functional plugin that has been tested in Linux, Windows and Solaris. It is highly useful for Java Application servers which uses Log4J as their logging framework. The opensource community must be grateful toward Apache Software foundation for this open source java Library and Zabbix Team for this enterprise class and fully featured monitoring system.
