package com.genymobile.pr;

import com.google.gson.annotations.SerializedName;

public class Repo {
    private String name;

    @SerializedName("full_name")
    private String fullName;

    public String getName() {
        return name;
    }

    public String getFullName() {
        return fullName;
    }
}
