package com.fyxridd.lib.transaction.api;

import com.fyxridd.lib.transaction.TransactionPlugin;

public class TransactionApi {
    /**
     * 获取玩家事务信息
     * @param name 玩家名,不为null
     * @return 玩家事务信息,没有则新建,异常返回null
     */
    public static TransactionUser getTransactionUser(String name) {
        return TransactionPlugin.instance.getTransactionManager().getTransactionUser(name);
    }

    /**
     * 删除玩家的所有事务
     * @param name 玩家名,可为null
     */
    public static void delTransaction(String name) {
        TransactionPlugin.instance.getTransactionManager().delTransaction(name);
    }
}
