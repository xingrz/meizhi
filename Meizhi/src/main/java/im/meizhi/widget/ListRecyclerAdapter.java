package im.meizhi.widget;

import android.support.v7.widget.RecyclerView;

import java.util.List;

public abstract class ListRecyclerAdapter<E, VH extends RecyclerView.ViewHolder>
        extends RecyclerView.Adapter<VH> {

    private List<E> list;

    public E getItem(int position) {
        return list == null ? null : list.get(position);
    }

    @Override
    public int getItemCount() {
        return list == null ? 0 : list.size();
    }

    public void setList(List<E> list) {
        this.list = list;
        notifyDataSetChanged();
    }

}