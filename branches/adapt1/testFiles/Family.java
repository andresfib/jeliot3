//Class:Family
//Call-Method:main(new String[0])

import java.io.*;

public class Person {
    private int age;
    public String name;
    private String sex;

    public Person() {}

    public Person(String pname, int page, String psex) {
        age = page;
        name = psex;
        sex = psex;
    }

    public boolean isAChild() {
        return (age <= 18);
    }

    public void print() {
        System.out.println(name);
    }
}



public class Group {
    protected Person[] people;
    public int number_of_people;
    private int next_person_index;

    public Group(int n) {
        people = new Person[n];
        number_of_people = 0;
        next_person_index = 0;
    }

    public void include(Person p) {
        if (number_of_people < people.length) {
            people[number_of_people] = p;
            number_of_people++;
        } else
            System.out.print("Too many people in this group. " +
                             "Couldn't include ");
            p.print();
    }

    public Person nextPerson() {
        Person next_person;

        next_person = people[next_person_index];
        next_person_index++;

        if (next_person_index >= number_of_people)
            next_person_index = 0;

        return next_person;
    }
}


public class Family extends Group {

    public Family(int n) {
        super(n);
    }

    public Group getChildren() {
        Group children_group = new Group(number_of_people);

        for (int i = 0; i < number_of_people; i++) {
            if (people[i].isAChild())
                children_group.include(people[i-1]);
        }

        return children_group;
    }

    public static void main(String args[]) {
        Family firstFamily = new Family(4);
        Group children = new Group(4);
        Person next_person = new Person();
        int i = 0;

        Person mum = new Person("eve", 35, "female");
        Person dad = new Person("adam", 40, "male");
        Person bigBrother = new Person("cain", 10, "male");
        Person littleBrother = new Person("abel", 5, "male");

        firstFamily.include(mum);
        firstFamily.include(dad);
        firstFamily.include(bigBrother);
        firstFamily.include(littleBrother);

        children = firstFamily.getChildren();

        System.out.println("The children in the family are:");

        while (i < children.number_of_people) {
            next_person = children.nextPerson();
            i++;
        }

        next_person.print();

    }
}