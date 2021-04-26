function getParameterByName(target) {
    // Get request URL
    let url = window.location.href;
    // Encode target parameter name to url encoding
    target = target.replace(/[\[\]]/g, "\\$&");

    // Ues regular expression to find matched parameter value
    let regex = new RegExp("[?&]" + target + "(=([^&#]*)|&|#|$)"),
        results = regex.exec(url);
    if (!results) return null;
    if (!results[2]) return '';

    // Return the decoded parameter value
    return decodeURIComponent(results[2].replace(/\+/g, " "));
}

function handleResult(resultData) {

    let indexElement = jQuery("#back_to_index");
    let params = resultData[0]["params"];
    indexElement.append('<p> <a href= "movie-list.html?' + params +  '">' + "Back to Movies" + '</a> </p>');


    let titleElement = jQuery("#m_title");
    titleElement.append("<p>" + resultData[0]["movie_title"] + "</p>");

    let infoElement = jQuery("#m_info");
    infoElement.append(
        "<p>Director: " + resultData[0]["movie_director"] + "</p>" +
        "<p>Year: " + resultData[0]["movie_year"] + "</p>" +
        "<p>Rating: " + resultData[0]["movie_rating"] + "</p>" +
        "<button class='btn btn-info'  onclick=\"addMovie(\'" + resultData[0]['movie_id'] + "\')\"> Add to Cart </button>"
    );


    let genresElement = jQuery("#genres_table_body");
    genresElement.append(
        "<tr><td>" + resultData[0]["movie_genres"] +"</td></tr>"
    );

    let starsElement = jQuery("#stars_table_body");
    let stars_split = resultData[0]["movie_stars"].split(",");
    //get ids for stars
    let star_ids_split = resultData[0]["star_ids"].split(",");
    let rowHTML = "";
    for (let i = 0; i < stars_split.length; i++) {
        rowHTML += "<tr><td>" +  '<a href="star.html?id=' + star_ids_split[i] + '">' +stars_split[i]  + '</a>' + "</td></tr>";
    }
    starsElement.append(rowHTML);
}

let movieId = getParameterByName('id');

jQuery.ajax({
    dataType: "json",  // Setting return data type
    method: "GET",// Setting request method
    url: "api/Movie?id=" + movieId, // Setting request url, which is mapped by StarsServlet in Stars.java
    success: (resultData) => handleResult(resultData) // Setting callback function to handle data returned successfully by the SingleStarServlet
});