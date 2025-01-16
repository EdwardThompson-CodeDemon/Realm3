package com.realm.Models;

import java.io.Serializable;

public class ApplicationVersion implements Serializable {
    public String app_id, branch_id, release_date, release_name, version_name, version_code, version_type, download_link, ratings;
    public String web_application_link;


    public String release_notes;
    public String icon;
    public String version_id;
    public String local_path;
}
