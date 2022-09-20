FROM amazoncorretto:17
COPY ./build/libs/fip-recorder-all.jar /bin/runner/run.jar
WORKDIR /bin/runner

CMD ["java","-jar","run.jar"]