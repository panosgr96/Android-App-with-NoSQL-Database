package com.example.panagiotis.mytodolist;

/**
 * Created by Panagiotis on 23-May-17.
 */

public class UserInformation {
    public String email;
    public String name;
    public String surname;
    public String bDate;

    public UserInformation(){

    }

    public UserInformation(String email, String name, String surname, String bDate) {
        this.email = email;
        this.name = name;
        this.surname = surname;
        this.bDate = bDate;
    }

}
