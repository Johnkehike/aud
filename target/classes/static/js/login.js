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
          var pass = $('#pass').val();
          var pass2 = $('#pass2').val();
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

               setTimeout(function() {
                  location.reload();
               }, 1000);
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



                $('#validate-email').on('click', function(event) {

                  event.preventDefault(); // prevent the default form submission
                 $('#emailAddress').attr('disabled', true);

                  var emailAddress = $('#emailAddress').val();

                  $('#validate-email').hide();
                  $('#loader3').show();


                  var registrationReq = {};
                  registrationReq.email= emailAddress;

                  var req = JSON.stringify(registrationReq);
                  // send the AJAX request
                  $.ajax({

                     headers: {
                       'Accept': 'application/json',
                       'Content-Type': 'application/json'
                     },
                    url: '/validate-email', // replace with the URL of your Spring Boot backend login endpoint
                    method: 'post',
                    data: req,
                    success: function(data) {
                    $('#loader3').hide();

                    if(data.code == '00'){


                      $('#otp-section').show();
                      $('#email-section').hide();
                      $('#email-validation-message').html(data.message)
                      .fadeIn(300)
                      .delay(4000)
                      .fadeOut(300, function() { $(this).remove();});
                      }

                      else {

                      $('#validate-email').show();
                      $('#emailAddress').attr('disabled', false);


                      $('#email-validation-message').html(data.message)
                      .fadeIn(300)
                      .delay(4000)
                      .fadeOut(300, function() { $(this).remove();});

                      }

                    },

                    error: function(jqXHR, textStatus, errorThrown) {

                      // display error message to the user
                      $('#loader3').hide();
                      $('#validate-email').show();
                      $('#emailAddress').attr('disabled', false);


                      $('#email-validation-message').html('Error experienced while validating email')
                      .fadeIn(300)
                      .delay(4000)
                      .fadeOut(300, function() { $(this).remove();});

                    }
                  });

                      });






                $('#validate-otp').on('click', function(event) {

                  event.preventDefault(); // prevent the default form submission

                  var emailAddress = $('#emailAddress').val();
                  var otp = $('#otp').val();

                  $('#validate-otp').hide();
                  $('#loader4').show();


                  var registrationReq = {};
                  registrationReq.email= emailAddress;
                  registrationReq.otp= otp;

                  var req = JSON.stringify(registrationReq);
                  // send the AJAX request
                  $.ajax({

                     headers: {
                       'Accept': 'application/json',
                       'Content-Type': 'application/json'
                     },
                    url: '/validate-otp', // replace with the URL of your Spring Boot backend login endpoint
                    method: 'post',
                    data: req,
                    success: function(data) {
                    $('#loader4').hide();

                    if(data.code == '00'){
                      $('#password-section').show();
                      $('#otp-section').hide();
                      }

                      else {

                      $('#validate-otp').show();

                      $('#otp-validation-message').html(data.message)
                      .fadeIn(300)
                      .delay(4000)
                      .fadeOut(300, function() { $(this).remove();});

                      }

                    },

                    error: function(jqXHR, textStatus, errorThrown) {

                      // display error message to the user
                      $('#loader4').hide();
                      $('#validate-otp').show();

                      $('#otp-validation-message').html('Error experienced while validating OTP')
                      .fadeIn(300)
                      .delay(4000)
                      .fadeOut(300, function() { $(this).remove();});

                    }
                  });

                      });








                $('#change-password').on('click', function(event) {

                  event.preventDefault(); // prevent the default form submission

                  var emailAddress = $('#emailAddress').val();
                  var password = $('#pass').val();

                  $('#change-password').hide();
                  $('#loader2').show();


                  var registrationReq = {};
                  registrationReq.email= emailAddress;
                  registrationReq.password= password;

                  var req = JSON.stringify(registrationReq);
                  // send the AJAX request
                  $.ajax({

                     headers: {
                       'Accept': 'application/json',
                       'Content-Type': 'application/json'
                     },
                    url: '/reset-password', // replace with the URL of your Spring Boot backend login endpoint
                    method: 'post',
                    data: req,
                    success: function(data) {
                    $('#loader2').hide();

                    if(data.code == '00'){

                                   $('#pass').val('');
                                   $('#pass2').val('');

                                          $('#span-sign-up-message').html(data.message)
                                          .fadeIn(300)
                                          .delay(4000)
                                          .fadeOut(300, function() { $(this).remove();});

                                          setTimeout(function() {
                                            location.reload();
                                          }, 1000);

                      }

                      else {

                      $('#change-password').show();

                      $('#span-sign-up-message').html(data.message)
                      .fadeIn(300)
                      .delay(4000)
                      .fadeOut(300, function() { $(this).remove();});

                      }

                    },

                    error: function(jqXHR, textStatus, errorThrown) {

                      // display error message to the user
                      $('#loader2').hide();
                      $('#change-password').show();

                      $('#span-sign-up-message').html('Error experienced while validating OTP')
                      .fadeIn(300)
                      .delay(4000)
                      .fadeOut(300, function() { $(this).remove();});

                    }
                  });

                      });




