let login_form = $("#login_form");

function delete_row(e)
{
    e.parentNode.parentNode.parentNode.parentNode.remove();
}

function handleLoginResult(resultDataString) {

}

function submitLoginForm(formSubmitEvent) {
    console.log("submit login form");
    formSubmitEvent.preventDefault();

    $.ajax(
        "api/login", {
            method: "GET",
            data: login_form.serialize(),
            success: handleLoginResult
        }
    );
}