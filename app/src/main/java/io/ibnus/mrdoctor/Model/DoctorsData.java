package io.ibnus.mrdoctor.Model;

public class DoctorsData
{

    public  String doctor_name;
    public  String doctor_num;

    public DoctorsData() {
    }

    public DoctorsData(String doctor_name, String doctor_num) {
        this.doctor_name = doctor_name;
        this.doctor_num = doctor_num;
    }

    public String getDoctor_name() {
        return doctor_name;
    }

    public void setDoctor_name(String doctor_name) {
        this.doctor_name = doctor_name;
    }

    public String getDoctor_num() {
        return doctor_num;
    }

    public void setDoctor_num(String doctor_num) {
        this.doctor_num = doctor_num;
    }
}
