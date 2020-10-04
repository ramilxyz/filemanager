package xyz.ramil.filemanager.view.fragments;

import android.app.Activity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;

import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.tabs.TabLayout;

import xyz.ramil.filemanager.R;
import xyz.ramil.filemanager.view.customview.LockableViewPager;
import xyz.ramil.filemanager.view.customview.ViewPagerAdapter;

public class BaseFragment extends Fragment {

    public ViewPagerAdapter adapter;
    private View view;
    private LockableViewPager viewPager;
    private TabLayout tabLayout;

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_base, container, false);

        viewPager = view.findViewById(R.id.viewPager);
        tabLayout = view.findViewById(R.id.tabLayout);

        viewPager.setOffscreenPageLimit(5);
        adapter = new ViewPagerAdapter(getChildFragmentManager());

        view.findViewById(R.id.swiper).setEnabled(false);

        MainFragment postFeed = new MainFragment();
        postFeed.setIsSaveScreen(false);
        adapter.addFrag(postFeed, getString(R.string.list_of_files));
        MainFragment savedPosts = new MainFragment();
        savedPosts.setIsSaveScreen(true);
        adapter.addFrag(savedPosts, getString(R.string.uploaded_files));

        viewPager.setAdapter(adapter);
        tabLayout.setupWithViewPager(viewPager);

        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                if (viewPager.getAdapter() != null && viewPager.getAdapter().getCount() > 2)
                    viewPager.setSwipeable(position != 0);
                InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Activity.INPUT_METHOD_SERVICE);
                View view = getActivity().getCurrentFocus();
                if (view == null) {
                    view = new View(getActivity());
                }
                imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
        if (viewPager.getAdapter() != null && viewPager.getAdapter().getCount() > 2)
            viewPager.setCurrentItem(1);

        return view;
    }
}
