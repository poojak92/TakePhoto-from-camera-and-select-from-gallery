package galleryimages.galleryimages.Camera;

import android.annotation.TargetApi;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.ExifInterface;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.android.cameraview.CameraView;

import java.io.ByteArrayInputStream;
import java.io.IOException;

import butterknife.ButterKnife;
import galleryimages.galleryimages.R;



        import butterknife.BindView;
        import butterknife.OnClick;

public class DocumentCaptureActivity extends AppCompatActivity {

    @BindView(R.id.cv_camera_view)
    CameraView mCameraView;

    @BindView(R.id.tv_document_info)
    TextView mTvDocumentInfo;
    @BindView(R.id.iv_scan_document)
    ImageView mIvScanDocument;
    @BindView(R.id.tv_document_name)
    TextView mTvDocumentName;
    @BindView(R.id.tv_document_description)
    TextView mTvDocumentDescription;
    @BindView(R.id.tv_cancel)
    TextView mTvCancel;
    @BindView(R.id.ll_vertical_camera_view)
    LinearLayout mLLVerticalCameraView;
    @BindView(R.id.ll_horizontal_camera_view)
    LinearLayout mLLHorizontalCameraView;
    @BindView(R.id.fl_capture)
    FrameLayout mFLParent;
    @BindView(R.id.iv_flash)
    ImageView mIvFlash;
    @BindView(R.id.progress_bar)
    ProgressBar mProgressBar;

    private View mViewTop;
    private View mViewLeft;
    private View mViewRight;
    private View mViewBottom;
    private LinearLayout mLLCameraOverlay;

    private boolean isCaptured = false;
    private boolean isFocusUnlocked = false;


