run jboss
mvn clean package -PnoTest jboss-as:deploy
localhost:8080/web/index.html

http://www.mariopareja.com/blog/archive/2010/01/11/how-to-push-a-new-local-branch-to-a-remote.aspx // git branches help

mvn <goal> -PnoTest: to run without tests
mvn <goal> -Parq-jbossas-managed: to run with managed instance of JBoss AS
mvn <goal> -Parq-jbossas-remote: to run with remote instance of JBoss AS (DEFAULT MODE]

run specific test: 
mvn clean test -Parq-jbossas-managed -Dtest=cz.fi.muni.eshop.test.dummy.translate.TranslateTest -DfailIfNoTests=false

mvn surefire-report:report

mvn clean install package jboss-as:redeploy -PnoTest

changing log level globaly: 
for FILE in $(find . | grep "\.java") ; do sed -i 's/FINE/INFO/g' $FILE; done
for FILE in $(find . | grep "\.java") ; do sed -i 's/log.fine/log.info/g' $FILE; done


PROBLEM SOLVING:
WELD-000072 Managed bean declaring a passivating scope must be passivation capable
solution = missing: implements Serializable

16:50:12,428 ERROR [org.jboss.msc.service.fail] (MSC service thread 1-3) MSC00001: Failed to start service jboss.deployment.unit."muni-esho.ear".WeldStartService: org.jboss.msc.service.StartException in service jboss.deployment.unit."muni-esho.ear".WeldStartService: Failed to start service
	at org.jboss.msc.service.ServiceControllerImpl$StartTask.run(ServiceControllerImpl.java:1767) [jboss-msc-1.0.2.GA.jar:1.0.2.GA]
	at java.util.concurrent.ThreadPoolExecutor$Worker.runTask(ThreadPoolExecutor.java:886) [rt.jar:1.6.0_31]
	at java.util.concurrent.ThreadPoolExecutor$Worker.run(ThreadPoolExecutor.java:908) [rt.jar:1.6.0_31]
	at java.lang.Thread.run(Thread.java:662) [rt.jar:1.6.0_31]
Caused by: org.jboss.weld.exceptions.DeploymentException: WELD-001408 Unsatisfied dependencies for type [BasketBean] with qualifiers [@Default] at injection point [[field] @Inject private cz.fi.muni.eshop.controller.OrderController.basket]
	at org.jboss.weld.bootstrap.Validator.validateInjectionPoint(Validator.java:311)
	at org.jboss.weld.bootstrap.Validator.validateInjectionPoint(Validator.java:280)
	at org.jboss.weld.bootstrap.Validator.validateBean(Validator.java:143)
	at org.jboss.weld.bootstrap.Validator.validateRIBean(Validator.java:163)
	at org.jboss.weld.bootstrap.Validator.validateBeans(Validator.java:382)
	at org.jboss.weld.bootstrap.Validator.validateDeployment(Validator.java:367)
	at org.jboss.weld.bootstrap.WeldBootstrap.validateBeans(WeldBootstrap.java:379)
	at org.jboss.as.weld.WeldStartService.start(WeldStartService.java:56)
	at org.jboss.msc.service.ServiceControllerImpl$StartTask.startService(ServiceControllerImpl.java:1811) [jboss-msc-1.0.2.GA.jar:1.0.2.GA]
	at org.jboss.msc.service.ServiceControllerImpl$StartTask.run(ServiceControllerImpl.java:1746) [jboss-msc-1.0.2.GA.jar:1.0.2.GA]
	... 3 more
Tady byla chyba v tom, ze sem @Injektoval do OrderControlleru primo BasketBeanu a ne jen jeji rozhrani BasketManager. Ale sou pripady kdy fakt nevim proc to 1408-cku vyhodi, tak snad me to ze sem to aspon jednou vyresil pomuze..

Nepouzivat -PnoTest , hodi chybu pri kompilaci, NoClassFound user transaction v OrderLineJPATest, musel bych zmenit scope user transakce a to nechci, pouzij misto toho surefireri argument -DskipTests=true
mvn clean install package jboss-as:deploy -DskipTests=true


web.xml was automaticaly created, but I delete it, there it is if needed:
[pkremens@dhcp-4-200 ~/Dropbox/muni-eshop]$ cat web/src/main/webapp/WEB-INF/web.xml
<?xml version="1.0" encoding="UTF-8"?>
<web-app xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" xmlns="http://java.sun.com/xml/ns/javaee" xmlns:web="http://java.sun.com/xml/ns/javaee/web-app_2_5.xsd" xsi:schemaLocation="http://java.sun.com/xml/ns/javaee http://java.sun.com/xml/ns/javaee/web-app_2_5.xsd" version="2.5">
  <display-name>web</display-name>
  <welcome-file-list>
    <welcome-file>index.html</welcome-file>
    <welcome-file>index.htm</welcome-file>
    <welcome-file>index.jsp</welcome-file>
    <welcome-file>default.html</welcome-file>
    <welcome-file>default.htm</welcome-file>
    <welcome-file>default.jsp</welcome-file>
  </welcome-file-list>


jmssource adding:
http://www.jboss.org/jdf/quickstarts/jboss-as-quickstart/helloworld-jms/
using CLI:
[standalone@localhost:9999 /] jms-queue add --queue-address=testQueue --entries=queue/test,java:jboss/exported/jms/queue/test
[standalone@localhost:9999 /] jms-topic add --topic-address=testTopic --entries=topic/test,java:jboss/exported/jms/topic/test

Modify the Server JMS Configuration Manually

    Open the file: JBOSS_HOME/standalone/configuration/standalone-full.xml
    Add the JMS test queue as follows:

        Find the messaging subsystem:

          <subsystem xmlns="urn:jboss:domain:messaging:1.1">

        Scroll to the end of this section and add the following XML after the </jms-connection-factories> end tag but before the </hornetq-server> element:

              <jms-destinations>
                  <jms-queue name="testQueue">
                      <entry name="queue/test"/>
                      <entry name="java:jboss/exported/jms/queue/test"/>
                  </jms-queue>
                  <jms-topic name="testTopic">
                      <entry name="topic/test"/>
                      <entry name="java:jboss/exported/jms/topic/test"/>
                  </jms-topic>
              </jms-destinations>



from now I must full profile to support jms: ./standalone.sh -c standalone-full.xml

datasource adding: https://zorq.net/b/2011/07/12/adding-a-mysql-datasource-to-jboss-as-7/
!change modul version to 1.1
run MySQL server before start

remove branch from origin:
git push origin --delete <branchName>

Hibernate ddl possible values:
    validate: validate the schema, makes no changes to the database.
    update: update the schema.
    create: creates the schema, destroying previous data.
    create-drop: drop the schema at the end of the session.


ERROR 1217 (23000): Cannot delete or update a parent row: a foreign key constraint fails

That happens if there are tables with foreign keys references to the table you are trying to drop.

All you need to do is:
SET FOREIGN_KEY_CHECKS=0

Drop your tables
SET FOREIGN_KEY_CHECKS=1
