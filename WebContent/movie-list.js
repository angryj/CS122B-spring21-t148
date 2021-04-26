let sort = $("#sort");

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
    let urlparams = window.location.search;
    let searchParams = new URLSearchParams(urlparams);
    let page = searchParams.get("pageNumber")
    let pageNum = 1

    if(page != null)
    {
        pageNum = parseInt(page);
    }
    let numShow = searchParams.get("show")
    if(numShow == null)
    {
        numShow = 25;
    }

    //hide the next button if theres no more results
    if(numShow>resultData.length)
    {
        let b = document.getElementById("Next");
        b.style.display = "none";
    }
    // Iterate through resultData, no more than 10 entries
    for (let i = 0; i < resultData.length; i++) {
        let rank = i+1 +((pageNum-1)*numShow)
        let rowHTML = "";
        rowHTML += "<tr><td>"+ rank + "<td>";
        let threeStars = resultData[i]["movie_stars"].split(",", 3);
        // get ids
        let threeIds = resultData[i]["star_ids"].split(",", 3);
        let threeGenres = resultData[i]["movie_genres"].split(",", 3);

        rowHTML +=
            "<td>" +

            '<a href="movie.html?id=' + resultData[i]['movie_id'] + '">' +
            resultData[i]["movie_title"] + '</a>' + "<td>"
            + "<td>" + resultData[i]["movie_year"] + "<td>"
            + "<td>" + resultData[i]["movie_director"] + "<td>"
            + "<td>" + resultData[i]["movie_rating"] + "<td>"

        if (threeGenres.length > 2) {
            rowHTML +=
                "<td>" +
                '<a href="movie-list.html?genre=' + threeGenres[0] + '">'
                + threeGenres[0] +
                '</a>' + '<a>, </a>'+
                '<a href="movie-list.html?genre=' + threeGenres[1] + '">'
                + threeGenres[1] +
                '</a>' + '<a>, </a>'+
                '<a href="movie-list.html?genre=' + threeGenres[2] + '">'
                + threeGenres[2] +
                // display star_name for the link text
                '</a>' +
                "</td>"

            //rowHTML += "<td>" + threeStars[0] + ", " + threeStars[1] + ", " + threeStars[2] + "<td>";
        }
        else if (threeGenres.length > 1) {
            rowHTML +=
                "<td>" +
                '<a href="movie-list.html?genre=' + threeGenres[0] + '">'
                + threeGenres[0] +
                '</a>' + '</a>' + '<a>, </a>'+
                '<a href="movie-list.html?genre=' + threeGenres[1] + '">'
                + threeGenres[1] +
                '</a>' +
                "</td>"
        }
        else {
            rowHTML +=
                "<td>" +
                '<a href="movie-list.html?genre=' + threeGenres[0] + '">'
                + threeGenres[0] +
                '</a>' +
                "</td>"        }
        if (threeStars.length > 2) {
            rowHTML +=
                "<td>" +
                // Add a link to single-star.html with id passed with GET url parameter
                '<a href="star.html?id=' + threeIds[0] + '">'
                + threeStars[0] +
                // display star_name for the link text
                '</a>' + '</a>' + '<a>, </a>'+
                // Add a link to single-star.html with id passed with GET url parameter
                '<a href="star.html?id=' + threeIds[1] + '">'
                + threeStars[1] +
                // display star_name for the link text
                '</a>' + '</a>' + '<a>, </a>'+
                // Add a link to single-star.html with id passed with GET url parameter
                '<a href="star.html?id=' + threeIds[2] + '">'
                + threeStars[2] +
                // display star_name for the link text
                '</a>' +
                "</td>"

            //rowHTML += "<td>" + threeStars[0] + ", " + threeStars[1] + ", " + threeStars[2] + "<td>";
        }
        else if (threeStars.length > 1) {
            rowHTML +=
                "<td>" +
                // Add a link to single-star.html with id passed with GET url parameter
                '<a href="star.html?id=' + threeIds[0] + '">'
                + threeStars[0] +
                // display star_name for the link text
                '</a>' + '</a>' + '<a>, </a>'+
                // Add a link to single-star.html with id passed with GET url parameter
                '<a href="star.html?id=' + threeIds[1] + '">'
                + threeStars[1] +
                // display star_name for the link text
                '</a>' +
                "</td>"
        }
        else {
            rowHTML +=
                "<td>" +
                // Add a link to single-star.html with id passed with GET url parameter
                '<a href="star.html?id=' + threeIds[0] + '">'
                + threeStars[0] +
                // display star_name for the link text
                '</a>' +
                "</td>"
        }
        rowHTML += "<td>"+ "<button class='btn btn-secondary btn-sm'  onclick=\"addMovie(\'" + resultData[i]['movie_id'] + "\')\">+</button>" + "</td>"

        rowHTML+= "</tr>";
        // Append the row created to the table body, which will refresh the page
        starTableBodyElement.append(rowHTML);
    }
}

