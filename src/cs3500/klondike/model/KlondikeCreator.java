package cs3500.klondike.model;

import cs3500.klondike.model.hw02.BasicKlondike;
import cs3500.klondike.model.hw02.KlondikeModel;
import cs3500.klondike.model.hw04.LimitedDrawKlondike;
import cs3500.klondike.model.hw04.WhiteheadKlondike;

/**
 * A factory class for creating different types of Klondike solitaire games.
 */
public class KlondikeCreator {

  /**
   * Enumerating the different types of Klondike solitaire games.
   */
  public enum GameType {
    BASIC,
    LIMITED,
    WHITEHEAD
  }

  /**
   * Creates a new instance of a Klondike solitaire game based on the given game type.
   *
   * @param type The type of Klondike solitaire game to create.
   * @return A new instance of the specified Klondike game type.
   * @throws IllegalArgumentException If an invalid game type is provided.
   */
  public static KlondikeModel create(GameType type) {
    switch (type) {
      case BASIC:
        return new BasicKlondike();
      case LIMITED:
        return new LimitedDrawKlondike(2);
      case WHITEHEAD:
        return new WhiteheadKlondike();
      default:
        throw new IllegalArgumentException();
    }
  }

  /**
   * Creates a limited draw Klondike with a specified redraw limit.
   *
   * @param numTimesRedraw The number of times cards can be redrawn from the draw pile.
   * @return A LimitedDrawKlondike with the chosen redraw limit.
   */
  public static KlondikeModel createLimitedDrawKlondike(int numTimesRedraw) {
    return new LimitedDrawKlondike(numTimesRedraw);
  }

}
