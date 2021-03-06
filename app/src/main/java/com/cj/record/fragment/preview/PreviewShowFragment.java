package com.cj.record.fragment.preview;


import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.text.TextUtils;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.cj.record.R;
import com.cj.record.activity.PreviewActivity;
import com.cj.record.baen.Hole;
import com.cj.record.baen.Record;
import com.cj.record.db.RecordDao;

import java.util.List;


public class PreviewShowFragment extends Fragment {

    private static PreviewShowFragment instance = null;

    public static PreviewShowFragment newInstance() {
        if (instance == null) {
            instance = new PreviewShowFragment();
        }
        return instance;
    }

    public PreviewShowFragment() {
    }

    private Hole hole;
    private RecyclerView preview_show_list1;
    //    private RecyclerView preview_show_list2;
    List<Record> listRecord1;
    //    List<Record> listRecord2;
    private Context context;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = getActivity();
        if (getArguments().containsKey(PreviewActivity.EXTRA_HOLE)) {
            hole = (Hole) getArguments().getSerializable(PreviewActivity.EXTRA_HOLE);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.frt_preview_show, container, false);
        listRecord1 = RecordDao.getInstance().getRecordOne(hole.getId());
        preview_show_list1 = (RecyclerView) view.findViewById(R.id.preview_show_list1);
        preview_show_list1.setLayoutManager(new LinearLayoutManager(context));
        preview_show_list1.setAdapter(new ItemAdapter(context, listRecord1));
        return view;
    }

    public class ItemAdapter extends RecyclerView.Adapter<ItemAdapter.ViewHolder> {
        private final TypedValue mTypedValue = new TypedValue();
        private int mBackground;
        private List<Record> mValues;

        public class ViewHolder extends RecyclerView.ViewHolder {
            public Record vo;

            public final View mView;
            public final TextView tvwBeginDepth;
            public final TextView tvwBeginEnd;
            public final TextView tvwEndDepth;
            public final TextView tvwType;
            public final TextView tvwContent;
            public final TextView tvwHint;

            public ViewHolder(View view) {
                super(view);
                mView = view;
                tvwBeginDepth = (TextView) view.findViewById(R.id.tvwBeginDepth);
                tvwBeginEnd = (TextView) view.findViewById(R.id.tvwBeginEnd);
                tvwEndDepth = (TextView) view.findViewById(R.id.tvwEndDepth);
                tvwType = (TextView) view.findViewById(R.id.tvwType);
                tvwContent = (TextView) view.findViewById(R.id.tvwContent);
                tvwHint = (TextView) view.findViewById(R.id.tvwHint);
            }

            @Override
            public String toString() {
                return super.toString() + " '" + tvwBeginDepth.getText();
            }
        }

        public ItemAdapter(Context context, List<Record> items) {
            context.getTheme().resolveAttribute(R.attr.selectableItemBackground, mTypedValue, true);
            mBackground = mTypedValue.resourceId;
            mValues = items;
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_record_preview, parent, false);
            view.setBackgroundResource(mBackground);
            return new ViewHolder(view);
        }


        @Override
        public void onBindViewHolder(ViewHolder rv, int position) {
            final ViewHolder holder = (ViewHolder) rv;
            holder.vo = mValues.get(position);
            if (TextUtils.isEmpty(holder.vo.getUpdateId())) {
                holder.tvwHint.setVisibility(View.GONE);
            } else {
                holder.tvwHint.setVisibility(View.VISIBLE);
            }
            holder.tvwBeginDepth.setText(holder.vo.getBeginDepth() + "m");

            holder.tvwBeginEnd.setVisibility(holder.vo.getType().equals("取水") ? View.GONE : View.VISIBLE);
            holder.tvwEndDepth.setVisibility(holder.vo.getType().equals("取水") ? View.GONE : View.VISIBLE);

            holder.tvwBeginEnd.setText(holder.vo.getType().equals("水位") ? "/" : "~");
            holder.tvwEndDepth.setText(holder.vo.getEndDepth() + "m");
            holder.tvwType.setText(holder.vo.getContentType());

            holder.tvwContent.setText(Html.fromHtml(holder.vo.getContent()));
        }

        @Override
        public int getItemCount() {
            return mValues.size();
        }
    }

}
