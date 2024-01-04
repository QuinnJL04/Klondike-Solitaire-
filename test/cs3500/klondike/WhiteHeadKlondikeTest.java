package cs3500.klondike;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import cs3500.klondike.model.KlondikeCreator;
import cs3500.klondike.model.hw02.Card;
import cs3500.klondike.model.hw04.LimitedDrawKlondike;
import cs3500.klondike.model.hw04.WhiteheadKlondike;

/**
 * Test class to test the behavior of whiteHeadKlondike methods.
 */
public class WhiteHeadKlondikeTest {

  LimitedDrawKlondike limitedKlondike;
  WhiteheadKlondike whiteKlondike;
  List<Card> limitedDeck;
  List<Card> whiteDeck;

  private List<Card> manipulateDeck(List<Card> deck, List<String> manipulatedDeck) {
    List<Card> resultDeck = new ArrayList<>();
    for (String currentCard : manipulatedDeck) {
      for (Card card : deck) {
        if (currentCard.equals(card.toString())) {
          resultDeck.add(card);
        }
      }
    }
    return resultDeck;
  }

  @Before
  public void init() {
    limitedKlondike = new LimitedDrawKlondike(2);
    whiteKlondike = new WhiteheadKlondike();
    limitedDeck = limitedKlondike.getDeck();
    whiteDeck = whiteKlondike.getDeck();
  }


  @Test
  public void whiteHeadIsGameOverFalse1() {
    List<Card> shortDeck = manipulateDeck(whiteDeck,
            List.of("2♣", "A♢",
                    "3♣",
                    "A♣", "2♢", "3♢"));
    whiteKlondike.startGame(shortDeck, false, 2, 3);
    whiteKlondike.moveDrawToFoundation(0);
    Assert.assertFalse(whiteKlondike.isGameOver());
  }

  @Test
  public void whiteHeadIsGameOver() {
    List<Card> shortDeck = manipulateDeck(whiteDeck,
            List.of("2♣", "A♢",
                    "3♣",
                    "2♢", "A♣", "3♢"));
    whiteKlondike.startGame(shortDeck, false, 2, 1);
    whiteKlondike.movePile(0, 1, 1);
    whiteKlondike.moveDraw(0);
    whiteKlondike.moveDrawToFoundation(1);
    Assert.assertFalse(whiteKlondike.isGameOver());
  }

  @Test
  public void faceUpCards() {
    List<Card> faceUpDeck = manipulateDeck(whiteDeck,
            List.of("3♣", "A♡", "2♣", "3♡", "A♣", "2♡", "4♡", "4♣"));
    whiteKlondike.startGame(faceUpDeck, false, 3, 1);
    Assert.assertTrue(whiteKlondike.isCardVisible(0, 0));
    Assert.assertTrue(whiteKlondike.isCardVisible(1, 0));
    Assert.assertTrue(whiteKlondike.isCardVisible(2, 1));
  }

  @Test
  public void moveDrawThrowsInvalidDestPileNegative() {
    whiteKlondike.startGame(whiteDeck, false, 7, 3);
    Assert.assertThrows(IllegalArgumentException.class, () ->
            whiteKlondike.moveDraw(-2));
  }

  @Test
  public void moveDrawThrowsInvalidDestPileGreaterThanNumPiles() {
    whiteKlondike.startGame(whiteDeck, false, 7, 3);
    Assert.assertThrows(IllegalArgumentException.class, () ->
            whiteKlondike.moveDraw(20));
  }

  @Test
  public void emptyDrawThrowsIllegalState() {
    List<Card> shortDeck = manipulateDeck(whiteDeck,
            List.of("A♣", "A♢", "A♠"));
    whiteKlondike.startGame(shortDeck, false, 2, 1);
    Assert.assertThrows(IllegalStateException.class, () ->
            whiteKlondike.moveDraw(1));
  }

