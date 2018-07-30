package com.testWork;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.HttpURLConnection;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Виталик on 28.07.2018.
 */
public class Request {
    private final String USER_AGENT = "Mozilla/5.0";
    private String url;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Request request = (Request) o;

        if (USER_AGENT != null ? !USER_AGENT.equals(request.USER_AGENT) : request.USER_AGENT != null) return false;
        return url != null ? url.equals(request.url) : request.url == null;

    }

    @Override
    public int hashCode() {
        int result = USER_AGENT != null ? USER_AGENT.hashCode() : 0;
        result = 31 * result + (url != null ? url.hashCode() : 0);
        return result;
    }

    public Request(String url){
        this.url=url;
    }

    public boolean send(){
        boolean result = false;
        try {
            result = sendGet();
        } catch (Exception e) {
            System.err.println(e.getMessage());
        }
        return result;
    }

    private Boolean sendGet() throws Exception {

        URL obj = new URL(url);
        HttpURLConnection con = (HttpURLConnection) obj.openConnection();

        // optional default is GET
        con.setRequestMethod("GET");
        //add request header
        con.setRequestProperty("User-Agent", USER_AGENT);
        boolean result=false;
        if(con.getResponseCode()==200){
            result=true;
        }
        return result;

    }
}
