package Models;

public class Dice {
    public int diceOne = 0;
    public int diceTwo = 0;
    public int turn = 0;

    public Dice() {}

    public Dice(int diceOne, int diceTwo) {
        this.diceOne = diceOne;
        this.diceTwo = diceTwo;
    }

    public int getDiceOne() {
        return diceOne;
    }

    public void setDiceOne(int diceOne) {
        this.diceOne = diceOne;
    }

    public int getDiceTwo() {
        return diceTwo;
    }

    public void setDiceTwo(int diceTwo) {
        this.diceTwo = diceTwo;
    }
}
