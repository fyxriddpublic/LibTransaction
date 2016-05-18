package com.fyxridd.lib.transaction;

import com.fyxridd.lib.core.api.config.ConfigApi;
import com.fyxridd.lib.core.api.plugin.SimplePlugin;
import com.fyxridd.lib.transaction.config.TransactionConfig;
import com.fyxridd.lib.transaction.manager.TransactionManager;

public class TransactionPlugin extends SimplePlugin{
    public static TransactionPlugin instance;

    private TransactionManager transactionManager;

    @Override
    public void onEnable() {
        instance = this;

        //注册配置
        ConfigApi.register(pn, TransactionConfig.class);

        transactionManager = new TransactionManager();

        super.onEnable();
    }

    public TransactionManager getTransactionManager() {
        return transactionManager;
    }
}