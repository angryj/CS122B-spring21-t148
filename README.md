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

### Prepared Statements:
All queuries use prepared statements:
https://github.com/UCI-Chenli-teaching/cs122b-spring21-team-148/blob/p3/src/AddMovieServlet.java
https://github.com/UCI-Chenli-teaching/cs122b-spring21-team-148/blob/p3/src/AddStarServlet.java
https://github.com/UCI-Chenli-teaching/cs122b-spring21-team-148/blob/p3/src/CheckMovieServlet.java
https://github.com/UCI-Chenli-teaching/cs122b-spring21-team-148/blob/p3/src/ConfirmationServlet.java
https://github.com/UCI-Chenli-teaching/cs122b-spring21-team-148/blob/p3/src/LoginAdminServlet.java
https://github.com/UCI-Chenli-teaching/cs122b-spring21-team-148/blob/p3/src/LoginServlet.java
https://github.com/UCI-Chenli-teaching/cs122b-spring21-team-148/blob/p3/src/MovieServlet.java
https://github.com/UCI-Chenli-teaching/cs122b-spring21-team-148/blob/p3/src/ShoppingCartServlet.java
https://github.com/UCI-Chenli-teaching/cs122b-spring21-team-148/blob/p3/src/StarServlet.java

### Contributions:

Jason- Part 2

Angelo- Part 1

Both: Debugging, CSS
