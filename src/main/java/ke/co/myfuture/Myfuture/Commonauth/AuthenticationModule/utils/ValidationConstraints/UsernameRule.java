package ke.co.myfuture.Myfuture.Commonauth.AuthenticationModule.utils.ValidationConstraints;//package com.emtechhouse.usersservice.utils.ValidationConstraints;
//
//import org.passay.PasswordData;
//import org.passay.PasswordValidator;
//import org.passay.Rule;
//import org.passay.RuleResult;
//import org.passay.UsernameRule;
//
//import jakarta.validation.ConstraintValidator;
//
//public class UsernameRule implements ConstraintValidator<Password, String> {
//    //Rule: Password should not contain user-name
//    Rule rule = new UsernameRule();
//
//    PasswordValidator validator = new PasswordValidator(rule);
//    PasswordData password = new PasswordData("microsoft");
//      password.setUsername("micro");
//    RuleResult result = validator.validate(password);
//
//      if(result.isValid()){
//        System.out.println("Password validated.");
//    }else{
//        System.out.println("Invalid Password: " + validator.getMessages(result));
//    }
//}
