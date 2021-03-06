package com.fyxridd.lib.transaction.api;

import com.fyxridd.lib.core.api.MessageApi;
import com.fyxridd.lib.core.api.event.TimeEvent;
import com.fyxridd.lib.core.api.fancymessage.FancyMessage;
import com.fyxridd.lib.transaction.TransactionPlugin;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.List;

/**
 * 事务
 */
public abstract class Transaction {
	//事务ID,-1表示未设置
	private long id = -1;
	//事务所属的玩家名
	private String name;
	
	//事务开始的时间点
	private long start = System.currentTimeMillis();
	//事务开始的时间点,单位秒,从TimeEvent中获取的
	private long startSecond = TimeEvent.getTime();
	//事务过期的时间,单位秒,-1表示无时间限制
	private long last;
	//事务提示间隔,单位秒,-1表示不提示
	private int tipInterval = -1;
	
	public Transaction(String name, long last) {
		this.name = name;
		this.last = last;
	}
	
	public Transaction(String name, long last, int tipInterval) {
		this.name = name;
		this.last = last;
		this.tipInterval = tipInterval;
	}
	
	/**
	 * 事务提示时间点到时调用
	 */
	public abstract void onTip();
	
	/**
	 * 玩家操作事务时调用
	 * @param args 操作内容
	 */
	public abstract void onOperate(String... args);
	
	/**
	 * 事务过期时调用
	 */
	public abstract void onTimeOut();

	/**
	 * 事务取消时调用(包括过期)
	 */
	public abstract void onCancel();
	
	/**
	 * 默认的提示方式<br>
	 * 适合具体的事务在onTip()方法里调用
	 * @param list 列表
	 */
	public void tip(List<FancyMessage> list) {
		Player p = Bukkit.getPlayerExact(name);
		if (p != null && p.isOnline()) {
			//头
			FancyMessage msg = get(p.getName(), 30);
			if (msg != null) MessageApi.send(p, msg, true);
			//中,信息
			for (FancyMessage fm:list) MessageApi.send(p, fm, true);
			//尾
			msg = get(p.getName(), 40);
			if (msg != null) MessageApi.send(p, msg, true);
		}
	}

	public long getId() {
		return id;
	}
	
	public void setId(long id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	/**
	 * 检测事务是否过期
	 * @return
	 */
	public boolean isTimeOut() {
		return last != -1 && System.currentTimeMillis()-start>last*1000;
	}

	/**
	 * 事务是否到了提示的时间
	 * @return
	 */
	public boolean isTipTime() {
        if (tipInterval == -1) return false;
		long past = TimeEvent.getTime()-startSecond;
		if (past == 0) return false;//特殊情况
		return past%tipInterval == 0;
	}

	private static FancyMessage get(String player, int id, Object... args) {
		return TransactionPlugin.instance.getTransactionManager().getConfig().getLang().get(player, id, args);
	}
}
