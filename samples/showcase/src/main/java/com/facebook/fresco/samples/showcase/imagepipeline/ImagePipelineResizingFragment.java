/*
 * Copyright (c) Meta Platforms, Inc. and affiliates.
 *
 * This source code is licensed under the MIT license found in the
 * LICENSE file in the root directory of this source tree.
 */

package com.facebook.fresco.samples.showcase.imagepipeline;

import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import androidx.annotation.Nullable;
import androidx.annotation.StringRes;
import com.facebook.drawee.drawable.ScalingUtils;
import com.facebook.fresco.samples.showcase.BaseShowcaseFragment;
import com.facebook.fresco.samples.showcase.R;
import com.facebook.fresco.samples.showcase.imagepipeline.widget.ResizableFrameLayout;
import com.facebook.fresco.samples.showcase.misc.ImageUriProvider;
import com.facebook.fresco.vito.options.ImageOptions;
import com.facebook.fresco.vito.view.VitoView;
import com.facebook.imagepipeline.common.ResizeOptions;

/**
 * Fragment that illustrates how to use the image pipeline directly in order to create
 * notifications.
 */
public class ImagePipelineResizingFragment extends BaseShowcaseFragment {

  private static final String CALLER_CONTEXT = "ImagePipelineResizingFragment";

  private final SizeEntry[] SPINNER_ENTRIES_SIZE =
      new SizeEntry[] {
        new SizeEntry(null),
        new SizeEntry(ResizeOptions.forDimensions(2560, 1440)),
        new SizeEntry(ResizeOptions.forDimensions(1920, 1080)),
        new SizeEntry(ResizeOptions.forDimensions(1200, 1200)),
        new SizeEntry(ResizeOptions.forDimensions(720, 1280)),
        new SizeEntry(ResizeOptions.forSquareSize(800)),
        new SizeEntry(ResizeOptions.forDimensions(800, 600)),
        new SizeEntry(ResizeOptions.forSquareSize(480)),
        new SizeEntry(ResizeOptions.forDimensions(320, 240)),
        new SizeEntry(ResizeOptions.forDimensions(240, 320)),
        new SizeEntry(ResizeOptions.forDimensions(160, 90)),
        new SizeEntry(ResizeOptions.forSquareSize(100)),
        new SizeEntry(ResizeOptions.forSquareSize(64)),
        new SizeEntry(ResizeOptions.forSquareSize(16)),
      };

  private ImageFormatEntry[] mImageFormatEntries;

  private Button mButton;
  private ImageView mImage;
  private Spinner mSizeSpinner;
  private Spinner mFormatSpinner;

  @Nullable
  @Override
  public View onCreateView(
      LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
    return inflater.inflate(R.layout.fragment_imagepipeline_resizing, container, false);
  }

  @Override
  public void onViewCreated(final View view, @Nullable Bundle savedInstanceState) {
    setupImageFormatEntries(sampleUris());

    mButton = view.findViewById(R.id.button);
    mImage = view.findViewById(R.id.image);
    mSizeSpinner = view.findViewById(R.id.spinner_size);
    mFormatSpinner = view.findViewById(R.id.spinner_format);

    mSizeSpinner.setAdapter(new SimpleResizeOptionsAdapter());
    mSizeSpinner.setOnItemSelectedListener(
        new AdapterView.OnItemSelectedListener() {
          @Override
          public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
            reloadImage();
          }

          @Override
          public void onNothingSelected(AdapterView<?> parent) {}
        });
    mSizeSpinner.setSelection(0);

