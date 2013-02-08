//Needs_Input
//Class:MainList
//Call-Method:main(new String[0])

import jeliot.io.*;

public class Node {
    int info;
    Node next;
    
    public Node() {}
    
    public Node(int newInfo) {
        info = newInfo;
        next = null;
    }
    
    public int getInfo() {
        return info;
    }
    
    public Node getNext() {
        return next;
    }
    
    public void setInfo(int newInfo) {
        info = newInfo;
    }
    
    public void setNext(Node newNext) {
        next = newNext;
    }
    
    public String toString() {
        if (next != null) {
            return info + "->" + next;
        } else {
            return "" + info;
        }
    }
}

public class List {

    Node anchor;
    
    public List() {
        anchor = new Node();
    }
    
    public Node listAnchor() {
        return anchor;
    }
    
    public boolean listIsEmpty() {
        return (anchor.getNext() == null);
    }
    
    public Node listNext(Node node) {
        return node.getNext();
    }
    
    public Node listPrev(Node node) {
        Node p = this.listAnchor();
        while (this.listNext(p) != node) {
            p = this.listNext(p);
        }
        return p;
    }
    
    public void listInsert(Node node, int x) {
        Node newNode = new Node(x);
        newNode.setNext(node.getNext());
        node.setNext(newNode);
    }
    
    public void listDelete(Node node) {
        this.listPrev(node).setNext(this.listNext(node));
    }
    
    public int listRetrieve(Node node) {
        return node.getInfo();
    }
    
    public void listUpdate(Node node, int newInfo) {
        node.setInfo(newInfo);
    }
    
    public Node listEnd() {
        return null;
    }
    
    public void readList() {
        int x;
        System.out.println("Enter value - 0 to end");
        x = Input.readInt();
        Node temp = listAnchor();
        while (x != 0) {
            listInsert(temp, x);
            temp = listNext(temp);
            System.out.println("Enter value - 0 to end");
            x = Input.readInt();
        }
    }
    
    public void printList() {
        /*
        if (!listIsEmpty()) {
            System.out.println(anchor.getNext().toString());
        }
        */
        if (!listIsEmpty()) {
            Node temp = listNext(listAnchor());
            while (temp != listEnd()) {
                System.out.print("->" + listRetrieve(temp));
                temp = listNext(temp);
            }
        } else {
            System.out.print("->");
        }
        System.out.println();
    }
}

/**
 * @author nmyller
 */
public class MainList {
    
    public static boolean inList(List l, int x) {
        Node p = l.listAnchor();
        while (p != l.listEnd()) {
            if (l.listRetrieve(p) == x) {
                return true;
            } else {
                p = l.listNext(p);
            }
        }
        return false;
    }
    
    public static void main(String[] args) {
        List l = new List();
        l.readList();
        l.printList();
        System.out.println("Enter a value to search: ");
        int x = Input.readInt();
        System.out.println("is in list? " + inList(l, x));
        System.out.println("Enter a value to search: ");
        x = Input.readInt();
        System.out.println("is in list? " + inList(l, x));
    }
    
    
}
