package cs3500.klondike.model.hw04;

import java.util.ArrayList;
import java.util.Collections;
import java.util.EmptyStackException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.Stack;

import cs3500.klondike.model.hw02.Card;
import cs3500.klondike.model.hw02.KlondikeCard;
import cs3500.klondike.model.hw02.KlondikeModel;

/**
 * Abstract class for Klondike models. Manages the game logic and provides common methods for
 * Klondike implementations.
 */
public abstract class AbstractKlondike implements KlondikeModel {

  protected List<Card> deck;
  protected final HashMap<Integer, Stack<Card>> foundationPiles;
  protected final HashMap<Integer, Stack<Card>> cascadePiles;
  protected List<Card> drawPile;

  protected int numPiles;
  protected int numDraw;
  protected boolean isGameStarted;

  /**
   * Sets the deck, cascade piles, foundation piles, and game state.
   */
  public AbstractKlondike() {
    this.deck = getDeck();
    this.cascadePiles = new HashMap<>();
    this.foundationPiles = new HashMap<>();
    this.isGameStarted = false;
  }

  @Override
  public List<Card> getDeck() {
    List<Card> deck = new ArrayList<>();
    for (int i = 1; i <= 13; i++) {
      for (KlondikeCard.Suits suit : KlondikeCard.Suits.values()) {
        deck.add(new KlondikeCard(suit, i));
      }
    }
    return deck;
  }

  protected boolean isValidDeck(List<Card> deck) {
    if (deck.isEmpty()) {
      return false;
    }

    int numAces = getAces(deck);
    if (numAces <= 0) {
      return false;
    }
    if (deck.size() % numAces != 0) {
      return false;
    }
    int numCardInRun = deck.size() / numAces;

    Set<String> set = new HashSet();

    for (Card card: deck) {
      set.add(card.toString());
    }

    int clubs = 0;
    int spades = 0;
    int hearts = 0;
    int diamonds = 0;

    for (String s: set) {
      if (s.contains("♣")) {
        clubs++;
      } else if (s.contains("♠")) {
        spades++;
      } else if (s.contains("♡")) {
        hearts++;
      } else {
        diamonds++;
      }
    }

    return equalSizeRunsCheck(clubs, numCardInRun, spades, hearts, diamonds);
  }

  protected boolean equalSizeRunsCheck(int clubs, int numCardInRun, int spades, int hearts,
                                       int diamonds) {
    if (clubs != 0 && clubs != numCardInRun) {
      return false;
    }
    if (spades != 0 && spades != numCardInRun) {
      return false;
    }
    if (hearts != 0 && hearts != numCardInRun) {
      return false;
    }
    return diamonds == 0 || diamonds == numCardInRun;
  }

  @Override
  public void startGame(List<Card> deck, boolean shuffle, int numPiles, int numDraw) {
    //If start game is called after game was already started.
    if (isGameStarted) {
      throw new IllegalStateException("Game was already started.");
    }

    if (deck == null) {
      throw new IllegalArgumentException();
    }

    List<Card> cloneDeck = new ArrayList<>(deck);
    this.deck = cloneDeck;

    if (!isValidDeck(deck)) {
      throw new IllegalArgumentException("Full Deck: " + deck);
    }

    if ((Math.pow(numPiles, 2) + numPiles) / 2 > cloneDeck.size()) {
      throw new IllegalArgumentException("Not enough cards for numPiles.");
    }

    if (numDraw <= 0) {
      throw new IllegalArgumentException("Can't play without numDraw being greater than 0");
    }

    if (numPiles <= 0) {
      throw new IllegalArgumentException("Need at least one cascade pile.");
    }

    //If shuffle is true shuffle deck
    if (shuffle) {
      Collections.shuffle(cloneDeck);
    }

    //Put stacks in foundation piles.
    for (int i = 0; i < getAces(cloneDeck); i++) {
      foundationPiles.put(i, new Stack<>());
    }

    //Put stacks in cascade piles.
    for (int i = 0; i < numPiles; i++) {
      cascadePiles.put(i, new Stack<>());
    }

    //Initialize numPiles and numDraw.
    this.numPiles = numPiles;
    this.numDraw = numDraw;

    //initialize the draw pile.
    drawPile = new ArrayList<>();

    //dealing cards to the cascade piles and draw pile
    dealToCascadeAndDrawPiles();

    isGameStarted = true;
  }

  protected void dealToCascadeAndDrawPiles() {
    int current;
    int firstIndex = 0;
    Iterator<Card> dickerator = deck.iterator();
    while (firstIndex < numPiles) {
      for (current = firstIndex; current < numPiles; current++) {
        Stack<Card> cascadePile = cascadePiles.get(current);
        cascadePile.push(dickerator.next());
      }
      firstIndex++;
    }
    while (dickerator.hasNext()) {
      drawPile.add(dickerator.next());
    }
    //make top card visible.
    for (int pileIndex = 0; pileIndex < numPiles; pileIndex++) {
      cascadePiles.get(pileIndex).peek().switchVisibility();
    }
    if (!drawPile.isEmpty()) {
      for (int i = 0; i < numDraw; i++) {
        drawPile.get(i).switchVisibility();
      }
    }

  }

