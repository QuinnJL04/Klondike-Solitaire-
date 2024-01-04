package cs3500.klondike;

import cs3500.klondike.controller.KlondikeTextualController;
import cs3500.klondike.model.hw02.BasicKlondike;
import cs3500.klondike.model.hw02.Card;

import java.io.StringReader;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;


/**
 * A test class to test for edge cases and expected behavior of the KlondikeTextualController.
 */
public class ExamplarControllerTests {

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
  public void testMultipleMovesSequenceControllerWithoutMPP() {
    List<Card> nineCardSuitDeck = manipulateDeck(deck,
            List.of("A♠", "2♢", "3♢", "2♠", "A♢", "3♠"));
    StringReader reader = new StringReader("md 2 mdf 1 mpf 1 2 mpf 2 2 mdf 2");
    controller = new KlondikeTextualController(reader, output);

    controller.playGame(basicKlondike, nineCardSuitDeck, false, 2, 3);
    Assert.assertTrue(basicKlondike.isGameOver());
  }

  @Test
  public void testControllerMultipleMovesWithMPP() {
    List<Card> multipleMovesDeck = manipulateDeck(deck,
            List.of("4♢", "3♢", "2♠", "A♢", "4♠", "3♠", "2♢", "A♠"));

    controller = new KlondikeTextualController(
            new StringReader("mpp 3 1 1 md 1 mdf 1 mpf 2 2 mpf 1 2 mpf 2 2"), output);

    controller.playGame(basicKlondike, multipleMovesDeck, false, 3, 2);
    Assert.assertTrue(basicKlondike.isGameOver());
  }

  @Test
  public void testInvalidInputControllerReprompts() {
    List<Card> smallDeck = manipulateDeck(deck, List.of("3♠", "A♢", "2♢", "2♠", "A♠", "3♢"));
    StringReader reader = new StringReader("mpp 2 3 3 3 1 1 q");
    controller = new KlondikeTextualController(reader, output);

    controller.playGame(basicKlondike, smallDeck, false, 2, 1);
    Assert.assertFalse(basicKlondike.isGameOver());
  }

  @Test
  public void testControllerQuits() {
    StringReader readable = new StringReader("q");
    StringBuffer appendable = new StringBuffer();

    controller = new KlondikeTextualController(readable, appendable);
    controller.playGame(basicKlondike, deck, false, 7, 3);
    String out = appendable.toString();
    Assert.assertTrue(out.contains("Game quit!"));
    Assert.assertTrue(out.contains("State of game when quit:"));
  }

  @Test
  public void testControllerOutputHasTheRightAmountOfLines() {
    controller = new KlondikeTextualController(new StringReader("q"), output);

    controller.playGame(basicKlondike, deck, false, 5, 3);

    Assert.assertEquals(18, output.toString().split("\n").length);
  }

  @Test
  public void testInvalidMoveThenQuitController() {
    List<Card> quickDeck = manipulateDeck(deck,
            List.of("3♣", "4♢", "4♣", "2♣", "3♢","2♢", "A♢", "A♣"));
    StringReader reader = new StringReader("mpp 3 1 0 md 0 q");
    controller = new KlondikeTextualController(reader, output);
    controller.playGame(basicKlondike, quickDeck, false, 3, 1);
    Assert.assertTrue(output.toString().split("\n").length > 11);
  }

  @Test
  public void testInvalidMoveOutputs() {
    StringReader reader = new StringReader("md mpp q");
    controller = new KlondikeTextualController(reader, output);
    controller.playGame(basicKlondike, deck, false, 7, 3);
    String out = output.toString();
    Assert.assertTrue(out.contains("Game quit!"));
    Assert.assertTrue(out.contains("State of game when quit:"));
  }

}