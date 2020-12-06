FROM ubuntu:20.04
RUN apt-get update
RUN apt-get install -y openjdk-14-jdk
RUN apt-get install -y maven
RUN apt-get install -y git
RUN apt-get install -y mysql-client
RUN apt-get install -y vim
RUN useradd -ms /bin/bash storeuser
USER storeuser
WORKDIR /home/storeuser
RUN mkdir /home/storeuser/input-folder
RUN mkdir /home/storeuser/processed-folder
RUN git clone https://github.com/peter-gill/StoreInventory
WORKDIR /home/storeuser/StoreInventory
RUN mvn package  -Dmaven.test.skip=true
