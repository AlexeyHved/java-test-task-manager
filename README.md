# java-test-task-manager
Task manager app, based on reactive stack with spring security JWT authorization
1) Clone repository to local machine and build .jar files with maven
2) Go to project directory and use "docker compose up" for build and run app
3) After that you can "Try it out" [registration endpoint](http://localhost:8080/webjars/swagger-ui/index.html#/auth-controller/registration) with swagger ui and copy/paste "accessToken" for authorize
4) And now you can use [other endpoints](http://localhost:8080/swagger-ui) with swagger ui
5) You can use json file from "postman" folder for other tests

#Auth server database diagram
![](https://github.com/AlexeyHved/java-test-task-manager/blob/main/jwt-auth-server/jwtserver-diagram.png)
#Manager database diagram
![](https://github.com/AlexeyHved/java-test-task-manager/blob/main/manager/managerdb-diagram.png)
