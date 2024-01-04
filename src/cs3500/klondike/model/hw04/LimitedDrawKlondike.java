package cs3500.klondike.model.hw04;

import cs3500.klondike.model.hw02.Card;

import java.util.HashMap;
import java.util.Map;

/**
 * LimitedDrawKlondike represents a variation of Klondike Solitaire with limited redraw attempts.
 * Players can redraw a set number of times after which the draw pile will then remove cards.
 *
 * @see AbstractKlondike
 * @see Card
 */
public class LimitedDrawKlondike extends AbstractKlondike {

  private final int numTimesRedrawAllowed;

  private final Map<Card, Integer> redrawCount;

  /**
   * Constructs a LimitedDrawKlondike game with the selected amount of redraw attempts allowed.
   *
   * @param numTimesRedrawAllowed The maximum number of redraw attempts allowed.
   * @throws IllegalArgumentException if numTimesRedrawAllowed is negative.
   */
  public LimitedDrawKlondike(int numTimesRedrawAllowed) {
    if (numTimesRedrawAllowed < 0) {
      throw new IllegalArgumentException("Can't have negative redraw attempts.");
    }
    this.numTimesRedrawAllowed = numTimesRedrawAllowed;
    redrawCount = new HashMap<>();
  }

  @Override
  public void discardDraw() {
    isGameStarted();
    numDraw = Math.min(numDraw, drawPile.size());
    if (drawPile.isEmpty()) {
      throw new IllegalStateException("Draw pile is empty");
    }
    if (!redrawCount.containsKey(drawPile.get(0))) {
      redrawCount.put(drawPile.get(0), 0);
    }
    if (!drawPile.isEmpty()) {
      if (redrawCount.get(drawPile.get(0)) == numTimesRedrawAllowed) {
        drawPile.remove(0);
      } else {
        Card cardToDiscard = drawPile.remove(0);
        int updatedInt = redrawCount.get(cardToDiscard);
        redrawCount.put(cardToDiscard, updatedInt + 1);
        cardToDiscard.switchVisibility();
        drawPile.add(cardToDiscard);
        drawPile.get(0).switchVisibility();
      }
    }
  }

}
