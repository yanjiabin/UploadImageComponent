package uploadimage.yanjiabin.com.uploadimage;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.bilibili.boxing.Boxing;
import com.bilibili.boxing.model.config.BoxingConfig;
import com.bilibili.boxing.model.entity.BaseMedia;
import com.uploadimage.component.MyBoxingActivity;
import com.uploadimage.component.utils.DisplayUtil;
import com.uploadimage.component.utils.ToastUtil;
import com.uploadimage.component.view.MultiImageUploadView;
import com.uploadimage.component.view.RoundImageUploadView;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private RoundImageUploadView uploadView;
    private TextView tvPicNumber;
    private static final int IMG_REQUEST_CODE = 0x010;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        uploadView = findViewById(R.id.upload_view);
        tvPicNumber = findViewById(R.id.tv_pic_number);
        setUploadView();
    }

    private void setUploadView() {
        uploadView.setAddHandlerImage(R.drawable.image_add);
        uploadView.setMax(6);
        uploadView.setNumCol(4);
        uploadView.setImgMargin(DisplayUtil.dp2px(this, 10));
        uploadView.setCloseHandlerImage(R.drawable.ic_delete_photo);
        uploadView.setOnImageChangeListener(new MultiImageUploadView.OnImageChangeListener() {
            @Override
            public void onImageChage(List<File> imgFiles, int maxSize) {
                tvPicNumber.setText(String.format("(%d/%d)", imgFiles.size(), maxSize));
            }
        });
        uploadView.setPadding(0, 0, 0, 0);
        uploadView.setAddClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startSelectImage();
            }
        });

        uploadView.setOnDelPicListener(new MultiImageUploadView.OnDelPicListener() {
            @Override
            public void onDelPicListener(int pos) {
                // TODO: 2019/2/22
                ToastUtil.setToast(pos+"");
            }
        });
    }


    private void startSelectImage() {
        BoxingConfig mulitImgConfig = new BoxingConfig(BoxingConfig.Mode.MULTI_IMG)
                .needCamera(R.drawable.ic_camera)
                .needGif()
                .withMaxCount(uploadView.getMax() - uploadView.getFiles().size());
        Boxing.of(mulitImgConfig).
                withIntent(this, MyBoxingActivity.class).
                start(this, IMG_REQUEST_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case IMG_REQUEST_CODE:
                    ArrayList<BaseMedia> images = Boxing.getResult(data);
                    if (images != null) {
                        for (BaseMedia image : images) {
                            uploadView.addFile(new File(image.getPath()));
                        }
                    }
                    // TODO: 2019/2/25
//                    refreshAdapter(images);

                    break;
            }
        }

    }
}
