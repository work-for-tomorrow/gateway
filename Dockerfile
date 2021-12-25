FROM openjdk:11
ENV TZ=Asia/Shanghai
ENV CURRENT_PROFILE=dev
ENV MODULE_NAME=gateway.cmcc.com-1.0
ENV JAVA_OPT='-Xms512M -Xmx512M'


RUN ln -snf /usr/share/zoneinfo/$TZ /etc/localtime && echo $TZ > /etc/timezone
RUN mkdir /dumpfile
# arthas
RUN ["/bin/bash","-c","cd ~ && wget https://alibaba.github.io/arthas/arthas-boot.jar"]

COPY ./target/${MODULE_NAME}.jar /usr/app/${MODULE_NAME}/${MODULE_NAME}.jar
CMD cd /usr/app/${MODULE_NAME} ; java -jar ${JAVA_OPT} -XX:+HeapDumpOnOutOfMemoryError -XX:HeapDumpPath=/dumpfile -Xlog:gc=trace:file=gc.log:time,pid:filecount=10,filesize=2097152 -Dspring.profiles.active=${CURRENT_PROFILE} ${MODULE_NAME}.jar
