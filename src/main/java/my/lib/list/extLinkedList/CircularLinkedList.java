package my.lib.list.extLinkedList;

import my.lib.list.extLinkedList.listNode.Node;

import java.util.AbstractSequentialList;
import java.util.List;
import java.util.ListIterator;
import java.util.NoSuchElementException;

/**
 * LinkedList의 확장판으로 마지막 Node의 next가 맨 처음 Index의 노드를 바라보고 있다.
 * @param <E> Object
 */
public class CircularLinkedList<E>
        extends AbstractSequentialList<E>
        implements List<E>, java.io.Serializable
{
    private Node<E> tail;
    private int currentIndex;

    public CircularLinkedList() {
        currentIndex = 0;
        tail = new Node(-1);
        tail.next = tail;
    }

    /**
     * 특정 Index에 Node를 추가
     * @param index index at which the specified element is to be inserted
     * @param value element to be inserted
     */
    @Override
    public void add(int index, E value) {
        if (index >= 0 && index <= currentIndex) {
            Node<E> prevNode = getNode(index - 1);
            Node<E> newNode = new Node(value, prevNode.next);
            prevNode.next = newNode;

            if (currentIndex == index) {
                tail = newNode;
            }

            currentIndex++;
        }
    }

    /**
     * 새로운 데이터 추가
     * @param value element whose presence in this collection is to be ensured
     * @return success return true or fail return false
     */
    @Override
    public boolean add(E value) {
        Node<E> prevNode = tail;
        Node<E> newNode = new Node(value, tail.next);
        prevNode.next = newNode;
        tail = newNode;
        currentIndex++;
        return true;
    }

    /**
     * 특정 Index의 Node를 제거하고 제거한 Node를 반환
     * @param index the index of the element to be removed
     * @return success return remove item value
     */
    @Override
    public E remove(int index) {
        if (index >= 0 && index <= currentIndex - 1) {
            Node<E> prevNode = getNode(index - 1);
            E removeItem = prevNode.next.item;
            prevNode.next = prevNode.next.next;

            if (currentIndex == index) tail = prevNode;

            currentIndex--;
            return removeItem;
        } else {
            return null;
        }
    }

    /**
     * 노드에 있는 데이터 값을 추적해 해당 데티터를 삭제
     * 삭제 성공시 true, 삭제 실패시 false 반환
     * @param value delete item value
     */
    public void removeItem(E value) {
        Node<E> currentNode = tail.next;
        Node<E> prevNode;
        for (int i = 0; i < currentIndex; i++) {
            prevNode = currentNode;
            currentNode = prevNode.next;

            if (((Comparable)(currentNode.item)).compareTo(value) == 0) {
                prevNode.next = currentNode.next;
                currentIndex--;

                return;
            }
        }
    }

    /**
     * 특정 Index의 값을 반환
     * @param index index of the element to return
     * @return get index value
     */
    @Override
    public E get(int index) {
        if (!(index >= 0 && index <= currentIndex - 1)) {
            throw new IndexOutOfBoundsException("Index: " + index + ", Size: " + size());
        }
        return getNode(index).item;
    }

    /**
     * 특정 Index의 데이터를 수정
     * @param index index of the element to replace
     * @param value element to be stored at the specified position
     * @return edit value
     */
    @Override
    public E set(int index, E value) {
        if (index >= 0 && index <= currentIndex - 1) {
            return getNode(index).item = value;
        } else throw new IndexOutOfBoundsException();
    }

    /**
     * 해당 값을 가지고 있는지 여부
     * @param value element whose presence in this src.list is to be tested
     * @return isContain - true, isNotContain - false
     */
    @Override
    public boolean contains(Object value) {
        return indexOf(value) > -1;
    }

    /**
     * 특정 Index의 Node를 Get
     * @param index data Index
     * @return search Node
     */
    private Node<E> getNode(int index) {
        if (index >= -1 && index <= currentIndex) {
            Node<E> currentNode = tail.next; // 더미 헤드
            for (int i = 0; i <= index; i++) {
                currentNode = currentNode.next;
            }

            return currentNode;
        } else throw new IndexOutOfBoundsException();
    }

    /**
     * 특정 데이터의 값의 Index를 탐색
     * @param value 검색할 데이터
     * @return 데이터가 있으면 Index, 데이터가 없으면 -1
     */
    public int indexOf(Object value) {
        Node<E> currentNode = tail.next;
        for (int i = 0; i < currentIndex; i++) {
            if (((Comparable)currentNode.item).compareTo(value) == 0) return i;
        }

        return -1;
    }

    /**
     * List의 size가 몇개인지 호출
     * @return List의 길이
     */
    @Override
    public int size() {
        return currentIndex;
    }

    /**
     * 해당 List를 전부 삭제
     */
    @Override
    public void clear() {
        currentIndex = 0;
        tail = new Node(-1);
        tail.next = tail;
    }

    @Override
    public String toString() {
        StringBuilder msg = new StringBuilder("[");
        Node<E> currentNode = tail.next.next; // 더미 헤드 다음 노드
        for (int i = 0; i < currentIndex; i++) {
            msg.append(String.valueOf(currentNode.item)); // 각 item을 문자열로 변환
            if (i < currentIndex - 1) {
                msg.append(", ");
            }
            currentNode = currentNode.next;
        }
        msg.append("]");
        return msg.toString();
    }

    /**
     * @param index index of the first element to be returned from the
     *        src.list iterator (by a call to {@link ListIterator#next next})
     * @return ListIterator
     */
    @Override
    public ListIterator<E> listIterator(int index) {
        if (index < 0 || index > currentIndex) {
            throw new IndexOutOfBoundsException("Index: " + index + ", Size: " + size());
        }

        return new ListIterator<E>() {
            private Node<E> currentNode = (index == this.currentIndex) ? tail.next : getNode(index); // 현재 노드
            private int currentIndex = index; // 현재 인덱스
            private boolean canModify = false; // 현재 위치에서 수정 가능한지 여부

            @Override
            public boolean hasNext() {
                return currentIndex < CircularLinkedList.this.currentIndex;
            }

            @Override
            public E next() {
                if (!hasNext()) {
                    throw new NoSuchElementException();
                }
                currentNode = currentNode.next;
                currentIndex++;
                canModify = true;
                return currentNode.item;
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
                currentNode = (currentIndex == 0) ? getNode(CircularLinkedList.this.currentIndex - 1) : getNode(currentIndex - 1);
                currentIndex--;
                canModify = true;
                return currentNode.item;
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
                    throw new IllegalStateException();
                }
                CircularLinkedList.this.remove(currentIndex);
                currentIndex--;
                canModify = false;
            }

            @Override
            public void set(E e) {
                if (!canModify) {
                    throw new IllegalStateException();
                }
                currentNode.item = e;
            }

            @Override
            public void add(E e) {
                Node<E> newNode = new Node<>(e, currentNode.next);
                currentNode.next = newNode;
                if (currentNode == tail) {
                    tail = newNode;
                }
                currentIndex++;
                CircularLinkedList.this.currentIndex++;
                canModify = false;
            }
        };
    }

}
