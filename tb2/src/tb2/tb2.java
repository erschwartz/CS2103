package tb2;
import java.util.List;
import java.util.Scanner;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;

public class tb2 {
		private static final String WELCOME_MESSAGE = "Welcome to TextBuddy. %1$s is ready for use.";
		private static final String ADD_MESSAGE = "added to %1$s: \"%2$s\"";
		private static final String DELETE_MESSAGE = "deleted from %1$s: \"%2$s\"";
		private static final String CLEAR_MESSAGE = "all content deleted from %1$s";
		public static final String MESSAGE_INVALID_FORMAT = "invalid command format :%1$s";


		enum COMMAND_TYPE {
			ADD, DISPLAY, CLEAR, EXIT, DELETE, INVALID
		}

		private static Scanner scanner = new Scanner(System.in);
		private static PrintWriter printWriter;
		private static Path FILE_PATH;
		private static String FILE_NAME;

		public static void main(String[] args) {
			FILE_NAME = args[0];
			openFile(FILE_NAME);
			showToUser(String.format(WELCOME_MESSAGE, FILE_NAME));
			while (true) {
				System.out.print("command: ");
				String command = scanner.nextLine();
				String userCommand = command;
				String feedback = executeCommand(userCommand);
				showToUser(feedback);
			}
		}

		private static void openFile(String fileName) {
			FILE_PATH = Paths.get(fileName);
		}

		private static void showToUser(String text) {
			System.out.println(text);
		}

		public static String executeCommand(String userCommand) {
			if (userCommand.trim().equals(""))
				return String.format(MESSAGE_INVALID_FORMAT, userCommand);

			String commandTypeString = getFirstWord(userCommand);
			COMMAND_TYPE commandType = determineCommandType(commandTypeString);

			switch (commandType) {
			case ADD:
				return add(userCommand);
			case DISPLAY:
				return display(userCommand);
			case CLEAR:
				return clear(userCommand);
			case DELETE:
				return delete(userCommand);
			case INVALID:
				return String.format(MESSAGE_INVALID_FORMAT, userCommand);
			case EXIT:
				printWriter.close();
				System.exit(0);
			default:
				//throw an error if the command is not recognized
				throw new Error("Unrecognized command type");
			}
		}

		private static String delete(String userCommand) {
			try {
				int lineNumber = Integer.parseInt(removeFirstWord(userCommand));
				List<String> fileLines = Files.readAllLines(FILE_PATH, Charset.forName("UTF-8"));
				if (lineNumber <= fileLines.size()) {
					String deletedLine = fileLines.remove(lineNumber - 1);
					printWriter = new PrintWriter(FILE_NAME);
					printWriter.print("");
					printWriter.close();
					for (String parameter : fileLines) {
						Files.write(FILE_PATH, parameter.getBytes(), StandardOpenOption.APPEND);
						Files.write(FILE_PATH, "\n".getBytes(), StandardOpenOption.APPEND);
					}
					return String.format(DELETE_MESSAGE, FILE_NAME, deletedLine);
				} else {
					return ("The given index is greater than the number of lines, try again. \n");
				}
			} catch (IOException e) {
				return String.format(MESSAGE_INVALID_FORMAT, userCommand);
			}
		}

		private static String display(String userCommand) {
			try {
				List<String> fileLines = Files.readAllLines(FILE_PATH, Charset.forName("UTF-8"));
				if (fileLines.size() > 0) {
					for (int i = 0; i < fileLines.size(); i++) {
						System.out.println(i + 1 + ". " + fileLines.get(i));
					}
					return "";
				} else {
					return FILE_NAME + " is empty.";
				}
			} catch (IOException e) {
				return String.format(MESSAGE_INVALID_FORMAT, userCommand);
			}
		}

		private static String clear(String userCommand) {
			try {
				printWriter = new PrintWriter(FILE_NAME);
				printWriter.print("");
				printWriter.close();
				return String.format(CLEAR_MESSAGE, FILE_NAME);
			} catch (FileNotFoundException e) {
				return String.format(MESSAGE_INVALID_FORMAT, userCommand);
			}
		}

		private static String add(String userCommand) { 
			try {
				Files.write(FILE_PATH, removeFirstWord(userCommand).getBytes(), StandardOpenOption.APPEND);
				Files.write(FILE_PATH, "\n".getBytes(), StandardOpenOption.APPEND);
				return String.format(ADD_MESSAGE, FILE_NAME, removeFirstWord(userCommand));
			} catch (IOException e) {
				return String.format(MESSAGE_INVALID_FORMAT, userCommand);
			}
		}

		public static String getFirstWord(String userCommand) {
			String commandTypeString = userCommand.trim().split("\\s+")[0];
			return commandTypeString;
		}

		public static String removeFirstWord(String userCommand) {
			return userCommand.replace(getFirstWord(userCommand), "").trim();
		}

		public static COMMAND_TYPE determineCommandType(String commandTypeString) {
			if (commandTypeString == null) {
				throw new Error("command type string cannot be null!");
			} else if (commandTypeString.equalsIgnoreCase("add")) {
				return COMMAND_TYPE.ADD;
			} else if (commandTypeString.equalsIgnoreCase("display")) {
				return COMMAND_TYPE.DISPLAY;
			} else if (commandTypeString.equalsIgnoreCase("exit")) {
				return COMMAND_TYPE.EXIT;
			} else if (commandTypeString.equalsIgnoreCase("clear")) {
				return COMMAND_TYPE.CLEAR;
			} else if (commandTypeString.equalsIgnoreCase("delete")) {
				return COMMAND_TYPE.DELETE;
			} else {
				return COMMAND_TYPE.INVALID;
			}
		}
	

}
