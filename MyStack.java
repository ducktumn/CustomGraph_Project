package DataStructuresAndAlgorithms;

/*  Written by Ali Egemen Bilak using VSCode and JDK 21.0.5 on Ubuntu 24.04.1 LTS
    https://github.com/ducktumn
    Last Edit: 18.12.2024
    Made for MEF University COMP201 final project.
*/

// Custom generic stack implementation
public class MyStack<E> {

    // Inner class to create a LinkedList structure from scratch
    private class Link<T> {
        private final T content;
        private Link<T> next;

        public Link(T content) {
            this.content = content;
            setNext(null);
        }

        public void setNext(Link<T> next) {
            this.next = next;
        }

        public Link<T> getNext() {
            return this.next;
        }

        public T getContent() {
            return this.content;
        }

    }

    private int size;
    private Link<E> top;

    public MyStack() {
        this.size = 0;
        this.top = null;
    }

    public int size() {
        return this.size;
    }

    public boolean isEmpty() {
        return this.size == 0;
    }

    // Returns the top element without removing
    public E top() {
        if (size != 0)
            return this.top.getContent();
        else
            return null;
    }

    // Adds a new element to the top
    public void push(E element) {
        Link<E> tempLink = new Link<E>(element);
        if (!(this.size == 0))
            tempLink.setNext(this.top);

        this.top = tempLink;
        this.size++;
    }

    // Returns the top element and removes it from the stack
    public E pop() {
        if (size == 0)
            return null;
        try {
            return this.top.getContent();
        } finally {
            if (size == 1)
                this.top = null;
            else {
                this.top = this.top.getNext();
            }
            size--;
        }

    }
}
