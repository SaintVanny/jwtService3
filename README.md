# Инструкция по сборке и запуску проекта

# Требования:
Java 17+
Maven 3.8+ 
Spring 3+ 


# Сборка:
bash
mvn clean install 
Важно(kalkan библиотеку может не сразу увидеть, чтобы избежать советую локально ставить  
<dependency>
            <groupId>kz.gov.pki.kalkan</groupId>
            <artifactId>knca_provider_jce_kalkan</artifactId>
            <version>0.7.5</version>
            <scope>system</scope>
            <systemPath>C:\Users\79602\.m2\repository\kz\gov\pki\kalkan\knca_provider_jce_kalkan\0.7.5\knca_provider_jce_kalkan-0.7.5.jar</systemPath>
        </dependency>
    </dependencies> )

    
mvn clean package
После сборки .jar будет находиться в директории target/:

target/название-проекта.jar
# Запуск:
bash
java -jar target/jwtService7-0.0.1-SNAPSHOT.jar.jar

Советую деплой через докер
