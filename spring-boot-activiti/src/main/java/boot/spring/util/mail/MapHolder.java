package boot.spring.util.mail;

import java.util.HashMap;
import java.util.Map;

/**
 * @Author duxiangdong
 * @Date 2022/1/25 11:57
 * @Version 1.0
 */
public class MapHolder<K, V> {
    private Map<K, V> map = new HashMap<K, V>();
    private MapHolder<K, V> parent;

    public Map<K, V> getMap() {
        return map;
    }

    public void setMap(Map<K, V> map) {
        this.map = map;
    }

    public MapHolder<K, V> getParent() {
        return parent;
    }

    public void setParent(MapHolder<K, V> parent) {
        this.parent = parent;
    }

    private MapHolder() {

    }


    public Map<K, V> get() {
        return this.map;
    }

    public static MapHolder<String, Object> create() {
        return new MapHolder<String, Object>();

    }

    public MapHolder<K, V> put(K key, V value) {
        this.map.put(key, value);
        return this;
    }
}
