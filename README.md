
# Calling a Function from a Spring Boot Application

#### Create a Simple Function

For our demonstration, we’ll be using technology provided by the [Fn Project](https://fnproject.io/), an Open Source, Container-native, Serverless platform.

Let’s begin by starting the Fn server. BTW, you can download the Fn server and client from [fnproject.io](https://fnproject.io/).

 `fn start` runs Fn in single server mode, using an embedded database and message queue.

```
fn start --log-level DEBUG
```

You can choose to start the server with a log level of DEBUG to display any events, warnings or errors.

To check your Fn version, execute `fn version`.

Once the server is started, we can move forward with the creation of our function.

First, we’ll need to create an "app" which acts as a top-level collection of functions and other elements, so I’ll create a directory called `fn-demo` and `cd` into that directory:

```
mkdir fn-demo

cd fn-demo/
```

And then we’ll create our app:

```
fn create app fn-demo
```

`fn list` will display any apps we have created:

```
fn list app
```

Now that we have our top-level app created, let’s add our function.

We’ll use the `fn init` command to create our function:

```
fn init --runtime java17 --trigger http --name hello-world hello-world
```

You can use virtually any runtime (go, node, python, kotlin, .net) but for our demonstration, we’ll use Java 17.  

We’re also going to use a trigger for our function. Triggers are pointers to functions, it’s an endpoint where the function can be invoked. You can have multiple triggers pointing to the same function but `http` is the permitted value.

`hello-world` will be the name of our function as well as the directory for our function.

You can see the init command has created the source for our basic function along with a `pom` file and a `func.xml`.

The `func.xml` contains configuration information for our function such as version runtime, name, entry point for our function, build and runtime images and a list of triggers.

And here is the basic `HelloFunction` code created by the `fn init` process:

```
bat HelloFunction.java
```

One more item before we build our function, let’s explore our contexts:

```
fn list contexts
```

The Fn context stores configuration information about your functions, your Fn server and the location of your container images. You can configure multiple contexts for your function development.

For example, I can add my registry username to the default context:

```
fn update context registry seighman
```

```
fn list contexts
```

The next step is to build and package our function:

```
fn build -v
```

The end result is the function is built and a new container image is created. In this container image, the application JAR file is packaged as a layer of an image containing a Java Runtime environment.

With our function created, the next step is to deploy our application.  Within the function folder, execute:

```
fn deploy --app fn-demo --local
```

Here we pass the app name and the `--local` flag. 

The `--local` flag will skip the push to a remote container registry making local development faster.

This command deploys the app to the local Fn server and links a trigger called hello-world to that function.

We can also display a list of our functions:

```
fn list functions fn-demo
```

And we can inspect details of our function.  The inspect command allows us to display the properties of our function:

```
fn inspect function fn-demo hello-world
```

And finally, we can invoke our function.

Let's run our function using the invoke command by passing the app name along with the function name:

```
fn invoke fn-demo hello-world
```

And we see `Hello, from the function!` is returned.

#### Download and Run Apache Tomcat

Download Apache Tomcat 9 from [here](https://dlcdn.apache.org/tomcat/tomcat-9/v9.0.64/bin/apache-tomcat-9.0.64.tar.gz).

Create a directory name `tomcat` and unzip the file to that directory.

Edit the `settings.xml` file and add the following server information in the `<servers>` section.  The file will be located in either the `$MAVEN_HOME/conf` directory or `$HOME/.m2` directory.

```
  <servers>
    <server>
      <id>TomcatServer</id>
      <username>admin</username>
      <password>password</password>
    </server>
  </servers>
```

In addtion, edit the `$CATALINA_HOME/conf/tomcat-users.xml` file and add the following:

```
  <role rolename="manager"/>
  <role rolename="admin-gui"/>
  <role rolename="manager-gui"/>
  <role rolename="manager-script"/>
  <user username="admin" password="password" roles="manager, admin-gui, manager-gui, manager-script"/>
```
  
   

#### Build and Deploy a Sample Application

Clone the repository and `cd` to the `SpringBoot-Tomcat-Function` directory:

```
git clone https://github.com/swseighman/SpringBoot-Tomcat-Function
```
```
cd SpringBoot-Tomcat-Function
```

Build the project:

```
mvn package
```

Next, copy the WAR file from `target/SpringBootFunction.war` to the `tomcat/apache-tomcat-9.0.64/webapps/` folder.

From a terminal, navigate to the `tomcat/apache-tomcat-9.0.64/bin` folder and execute:

```
catalina.sh run
```

The Tomcat server will be started and the sample application will be deployed.

**Deploy Using Maven**

Add the following plugin to the `pom.xml` file:

```
<plugin>
	<groupId>org.opoo.maven</groupId>
	<artifactId>tomcat9-maven-plugin</artifactId>
	<version>3.0.1</version>
	<configuration>
		<url>http://localhost:8082/manager/text</url>
		<server>TomcatServer</server>
		<path>/SpringBootFunction</path>
	</configuration>
</plugin>
```

Deploy the application/war:

```
mvn tomcat9:deploy
```
```
... <snip>
[INFO] --- tomcat9-maven-plugin:3.0.1:deploy (default-cli) @ SpringBootFunction ---
[INFO] Deploying war to http://localhost:8082/SpringBootFunction
Uploading: http://localhost:8082/manager/text/deploy?path=%2FSpringBootFunction
Uploaded: http://localhost:8082/manager/text/deploy?path=%2FSpringBootFunction (18676 KB at 105510.1 KB/sec)

[INFO] tomcatManager status code:200, ReasonPhrase:
[INFO] OK - Deployed application at context path [/SpringBootFunction]
[INFO] ------------------------------------------------------------------------
[INFO] BUILD SUCCESS
[INFO] ------------------------------------------------------------------------
[INFO] Total time:  6.758 s
[INFO] Finished at: 2022-07-06T11:55:48-04:00
[INFO] ------------------------------------------------------------------------
```

To undeploy the app/war:

```
mvn tomcat9:undeploy
```

```
[INFO] Scanning for projects...
[INFO]
[INFO] -------------------< com.example:SpringBootFunction >-------------------
[INFO] Building springboot-function 0.0.1-SNAPSHOT
[INFO] --------------------------------[ war ]---------------------------------
[INFO]
[INFO] --- tomcat9-maven-plugin:3.0.1:undeploy (default-cli) @ SpringBootFunction ---
[INFO] Undeploying application at http://localhost:8082/SpringBootFunction
[INFO] OK - Undeployed application at context path [/SpringBootFunction]
[INFO] ------------------------------------------------------------------------
[INFO] BUILD SUCCESS
[INFO] ------------------------------------------------------------------------
[INFO] Total time:  2.367 s
[INFO] Finished at: 2022-07-06T12:14:23-04:00
[INFO] ------------------------------------------------------------------------
```

#### Testing the Sample Application

To test the application, browse to [http://localhost:8080/SpringBootFunction/hello](http://localhost:8080/SpringBootFunction/hello)

`Hello!` will be displayed in the browser.

Next, we'll call the function we created earlier. Browse to [http://localhost:8080/SpringBootFunction/callfunction](http://localhost:8080/SpringBootFunction/callfunction)

The browser will display `Hello, from the function!`.

You'll also notice the Fn server console will log the call/trigger from our Spring Boot application:

```
time="2022-06-28T15:52:39Z" level=info msg="starting call" action="server.handleHTTPTriggerCall)-fm" app_id=01G6N7634PNG8G00GZJ0000001 app_name=fn-demo call_id=01G6NG7GE5NG8G00GZJ0000004 container_id=01G6NG7GE5NG8G00GZJ0000005 fn_id=01G6N7A0DVNG8G00GZJ0000002 trigger_source=/hello-world
```

#### Summary

In this lab, we created a basic function and then called that function from within a Spring Boot application.







