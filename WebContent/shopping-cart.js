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

function check_out() {
    let blah = document.querySelectorAll('.Content')
    if (blah.length <= 1) {
        alert("The cart is empty.");
    }
    else {
        window.location.href = "payment.html";
    }
}

function addMovie(movieId){
    jQuery.ajax({
        dataType: "json",
        method: "GET",
        url: "api/shopping-cart?action=add&id=" + movieId,
        success: window.location.replace("shopping-cart.html")
    });
}

function deleteMovie(movieId) {
    jQuery.ajax({
        dataType: "json",
        method: "GET",
        url: "api/shopping-cart?action=delete&id=" + movieId,
        success: window.location.replace("shopping-cart.html")
    });
}

function updateMovie(movieId, qty) {
    jQuery.ajax({
        dataType: "json",
        method: "GET",
        url: "api/shopping-cart?action=update&id=" + movieId + "&qty=" + qty,
        success: window.location.replace("shopping-cart.html")
    });
}

function clearCart() {
    jQuery.ajax({
        dataType: "json",
        method: "GET",
        url: "api/shopping-cart?action=clear&id=0",
        success: window.location.replace("shopping-cart.html")
    });
}

function updateCart() {
    jQuery.ajax({
        dataType: "json",
        method: "GET",
        url: "api/shopping-cart",
        success: (resultData) => fillCart(resultData)
    });
}

function fillCart(resultData) {
    let body = jQuery("#cart_form");
    let totalCost = 0;
    for (let i = 0; i < resultData.length; i++) {
        let rowHTML = '';
        rowHTML += '<div class = "Content" ><form><div class="Item">';
        rowHTML += '<label><a href="movie.html?id=' + resultData[i]["movie_id"] + '"><h5>' + resultData[i]["movie_title"] + '</h5></a></label>';
        rowHTML += '<div><label>$'+ resultData[i]["movie_price"] * resultData[i]["movie_quantity"] + '.00</label>';
        rowHTML += '<input type="hidden" name="action" min="1" value="update"><input type="hidden" name="id" min="1" value="'+ resultData[i]["movie_id"] + '">';
        rowHTML += '<input type="number" name="qty" min="1" value="' + resultData[i]["movie_quantity"] + '">&nbsp;';
        rowHTML += '<input type="submit" class="btn btn-primary btn-sm" value = "Update">';
        rowHTML += '&nbsp;<a class="btn btn-primary btn-sm" href=shopping-cart.html?action=delete&id=' + resultData[i]["movie_id"] + '>'+ "Remove" + '</a>';
        rowHTML += '</div> </div> </form> </div>';
        body.append(rowHTML);
        totalCost += resultData[i]["movie_quantity"] * resultData[i]["movie_price"];
    }
    let sum = jQuery("#Sum");
    sum.append('<h5>$'+ totalCost + '.00</h5>')

}
let movieId = getParameterByName('id');
let action = getParameterByName('action');
let qty = getParameterByName('qty');

switch(action){
    case "add" : addMovie(movieId); break;
    case "update" : updateMovie(movieId, qty); break;
    case "delete" : deleteMovie(movieId); break;
    case "clear" : clearCart(); break;
    case null : updateCart();
}

let login_form = $("#login_form");

function submitLoginForm(formSubmitEvent) {
    formSubmitEvent.preventDefault();

    $.ajax(
        "api/shopping-cart", {
            method: "POST",
            data: login_form.serialize(),
            success: handleLoginResult
        }
    );
}

function handleLoginResult(resultDataString) {
    let resultDataJson = JSON.parse(resultDataString);

    // If login succeeds, it will redirect the user to index.html
    if (resultDataJson["status"] === "success") {
        alert("Purchase Successful!");
        window.location.replace("confirmation.html");
    } else {
        // If login fails, the web page will display
        // error messages on <div> with id "login_error_message"
        console.log("show error message");
        console.log(resultDataJson["message"]);
        alert("Purchase unsuccessful");
        $("#login_error_message").text(resultDataJson["message"]);
    }

}

login_form.submit(submitLoginForm);