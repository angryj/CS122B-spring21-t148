## CS 122B Project 1 
Demo: https://www.youtube.com/watch?v=H-HrCSS2Tyw

How to Run:
Clone the repo: https://github.com/UCI-Chenli-teaching/cs122b-spring21-team-148.git

Run mvn package on the repo to create a .war file

Populate the database using movie-data.sql and create_tables.sql (The steps are as follows)

Create database moviedb

use moviedb

Source create_tables.sql

Source movie-data.sql


Go back to the repo directory and Use cp ./target/*.war /var/lib/tomcat9/webapps/ to push the .war file into tomcat

Refresh tomcat on the web browser, and the deployed project should appear.

Contributions:

Jason- Creating the movie list and the single movie page

Angelo- Creating the single star page, demo

Both: debugging the entire project, creating hyperlinks to jump between movie list/single movie/single star pages
