
import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

/**
 * The ApplicationRunner program implements a console based "word guessing"
 * game. It reads the file and chooses one word from it for the game. User has
 * to find out the secret word within 10 guesses, if they do not, they lose.
 * Relevant message will be displayed to the user after the game and user will
 * be given a choice if they want to play again, or exit the game.
 *
 * @author Marcel Kredzel
 */
public class ApplicationRunner {

    static final int EXIT = 0;
    static final int PLAY = 1;
    static final char GIVEUP = '*';

    static int wrongGuesses, totalWords;

    static char[] censor;

    static String guessedLetters, newRandomWord;
    
    static String[] words;

    static Scanner sc = new Scanner(System.in);

    static String dataFile = System.getProperty("user.dir") + File.separator 
                            + "wordlist.txt";

    /**
     * Main method
     *
     * @param args methods
     */
    public static void main(String[] args) {

        countWords(dataFile);

        addWordsToArray(dataFile, words);

        welcomeMsg("Word Guessing Game");

        gameMenu(newRandomWord, words);
    }

    /**
     * Method reads the file and finds out amount of words in a file
     *
     * @param dataFile the name of the file to be read
     */
    public static void countWords(String dataFile) {
        try {
            Scanner fileReader = new Scanner(new File(dataFile));

            while (fileReader.hasNextLine()) {
                totalWords++;
                fileReader.next();
            }

            words = new String[totalWords];

        } catch (FileNotFoundException ex) {
            System.err.println("File Not Found...");
        }
    }

    /**
     * Method reads the file and adds all the words to an array of strings
     *
     * @param dataFile the name of the file to be read
     * @param words the array
     */
    public static void addWordsToArray(String dataFile, String[] words) {
        try {
            Scanner fileReader = new Scanner(new File(dataFile));

            for (int i = 0; i < words.length; i++) {
                words[i] = fileReader.next();
            }
        } catch (FileNotFoundException ex) {
            System.err.println("File Not Found...");
        }
    }

    /**
     * Method gives the choice to the user, whether they want to play the game,
     * or exit
     *
     * @param newRandomWord the random word from the array
     * @param words the array
     */
    public static void gameMenu(String newRandomWord, String[] words) {
        System.out.print("Play (1), or Exit (0) > ");
        int menuChoice = sc.nextInt();
        
        switch (menuChoice) {
            case EXIT:
                sc.close();
                System.exit(0);
            case PLAY:
                newRandomWord = chooseOneWord(words);
                censor = newRandomWord.toCharArray();
                for (int i = 0; i < newRandomWord.length(); i++) {
                    censor[i] = '-';
                }   defaultMsg();
                playOneGame(newRandomWord, words);
                break;
            default:
                System.out.println("Wrong input, please try again...");
                gameMenu(newRandomWord, words);
                break;
        }
    }

    /**
     * Method randomly chooses one word from the array, so it can be displayed
     * for the game
     *
     * @param words the array
     * @return the random word from the array
     */
    public static String chooseOneWord(String[] words) {
        int randomNum = (int) (Math.random() * words.length);
        String randomWord = words[randomNum];
        wrongGuesses = 0;
        guessedLetters = "";
        return randomWord;
    }

    /**
     * Method displays a welcome message, when program starts
     *
     * @param msg the content of the message
     */
    public static void welcomeMsg(String msg) {
        System.out.println(msg);
    }

    /**
     * Method to play a single game
     *
     * @param newRandomWord the chosen word for the game
     * @param words the array
     */
    public static void playOneGame(String newRandomWord, String[] words) {
        while (wrongGuesses <= 11) {

            char guessedLetter = sc.next().charAt(0);
            int indexOf = newRandomWord.indexOf(guessedLetter);

            if (guessedLetter == GIVEUP) {
                giveUp(newRandomWord);
                newRandomWord = chooseOneWord(words);
                gameMenu(newRandomWord, words);

            // Validation of the user's input (lower case character)
            } else if ((int) guessedLetter <= 96 || (int) guessedLetter >= 123){
                System.out.println("Wrong input. Please try again... " 
                                    + "(lower case letter or * to give up)");

            /**
             * Validation of the user's input (same input was entered twice, 
             * and it was a correct letter)    
             */
            } else if (indexOf >= 0 && String.valueOf(censor).contains(String
                                .valueOf(guessedLetter))) {
                System.out.println("You have already tried letter '" 
                        + guessedLetter + "'. It was accepted as correct letter."
                        + " Try again with a different letter...");

            /**
             * Validation of the user's input (same input was entered twice, 
             * and it was a wrong guess)
             */
            } else if (indexOf == -1 && guessedLetters.contains(String
                                .valueOf(guessedLetter))) {
                System.out.println("You have already tried letter '" 
                        + guessedLetter 
                        + "' and it is not a part of the word. Try again...");

            } else if (indexOf == -1 && wrongGuesses == 9) {
                lose(guessedLetter, newRandomWord);
                newRandomWord = chooseOneWord(words);
                gameMenu(newRandomWord, words);

            } else if (indexOf == -1) {
                wrongGuess(guessedLetter);

            } else {
                for (int i = 0; i < newRandomWord.length(); i++) {
                    if (newRandomWord.charAt(i) == guessedLetter) {
                        censor[i] = guessedLetter;
                    }
                }

                if (newRandomWord.equals(String.valueOf(censor))) {
                    win(newRandomWord);
                    newRandomWord = chooseOneWord(words);
                    gameMenu(newRandomWord, words);
                }
                defaultMsg();
            }
        }
    }

    /**
     * Method increases amount of wrong guesses and adds a guess to a string
     * with all wrongly guessed letters
     *
     * @param guessedLetter the input from the user
     */
    public static void wrongGuess(char guessedLetter) {
        wrongGuesses++;
        guessedLetters += guessedLetter;
        guessedLetters += " ";
        defaultMsg();
    }

    // Method displays a default message before every guess
    public static void defaultMsg() {
        System.out.println(censor);
        System.out.println(wrongGuesses + " wrong guesses so far [ " 
                + guessedLetters + "]");
        System.out.println("Have a guess (lower case letter or * to give up)");
    }

    /**
     * Method displays a message for the winner
     *
     * @param newRandomWord the word, which was used for the game
     */
    public static void win(String newRandomWord) {
        System.out.println("The hidden word was " + newRandomWord.toUpperCase());
        System.out.println("you win!");
        System.out.println("");
    }

    /**
     * Method displays a message, if user loses
     *
     * @param guessedLetter the input from the user
     * @param newRandomWord the word, which was used for the game
     */
    public static void lose(char guessedLetter, String newRandomWord) {
        guessedLetters += guessedLetter;
        guessedLetters += " ";
        System.out.println("10 wrong guesses so far [ " + guessedLetters + "]");
        System.out.println("The hidden word was " + newRandomWord.toUpperCase());
        System.out.println("You lose...");
        System.out.println("");
    }

    /**
     * Method displays a message for the user, who decides to give up
     *
     * @param newRandomWord the word, which was used for the game
     */
    public static void giveUp(String newRandomWord) {
        System.out.println("You gave up!");
        System.out.println("The hidden word was " + newRandomWord.toUpperCase());
        System.out.println("You lose...");
        System.out.println("");
    }
}
