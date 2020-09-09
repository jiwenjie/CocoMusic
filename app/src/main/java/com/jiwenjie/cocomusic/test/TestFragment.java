package com.jiwenjie.cocomusic.test;

import android.os.Bundle;
import android.widget.TextView;

import com.jiwenjie.basepart.views.BaseFragment;
import com.jiwenjie.cocomusic.R;

import org.jetbrains.annotations.Nullable;

/**
 * author:Jiwenjie
 * email:278630464@qq.com
 * time:2019/05/12
 * desc:
 * version:1.0
 */
public class TestFragment extends BaseFragment {

   public static TestFragment newInstance(String title) {
      TestFragment fragment = new TestFragment();
      Bundle bundle = new Bundle();
      bundle.putString("test", title);
      fragment.setArguments(bundle);
      return fragment;
   }

   @Override
   protected int getLayoutId() {
      return R.layout.fragment_test;
   }

   @Override
   protected void initFragment(@Nullable Bundle savedInstanceState) {
      String test = getArguments().getString("test");
      ((TextView) getView().findViewById(R.id.fragmentText)).setText(test);
   }
}
