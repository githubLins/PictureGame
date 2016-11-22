package cn.edu.bistu.se.cs.picturegame;


import android.graphics.Bitmap;
import java.util.ArrayList;
import java.util.List;
import cn.edu.bistu.se.cs.picturegame.ImagePiece;

public class ImageSpliter {
    public   List<ImagePiece>  splitImage(Bitmap bitmap, int piece) {
        List<ImagePiece> imagePieces = new ArrayList<ImagePiece>();
        int width = bitmap.getWidth();
        int height = bitmap.getHeight();
        int pieceWidth = width / piece;
        int pieceHeight = height / piece;
        for (int i = 0; i <piece; i++) {
            for (int j = 0; j <piece; j++) {
                ImagePiece imagePiece = new ImagePiece();

                imagePiece.setIndex(i * piece + j);
                int x = j * pieceWidth;
                int y = i * pieceHeight;
                imagePiece.setBitmap(Bitmap.createBitmap(bitmap, x, y, pieceWidth, pieceHeight));
                imagePieces.add(imagePiece);
            }
        }
        return imagePieces;

    }
}

