FROM tomcat:9-jre17

ADD target/SpringBootFunction.war /usr/local/tomcat/webapps/

EXPOSE 8080
CMD ["catalina.sh", "run"]

# docker build -t springboot-function .
# docker run -p 8080:8080 springboot-function
