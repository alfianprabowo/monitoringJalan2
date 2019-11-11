package com.example.alfia.monitoringjalan.Module;

public class Module {

    private int references;
    private int ruas;
    private int rusak;
    private int ruang_jalan;
    private int penilik;
    private int coa;
    private int journal_book;
    private int trial_balance;
    private int users;
    private int groups;
    private int group_privilages;

    public Module(int references, int ruas, int rusak, int ruang_jalan, int penilik, int coa, int journal_book, int trial_balance, int users, int groups, int group_privilages) {
        this.references = references;
        this.ruas = ruas;
        this.rusak = rusak;
        this.ruang_jalan = ruang_jalan;
        this.penilik = penilik;
        this.coa = coa;
        this.journal_book = journal_book;
        this.trial_balance = trial_balance;
        this.users = users;
        this.groups = groups;
        this.group_privilages = group_privilages;
    }

    public Module(){

    }

    public int getReferences() {
        return references;
    }

    public int getRuas() {
        return ruas;
    }

    public int getRusak() {
        return rusak;
    }

    public int getRuang_jalan() {
        return ruang_jalan;
    }

    public int getPenilik() {
        return penilik;
    }

    public int getCoa() {
        return coa;
    }

    public int getJournal_book() {
        return journal_book;
    }

    public int getTrial_balance() {
        return trial_balance;
    }

    public int getUsers() {
        return users;
    }

    public int getGroups() {
        return groups;
    }

    public int getGroup_privilages() {
        return group_privilages;
    }
}
