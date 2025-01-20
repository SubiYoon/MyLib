package my.lib.list.extLinkedList;

import my.lib.list.extLinkedList.listNode.BidirectionalNode;

import java.util.AbstractSequentialList;
import java.util.List;
import java.util.ListIterator;
import java.util.NoSuchElementException;

public class CircularDoublyLinkedList<E>
        extends AbstractSequentialList<E>
        implements List<E>, java.io.Serializable
{
    private BidirectionalNode<E> head;
    private int currentIndex;

    public CircularDoublyLinkedList() {
       currentIndex = 0;
        head = new BidirectionalNode<>(null);
        head.next = head.prev = head;
    }

    /**
     * 특정 Index에 데이터를 추가
     * @param index index at which the specified element is to be inserted
     * @param value element to be inserted
     */
    @Override
    public void add(int index, E value){
        if (index >= 0 && index <= currentIndex) {
            BidirectionalNode<E> prevNode = getNode(index - 1);
            BidirectionalNode<E> newNode = new BidirectionalNode<>(prevNode, value, prevNode.next);
            newNode.next.prev = newNode;
            prevNode.next = newNode;
            currentIndex++;
        } else throw new IndexOutOfBoundsException("Index out of bounds");
    }

    /**
     * 마지막에 데이터 추가
     * @param value element whose presence in this collection is to be ensured
     * @return success true, fail false
     */
    @Override
    public boolean add(E value) {
        BidirectionalNode<E> prevNode = head.prev;
        BidirectionalNode<E> newNode = new BidirectionalNode<>(prevNode, value, head);
        prevNode.next = newNode;
        head.prev = newNode;
        currentIndex++;
        return true;
    }

    /**
     * 특정 Index의 데이터 삭제
     * @param index the index of the element to be removed
     * @return deleted data
     */
    @Override
    public E remove(int index) {
        if (index >= 0 && index <= currentIndex - 1) {
            BidirectionalNode<E> currentNode = getNode(index);
            currentNode.prev.next = currentNode.next;
            currentNode.next.prev = currentNode.prev;
            currentIndex--;
            return currentNode.item;
        } else return null;
    }

    /**
     * 특정 value를 찾아 해당 데이터 삭제
     * @param value 삭제할 value
     * @return success true, fail false
     */
    public boolean removeItem(E value) {
        BidirectionalNode<E> currentNode = head;
        for (int i = 0; i < currentIndex; i++) {
            currentNode = currentNode.next;
            if (((Comparable)currentNode.item).compareTo(value) == 0) {
                currentNode.prev.next = currentNode.next;
                currentNode.next.prev = currentNode.prev;

                currentIndex--;
                return true;
            }
        }
        return false;
    }

    /**
     * 특정 Index의 data 반환
     * @param index index of the element to return
     * @return data
     */
    @Override
    public E get(int index) {
        if (index >= 0 && index <= currentIndex - 1) {
            return getNode(index).item;
        } else throw new IndexOutOfBoundsException("Index out of bounds");
    }

    /**
     * 특정 Index의 데이터 수정
     * @param index index of the element to replace
     * @param value element to be stored at the specified position
     * @return value
     */
    @Override
    public E set(int index, E value) {
        if (index >= 0 && index <= currentIndex - 1) {
            return getNode(index).item = value;
        } else throw new IndexOutOfBoundsException("Index out of bounds");
    }

    private BidirectionalNode<E> getNode(int index) {
        if (index >= -1 && index <= currentIndex - 1) {
            BidirectionalNode<E> currentNode = head;

            if (index < currentIndex / 2) {
                for (int i = 0; i <= index; i++) {
                    currentNode = currentNode.next;
                }
            } else {
                for (int i = currentIndex - 1; i >= index ; i--) {
                    currentNode = currentNode.prev;
                }
            }

            return currentNode;
        } else throw new IndexOutOfBoundsException("Index out of bounds");
    }

    @Override
    public int indexOf(Object value){
        BidirectionalNode<E> currentNode = head;
        for (int i = 0; i < currentIndex; i++) {
            currentNode = currentNode.next;
            if (((Comparable)currentNode.item).compareTo(value) == 0) {
                return i;
            }
        }

        return -1;
    }

    /**
     * List size
     * @return
     */
    @Override
    public int size() {
        return currentIndex;
    }

    /**
     * List size eq 0 - true
     * List size neq 0 - false
     */
    @Override
    public boolean isEmpty() {
        return currentIndex == 0;
    }

    /**
     * data clear
     */
    @Override
    public void clear() {
        currentIndex = 0;
        head.next = head.prev = head;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("[");

        BidirectionalNode<E> currentNode = head.next; // 첫 번째 노드부터 순회
        for (int i = 0; i < currentIndex; i++) {
            sb.append(currentNode.item);
            if (i < currentIndex - 1) {
                sb.append(", "); // 마지막 요소가 아니라면 쉼표 추가
            }
            currentNode = currentNode.next;
        }

        sb.append("]");
        return sb.toString();
    }

    /**
     *
     * @param index index of the first element to be returned from the
     *        src.list iterator (by a call to {@link ListIterator#next next})
     * @return
     */
    @Override
    public ListIterator<E> listIterator(int index) {
        if (index < 0 || index > currentIndex) {
            throw new IndexOutOfBoundsException("Index out of bounds");
        }

        return new ListIterator<E>() {
            private BidirectionalNode<E> currentNode = (index == 0) ? head.next : getNode(index);
            private int currentIndex = index;
            private boolean canModify = false;

            @Override
            public boolean hasNext() {
                return currentIndex < CircularDoublyLinkedList.this.currentIndex;
            }

            @Override
            public E next() {
                if (!hasNext()) {
                    throw new NoSuchElementException();
                }
                E value = currentNode.item;
                currentNode = currentNode.next;
                currentIndex++;
                canModify = true;
                return value;
            }

            @Override
            public boolean hasPrevious() {
                return currentIndex > 0;
            }

            @Override
            public E previous() {
                if (!hasPrevious()) {
                    throw new NoSuchElementException();
                }
                currentNode = currentNode.prev;
                E value = currentNode.item;
                currentIndex--;
                canModify = true;
                return value;
            }

            @Override
            public int nextIndex() {
                return currentIndex;
            }

            @Override
            public int previousIndex() {
                return currentIndex - 1;
            }

            @Override
            public void remove() {
                if (!canModify) {
                    throw new IllegalStateException("Cannot remove element");
                }
                BidirectionalNode<E> prevNode = currentNode.prev;
                prevNode.next = currentNode.next;
                currentNode.next.prev = prevNode;

                currentNode = currentNode.next; // Move forward after removal
                CircularDoublyLinkedList.this.currentIndex--;
                canModify = false;
            }

            @Override
            public void set(E e) {
                if (!canModify) {
                    throw new IllegalStateException("Cannot modify element");
                }
                currentNode.item = e; // Modify the current node's value
            }

            @Override
            public void add(E e) {
                BidirectionalNode<E> prevNode = currentNode.prev;
                BidirectionalNode<E> newNode = new BidirectionalNode<>(prevNode, e, currentNode);

                prevNode.next = newNode;
                currentNode.prev = newNode;

                CircularDoublyLinkedList.this.currentIndex++;
                currentIndex++;
                canModify = false;
            }
        };
    }
}
