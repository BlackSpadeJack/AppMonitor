package com.app.monitor.core.datas;

import java.util.ArrayList;

/**
 * 实时刷新传递监控 信息
 * 
 * @author yx
 * 
 */
public interface RefreshDataListener {
    public void updateDataInfo(ArrayList<String> processInfo);

}
