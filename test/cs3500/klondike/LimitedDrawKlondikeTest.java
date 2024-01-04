package cs3500.klondike;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

import cs3500.klondike.controller.KlondikeTextualController;
import cs3500.klondike.model.KlondikeCreator;
import cs3500.klondike.model.hw02.Card;
import cs3500.klondike.model.hw04.LimitedDrawKlondike;

/**
 * Test class for testing LimitedDrawKlondike methods.
 */
public class LimitedDrawKlondikeTest {

  LimitedDrawKlondike limitedKlondike;
  List<Card> deck;

  @Before
  public void init() {
    limitedKlondike = new LimitedDrawKlondike(2);
    deck = limitedKlondike.getDeck();
  }

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

  @Test
  public void sameRunsDoesntThrow() {
    List<Card> smallDeck = manipulateDeck(deck, List.of("A♣", "A♣", "2♣", "2♣", "3♣", "3♣"));
    limitedKlondike.startGame(smallDeck, false, 3, 3);
    Assert.assertTrue(limitedKlondike.getCardAt(0, 0).isVisible());
    Assert.assertEquals("A♣", limitedKlondike.getCardAt(0, 0).toString());
  }

  @Test
  public void testLimitedThrowsArgNegativeRedraw() {
    Assert.assertThrows(IllegalArgumentException.class, () ->
            new LimitedDrawKlondike(-1));
  }

  @Test
  public void controllerWorksWithOtherModels() {
    StringReader reader = new StringReader("dd q");
    StringBuffer buffer = new StringBuffer();
    KlondikeTextualController controller = new KlondikeTextualController(reader, buffer);
    controller.playGame(limitedKlondike,
            limitedKlondike.getDeck(), false, 7, 3);
    Assert.assertTrue(buffer.toString().contains("Game quit!"));
  }

  @Test
  public void testLimitedDrawDiscards() {
    List<Card> smallDeck = manipulateDeck(deck,
            List.of("A♣", "3♢",
                    "2♢",
                    "2♣", "4♢", "A♢", "3♣", "4♣"));
    limitedKlondike.startGame(smallDeck, false, 2, 5);
    Assert.assertEquals(5, limitedKlondike.getDrawCards().size());
    for (int i = 0; i < 15; i++) {
      limitedKlondike.discardDraw();
    }
    Assert.assertEquals(0, limitedKlondike.getDrawCards().size());
  }

  @Test
  public void negativeNumDrawThrows() {
    List<Card> ddDeck = manipulateDeck(deck, List.of("A♣", "2♣", "3♣", "A♡", "2♡",
            "3♡", "4♣", "4♡", "5♣", "5♡"));
    limitedKlondike.startGame(ddDeck, false, 3, 3);
    for (int i = 0; i < 10; i++) {
      limitedKlondike.discardDraw();
    }
    Assert.assertEquals(3, limitedKlondike.getNumDraw());
    limitedKlondike.discardDraw();
    Assert.assertEquals(2, limitedKlondike.getNumDraw());
    limitedKlondike.discardDraw();
    Assert.assertEquals(1, limitedKlondike.getNumDraw());
    Assert.assertThrows(IllegalStateException.class, () -> limitedKlondike.discardDraw());
    //Assert.assertEquals(0, limitedKlondike.getDrawCards().size());
  }

  @Test
  public void basicLimitedDrawIsConstructed() {
    Assert.assertTrue(KlondikeCreator.create(KlondikeCreator.GameType.LIMITED)
            instanceof LimitedDrawKlondike);
  }

}
