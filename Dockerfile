FROM tomcat:10-jre17

COPY target/SpringBootWeb/ /usr/local/tomcat/webapps/
ADD target/SpringBootWeb.war /usr/local/tomcat/webapps/

EXPOSE 8080
CMD ["catalina.sh", "run"]

# docker build -t mywebapp .
# docker run -p 8080:8080 mywebapp