function handleSort(object)
{
    let urlparams = window.location.search;
    
    let searchParams = new URLSearchParams(urlparams);
    if (object.value == "Title Ascending Rating Descending") {
        searchParams.set("sort","titleASCratingDESC");
    }
    if (object.value == "Title Ascending Rating Ascending") {
        searchParams.set("sort","titleASCratingASC");
    }
    if (object.value == "Title Descending Rating Descending") {
        searchParams.set("sort","titleDESCratingDESC");
    }
    if (object.value == "Title Descending Rating Ascending") {
        searchParams.set("sort","titleDESCratingASC");
    }
    if (object.value == "Rating Ascending Title Descending") {
        searchParams.set("sort","ratingASCtitleDESC");
    }
    if (object.value == "Rating Ascending Title Ascending") {
        searchParams.set("sort","ratingASCtitleASC");
    }
    if (object.value == "Rating Descending Title Descending") {
        searchParams.set("sort","ratingDESCtitleDESC");
    }
    if (object.value == "Rating Descending Title Ascending") {
        searchParams.set("sort","ratingDESCtitleASC");
    }
    window.location.replace("movie-list.html" + "?" + searchParams);
}

function handleShow(object)
{
    let urlparams = window.location.search;
    let searchParams = new URLSearchParams(urlparams);

    if(object.value == "10")
    {
        searchParams.set("show","10")
    }
    if(object.value == "25")
    {
        searchParams.set("show","25")
    }
    if(object.value == "50")
    {
        searchParams.set("show","50")
    }
    if(object.value == "100")
    {
        searchParams.set("show","100")
    }

    window.location.replace("movie-list.html" + "?" + searchParams);

}

function handlePrev()
{
    let urlparams = window.location.search;
    let searchParams = new URLSearchParams(urlparams);
    let page = searchParams.get("pageNumber")
    if(page != null)
    {
        let temp = parseInt(page);
        temp -= 1;
        temp = temp.toString()
        searchParams.set("pageNumber",temp)
        window.location.replace("movie-list.html" + "?" + searchParams);
    }
}

function handleNext()
{
    let urlparams = window.location.search;
    let searchParams = new URLSearchParams(urlparams);
    let page = searchParams.get("pageNumber")
    if (page == null)
    {
        page = 1;
    }
    let temp = parseInt(page);
    temp += 1;
    temp = temp.toString()
    searchParams.set("pageNumber",temp)
    window.location.replace("movie-list.html" + "?" + searchParams);
}

function setDefault(url)
{
    let val = document.getElementById("sort");
    let val2 = document.getElementById("show");

    //set the text that is displayed inside the textbox of the drop down menu
    if (searchParams.get("sort") == null)
    {
        val.value = "Title Ascending Rating Descending";
    }
    else if (searchParams.get("sort") == "titleASCratingDESC")
    {
        val.value = "Title Ascending Rating Descending";
    }
    else if (searchParams.get("sort") == "titleASCratingASC")
    {
        val.value = "Title Ascending Rating Ascending";
    }
    else if (searchParams.get("sort") == "titleDESCratingDESC")
    {
        val.value = "Title Descending Rating Descending";
    }
    else if (searchParams.get("sort") == "titleDESCratingASC")
    {
        val.value = "Title Descending Rating Ascending";
    }
    else if (searchParams.get("sort") == "ratingASCtitleDESC")
    {
        val.value = "Rating Ascending Title Descending";
    }
    else if (searchParams.get("sort") == "ratingASCtitleASC")
    {
        val.value = "Rating Ascending Title Ascending";
    }
    else if (searchParams.get("sort") == "ratingDESCtitleDESC")
    {
        val.value = "Rating Descending Title Descending";
    }
    else if (searchParams.get("sort") == "ratingDESCtitleASC")
    {
        val.value = "Rating Descending Title Ascending";
    }

    if(searchParams.get("show") == null)
    {
        val2.value = "25"
    }
    else if(searchParams.get("show") == "10")
    {
        val2.value = "10"
    }
    else if(searchParams.get("show") == "25")
    {
        val2.value = "25"
    }
    else if(searchParams.get("show") == "50")
    {
        val2.value = "50"
    }
    else if(searchParams.get("show") == "100")
    {
        val2.value = "100"
    }

    let x = document.getElementById("previous");

    //if on page 1, hide the previous button
    if (searchParams.get("pageNumber") == null || searchParams.get("pageNumber") == "1")
    {
        x.style.display = "none";
    }
    else {
        x.style.display = "block";
    }
}


let urlparams = window.location.search;
let searchParams = new URLSearchParams(urlparams);

//set the text that is displayed inside the textbox of the drop down menu and hide prev/next buttons
setDefault(searchParams);



// Makes the HTTP GET request and registers on success callback function handleStarResult
jQuery.ajax({
    dataType: "json",
    method: "GET",
    url: "api/Movie-List" + urlparams,
    success: (resultData) => handleMoviesResult(resultData) // Setting callback function to handle data returned successfully by the StarsServlet
});