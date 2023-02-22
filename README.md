###### Configuring ElasticSearch Cluster:  (This document has been written for CentOS server)

**Download and Install JDK:**
  <ul>
      <li>Download JDK using following link: https://www.oracle.com/sg/java/technologies/javase/javase-jdk8-downloads.html(jdk-8u291-linux-x64.tar.gz)</li>
      <li>Copy to any directory but preferred directory /opt/</li>
      <li>Unzip downloaded tar.gz file using: tar -xvzf _file_name_with_tar.gz</li>
      <li>Now, rename created directory using: mv jdk-8u291-linux-x64 jdk</li>
      <li>Now, copy path of jdk folder</li>
      <li>Open bash_profile using :<code>vi ~/.bash_profile</code> and add:</li>
  </ul>

```
export JAVA_HOME=path_to_the_jdk_directory
export ES_JAVA_HOME=/path_to_the_jdk_directory
export $PATH=$JAVA_HOME/bin:$PATH
```
  
Now, issue following command: ```source ~/.bash_profile```
Check Java installation using: ```java --version and echo $JAVA_HOME```

Note: https://docs.oracle.com/en/java/javase/16/install/installation-jdk-linux-platforms.html  Can also be followed to see documentation of installation

**Download and Install ElasticSearch:**

Create a file called elasticsearch.repo in the /etc/yum.repos.d/ directory for RedHat based distributions, or in the /etc/zypp/repos.d/ directory for OpenSuSE based distributions, containing
```
[elasticsearch]
name=Elasticsearch repository for 7.x packages
baseurl=https://artifacts.elastic.co/packages/7.x/yum
gpgcheck=1
gpgkey=https://artifacts.elastic.co/GPG-KEY-elasticsearch
enabled=0
autorefresh=1
type=rpm-md
```

And your repository is ready for use. You can now install Elasticsearch with following commands:
```
sudo yum install --enablerepo=elasticsearch elasticsearch
```

Above installation should be done on every server of the cluster.

Configure cluster:
Go to /usr/share/elasticsearch and generate certificate using
```
./bin/elasticsearch-certutil ca
./bin/elasticsearch-certutil cert --ca elastic-certificates.p12
```
Now move above certificates to /etc/elasticsearch/ 

Open /etc/elasticsearch/elasticsearch.yml and change/append following things:
```
cluster.name: fullfilman-es
node.name: es-node-01
node.master: true
node.data: true
network.host: private_ip_machine_01
http.port: 9200
discovery.zen.minimum_master_nodes: 2
discovery.zen.ping.unicast.hosts: ["private_ip_machine_01","private_ip_machine_02","private_ip_machine_03"]
cluster.initial_master_nodes: ["private_ip_machine_01", "private_ip_machine_02", "private_ip_machine_03"]
xpack.security.enabled: true
xpack.security.transport.ssl.enabled: true
xpack.security.transport.ssl.verification_mode: certificate
xpack.security.transport.ssl.client_authentication: required
xpack.security.transport.ssl.keystore.path: elastic-certificates.p12
xpack.security.transport.ssl.truststore.path: elastic-certificates.p12
Now issue command below to configure auto startup, start, stopsudo chkconfig --add elasticsearch
sudo -i service elasticsearch start
sudo -i service elasticsearch stop
```
Elasticsearch server log can be seen here: ```/var/log/elasticsearch/```

Start Elasticsearch in every nodes and then

Now, to enable userName and password for elasticsearch and kibana, issue following command and enter preferred passwords (only on the first server):
Go to ```/usr/share/elasticsearch```
```
./bin/elasticsearch-setup-passwords interactive
```

**Elasticsearch cluster is up and Running.**

Note: Make sure that from each server to each server, there is an accessibility to 9200 and 9300 ports.

Install Kibana: 

Create a file called kibana.repo in the ```/etc/yum.repos.d/``` directory for RedHat based distributions, or in the ```/etc/zypp/repos.d/``` directory for OpenSuSE based distributions, containing:

```
[kibana-7.x]
name=Kibana repository for 7.x packages
baseurl=https://artifacts.elastic.co/packages/7.x/yum
gpgcheck=1
gpgkey=https://artifacts.elastic.co/GPG-KEY-elasticsearch
enabled=1
autorefresh=1
type=rpm-md
```
Now execute following command to install kibana:
```
sudo yum install kibana
```
Now, configure username and password created for kibana before during elasticsearch username and password configuration using following:
```
vi /etc/kibana/kibana.yml
```
Change following lines:

```
server.host: "your_machine_public_ip"
elasticsearch.username: "kibana_system"
elasticsearch.password: "password_you_have_created_for_kibana_system_user"
```
<br>Now start enable auto start and start kibana manually using following commands

```
sudo chkconfig --add kibana
sudo -i service kibana start
sudo -i service kibana stop
```

Now Kibana should be accessible using web browser URL:
http://your_public_ip:5601

Note: Make sure than 5601 port is accessible publicly from outside


Configure Build and Run API Application

The API application on the top of elasticsearch has been written using Java-8 language and used Spring Boot framework. As build tools, maven has been used and to build the application from the source code maven is required.

**Pre-Requirements:**
<ul>
<li>Jdk8 installed (as described above)</li>
<li>Maven installed (Downloaded from https://maven.apache.org/download.cgi and version is 3.8.1) </li>
</ul>
Note: To install maven following tutorials might be used 
<a href="https://maven.apache.org/install.html">https://maven.apache.org/install.html</a>

Now, checkout source from github and open terminal and enter to the project directory

**Now run:**
```
mvn clean install -DskipTests
```
If you would like to change any settings like Elasticsearch cluster information, username,password, API Token (that is required to consume API to migrate data), migration starting cron open resources/application-prod.properties and do so.

Now, visit the target directory, **fulfillman.war** is ready there to deploy.

Deployment of the application


Currently fulfilman.war is configured for apache-tomcat-9 and wildfly-21 web servers and one of these servers shall be used to deploy. If WebServer cluster is required then wildfly is recommended but now, application is running on singleton tomcat server.

Install and Run on tomcat: (jdk installation required)

**Download apache-tomcat-9**
<ul>
<li>Unzip to any suitable location</li>
<li>Rename it to tomcat and go to tomcat/bin</li>
<li>Run startup.sh </li>
<li>Now copy fulfilman.war to /tomcat/webapps/</li>
<li>See log at /tomcat/logs/catalina.out</li>
</ul>

**Note:** Make sure 8080 port is exposed to outside











# elasticsearch
