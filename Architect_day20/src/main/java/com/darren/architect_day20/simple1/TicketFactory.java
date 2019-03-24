package com.darren.architect_day20.simple1;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by hcDarren on 2017/10/29.
 * 火车票的查询工厂
 */

public class TicketFactory {
    // 做一个缓存
    static Map<String,Ticket> sTicketMap = new HashMap<>();

    public static Ticket getTicket(String form,String to){
        String key = form+"-"+to;
        Ticket ticket = sTicketMap.get(key);
        if(ticket!= null){
            return ticket;
        }
        ticket = new Ticket(form,to);
        sTicketMap.put(key,ticket);
        return ticket;
    }
}
