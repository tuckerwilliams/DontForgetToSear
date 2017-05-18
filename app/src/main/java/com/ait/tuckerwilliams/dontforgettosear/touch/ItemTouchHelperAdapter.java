package com.ait.tuckerwilliams.dontforgettosear.touch;

public interface ItemTouchHelperAdapter {
    void onItemDismiss(int position);

    void onItemMove(int fromPosition, int toPosition);
}
