package com.uploadimage.component.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.util.Log;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * Created by DELL on 2017/12/1.
 */

public class FileUtil {
    /** 目录根路径 */
    public static final String DIR_HOME;
    /** 回答的录音音频 */
    public static final String DIR_REPLY_AUDIOS;
    static {
        DIR_HOME = Environment
                .getExternalStorageDirectory() + "/UNI/";
        DIR_REPLY_AUDIOS = DIR_HOME + "ReplyAudios/";
    }

    /**
     * 判断SD卡是否可用
     * @return
     */
    public static boolean isSDCardEnable() {
        return Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED);
    }



    public static String saveBitmapToFile(String aimpath, String _file, int quality) {
        File file = new File(_file);
        return file.exists() && file.length() != 0L?_file:(saveBitmap(BitmapFactory.decodeFile(aimpath), _file, quality)?_file:aimpath);
    }
    public static boolean saveBitmapToFile(Bitmap bitmap, String _file) {
        return saveBitmap(bitmap, _file, 100);
    }

    private static boolean saveBitmap(Bitmap bitmap, String _file, int quality) {
        BufferedOutputStream os = null;

        try {
            File file = new File(_file);
            int end = _file.lastIndexOf(File.separator);
            String _filePath = _file.substring(0, end);
            File filePath = new File(_filePath);
            if(!filePath.exists()) {
                filePath.mkdirs();
            }

            file.createNewFile();
            os = new BufferedOutputStream(new FileOutputStream(file));
            bitmap.compress(Bitmap.CompressFormat.JPEG, quality, os);
            boolean var8 = true;
            return var8;
        } catch (IOException var18) {
            Log.e("-->", var18.getMessage() + "  " + _file);
            var18.printStackTrace();
        } finally {
            if(os != null) {
                try {
                    os.close();
                } catch (IOException var17) {
                    Log.e("-->", var17.getMessage(), var17);
                }
            }

        }

        return false;
    }

    public static boolean copyAsset(Context ctx, String assetName, String destinationPath) throws IOException {
        InputStream in = ctx.getAssets().open(assetName);
        File f = new File(destinationPath);
        f.createNewFile();
        OutputStream out = new FileOutputStream(new File(destinationPath));

        byte[] buffer = new byte[1024];
        int read;
        while ((read = in.read(buffer)) != -1) {
            out.write(buffer, 0, read);
        }
        in.close();
        out.close();

        return true;
    }

    public static String extractFileNameFromURL(String url) {
        return url.substring(url.lastIndexOf('/') + 1);
    }

}
