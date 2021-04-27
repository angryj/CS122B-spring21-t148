### CS 122B Project 2
Demo: tbd

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

### Contributions:

Jason- Creating the Login page, and implementing the shopping cart 

Angelo- Implementing the main page, extending Movie List page

Both: Debugging, extending single movies and stars, general css tweaks
