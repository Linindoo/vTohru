package cn.vtohru.demo.service;

import cn.vtohru.app.api.WeekNotice;
import org.pf4j.Extension;

@Extension
public class MondayService implements WeekNotice {

    @Override
    public String info() {
        return "Monday";
    }
}
