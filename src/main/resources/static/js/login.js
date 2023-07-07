    function fireErrorMessage(title, msg) {
        Swal.fire({
            type: 'error',
            title: title,
            text: msg,
            footer: ''
        });
    }

    function fireSuccessMessage(msg) {
            Swal.fire(
                    'Successful',
                    msg,
                    'success',
                    );
        }





        $('#signup-form').on('submit', function(event) {

          event.preventDefault(); // prevent the default form submission

          var user = $('#user').val();
          var pass = $('#user').val();
          var pass2 = $('#user').val();
          var emailAddress = $('#emailAddress').val();

          if(pass != pass2) {

            fireErrorMessage("Wrong Password", "Passwords do not match");
            return;

          }

          $('#submit-signup').hide();
          $('#loader2').show();


          var registrationReq = {};
          registrationReq.name = user;
          registrationReq.password= pass;
          registrationReq.email= emailAddress;

          var req = JSON.stringify(registrationReq);

          // send the AJAX request
          $.ajax({

             headers: {
               'Accept': 'application/json',
               'Content-Type': 'application/json'
             },
            url: '/signup', // replace with the URL of your Spring Boot backend login endpoint
            method: 'post',
            data: req,
            success: function(data) {
            console.log('Here now');
            $('#loader2').hide();

            if(data.code == '00'){

              $('#span-sign-up-message').html(data.message)
              .fadeIn(300)
              .delay(4000)
              .fadeOut(300, function() { $(this).remove();});
              }

              else {

              $('#span-sign-up-message').html(data.message)
              .fadeIn(300)
              .delay(4000)
              .fadeOut(300, function() { $(this).remove();});

              }

            },

            error: function(jqXHR, textStatus, errorThrown) {

              // display error message to the user
              $('#loader2').hide();
              $('#submit-signup').show();

              $('#span-sign-up-message').html('Error experienced while saving data')
              .fadeIn(300)
              .delay(4000)
              .fadeOut(300, function() { $(this).remove();});

            }
          });

              });





