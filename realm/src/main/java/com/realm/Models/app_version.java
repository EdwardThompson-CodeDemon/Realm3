package com.realm.Models;

import com.realm.annotations.DynamicProperty;
import com.realm.annotations.RealmModel;

import java.io.Serializable;


//@DynamicClass(table_name = "app_versions_table")
public class app_version extends RealmModel implements Serializable {


        @DynamicProperty(json_key = "version_name")
        public String version_name="";

        @DynamicProperty(json_key = "version_code")
        public String version_code="";

        @DynamicProperty(json_key = "release_name")
        public String release_name="";

  @DynamicProperty(json_key = "release_time")
  public String release_time="";

  @DynamicProperty(json_key = "release_notes")
  public String release_notes="";

  @DynamicProperty(json_key = "creation_time")
  public String creation_time="";


 @DynamicProperty(json_key = "download_link")
  public String download_link="";



 @DynamicProperty(json_key = "file")
  public String file="";

@DynamicProperty(json_key = "local_path")
  public String local_path="";


@DynamicProperty(json_key = "icon")
  public String icon="";






        public app_version()
        {




        }


}
