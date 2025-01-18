package com.realm.mail;
/**
 * Created by Edward Thompson on 19/01/2022.
 * @deprecated Gmail no longer supports insecure login behaviour
 */
@Deprecated
public class GmailMailBuilder extends MailBuilder {

    public  GmailMailBuilder()
    {
        md.hostAddress="smtp.gmail.com";
        md.port="587";

    }
}
