FROM openjdk:17-jdk-alpine


# 2. Uygulamanın ihtiyaç duyacağı TÜM yapılandırma değerleri için
# build-time argümanları tanımlıyoruz.
ARG JWT_SECRET
ARG DB_URL
ARG DB_USERNAME
ARG DB_PASSWORD
ARG AWS_S3_BUCKET_NAME
ARG AWS_REGION
ARG AWS_ACCESS_KEY
ARG AWS_SECRET_KEY

# 3. Build-time'da alınan argümanları, konteyner çalıştığında
# uygulamanın okuyabilmesi için ortam değişkenlerine (environment variables) atıyoruz.
ENV JWT_SECRET=$JWT_SECRET
ENV DB_URL=$DB_URL
ENV DB_USERNAME=$DB_USERNAME
ENV DB_PASSWORD=$DB_PASSWORD
ENV AWS_S3_BUCKET_NAME=$AWS_S3_BUCKET_NAME
ENV AWS_REGION=$AWS_REGION
ENV AWS_ACCESS_KEY=$AWS_ACCESS_KEY
ENV AWS_SECRET_KEY=$AWS_SECRET_KEY


COPY target/cloud-storage-0.0.1-SNAPSHOT.jar app.jar

ENTRYPOINT ["java","-jar","/app.jar"]