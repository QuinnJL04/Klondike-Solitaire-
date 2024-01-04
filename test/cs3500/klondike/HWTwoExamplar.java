package cs3500.klondike;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import cs3500.klondike.model.hw02.BasicKlondike;
import cs3500.klondike.model.hw02.Card;
import cs3500.klondike.model.hw02.KlondikeModel;


/**
 * Test class for model implementations to test edge cases and expected behavior.
 */
public class HWTwoExamplar {
  private KlondikeModel gameModel;
  private List<Card> deck;

  List<String> cardReps = Arrays.asList(
          "A♠", "2♠", "3♠", "4♠", "5♠", "6♠", "7♠", "8♠", "9♠", "10♠", "J♠", "Q♠", "K♠",
          "A♡", "2♡", "3♡", "4♡", "5♡", "6♡", "7♡", "8♡", "9♡", "10♡", "J♡", "Q♡", "K♡",
          "A♢", "2♢", "3♢", "4♢", "5♢", "6♢", "7♢", "8♢", "9♢", "10♢", "J♢", "Q♢", "K♢",
          "A♣", "2♣", "3♣", "4♣", "5♣", "6♣", "7♣", "8♣", "9♣", "10♣", "J♣", "Q♣", "K♣"
  );

  private List<Card> resultDeck;

  /**
   * Manipulates a deck of cards and filters it to the list of card representations passed in and
   * returning a new deck containing cards specified in the provided card list.
   *
   * @param deck            The original deck of cards.
   * @param manipulatedDeck A list of String card representations that are left in deck.
   * @return A list of cards equal to the specified card representations from the original deck.
   */
  public List<Card> manipulateDeck(List<Card> deck, List<String> manipulatedDeck) {
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
    gameModel = new BasicKlondike();
    deck = gameModel.getDeck();
  }

  @Test
  public void movePileThrowsIllegalStateForWrongColor() {
    resultDeck = manipulateDeck(deck, cardReps);
    gameModel.startGame(resultDeck, false, 2, 1);
    Assert.assertThrows(IllegalStateException.class, () ->
            gameModel.movePile(0, 1, 1));
  }

  @Test
  public void getDrawCardsGetsCorrectNumber() {
    gameModel.startGame(deck, false, 7, 3);
    Assert.assertEquals(3, gameModel.getDrawCards().size());
  }

  @Test
  public void movePileFromInvalidSrcPileThrowsIllegalArg() {
    gameModel.startGame(deck, false, 7, 3);
    Assert.assertThrows(IllegalArgumentException.class, () ->
            gameModel.movePile(7, 1, 1));
  }

  @Test
  public void movePileToInvalidDestPileThrowsIllegalArg() {
    gameModel.startGame(deck, false, 7, 3);
    Assert.assertThrows(IllegalArgumentException.class, () ->
            gameModel.movePile(0, 1, 7));
  }

  @Test
  public void moveDrawToInvalidDestPileThrowsIllegalArg() {
    gameModel.startGame(deck, false, 7, 3);
    Assert.assertThrows(IllegalArgumentException.class, () -> gameModel.moveDraw(7));
  }

  @Test
  public void moveToFoundationFromInvalidSrcPileThrowsIllegalArg() {
    gameModel.startGame(deck, false, 7, 3);
    Assert.assertThrows(IllegalArgumentException.class, () ->
            gameModel.moveToFoundation(7, 0));
  }

  @Test
  public void moveDrawToFoundationToInvalidFoundationPileThrowsIllegalArg() {
    gameModel.startGame(deck, false, 7, 3);
    Assert.assertThrows(IllegalArgumentException.class, () ->
            gameModel.moveDrawToFoundation(4));
  }

  @Test
  public void moveDrawToFoundationNotAllowedThrowsIllegalState() {
    gameModel.startGame(deck, false, 7, 3);
    Assert.assertThrows(IllegalStateException.class, () ->
            gameModel.moveDrawToFoundation(0));
  }

  @Test
  public void movePileNotKingCardToEmptyPile() {
    resultDeck = manipulateDeck(deck, cardReps);
    gameModel.startGame(resultDeck, false, 4, 3);
    gameModel.moveToFoundation(0, 0);
    Assert.assertThrows(IllegalStateException.class, () ->
            gameModel.movePile(3, 1, 0));
  }

  @Test
  public void moveCardWithANumberDifferenceGreaterThanOneToFoundationPile() {
    resultDeck = manipulateDeck(deck, cardReps);
    gameModel.startGame(resultDeck, false, 4, 3);
    gameModel.moveToFoundation(0, 0);
    Assert.assertThrows(IllegalStateException.class, () ->
            gameModel.moveToFoundation(2, 0));
  }

  @Test
  public void testIfDiscardDrawCyclesThroughDrawDeckProperly() {
    gameModel.startGame(deck, false, 7, 3);
    for (int i = 0; i < deck.size(); i++) {
      Card discardedCard = gameModel.getDrawCards().get(0);
      gameModel.discardDraw();
      if (i < deck.size() - 1) {
        Card newTopCard = gameModel.getDrawCards().get(0); // Get the new top card after discarding
        Assert.assertEquals(discardedCard, newTopCard);
      }
    }
  }

  @Test
  public void moveInvalidSuitToFoundationThrowsIllegalState() {
    List<Card> manipulatedDeck = manipulateDeck(deck, cardReps);
    gameModel.startGame(manipulatedDeck, false, 7, 3);
    gameModel.moveToFoundation(0, 0);
    Assert.assertThrows(IllegalStateException.class, () ->
            gameModel.moveToFoundation(6, 0));
  }

  @Test
  public void moveDrawEmptyDrawPile() {
    gameModel.startGame(deck, false, 4, 3);
    Assert.assertThrows(IllegalStateException.class, () -> gameModel.moveDraw(1));
  }

}
