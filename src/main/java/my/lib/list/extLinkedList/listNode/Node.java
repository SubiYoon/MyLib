package my.lib.list.extLinkedList.listNode;

public class Node<T> {
    public T item;
    public Node<T> next;

    public Node(T newItem) {
        this.item = newItem;
        this.next = null;
    }

    public Node(T newItem, Node next) {
        this.item = newItem;
        this.next = next;
    }
}