  protected void isGameStarted() {
    if (!isGameStarted) {
      throw new IllegalStateException("Game is not started yet.");
    }
  }

  protected void validatePileIndices(int srcPile, int destPile) {
    if (destPile >= getNumPiles() || destPile < 0) {
      throw new IllegalArgumentException("Not a valid destPile, index out of bounds.");
    }

    if (srcPile >= getNumPiles() || srcPile < 0) {
      throw new IllegalArgumentException("Not a valid srcPile, index out of bounds.");
    }

    if (srcPile == destPile) {
      throw new IllegalArgumentException("Already in that pile.");
    }
  }

  protected void validateNumCardsToMove(int numCards) {
    if (numCards <= 0) {
      throw new IllegalArgumentException("Can't move nothing to a pile.");
    }
  }

  private int getAces(List<Card> deck) {
    int aceCount = 0;
    for (int i = 0; i < deck.size(); i++) {
      if (deck.get(i).toString().contains("A")) {
        aceCount++;
      }
    }
    return aceCount;
  }

  protected int compareCardNums(Card card1, Card card2) {
    int thisCardNum = getCardNum(card1);
    int otherCardNum = getCardNum(card2);
    int diff = thisCardNum - otherCardNum;
    if (diff == 1 || diff == -1) {
      return diff;
    } else {
      return 0;
    }
  }

  private int getCardNum(Card card) {
    String cardNumStr = card.toString().substring(0, card.toString().length() - 1);
    switch (cardNumStr) {
      case "A":
        return 1;
      case "K":
        return 13;
      case "Q":
        return 12;
      case "J":
        return 11;
      default:
        return Integer.parseInt(cardNumStr);
    }
  }

  protected boolean checkSameSuits(Card thisCard, Card otherCard) {
    char thisSuit = thisCard.toString().charAt(thisCard.toString().length() - 1);
    char otherSuit = otherCard.toString().charAt(otherCard.toString().length() - 1);

    return thisSuit == otherSuit;
  }

  protected boolean checkAlternatingSuits(Card thisCard, Card otherCard) {
    char thisSuit = thisCard.toString().charAt(thisCard.toString().length() - 1);
    char otherSuit = otherCard.toString().charAt(otherCard.toString().length() - 1);

    List<Character> reds = new ArrayList<>(List.of('♢', '♡'));
    List<Character> blacks = new ArrayList<>(List.of('♠', '♣'));

    return (reds.contains(thisSuit) && blacks.contains(otherSuit))
            || (blacks.contains(thisSuit) && reds.contains(otherSuit));
  }

  @Override
  public void movePile(int srcPile, int numCards, int destPile) {
    isGameStarted();
    validatePileIndices(srcPile, destPile);
    validateNumCardsToMove(numCards);
    Stack<Card> srcStack = cascadePiles.get(srcPile);
    if (numCards > cascadePiles.get(srcPile).size()) {
      throw new IllegalArgumentException("Not enough cards.");
    } else {
      moveCards(numCards, destPile, srcStack);
    }
  }

