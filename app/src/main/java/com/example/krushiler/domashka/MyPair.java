package com.example.krushiler.domashka;

public class MyPair<L, R> {
    private L l;
    private R r;
    public MyPair(L l, R r){
        this.l = l;
        this.r = r;
    }
    public L getFile(){ return l; }
    public R getDescription(){ return r; }
    public void setL(L l){ this.l = l; }
    public void setR(R r){ this.r = r; }
}
