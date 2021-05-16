import client.customer.implementation.CustomerCLI;
import client.management.implementation.ManagementCLI;
import client.mutual.model.CustomerReservation;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static java.lang.Integer.parseInt;

public class ClientMain {
    public static void main(String[] args) {
        while (true) {
            System.out.println();
            System.out.println("What would you like to access:");
            System.out.println("1) Customer Console");
            System.out.println("2) Management Console");
            System.out.println("3) Exit");
            System.out.print(System.lineSeparator() + "Selection: ");
            switch (getOption(1, 3)) {
                case 1:
                    System.out.println();
                    new CustomerCLI().initialize();
                    break;
                case 2:
                    System.out.println();
                    new ManagementCLI().initialize();
                    break;
                default:
                    return;
            }
        }
    }

    private static int getOption(int lowerBound, int upperBound) {
        Scanner input = new Scanner(System.in);
        while (true) {
            if (input.hasNextLine()) {
                try {
                    int selection = parseInt(input.nextLine());
                    if (selection >= lowerBound && selection <= upperBound) {
                        return selection;
                    }
                    System.out.print("Invalid entry, try again: ");
                }
                catch (NumberFormatException e) {
                    System.out.print("Invalid entry, try again: ");
                }
            }
        }
    }
}