    mFormatSpinner.setAdapter(new SimpleImageFormatAdapter(mImageFormatEntries));
    mFormatSpinner.setOnItemSelectedListener(
        new AdapterView.OnItemSelectedListener() {
          @Override
          public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
            reloadImage();
          }

          @Override
          public void onNothingSelected(AdapterView<?> parent) {}
        });
    mFormatSpinner.setSelection(0);

    mImage.setOnClickListener(
        new View.OnClickListener() {
          @Override
          public void onClick(View v) {
            reloadImage();
          }
        });

    view.getViewTreeObserver()
        .addOnGlobalLayoutListener(
            new ViewTreeObserver.OnGlobalLayoutListener() {
              @Override
              public void onGlobalLayout() {
                ResizableFrameLayout mainImageFrameLayout =
                    (ResizableFrameLayout) view.findViewById(R.id.frame_main);
                mainImageFrameLayout.init(view.findViewById(R.id.btn_resize));

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                  view.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                } else {
                  view.getViewTreeObserver().removeGlobalOnLayoutListener(this);
                }
              }
            });
  }

  private void setupImageFormatEntries(ImageUriProvider imageUriProvider) {
    mImageFormatEntries =
        new ImageFormatEntry[] {
          new ImageFormatEntry(
              R.string.format_name_jpeg_landscape,
              imageUriProvider.createSampleUri(
                  ImageUriProvider.ImageSize.XXL, ImageUriProvider.Orientation.LANDSCAPE)),
          new ImageFormatEntry(
              R.string.format_name_jpeg_portrait,
              imageUriProvider.createSampleUri(
                  ImageUriProvider.ImageSize.XXL, ImageUriProvider.Orientation.PORTRAIT)),
          new ImageFormatEntry(
              R.string.format_name_png_landscape,
              imageUriProvider.createPngUri(
                  ImageUriProvider.Orientation.LANDSCAPE, ImageUriProvider.UriModification.NONE)),
          new ImageFormatEntry(
              R.string.format_name_png_portrait,
              imageUriProvider.createPngUri(
                  ImageUriProvider.Orientation.PORTRAIT, ImageUriProvider.UriModification.NONE)),
          new ImageFormatEntry(R.string.format_name_webp, imageUriProvider.createWebpStaticUri()),
          new ImageFormatEntry(
              R.string.format_name_animated_webp, imageUriProvider.createWebpAnimatedUri()),
          new ImageFormatEntry(
              R.string.format_name_translucent_webp, imageUriProvider.createWebpTranslucentUri()),
        };
  }

  private void reloadImage() {
    reloadImage(
        mImageFormatEntries[mFormatSpinner.getSelectedItemPosition()].uri,
        SPINNER_ENTRIES_SIZE[mSizeSpinner.getSelectedItemPosition()].resizeOptions);
  }

  private void reloadImage(Uri imageUri, @Nullable ResizeOptions resizeOptions) {
    VitoView.show(
        imageUri,
        ImageOptions.create()
            .resize(resizeOptions)
            .overlayRes(R.drawable.resize_outline)
            .scale(ScalingUtils.ScaleType.CENTER_CROP)
            .errorRes(R.color.primaryDark)
            .build(),
        CALLER_CONTEXT,
        mImage);
  }

  private class SimpleResizeOptionsAdapter extends BaseAdapter {

    @Override
    public int getCount() {
      return SPINNER_ENTRIES_SIZE.length;
    }

    @Override
    public Object getItem(int position) {
      return SPINNER_ENTRIES_SIZE[position];
    }

    @Override
    public long getItemId(int position) {
      return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
      final LayoutInflater layoutInflater = getLayoutInflater();

      final View view =
          convertView != null
              ? convertView
              : layoutInflater.inflate(
                  android.R.layout.simple_spinner_dropdown_item, parent, false);

      final TextView textView = (TextView) view.findViewById(android.R.id.text1);
      textView.setText(SPINNER_ENTRIES_SIZE[position].toString());

      return view;
    }
  }

  private class SizeEntry {

    final @Nullable ResizeOptions resizeOptions;

    SizeEntry(@Nullable ResizeOptions resizeOptions) {
      this.resizeOptions = resizeOptions;
    }

    @Override
    public String toString() {
      return resizeOptions == null
          ? getString(R.string.imagepipeline_resizing_disabled)
          : resizeOptions.toString();
    }
  }

  private class SimpleImageFormatAdapter extends BaseAdapter {

    private final ImageFormatEntry[] mImageFormatEntries;

    public SimpleImageFormatAdapter(ImageFormatEntry[] imageFormatEntries) {
      mImageFormatEntries = imageFormatEntries;
    }

    @Override
    public int getCount() {
      return mImageFormatEntries.length;
    }

    @Override
    public Object getItem(int position) {
      return mImageFormatEntries[position];
    }

    @Override
    public long getItemId(int position) {
      return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
      final LayoutInflater layoutInflater = getLayoutInflater();

      final View view =
          convertView != null
              ? convertView
              : layoutInflater.inflate(
                  android.R.layout.simple_spinner_dropdown_item, parent, false);

      final TextView textView = (TextView) view.findViewById(android.R.id.text1);
      textView.setText(mImageFormatEntries[position].nameResId);

      return view;
    }
  }

  private class ImageFormatEntry {

    final @StringRes int nameResId;
    final Uri uri;

    private ImageFormatEntry(@StringRes int nameResId, Uri uri) {
      this.nameResId = nameResId;
      this.uri = uri;
    }
  }
}
