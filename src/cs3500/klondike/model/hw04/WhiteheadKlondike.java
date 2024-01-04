package cs3500.klondike.model.hw04;

import cs3500.klondike.model.hw02.Card;

import java.util.EmptyStackException;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.Stack;

/**
 * WhiteheadKlondike is a variation of Klondike Solitaire where cascade cards are dealt face up, and
 * valid moves include same color (single card move) or same suit runs (multiple card moves).
 *
 * @see AbstractKlondike
 * @see Card
 */
public class WhiteheadKlondike extends AbstractKlondike {

  /**
   * Constructs a WhiteheadKlondike object. Takes no parameters.
   */
  public WhiteheadKlondike() {
    super();
  }


  @Override
  public void startGame(List<Card> deck, boolean shuffle, int numPiles, int numDraw)
          throws IllegalArgumentException {
    super.startGame(deck, shuffle, numPiles, numDraw);

  }

  @Override
  protected void dealToCascadeAndDrawPiles() {
    int current;
    int firstIndex = 0;
    Iterator<Card> dickerator = deck.iterator();
    //Dealing to cascade piles.
    while (firstIndex < numPiles) {
      for (current = firstIndex; current < numPiles; current++) {
        Stack<Card> cascadePile = cascadePiles.get(current);
        Card currentCard = dickerator.next();
        if (!currentCard.isVisible()) {
          currentCard.switchVisibility();
        }
        cascadePile.push(currentCard);
      }
      firstIndex++;
    }
    //Dealing to draw pile
    while (dickerator.hasNext()) {
      drawPile.add(dickerator.next());
    }
    if (!drawPile.isEmpty()) {
      for (int i = 0; i < numDraw; i++) {
        drawPile.get(i).switchVisibility();
      }
    }
  }

  @Override
  public void movePile(int srcPile, int numCards, int destPile) {
    isGameStarted();
    validatePileIndices(srcPile, destPile);
    validateNumCardsToMove(numCards);
    Stack<Card> srcStack = cascadePiles.get(srcPile);
    if (numCards > srcStack.size()) {
      throw new IllegalArgumentException("Not enough cards in the source pile.");
    }
    moveCards(numCards, destPile, srcStack);
  }

  private void moveCards(int numCards, int destPile, Stack<Card> srcStack) {
    Stack<Card> destStack = cascadePiles.get(destPile);
    // Initialize empty temporary stack.
    Stack<Card> tempStack = new Stack<>();
    // Move the cards from srcPile into a temporary stack.
    for (int i = 0; i < numCards; i++) {
      Card card = srcStack.pop();
      tempStack.push(card);
    }
    if (tempStack.size() > 1) {
      Set<String> suitSet = new HashSet<>();
      for (Card card : tempStack) {
        suitSet.add(String.valueOf(card.toString().charAt(card.toString().length() - 1)));
      }
      if (suitSet.size() > 1) {
        throw new IllegalStateException("Cards with different suits can't be moved together.");
      }
    }
    // Check if the move is valid based on top cards of tempStack and destStack
    if (!tempStack.isEmpty() && (destStack.isEmpty() || (compareCardNums(tempStack.peek(),
            destStack.peek()) == -1
            && !checkAlternatingSuits(tempStack.peek(), destStack.peek())))) {
      // Move the cards from tempStack to destStack
      while (!tempStack.isEmpty()) {
        Card newCard = tempStack.pop();
        destStack.push(newCard);
      }
    } else {
      // Restore cards back to srcStack
      while (!tempStack.isEmpty()) {
        srcStack.push(tempStack.pop());
      }
      throw new IllegalStateException("Invalid: Cards can't be moved to the destination pile.");
    }
  }


  @Override
  public void moveDraw(int destPile) {
    isGameStarted();

    if (destPile >= getNumPiles() || destPile < 0) {
      throw new IllegalArgumentException("Not a valid destPile, index out of bounds.");
    }

    if (drawPile.isEmpty()) {
      throw new IllegalStateException("Draw pile is empty.");
    }
    Stack<Card> destStack = cascadePiles.get(destPile);
    Card viewDrawCard = drawPile.get(0);
    if (destStack.isEmpty()) {
      Card drawCard = drawPile.remove(0);
      cascadePiles.get(destPile).push(drawCard);
    } else if (compareCardNums(viewDrawCard, destStack.peek()) == -1
            && !checkAlternatingSuits(viewDrawCard, destStack.peek())) {
      Card drawCard = drawPile.remove(0);
      cascadePiles.get(destPile).push(drawCard);
    } else {
      throw new IllegalStateException("Not a valid draw move.");
    }
  }

  @Override
  protected boolean isValidMoveCascade(Stack<Card> srcPile, Stack<Card> destPile) {
    if (srcPile.isEmpty() || destPile.isEmpty()) {
      return false;
    }
    Card srcCard = srcPile.peek();
    Card destCard = destPile.peek();
    return (compareCardNums(srcCard, destCard) == -1) && !checkAlternatingSuits(srcCard, destCard);
  }

  @Override
  public void moveToFoundation(int srcPile, int foundationPile) {
    isGameStarted();
    if (srcPile >= getNumPiles() || srcPile < 0) {
      throw new IllegalArgumentException("Not a valid srcPile, index out of bounds.");
    }

    if (foundationPile < 0 || foundationPile >= getNumFoundations()) {
      throw new IllegalArgumentException("Invalid foundation pile index.");
    }

    if (cascadePiles.get(srcPile).isEmpty()) {
      throw new IllegalStateException("Empty srcPile.");
    }

    try {
      if (checkSameSuits(cascadePiles.get(srcPile).peek(),
              foundationPiles.get(foundationPile).peek())
              && compareCardNums(cascadePiles.get(srcPile).peek(),
              foundationPiles.get(foundationPile).peek()) == 1) {
        Card card = cascadePiles.get(srcPile).pop();
        foundationPiles.get(foundationPile).push(card);
      } else {
        throw new IllegalStateException("Not a valid move to foundation");
      }
    } catch (EmptyStackException e) {
      if (cascadePiles.get(srcPile).peek().toString().contains("A")) {
        foundationPiles.get(foundationPile).push(cascadePiles.get(srcPile).pop());
      } else {
        throw new IllegalStateException();
      }
    }
  }

}
