package com.twq.local;

import java.util.HashSet;
import java.util.Set;

/**
 *  项目集合
 */
public class ItemSet {
    //该项目集合包含的项目(即商品)
    private Set<String> items = new HashSet<String>();
    //该项目集合的在所有交易中出现的次数
    private int supportCount;

    public ItemSet() {}

    public ItemSet(Set<String> items) {
        this.items = items;
    }

    public Set<String> getItems() {
        return items;
    }
    public void setItems(Set<String> items) {
        this.items = items;
    }
    public int getSupportCount() {
        return supportCount;
    }
    public void setSupportCount(int supportCount) {
        this.supportCount = supportCount;
    }
}
