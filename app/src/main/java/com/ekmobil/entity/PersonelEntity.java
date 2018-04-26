package com.ekmobil.entity;

/**
 * Created by melih on 25.03.2018.
 */

public class PersonelEntity {
    private String id;
    private String personelImage;
    private String personelPhone;
    private String personelNameSurname;
    private String personelMail;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getPersonelImage() {
        return personelImage;
    }

    public void setPersonelImage(String personelImage) {
        this.personelImage = personelImage;
    }

    public String getPersonelPhone() {
        return personelPhone;
    }

    public void setPersonelPhone(String personelPhone) {
        this.personelPhone = personelPhone;
    }

    public String getPersonelNameSurname() {
        return personelNameSurname;
    }

    public void setPersonelNameSurname(String personelNameSurname) {
        this.personelNameSurname = personelNameSurname;
    }

    public String getPersonelMail() {
        return personelMail;
    }

    public void setPersonelMail(String personelMail) {
        this.personelMail = personelMail;
    }
}
