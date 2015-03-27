package com.sybiload.shopp;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageButton;

import com.sybiload.shopp.Adapter.AdapterList;
public class FragmentList extends Fragment
{
    private RecyclerView recyclerView;
    private RecyclerView.LayoutManager layoutManager;

    ImageButton fabImageButton;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        View view = inflater.inflate(R.layout.fragment_list, container, false);

        // populate items
        new Misc().populateList(getActivity());

        fabImageButton = (ImageButton) view.findViewById(R.id.imageButtonListFab);

        fabImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), ActivityNewList.class);
                startActivity(intent);
            }
        });

        recyclerView = (RecyclerView) view.findViewById(R.id.recyclerViewList);

        recyclerView.setHasFixedSize(true);

        layoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(layoutManager);


        return view;
    }

    public void enterList(final int position)
    {
        Static.currentList = Static.allList.get(position);
        new Misc().populateItem(getActivity(), Static.currentList);

        Intent intent = new Intent(getActivity(), ActivityShop.class);

        startActivity(intent);
        new Misc().leftTransition(getActivity());
    }

    @Override
    public void onResume()
    {
        // add all items to shop
        AdapterList adapterList = new AdapterList(this);
        recyclerView.setAdapter(adapterList);

        super.onResume();
    }
}
