package com.fyxridd.lib.transaction.manager;

import com.fyxridd.lib.core.api.MessageApi;
import com.fyxridd.lib.core.api.PlayerApi;
import com.fyxridd.lib.core.api.config.ConfigApi;
import com.fyxridd.lib.core.api.config.Setter;
import com.fyxridd.lib.core.api.event.TimeEvent;
import com.fyxridd.lib.core.api.fancymessage.FancyMessage;
import com.fyxridd.lib.func.api.FuncApi;
import com.fyxridd.lib.transaction.TransactionPlugin;
import com.fyxridd.lib.transaction.api.Transaction;
import com.fyxridd.lib.transaction.api.TransactionUser;
import com.fyxridd.lib.transaction.config.TransactionConfig;
import com.fyxridd.lib.transaction.func.TransactionCmd;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.*;
import org.bukkit.plugin.EventExecutor;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class TransactionManager {
    private TransactionConfig config;

	//玩家名 玩家事务信息
	private Map<String, TransactionUser> trans = new HashMap<>();
	
	public TransactionManager() {
        //添加配置监听
        ConfigApi.addListener(TransactionPlugin.instance.pn, TransactionConfig.class, new Setter<TransactionConfig>() {
            @Override
            public void set(TransactionConfig value) {
                config = value;
            }
        });
        //注册功能
        FuncApi.register(TransactionPlugin.instance.pn, new TransactionCmd());
        //注册事件
        {
            //时间事件
            Bukkit.getPluginManager().registerEvent(TimeEvent.class, TransactionPlugin.instance, EventPriority.NORMAL, new EventExecutor() {
                @Override
                public void execute(Listener listener, Event e) throws EventException {
                    if (e instanceof TimeEvent) {
                        Iterator<Map.Entry<String, TransactionUser>> it = trans.entrySet().iterator();
                        while (it.hasNext()) {
                            Map.Entry<String, TransactionUser> entry = it.next();
                            //过期与提示
                            entry.getValue().onTime();
                            //检测事务为空
                            if (entry.getValue().isEmpty()) it.remove();//玩家事务为空,删除事务
                        }
                    }
                }
            }, TransactionPlugin.instance);
        }
    }

    /**
     * @see com.fyxridd.lib.transaction.api.TransactionApi#getTransactionUser(String)
     */
    public TransactionUser getTransactionUser(String name) {
        //玩家存在性检测
        name = PlayerApi.getRealName(null, name);
        if (name == null) return null;
        //
        if (!trans.containsKey(name)) trans.put(name, new TransactionUser(name));
        return trans.get(name);
    }

    /**
     * @see com.fyxridd.lib.transaction.api.TransactionApi#delTransaction(String)
     */
    public void delTransaction(String name) {
        if (name == null) return;
        //玩家存在性检测
        name = PlayerApi.getRealName(null, name);
        if (name == null) return;
        //
        TransactionUser tu = trans.remove(name);
        if (tu != null) tu.delAllTransaction();
    }

    public TransactionConfig getConfig() {
        return config;
    }

    /**
     * 事务处理
     * @param p 玩家,不为null
     * @param args 操作内容
     * @return 操作结果
     */
    public boolean operate(Player p, String... args) {
        TransactionUser user = trans.get(p.getName());
        if (user == null) {//当前没有事务
            MessageApi.send(p, get(p.getName(), 20), true);
            return false;
        }
        long id = user.getRunning();
        Transaction trans = user.getTransaction(id);
        if (trans == null) {//指定的事务不存在或已经结束
            MessageApi.send(p, get(p.getName(), 10), true);
            return false;
        }
        //执行事务
        trans.onOperate(args);
        return true;
    }

    private FancyMessage get(String player, int id, Object... args) {
        return config.getLang().get(player, id, args);
    }
}
