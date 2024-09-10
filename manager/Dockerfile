FROM amazoncorretto:17-alpine-jdk
ENV JAVA_TOOL_OPTIONS -agentlib:jdwp=transport=dt_socket,server=y,suspend=n,address=*:9080
COPY target/*.jar manager.jar
ENTRYPOINT ["java", "-jar", "/manager.jar"]