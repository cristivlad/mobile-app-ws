package com.example.mobileappws.shared;

import com.amazonaws.regions.Regions;
import com.amazonaws.services.simpleemail.AmazonSimpleEmailServiceClientBuilder;
import com.amazonaws.services.simpleemail.model.*;
import com.example.mobileappws.shared.dto.UserDto;
import lombok.extern.slf4j.Slf4j;

import static org.apache.commons.codec.CharEncoding.UTF_8;
@Slf4j
public class AmazonSES {
    private static final String FROM = "cristian.vlad10@gmail.com";
    private static final String SUBJECT = "One last step to complete your registration with PhotoApp";
    private static final String HTML_BODY = """
            <h1>Please verify your email address</h1>
            <p>Thank you for registering with our mobile app. To complete the registration process, click on the following link: 
                <a href='http://ec2-18-197-69-142.eu-central-1.compute.amazonaws.com:8080/email-verification.html?token=$tokenValue'>
                    Final step to complete your registration </a><br/><br/>
                    Thank you !
            """;
    private static final String TEXT_BODY = """
            Please verify your email address.
            Thank you for registering with our mobile app. To complete the registration process, open the following URL in your browser window:
                http://ec2-18-197-69-142.eu-central-1.compute.amazonaws.com:8080/verification-service/email-verification.html?token=$tokenValue
            Thank you !
            """;

    public void verifyEmail(UserDto userDto) {
        var client = AmazonSimpleEmailServiceClientBuilder.standard().withRegion(Regions.EU_CENTRAL_1).build();

        String htmlBodyWithToken = HTML_BODY.replace("$tokenValue", userDto.getEmailVerificationToken());
        String textBodyWithToken = TEXT_BODY.replace("$tokenValue", userDto.getEmailVerificationToken());

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
}
