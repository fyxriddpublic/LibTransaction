package com.fyxridd.lib.transaction.func;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.fyxridd.lib.func.api.func.Extend;
import com.fyxridd.lib.func.api.func.Func;
import com.fyxridd.lib.func.api.func.FuncType;
import com.fyxridd.lib.transaction.TransactionPlugin;

@FuncType("cmd")
public class TransactionCmd {
    @Func("operate")
    public void onOperate(CommandSender sender, @Extend String content) {
        if (sender instanceof Player) TransactionPlugin.instance.getTransactionManager().operate((Player)sender, content.split(" "));
    }
}
