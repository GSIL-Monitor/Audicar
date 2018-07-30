package com.beautyyan.beautyyanapp.utils;

import com.beautyyan.beautyyanapp.http.bean.City;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Created by xuelu on 2017/5/5.
 *
 * 城市名按拼音排序
 */

public class CitiesSort {

    public static List<City> getSortCities(List<City> list) {
        for (City city : list) {
            System.out.println(PinyinUtil.hanziToPinyin(city.getCityName()));
        }
        Collections.sort(list, new ToSort());//new ToSort() 根据需求定义排序
        System.out.println("排序后！！！！！！！！！");
        for (City city : list) {
            System.out.println(city.getCityName());
        }
        return list;
    }

    //排序
    static class ToSort implements Comparator<City> {
        @Override
        public int compare(City o1, City o2) {
            // TODO Auto-generated method stub
            String s1 = PinyinUtil.hanziToPinyin(o1.getCityName());
            String s2 = PinyinUtil.hanziToPinyin(o2.getCityName());
            if ("厦门".equals(o1.getCityName()))
            {
                s1 = "xiamen";
            }
            if ("厦门".equals(o2.getCityName())) {
                s2 = "xiamen";
            }
            if ("长沙".equals(o1.getCityName()))
            {
                s1 = "changsha";
            }
            if ("长春".equals(o1.getCityName())) {
                s1 = "changchun";
            }
            if ("长春".equals(o2.getCityName()))
            {
                s2 = "changchun";
            }
            if ("长沙".equals(o2.getCityName())) {
                s2 = "changsha";
            }
            if ("重庆".equals(o1.getCityName()))
            {
                s1 = "chongqing";
            }
            if ("重庆".equals(o2.getCityName())) {
                s2 = "chongqing";
            }
            if (s1.compareTo(s2) > 0) {
                return 1;
            } else {
                return -1;
            }
        }
    }
}
