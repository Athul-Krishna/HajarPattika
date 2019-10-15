package com.example.athulk.hajarpattika;

import android.util.SparseIntArray;

public class UndoData {
    private static UndoData SINGLE_INSTANCE = null;
    private int i;
    private SparseIntArray array;
    private UndoData(){
        i = -1;
        array = new SparseIntArray();
    }
    public int getPos(){return i;}
    public void putPos(int a){i = a;}

    public SparseIntArray getArray() {
        return array;
    }
    public void putArray(SparseIntArray arr){
        array = arr;
    }
    public static UndoData getInstance() {
        if(SINGLE_INSTANCE == null){
            SINGLE_INSTANCE = new UndoData();
        }
        return SINGLE_INSTANCE;
    }
}
