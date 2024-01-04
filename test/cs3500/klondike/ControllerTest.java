package cs3500.klondike;

import cs3500.klondike.controller.KlondikeTextualController;
import cs3500.klondike.model.hw02.BasicKlondike;
import cs3500.klondike.model.hw02.Card;

import java.io.Reader;
import java.io.StringReader;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

/**
 * A test class to test the expected behavior of KlondikeTextualController.
 */
public class ControllerTest {

  private BasicKlondike basicKlondike;
  private List<Card> deck;
  private StringBuffer output;
  private KlondikeTextualController controller;

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
    basicKlondike = new BasicKlondike();
    deck = basicKlondike.getDeck();
    output = new StringBuffer();
  }

  //(♣, ♠, ♡, ♢)

  @Test
  public void testPlayGameQuits() {
    Reader reader = new StringReader("q");
    controller = new KlondikeTextualController(reader, output);
    controller.playGame(basicKlondike, basicKlondike.getDeck(), false, 5, 2);
    Assert.assertTrue(output.toString().contains("quit"));
  }

  @Test
  public void testInvalidMove() {
    Reader reader = new StringReader("mpf a 1 1 q");
    controller = new KlondikeTextualController(reader, output);
    controller.playGame(basicKlondike, basicKlondike.getDeck(), false, 5, 2);
    Assert.assertTrue(output.toString().contains("Invalid"));
  }

  @Test
  public void testInvalidMoveCopy() {
    Reader reader = new StringReader("mdf a 1 1 q");
    controller = new KlondikeTextualController(reader, output);
    controller.playGame(basicKlondike, deck, false, 5, 2);
    Assert.assertTrue(output.toString().contains("invalid"));
  }

  @Test
  public void testControllerGameWon() {
    List<Card> quickDeck = manipulateDeck(deck, List.of("A♣", "3♣", "2♣", "A♢","2♢", "3♢"));
    Reader reader = new StringReader("mpf 1 1 mpf 2 1 mpf 2 1 mdf 2 mdf 2 mdf 2");
    controller = new KlondikeTextualController(reader, output);
    controller.playGame(basicKlondike, quickDeck, false, 2, 3);
    Assert.assertTrue(output.toString().contains("win"));
  }

  @Test
  public void testControllerUnwinnableGame() {
    List<Card> quickDeck = manipulateDeck(deck, List.of("4♢", "A♣", "A♢", "2♢", "4♣", "3♣", "2♣",
            "3♢"));
    Reader reader = new StringReader("mpp 3 1 1 mpp 2 1 1 mpf 2 1 mdf 1 md 3 mpf 1 1 mpf 3 1");
    controller = new KlondikeTextualController(reader, output);
    controller.playGame(basicKlondike, quickDeck, false, 3, 2);
    Assert.assertTrue(output.toString().contains("over"));
  }

  @Test
  public void testControllerIgnoresSpecialCharacters() {
    List<Card> quickDeck = manipulateDeck(deck, List.of("A♣", "3♣", "2♣", "A♢","2♢", "3♢"));
    Reader reader = new StringReader("mpf @ @ 1 1 mpf 2 1 mpf 2 1 mdf 2 mdf 2 mdf 2");
    controller = new KlondikeTextualController(reader, output);
    controller.playGame(basicKlondike, quickDeck, false, 2, 3);
    Assert.assertTrue(output.toString().contains("Invalid"));
  }

  @Test
  public void testControllerQuitsMidInput() {
    List<Card> quickDeck = manipulateDeck(deck, List.of("A♣", "3♣", "2♣", "A♢","2♢", "3♢"));
    Reader reader = new StringReader("mpp 1 1 q");
    controller = new KlondikeTextualController(reader, output);
    controller.playGame(basicKlondike, quickDeck, false, 2, 3);
    Assert.assertTrue(output.toString().contains("quit"));
  }

  @Test
  public void testControllerDDRecycles() {
    Reader reader = new StringReader("dd dd dd dd dd dd dd");
    controller = new KlondikeTextualController(reader, output);
    controller.playGame(basicKlondike, deck, false, 7, 3);
    Assert.assertTrue(output.toString().contains("9♢"));
  }

  @Test
  public void testControllerPlacesXOnEmptyPile() {
    List<Card> nineCardSuitDeck = manipulateDeck(deck,
            List.of("A♠", "2♢", "3♢", "2♠", "A♢", "3♠"));
    StringReader reader = new StringReader("md 2 mdf 1 mpf 1 2 mpf 2 2 mdf 2");
    controller = new KlondikeTextualController(reader, output);
    controller.playGame(basicKlondike, nineCardSuitDeck, false, 2, 3);
    Assert.assertTrue(output.toString().contains("X"));
  }

}