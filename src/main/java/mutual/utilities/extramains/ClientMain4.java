package mutual.utilities.extramains;

import client.customer.implementation.CustomerCLI;
import client.management.implementation.ManagementCLI;

import java.util.Scanner;

import static java.lang.Integer.parseInt;

public class ClientMain4 {
    public static void main(String[] args) {
        System.out.println();
        System.out.println("Would you like to access:");
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
                System.exit(0);
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
