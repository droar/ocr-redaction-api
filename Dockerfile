FROM openjdk:11-slim

# Adding tesseract
RUN apt-get update && apt-get install -y \
    tesseract-ocr \
    ghostscript

WORKDIR /home/ocr-redaction-api
COPY ./build/libs/ocr-redaction-api-%%VERSION%%.jar ocr-redaction-api.jar
COPY ./build/resources/main/tess_data_folder /tess_data_folder

EXPOSE 8082
ENTRYPOINT ["java","-Dspring.profiles.active=%%SPRING.ACTIVE.PROFILE%%", "-jar","ocr-redaction-api.jar"]

