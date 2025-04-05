import java.util.*;

public class ElasticMap {
    private static class Entry {
        String key;
        int value;
        boolean deleted;

        Entry(String key, int value) {
            this.key = key;
            this.value = value;
            this.deleted = false;
        }
    }

    private Entry[] table;
    private int size;
    private int count;
    private final double LOAD_FACTOR = 0.7;

    public ElasticMap(int capacity) {
        if (capacity <= 0) throw new IllegalArgumentException("Capacity must be positive.");
        this.size = capacity;
        this.table = new Entry[size];
        this.count = 0;
    }

    private int hash(String key) {
        return Math.abs(key.hashCode()) % size;
    }

    private int probeRange(int depth) {
        return (int)(Math.pow(Math.log(depth + 2), 2)) + 1;
    }

    public void put(String key, int value) {
        if (key == null) throw new IllegalArgumentException("Key cannot be null.");

        if ((double)(count + 1) / size > LOAD_FACTOR) {
            resize(size * 2);
        }

        int index = hash(key);
        int depth = 1;

        while (depth < size) {
            int range = probeRange(depth);

            for (int i = 0; i < range; i++) {
                int probeIndex = (index + i) % size;
                Entry entry = table[probeIndex];

                if (entry == null || entry.deleted || entry.key.equals(key)) {
                    table[probeIndex] = new Entry(key, value);
                    if (entry == null || entry.deleted) count++;
                    return;
                }
            }
            depth++;
        }

        // 이론상 resize가 되어야 하므로 여기까지 도달하면 구조 오류
        throw new IllegalStateException("Insertion failed after resize.");
    }

    public Integer get(String key) {
        if (key == null) return null;

        int index = hash(key);
        int depth = 1;

        while (depth < size) {
            int range = probeRange(depth);
            for (int i = 0; i < range; i++) {
                int probeIndex = (index + i) % size;
                Entry entry = table[probeIndex];

                if (entry == null) return null;
                if (!entry.deleted && entry.key.equals(key)) {
                    return entry.value;
                }
            }
            depth++;
        }
        return null;
    }

    public boolean containsKey(String key) {
        return get(key) != null;
    }

    public boolean remove(String key) {
        if (key == null) return false;

        int index = hash(key);
        int depth = 1;

        while (depth < size) {
            int range = probeRange(depth);
            for (int i = 0; i < range; i++) {
                int probeIndex = (index + i) % size;
                Entry entry = table[probeIndex];

                if (entry == null) return false;
                if (!entry.deleted && entry.key.equals(key)) {
                    entry.deleted = true;
                    count--;
                    return true;
                }
            }
            depth++;
        }
        return false;
    }

    private void resize(int newSize) {
        Entry[] oldTable = this.table;
        this.table = new Entry[newSize];
        this.size = newSize;
        this.count = 0;

        for (Entry entry : oldTable) {
            if (entry != null && !entry.deleted) {
                put(entry.key, entry.value);
            }
        }
    }
}
