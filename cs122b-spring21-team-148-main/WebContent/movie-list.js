/**
 * This example is following frontend and backend separation.
 *
 * Before this .js is loaded, the html skeleton is created.
 *
 * This .js performs two steps:
 *      1. Use jQuery to talk to backend API to get the json data.
 *      2. Populate the data to correct html elements.
 */


/**
 * Handles the data returned by the API, read the jsonObject and populate data into html elements
 * @param resultData jsonObject
 */
function handleMoviesResult(resultData) {
    console.log("handleStarResult: populating  from resultData");

    // Populate the star table
    // Find the empty table body by id "star_table_body"
    let starTableBodyElement = jQuery("#movie_table_body");

    // Iterate through resultData, no more than 10 entries
    for (let i = 0; i < Math.min(20, resultData.length); i++) {

        let rowHTML = "";
        rowHTML += "<tr>";
        let threeStars = resultData[i]["movie_stars"].split(",", 3);
        // get ids
        let threeIds = resultData[i]["star_ids"].split(",", 3);

        rowHTML +=
            "<td>" + '<a href="movie.html?id=' + resultData[i]['movie_id'] + '">' +
            resultData[i]["movie_title"] + '</a>' +"<td>"
            + "<td>" + resultData[i]["movie_year"] + "<td>"
            + "<td>" + resultData[i]["movie_director"] + "<td>"
            + "<td>" + resultData[i]["movie_rating"] + "<td>"
            + "<td>" + resultData[i]["movie_genres"] + "<td>";

        if (threeStars.length > 2) {
            rowHTML +=
            "<th>" +
            // Add a link to single-star.html with id passed with GET url parameter
            '<a href="star.html?id=' + threeIds[0] + '">'
            + threeStars[0] +
                     // display star_name for the link text
            '</a>' +
            "</th>" + "," + "<th>" +
                // Add a link to single-star.html with id passed with GET url parameter
                '<a href="star.html?id=' + threeIds[1] + '">'
                + threeStars[1] +
                // display star_name for the link text
                '</a>' +
                "</th>"

            +  "<th>" +
                // Add a link to single-star.html with id passed with GET url parameter
                '<a href="star.html?id=' + threeIds[2] + '">'
                + threeStars[2] +
                // display star_name for the link text
                '</a>' +
                "</th>"

            //rowHTML += "<td>" + threeStars[0] + ", " + threeStars[1] + ", " + threeStars[2] + "<td>";
        }
        else if (threeStars.length > 1) {
            rowHTML +=
                "<th>" +
                // Add a link to single-star.html with id passed with GET url parameter
                '<a href="star.html?id=' + threeIds[0] + '">'
                + threeStars[0] +
                // display star_name for the link text
                '</a>' +
                "</th>" + "," + "<th>" +
                // Add a link to single-star.html with id passed with GET url parameter
                '<a href="star.html?id=' + threeIds[1] + '">'
                + threeStars[1] +
                // display star_name for the link text
                '</a>' +
                "</th>"        }
        else {
            rowHTML +=
                "<th>" +
                // Add a link to single-star.html with id passed with GET url parameter
                '<a href="star.html?id=' + threeIds[0] + '">'
                + threeStars[0] +
                // display star_name for the link text
                '</a>' +
                "</th>"        }
        rowHTML+= "</tr>";
        // Append the row created to the table body, which will refresh the page
        starTableBodyElement.append(rowHTML);
    }
}

// Makes the HTTP GET request and registers on success callback function handleStarResult
jQuery.ajax({
    dataType: "json",
    method: "GET",
    url: "api/Movie-List",
    success: (resultData) => handleMoviesResult(resultData) // Setting callback function to handle data returned successfully by the StarsServlet
});