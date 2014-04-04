/** Submits the email, which is saved in a text file */
function submit(e) {
    var email = $("#email").val();
	var dataObject = { 'email': email,
                       'emailSubmit': true};
	
	// Actually submit the rating to the database
    $.ajax({
    	type: 'POST',
    	url: '../cgi-bin/gameup.php',
     	data: dataObject,
     	success: function (msg){
            // Redirect somewhere smart
     		alert('Everything is AWESOME!');
      	}
    });

    e.preventDefault();
	$("#email").val("");
}