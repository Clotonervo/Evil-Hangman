package hangman;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.*;

public class EvilHangmanGame implements IEvilHangmanGame {

    public Set<String> currDict = new TreeSet<String>();
    private Map<Pattern, Set<String>> partitions = new HashMap<Pattern, Set<String>>();
    public ArrayList<Integer> indexOfGuessedLetter;
    public Set<Character> setOfGuessedLetters;
    boolean lastGuessWasCorrect = false;
    boolean fileFound = true;
    public char currGess = 'x';

    public EvilHangmanGame(){}


    @SuppressWarnings("serial")
    public static class GuessAlreadyMadeException extends Exception {}

    /**
     * Starts a new game of evil hangman using words from <code>dictionary</code>
     * with length <code>wordLength</code>.
     *	<p>
     *	This method should set up everything required to play the game,
     *	but should not actually play the game. (ie. There should not be
     *	a loop to prompt for input from the user.)
     *
     * @param dictionary Dictionary of words to use for the game
     * @param wordLength Number of characters in the word to guess
     */
    public void startGame(File dictionary, int wordLength)
    {
        setOfGuessedLetters = new TreeSet<Character>();
        currDict = new TreeSet<String>();
        partitions = new HashMap<Pattern, Set<String>>();
        indexOfGuessedLetter = new ArrayList<Integer>();
        lastGuessWasCorrect = false;
        try {
            Scanner input = new Scanner(dictionary);
            while (input.hasNext()){
                String nextInput = input.next();
                if(nextInput.chars().allMatch(Character::isAlphabetic) && nextInput.length() == wordLength){
                    currDict.add(nextInput.toLowerCase());
                }
            }
            input.close();
        }
        catch (FileNotFoundException x){
            fileFound = false;
        }
    }


    /**
     * Make a guess in the current game.
     *
     * @param guess The character being guessed
     *
     * @return The set of strings that satisfy all the guesses made so far
     * in the game, including the guess made in this call. The game could claim
     * that any of these words had been the secret word for the whole game.
     *
     * @throws IEvilHangmanGame.GuessAlreadyMadeException If the character <code>guess</code>
     * has already been guessed in this game.
     */
    public Set<String> makeGuess(char guess) throws IEvilHangmanGame.GuessAlreadyMadeException
    {
        if (!setOfGuessedLetters.add(guess)){
            throw new IEvilHangmanGame.GuessAlreadyMadeException();
        }

        makePartitions(guess);
        Set<String> returnThis = pickNewDictionary();
        currDict = returnThis;

        return returnThis;
    }

//===================================== Make Partitions ==========================================
    private void makePartitions(char guess)
    {
        partitions = new HashMap<Pattern, Set<String>>();
        currGess = guess;

        Iterator<String> iter = currDict.iterator();

        while (iter.hasNext()){
            String currWord = iter.next();
            ArrayList<Integer> indexes = new ArrayList<Integer>();

            for (int i = 0; i < currWord.length(); ++i){
                if (currWord.charAt(i) == guess){
                    indexes.add(i);
                }
            }


            String keyBasedOnIndexes = "";

            Iterator<Integer> indexIter = indexes.iterator();

            while (indexIter.hasNext()){
                keyBasedOnIndexes += indexIter.next() + ",";
            }

            Pattern p = new Pattern(indexes, indexes.size());

            if (indexes.size() == 0){
                if (partitions.containsKey(p)){
                    partitions.get(p).add(currWord);
                }
                else {
                    partitions.put(p, new TreeSet<String>());
                    partitions.get(p).add(currWord);
                }
            }
            else if (partitions.containsKey(p)){
                partitions.get(p).add(currWord);
            }
            else{
                partitions.put(p, new TreeSet<String>());
                partitions.get(p).add(currWord);
            }
        }

    }



//============================ Pick New Dictionary From Partitions ==============================
    private Set<String> pickNewDictionary()
    {
        int maxWords = 0;
        int numLettersInWord = 0;
        indexOfGuessedLetter = new ArrayList<Integer>();
        Set<String> newDictionary = new TreeSet<String>();
        if (currGess == 'r'){
            currGess = 'r';
        }

        Iterator <Map.Entry<Pattern, Set<String>>> mapIter = partitions.entrySet().iterator();

        while (mapIter.hasNext()) {
            Map.Entry<Pattern, Set<String>> entry = mapIter.next();
            if (entry.getValue().size() > maxWords) {
                newDictionary = entry.getValue();
                maxWords = entry.getValue().size();
                numLettersInWord = entry.getKey().numElements;
                indexOfGuessedLetter = entry.getKey().elementIndexes;
                if (entry.getKey().numElements == 0) {
                    lastGuessWasCorrect = false;
                } else {
                    lastGuessWasCorrect = true;
                }
            }
            else if (entry.getValue().size() == maxWords) {
                if (entry.getKey().numElements == 0) {
                    indexOfGuessedLetter = null;
                    lastGuessWasCorrect = false;
                    newDictionary = entry.getValue();
                }
                else if (!lastGuessWasCorrect){
                    continue;
                }
                else if (entry.getKey().numElements < numLettersInWord) {
                    newDictionary = entry.getValue();
                    numLettersInWord = entry.getKey().numElements;
                    indexOfGuessedLetter = entry.getKey().elementIndexes;
                }
                else if (entry.getKey().elementIndexes.get(entry.getKey().elementIndexes.size() - 1) > indexOfGuessedLetter.get(indexOfGuessedLetter.size() - 1) && entry.getKey().numElements < numLettersInWord) {
                    newDictionary = entry.getValue();
                    numLettersInWord = entry.getKey().numElements;
                    indexOfGuessedLetter = entry.getKey().elementIndexes;
                }
                else if (entry.getKey().elementIndexes.get(entry.getKey().elementIndexes.size() - 1) == indexOfGuessedLetter.get(indexOfGuessedLetter.size() - 1) && entry.getKey().numElements < numLettersInWord) {

                    for (int i = entry.getKey().numElements; i >= 0; i--) {
                        if (entry.getKey().elementIndexes.get(i) > indexOfGuessedLetter.get(i)) {
                            newDictionary = entry.getValue();
                            numLettersInWord = entry.getKey().numElements;
                            indexOfGuessedLetter = entry.getKey().elementIndexes;
                        }
                    }
                }
            }
        }

        return newDictionary;
    }



}
