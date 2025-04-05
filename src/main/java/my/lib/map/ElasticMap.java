import java.util.*;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

/**
 * ElasticMap은 log^2(n) 방식의 probing을 사용하여 충돌을 최소화하고
 * 자동 리사이징 및 삭제 처리까지 지원하는 해시 기반 Map 구현입니다.
 * 
 * @param <K> 키 타입
 * @param <V> 값 타입
 */
public class ElasticMap<K, V> implements Iterable<Map.Entry<K, V>> {

    /**
     * Entry 클래스는 키-값 쌍과 삭제 여부를 저장합니다.
     */
    private static class Entry<K, V> {
        K key;
        V value;
        boolean deleted;

        Entry(K key, V value) {
            this.key = key;
            this.value = value;
            this.deleted = false;
        }
    }

    private Entry<K, V>[] table;
    private int size;
    private int capacity;
    private static final double LOAD_FACTOR = 0.7;

    @SuppressWarnings("unchecked")
    public ElasticMap(int initialCapacity) {
        if (initialCapacity <= 0) throw new IllegalArgumentException("Capacity must be positive");
        this.capacity = initialCapacity;
        this.table = new Entry[capacity];
        this.size = 0;
    }

    private int hash(K key) {
        return Math.abs(key.hashCode()) % capacity;
    }

    private int probeRange(int depth) {
        return (int)(Math.pow(Math.log(depth + 2), 2)) + 1;
    }

    private void validateKeyValue(K key, V value) {
        if (key == null) throw new IllegalArgumentException("Key must not be null");
        if (value == null) throw new IllegalArgumentException("Value must not be null");
    }

    private void validateKey(K key) {
        if (key == null) throw new IllegalArgumentException("Key must not be null");
    }

    /**
     * 키-값 쌍을 ElasticMap에 추가합니다. 동일한 키가 있으면 값을 덮어씁니다.
     * 
     * @param key 키
     * @param value 값
     */
    public void put(K key, V value) {
        validateKeyValue(key, value);
        if ((double) size / capacity >= LOAD_FACTOR) {
            resize();
        }

        int index = hash(key);
        int depth = 1;

        while (depth < capacity) {
            int range = probeRange(depth);
            for (int i = 0; i < range; i++) {
                int probeIndex = (index + i) % capacity;
                Entry<K, V> entry = table[probeIndex];

                if (entry == null || entry.deleted || entry.key.equals(key)) {
                    table[probeIndex] = new Entry<>(key, value);
                    size++;
                    return;
                }
            }
            depth++;
        }

        throw new RuntimeException("ElasticMap is full! (shouldn't happen after resize)");
    }

    /**
     * 주어진 키에 해당하는 값을 반환합니다.
     * 
     * @param key 키
     * @return 값, 없으면 null
     */
    public V get(K key) {
        validateKey(key);
        int index = hash(key);
        int depth = 1;

        while (depth < capacity) {
            int range = probeRange(depth);
            for (int i = 0; i < range; i++) {
                int probeIndex = (index + i) % capacity;
                Entry<K, V> entry = table[probeIndex];

                if (entry == null) return null;
                if (!entry.deleted && entry.key.equals(key)) return entry.value;
            }
            depth++;
        }
        return null;
    }

    /**
     * 주어진 키에 해당하는 항목을 제거합니다.
     * 
     * @param key 키
     * @return 성공 여부
     */
    public boolean remove(K key) {
        validateKey(key);
        int index = hash(key);
        int depth = 1;

        while (depth < capacity) {
            int range = probeRange(depth);
            for (int i = 0; i < range; i++) {
                int probeIndex = (index + i) % capacity;
                Entry<K, V> entry = table[probeIndex];

                if (entry == null) return false;
                if (!entry.deleted && entry.key.equals(key)) {
                    entry.deleted = true;
                    size--;
                    return true;
                }
            }
            depth++;
        }
        return false;
    }

    /**
     * 현재 저장된 모든 키를 반환합니다.
     * 
     * @return 키 Set
     */
    public Set<K> keySet() {
        Set<K> keys = new HashSet<>();
        for (Entry<K, V> entry : table) {
            if (entry != null && !entry.deleted) {
                keys.add(entry.key);
            }
        }
        return keys;
    }

    /**
     * 현재 저장된 모든 값을 반환합니다.
     * 
     * @return 값 리스트
     */
    public Collection<V> values() {
        List<V> values = new ArrayList<>();
        for (Entry<K, V> entry : table) {
            if (entry != null && !entry.deleted) {
                values.add(entry.value);
            }
        }
        return values;
    }

    /**
     * 자동으로 용량을 2배로 늘리고 기존 데이터를 재배치합니다.
     */
    @SuppressWarnings("unchecked")
    private void resize() {
        Entry<K, V>[] oldTable = table;
        capacity *= 2;
        table = new Entry[capacity];
        size = 0;

        for (Entry<K, V> entry : oldTable) {
            if (entry != null && !entry.deleted) {
                put(entry.key, entry.value);
            }
        }
    }

    /**
     * Stream으로 값을 순회할 수 있게 해줍니다.
     */
    @Override
    public Iterator<Map.Entry<K, V>> iterator() {
        return new Iterator<>() {
            int index = 0;

            public boolean hasNext() {
                while (index < capacity) {
                    Entry<K, V> entry = table[index];
                    if (entry != null && !entry.deleted) return true;
                    index++;
                }
                return false;
            }

            public Map.Entry<K, V> next() {
                while (index < capacity) {
                    Entry<K, V> entry = table[index++];
                    if (entry != null && !entry.deleted) {
                        return new AbstractMap.SimpleEntry<>(entry.key, entry.value);
                    }
                }
                throw new NoSuchElementException();
            }
        };
    }

    /**
     * 이 ElasticMap을 Stream으로 변환합니다.
     * 
     * @return Stream<Map.Entry<K, V>>
     */
    public Stream<Map.Entry<K, V>> stream() {
        return StreamSupport.stream(Spliterators.spliteratorUnknownSize(this.iterator(), 0), false);
    }

    /**
     * 현재 ElasticMap에 저장된 항목 수를 반환합니다.
     * 
     * @return 크기
     */
    public int size() {
        return size;
    }
}
