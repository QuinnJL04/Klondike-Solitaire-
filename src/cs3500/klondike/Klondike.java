package cs3500.klondike;


import java.io.StringReader;

import cs3500.klondike.controller.KlondikeController;
import cs3500.klondike.controller.KlondikeTextualController;
import cs3500.klondike.model.KlondikeCreator;
import cs3500.klondike.model.hw02.KlondikeModel;

/**
 * Entry point for users to select game modes and the number of piles and draws.
 */
public final class Klondike {

  /**
   * Main method to start the Klondike game based on command-line arguments.
   *
   * @param args Command-line arguments: "basic", "limited", or "whitehead" to select the game mode.
   *             For "limited" mode, additional arguments can specify the redraw limit,
   *             number of piles, and draws.
   * @throws IllegalArgumentException If the provided arguments are invalid.
   */
  public static void main(String[] args) {
    KlondikeModel model;
    int numDraw = 3;
    int numPiles = 7;
    if (args.length < 1) {
      throw new IllegalArgumentException("Select a game mode: Basic, Limited, or Whitehead");
    }
    switch (args[0].toLowerCase()) {
      case "basic":
        model = KlondikeCreator.create(KlondikeCreator.GameType.BASIC);
        if (args.length > 1) {
          numPiles = convertStringToInt(args[1]);
        }
        if (args.length > 2) {
          numDraw = convertStringToInt(args[2]);
        }
        break;
      case "limited":
        int redrawLimit;
        if (args.length < 2 || convertStringToInt(args[1]) <= 0) {
          throw new IllegalArgumentException("Cannot have 0 redraws.");
        }
        redrawLimit = convertStringToInt(args[1]);
        model = KlondikeCreator.createLimitedDrawKlondike(redrawLimit);
        if (args.length > 2) {
          numPiles = convertStringToInt(args[2]);
        }
        if (args.length > 3) {
          numDraw = convertStringToInt(args[3]);
        }
        break;
      case "whitehead":
        model = KlondikeCreator.create(KlondikeCreator.GameType.WHITEHEAD);
        if (args.length > 1) {
          numPiles = convertStringToInt(args[1]);
        }
        if (args.length > 2) {
          numDraw = convertStringToInt(args[2]);
        }
        break;
      default:
        throw new IllegalArgumentException("Invalid game type.");
    }
    StringReader readable = new StringReader("Q");
    StringBuffer buffer = new StringBuffer();
    KlondikeController controller = new KlondikeTextualController(readable, buffer);
    try {
      controller.playGame(model, model.getDeck(), false, numPiles, numDraw);
      System.out.println(buffer);
    } catch (IllegalStateException | IllegalArgumentException e) {
      return;
    }
  }

  private static int convertStringToInt(String s) {
    int num;
    try {
      num = Integer.parseInt(s);
    } catch (NumberFormatException e) {
      throw new IllegalArgumentException("Invalid Input.");
    }
    return num;
  }

}