    private static final int[] FLASH_OPTIONS = {
            CameraView.FLASH_OFF,
            CameraView.FLASH_ON,
            CameraView.FLASH_AUTO,
    };
    private int flashClickCnt = 0;
    private int flashImages[] = new int[]{R.drawable.ic_flash_off, R.drawable.ic_flash_on};
    private int flashOptionsAvailable = 2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_document_capture);
        ButterKnife.bind(this);

        // doChangeFragment(CaptureDocumentFragment.newInstance(), CaptureDocumentFragment.TAG, false);


        if (mCameraView != null) {
            mCameraView.addCallback(cameraCallback);
        }
       /* mUploadDocumentsSingleton = UploadDocumentsSingleton.getInstance();
        mSelectedDocument = mUploadDocumentsSingleton.getmClickedDocument();
        if (mSelectedDocument.isSefie()) {
            mIvFlash.setVisibility(View.GONE);
            mCameraView.setFacing(CameraView.FACING_FRONT);
        } else {
            mCameraView.setFacing(CameraView.FACING_BACK);
        }*/

        initView();
    }

    public CameraView.Callback cameraCallback = new CameraView.Callback() {
        @Override
        public void onCameraOpened(CameraView cameraView) {
            super.onCameraOpened(cameraView);
        }

        @Override
        public void onCameraClosed(CameraView cameraView) {
            super.onCameraClosed(cameraView);
        }

        @Override
        public void onPictureTaken(CameraView cameraView, byte[] data) {
            isCaptured = true;
            checkIfCaptured(data);
        }

        @Override
        public void onFocusUnlock(CameraView cameraView, byte[] data) {
            isFocusUnlocked = true;
            checkIfCaptured(data);
        }
    };

    @OnClick(R.id.iv_capture)
    public void onClickCapture() {

        mProgressBar.setVisibility(View.VISIBLE);
        isCaptured =false;
        isFocusUnlocked = false;
        mCameraView.takePicture();
    }


    @Override
    public void onResume() {
        super.onResume();
        if (mCameraView != null) {
            mCameraView.start();
            /*mCameraView.setAspectRatio(AspectRatio.of(8,4));*/
        }
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    // As this method called just before fragment get killed and meanwhile all camera view's action done
    // we are stopping camera here.
    // If stop camera in onDestroy camera view's (Not release all resources) action not completes and app crashes
   /* @Override
    public void onDetach() {
        super.onDetach();
        if (mCameraView != null) {
            mCameraView.stop();
        }
    }*/

    @TargetApi(Build.VERSION_CODES.N)
    public int getRotateDegrees(final byte[] data) {
        ExifInterface exifInterface = null;
        try {
            exifInterface = new ExifInterface(new ByteArrayInputStream(data));
        } catch (IOException e) {
            e.printStackTrace();
        }
        int orientation = exifInterface.getAttributeInt(ExifInterface.TAG_ORIENTATION, 1);
        int rotationDegrees = 0;
        switch (orientation) {
            case ExifInterface.ORIENTATION_ROTATE_90:
                rotationDegrees = 90;
                break;
            case ExifInterface.ORIENTATION_ROTATE_180:
                rotationDegrees = 180;
                break;
            case ExifInterface.ORIENTATION_ROTATE_270:
                rotationDegrees = 270;
                break;
        }

        return rotationDegrees;
    }

    private void initView() {

        setFlashImage();
       /* mTvDocumentInfo.setText(mSelectedDocument.getDocumentBottomText());

        String documentName = mSelectedDocument.getDocumentName();

        if(documentName.contains("\n")){
            documentName = documentName.replace('\n',' ' );
        }

        if(mSelectedDocument.isBackToBeUploaded()){
            int capturetingSide =  mUploadDocumentsSingleton.getCapturingSide();
            if(capturetingSide == UploadDocumentsSingleton.BACK){
                documentName = documentName +"\n"+"(Back)";
            }else if(capturetingSide == UploadDocumentsSingleton.FRONT){
                documentName = documentName +"\n"+"(Front)";
            }
        }

        if(mSelectedDocument.isSefie()){
            mTvDocumentName.setText(mParentActivity.getString(R.string.file_name_selfie));
        }else {
            mTvDocumentName.setText(documentName);
        }

        mTvDocumentDescription.setText(mSelectedDocument.getDocumentDescription());
        mIvScanDocument.setImageDrawable(mSelectedDocument.getScanDocumentDrawable());

        if(mSelectedDocument.isSefie() || !mSelectedDocument.isOrientationHorizontal()){
            mFLParent.removeView(mLLHorizontalCameraView);
        }else if(mSelectedDocument.isOrientationHorizontal()){
            mFLParent.removeView(mLLVerticalCameraView);
        }*/

        getViews();
    }


    private void getViews(){
        mViewLeft = findViewById(R.id.view_left);
        mViewTop = findViewById(R.id.view_top);
        mViewRight = findViewById(R.id.view_right);
        mViewBottom = findViewById(R.id.view_bottom);
        mLLCameraOverlay = findViewById(R.id.ll_camera_overlay);
    }

    private Bitmap cropBitmap(Bitmap source){
        int desHeight = 0;
        int x1 = 0, x2 = 0, y1 = 0, y2 = 0;

        desHeight = mCameraView.getHeight() + (mViewLeft.getWidth() * 2) + mViewTop.getHeight();
        x1 = mViewLeft.getWidth();
        y1 = mViewTop.getHeight();
        x2 = mLLCameraOverlay.getWidth();
        y2 = mLLCameraOverlay.getHeight();

        /*if(mSelectedDocument.isSefie()){
            y1 = mViewTop.getBottom();
        }*/

        Bitmap bmp = Bitmap.createScaledBitmap(source, mCameraView.getWidth(), desHeight, true);
        Bitmap croppedBitmap = Bitmap.createBitmap(bmp, x1, y1, x2, y2);


        return croppedBitmap;

    }

    @OnClick(R.id.iv_flash)
    public void onClickFlashImage(){
        flashClickCnt++;
        if(flashClickCnt == flashOptionsAvailable){
            flashClickCnt = 0;
        }
        setFlashImage();
    }

 /*   @OnClick(R.id.tv_cancel)
    public void onClickCancel(){
        mParentActivity.onBackPressed();
    }
*/
    public void setFlashImage(){
        mIvFlash.setImageDrawable(ContextCompat.getDrawable(this,flashImages[flashClickCnt]));
        mCameraView.setFlash(FLASH_OPTIONS[flashClickCnt]);
    }

    public void checkIfCaptured(byte[] data){

        if(isCaptured && isFocusUnlocked){
            mProgressBar.setVisibility(View.GONE);
            BitmapFactory.Options options = new BitmapFactory.Options();
            Bitmap mBitmap = BitmapFactory.decodeByteArray(data, 0, data.length, options);
            int rotateDegrees = getRotateDegrees(data);
           /* mBitmap = CommanUtils.rotateBitmap(mBitmap, rotateDegrees);

            UploadDocumentsSingleton.getInstance().setmCapturedBitmap(mBitmap);
            if (mUploadDocumentsSingleton.getCapturingSide() == UploadDocumentsSingleton.FRONT) {
                mUploadDocumentsSingleton.setmCapturedBitmap(cropBitmap(mBitmap));
            } else if (mUploadDocumentsSingleton.getCapturingSide() == UploadDocumentsSingleton.BACK) {
                mUploadDocumentsSingleton.setmCapturedBackBitmap(cropBitmap(mBitmap));
            }

            mParentActivity.doChangeFragment(ConfirmDocumentFragment.newInstance(), ConfirmDocumentFragment.TAG, false);*/
        }

    }
}
