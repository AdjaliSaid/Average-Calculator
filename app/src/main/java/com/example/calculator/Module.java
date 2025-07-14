package com.example.calculator;
public class Module {
    private String name;
    private int coef;
    private int cred;
    private float td;
    private float tp;
    private float exam;
    private float Average;

    public Module() {
        tp=-1;
        td=-1;
        exam=-1;
        Average=-1;
    }
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getCred() {
        return cred;
    }

    public void setCred(int cred) {
        this.cred = cred;
    }

    public int getCoef() {
        return coef;
    }

    public void setCoef(int coef) {
        this.coef = coef;
    }

    public float getTd() {
        return td;
    }

    public float getExam() {
        return exam;
    }

    public float getAverage() {
        return Average;
    }

    public void setAverage(float average) {
        Average = average;
    }

    public void setExam(float exam) {
        this.exam = exam;
    }

    public void setTd(float td) {
        this.td = td;
    }

    public float getTp() {
        return tp;
    }

    public void setTp(float tp) {
        this.tp = tp;
    }
}
