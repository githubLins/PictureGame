package cn.edu.bistu.se.cs.picturegame;

import android.graphics.Bitmap;
import android.provider.ContactsContract;

public class  ImagePiece {
    private int index;
    private Bitmap bitmap;
    public  ImagePiece() {

    }
        public ImagePiece( int index){
            this.index = index;
        }
        public ImagePiece(Bitmap bitmap){
            this.bitmap=bitmap;
        }
    public ImagePiece(Bitmap bitmap,int index){
        this.bitmap=bitmap;
        this.index=index;
    }
    public int getIndex(){
        return index;
    }
    public void setIndex( int index){
        this.index=index;
    }
    public Bitmap getBitmap(){
        return bitmap;
    }
    public void setBitmap(Bitmap bitmap){
        this.bitmap=bitmap;
    }
    @Override
    public String toString(){
        return "ImagePiece{"+"index="+index+",bitmap="+bitmap+'}';
    }
}

