package util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public abstract class InputReading {

	static private BufferedReader br = new BufferedReader(new InputStreamReader(System.in));

	public static String getString() {
		try {
			String input = InputReading.br.readLine();
			System.out.println();
			return input;
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	public static boolean getBoolean(String question) {
		while (true) {
			System.out.println(question + " (y/n)");
			switch (getString().toLowerCase()) {
				case "y":
					return true;
				case "n":
					return false;
				default:
					System.out.println("Unrecognised response. Please enter 'y' or 'n'.\n");
			}
		}
	}

	public static void getVoid() {
		try {
			InputReading.br.read();
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

}
