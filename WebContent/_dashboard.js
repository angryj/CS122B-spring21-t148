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
function handleStar(cartEvent) {
    console.log("submit cart form");
    /**
     * When users click the submit button, the browser will not direct
     * users to the url defined in HTML form. Instead, it will call this
     * event handler when the event is triggered.
     */
    //cartEvent.preventDefault();

    jQuery.ajax({
        dataType: "json",
        data: addstar.serialize(),
        method: "GET",
        url: "api/addstar",
        success: (resultData) => showSuccess(resultData),
        error: function(XMLHttpRequest, textStatus, errorThrown) {
            alert("Status: " + textStatus); alert("Error: " + errorThrown);
        }
    });


    // clear input form
}

function showSuccess(resultData)
{
    window.alert("Success! Star id is: " + resultData[0]["id"]);

}

jQuery.ajax({
    dataType: "json",
    method: "GET",
    url: "api/metadata",
    success: (resultData) => handleMetaData(resultData)
});


// Bind the submit action of the form to a event handler function
addstar.submit(handleStar);