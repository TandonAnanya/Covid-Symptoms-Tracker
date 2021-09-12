package com.example.covidmc;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraCaptureSession;
import android.hardware.camera2.CameraDevice;
import android.hardware.camera2.CameraManager;
import android.hardware.camera2.CaptureRequest;
import android.os.HandlerThread;
import android.view.Surface;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;

import java.util.Collections;
import java.util.Objects;


class CameraView {
    private String cameraId;
    private final Activity activity;
    private CameraDevice cameraDevice;
    private CameraCaptureSession previewSession;

    private CaptureRequest.Builder previewCaptureRequestBuilder;

    CameraView(Activity _activity) {
        activity = _activity;
    }

    void start(final Surface previewSurface) {
        initiateCameraActivities(previewSurface);
    }

    void stop() {
        try {
            cameraDevice.close();
        } catch (Exception e) {

        }
    }

    void initiateCameraActivities(final Surface previewSurface){
        CameraManager cameraManager = (CameraManager) activity.getSystemService(Context.CAMERA_SERVICE);
        try {
            cameraId = Objects.requireNonNull(cameraManager).getCameraIdList()[0];
        } catch (CameraAccessException | NullPointerException e) {

        }

        try {

            if (ActivityCompat.checkSelfPermission(activity, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {

            }
            Objects.requireNonNull(cameraManager).openCamera(cameraId, new CameraDevice.StateCallback() {
                @Override
                public void onOpened(@NonNull CameraDevice camera) {
                    cameraDevice = camera;

                    CameraCaptureSession.StateCallback stateCallback = new CameraCaptureSession.StateCallback() {

                        @Override
                        public void onConfigured(@NonNull CameraCaptureSession session) {
                            previewSession = session;
                            try {

                                previewCaptureRequestBuilder = cameraDevice.createCaptureRequest(CameraDevice.TEMPLATE_PREVIEW);
                                previewCaptureRequestBuilder.addTarget(previewSurface); // this is previewSurface
                                previewCaptureRequestBuilder.set(CaptureRequest.FLASH_MODE, CaptureRequest.FLASH_MODE_TORCH);

                                HandlerThread thread = new HandlerThread("CameraPreview");
                                thread.start();

                                previewSession.setRepeatingRequest(previewCaptureRequestBuilder.build(), null, null);

                            } catch (CameraAccessException e) {

                            }
                        }

                        @Override
                        public void onConfigureFailed(@NonNull CameraCaptureSession session) {

                        }
                    };

                    try {
                        camera.createCaptureSession(Collections.singletonList(previewSurface), stateCallback, null); //1
                    } catch (CameraAccessException e) {

                    }
                }

                @Override
                public void onDisconnected(@NonNull CameraDevice camera) {
                }

                @Override
                public void onError(@NonNull CameraDevice camera, int error) {

                }
            }, null);
        } catch (CameraAccessException | SecurityException e) {

        }
    }

}