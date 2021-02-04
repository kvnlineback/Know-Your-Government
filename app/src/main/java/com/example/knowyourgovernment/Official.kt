package com.example.knowyourgovernment

import java.io.Serializable

class Official(var title: String, var name: String, var address: String, var party: String, var phone: String, var photoURL: String, var url: String, var channels: Map<String, String>, var email: String) : Serializable {
    override fun toString(): String {
        return "Official{" +
                "title='" + title + '\'' +
                ", url='" + url + '\'' +
                ", name='" + name + '\'' +
                ", address='" + address + '\'' +
                ", party='" + party + '\'' +
                ", phone='" + phone + '\'' +
                ", photoURL='" + photoURL + '\'' +
                ", email='" + email + '\'' +
                ", channels=" + channels +
                '}'
    }
}