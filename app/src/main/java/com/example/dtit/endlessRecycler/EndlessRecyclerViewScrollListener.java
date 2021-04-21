package com.example.dtit.endlessRecycler;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import org.jetbrains.annotations.NotNull;

public class EndlessRecyclerViewScrollListener extends RecyclerView.OnScrollListener {

    public static final int VISIBLE_THRESHOLD = 10;

    private int previousTotalItemCount = 0;
    private boolean loading = true;

    private final LinearLayoutManager LLM;
    private final RecyclerAdapter RA;

    public EndlessRecyclerViewScrollListener(LinearLayoutManager layoutManager, RecyclerAdapter recyclerAdapter) {
        this.LLM = layoutManager;
        this.RA = recyclerAdapter;
    }

    @Override
    public void onScrolled(@NotNull RecyclerView view, int dx, int dy) {
        int totalItemCount = LLM.getItemCount();

        if (loading && (totalItemCount > previousTotalItemCount)) {
            loading = false;
            previousTotalItemCount = totalItemCount;
        }

        if (!loading && (LLM.findLastVisibleItemPosition() + VISIBLE_THRESHOLD) > totalItemCount) {
            onLoadMore(view);
            loading = true;
        }
    }

    private void onLoadMore(RecyclerView recyclerView) {
        RA.receiveData(recyclerView);
    }
}
