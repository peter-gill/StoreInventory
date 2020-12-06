# StoreInventory

Run to this - 

1. Build and run mysql docker image.

Location:   
            
            peter-gill/StoreInventory/my-mysql/Dockerfile

            peter-gill/StoreInventory/my-mysql/sql-scripts/CreateTable.sql
            
Build cmd:  
            
            docker build -t my-mysql .

Run cmd:    

            docker run -d -p 3306:3306 --name my-mysql  my-mysql


2. Build and run store-inventory docker image.

Location:   

            peter-gill/StoreInventory/Dockerfile
Build cmd:  

            docker build -t store-inventory .
Run cmd:

            docker container run -it --volume /Users/pgill/workspace/input-folder:/home/storeuser/input-folder --volume /Users/pgill/workspace/processed-folder:/home/storeuser/processed-folder --network "host" store-inventory

Run app:    

            java -jar /home/storeuser/StoreInventory/target/StoreInventory-0.0.1-SNAPSHOT-jar-with-dependencies.jar /home/storeuser/input-folder


Notes:
1. Language is Java as not so familiar with .NET Core
2. Yet to add defensive programing for processing "input-folder" and "processed-folder", thus where --volume is used in the docker run command,
   the folders mounted (i.e. right hand side of ":"), need to be written as is. The local folders can vary as needed (i.e. left hand side of ":").
3. Unit tests can be run from interactive mode


Example to enter interactive mode and run unit tests:

            storeuser@docker-desktop:~/StoreInventory$ mvn test


Useful commands:

            mysql -h 127.0.0.1 -u storeuser -p company -e "SELECT * FROM products;"
            mysql -h 127.0.0.1 -u storeuser -p company -e "DELETE FROM products;"
