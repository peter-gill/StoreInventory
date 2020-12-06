FROM mysql

ENV MYSQL_DATABASE company
ENV MYSQL_ROOT_PASSWORD "supersecret"
ENV MYSQL_USER storeuser
ENV MYSQL_PASSWORD storeuser

COPY ./sql-scripts/ /docker-entrypoint-initdb.d/
