package com.keembay.scheduler.job.processor;

import com.bugsnag.Bugsnag;
import com.keembay.scheduler.dto.ArchiveNotificationDTO;
import com.keembay.scheduler.dto.UserRenewalDTO;
import com.keembay.scheduler.service.EmailRenewalUtil;
import com.keembay.scheduler.service.SMSRenewalUtil;
import com.sendgrid.Response;
import com.twilio.rest.api.v2010.account.Message;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.concurrent.TimeUnit;


@Slf4j
public class RestUserRenewalProcessor implements ItemProcessor<UserRenewalDTO, ArchiveNotificationDTO> {

    @Autowired
    private SMSRenewalUtil smsRenewalUtil;

    @Autowired
    private EmailRenewalUtil emailRenewalUtil;

    @Autowired
    private Bugsnag bugsnag;

    @Value("${renewal.warning.mail.subject}")
    private String renewalWarningSubject;

    @Value("${renewal.warning.body}")
    private String renewalWarningMsg;

    @Value("${renewal.points_reset.mail.subject}")
    private String renewalPointsResetSubject;

    @Value("${renewal.points_reset.body}")
    private String renewalPointsResetMsg;

    @Value("${renewal.reset.in.days}")
    private int jobsRenewalResetInDays;

    @Override
    public ArchiveNotificationDTO process(UserRenewalDTO user) throws Exception {

        String sendMsg,emailSubject,messageType,smsStatus,emailStatus;
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
        boolean userPassedWarningPeriod = userPassedWarningPeriod(user.getExpiryDate(),formatter);
        String pointsResetDate = getPointsResetDate(user.getExpiryDate(),formatter);

        if(!userPassedWarningPeriod){
            sendMsg = String.format(renewalWarningMsg,user.getUserName(),pointsResetDate);
            emailSubject = renewalWarningSubject;
            messageType="Warning";
        } else {
            sendMsg = String.format(renewalPointsResetMsg,user.getUserName(),pointsResetDate);
            emailSubject = renewalPointsResetSubject;
            messageType="ResetPoints";
        }

        smsStatus = sendSMS(user.getMobile(), sendMsg);
        emailStatus = sendEmail(user.getEmail(), emailSubject, sendMsg);

        ArchiveNotificationDTO archiveNotificationDTO =
                prepareToWrite(smsStatus, emailStatus, user, messageType, sendMsg, formatter);

        return archiveNotificationDTO;
    }

    private String getPointsResetDate(String expiryDate, SimpleDateFormat formatter) {
        Date d1 = null;
        try {
            d1 = formatter.parse(expiryDate);
        } catch (ParseException e) {
            bugsnag.notify(e);
            e.printStackTrace();
        }
        Calendar cal =Calendar.getInstance();
        cal.setTime(d1);
        cal.add(Calendar.DATE, jobsRenewalResetInDays);
        return formatter.format(cal.getTime());
    }

    private boolean userPassedWarningPeriod(String expiryDate, SimpleDateFormat formatter) {
        String dateStart = expiryDate;
        String dateStop = formatter.format(new Date());
        Date d1 = null;
        Date d2 = null;
        try {
            d1 = formatter.parse(dateStart);
            d2 = formatter.parse(dateStop);
        } catch (ParseException e) {
            bugsnag.notify(e);
            e.printStackTrace();
        }
        long diffInMillies = Math.abs(d2.getTime() - d1.getTime());
        long diff = TimeUnit.DAYS.convert(diffInMillies, TimeUnit.MILLISECONDS);
        return diff > jobsRenewalResetInDays;
    }

    private String sendSMS(String mobile, String sendMsg) {
        Message.Status statusMessage = smsRenewalUtil.sendMessage(mobile, sendMsg);
        if (Message.Status.FAILED == statusMessage || Message.Status.UNDELIVERED == statusMessage) {
            String error = "Error trying to send SMS: Status:" + statusMessage;
            log.error (error);
            return error;
        }
        return "success";
    }

    private String sendEmail(String email, String subject, String sendMsg) throws Exception{
        Response response =  emailRenewalUtil.sendEmailSendgrid(email, subject, sendMsg);
        if (response.getStatusCode() != 202) {
            String error ="Error trying to send the email: Status:" + response.getStatusCode();
            log.error (error);
            return error;
        }
        return "success";
    }

    private ArchiveNotificationDTO prepareToWrite(String smsStatus, String emailStatus, UserRenewalDTO user,
                                                  String messageType, String sendMsg, SimpleDateFormat formatter) throws Exception {

        if(smsStatus != "success") {
            sendMsg += smsStatus; //reflect failure in DB
        }
        if(emailStatus != "success") {
            sendMsg += emailStatus; //reflect failure in DB
        }

        //Save to DB as a metadata
        ArchiveNotificationDTO archiveNotificationDTO = new ArchiveNotificationDTO(user.getUserName(),
                user.getEmail(),
                user.getMobile(),
                formatter.parse(user.getExpiryDate()),
                messageType,
                sendMsg,
                new Date());

        return archiveNotificationDTO;
    }
}
