package DataStructuresAndAlgorithms;

/*  Written by Ali Egemen Bilak using VSCode and JDK 21.0.5 on Ubuntu 24.04.1 LTS
    https://github.com/ducktumn
    Last Edit: 18.12.2024
    Made for MEF University COMP201 final project.
*/

// Custom generic queue implementation
public class MyQueue<E> {

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
    private Link<E> front;
    private Link<E> rear;

    public MyQueue() {
        this.size = 0;
        this.front = null;
        this.rear = null;
    }

    // Adds a new element to the rear
    public void enqueue(E element) {
        Link<E> tempLink = new Link<>(element);
        if (size == 0)
            this.front = tempLink;
        else
            this.rear.setNext(tempLink);

        this.rear = tempLink;
        size++;
    }

    // Removes and returns the element at the front
    public E dequeue() {
        if (size == 0)
            return null;
        try {
            return this.front.getContent();
        } finally {
            if (size == 1) {
                this.front = null;
                this.rear = null;
            } else {
                this.front = this.front.getNext();
            }
            size--;
        }
    }

    // Returns the element at the front without removing it
    public E first() {
        if (size != 0)
            return this.front.getContent();
        else
            return null;
    }

    public int size() {
        return this.size;
    }

    public boolean isEmpty() {
        return this.size == 0;
    }

}
