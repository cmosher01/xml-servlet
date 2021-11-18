# Development

# prerequisites
```sh
$ docker-compose --version
Docker Compose version v2.1.1

$ java -version
openjdk version "17.0.1" 2021-10-19 LTS
OpenJDK Runtime Environment Corretto-17.0.1.12.1 (build 17.0.1+12-LTS)
OpenJDK 64-Bit Server VM Corretto-17.0.1.12.1 (build 17.0.1+12-LTS, mixed mode, sharing)
```

# build
```sh
$ ./gradlew build
> Task :generateMainEffectiveLombokConfig1
> Task :compileJava
> Task :processResources NO-SOURCE
> Task :classes
> Task :war
> Task :assemble
> Task :generateTestEffectiveLombokConfig1
> Task :compileTestJava NO-SOURCE
> Task :processTestResources NO-SOURCE
> Task :testClasses UP-TO-DATE
> Task :test NO-SOURCE
> Task :check UP-TO-DATE
> Task :build

BUILD SUCCESSFUL in 1s
4 actionable tasks: 4 executed
```

# run
```sh
$ docker-compose up -d
[+] Running 6/6
Network xml-servlet_default       Created                                                                                                                                                                                                               0.1s
Volume "xml-servlet_tomcat_temp"  Created                                                                                                                                                                                                               0.0s
Volume "xml-servlet_tomcat_logs"  Created                                                                                                                                                                                                               0.0s
Volume "xml-servlet_tomcat_work"  Created                                                                                                                                                                                                               0.0s
Container xml-servlet-tomcat-1    Started                                                                                                                                                                                                               0.5s
Container xml-servlet-nginx-1     Started                                                                                                                                                                                                              11.3s

$ docker-compose ps
NAME                   COMMAND                  SERVICE             STATUS              PORTS
xml-servlet-nginx-1    "/docker-entrypoint.…"   nginx               running (healthy)   0.0.0.0:80->80/tcp
xml-servlet-tomcat-1   "catalina.sh run sec…"   tomcat              running (healthy)   8080/tcp

```

# use
```sh
$ curl localhost
<!doctype html><html><body><pre><a href="/test/archive/d1/d2/">/test/archive/d1/d2/</a></pre></body></html>

$ lynx localhost
```
