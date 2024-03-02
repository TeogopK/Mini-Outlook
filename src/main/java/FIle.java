import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Scanner;

public class FIle {
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        String line = sc.nextLine();

        System.out.println("Hello brother, " + line);

        try (BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter("output.txt"))) {
            bufferedWriter.write(line + "Message received, general!");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
