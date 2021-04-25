
function fillPage(resultData) {
    let body = jQuery("#cart_form");
    let totalCost = 0;
    for (let i = 0; i < resultData.length; i++) {
        let rowHTML = '';
        rowHTML += '<div class="Item">';
        rowHTML += '<div><h5>' + resultData[i]["movie_title"] + ' x ' + resultData[i]["movie_quantity"] + '</h5></div>';
        rowHTML += '<div>$'+ resultData[i]["movie_price"] * resultData[i]["movie_quantity"] + '.00';
        rowHTML += '</div> </div> ';
        body.append(rowHTML);
        totalCost += resultData[i]["movie_quantity"] * resultData[i]["movie_price"];
    }
    let sum = jQuery("#Sum");
    sum.append('<h5>$'+ totalCost + '.00</h5>')

}

jQuery.ajax({
    dataType: "json",
    method: "GET",
    url: "api/confirmation",
    success: (resultData) => fillPage(resultData)
});

