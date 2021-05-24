let addstar = $("#addstar");
let addmovie = $("#addmovie");

/**
 * Handle the data returned by IndexServlet
 * @param resultDataString jsonObject, consists of session info
 */
function handleMetaData(resultData) {
    //let resultDataJson = JSON.parse(resultDataString);

    let meta = jQuery("#meta_table_body");

    //resultdata[i]["columns"] is an array that contains all the columns for resultdata[i]["table"]
        for (let i = 0; i < resultData.length; i++) {
            let rowHTML = "";
            rowHTML += "<tr>";
            rowHTML += "<th>" + resultData[i]["table"] + "</th>";
            for (let j = 0; j < resultData[i]["columns"].length; j++) {
                rowHTML += "<th>" + resultData[i]["columns"][j] + "</th>";
            }

            // Append the row created to the table body, which will refresh the page
            meta.append(rowHTML);
        }

}

/**
 * Handle the items in item list
 * @param resultArray jsonObject, needs to be parsed to html
 */

/**
 * Submit form content with POST method
 * @param cartEvent
 */
function handleStar() {
    console.log("submit star form");
    /**
     * When users click the submit button, the browser will not direct
     * users to the url defined in HTML form. Instead, it will call this
     * event handler when the event is triggered.
     */

    jQuery.ajax({
        dataType: "json",
        data: addstar.serialize(),
        method: "GET",
        url: "api/addstar",
        success: (resultData) => window.alert("Success! Star id is: " + resultData[0]["id"]),
       error: function(XMLHttpRequest, textStatus, errorThrown) {
           alert("Status: " + textStatus); alert("Error: " + errorThrown);
        }
    });
    addstar[0].reset();


    // clear input form
}

function showSuccess(resultData)
{
    window.alert("Success! Star id is: " + resultData[0]["id"]);

}

function handleMovie() {
    console.log("submit movie form");
    /**
     * When users click the submit button, the browser will not direct
     * users to the url defined in HTML form. Instead, it will call this
     * event handler when the event is triggered.
     */
    //cartEvent.preventDefault();

    jQuery.ajax({
        dataType: "json",
        data: addmovie.serialize(),
        method: "GET",
        url: "api/checkmovie",
        success: (resultData) => handleMovieFound(resultData),
        error: function(XMLHttpRequest, textStatus, errorThrown) {
            alert("Status: " + textStatus); alert("Error: " + errorThrown);
        }
    });


    // clear input form
}

function handleMovieFound(resultData)
{
    if(resultData[0]["movie_found"] == true)
    {
        addmovie[0].reset();
        window.alert("Movie Already Exists");
    }
    else{
        jQuery.ajax({
            dataType: "json",
            data: addmovie.serialize(),
            method: "GET",
            url: "api/addmovie",
            success: (resultData2) => showMessage(resultData2),

        });

    }



}

function showMessage(resultData2)
{
    addmovie[0].reset();
    window.alert("Successfully added movie! movie id: " + resultData2[0]["movieid"] + " star id: " + resultData2[0]["starid"] + " genre id: " + resultData2[0]["genreid"])

}



jQuery.ajax({
    dataType: "json",
    method: "GET",
    url: "api/metadata",
    success: (resultData) => handleMetaData(resultData)
});


// Bind the submit action of the form to a event handler function
//addstar.submit(handleStar);
