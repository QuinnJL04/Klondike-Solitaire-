package cs3500.klondike.controller;

import cs3500.klondike.model.hw02.Card;
import cs3500.klondike.model.hw02.KlondikeModel;
import cs3500.klondike.view.KlondikeTextualView;

import java.io.IOException;
import java.util.InputMismatchException;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Scanner;

/**
 * Represents a Klondike Textual Controller which takes in a readable
 * input and outputs an appendable.
 */
public class KlondikeTextualController implements cs3500.klondike.controller.KlondikeController {

  private final Readable readable;
  private final Appendable appendable;
  private Boolean quit;


  /**
   * Constructs a KlondikeTextualController with a readable user input and an appendable output.
   *
   * @param readable   Readable to read users input.
   * @param appendable Appendable to transmit output to the user.
   */
  public KlondikeTextualController(Readable readable, Appendable appendable) {
    if (readable == null || appendable == null) {
      throw new IllegalArgumentException("readable, appendable, or basicKlondike is null.");
    }
    this.readable = readable;
    this.appendable = appendable;
  }

  @Override
  public void playGame(KlondikeModel model, List<Card> deck, boolean shuffle, int numPiles,
                       int numDraw) {
    if (model == null) {
      throw new IllegalArgumentException("Model is null.");
    }
    KlondikeTextualView textView = new KlondikeTextualView(model, appendable);
    try {
      model.startGame(deck, shuffle, numPiles, numDraw);
    } catch (IllegalArgumentException e) {
      throw new IllegalStateException("Game could not start.");
    }

    Scanner sc = new Scanner(readable);
    quit = false;
    if (!sc.hasNext()) {
      throw new IllegalStateException("Ran out of input");
    }
    while (!model.isGameOver() && !quit && sc.hasNext()) {
      try {
        textView.render();
        writeMessage(String.format("Score: %d\n", model.getScore()));
        String userInput = sc.next();
        if (userInput.equals("q") || userInput.equals("Q")) {
          quit = true;
        } else {
          processInput(sc, userInput, model);
        }
        if (quit) {
          writeMessage("Game quit!\n" + "State of game when quit:\n");
          textView.render();
          writeMessage(String.format("Score: %d\n", model.getScore()));
        }
      } catch (NoSuchElementException | IOException e) {
        continue;
      }
      try {
        if (deck.size() == model.getScore()) {
          textView.render();
          writeMessage("You win!\n");
        }
        if (model.isGameOver() && model.getScore() != deck.size()) {
          textView.render();
          writeMessage(String.format("Game over. Score: %d\n", model.getScore()));
        }
      } catch (IOException e) {
        throw new IllegalStateException(e.getMessage());
      }
    }
  }

  private void processInput(Scanner scanner, String input, KlondikeModel model) throws IOException {
    try {
      switch (input) {
        case "mpp":
          mppHelper(scanner, model);
          break;
        case "md":
          mdHelper(scanner, model);
          break;
        case "mpf":
          mpfHelper(scanner, model);
          break;
        case "mdf":
          mdfHelper(scanner, model);
          break;
        case "dd":
          ddHelper(model);
          break;
        case "q":
          quit = true;
          break;
        default:
          writeMessage("Invalid move. Play again. Command was invalid. \n");
          break;
      }
    } catch (NoSuchElementException e) {
      System.out.println("Error handled");
    }
  }

  private void ddHelper(KlondikeModel model) {
    try {
      model.discardDraw();
      return;
    } catch (IllegalArgumentException e) {
      writeMessage("Invalid move. Play again. " + e.getMessage() + "\n");
      return;
    }
  }

  private void mdHelper(Scanner scanner, KlondikeModel model) {
    try {
      int drawPile = readValidNumber(scanner) - 1;
      if (drawPile == -25) {
        quit = true;
        return;
      }
      model.moveDraw(drawPile);
      return;
    } catch (IllegalArgumentException | IllegalStateException e) {
      writeMessage("Invalid move. Play again. Not a valid srcPile." + e.getMessage() + "\n");
      return;
    }
  }

  private void mdfHelper(Scanner scanner, KlondikeModel model) {
    try {
      int drawPileFoundation = readValidNumber(scanner) - 1;
      if (drawPileFoundation == -25) {
        quit = true;
        return;
      }
      model.moveDrawToFoundation(drawPileFoundation);
      return;
    } catch (IllegalStateException | IllegalArgumentException e) {
      writeMessage("Invalid move. Play again. " + e.getMessage() + "\n");
      return;
    }
  }

  private void mpfHelper(Scanner scanner, KlondikeModel model) {
    try {
      int sourcePileFoundation = readValidNumber(scanner) - 1;
      int destinationFoundation = readValidNumber(scanner) - 1;
      if (sourcePileFoundation == -25 || destinationFoundation == -25) {
        quit = true;
        return;
      }
      model.moveToFoundation(sourcePileFoundation, destinationFoundation);
      return;
    } catch (IllegalArgumentException | IllegalStateException e) {
      writeMessage("Invalid move. Play again. " + e.getMessage() + "\n");
      return;
    }
  }

  private void mppHelper(Scanner scanner, KlondikeModel model) {
    try {
      int sourcePile = readValidNumber(scanner) - 1;
      int numCards = readValidNumber(scanner);
      int destinationPile = readValidNumber(scanner) - 1;
      if (sourcePile == -25 || numCards == -25 || destinationPile == -25) {
        quit = true;
        return;
      }
      model.movePile(sourcePile, numCards, destinationPile);
      return;
    } catch (IllegalArgumentException | IllegalStateException e) {
      writeMessage("Invalid move. Play again. " + e.getMessage() + "\n");
      return;
    }
  }

  private int readValidNumber(Scanner scanner) {
    while (true) {
      try {
        String input = scanner.next();
        if (input.equalsIgnoreCase("q")) {
          quit = true;
          return -25;
        }
        return Integer.parseInt(input);
      } catch (NumberFormatException e) {
        writeMessage("Invalid input. Please enter a valid number.\n");
      } catch (InputMismatchException e) {
        writeMessage("Game quit!\n");
      }
    }
  }

  private void writeMessage(String message) throws IllegalStateException {
    try {
      appendable.append(message);
    } catch (IOException e) {
      throw new IllegalStateException(e.getMessage());
    }
  }

}
