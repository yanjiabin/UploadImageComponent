package com.uploadimage.component.view;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Environment;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.RelativeLayout;


import com.uploadimage.component.R;
import com.uploadimage.component.utils.DefaultMediaLoader;
import com.uploadimage.component.utils.FileUtil;
import com.uploadimage.component.utils.IMediaLoader;
import com.uploadimage.component.utils.ScreenUtils;
import com.uploadimage.component.utils.ToastUtil;

import java.io.File;
import java.util.ArrayList;
import java.util.List;


/**
 * 图片上传+展示控件
 *
 * @author WQ 2015年11月12日
 */
public class MultiImageUploadView extends FlowLayout {
    public String IMG_CACHE_DIR;
    public final int DEFAULT_COMPRESS_QUALITY = 30;
    ViewGroup handlerView;// 加号图片
    ImageSizeConfig sizeConfig;
    int numCol = 3;// 图片列数
    int max = 6;
    int closeRes = R.drawable.ic_delete_photo;// 删除按钮的资源
    List<File> files;//原始图片集合
    List<File> compress_files;//压缩后的图片集合
    String suffix[] = {"png", "jpg", "jpeg", "gif"};
    OnImageChangeListener onImageChangeListener;//内容改变监听
    OnItemClickListener onItemClickListener;
    IMediaLoader mediaLoader= DefaultMediaLoader.getInstance();//图片加载器
    int imgMargin;
    boolean isEditMode = true;//是否可编辑模式
    public MultiImageUploadView(Context context, AttributeSet attrs,
                                int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    public MultiImageUploadView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public MultiImageUploadView(Context context) {
        super(context);
        init();
    }

    public void setEditMode(boolean editMode) {
        isEditMode = editMode;
    }

    /**
     * 设置图片加载器 改由外部实现,控件不再关心图片的加载
     *
     * @param mediaLoader
     */
    public void setMediaLoader(IMediaLoader mediaLoader) {
        this.mediaLoader = mediaLoader;
    }

    /**
     * 设置内容改变监听
     *
     * @param onImageChangeListener
     */
    public void setOnImageChangeListener(OnImageChangeListener onImageChangeListener) {
        this.onImageChangeListener = onImageChangeListener;
    }

    public String[] getSuffix() {
        return suffix;
    }

    /**
     * 设置允许的后缀
     *
     * @param suffix
     */
    public void setSuffix(String[] suffix) {
        this.suffix = suffix;
    }

    public int getMax() {
        return max;
    }

    public int getNumCol() {
        return numCol;
    }

    /**
     * 设置展示的列数
     *
     * @param numCol
     */
    public void setNumCol(int numCol) {
        this.numCol = numCol;
        init();// 重新初始化
    }

    /**
     * 设置图片的尺寸配置
     *
     * @param sizeConfig
     */
    public void setImageSizeConfig(ImageSizeConfig sizeConfig) {
        this.sizeConfig = sizeConfig;
        init();
    }

    /**
     * 设置最大展示的图片数量
     *
     * @param max
     */
    public void setMax(int max) {
        this.max = max;
    }

    /**
     * 设置加号图片资源
     *
     * @param res
     */
    public void setAddHandlerImage(int res) {
        ((ImageView) handlerView.findViewById(R.id.handlerView)).setImageResource(res);
    }

    /**
     * 设置关闭图片资源
     *
     * @param res
     */
    public void setCloseHandlerImage(int res) {
        closeRes = res;
    }

    /**
     * 设置添加按钮的点击事件
     *
     * @param listener
     */
    public void setAddClickListener(View.OnClickListener listener) {
        handlerView.setOnClickListener(listener);
    }


    /**
     * 获取控件上展示的图片
     *
     * @return
     */
    public List<File> getFiles() {
        // return files;
        return compress_files;
    }


  protected  void updateHandlerStatus() {
        //添加按钮状态更新,以及图片改变事件回调
        handlerView.setVisibility(compress_files.size() < max ? View.VISIBLE
                : View.GONE);
        handlerView.setEnabled(compress_files.size() < max);
        if (onImageChangeListener != null) {
            onImageChangeListener.onImageChage(getFiles(), max);
        }
    }

    /**
     * 添加图片文件
     *
     * @param file
     * @param hasToast 出错时是否给予提示
     */
    public void addFile(final File file, boolean hasToast) {
        if (files.contains(file)) {
            if (hasToast)
                ToastUtil.setToast("图片已存在");
            return;
        }
        if (!file.toString().startsWith("http") && (file.length() == 0 || !file.exists())) {
            if (hasToast)
                ToastUtil.setToast("无效图片");
            if (!checkSuffix(file.getName())) {
                if (hasToast) {
                    ToastUtil.setToast("图片类型不允许!");
                }
                return;
            }
            return;
        }
        try {
            final String imgPath = dealNetPath(file.toString());
            fileCompress(compress_files, file, DEFAULT_COMPRESS_QUALITY);
            files.add(file);
            final int index = compress_files.size() - 1;
            View img = getImage(index,null, imgPath);
            addView(img, index);
            img.setId(index);
            updateHandlerStatus();
        } catch (Exception e) {
            e.printStackTrace();
            ToastUtil.setToast("图片添加出错!");
        }
    }

    void fileCompress(List<File> compress_files, File file, int quality) {
        String imgPath = file.toString();
        if (!imgPath.startsWith("http") && quality != 100) {
            String aimPath = new File(IMG_CACHE_DIR, file.getName())
                    .getAbsolutePath();
            String compress_path = FileUtil.saveBitmapToFile(imgPath,
                    aimPath, quality);// 压缩上传的图片质量

            File compress_file = new File(compress_path);
            compress_files.add(compress_file);
        } else {
            compress_files.add(file);
        }
    }

    /**
     * 删除缓存
     */
    public void clearCache() {
        for (File file : compress_files) {
            file.delete();
        }
    }

    /**
     * 添加图片文件
     *
     * @param file
     */
    public void addFile(File file) {
        addFile(file, true);
    }

    /**
     * 检查后缀
     *
     * @param filename
     * @return
     */
    private boolean checkSuffix(String filename) {
        filename = filename.toLowerCase();
        for (String suffix : this.suffix) {
            if (filename.endsWith(suffix))
                return true;
        }
        return false;
    }

    /**
     * 初始化
     */
    private void init() {
        if (isInEditMode()) return;
        if(IMG_CACHE_DIR==null){
            IMG_CACHE_DIR= Environment
                    .getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM)
                    .getAbsoluteFile()
                    + "/imageCache";
            ViewTreeObserver vto = getViewTreeObserver();
            vto.addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
                public boolean onPreDraw() {

                    int height = getMeasuredHeight();
                    int width = getMeasuredWidth();
//                tvValues.append("方法三: height:"+height + ",width:" + width + "..\n");
                    if(handlerView!=null && width!=0 && height!=0) {//重新计算添加按钮的宽高
                        getViewTreeObserver().removeOnPreDrawListener(this);
                        handlerView.setLayoutParams(getParams());
                    }
                    return true;
                }
            });
        }
        ImageView handlerView = (ImageView) findViewById(R.id.handlerView);
        removeAllViews();
        if (handlerView == null) {
            handlerView = new ImageView(getContext());
            handlerView.setId(R.id.handlerView);
            handlerView.setScaleType(ScaleType.FIT_XY);
            handlerView.setImageResource(R.drawable.image_add);
        }
        if (this.handlerView != null) {
            this.handlerView.removeAllViews();
        }
        if (sizeConfig == null) {
            sizeConfig = new SquareImageSizeConfig(this);
        }
        setHorizontalSpacing(dp2px(getContext(), 8));
        setVerticalSpacing(dp2px(getContext(), 8));
        setPadding(dp2px(getContext(), 8) + imgMargin, 0, 0, 0);
        LayoutParams params = getParams();
        RelativeLayout.LayoutParams p2 = new RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.MATCH_PARENT,
                RelativeLayout.LayoutParams.MATCH_PARENT);
        p2.topMargin = imgMargin;
        p2.rightMargin = imgMargin;
        RelativeLayout layout = new RelativeLayout(getContext());
        layout.setLayoutParams(getParams());
        layout.addView(handlerView, p2);
        addView(layout, params);
        this.handlerView = layout;
        files = new ArrayList<File>();
        compress_files = new ArrayList<File>();

    }

    /**
     * 创建图片视图
     *
     * @param bit
     * @param url
     * @return
     */
    View getImage(final int index, Bitmap bit, String url) {
        RelativeLayout layout = new RelativeLayout(getContext());

        layout.setLayoutParams(getParams());

        ImageView view = createImageView(getContext());
        RelativeLayout.LayoutParams p2 = new RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.MATCH_PARENT,
                RelativeLayout.LayoutParams.MATCH_PARENT);
        view.setScaleType(ScaleType.CENTER_CROP);
        p2.topMargin = imgMargin;
        p2.rightMargin = p2.topMargin;
        if (bit != null) {
            view.setImageBitmap(bit);
        } else {
            mediaLoader.displayThumbnail(view, url, sizeConfig.getWidth(), sizeConfig.getHeight());
        }
        layout.addView(view, p2);
        if(onItemClickListener!=null) {
            layout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onItemClickListener.onItemClick(MultiImageUploadView.this,v.getId());
                }
            });
        }
        // 删除按钮,编辑模式才展示
        if (isEditMode) {
            RelativeLayout.LayoutParams p1 = new RelativeLayout.LayoutParams(dp2px(getContext(), 20), dp2px(getContext(), 20));
            ImageView close = new ImageView(getContext());
            close.setImageResource(closeRes);
            layout.setId(files.size() - 1);
            p1.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
            layout.addView(close, p1);
            close.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    RelativeLayout lay = (RelativeLayout) v.getParent();
                    ViewGroup parent = (ViewGroup) lay.getParent();
                    int pos = parent.indexOfChild(lay);
                    files.remove(lay.getId());
                    compress_files.remove(lay.getId());
                    parent.removeView(lay);
                    updateHandlerStatus();
                    // // 重新设置子控件的索引位置
                    for (int i = 0; i < parent.getChildCount() - 1; i++) {
                        View child = parent.getChildAt(i);
                        child.setId(i);
                    }
                    if (mOnDelPicListener != null) {
                        mOnDelPicListener.onDelPicListener(pos);
                    }
                }
            });
        }
        return layout;
    }

    public interface OnDelPicListener{
        void onDelPicListener(int pos);
    }

    private OnDelPicListener mOnDelPicListener;
    public void setOnDelPicListener(OnDelPicListener onDelPicListener) {
        this.mOnDelPicListener = onDelPicListener;
    }

    protected ImageView createImageView(Context context){
        return  new ImageView(context);
    }
    public LayoutParams getParams() {
        LayoutParams params = new LayoutParams(sizeConfig.getWidth(), sizeConfig.getHeight());
        return params;
    }

    String dealNetPath(String path) {
        if (!path.startsWith("http://")) {
            path = path.replace("http:/", "http://");
            path = path.replace("https:/", "https://");
        }
        return path;
    }

    int dp2px(Context context, int dp) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp,
                context.getResources().getDisplayMetrics());
    }

    public int getImgMargin() {
        return imgMargin;
    }

    /**
     * 设置每个图的外边距
     * 主要用于需要将删除按钮悬浮半边的情况
     *
     * @param imgMargin
     */
    public void setImgMargin(int imgMargin) {
        this.imgMargin = imgMargin;
        init();
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    /**
     * 图片改变接口,删除添加都会回调
     */
    public interface OnImageChangeListener {
        public void onImageChage(List<File> imgFiles, int maxSize);
    }
    /**
     * 图片点击事件接口
     */
    public interface OnItemClickListener {
         void onItemClick(MultiImageUploadView parent, int position);
    }

    /**
     * 图片尺寸配置接口
     */
    public abstract static class ImageSizeConfig {
        public abstract int getWidth();//获取宽度

        public abstract int getHeight();//获取高度
    }

    /**
     * 正方形图片尺寸
     */
    public static class SquareImageSizeConfig extends ImageSizeConfig {
        int width = -1, height = -1;
        protected MultiImageUploadView uploadView;
        protected int WH[];

        public SquareImageSizeConfig(MultiImageUploadView uploadView) {
            this.uploadView = uploadView;

            WH = ScreenUtils.WHD(uploadView.getContext());
        }

        @Override
        public int getHeight() {
            return getWidth();
        }

        @Override
        public int getWidth() {
            int numCol = uploadView.getNumCol();
            ViewGroup.MarginLayoutParams layoutParams= (ViewGroup.MarginLayoutParams) uploadView.getLayoutParams();
//            int uploadViewWidth
//            uploadView.getWidth();
            width = (uploadView.getWidth()- uploadView.dp2px(uploadView.getContext(), 8 * (numCol - 1)) - uploadView.imgMargin)
                    / numCol;
//            width=width  -layoutParams.leftMargin-layoutParams.rightMargin;//减去
            return width;
        }
    }


    public interface IMediaCompresser{
        String compress(String sourcePath);
    }

    @Deprecated
    public void addImg(Intent data) {
    }

    @Deprecated
    public void attachActivity(Activity attachAct) {
    }

    @Deprecated
    public void attachActivity(Activity attachAct, int requestCode) {
    }
}
