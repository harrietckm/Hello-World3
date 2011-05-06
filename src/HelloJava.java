import java.util.Scanner;


public class HelloJava {

    /**
     * @param args
     */
    public static void main(String[] args) {

        // TODO Auto-generated method stub
        System.out.println("I'm here in linux");
        String name = "Pig";
        Scanner inp = new Scanner(System.in);

        System.out.println(name + ", please enter your name... ");

        name = inp.nextLine();
        System.out.println("Thank you " + name );
        //blah blah adding extra stuff

    }


}
