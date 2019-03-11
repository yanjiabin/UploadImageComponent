
# screenshot
<a href="pics/1.gif" width="40%"/></a> <img src="pics/1.gif" width="40%"/></a>
## 
Step 1. Add the JitPack repository to your build file</br>
Add it in your root build.gradle at the end of repositories:</br>
```Java
	allprojects {
		repositories {
			...
			maven { url 'https://jitpack.io' }
		}
	}
```
```Java
Step 2. Add the dependency

	dependencies {
	        implementation 'com.github.yanjiabin:UploadImageComponent:1.0'
	}
```
#### in your xml
```java
<?xml version="1.0" encoding="utf-8"?>
  <RelativeLayout
        android:layout_width="match_parent"
        android:layout_height="wrap_content">

        <com.uploadimage.component.view.RoundImageUploadView
            android:id="@+id/upload_view"
            android:layout_width="match_parent"
            android:layout_height="wrap_content"
            android:layout_alignParentLeft="true"
            android:layout_marginBottom="10dp"
            android:layout_marginLeft="10dp"
            android:layout_marginRight="30dp" />
        <TextView
            android:id="@+id/tv_pic_number"
            android:layout_width="wrap_content"
            android:layout_height="wrap_content"
            android:paddingBottom="5dp"
            android:paddingRight="10dp"
            android:text="(0/6)"
            android:layout_alignParentBottom="true"
            android:layout_alignParentRight="true"
            android:textColor="@color/font_gray_3" />
    </RelativeLayout>

```
```Java
     uploadView = findViewById(R.id.upload_view);
      tvPicNumber = findViewById(R.id.tv_pic_number);
      setUploadView();
      
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
```
