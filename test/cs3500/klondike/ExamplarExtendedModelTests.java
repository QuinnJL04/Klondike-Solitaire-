package cs3500.klondike;

import cs3500.klondike.model.hw02.Card;
import cs3500.klondike.model.hw04.WhiteheadKlondike;
import cs3500.klondike.model.hw04.LimitedDrawKlondike;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

/**
 * Examplar testing class for testing expected behavior of Limited Klondike and Whitehead Klondike.
 */
public class ExamplarExtendedModelTests {

  LimitedDrawKlondike limitedKlondike;
  WhiteheadKlondike whiteKlondike;
  List<Card> limitedDeck;
  List<Card> whiteDeck;

  //(♣, ♠, ♡, ♢)

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
  public void testDiscardDrawDeletesCards() {
    List<Card> ddDeck = manipulateDeck(limitedDeck, List.of("A♣", "2♣", "3♣", "A♡", "2♡",
            "3♡", "4♣", "4♡", "5♣", "5♡"));
    limitedKlondike.startGame(ddDeck, false, 3, 3);
    for (int i = 0; i < 12; i++) {
      limitedKlondike.discardDraw();
    }
    Assert.assertEquals(0, limitedKlondike.getDrawCards().size());
  }

  @Test
  public void testMovingMultipleCards() {
    List<Card> faceUpDeck = manipulateDeck(whiteDeck,
            List.of("3♠", "A♣", "2♣", "3♣", "2♠", "A♠", "4♠", "4♣"));
    whiteKlondike.startGame(faceUpDeck, false, 3, 1);
    whiteKlondike.movePile(2, 2, 0);
    Assert.assertEquals("A♠", whiteKlondike.getCardAt(0, 2).toString());
  }

  @Test
  public void testMovingToEmptyPile() {
    List<Card> sameSuitDeck = manipulateDeck(whiteDeck,
            List.of("A♣", "3♠", "2♠", "3♣", "2♣", "A♠", "4♣", "4♠"));
    whiteKlondike.startGame(sameSuitDeck, false, 3, 1);
    whiteKlondike.moveToFoundation(0, 1);
    whiteKlondike.movePile(1, 1, 0);
    Assert.assertEquals("3♣", whiteKlondike.getCardAt(0, 0).toString());
  }

  @Test
  public void testMoveMultipleCardsDiffSuitToEmptyPileThrows() {
    List<Card> sameSuitDeck = manipulateDeck(whiteDeck,
            List.of("A♡", "3♡", "3♢", "2♢" , "2♡", "A♢", "4♡", "4♢"));
    whiteKlondike.startGame(sameSuitDeck, false, 3, 1);
    whiteKlondike.moveToFoundation(0, 1);
    Assert.assertThrows(IllegalStateException.class, () ->
            whiteKlondike.movePile(2, 3 , 0));
  }

  @Test
  public void testMovePileAlternatingColorThrows() {
    List<Card> diffColorDeck = manipulateDeck(whiteDeck,
            List.of("3♣", "2♣", "A♡", "3♡", "A♣", "2♡", "4♡", "4♣"));
    whiteKlondike.startGame(diffColorDeck, false, 3, 1);
    Assert.assertThrows(IllegalStateException.class, () ->
            whiteKlondike.movePile(2, 1, 0));
  }

}
