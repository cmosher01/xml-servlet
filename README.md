# xml-servlet

Copyright © 2021–2022, by Christopher Alan Mosher, Shelton, Connecticut, USA, cmosher01@gmail.com

[![License](https://img.shields.io/github/license/cmosher01/xml-servlet.svg)](https://www.gnu.org/licenses/gpl.html)
[![Donate](https://img.shields.io/badge/Donate-PayPal-green.svg)](https://www.paypal.com/cgi-bin/webscr?cmd=_s-xclick&hosted_button_id=CVSSQ2BWDCKQ2)

## Development

#### prerequisites
```sh
$ docker-compose --version
Docker Compose version v2.1.1

$ java -version
openjdk version "17.0.1" 2021-10-19 LTS
OpenJDK Runtime Environment Corretto-17.0.1.12.1 (build 17.0.1+12-LTS)
OpenJDK 64-Bit Server VM Corretto-17.0.1.12.1 (build 17.0.1+12-LTS, mixed mode, sharing)
```

#### build
```sh
$ docker-compose build
```

#### run
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

#### use
```sh
$ curl localhost:60080
<!doctype html><html><body><pre><a href="/test/archive/">/test/archive/</a></pre></body></html>

$ lynx localhost:60080
```
