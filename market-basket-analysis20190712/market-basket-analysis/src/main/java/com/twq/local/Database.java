package com.twq.local;

import java.io.*;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 所有的交易数据解析存储的类
 */
public class Database {
    // 所有的交易数据
    private List<Set<String>> transactions = new LinkedList<>();
    // 所有item以及对应的支持数的集合
    private Map<String, Integer> allItems = new HashMap<>();

    public Database() {
        // 读取交易数据并解析
        try {
            BufferedReader br =
                    new BufferedReader(new InputStreamReader(new FileInputStream("C:\\Users\\25417\\Desktop\\market-basket-analysis(源代码)\\market-basket-analysis\\dataset\\all_pinglun_text-seg-result.csv")));
            String line = null;
            while ((line = br.readLine()) != null) {
                if (line.isEmpty()) continue;
                String[] items = line.split(",");
                Set<String> set = new HashSet<>();
                for (String item : items) {
                    set.add(item);
                    Integer support = allItems.get(item);
                    if (support != null) {
                        support += 1;
                        allItems.put(item, support);
                    } else {
                        allItems.put(item, 1);
                    }
                }
                transactions.add(set);
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // 获取所有交易数据的总记录数
    public int getTotalNumberOfTransactions() {
        return transactions.size();
    }

    /**
     * 获取该DataBase中所有交易包含的所有的item以及它的support_count
     *
     * @return 返回 1-项候选项集
     */
    public List<ItemSet> getAllItems() {
        return allItems.entrySet().stream().map(entry -> {
            ItemSet itemSet = new ItemSet(new HashSet<String>() {{
                add(entry.getKey());
            }});
            itemSet.setSupportCount(entry.getValue());
            return itemSet;
        }).collect(Collectors.toList());
    }


    /**
     * 计算同时出现了项集items中所有item的交易的数量
     *
     * @param itemSet
     * @return
     */
    public int getNumberOfItemSet(Set<String> itemSet) {
        int itemSetSupportCount = 0;
        // For each transaction.
        //循环遍历每条交易
        for (Set<String> transaction : transactions) {
            // See if this transaction contains all attributes.
            // 判断这条交易是否包含了项集itemSet中的每一条item
            boolean containsAllItems = true;
            for (String item : itemSet) {
                if (!transaction.contains(item)) {
                    containsAllItems = false;
                    break;
                }
            }
            // If the transaction contained all attributes, inc record count.
            // 如果包含了，则累加1
            if (containsAllItems) itemSetSupportCount++;
        }

        return itemSetSupportCount;
    }

}
