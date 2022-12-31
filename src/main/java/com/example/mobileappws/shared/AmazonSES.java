package com.example.mobileappws.shared;

import com.amazonaws.regions.Regions;
import com.amazonaws.services.simpleemail.AmazonSimpleEmailService;
import com.amazonaws.services.simpleemail.AmazonSimpleEmailServiceClientBuilder;
import com.amazonaws.services.simpleemail.model.*;
import com.example.mobileappws.shared.dto.UserDto;
import lombok.extern.slf4j.Slf4j;

import static org.apache.commons.codec.CharEncoding.UTF_8;
@Slf4j
public class AmazonSES {
    private static final String FROM = "cristian.vlad10@gmail.com";
    private static final String SUBJECT = "One last step to complete your registration with PhotoApp";
    private static final String PASSWORD_RESET_SUBJECT = "Password reset request";
    private static final String HTML_BODY = """
            <h1>Please verify your email address</h1>
            <p>Thank you for registering with our mobile app. To complete the registration process, click on the following link: 
                <a href='http://localhost:8080/users/email-verification?token=$tokenValue'>
                    Final step to complete your registration </a><br/><br/></p>
                    Thank you !
            """;
    private static final String TEXT_BODY = """
            Please verify your email address.
            Thank you for registering with our mobile app. To complete the registration process, open the following URL in your browser window:
                http://localhost:8080/users/email-verification?token=$tokenValue
            Thank you !
            """;
    private static final String PASSWORD_RESET_HTML_BODY = """
            <h1>A request to reset your password</h1>
            <p>Hi, $firstName!</p>
            <p>Someone has requested to reset your password with our project. If it were not you, please ignore the message, 
            otherwise please click on the link below to set a new password: 
                <a href='http://localhost:8080/users/password-reset?token=$tokenValue'>
                Click this link to Reset Password</a><br/><br/></p>
                Thank you!
            """;
    private static final String PASSWORD_RESET_TEXT_BODY = """
            <h1>A request to reset your password</h1>
            <p>Hi, $firstName!</p>
            <p>Someone has requested to reset your password with our project. If it were not you, please ignore the message, 
            otherwise please click on the link below to set a new password: 
                http://localhost:8080/users/password-reset?token=$tokenValue
                Thank you!
            """;
    public static final String TOKEN_VALUE = "$tokenValue";

    public void verifyEmail(UserDto userDto) {
        var client = AmazonSimpleEmailServiceClientBuilder.standard().withRegion(Regions.EU_CENTRAL_1).build();

        String htmlBodyWithToken = HTML_BODY.replace(TOKEN_VALUE, userDto.getEmailVerificationToken());
        String textBodyWithToken = TEXT_BODY.replace(TOKEN_VALUE, userDto.getEmailVerificationToken());

        var request = new SendEmailRequest()
                .withDestination(new Destination().withToAddresses(userDto.getEmail()))
                .withMessage(new Message()
                        .withBody(new Body().withHtml(new Content().withCharset(UTF_8).withData(htmlBodyWithToken))
                                .withText(new Content().withCharset(UTF_8).withData(textBodyWithToken)))
                        .withSubject(new Content().withCharset(UTF_8).withData(SUBJECT)))
                .withSource(FROM);
        client.sendEmail(request);

        log.info("Email Sent!");
    }

    public boolean sendPasswordResetRequest(String name, String email, String token) {
        AmazonSimpleEmailService client = AmazonSimpleEmailServiceClientBuilder.standard().withRegion(Regions.EU_CENTRAL_1).build();

        String htmlBodyWithToken = PASSWORD_RESET_HTML_BODY.replace(TOKEN_VALUE, token);
        htmlBodyWithToken = htmlBodyWithToken.replace("$firstName", name);

        String textBodyWithToken = PASSWORD_RESET_TEXT_BODY.replace(TOKEN_VALUE, token);
        textBodyWithToken = textBodyWithToken.replace("$firstName", name);

        SendEmailRequest request = new SendEmailRequest()
                .withDestination(new Destination().withToAddresses(email))
                .withMessage(new Message().withBody(new Body().withHtml(new Content().withCharset(UTF_8).withData(htmlBodyWithToken))
                        .withText(new Content().withCharset(UTF_8).withData(textBodyWithToken)))
                        .withSubject(new Content().withCharset(UTF_8).withData(PASSWORD_RESET_SUBJECT)))
                .withSource(FROM);

        SendEmailResult result = client.sendEmail(request);

        return result != null && (result.getMessageId() != null && !result.getMessageId().isEmpty());
    }
}
