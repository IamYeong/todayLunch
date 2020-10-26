package com.todaylunch.unknown;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.lang.reflect.Array;
import java.util.ArrayList;


public class fragment4 extends Fragment {

    ArrayList<ListObject2> arrayList;
    RecyclerView recyclerView;
    MyAdapter_Setting mAdapter;
    TextView tvTitle;
    TypefaceUtil typefaceUtil;
    int fontNumber;

    public fragment4() {
        // Required empty public constructor
    }



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        View view = inflater.inflate(R.layout.fragment_fragment4, container, false);

        arrayList = new ArrayList<>();
        addMenu();

        typefaceUtil = new TypefaceUtil(getContext());

        fontNumber = MainActivity.FONT_NUMBER;
        tvTitle = view.findViewById(R.id.tv_fragment4_title);
        tvTitle.setTypeface(typefaceUtil.getTypeface(fontNumber));

        recyclerView = (RecyclerView) view.findViewById(R.id.rv_frg4);
        LinearLayoutManager mLinearManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(mLinearManager);
        mAdapter = new MyAdapter_Setting(getActivity(), arrayList);
        recyclerView.setAdapter(mAdapter);

        return view;
    }

    private void addMenu() {

        String list1 = getResources().getString(R.string.settint_design);
        String list2 = getResources().getString(R.string.setting_language);
        String list3 = getResources().getString(R.string.review);
        String list4 = getResources().getString(R.string.privacy);

        arrayList.add(new ListObject2(R.drawable.ic_layers_black_24dp, list1));
        arrayList.add(new ListObject2(R.drawable.ic_language_black_24dp, list2));
        arrayList.add(new ListObject2(R.drawable.ic_forum_grey_24dp, list3));
        arrayList.add(new ListObject2(R.drawable.ic_people_black_24dp, list4));

    }


}