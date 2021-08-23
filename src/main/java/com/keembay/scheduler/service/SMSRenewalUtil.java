package com.keembay.scheduler.service;

import com.twilio.Twilio;
import com.twilio.rest.api.v2010.account.Message;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class SMSRenewalUtil {
    @Value("${smsAccountSID}")
    private String smsAccountSID;

    @Value("${smsAuthToken}")
    private String smsAuthToken;

    @Value("${smsFromTelephoneNumber}")
    private String smsFromTelephoneNumber;


    public Message.Status sendMessage(String toPhoneNumber, String textMessage) {
        Twilio.init(this.smsAccountSID, this.smsAuthToken);

        Message message = Message.creator(
                //TO
                new com.twilio.type.PhoneNumber(toPhoneNumber),
                //FROM
                new com.twilio.type.PhoneNumber(this.smsFromTelephoneNumber),textMessage)
                .create();
        Message.Status status = message.getStatus();

        return status;
    }
}
