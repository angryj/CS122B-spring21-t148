let login_form = $("#login_form");

function delete_row(e)
{
    e.parentNode.parentNode.parentNode.parentNode.remove();
}

function clear_cart(e)
{
    document.querySelectorAll('.Content').forEach(e => e.remove());
}

function check_out()
{
    let blah = document.querySelectorAll('.Content')
    if (blah.length === 0) {
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
        success: ""
    });
}

function deleteMovie(movieId) {
    jQuery.ajax({
        dataType: "json",
        method: "GET",
        url: "api/shopping-cart?action=delete&id=" + movieId,
        success: ""
    });
}

function updateMovie(movieId, qty) {
    jQuery.ajax({
        dataType: "json",
        method: "GET",
        url: "api/shopping-cart?action=update&id=" + movieId + "&qty=" + qty,
        success: ""
    });
}


function submitLoginForm(formSubmitEvent) {
    console.log("submit login form");
    formSubmitEvent.preventDefault();
    $.ajax(
        "api/shopping-cart", {
            method: "GET",
            data: login_form.serialize(),
            success: handleLoginResult
        }
    );
}