package com.realm.Services;

public class SyncParams {
    String authenticationurl,result_array_name,isok_link;
    public SyncParams setAuthenticationUrl(String url)
    {
        authenticationurl=url;
        return this;
    }
  public SyncParams setResultArrayName(String result_array_name)
    {
        this.result_array_name=result_array_name;
        return this;
    }
}
