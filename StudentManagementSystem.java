import java.util.*;

/* ---------- INTERFACE (abstraction) ---------- */
interface Gradable {
    double calculateAverage();
    String getGrade();
}

/* ---------- ABSTRACT CLASS (abstraction) ---------- */
abstract class Person {
    // Encapsulation: private fields + public getters/setters
    private String name;
    private int age;
    private String email;

    public Person(String name, int age, String email) {
        this.name = name;
        this.age = age;
        this.email = email;
    }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public int getAge() { return age; }
    public void setAge(int age) { this.age = age; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    // Must be implemented by child classes (polymorphism)
    public abstract String getRole();

    @Override
    public String toString() {
        return String.format("%s | %s | Age: %d | %s", getRole(), name, age, email);
    }
}

/* ---------- INHERITANCE: Student extends Person ---------- */
class Student extends Person implements Gradable {
    private final int rollNo;
    private String course;
    private final Map<String, Double> marks = new LinkedHashMap<>();

    public Student(int rollNo, String name, int age, String email, String course) {
        super(name, age, email);
        this.rollNo = rollNo;
        this.course = course;
    }

    public int getRollNo() { return rollNo; }
    public String getCourse() { return course; }
    public void setCourse(String course) { this.course = course; }

    public void addMarks(String subject, double score) {
        marks.put(subject, score);
    }

    public Map<String, Double> getMarks() { return marks; }

    @Override
    public String getRole() { return "Student"; }

    @Override
    public double calculateAverage() {
        if (marks.isEmpty()) return 0;
        double sum = 0;
        for (double m : marks.values()) sum += m;
        return sum / marks.size();
    }

    @Override
    public String getGrade() {
        double avg = calculateAverage();
        if (marks.isEmpty()) return "N/A";
        if (avg >= 90) return "A+";
        if (avg >= 80) return "A";
        if (avg >= 70) return "B";
        if (avg >= 60) return "C";
        if (avg >= 40) return "D";
        return "F";
    }

    @Override
    public String toString() {
        return String.format("Roll No: %d | %s | Course: %s", rollNo, super.toString(), course);
    }
}

/* ---------- CUSTOM EXCEPTION ---------- */
class StudentNotFoundException extends Exception {
    public StudentNotFoundException(int rollNo) {
        super("No student found with roll number " + rollNo);
    }
}

/* ---------- MANAGER CLASS (business logic) ---------- */
class StudentManager {
    private final Map<Integer, Student> students = new TreeMap<>();

    public boolean addStudent(Student s) {
        if (students.containsKey(s.getRollNo())) return false;
        students.put(s.getRollNo(), s);
        return true;
    }

    public Student findStudent(int rollNo) throws StudentNotFoundException {
        Student s = students.get(rollNo);
        if (s == null) throw new StudentNotFoundException(rollNo);
        return s;
    }

    public void deleteStudent(int rollNo) throws StudentNotFoundException {
        findStudent(rollNo);
        students.remove(rollNo);
    }

    public Collection<Student> getAllStudents() {
        return students.values();
    }
}

/* ---------- MAIN CLASS (user interface) ---------- */
public class StudentManagementSystem {
    private static final Scanner sc = new Scanner(System.in);
    private static final StudentManager manager = new StudentManager();

    public static void main(String[] args) {
        int choice;
        do {
            showMenu();
            choice = readInt("Enter your choice: ");
            try {
                switch (choice) {
                    case 1 -> addStudent();
                    case 2 -> viewAll();
                    case 3 -> searchStudent();
                    case 4 -> updateStudent();
                    case 5 -> deleteStudent();
                    case 6 -> addMarks();
                    case 7 -> reportCard();
                    case 0 -> System.out.println("Goodbye!");
                    default -> System.out.println("Invalid choice, try again.");
                }
            } catch (StudentNotFoundException e) {
                System.out.println("Error: " + e.getMessage());
            }
        } while (choice != 0);
    }

    private static void showMenu() {
        System.out.println("\n===== STUDENT MANAGEMENT SYSTEM =====");
        System.out.println("1. Add Student");
        System.out.println("2. View All Students");
        System.out.println("3. Search Student");
        System.out.println("4. Update Student");
        System.out.println("5. Delete Student");
        System.out.println("6. Add Marks");
        System.out.println("7. Report Card");
        System.out.println("0. Exit");
    }

    private static void addStudent() {
        int roll = readInt("Roll No: ");
        String name = readString("Name: ");
        int age = readInt("Age: ");
        String email = readString("Email: ");
        String course = readString("Course: ");
        boolean added = manager.addStudent(new Student(roll, name, age, email, course));
        System.out.println(added ? "Student added successfully." : "Roll number already exists.");
    }

    private static void viewAll() {
        if (manager.getAllStudents().isEmpty()) {
            System.out.println("No students found.");
            return;
        }
        for (Student s : manager.getAllStudents()) System.out.println(s);
    }

    private static void searchStudent() throws StudentNotFoundException {
        System.out.println(manager.findStudent(readInt("Roll No: ")));
    }

    private static void updateStudent() throws StudentNotFoundException {
        Student s = manager.findStudent(readInt("Roll No to update: "));
        s.setName(readString("New Name: "));
        s.setAge(readInt("New Age: "));
        s.setEmail(readString("New Email: "));
        s.setCourse(readString("New Course: "));
        System.out.println("Student updated.");
    }

    private static void deleteStudent() throws StudentNotFoundException {
        manager.deleteStudent(readInt("Roll No to delete: "));
        System.out.println("Student deleted.");
    }

    private static void addMarks() throws StudentNotFoundException {
        Student s = manager.findStudent(readInt("Roll No: "));
        String subject = readString("Subject: ");
        double score = readInt("Marks (0-100): ");
        if (score < 0 || score > 100) {
            System.out.println("Marks must be between 0 and 100.");
            return;
        }
        s.addMarks(subject, score);
        System.out.println("Marks saved.");
    }

    private static void reportCard() throws StudentNotFoundException {
        Student s = manager.findStudent(readInt("Roll No: "));
        System.out.println("\n--- REPORT CARD ---");
        System.out.println(s);
        for (Map.Entry<String, Double> e : s.getMarks().entrySet())
            System.out.printf("  %-15s %.1f%n", e.getKey(), e.getValue());
        System.out.printf("Average: %.2f | Grade: %s%n", s.calculateAverage(), s.getGrade());
    }

    /* ---------- input helpers ---------- */
    private static int readInt(String msg) {
        while (true) {
            System.out.print(msg);
            try {
                return Integer.parseInt(sc.nextLine().trim());
            } catch (NumberFormatException e) {
                System.out.println("Please enter a valid number.");
            }
        }
    }

    private static String readString(String msg) {
        System.out.print(msg);
        return sc.nextLine().trim();
    }
}
