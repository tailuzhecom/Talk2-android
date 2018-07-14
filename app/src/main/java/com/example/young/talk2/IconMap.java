package com.example.young.talk2;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by young on 18-5-24.
 */

public class IconMap {
    private Map<Integer, Integer> iconMapLocal;
    private Map<String, Integer> iconMap;

    public void initIconMap() {
        iconMapLocal = new HashMap<>();
        iconMapLocal.put(1, R.drawable.apple);
        iconMapLocal.put(2, R.drawable.banana);
        iconMapLocal.put(3, R.drawable.orange);

        iconMap = new HashMap<>();
        iconMap.put("kd", 1);
        iconMap.put("LBJ", 2);
        iconMap.put("curry", 3);
        iconMap.put("ssc", 2);
        iconMap.put("Irving", 1);
        iconMap.put("hunt", 2);
        iconMap.put("james", 3);
    }

    public IconMap() {
        initIconMap();
    }

    //获得用户的头像
    public int getUserIcon(String username) {
        if(iconMapLocal.get(iconMap.get(username)) == null) {
            return R.drawable.apple;
        } else {
            return iconMapLocal.get(iconMap.get(username));
        }
    }

    public void addUserIcon(String username, int iconId) {
        iconMap.put(username, iconId);
    }
}
