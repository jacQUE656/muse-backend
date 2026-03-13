package com.example.musebackend.Request;


import lombok.Data;

@Data
public class RegisterRequest {

    private String firstname;
    private String lastname;
    private String email;
    private String phonenumber;
    private String password;

//    private String confirmpassword;

//    @NotBlank(message = "VALIDATION.REGISTRATION.FIRSTNAME.NOT_BLANK")
//    @Size(min = 3 , max = 50, message ="VALIDATION.REGISTRATION.FIRSTNAME.SIZE")

//
//    @Pattern( // FOR UPPERCASE START
//            regexp = "^[\\p{L} '-]+$",
//            message = "VALIDATION.REGISTRATION.FIRSTNAME.PATTERN"
//    )



//    @NotBlank(message = "VALIDATION.REGISTRATION.LASTNAME.NOT_BLANK")
//    @Size(min = 3 , max = 50, message ="VALIDATION.REGISTRATION.LASTNAME.SIZE")
//  @Pattern( // FOR UPPERCASE START
//            regexp = "^[\\p{L} '-]+$",
//            message = "VALIDATION.REGISTRATION.LASTNAME.PATTERN"
//    )



//    @NotBlank(message = "VALIDATION.REGISTRATION.EMAIL.NOT_BLANK")
//    @Email(message = "VALIDATION.REGISTRATION.EMAIL.NOT_FORMAT")
   // @NonDisposableEmail(message = "VALIDATION.REGISTRATION.EMAIL.DISPOSABLE")



//    @NotBlank(message = "VALIDATION.REGISTRATION.PHONE.NOT_BLANK")
//    @Size(min = 5 , max = 50, message ="VALIDATION.PHONE.LASTNAME.SIZE")

//
//    @NotBlank(message = "VALIDATION.REGISTRATION.PASSWORD.NOT_BLANK")
//    @Size(min = 6 , max = 50, message ="VALIDATION.REGISTRATION.PASSWORD.SIZE")

//    @Pattern(
//            regexp = "^(?=.*[A-Z])(?=.*[a-z])(?=.*\\d)(?=.*\\W).*$",
//            message = "VALIDATION.REGISTRATION.PASSWORD.WEAK"
//    )


//    @NotBlank(message = "VALIDATION.REGISTRATION.CONFIRM.PASSWORD.NOT_BLANK")
//    @Size(min = 6 , max = 50, message ="VALIDATION.REGISTRATION.CONFIRM.PASSWORD.SIZE")

//    @Pattern(
//            regexp = "^(?=.*[A-Z])(?=.*[a-z])(?=.*\\d)(?=.*\\W).*$",
//            message = "VALIDATION.REGISTRATION.CONFIRM.PASSWORD.WEAK"
//    )


}
