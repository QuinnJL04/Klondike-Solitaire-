package cs3500.klondike.view;

import java.io.IOException;
import java.util.List;

import cs3500.klondike.model.hw02.Card;
import cs3500.klondike.model.hw02.KlondikeModel;

/**
 * A simple text-based rendering of the Klondike game.
 */
public class KlondikeTextualView implements TextualView {
  private final KlondikeModel model;
  private Appendable appendable;

  /**
   * Builds a KlondikeTextualView with the given model.
   *
   * @param model The KlondikeModel representing the state of the game.
   */
  public KlondikeTextualView(KlondikeModel model) {
    this.model = model;
  }

  /**
   * Builds a KlondikeTextualView with the given model and an Appendable object that the game will
   * be rendered with.
   *
   * @param model      The KlondikeModel representing the game state.
   * @param appendable The Appendable object to which the game board will be rendered.
   */
  public KlondikeTextualView(KlondikeModel model, Appendable appendable) {
    this.model = model;
    this.appendable = appendable;
  }

  @Override
  public void render() throws IOException {
    appendable.append(toString() + "\n");
  }

  /**
   * Makes a String representation of the game board.
   *
   * @return String game board.
   */
  public String toString() {
    StringBuilder boardString = new StringBuilder();

    // Draw pile
    boardString.append("Draw: ");
    List<Card> drawCards = model.getDrawCards();
    for (int i = 0; i < drawCards.size(); i++) {
      boardString.append(drawCards.get(i).toString());
      if (i < drawCards.size() - 1) {
        boardString.append(", ");
      }
    }
    boardString.append("\n");

    // Foundation piles
    boardString.append("Foundation: ");
    for (int i = 0; i < model.getNumFoundations(); i++) {
      Card card = model.getCardAt(i);
      if (card == null) {
        boardString.append("<none>");
      } else {
        boardString.append(card.toString());
      }
      if (i < model.getNumFoundations() - 1) {
        boardString.append(", ");
      }
    }
    boardString.append("\n");

    // Cascade piles
    for (int row = 0; row < model.getNumRows(); row++) {
      for (int pile = 0; pile < model.getNumPiles(); pile++) {
        try {
          if (model.getPileHeight(pile) == 0 && row == 0) {
            boardString.append("  X");
            continue;
          }

          if (model.isCardVisible(pile, row)) {
            Card card = model.getCardAt(pile, row);
            if (card.toString().length() == 3) {
              boardString.append(card.toString());
            } else {
              boardString.append(" ").append(card.toString());
            }
          } else {
            boardString.append("  ?");
          }
        } catch (Exception e) {
          boardString.append("   ");
        }
      }
      boardString.append("\n");
    }
    boardString.deleteCharAt(boardString.length() - 1);
    return boardString.toString();
  }

}
