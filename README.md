### CS 122B Project 4
Demo: 

### To run this:
1. Clone the repo: https://github.com/UCI-Chenli-teaching/cs122b-spring21-team-148.git
2. Run mvn package on the repo to create a .war file
3. Populate the moviedb database using movie-data.sql and create_tables.sql and new-table.sql files (The steps are as follows)

Create database moviedb

use moviedb

Source create_tables.sql

Source movie-data.sql

Source new-table.sql


Go back to the repo directory and Use cp ./target/*.war /var/lib/tomcat9/webapps/ to push the .war file into tomcat

Refresh tomcat on the web browser, and the deployed project should appear.

### Substring Matching Design:
For title, director, and star, we used the pattern % + search_term + % to get the mysql result we wanted.
The logic for determining the correct query is located in the file MovieListServlet.java, lines 128-157

- # JMeter TS/TJ Time Logs
    - #### with logProcessor.py, simply run it with python logProcessor.py <relativeFilePath1> <relativeFilePath2> ... <relativeFilePathX> 

- # JMeter TS/TJ Time Measurement Report

| **Single-instance Version Test Plan**          | **Graph Results Screenshot** | **Average Query Time(ms)** | **Average Search Servlet Time(ms)** | **Average JDBC Time(ms)** | 
|------------------------------------------------|------------------------------|----------------------------|-------------------------------------|---------------------------
| Case 1: HTTP/1 thread                          | ![](path to image in img/)   | 107                        | 28.71                               | 28.44                     |
| Case 2: HTTP/10 threads                        | ![](path to image in img/)   | 244                        | 162.73                              | 162.46                    | 
| Case 3: HTTPS/10 threads                       | ![](path to image in img/)   | 317                        | 140.27                              | 139.98                    | 
| Case 4: HTTP/10 threads/No connection pooling  | ![](path to image in img/)   | 236                        | 144.78                              | 144.26                    | 

- #Analysis: Cases 1-4
    Case 1: As the control for these cases, case 1 serves as the baseline for comparing all the cases in the single-instance test plan. As a base, we have about 107 ms average 
    query time.
    Case 2: With multithreading, we find that the TS and TJ values have increased substantially, however, this may be largely due to the way we implemented connection pooling, 
    which added a 100ms delay.
    Case 3: As with Case 2, multithreading reduces the overall speed of queries. However, this is exacerbated by the fact that requests from the HTTPS protocol thake much 
    longer than HTTP protocols.
    Case 4: 
    
| **Scaled Version Test Plan**                   | **Graph Results Screenshot** | **Average Query Time(ms)** | **Average Search Servlet Time(ms)** | **Average JDBC Time(ms)** | 
|------------------------------------------------|------------------------------|----------------------------|-------------------------------------|---------------------------
| Case 1: HTTP/1 thread                          | ![](path to image in img/)   | 110                        | 22.02                               | 21.74                     |
| Case 2: HTTP/10 threads                        | ![](path to image in img/)   | 144                        | 54.43                               | 54.22                     |
| Case 3: HTTP/10 threads/No connection pooling  | ![](path to image in img/)   | 147                        | 87.38                               | 87.00                     |

- #Analysis: Cases 1-3
    

