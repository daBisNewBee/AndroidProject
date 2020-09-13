package koal.glide_demo;

import org.junit.Test;

import java.util.function.Consumer;
import java.util.function.Predicate;

/**
 * Created by wenbin.liu on 2020-07-02
 *
 * @author wenbin.liu
 */
public class LamdaTest {

    static class Student{
        private String name;

        public Student(String name) {
            this.name = name;
        }

        public void setNewName(String name) {
            this.name = name;
        }

        private void printName() {
            System.out.println("name = " + name);
        }
    }

    @Test
    public void add() {
        int i = 0;
        for (int j = 0; j < 100; j++) {
            if (i++ == 50) {
                System.out.println("i == 50");
            }
        }
        System.out.println("i = " + i);
    }

    @Test
    public void consumer() {
        Student student = new Student("aaa");
        student.printName();
        Consumer<Student> consumer = new Consumer<Student>() {
            @Override
            public void accept(Student student) {
                student.setNewName("bbb");
            }
        };
        consumer.accept(student);
        student.printName();

        Consumer<Student> consumer1 = stu -> stu.setNewName("ccc");
        consumer1.accept(student);
        student.printName();

        Predicate<Student> predicate = new Predicate<Student>() {
            @Override
            public boolean test(Student student) {
                return student.name.equals("aaa");
            }
        };
        predicate.test(student);
    }
}
