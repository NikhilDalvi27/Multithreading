package com.sergeyvolkodav.queue;

import java.util.Scanner;

public class YourNonBlockingQueue {

    public static void main(String[] args) {
        CustomQueue1 queue = new CustomQueue1(4);
        Scanner sc = new Scanner(System.in);

        while (true) {

            queue.printQueue();
            System.out.println("Enter 1 for enqueue, 2 for dequeue, -1 for exit");
            int input = sc.nextInt();
            if (input == -1) {
                break;
            }

            if (input == 1) {
                System.out.println("Enter value to be enqueue");
                int num = sc.nextInt();
                queue.enqueue(num);
            } else if (input == 2) {
                queue.dequeue();
            } else {
                System.out.println("Invalid input, try again");
            }

        }
    }
}

class CustomQueue1 {


    Object arr[];
    int size;
    int currentSize;
    int head,tail;

    CustomQueue1(int size){
        arr = new Object[size];
        this.size = size;
        currentSize=0;
        head=0;
        tail=0;
    }

    public void printQueue() {
        int i = head;
        int count = 0;
        while (count != currentSize) {
            System.out.print(arr[i] + " ");
            i = (i + 1) % size;
            count++;
        }
        System.out.println();
    }
    public void enqueue(Object o) {
        if (currentSize == size) {
            System.out.println("Queue full, cannot enqueue");
            return;
        }
        currentSize++;
        arr[tail] = o;
        tail = (tail + 1) % size;
    }

    public Object dequeue() {
        if (currentSize==0) {
            System.out.println("Empty Queue, cannot dequeue");
            return null;
        }
        Object temp = arr[head];
        arr[head] = null;
        currentSize--;
        head = (head + 1) % size;
        return temp;
    }

}