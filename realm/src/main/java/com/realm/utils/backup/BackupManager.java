package com.realm.utils.backup;



import static com.realm.Realm.context;
import static com.realm.Realm.realm;

import android.app.Activity;
import android.content.Context;
import android.os.Build;
import android.util.Log;

import androidx.annotation.RequiresApi;

import com.jcraft.jsch.Channel;
import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.Session;
import com.jcraft.jsch.SftpException;
import com.realm.Models.BackupEntry;
import com.realm.Models.Query;
import com.realm.R;
import com.realm.Realm;
import com.realm.annotations.SyncDescription;
import com.realm.annotations.sync_service_description;
import com.realm.annotations.sync_status;
import com.realm.utils.Mail.MailActionCallback;
import com.realm.utils.Mail.MailData;
import com.realm.utils.Mail.OVHMailBuilder;
import com.realm.utils.svars;

import net.lingala.zip4j.ZipFile;
import net.lingala.zip4j.exception.ZipException;
import net.lingala.zip4j.model.FileHeader;
import net.lingala.zip4j.model.ZipParameters;
import net.lingala.zip4j.model.enums.EncryptionMethod;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URI;
import java.nio.file.CopyOption;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.StandardCopyOption;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;



public class BackupManager {
    static String logTag = "BackupManager";
//    ConstraintLayout main;
    Activity act;

    public BackupManager(Activity act) {
        this.act = act;
//        this.main = main;




    }