  @Test
  public void movePileToEmptyPileSingleSuitRun() {
    List<Card> quickDeck = manipulateDeck(whiteDeck,
            List.of("A♣", "3♠", "3♣",
                    "2♠", "2♣",
                    "A♠",
                    "4♣", "4♠"));
    whiteKlondike.startGame(quickDeck, false, 3, 1);
    whiteKlondike.moveToFoundation(0, 1);
    whiteKlondike.movePile(1, 2, 0);
    Assert.assertEquals("2♠", whiteKlondike.getCardAt(0, 1).toString());
  }

  @Test
  public void moveDrawToEmptyPile() {
    List<Card> quickDeck = manipulateDeck(whiteDeck,
            List.of("A♣", "4♣", "3♣",
                    "2♠", "2♣",
                    "A♠",
                    "3♠", "4♠"));
    whiteKlondike.startGame(quickDeck, false, 3, 1);
    whiteKlondike.moveToFoundation(0, 1);
    whiteKlondike.moveDraw(0);
    Assert.assertEquals("3♠", whiteKlondike.getCardAt(0, 0).toString());
  }

  @Test
  public void moveDrawToCard() {
    List<Card> quickDeck = manipulateDeck(whiteDeck,
            List.of("3♣", "4♣", "A♣",
                    "2♠", "2♣",
                    "A♠",
                    "3♠", "4♠"));
    whiteKlondike.startGame(quickDeck, false, 3, 1);
    whiteKlondike.movePile(1, 1, 0);
    whiteKlondike.moveDraw(1);
    Assert.assertEquals("3♠", whiteKlondike.getCardAt(1, 1).toString());
  }

  @Test
  public void invalidMoveDraw() {
    List<Card> quickDeck = manipulateDeck(whiteDeck,
            List.of("3♣", "4♣", "A♣",
                    "2♣", "2♡",
                    "A♡",
                    "3♡", "4♡"));
    whiteKlondike.startGame(quickDeck, false, 3, 1);
    whiteKlondike.movePile(1, 1, 0);
    Assert.assertThrows(IllegalStateException.class, () -> whiteKlondike.moveDraw(1));
  }

  @Test
  public void moveToFoundationThrowsInvalidSrcPile() {
    whiteKlondike.startGame(whiteDeck, false, 7, 3);
    Assert.assertThrows(IllegalArgumentException.class, () ->
            whiteKlondike.moveToFoundation(-1, 1));
  }

  @Test
  public void moveToFoundationThrowsInvalidDestPile() {
    whiteKlondike.startGame(whiteDeck, false, 7, 3);
    Assert.assertThrows(IllegalArgumentException.class, () ->
            whiteKlondike.moveToFoundation(1, -1));
  }

  @Test
  public void moveToFoundationFromEmptyPileThrows() {
    List<Card> quickDeck = manipulateDeck(whiteDeck,
            List.of("A♣", "4♣", "3♣",
                    "2♠", "2♣",
                    "A♠",
                    "3♠", "4♠"));
    whiteKlondike.startGame(quickDeck, false, 3, 1);
    whiteKlondike.moveToFoundation(0, 1);
    Assert.assertThrows(IllegalStateException.class, () ->
            whiteKlondike.moveToFoundation(0, 1));
  }

  @Test
  public void testGetCardAt() {
    List<Card> quickDeck = manipulateDeck(whiteDeck,
            List.of("A♣", "4♣", "3♣",
                    "2♠", "2♣",
                    "A♠",
                    "3♠", "4♠"));
    whiteKlondike.startGame(quickDeck, false, 3, 1);
    Assert.assertEquals("2♠", whiteKlondike.getCardAt(1, 1).toString());
    Assert.assertEquals("4♣", whiteKlondike.getCardAt(1, 0).toString());
    whiteKlondike.movePile(0, 1, 1);
    Assert.assertEquals("2♠", whiteKlondike.getCardAt(1, 1).toString());
    Assert.assertEquals("A♣", whiteKlondike.getCardAt(1, 2).toString());
  }

