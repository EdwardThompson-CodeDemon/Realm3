package com.realm.utils.Mail;
/**
 * Created by Edward Thompson on 19/01/2022.
 */

public interface MailActionCallback {
    void onMailSent();

    void onMailSendingFailed(Exception e);
    default void onRetry(int tries,int max_tries){

    }
    default void onActionLogged(String Log){


    }
}
