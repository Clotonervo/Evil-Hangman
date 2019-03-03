package hangman;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Iterator;
import java.util.Scanner;
import java.util.Set;
import java.util.TreeSet;

public class Main {

    public static EvilHangmanGame gameInstance = new EvilHangmanGame();
    private static boolean wonGame;

    public static void main(String[] args) throws IOException
    {
        String dictionaryFileName = "";
        int numLetters = 0;
        int numGuesses = 0;

        if (args.length == 3){
            dictionaryFileName = args[0];
            numLetters = Integer.parseInt(args[1]);
            numGuesses = Integer.parseInt(args[2]);
        }

        gameInstance.startGame(new File(dictionaryFileName), numLetters);

        if(!gameInstance.fileFound){
            FileNotFoundException x =new FileNotFoundException();
            System.out.print(x.toString());
            return;
        }

        mainGame(numGuesses, numLetters);

        Iterator<String> iter = gameInstance.currDict.iterator();
        String actualWord = iter.next();

        if(wonGame){
            System.out.println("You won!");
            System.out.println("The word was: " + actualWord);
        }
        else{
            System.out.println("You lose!");
            System.out.println("The word was: " + actualWord);
        }
    }

//===================================== Main game ==========================================
    private static void mainGame(int numGuesses, int numLetters)
    {
        Set<Character> guesses = new TreeSet<Character>();
        char[] currDisplay = new char[numLetters];
        for (int i = 0; i < numLetters; ++i) {
            currDisplay[i] = '-';
        }

        while (numGuesses != 0) {
            System.out.println("You have " + numGuesses + " guesses left");
            String guessedLettersOutput = "Used letters: ";
            Iterator iter = guesses.iterator();
            while (iter.hasNext()) {
                guessedLettersOutput += iter.next() + " ";
            }
            System.out.println(guessedLettersOutput);

            String currWord = "Word: ";
            for (int i = 0; i < currDisplay.length; ++i) {
                currWord += currDisplay[i] + " ";
            }
            System.out.println(currWord);
            String currInputString = "";
            boolean prompt = true;
            while (prompt) {
                try {
                    currInputString = inputGuess();
                    prompt = false;
                } catch (IEvilHangmanGame.GuessAlreadyMadeException x) {
                    System.out.println("Guess already made");
                }
            }

            if(numGuesses == 1) {
                endGame(currInputString, currDisplay);
                boolean gameEnds = true;
                if (!gameInstance.lastGuessWasCorrect) {
                    numGuesses--;
                }
                else if (endOfGame(currDisplay)){
                    wonGame = true;
                    return;
                }
            }
            else{
                mainGamePartTwo(currInputString, currDisplay);
                boolean gameEnds = true;
                if (!gameInstance.lastGuessWasCorrect){
                    numGuesses--;
                }
                else if (endOfGame(currDisplay)){
                    wonGame = true;
                    return;
                }
            }

            guesses.add(currInputString.charAt(0));
        }

        wonGame = false;
    }

//===================================== Input Guess (throws exception) ==========================================
    private static String inputGuess() throws IEvilHangmanGame.GuessAlreadyMadeException
    {
        boolean prompt = true;
        String currInputString = "";

        while (prompt) {
            System.out.println("Enter Guess: ");
            Scanner userInput = new Scanner(System.in);
            currInputString = userInput.next();

            if (currInputString.length() > 1 || currInputString.length() < 1 || !Character.isAlphabetic(currInputString.charAt(0)) || !Character.isLowerCase(currInputString.charAt(0))) {
                System.out.println("Invalid Input");
                continue;
            }
            else {
                prompt = false;
            }
        }
        try{
            gameInstance.makeGuess(currInputString.charAt(0));
            }
        catch (IEvilHangmanGame.GuessAlreadyMadeException x){
            throw x;
        }

        return currInputString;
    }

//===================================== Main Game part two ==========================================
    private static void mainGamePartTwo(String currInputString, char[] currDisplay)
    {
        if (gameInstance.lastGuessWasCorrect){
            String outputString = "";
            for (int i = 0; i < gameInstance.indexOfGuessedLetter.size(); ++i){
                int index = gameInstance.indexOfGuessedLetter.get(i);
                currDisplay[index] = currInputString.charAt(0);
            }
            if (endOfGame(currDisplay)){
                return;
            }
            else if (gameInstance.indexOfGuessedLetter.size() > 1){
                outputString = "Yes, there are " + gameInstance.indexOfGuessedLetter.size() + " " + currInputString.charAt(0) + "'s\n";
            }
            else {
                outputString = "Yes, there is " + gameInstance.indexOfGuessedLetter.size() + " " + currInputString.charAt(0) + "\n";
            }
            System.out.println(outputString);
        }
        else{
            System.out.println("Sorry, there are no " + currInputString.charAt(0) + "'s\n");
        }
    }

//===================================== Last guess output ==========================================

    private static void endGame(String currInputString, char[] currDisplay)
    {
        if (gameInstance.lastGuessWasCorrect){
            String outputString = "";
            for (int i = 0; i < gameInstance.indexOfGuessedLetter.size(); ++i){
                int index = gameInstance.indexOfGuessedLetter.get(i);
                currDisplay[index] = currInputString.charAt(0);
            }
            if (endOfGame(currDisplay)){
                return;
            }
            if (gameInstance.indexOfGuessedLetter.size() > 1){
                outputString = "Yes, there are " + gameInstance.indexOfGuessedLetter.size() + " " + currInputString.charAt(0) + "'s\n";
            }
            else {
                outputString = "Yes, there is " + gameInstance.indexOfGuessedLetter.size() + " " + currInputString.charAt(0) + "\n";
            }
            System.out.println(outputString);
        }
        else{
            return;
        }
    }

//===================================== Checking to see if game is finished ==========================================
    private static boolean endOfGame(char[] array)
    {
        for (int i = 0; i < array.length; ++i) {
            if(array[i] == '-'){
                 return false;
            }
        }
        return true;
    }


    }

