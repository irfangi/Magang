package com.oni;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import org.hibernate.validator.constraints.NotBlank;

import java.io.Serializable;

@JsonIgnoreProperties(value = {"createdAt", "updatedAt"},allowGetters = true)
public class Twitter implements Serializable{
    @NotBlank
    private String id;

    @NotBlank
    private String value;

    public Twitter(String id, String value) {
        this.id = id;
        this.value = value;
    }


    public String getId() {
        return id;
    }

    public String getValue() {
        return value;
    }
}
