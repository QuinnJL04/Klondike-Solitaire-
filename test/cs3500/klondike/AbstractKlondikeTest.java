package cs3500.klondike;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import cs3500.klondike.model.hw02.BasicKlondike;
import cs3500.klondike.model.hw02.Card;
import cs3500.klondike.model.hw02.KlondikeModel;
import cs3500.klondike.view.KlondikeTextualView;


/**
 * Test class for model implementations to test general behavior.
 */
public class AbstractKlondikeTest {
  private KlondikeModel gameModel;
  private List<Card> deck;

  /**
   * Takes a full 52 card deck and returns a List of Cards based on the specified deck.
   *
   * @param deck            standard 52 card deck.
   * @param manipulatedDeck list of specified cards to keep.
   * @return List of cards based on the passed in manipulated list.
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
  public void testGetDeck() {
    List<Card> deck = gameModel.getDeck();
    Assert.assertNotNull(deck);
    Assert.assertEquals(52, deck.size());
  }

  @Test
  public void testStartGame() {
    gameModel.startGame(deck, false, 7, 3);
    Assert.assertEquals(7, gameModel.getNumPiles());
    Assert.assertEquals(3, gameModel.getNumDraw());
    Assert.assertEquals(7, gameModel.getNumRows());
  }

  @Test
  public void testMovePileValidMoveByPileHeightCheck() {
    gameModel.startGame(deck, false, 7, 3);
    KlondikeTextualView board = new KlondikeTextualView(gameModel);
    System.out.println(board);
    gameModel.movePile(4, 1, 5);
    Assert.assertEquals(7, gameModel.getPileHeight(6));
  }

  @Test
  public void testStartGameThrowsAlreadyStarted() {
    gameModel.startGame(deck, false, 7, 3);
    Assert.assertThrows(IllegalStateException.class, () ->
            gameModel.startGame(deck, false, 7, 3));
  }
  //(♣, ♠, ♡, ♢)

  @Test
  public void testStartGameThrowsInvalidDeck() {
    List<Card> badDeck = manipulateDeck(deck, List.of("2♣", "3♣", "4♣", "2♡", " A♡", "3♡", "4♡"));
    Assert.assertThrows(IllegalArgumentException.class, () ->
            gameModel.startGame(badDeck, false, 3, 1));
  }

  @Test
  public void testStartGameThrowsNoAcesInDeck() {
    List<Card> badDeck = manipulateDeck(deck, List.of("2♣", "3♣", "4♣", "2♡", "3♡", "4♡"));
    Assert.assertThrows(IllegalArgumentException.class, () ->
            gameModel.startGame(badDeck, false, 3, 1));
  }

  @Test
  public void testStartGameThrowsUnequalSuitRuns() {
    List<Card> badDeck = manipulateDeck(deck, List.of("A♣", "2♣", "3♣","A♡" ,"2♡" , "3♡", "4♡"));
    Assert.assertThrows(IllegalArgumentException.class, () ->
            gameModel.startGame(badDeck, false, 3, 1));
  }

  @Test
  public void testStartGameThrowsNotEnoughCards() {
    List<Card> badDeck = manipulateDeck(deck, List.of("A♣"));
    Assert.assertThrows(IllegalArgumentException.class, () ->
            gameModel.startGame(badDeck, false, 7, 3));
  }

  @Test
  public void testStartGameThrowsInvalidNumDraw() {
    Assert.assertThrows(IllegalArgumentException.class, () ->
            gameModel.startGame(deck, false, 7, 0));
    Assert.assertThrows(IllegalArgumentException.class, () ->
            gameModel.startGame(deck, false, 7, -1));
  }

  @Test
  public void testStartGameThrowsInvalidNumPiles() {
    Assert.assertThrows(IllegalArgumentException.class, () ->
            gameModel.startGame(deck, false, 0, 1));
    Assert.assertThrows(IllegalArgumentException.class, () ->
            gameModel.startGame(deck, false, -1, 1));
  }

  //(♣, ♠, ♡, ♢)
  @Test
  public void testStartGamePassesForDoubleSuitDeck() {
    List<Card> doubleSuitsDeck = manipulateDeck(deck,
            List.of("A♣", "2♣", "3♣", "A♡", "2♡", "3♡", "A♣", "2♣", "3♣"));
    gameModel.startGame(doubleSuitsDeck, false, 3, 1);
    KlondikeTextualView view = new KlondikeTextualView(gameModel);
    System.out.println(view);
    Assert.assertEquals("A♣", gameModel.getCardAt(0, 0).toString());
  }

  @Test
  public void testNullDeck() {
    Assert.assertThrows(IllegalArgumentException.class, () ->
            gameModel.startGame(null, false, 7, 2));
  }

  @Test
  public void discardDrawRecyclesInfinitely() {
    gameModel.startGame(deck, false, 7, 4);
    for (int i = 0; i < 1000; i++) {
      gameModel.discardDraw();
    }
    Assert.assertTrue(gameModel.getNumDraw() > 0);
  }

}
