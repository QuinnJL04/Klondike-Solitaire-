package cs3500.klondike.model.hw02;


import java.util.Objects;

/**
 * Represents a card in a Klondike solitaire game. Each card has a suit, number, and color.
 */
public class KlondikeCard implements Card {

  /**
   * Enumerates the suits of a card: SPADES, CLUBS, DIAMONDS, or HEARTS.
   */
  public enum Suits {
    SPADES("♠"),
    CLUBS("♣"),
    DIAMONDS("♢"),
    HEARTS("♡");

    private final String icon;

    Suits(String icon) {
      this.icon = icon;
    }
  }

  /**
   * Enumerates the colors of a card: RED or BLACK.
   */
  public enum Color {
    RED,
    BLACK
  }

  private Suits face;
  private int number;
  private Color color;
  private boolean visible;

  /**
   * Builds a KlondikeCard with a face and number.
   *
   * @param face   the cards suit.
   * @param number the cards rank.
   */
  public KlondikeCard(Suits face, int number) {
    this.face = face;
    this.number = number;
    if (face == Suits.CLUBS || face == Suits.SPADES) {
      this.color = Color.BLACK;
    } else {
      this.color = Color.RED;
    }
    visible = false;
  }

  @Override
  public String toString() {
    String value;
    if (number == 1) {
      value = "A";
    } else if (number <= 10) {
      value = String.valueOf(number);
    } else if (number == 11) {
      value = "J";
    } else if (number == 12) {
      value = "Q";
    } else if (number == 13) {
      value = "K";
    } else {
      value = "";
    }
    return value + face.icon;
  }

  @Override
  public void switchVisibility() {
    visible = !visible;
  }

  @Override
  public boolean isVisible() {
    return visible;
  }

  @Override
  public boolean equals(Object other) {
    if (this == other) {
      return true;
    }
    if (other == null || getClass() != other.getClass()) {
      return false;
    }
    KlondikeCard that = (KlondikeCard) other;
    return number == that.number && Objects.equals(face, that.face);
  }

  @Override
  public int hashCode() {
    return Objects.hash(number, face);
  }

}
