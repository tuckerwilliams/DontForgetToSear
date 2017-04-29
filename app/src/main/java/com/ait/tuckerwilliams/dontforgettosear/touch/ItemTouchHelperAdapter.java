package com.ait.tuckerwilliams.dontforgettosear.touch;

public interface ItemTouchHelperAdapter {
    void onItemDismiss(int position);

    boolean onItemMove(int fromPosition, int toPosition);
}