  private void moveCards(int numCards, int destPile, Stack<Card> srcStack) {
    // Initialize empty temporary stack.
    Stack<Card> tempStack = new Stack<>();
    Stack<Card> destStack = cascadePiles.get(destPile);
    // Move the cards from srcPile into a temporary stack.
    for (int i = 0; i < numCards; i++) {
      Card card;
      try {
        card = srcStack.pop();
      } catch (NullPointerException e) {
        continue;
      }
      tempStack.push(card);
    }
    // Check the bottom of the stack of cards for a king card
    if (destStack.isEmpty() && !tempStack.isEmpty() && !tempStack.peek()
            .toString().contains("K")) {
      throw new IllegalStateException("The destination stack must start with a King.");
    }

    // Move cards from the temporary stack to destPile.
    for (int j = 0; j < tempStack.size(); j++) {
      try {
        if (!destStack.isEmpty()) {
          if ((compareCardNums(tempStack.peek(), destStack.peek()) == -1)
                  && checkAlternatingSuits(tempStack.peek(), destStack.peek())) {
            Card newCard = tempStack.pop();
            destStack.push(newCard);
          } else {
            throw new IllegalStateException();
          }
        } else {
          Card newCard = tempStack.pop();
          destStack.push(newCard);
        }
      } catch (NullPointerException e) {
        continue;
      }
    }
    // If there is another card left in the stack, make the top one visible.
    if (!srcStack.isEmpty()) {
      if (!srcStack.peek().isVisible()) {
        srcStack.peek().switchVisibility();
      }
    }
    if (!destStack.isEmpty() && !destStack.peek().isVisible()) {
      destStack.peek().switchVisibility();
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
    if (destStack.isEmpty() && !viewDrawCard.toString().contains("K")) {
      throw new IllegalStateException("New Destination pile must start with a king.");
    }
    if (!destStack.isEmpty()) {
      if (compareCardNums(drawPile.get(0), destStack.peek()) == -1
              && checkAlternatingSuits(drawPile.get(0), destStack.peek())) {
        Card drawCard = drawPile.remove(0);
        cascadePiles.get(destPile).push(drawCard);
      } else {
        throw new IllegalStateException("Not a valid draw move.");
      }
    } else {
      Card drawCard = drawPile.remove(0);
      cascadePiles.get(destPile).push(drawCard);
    }

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
        //Set the last card behind the one that was moved visible.
        if (!cascadePiles.get(srcPile).isEmpty()) {
          cascadePiles.get(srcPile).peek().switchVisibility();
        }
      } else {
        throw new IllegalStateException("Not a valid move to foundation");
      }
    } catch (EmptyStackException e) {
      if (cascadePiles.get(srcPile).peek().toString().contains("A")) {
        foundationPiles.get(foundationPile).push(cascadePiles.get(srcPile).pop());
        // Make the last card behind the one that was moved visible.
        if (!cascadePiles.get(srcPile).isEmpty()) {
          if (!cascadePiles.get(srcPile).peek().isVisible()) {
            cascadePiles.get(srcPile).peek().switchVisibility();
          }
        }
      } else {
        throw new IllegalStateException();
      }
    }
  }

  @Override
  public void moveDrawToFoundation(int foundationPile) {
    isGameStarted();

    if (drawPile.isEmpty()) {
      throw new IllegalStateException("Draw pile is empty.");
    }

    if (foundationPile < 0 || foundationPile >= foundationPiles.size()) {
      throw new IllegalArgumentException("Invalid foundation pile index.");
    }

    Card drawCard = drawPile.get(0);

    // Check if the foundation pile is empty
    if (foundationPiles.get(foundationPile).isEmpty()) {
      // Check if the drawn card is an Ace
      if (drawCard.toString().contains("A")) {
        drawCard.switchVisibility();
        foundationPiles.get(foundationPile).push(drawCard);
        drawPile.remove(0);
      } else {
        throw new IllegalStateException("The foundation pile must start with an Ace.");
      }
    } else {
      Card topFoundationCard = foundationPiles.get(foundationPile).peek();

      // Check if the drawn card can be placed on top of the foundation pile
      if (compareCardNums(drawCard, topFoundationCard) == 1
              && checkSameSuits(drawCard, topFoundationCard)) {
        drawCard.switchVisibility();
        foundationPiles.get(foundationPile).push(drawCard);
        drawPile.remove(0);
      } else {
        throw new IllegalStateException("Not a valid draw move to foundation.");
      }
    }
  }


  @Override
  public void discardDraw() {
    if (!isGameStarted) {
      throw new IllegalStateException("Game hasn't begun yet.");
    }
    if (drawPile.isEmpty()) {
      throw new IllegalStateException("Draw pile is empty. No cards to discard.");
    }
    Card cardToDiscard = drawPile.remove(0);
    cardToDiscard.switchVisibility();
    drawPile.add(cardToDiscard);
    drawPile.get(0).switchVisibility();
  }

  @Override
  public int getNumRows() {
    if (!isGameStarted) {
      throw new IllegalStateException("Game hasn't begun yet.");
    }

    int rows = 0;
    for (Stack<Card> cascadePile : cascadePiles.values()) {
      int pileHeight = cascadePile.size();
      if (pileHeight > rows) {
        rows = pileHeight;
      }
    }
    return rows;
  }

  @Override
  public int getNumPiles() {
    if (!isGameStarted) {
      throw new IllegalStateException("Game hasn't begun yet.");
    }

    return cascadePiles.size();
  }

  @Override
  public int getNumDraw() {
    if (!isGameStarted) {
      throw new IllegalStateException("Game hasn't begun yet.");
    }
    return numDraw;
  }

  protected boolean isValidMoveCascade(Stack<Card> srcPile, Stack<Card> destPile) {
    if (srcPile.isEmpty() || destPile.isEmpty()) {
      return false;
    }
    Card srcCard = srcPile.peek();
    Card destCard = destPile.peek();
    return (compareCardNums(srcCard, destCard) == -1) && checkAlternatingSuits(srcCard, destCard);
  }

  protected boolean isValidMoveFoundation(Stack<Card> srcPile, Stack<Card> destPile) {
    if (srcPile.isEmpty() || destPile.isEmpty()) {
      return false;
    }
    Card srcCard = srcPile.peek();
    Card destCard = destPile.peek();
    return (compareCardNums(srcCard, destCard) == 1) && checkSameSuits(srcCard, destCard);
  }

  @Override
  public boolean isGameOver() throws IllegalStateException {
    if (!isGameStarted) {
      throw new IllegalStateException("Game can't be over if it never started.");
    }
    if (!drawPile.isEmpty()) {
      return false;
    }
    for (int srcPile = 0; srcPile < getNumPiles(); srcPile++) {
      for (int destPile = 0; destPile < getNumPiles(); destPile++) {
        if (srcPile != destPile && isValidMoveCascade(cascadePiles.get(srcPile),
                cascadePiles.get(destPile))) {
          return false;
        }
      }
      for (int foundationPile = 0; foundationPile < getNumFoundations(); foundationPile++) {
        if (isValidMoveFoundation(cascadePiles.get(srcPile), foundationPiles.get(foundationPile))) {
          return false;
        }
      }
    }
    return true;
  }

  @Override
  public int getScore() {
    if (!isGameStarted) {
      throw new IllegalStateException("Game hasn't begun yet.");
    }
    int score = 0;
    for (int i = 0; i < foundationPiles.size(); i++) {
      score += foundationPiles.get(i).size();
    }
    return score;
  }

  @Override
  public int getPileHeight(int pileNum) {
    if (!isGameStarted) {
      throw new IllegalStateException("Game hasn't begun yet.");
    }
    if (pileNum < 0 || pileNum >= cascadePiles.size()) {
      throw new IllegalArgumentException("Not a valid pileNum.");
    }
    return cascadePiles.get(pileNum).size();
  }

  @Override
  public boolean isCardVisible(int pileNum, int card) {
    if (!isGameStarted) {
      throw new IllegalStateException("Game hasn't begun yet.");
    }

    if (pileNum < 0 || pileNum >= getNumPiles()) {
      throw new IllegalArgumentException("Invalid pileNum.");
    }

    Stack<Card> pile = cascadePiles.get(pileNum);

    if (card < 0 || card >= pile.size()) {
      throw new IllegalArgumentException("Invalid card index.");
    }

    Card selectedCard = pile.get(card);
    return selectedCard.isVisible();
  }

  @Override
  public Card getCardAt(int pileNum, int card) {

    if (!isGameStarted) {
      throw new IllegalStateException("Game hasn't begun yet.");
    }
    if (pileNum < 0 || pileNum >= getNumPiles()) {
      throw new IllegalArgumentException("Invalid pile number.");
    }
    Stack<Card> pile = cascadePiles.get(pileNum);
    StringBuilder pileNumAndCards = new StringBuilder();
    for (int i = 0; i < cascadePiles.size(); i++) {
      pileNumAndCards.append("Pile " + i + " Card in Pile: " + cascadePiles.get(i) + "\n");
    }
    if (card < 0 || card >= getPileHeight(pileNum)) {
      throw new IllegalArgumentException("Invalid card number.\nCard: " + card + " \nPileNum: "
              + pileNum + pileNumAndCards.toString());
    }

    if (!pile.get(card).isVisible()) {
      throw new IllegalArgumentException("Card is not flipped over.");
    }

    if (pile.size() <= card) {
      throw new IllegalArgumentException("Invalid card number.");
    }

    Card selectedCard = pile.get(card);
    if (!selectedCard.isVisible()) {
      throw new IllegalArgumentException("Card is not flipped over.");
    }
    return selectedCard;
  }

  @Override
  public Card getCardAt(int foundationPile) {
    if (!isGameStarted) {
      throw new IllegalStateException("Game hasn't begun yet.");
    }
    if (foundationPile < 0 || foundationPile >= getNumFoundations()) {
      throw new IllegalArgumentException("Invalid foundation pile number.");
    }
    Stack<Card> pile = foundationPiles.get(foundationPile);
    if (!pile.isEmpty()) {
      return pile.peek();
    }
    return null;
  }


  @Override
  public List<Card> getDrawCards() throws IllegalStateException {
    if (!isGameStarted) {
      throw new IllegalStateException("Game hasn't begun yet.");
    }
    // Create a copy of the draw pile
    List<Card> drawCardsCopy = new ArrayList<>(drawPile.subList(0,
            Math.min(numDraw, drawPile.size())));
    for (Card card : drawCardsCopy) {
      card.switchVisibility();
    }
    return drawCardsCopy;
  }

  @Override
  public int getNumFoundations() throws IllegalStateException {
    if (!isGameStarted) {
      throw new IllegalStateException("Game hasn't begun yet.");
    }
    return getAces(deck);
  }

}