    public static void backupAppData(Context act, BackupListener backupListener) {
        String backupFile = "RBV_2_" + svars.getTransactionNo().replace(":", "_") + ".zip";
        backupListener.onStatusChanged(context.getResources().getString(R.string.verifying_files_to_backup));
        //   listener.on_secondary_status_changed("...");
        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        if (validateFilepath(svars.current_app_config(act).appDataFolder))
            backupListener.onStatusChanged(context.getResources().getString(R.string.employee_data_files_ok));
        if (validateFilepath(svars.current_app_config(act).databaseFolder))
            backupListener.onStatusChanged(context.getResources().getString(R.string.db_ok));
        if (validateFilepath(svars.current_app_config(act).logsFolder))
            backupListener.onStatusChanged(context.getResources().getString(R.string.log_files_ok));
        if (validateFilepath(svars.current_app_config(act).crashReportsFolder))
            backupListener.onStatusChanged(context.getResources().getString(R.string.trace_files_ok));

        String backup_folder = svars.current_app_config(act).file_path_general_backup + backupFile.replace(".zip", "") + "/";
        if (validateFilepath(backup_folder))
            backupListener.onStatusChanged(context.getResources().getString(R.string.backup_location_ok));
        ArrayList<String> backupList = new ArrayList<>();
        backupList.add(svars.current_app_config(act).databaseFolder + svars.current_app_config(act).DB_NAME);
        for (sync_service_description ssd : realm.getSyncDescription()) {
            if (ssd.servic_type == SyncDescription.service_type.Upload && ssd.storage_mode_check) {
//ArrayList<JSONObject> jsonObjects =Realm.databaseManager.load_dynamic_json_records_ann(ssd,new String[]{"sync_status='"+ sync_status.pending.ordinal()+"'"});
                ArrayList<JSONObject> jsonObjects = null;
                try {
                    jsonObjects = Realm.databaseManager.loadJSONArray(Class.forName(ssd.object_package), new Query().setTableFilters("sync_status='" + sync_status.pending.ordinal() + "'"));
                } catch (ClassNotFoundException e) {
                    e.printStackTrace();
                }
                int total = jsonObjects.size();
                for (int i = 0; i < total; i++) {
//                    JSONObject jsonObject =Realm.databaseManager.loadJSONObject(Class.forName(ssd.object_package),new Query().setTableFilters("sync_status='"+ sync_status.pending.ordinal()+"'"));
                    JSONObject jsonObject = jsonObjects.get(i);
                    Iterator keys = jsonObject.keys();
                    List<String> key_list = new ArrayList<>();
                    while (keys.hasNext()) {
                        key_list.add((String) keys.next());
                    }

                    for (String k : realm.getFilePathFields(ssd.object_package, key_list)) {
                        try {
//                            backupList.add(svars.current_app_config(Realm.context).file_path_employee_data + jsonObject.getString(k));
                            File file = new File(svars.current_app_config(Realm.context).appDataFolder + jsonObject.getString(k));
                            copyFile(file.getAbsolutePath(), backup_folder + "ApplicationFiles/" + file.getName());
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                }

            }
        }


try{
    for (File file : new File(svars.current_app_config(act).file_path_logs).listFiles()) {
        if (file.isFile()) {
//                backupList.add(file.getAbsolutePath());
            copyFile(file.getAbsolutePath(), backup_folder + "Logs/" + file.getName());
        }
    }
}catch (Exception exception){

}
    try{    for (File file : new File(svars.current_app_config(act).crashReportsFolder).listFiles()) {
            if (file.isFile()) {
//                backupList.add(file.getAbsolutePath());
                copyFile(file.getAbsolutePath(), backup_folder + "CrashReports/" + file.getName());
            }
        }
    }catch (Exception exception){

    }
        backupListener.onStatusChanged("Staging files");
        for (String s : backupList) {

            copyFile(s, backup_folder + new File(s).getName());
        }

//zip folder

        backupListener.onStatusChanged(context.getResources().getString(R.string.creating_archive));
        try {
            ZipParameters zipParameters = new ZipParameters();
            zipParameters.setEncryptFiles(true);
            zipParameters.setEncryptionMethod(EncryptionMethod.ZIP_STANDARD);

            ZipFile zipFile = new ZipFile(svars.current_app_config(act).file_path_general_backup + backupFile, "pass".toCharArray());
            zipFile.addFolder(new File(backup_folder), zipParameters);
        } catch (ZipException e) {
            e.printStackTrace();
        }
        BackupEntry backupEntry = new BackupEntry(backupFile, new File(svars.current_app_config(act).file_path_general_backup + backupFile).length() + "", svars.current_app_config(act).file_path_general_backup + backupFile);

        backupListener.onBackupArchiveCreated(backupEntry);
        backupListener.onStatusChanged(context.getResources().getString(R.string.archive_creation_complete));

        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        backupListener.onStatusChanged(context.getResources().getString(R.string.initializing_backup));

        if (svars.isBackupTypeActive(BackupType.Mail)) {
            backupListener.onStatusChanged(context.getResources().getString(R.string.email_backup_begun));
            backupListener.onBackupBegun(BackupType.Mail,backupEntry);

            sendEmailBackup(backupEntry, backupListener);
        }

        if (svars.isBackupTypeActive(BackupType.SFTP)) {
            backupListener.onBackupBegun(BackupType.SFTP,backupEntry);
            backupListener.onStatusChanged(context.getResources().getString(R.string.sftp_backup_begun));
            sendSFTPBackup(backupEntry, backupListener);
        }


        backupListener.onStatusChanged(context.getResources().getString(R.string.backup_complete));
//        svars.set_backup_time(act, Calendar.getInstance().getTime().toString().split("G")[0].trim());
        backupListener.onBackupComplete();


    }

    public static boolean validateFilepath(String filePath) {
        File file = new File(filePath);
        if (file.exists()) {
            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            return true;
        }
        file.mkdirs();
        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return file.exists();
    }

    private static void copyFile(String inputPath, String outputPath) {

        InputStream in = null;
        OutputStream out = null;
        try {

            //create output directory if it doesn't exist
            File dir = new File(new File(outputPath).getParent());
            if (!dir.exists()) {
                dir.mkdirs();
            }


            in = new FileInputStream(inputPath);
            out = new FileOutputStream(outputPath);

            byte[] buffer = new byte[1024];
            int read;
            while ((read = in.read(buffer)) != -1) {
                out.write(buffer, 0, read);
            }
            in.close();
            in = null;

            // write the output file
            out.flush();
            out.close();
            out = null;

            // delete the original file
//            new File(inputPath ).delete();


        } catch (FileNotFoundException fnfe1) {
            Log.e("tag", fnfe1.getMessage());
        } catch (Exception e) {
            Log.e("tag", e.getMessage());
        }

    }




    static void sendEmailBackup(BackupEntry backupEntry, BackupListener backupListener) {

            new OVHMailBuilder().from(svars.backupEmailConfiguration().username)
                    .setPassword(svars.backupEmailConfiguration().password)
                    .to(svars.backupEmail())
                    .subject("Application backup for " + Realm.context.getResources().getString(R.string.app_name))
                    .body(htmlBackupReport(backupEntry))
                    .addCCEmailAddress("idcapturefarmercontractor@capturesolutions.com")
                    .addAttachmentPath(new File(backupEntry.file_path),backupEntry.file_name)
                    .setBodyType(MailData.messageBodyType.HTML)
                    .setCallback(new MailActionCallback() {
                        @Override
                        public void onMailSent() {
                            backupListener.onBackupComplete(BackupType.Mail, backupEntry);

                        }

                        @Override
                        public void onActionLogged(String log) {
                            Log.e(logTag, log);
                            backupListener.onStatusChanged(log);
                        }

                        @Override
                        public void onMailSendingFailed(Exception ex) {
                            backupListener.onBackupFailed(BackupType.Mail, backupEntry);


                        }
                    })
                    .sendMail();
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    static String zipBackupReport(BackupEntry backupEntry) throws ZipException {
        StringBuilder reportBuilder = new StringBuilder();
        List<FileHeader> fileHeaders = new ZipFile(backupEntry.file_path, "pass".toCharArray()).getFileHeaders();
        fileHeaders.stream().forEach(fileHeader -> System.out.println(fileHeader.getFileName()));
        return null;
    }

    static String htmlBackupReport(BackupEntry backupEntry) {
        StringBuilder reportBuilder = new StringBuilder();
//        List<FileHeader> fileHeaders = new ZipFile(backupEntry.file_path, "pass".toCharArray()).getFileHeaders();
        reportBuilder.append("<!DOCTYPE html>\n" +
                "<html lang=\"en\" xmlns=\"http://www.w3.org/1999/xhtml\" xmlns:v=\"urn:schemas-microsoft-com:vml\" xmlns:o=\"urn:schemas-microsoft-com:office:office\">\n" +
                "<head>\n" +
                "    <meta charset=\"utf-8\"> <!-- utf-8 works for most cases -->\n" +
                "    <meta name=\"viewport\" content=\"width=device-width\"> <!-- Forcing initial-scale shouldn't be necessary -->\n" +
                "    <meta http-equiv=\"X-UA-Compatible\" content=\"IE=edge\"> <!-- Use the latest (edge) version of IE rendering engine -->\n" +
                "    <meta name=\"x-apple-disable-message-reformatting\">  <!-- Disable auto-scale in iOS 10 Mail entirely -->\n" +
                "    <title></title> <!-- The title tag shows in email notifications, like Android 4.4. -->\n" +
                "\n" +
                "    <link href=\"https://fonts.googleapis.com/css?family=Poppins:200,300,400,500,600,700\" rel=\"stylesheet\">\n" +
                "\n" +
                "    <!-- CSS Reset : BEGIN -->\n" +
                "    <style>\n" +
                "\n" +
                "        /* What it does: Remove spaces around the email design added by some email clients. */\n" +
                "        /* Beware: It can remove the padding / margin and add a background color to the compose a reply window. */\n" +
                "        html,\n" +
                "body {\n" +
                "    margin: 0 auto !important;\n" +
                "    padding: 0 !important;\n" +
                "    height: 100% !important;\n" +
                "    width: 100% !important;\n" +
                "    background: #f1f1f1;\n" +
                "}\n" +
                "\n" +
                "/* What it does: Stops email clients resizing small text. */\n" +
                "* {\n" +
                "    -ms-text-size-adjust: 100%;\n" +
                "    -webkit-text-size-adjust: 100%;\n" +
                "}\n" +
                "\n" +
                "/* What it does: Centers email on Android 4.4 */\n" +
                "div[style*=\"margin: 16px 0\"] {\n" +
                "    margin: 0 !important;\n" +
                "}\n" +
                "\n" +
                "/* What it does: Stops Outlook from adding extra spacing to tables. */\n" +
                "table,\n" +
                "td {\n" +
                "    mso-table-lspace: 0pt !important;\n" +
                "    mso-table-rspace: 0pt !important;\n" +
                "}\n" +
                "\n" +
                "/* What it does: Fixes webkit padding issue. */\n" +
                "table {\n" +
                "    border-spacing: 0 !important;\n" +
                "    border-collapse: collapse !important;\n" +
                "    table-layout: fixed !important;\n" +
                "    margin: 0 auto !important;\n" +
                "}\n" +
                "\n" +
                "/* What it does: Uses a better rendering method when resizing images in IE. */\n" +
                "img {\n" +
                "    -ms-interpolation-mode:bicubic;\n" +
                "}\n" +
                "\n" +
                "/* What it does: Prevents Windows 10 Mail from underlining links despite inline CSS. Styles for underlined links should be inline. */\n" +
                "a {\n" +
                "    text-decoration: none;\n" +
                "}\n" +
                "\n" +
                "/* What it does: A work-around for email clients meddling in triggered links. */\n" +
                "*[x-apple-data-detectors],  /* iOS */\n" +
                ".unstyle-auto-detected-links *,\n" +
                ".aBn {\n" +
                "    border-bottom: 0 !important;\n" +
                "    cursor: default !important;\n" +
                "    color: inherit !important;\n" +
                "    text-decoration: none !important;\n" +
                "    font-size: inherit !important;\n" +
                "    font-family: inherit !important;\n" +
                "    font-weight: inherit !important;\n" +
                "    line-height: inherit !important;\n" +
                "}\n" +
                "\n" +
                "/* What it does: Prevents Gmail from displaying a download button on large, non-linked images. */\n" +
                ".a6S {\n" +
                "    display: none !important;\n" +
                "    opacity: 0.01 !important;\n" +
                "}\n" +
                "\n" +
                "/* What it does: Prevents Gmail from changing the text color in conversation threads. */\n" +
                ".im {\n" +
                "    color: inherit !important;\n" +
                "}\n" +
                "\n" +
                "/* If the above doesn't work, add a .g-img class to any image in question. */\n" +
                "img.g-img + div {\n" +
                "    display: none !important;\n" +
                "}\n" +
                "\n" +
                "/* What it does: Removes right gutter in Gmail iOS app: https://github.com/TedGoas/Cerberus/issues/89  */\n" +
                "/* Create one of these media queries for each additional viewport size you'd like to fix */\n" +
                "\n" +
                "/* iPhone 4, 4S, 5, 5S, 5C, and 5SE */\n" +
                "@media only screen and (min-device-width: 320px) and (max-device-width: 374px) {\n" +
                "    u ~ div .email-container {\n" +
                "        min-width: 320px !important;\n" +
                "    }\n" +
                "}\n" +
                "/* iPhone 6, 6S, 7, 8, and X */\n" +
                "@media only screen and (min-device-width: 375px) and (max-device-width: 413px) {\n" +
                "    u ~ div .email-container {\n" +
                "        min-width: 375px !important;\n" +
                "    }\n" +
                "}\n" +
                "/* iPhone 6+, 7+, and 8+ */\n" +
                "@media only screen and (min-device-width: 414px) {\n" +
                "    u ~ div .email-container {\n" +
                "        min-width: 414px !important;\n" +
                "    }\n" +
                "}\n" +
                "\n" +
                "\n" +
                "    </style>\n" +
                "\n" +
                "    <!-- CSS Reset : END -->\n" +
                "\n" +
                "    <!-- Progressive Enhancements : BEGIN -->\n" +
                "    <style>\n" +
                "\n" +
                "\t    .primary{\n" +
                "\tbackground: #17bebb;\n" +
                "}\n" +
                ".bg_white{\n" +
                "\tbackground: #ffffff;\n" +
                "}\n" +
                ".bg_light{\n" +
                "\tbackground: #f7fafa;\n" +
                "}\n" +
                ".bg_black{\n" +
                "\tbackground: #000000;\n" +
                "}\n" +
                ".bg_dark{\n" +
                "\tbackground: rgba(0,0,0,.8);\n" +
                "}\n" +
                ".email-section{\n" +
                "\tpadding:2.5em;\n" +
                "}\n" +
                "\n" +
                "/*BUTTON*/\n" +
                ".btn{\n" +
                "\tpadding: 10px 15px;\n" +
                "\tdisplay: inline-block;\n" +
                "}\n" +
                ".btn.btn-primary{\n" +
                "\tborder-radius: 5px;\n" +
                "\tbackground: #17bebb;\n" +
                "\tcolor: #ffffff;\n" +
                "}\n" +
                ".btn.btn-white{\n" +
                "\tborder-radius: 5px;\n" +
                "\tbackground: #ffffff;\n" +
                "\tcolor: #000000;\n" +
                "}\n" +
                ".btn.btn-white-outline{\n" +
                "\tborder-radius: 5px;\n" +
                "\tbackground: transparent;\n" +
                "\tborder: 1px solid #fff;\n" +
                "\tcolor: #fff;\n" +
                "}\n" +
                ".btn.btn-black-outline{\n" +
                "\tborder-radius: 0px;\n" +
                "\tbackground: transparent;\n" +
                "\tborder: 2px solid #000;\n" +
                "\tcolor: #000;\n" +
                "\tfont-weight: 700;\n" +
                "}\n" +
                ".btn-custom{\n" +
                "\tcolor: rgba(0,0,0,.3);\n" +
                "\ttext-decoration: underline;\n" +
                "}\n" +
                "\n" +
                "h1,h2,h3,h4,h5,h6{\n" +
                "\tfont-family: 'Poppins', sans-serif;\n" +
                "\tcolor: #000000;\n" +
                "\tmargin-top: 0;\n" +
                "\tfont-weight: 400;\n" +
                "}\n" +
                "\n" +
                "body{\n" +
                "\tfont-family: 'Poppins', sans-serif;\n" +
                "\tfont-weight: 400;\n" +
                "\tfont-size: 15px;\n" +
                "\tline-height: 1.8;\n" +
                "\tcolor: rgba(0,0,0,.4);\n" +
                "}\n" +
                "\n" +
                "a{\n" +
                "\tcolor: #17bebb;\n" +
                "}\n" +
                "\n" +
                "table{\n" +
                "}\n" +
                "/*LOGO*/\n" +
                "\n" +
                ".logo h1{\n" +
                "\tmargin: 0;\n" +
                "}\n" +
                ".logo h1 a{\n" +
                "\tcolor: #17bebb;\n" +
                "\tfont-size: 24px;\n" +
                "\tfont-weight: 700;\n" +
                "\tfont-family: 'Poppins', sans-serif;\n" +
                "}\n" +
                "\n" +
                "/*HERO*/\n" +
                ".hero{\n" +
                "\tposition: relative;\n" +
                "\tz-index: 0;\n" +
                "}\n" +
                "\n" +
                ".hero .text{\n" +
                "\tcolor: rgba(0,0,0,.3);\n" +
                "}\n" +
                ".hero .text h2{\n" +
                "\tcolor: #000;\n" +
                "\tfont-size: 34px;\n" +
                "\tmargin-bottom: 0;\n" +
                "\tfont-weight: 200;\n" +
                "\tline-height: 1.4;\n" +
                "}\n" +
                ".hero .text h3{\n" +
                "\tfont-size: 24px;\n" +
                "\tfont-weight: 300;\n" +
                "}\n" +
                ".hero .text h2 span{\n" +
                "\tfont-weight: 600;\n" +
                "\tcolor: #000;\n" +
                "}\n" +
                "\n" +
                ".text-author{\n" +
                "\tbordeR: 1px solid rgba(0,0,0,.05);\n" +
                "\tmax-width: 50%;\n" +
                "\tmargin: 0 auto;\n" +
                "\tpadding: 2em;\n" +
                "}\n" +
                ".text-author img{\n" +
                "\tborder-radius: 50%;\n" +
                "\tpadding-bottom: 20px;\n" +
                "}\n" +
                ".text-author h3{\n" +
                "\tmargin-bottom: 0;\n" +
                "}\n" +
                "ul.social{\n" +
                "\tpadding: 0;\n" +
                "}\n" +
                "ul.social li{\n" +
                "\tdisplay: inline-block;\n" +
                "\tmargin-right: 10px;\n" +
                "}\n" +
                "\n" +
                "/*FOOTER*/\n" +
                "\n" +
                ".footer{\n" +
                "\tborder-top: 1px solid rgba(0,0,0,.05);\n" +
                "\tcolor: rgba(0,0,0,.5);\n" +
                "}\n" +
                ".footer .heading{\n" +
                "\tcolor: #000;\n" +
                "\tfont-size: 20px;\n" +
                "}\n" +
                ".footer ul{\n" +
                "\tmargin: 0;\n" +
                "\tpadding: 0;\n" +
                "}\n" +
                ".footer ul li{\n" +
                "\tlist-style: none;\n" +
                "\tmargin-bottom: 10px;\n" +
                "}\n" +
                ".footer ul li a{\n" +
                "\tcolor: rgba(0,0,0,1);\n" +
                "}\n" +
                "\n" +
                "\n" +
                "@media screen and (max-width: 500px) {\n" +
                "\n" +
                "\n" +
                "}\n" +
                "\n" +
                "\n" +
                "    </style>\n" +
                "\n" +
                "\n" +
                "</head>\n" +
                "\n" +
                "<body width=\"100%\" style=\"margin: 0; padding: 0 !important; mso-line-height-rule: exactly; background-color: #f1f1f1;\">\n" +
                "\t<center style=\"width: 100%; background-color: #f1f1f1;\">\n" +
                "    <div style=\"display: none; font-size: 1px;max-height: 0px; max-width: 0px; opacity: 0; overflow: hidden; mso-hide: all; font-family: sans-serif;\">\n" +
                "      &zwnj;&nbsp;&zwnj;&nbsp;&zwnj;&nbsp;&zwnj;&nbsp;&zwnj;&nbsp;&zwnj;&nbsp;&zwnj;&nbsp;&zwnj;&nbsp;&zwnj;&nbsp;&zwnj;&nbsp;&zwnj;&nbsp;&zwnj;&nbsp;&zwnj;&nbsp;&zwnj;&nbsp;&zwnj;&nbsp;&zwnj;&nbsp;&zwnj;&nbsp;&zwnj;&nbsp;\n" +
                "    </div>\n" +
                "    <div style=\"max-width: 600px; margin: 0 auto;\" class=\"email-container\">\n" +
                "    \t<!-- BEGIN BODY -->\n" +
                "      <table align=\"center\" role=\"presentation\" cellspacing=\"0\" cellpadding=\"0\" border=\"0\" width=\"100%\" style=\"margin: auto;\">\n" +
                "      \t<tr>\n" +
                "          <td valign=\"top\" class=\"bg_white\" style=\"padding: 1em 2.5em 0 2.5em;\">\n" +
                "          \t<table role=\"presentation\" border=\"0\" cellpadding=\"0\" cellspacing=\"0\" width=\"100%\">\n" +
                "          \t\t<tr>\n" +
                "          \t\t\t<td class=\"logo\" style=\"text-align: center;\">\n" +
                "\t\t\t            <h1><a href=\"#\">Capture Solutions</a></h1>\n" +
                "\t\t\t          </td>\n" +
                "          \t\t</tr>\n" +
                "          \t</table>\n" +
                "          </td>\n" +
                "\t      </tr><!-- end tr -->\n" +
                "\t\t\t\t<tr>\n" +
                "          <td valign=\"middle\" class=\"hero bg_white\" style=\"padding: 2em 0 4em 0;\">\n" +
                "            <table role=\"presentation\" border=\"0\" cellpadding=\"0\" cellspacing=\"0\" width=\"100%\">\n" +
                "            \t<tr>\n" +
                "            \t\t<td style=\"padding: 0 2.5em; text-align: center; padding-bottom: 3em;\">\n" +
                "            \t\t\t<div class=\"text\">\n" +
                "            \t\t\t\t<h2>Application backup for Idcapture farmer contractor</h2>\n" +
                "            \t\t\t</div>\n" +
                "            \t\t</td>\n" +
                "            \t</tr>\n" +
                "            \t<tr>\n" +
                "\t\t\t          <td style=\"text-align: center;\">\n" +
                "\t\t\t          \t<div class=\"text-author\">\n" +
                "\t\t\t\t          \t<img src=\"images/person_2.jpg\" alt=\"\" style=\"width: 100px; max-width: 600px; height: auto; margin: auto; display: block;\">\n" +
                "\t\t\t\t          \t<h3 class=\"name\">" + splitNL(backupEntry.file_name, 20) + "</h3>\n" +
                "\t\t\t\t          \t<span class=\"position\">Backup size:" + getUserDisplayBytes(backupEntry.file_size) + "</span>\n" +
                "\t\t\t\t           \t<p><a href=\"#\" class=\"btn btn-primary\">Download backup</a></p>\n" +
                "\t\t\t\t           \t<p><a href=\"#\" class=\"btn-custom\">Ignore Request</a></p>\n" +
                "\t\t\t           \t</div>\n" +
                "\t\t\t          </td>\n" +
                "\t\t\t        </tr>\n" +
                "            </table>\n" +
                "          </td>\n" +
                "\t      </tr><!-- end tr -->\n" +
                "      <!-- 1 Column Text + Button : END -->\n" +
                "      </table>\n" +
                "      <table align=\"center\" role=\"presentation\" cellspacing=\"0\" cellpadding=\"0\" border=\"0\" width=\"100%\" style=\"margin: auto;\">\n" +
                "      \t<tr>\n" +
                "          <td valign=\"middle\" class=\"bg_light footer email-section\">\n" +
                "            <table>\n" +
                "            \t<tr>\n" +
                "                <td valign=\"top\" width=\"33.333%\" style=\"padding-top: 20px;\">\n" +
                "                  <table role=\"presentation\" cellspacing=\"0\" cellpadding=\"0\" border=\"0\" width=\"100%\">\n" +
                "                    <tr>\n" +
                "                      <td style=\"text-align: left; padding-right: 10px;\">\n" +
                "                      \t<h3 class=\"heading\">About</h3>\n" +
                "                      \t<p>Capture solutions application backup.Realm v 1.0.190</p>\n" +
                "                      </td>\n" +
                "                    </tr>\n" +
                "                  </table>\n" +
                "                </td>\n" +
                "                <td valign=\"top\" width=\"33.333%\" style=\"padding-top: 20px;\">\n" +
                "                  <table role=\"presentation\" cellspacing=\"0\" cellpadding=\"0\" border=\"0\" width=\"100%\">\n" +
                "                    <tr>\n" +
                "                      <td style=\"text-align: left; padding-left: 5px; padding-right: 5px;\">\n" +
                "                      \t<h3 class=\"heading\">Contact Info</h3>\n" +
                "                      \t<ul>\n" +
                "\t\t\t\t\t                <li><span class=\"text\">Kenya-Nairobi- Parklands,6th avenue, valley view offic park,</span></li>\n" +
                "\t\t\t\t\t                <li><span class=\"text\">+254 715 300 161</span></a></li>\n" +
                "\t\t\t\t\t              </ul>\n" +
                "                      </td>\n" +
                "                    </tr>\n" +
                "                  </table>\n" +
                "                </td>\n" +
                "                <td valign=\"top\" width=\"33.333%\" style=\"padding-top: 20px;\">\n" +
                "                  <table role=\"presentation\" cellspacing=\"0\" cellpadding=\"0\" border=\"0\" width=\"100%\">\n" +
                "                    <tr>\n" +
                "                      <td style=\"text-align: left; padding-left: 10px;\">\n" +
                "                      \t<h3 class=\"heading\">Useful Links</h3>\n" +
                "                      \t<ul>\n" +
                "\t\t\t\t\t                <li><a href=\"https://www.cs4africa.com/\">Home</a></li>\n" +
                "                                    <li><a href=\"https://www.capturesolutions.com/id-capture\">IDCAPTURE</a></li>\n" +
                "                                    <li><a href=\"https://www.capturesolutions.com/timecapture\">Timecapture</a></li>\n" +
                "\t\t\t\t\t                <li><a href=\"https://www.cs4africa.com/agricapture/\">Agricapture</a></li>\n" +
                "                                    </ul>\n" +
                "                      </td>\n" +
                "                    </tr>\n" +
                "                  </table>\n" +
                "                </td>\n" +
                "              </tr>\n" +
                "            </table>\n" +
                "          </td>\n" +
                "        </tr><!-- end: tr -->\n" +
                "        <tr>\n" +
                "         \n" +
                "         \n" +
                "        </tr>\n" +
                "      </table>\n" +
                "\n" +
                "    </div>\n" +
                "  </center>\n" +
                "</body>\n" +
                "</html>");
        return reportBuilder.toString();
    }

    static String splitNL(String string, int len) {
        if (string.length() < len) return string;
        String out = "";
        for (int i = 0; i < string.length(); i++) {
            out += string.charAt(i) + ((i % len == 0) ? "\n" : "");
        }
        return out.trim();
    }

    public static String getUserDisplayBytes(String bytes) {

        long fileSizeInBytes = Long.parseLong(bytes);
        if (fileSizeInBytes > 1024) {
            long fileSizeInKB = fileSizeInBytes / 1024;
            if (fileSizeInKB > 1024) {
                long fileSizeInMB = fileSizeInKB / 1024;
                if (fileSizeInMB > 1024) {
                    double fileSizeInGB = (double) fileSizeInMB / (double) 1024;

                    return Math.round(fileSizeInGB) + " gb";


                } else {
                    return fileSizeInMB + " mb";

                }

            } else {
                return fileSizeInKB + " kb";
            }


        } else {
            return fileSizeInBytes + " bytes";
        }


    }

    static void sendSFTPBackup(BackupEntry backupEntry, BackupListener backupListener) {
        if (Thread.currentThread().isInterrupted()) {
            return;
        }
        Log.e(logTag, "Uploading SFTP : " + backupEntry.file_path);

        File uploadFilePath;
        Session session;
        Channel channel = null;
        ChannelSftp sftp = null;
        uploadFilePath = new File(backupEntry.file_path);


        JSch ssh = new JSch();
        int reconnection_count = 0;
        int max_reconnection_count = 10;
        while (true) {

            try {


                session = ssh.getSession("FarmerContractorBackup", "ta.cs4africa.com", 5032);
                session.setConfig("StrictHostKeyChecking", "no");
                session.setPassword("@FarmerContractorBackup123");
                session.connect();
                backupListener.onStatusChanged(Realm.context.getResources().getString(R.string.connected_to_sftp_server));


                channel = session.openChannel("sftp");
                channel.connect();
                sftp = (ChannelSftp) channel;

                try{
                    sftp.mkdir("User_" + svars.user_id(Realm.context));
                }catch (Exception ex){}
                sftp.cd("User_" + svars.user_id(Realm.context));


                break;
            } catch (Exception e) {
                Log.e("Upload error =>", "" + e.getMessage());
                backupListener.onBackupFailed(BackupType.SFTP, backupEntry);
//                listener.on_error_encountered(act.getString(R.string.connection_failed));
                if (reconnection_count < max_reconnection_count) {
                    reconnection_count++;
                    backupListener.onStatusChanged(Realm.context.getResources().getString(R.string.attempting_sftp_reconnection));

                } else {
//                    session.disconnect();
                    return;
                }

            }
        }
        backupListener.onStatusChanged(Realm.context.getResources().getString(R.string.uploading_backup));
        try {
            Log.e(logTag,"Upload file check:Exists:"+uploadFilePath.exists());
            FileInputStream fileInputStream = new FileInputStream(uploadFilePath);
//            ByteArrayInputStream fileInputStream2 =new ByteArrayInputStream( new FileInputStream(uploadFilePath));
            sftp.put(fileInputStream, uploadFilePath.getName(), null);
            fileInputStream.close();
        } catch (SftpException e) {
            Log.e(logTag,"Upload error:SftpException:"+e);
            backupListener.onBackupFailed(BackupType.SFTP, backupEntry);
            e.printStackTrace();
        } catch (FileNotFoundException e) {
            Log.e(logTag,"Upload error:FileNotFoundException:"+e);
            backupListener.onBackupFailed(BackupType.SFTP, backupEntry);
            e.printStackTrace();
        } catch (IOException e) {
            Log.e(logTag,"Upload error:IOException:"+e);
            backupListener.onBackupFailed(BackupType.SFTP, backupEntry);
            e.printStackTrace();
        }


        if (sftp.getExitStatus() == -1) {
            backupListener.onBackupComplete(BackupType.SFTP, backupEntry);
        } else {
            backupListener.onBackupFailed(BackupType.SFTP, backupEntry);


        }
    }







    @RequiresApi(api = Build.VERSION_CODES.O)
    public static void zipFolderNio(Path source) throws IOException {

        // get current working directory
        String currentPath = System.getProperty("user.dir") + File.separator;

        // get folder name as zip file name
        // can be other extension, .foo .bar .whatever
        String zipFileName = source.getFileName().toString() + ".zip";
        URI uri = URI.create("jar:file:" + currentPath + zipFileName);

        Files.walkFileTree(source, new SimpleFileVisitor<Path>() {
            @Override
            public FileVisitResult visitFile(Path file, BasicFileAttributes attributes) {

                // Copying of symbolic links not supported
                if (attributes.isSymbolicLink()) {
                    return FileVisitResult.CONTINUE;
                }

                Map<String, String> env = new HashMap<>();
                env.put("create", "true");

                try (FileSystem zipfs = FileSystems.newFileSystem(uri, env)) {

                    Path targetFile = file.relativize(file);
                    Path pathInZipfile = zipfs.getPath(targetFile.toString());

                    // NoSuchFileException, need create parent directories in zip path
                    if (pathInZipfile.getParent() != null) {
                        Files.createDirectories(pathInZipfile.getParent());
                    }

                    // copy file attributes
                    CopyOption[] options = {
                            StandardCopyOption.REPLACE_EXISTING,
                            StandardCopyOption.COPY_ATTRIBUTES,
                            LinkOption.NOFOLLOW_LINKS
                    };
                    // Copy a file into the zip file path
                    Files.copy(file, pathInZipfile, options);

                } catch (IOException e) {
                    e.printStackTrace();
                }

                return FileVisitResult.CONTINUE;
            }

            @Override
            public FileVisitResult visitFileFailed(Path file, IOException exc) {
                System.err.printf("Unable to zip : %s%n%s%n", file, exc);
                return FileVisitResult.CONTINUE;
            }

        });

    }






    public void zip(String[] _files, String zipFileName) {
        try {

            int BUFFER = 1250 * 1250;
            BufferedInputStream origin = null;
            FileOutputStream dest = new FileOutputStream(zipFileName);
            ZipOutputStream out = new ZipOutputStream(new BufferedOutputStream(
                    dest));
            byte data[] = new byte[BUFFER];

            for (int i = 0; i < _files.length; i++) {
                Log.e("Compression :", "Adding: " + _files[i]);
                FileInputStream fi = new FileInputStream(_files[i]);
                origin = new BufferedInputStream(fi, BUFFER);

                ZipEntry entry = new ZipEntry(_files[i].substring(_files[i].lastIndexOf("/") + 1));
                out.putNextEntry(entry);
                int count;

                while ((count = origin.read(data, 0, BUFFER)) != -1) {
                    out.write(data, 0, count);
                }
                origin.close();
            }

            out.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public enum BackupType {
        Null,
        Mail,
        SFTP,
        GoogleDrive

    }

    public interface BackupListener {
        void onBackupArchiveCreated(BackupEntry backupEntry);
        void onStatusChanged(String status);

        void onBackupComplete();

        void onBackupBegun(BackupType backupType, BackupEntry backupEntry);
        void onBackupComplete(BackupType backupType, BackupEntry backupEntry);

        void onBackupFailed(BackupType backupType, BackupEntry backupEntry);


    }


    class BackupItem {


    }


}
