package com.realm.mail;
/**
 * Created by Edward Thompson on 19/01/2022.
 */

public class OVHMailBuilder extends MailBuilder {
    public OVHMailBuilder()
    {
        md.hostAddress="ssl0.ovh.net";
        md.port="587";

    }
}
