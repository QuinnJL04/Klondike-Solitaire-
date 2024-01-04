package cs3500.klondike.model.hw02;

/**
 * Represents a standard playing card that has a suit and rank.
 */
public interface Card {

  /**
   * Renders a card with its value followed by its suit as one of
   * the following symbols (♣, ♠, ♡, ♢).
   * For example, the 3 of Hearts is rendered as {@code "3♡"}.
   * @return the formatted card
   */
  String toString();

  /**
   * Switches the cards visibility.
   */
  void switchVisibility();

  /**
   * Sees if the card is visible.
   *
   * @return true if the card is face-up.
   */
  boolean isVisible();
}
