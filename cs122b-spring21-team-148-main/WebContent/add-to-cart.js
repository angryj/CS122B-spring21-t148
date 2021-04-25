function addMovie(){
    jQuery.ajax({
        dataType: "json",
        method: "GET",
        url: "api/shopping-cart?action=add&id=" + movieId,
        success: function() {
            alert("add to cart successful!");
        }
    });
}

$(document).ready(function(){
    $('#cart').click(function(){
        addMovie(movieId);
    });
});