  @Test
  public void moveToFoundationThrowsInvalidMove() {
    List<Card> quickDeck = manipulateDeck(whiteDeck,
            List.of("A♣", "4♣", "3♣",
                    "2♠", "2♣",
                    "A♠",
                    "3♠", "4♠"));
    whiteKlondike.startGame(quickDeck, false, 3, 2);
    Assert.assertThrows(IllegalStateException.class, () ->
            whiteKlondike.moveToFoundation(1, 0));
  }

  @Test
  public void moveToFoundationIsValid() {
    List<Card> quickDeck = manipulateDeck(whiteDeck,
            List.of("A♣", "4♣", "3♣",
                    "2♠", "2♣",
                    "A♠",
                    "3♠", "4♠"));
    whiteKlondike.startGame(quickDeck, false, 3, 2);
    whiteKlondike.moveToFoundation(2, 0);
    Assert.assertEquals("A♠", whiteKlondike.getCardAt(0).toString());
  }

  @Test
  public void movePileNumCardsTooBig() {
    List<Card> quickDeck = manipulateDeck(whiteDeck,
            List.of("A♣", "4♣", "3♣",
                    "2♠", "2♣",
                    "A♠",
                    "3♠", "4♠"));
    whiteKlondike.startGame(quickDeck, false, 3, 2);
    Assert.assertThrows(IllegalArgumentException.class, () ->
            whiteKlondike.movePile(2, 4, 0));
  }

  @Test
  public void moveCardToNonEmptyFoundationThrows() {
    List<Card> quickDeck = manipulateDeck(whiteDeck,
            List.of("A♣", "4♣", "3♣",
                    "2♠", "2♣",
                    "A♠",
                    "3♠", "4♠"));
    whiteKlondike.startGame(quickDeck, false, 3, 2);
    whiteKlondike.moveToFoundation(0, 0);
    Assert.assertThrows(IllegalStateException.class, () ->
            whiteKlondike.moveToFoundation(2, 0));
  }

  @Test
  public void moveCardToNonEmptyFoundation() {
    List<Card> quickDeck = manipulateDeck(whiteDeck,
            List.of("A♣", "4♣", "3♣",
                    "2♣", "2♠",
                    "A♠",
                    "3♠", "4♠"));
    whiteKlondike.startGame(quickDeck, false, 3, 2);
    whiteKlondike.moveToFoundation(0, 0);
    whiteKlondike.moveToFoundation(1, 0);
    Assert.assertEquals("2♣", whiteKlondike.getCardAt(0).toString());
  }

  @Test
  public void moveAnyValueCardToEmptyFoundation() {
    List<Card> quickDeck = manipulateDeck(whiteDeck,
            List.of("A♣", "4♣", "3♣",
                    "2♣", "2♠",
                    "A♠",
                    "3♠", "4♠"));
    whiteKlondike.startGame(quickDeck, false, 3, 2);
    whiteKlondike.moveToFoundation(0, 0);
    whiteKlondike.movePile(1, 1, 0);
    Assert.assertEquals("2♣", whiteKlondike.getCardAt(0, 0).toString());
  }

  @Test
  public void whiteheadKlondikeIsConstructed() {
    Assert.assertTrue(KlondikeCreator.create(KlondikeCreator.GameType.WHITEHEAD)
            instanceof WhiteheadKlondike);
  }

  @Test
  public void whiteheadNonSameSuitRunsDontWork() {
    List<Card> deck = manipulateDeck(whiteDeck,
            List.of("3♣", "2♣", "2♠", "A♠", "A♣", "3♠"));
    whiteKlondike.startGame(deck, false, 3, 2);
    Assert.assertThrows(IllegalStateException.class, () ->
            whiteKlondike.movePile(1, 2, 0));
  }

  @Test
  public void whiteHeadSameSuitMultiMoveOffRip() {
    List<Card> deck = manipulateDeck(whiteDeck,
            List.of("3♣", "2♣", "2♠", "A♣", "A♠", "3♠"));
    whiteKlondike.startGame(deck, false, 3, 2);
    whiteKlondike.movePile(1, 2, 0);
    Assert.assertEquals("A♣", whiteKlondike.getCardAt(0, 2).toString());
  }

}
