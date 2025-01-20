package my.lib.list.extLinkedList.listNode;

public class BidirectionalNode<E> {
    public BidirectionalNode<E> prev;
    public E item;
    public BidirectionalNode<E> next;

    public BidirectionalNode() {
        prev = next = null;
        item = null;
    }

    public BidirectionalNode(E newItem) {
        prev = next = null;
        item = newItem;
    }

    public BidirectionalNode(BidirectionalNode<E> prev, E item, BidirectionalNode<E> next) {
        this.prev = prev;
        this.item = item;
        this.next = next;
    }
}
