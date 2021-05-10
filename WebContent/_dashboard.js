let addstar = $("#addstar");
let addmovie = $("#addmovie");

/**
 * Handle the data returned by IndexServlet
 * @param resultDataString jsonObject, consists of session info
 */
function handleMetaData(resultData) {
    //let resultDataJson = JSON.parse(resultDataString);

    let meta = jQuery("#meta_table_body");
    for (let i = 0; i < resultData.length; i++) {
        console.log(resultData[i]["table"]);
        for (let j = 0; j < resultData[i]["columns"].length; j++) {
            console.log(resultData[i]["columns"][j]);
        }
    }

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


    /*console.log("handle session response");
    console.log(resultDataJson);
    console.log(resultDataJson["sessionID"]);

    // show the session information 
    $("#sessionID").text("Session ID: " + resultDataJson["sessionID"]);
    $("#lastAccessTime").text("Last access time: " + resultDataJson["lastAccessTime"]);*/

    // show cart information
}

/**
 * Handle the items in item list
 * @param resultArray jsonObject, needs to be parsed to html
 */
function handleCartArray(resultArray) {
    console.log(resultArray);
    let item_list = $("#item_list");
    // change it to html list
    let res = "<ul>";
    for (let i = 0; i < resultArray.length; i++) {
        // each item will be in a bullet point
        res += "<li>" + resultArray[i] + "</li>";
    }
    res += "</ul>";

    // clear the old array and show the new array in the frontend
    item_list.html("");
    item_list.append(res);
}

/**
 * Submit form content with POST method
 * @param cartEvent
 */
function handleCartInfo(cartEvent) {
    console.log("submit cart form");
    /**
     * When users click the submit button, the browser will not direct
     * users to the url defined in HTML form. Instead, it will call this
     * event handler when the event is triggered.
     */
    cartEvent.preventDefault();

    $.ajax("api/index", {
        method: "POST",
        data: cart.serialize(),
        success: resultDataString => {
            let resultDataJson = JSON.parse(resultDataString);
            handleCartArray(resultDataJson["previousItems"]);
        }
    });

    // clear input form
    cart[0].reset();
}

jQuery.ajax({
    dataType: "json",
    method: "GET",
    url: "api/metadata",
    success: (resultData) => handleMetaData(resultData)
});


// Bind the submit action of the form to a event handler function
//cart.submit(handleCartInfo);