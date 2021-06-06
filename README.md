# General

Team#: 148 Names: Angelo Basa, Jason Wu 
Project 5 Video Demo Link:
  https://www.youtube.com/watch?v=7k3HcGHrfi0

# Instruction of deployment:
       Clone the repo: https://github.com/UCI-Chenli-teaching/cs122b-spring21-team-148.git
        Run mvn package on the repo to create a .war file
        Populate the moviedb database using movie-data.sql and create_tables.sql and new-table.sql files (The steps are as follows)
        Create database moviedb

        use moviedb

        Source create_tables.sql

        Source movie-data.sql

        Source new-table.sql

        Go back to the repo directory and Use cp ./target/*.war /var/lib/tomcat9/webapps/ to push the .war file into tomcat

        Refresh tomcat on the web browser, and the deployed project should appear.

# Collaborations and Work Distribution:
Angelo: Connection pooling, master slave, debugging
Jason: Master slave, jmeter, debugging

Connection Pooling
# Include the filename/path of all code/configuration files in GitHub of using JDBC Connection Pooling.
  https://github.com/UCI-Chenli-teaching/cs122b-spring21-team-148/blob/main/WebContent/META-INF/context.xml
# Explain how Connection Pooling is utilized in the Fabflix code.
    We used connection pooling so that there is a pool of up to 100 connections, 
    where any thread is able to connect to them if they are available. These       
    connections are needed for any time the db is called: searching, checking out, autocomplete, etc. 
    These connections are freed up and reused by the next database request, which should speed up each request.
   #   Explain how Connection Pooling works with two backend SQL.
     Each backend sql has its own connection pool; there would be no need to share a pool between the two because 
     one of them is a connection for writing and       another is for reading/writing. The connections wouldnt be 
     reused between the two backend sql's because of their different purposes so sharing a pool is pointless.

# Master/Slave
- #### Include the filename/path of all code/configuration files in GitHub of routing queries to Master/Slave SQL.
    https://github.com/UCI-Chenli-teaching/cs122b-spring21-team-148/blob/main/WebContent/META-INF/context.xml
    has the two master slave resources ^^^
    
    https://github.com/UCI-Chenli-teaching/cs122b-spring21-team-148/blob/main/apacheconfigurations.txt
    load balancer routing configurations ^^^
    
    each servlet has master slave routing, depending on if it's for writing or reading. The following 3 use the master resource:
    https://github.com/UCI-Chenli-teaching/cs122b-spring21-team-148/blob/main/src/AddMovieServlet.java
    https://github.com/UCI-Chenli-teaching/cs122b-spring21-team-148/blob/main/src/AddStarServlet.java
    https://github.com/UCI-Chenli-teaching/cs122b-spring21-team-148/blob/main/src/ConfirmationServlet.java
    
    the rest of the servlets use the second resource that uses localhost, which can be either the master or the slave (because those servlets only do read operations)
    
- #### How read/write requests were routed to Master/Slave SQL?
We had two resources: one for the master, and one for the localhost(whether that was master/slave). For write operations, we used the master resource to ensure that any write operation was only done by the master (and updated to the slave). For read operations, local host was used so that either the master or the slave can do the read, depending on which instance the session was connected to.


# JMeter TS/TJ Time Logs
    - #### with logProcessor.py, simply run it with python logProcessor.py <relativeFilePath1> <relativeFilePath2> ... <relativeFilePathX> 

# JMeter TS/TJ Time Measurement Report

| **Single-instance Version Test Plan**          | **Graph Results Screenshot** | **Average Query Time(ms)** | **Average Search Servlet Time(ms)** | **Average JDBC Time(ms)** | 
|------------------------------------------------|------------------------------|----------------------------|-------------------------------------|---------------------------
| Case 1: HTTP/1 thread                          |  ![](https://github.com/UCI-Chenli-teaching/cs122b-spring21-team-148/blob/main/img/Single-instance%2C%20Single-threaded%2C%20HTTP%2C%20w%20Pooling.png)   | 107                        | 28.71                               | 28.44                     |
| Case 2: HTTP/10 threads                        | ![](https://github.com/UCI-Chenli-teaching/cs122b-spring21-team-148/blob/main/img/Single-instance%2C%20Multi-threaded%2C%20HTTP%2C%20w%20Pooling.png)   | 244                        | 162.73                              | 162.46                    | 
| Case 3: HTTPS/10 threads                       | ![](https://github.com/UCI-Chenli-teaching/cs122b-spring21-team-148/blob/main/img/Single-instance%2C%20Multi-threaded%2C%20HTTPS%2C%20w%20Pooling.png)   | 317                        | 140.27                              | 139.98                    | 
| Case 4: HTTP/10 threads/No connection pooling  | ![](https://github.com/UCI-Chenli-teaching/cs122b-spring21-team-148/blob/main/img/Single-instance%2C%20Multi-threaded%2C%20HTTP%2C%20wo%20Pooling.png)   | 236                        | 144.78                              | 144.26                    | 
    
#Analysis: Cases 1-4  
    Case 1: As the control for these cases, case 1 serves as the baseline for comparing all the cases in the single-instance test plan. As a base, we have about 107 ms average   
    query time.  
    Case 2: With multithreading, we find that the TS and TJ values have increased substantially, however, this may be largely due to the way we implemented connection pooling,   
    which added a 100ms delay.  
    Case 3: As with Case 2, multithreading reduces the overall speed of queries. However, this is exacerbated by the fact that requests from the HTTPS protocol thake much   
    longer than HTTP protocols.  
    Case 4: contrary to our expectations, for the single instance, requests without connection pooling proved to be more performant, which may have been caused by the way we  
    implemented the pooling. There was also the problem of too many connections, which meant that each query had to wait for a connection.
    
| **Scaled Version Test Plan**                   | **Graph Results Screenshot** | **Average Query Time(ms)** | **Average Search Servlet Time(ms)** | **Average JDBC Time(ms)** | 
|------------------------------------------------|------------------------------|----------------------------|-------------------------------------|---------------------------
| Case 1: HTTP/1 thread                          | ![](https://github.com/UCI-Chenli-teaching/cs122b-spring21-team-148/blob/main/img/Scaled%2C%20Single-threaded%2C%20HTTP%2C%20w%20pooling.png)   | 110                        | 22.02                               | 21.74                     |
| Case 2: HTTP/10 threads                        | ![](https://github.com/UCI-Chenli-teaching/cs122b-spring21-team-148/blob/main/img/Scaled%2C%20Multi-threaded%2C%20HTTP%2C%20w%20pooling.png)   | 144                        | 54.43                               | 54.22                     |
| Case 3: HTTP/10 threads/No connection pooling  | ![](https://github.com/UCI-Chenli-teaching/cs122b-spring21-team-148/blob/main/img/Scaled%2C%20Multi-threaded%2C%20HTTP%2C%20wo%20pooling.png)  | 147                        | 87.38                               | 87.00                     |

#Analysis: Cases 1-3  
Case 1: like for the single instance test, Case 1 of the scaled version serves as the control for which the other cases are compared to. The TS and TJ times in general are very fast, especially compared for the single instance, as the load is split between and master and salve instances  
Case 2: Compared to Case 1, the multithreaded test takes longer for the overall query time. However, because the load is split between master and slave, the average query time is much less than that of the single instance version  
Case 3: Because our deployment in Case 3 did not implement connection pooling, it compared poorly, especially in comparison to our test during case 2.  
    

