package com.beautyyan.beautyyanapp.view.stickylistview;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.RelativeLayout;
import android.widget.SectionIndexer;
import android.widget.TextView;

import com.beautyyan.beautyyanapp.R;
import com.beautyyan.beautyyanapp.YuYuanApp;
import com.beautyyan.beautyyanapp.activity.ChooseCityActivity;
import com.beautyyan.beautyyanapp.http.bean.City;
import com.beautyyan.beautyyanapp.utils.Constant;
import com.beautyyan.beautyyanapp.utils.PinyinUtil;
import com.beautyyan.beautyyanapp.utils.ToastUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ChooseCityAdapter extends BaseAdapter implements
        StickyListHeadersAdapter, SectionIndexer {

    private final Context mContext;
    private List<City> cityList;
//    private int[] mSectionIndices;
//    private Character[] mSectionLetters;
    private LayoutInflater mInflater;
    private HashMap<String, List<City>> map;
    private List<String> zimuList;

    private boolean isEnable = true;
    private GvAdapter adapter;

    public ChooseCityAdapter(Context context, List<City> cityList, HashMap<String, List<City>> map, List<String> zimuList) {
        mContext = context;
        mInflater = LayoutInflater.from(context);
        this.cityList = cityList;
        this.map = map;
        this.zimuList = zimuList;
        if (cityList.size() == 0 || map.size() == 0 || zimuList.size() == 0) {
            return;
        }
//        mSectionIndices = getSectionIndices();
//        mSectionLetters = getSectionLetters();
    }

    private int[] getSectionIndices() {
        ArrayList<Integer> sectionIndices = new ArrayList<Integer>();
        char lastFirstChar = PinyinUtil.getFirstSpell(cityList.get(0).getCityName()).charAt(0);

        sectionIndices.add(0);
        for (int i = 1; i < cityList.size(); i++) {
            if (PinyinUtil.getFirstSpell(cityList.get(i).getCityName()).charAt(0) != lastFirstChar) {
                lastFirstChar = PinyinUtil.getFirstSpell(cityList.get(i).getCityName()).charAt(0);
                sectionIndices.add(i);
            }
        }
        int[] sections = new int[sectionIndices.size()];
        for (int i = 0; i < sectionIndices.size(); i++) {
            sections[i] = sectionIndices.get(i);
        }
        return sections;
    }

//    private Character[] getSectionLetters() {
//        Character[] letters = new Character[mSectionIndices.length];
//        for (int i = 0; i < mSectionIndices.length; i++) {
//            letters[i] = PinyinUtil.getFirstSpell(cityList.get(mSectionIndices[i]).getCityName()).charAt(0);
//        }
//        return let ters;
//    }

    @Override
    public int getCount() {
        return zimuList.size();
    }

    @Override
    public Object getItem(int position) {
        return zimuList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        ViewHolder holder;

        if (convertView == null) {
            holder = new ViewHolder();
            convertView = mInflater.inflate(R.layout.item_gridview, parent, false);
            holder.gv = (GridView) convertView.findViewById(R.id.gridview);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
//        if (position != 0) {
            holder.gv.setAdapter(new GvAdapter(map.get(zimuList.get(position)), position));
//        }
//        else {
//                adapter = new GvAdapter(map.get(zimuList.get(0)), 0);
//                holder.gv.setAdapter(adapter);
//        }

        return convertView;
    }

    private class GvAdapter extends BaseAdapter {

        private List<City> cityList;
        private int pos;
        public GvAdapter(List<City> cityList, int pos) {
            this.cityList = cityList;
            this.pos = pos;
        }

        @Override
        public int getCount() {
            return cityList.size();
        }

        @Override
        public Object getItem(int position) {
            return cityList.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            CityHolderView holder = null;
            if (convertView == null) {
                convertView = mInflater.inflate(R.layout.choose_city_gv_item, parent, false);
                holder = new CityHolderView();
                holder.cityTv = (TextView) convertView.findViewById(R.id.city_tv);
                convertView.setTag(holder);
            }
            if (pos != 0) {
                holder = (CityHolderView) convertView.getTag();
                holder.cityTv.setText(cityList.get(position).getCityName());
                holder.cityTv.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (ChooseCityActivity.getIns() == null) {
                            return;
                        }
                        ChooseCityActivity.getIns().notifyLocatedCityChanged(cityList.get(position));
                    }
                });
            }
            else {
                convertView = mInflater.inflate(R.layout.head_view, parent, false);
                RelativeLayout rl = (RelativeLayout) convertView.findViewById(R.id.located_rl);
                TextView tvLoca = (TextView) convertView.findViewById(R.id.tv_located);
                rl.setEnabled(isEnable);
                if ("".equals(cityList.get(position).getCityName())) {
                    rl.setEnabled(false);
                }
                else {
                    rl.setEnabled(true);
                }
                tvLoca.setText("".equals(Constant.getInstance().getLocatedCity().getCityName()) ? "定位失败" :
                        Constant.getInstance().getLocatedCity().getCityName());
                rl.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (ChooseCityActivity.getIns() == null) {
                            return;
                        }
                        if (!ChooseCityActivity.getIns().isInOpenCities) {
                            ToastUtils.showCenterShort(YuYuanApp.getIns(), "该城市暂未开通！");
                            return;
                        }
                        ChooseCityActivity.getIns().notifyLocatedCityChanged(cityList.get(position));
                    }
                });


            }
            return convertView;
        }
    }

    private class CityHolderView {
        TextView cityTv;
    }

    @Override
    public View getHeaderView(int position, View convertView, ViewGroup parent) {
        HeaderViewHolder holder;

        if (convertView == null) {
            holder = new HeaderViewHolder();
            convertView = mInflater.inflate(R.layout.choose_city_listview_item, parent, false);
            holder.zimuTv = (TextView) convertView.findViewById(R.id.zimu_tv);
            convertView.setTag(holder);
        } else {
            holder = (HeaderViewHolder) convertView.getTag();
        }

        // set header text as first char in name
        holder.zimuTv.setText(getItem(position).toString());

        return convertView;
    }

    /**
     * Remember that these have to be static, postion=1 should always return
     * the same Id that is.
     */
    @Override
    public long getHeaderId(int position) {
        // return the first character of the country as ID because this is what
        // headers are based upon
        long id;
        if ("热门城市".equals(zimuList.get(position))) {
            id = -1;
        }
        else if ("定位城市".equals(zimuList.get(position))) {
            id = -2;
        }
        else {
            id = zimuList.get(position).charAt(0);
        }
        return id;
    }

    @Override
    public int getPositionForSection(int section) {
//        if (mSectionIndices.length == 0) {
//            return 0;
//        }
//
//        if (section >= mSectionIndices.length) {
//            section = mSectionIndices.length - 1;
//        } else if (section < 0) {
//            section = 0;
//        }
//        return mSectionIndices[section];
        return 0;
    }

    @Override
    public int getSectionForPosition(int position) {
//        for (int i = 0; i < mSectionIndices.length; i++) {
//            if (position < mSectionIndices[i]) {
//                return i - 1;
//            }
//        }
//        return mSectionIndices.length - 1;
        return 0;
    }

    @Override
    public Object[] getSections() {
//        return mSectionLetters;
        return new Object[]{};
    }

    public void clear() {
        cityList = new ArrayList<>(0);
//        mSectionIndices = new int[0];
//        mSectionLetters = new Character[0];
        notifyDataSetChanged();
    }

//    public void restore() {
//        cities = mContext.getResources().getStringArray(R.array.countries);
//        mSectionIndices = getSectionIndices();
//        mSectionLetters = getSectionLetters();
//        notifyDataSetChanged();
//    }

    class HeaderViewHolder {
        TextView zimuTv;
    }

    class ViewHolder {
        GridView gv;
    }

    public void setEnable(boolean enable) {
        isEnable = enable;
    }

}
