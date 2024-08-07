/*
 * Copyright (c) Meta Platforms, Inc. and affiliates.
 *
 * This source code is licensed under the MIT license found in the
 * LICENSE file in the root directory of this source tree.
 */

package com.facebook.fresco.samples.showcase.vito;

import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import androidx.annotation.Nullable;
import com.facebook.fresco.samples.showcase.BaseShowcaseFragment;
import com.facebook.fresco.samples.showcase.R;
import com.facebook.fresco.samples.showcase.misc.ImageUriProvider;
import com.facebook.fresco.vito.options.ImageOptions;
import com.facebook.fresco.vito.view.VitoView;
import com.facebook.imagepipeline.common.RotationOptions;

/** Simple vito fragment that just displays an image. */
public class VitoRotationFragment extends BaseShowcaseFragment {

  private static final String CALLER_CONTEXT = "VitoRotationFragment";

  private ImageView mImageView;
  private Uri mUri;

  @Nullable
  @Override
  public View onCreateView(
      LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
    return inflater.inflate(R.layout.fragment_vito_rotation, container, false);
  }

  @Override
  public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
    mUri = sampleUris().createSampleUri(ImageUriProvider.ImageSize.M);

    mImageView = view.findViewById(R.id.image);
    final Spinner spinner = view.findViewById(R.id.spinner);

    final SimpleRotationOptionsAdapter adapter = new SimpleRotationOptionsAdapter();

    spinner.setAdapter(adapter);
    spinner.setOnItemSelectedListener(
        new AdapterView.OnItemSelectedListener() {
          @Override
          public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
            final SimpleRotationOptionsAdapter.Entry spinnerEntry =
                (SimpleRotationOptionsAdapter.Entry) adapter.getItem(position);
            setRotationOptions(spinnerEntry.rotationOptions);
          }

          @Override
          public void onNothingSelected(AdapterView<?> parent) {}
        });
    spinner.setSelection(0);
  }

  private void setRotationOptions(RotationOptions rotationOptions) {
    VitoView.show(
        mUri, ImageOptions.create().rotate(rotationOptions).build(), CALLER_CONTEXT, mImageView);
  }

  public class SimpleRotationOptionsAdapter extends BaseAdapter {

    private final Entry[] SPINNER_ENTRIES =
        new Entry[] {
          new Entry(RotationOptions.disableRotation(), "disableRotation"),
          new Entry(RotationOptions.autoRotate(), "autoRotate"),
          new Entry(RotationOptions.autoRotateAtRenderTime(), "autoRotateAtRenderTime"),
          new Entry(RotationOptions.forceRotation(RotationOptions.NO_ROTATION), "NO_ROTATION"),
          new Entry(RotationOptions.forceRotation(RotationOptions.ROTATE_90), "ROTATE_90"),
          new Entry(RotationOptions.forceRotation(RotationOptions.ROTATE_180), "ROTATE_180"),
          new Entry(RotationOptions.forceRotation(RotationOptions.ROTATE_270), "ROTATE_270"),
        };

    @Override
    public int getCount() {
      return SPINNER_ENTRIES.length;
    }

    @Override
    public Object getItem(int position) {
      return SPINNER_ENTRIES[position];
    }

    @Override
    public long getItemId(int position) {
      return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
      final LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());

      final View view =
          convertView != null
              ? convertView
              : layoutInflater.inflate(
                  android.R.layout.simple_spinner_dropdown_item, parent, false);

      final TextView textView = view.findViewById(android.R.id.text1);
      textView.setText(SPINNER_ENTRIES[position].description);

      return view;
    }

    public class Entry {

      final RotationOptions rotationOptions;
      final String description;

      private Entry(RotationOptions rotationOptions, String description) {
        this.rotationOptions = rotationOptions;
        this.description = description;
      }
    }
  }
}
