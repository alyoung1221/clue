package gdx.clue;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.actions.SequenceAction;
import com.badlogic.gdx.scenes.scene2d.ui.CheckBox;
import com.badlogic.gdx.scenes.scene2d.ui.Label;

import java.util.List;

import static gdx.clue.CardEnum.*;
import gdx.clue.astar.Location;
import java.util.Random;

public class ShowCardsRoutine {
    List<Card> suggestions;
    String suggestion_text;
    Player suggesting_player;
    
    int index = -1;

    GameScreen screen;

    public ShowCardsRoutine(GameScreen screen) {
        this.screen = screen;
    }

    public void setSuggestion(List<Card> suggestions, Player suggesting_player) {
        this.suggestions = suggestions;
        this.suggesting_player = suggesting_player;

        Card room = null, suspect = null, weapon = null;

        for (Card card: suggestions) {
            if (card.type() == CardType.SUSPECT) {
                suspect = card;
            }
            
            if (card.type() == CardType.WEAPON) {
                weapon = card;
            }
            
            if (card.type() == CardType.ROOM) {
                room = card;
            }
        }

        // call the suspect over to the room and set their current location
        for (Player player: screen.getGame().getPlayers()) {
            if (player.getSuspect().id() == suspect.id()) {
                screen.getPlayerIconPlacement().removePlayerIcon(player.getSuspect().id());
                Location room_location = suggesting_player.getLocation();
                screen.getPlayerIconPlacement().addPlayerIcon(room_location.getRoomId(), player.getSuspect().id());
                player.setLocation(room_location);
                screen.addMessage(player.getSuspect().title() + " has been called over to the " + room + " by " + suggesting_player.getSuspect().title(), player.getPlayerColor());
            }
        }

        suggestion_text = String.format(ClueMain.formatter, suggesting_player.getSuspect().title(), suspect, weapon, room);
        screen.addMessage(suggestion_text, suggesting_player.getPlayerColor());
    }

    /**
     * Proving and Disproving Suggestions: Once you make a suggestion, your
     * opponents attempt to prove the suggestion false, beginning with the
     * player to your right. That player looks at their cards for one of the
     * three cards that you just named, and if they have at least one of them,
     * they must show you (and only you) the matching card of their choice. If
     * the player on your left is unable to disprove your suggestion, the next
     * player must attempt to do so. Once a player shows you a card that matches
     * one in your suggestion, cross that card off of your detective notepad.
     *
     * Function is called iteratively.
     */
    public void showCards() {
        if (this.index == -1) {
            // get the next player to the right and ask to show a card
            int suggestingPlayerIndex = screen.getGame().getPlayers().indexOf(suggesting_player);

            System.out.printf("START SHOW CARDS suggesting_player: %s, suggestingPlayerIndex: %d\n", suggesting_player.getSuspect(), suggestingPlayerIndex);

            this.index = suggestingPlayerIndex + 1;

            if (this.index == screen.getGame().getPlayers().size()) {
                this.index = 0;
            }
        }

        Player your_player = screen.getYourPlayer();
        Player next_player = screen.getGame().getPlayers().get(this.index);

        System.out.printf("SHOW CARDS suggesting_player: %s, next: %s, index: %d\n", suggesting_player.getSuspect(), next_player.getSuspect(), this.index);

        // TODO: fix logic for no match
        Boolean no_match = (next_player == suggesting_player) || screen.getUndealt()
        	.stream()
    	    .anyMatch(c -> !suggestions.contains(c));
                
        System.out.println("Match: " + no_match);
        
        if (no_match) {
            ClueMain.END_BUTTON.setVisible(true);
            this.index = -1;
            Sounds.play(Sound.LAUGH);
            
            //if no one was able to show any cards after full round 
            //then it is possible to make the accusation with this suggestion

            if (next_player.isComputerPlayer()) {
                screen.makeAccusation(next_player, suggestions);
            } 
            else {
                ClueMain.ACCUSE_BUTTON.setVisible(true);
                screen.addMessage("You may make an accusation!", Color.PINK);
            }
            
            return;//done
        }
        else {
            ClueMain.ACCUSE_BUTTON.setVisible(false);
        }

        // TODO: validate logic for checking if player has suggested card
        if (suggesting_player != your_player && next_player == your_player) {
            if (!your_player.isHoldingCardInSuggestion(suggestions)) {
                String text = "You are not holding any of the cards suggested by " + suggesting_player.getSuspect().title();
                screen.addMessage(text, next_player.getPlayerColor());
            } 
            else {
                PickCardToShowDialog dialog = new PickCardToShowDialog(screen, this, your_player, suggesting_player, suggestions, suggestion_text);
                dialog.show(screen.getStage());
                return;//done
            }
        }
        else {
            List<Card> cards_in_hand = next_player.getHand();
            List<Card> cards_in_hand_matching_one_of_three_suggested_cards = cards_in_hand
            	.stream()
            	.filter((c) -> suggestions.contains(c))
            	.toList();

            if (cards_in_hand_matching_one_of_three_suggested_cards.size() > 0) {
                int picked = new Random().nextInt(cards_in_hand_matching_one_of_three_suggested_cards.size());
                Card card_to_show = cards_in_hand_matching_one_of_three_suggested_cards.get(picked);

                String text = next_player.getSuspect().title() + " is showing the \"" + card_to_show + "\" card to you.";
                
                if (suggesting_player != your_player) {
                    text = next_player.getSuspect().title() + " is showing a card to " + suggesting_player.getSuspect().title() + ".";
                }

                Color color = next_player.getPlayerColor();
                
                screen.addMessage(text, color);
                Sounds.play(Sound.POSITIVE_EFFECT);

                Label label = screen.getNotebookPanel().getLabelByCard(card_to_show);
                CheckBox checkbox = screen.getNotebookPanel().getCheckBoxByPlayerAndCard(next_player, card_to_show);
                
                this.index = -1;

                if (suggesting_player.isComputerPlayer()) {
                    suggesting_player.getNotebook().setToggled(card_to_show);

                    List<Card> your_cards_in_hand_matching = your_player.getHand()
                        .stream()
                        .filter((c) -> suggestions.contains(c))
                        .toList();

                    System.out.println("Matching: ");
                    System.out.print(your_cards_in_hand_matching);
                    
                    if (your_cards_in_hand_matching.size() == 2) {
                    	your_player.getNotebook().setToggled(card_to_show);
                    }
                    else {
                    	checkbox = null;
                    }

                    ClueMain.END_BUTTON.toggle();
                }
                else {
                    if (checkbox != null && !checkbox.isChecked()) {          
                        checkbox.setDisabled(false);
                        checkbox.setVisible(true);
                        checkbox.toggle();

                        label.setColor(color);
                    }
                    
                    ClueMain.END_BUTTON.setVisible(true);
                }

                return;//done
            } 
            else {
            	List<CheckBox> disabled = screen.getNotebookPanel().getCheckBoxesByPlayerAndCards(next_player, suggestions);

            	for (CheckBox cb: disabled) {
            		cb.setDisabled(true);
            		cb.setVisible(false);
            	}
            	
                screen.addMessage(next_player.getSuspect().title() + " does not have a card to show.", next_player.getPlayerColor());
            }
        }

        index++;
        
        if (index == screen.getGame().getPlayers().size()) {
            index = 0;
        }

        SequenceAction seq = Actions.action(SequenceAction.class);
        seq.addAction(Actions.delay(1f));
        seq.addAction(Actions.run(new Runnable() {
            public void run() {
                showCards();
            }
        }));
        
        screen.getStage().addAction(seq);
    }
    
    void reset() {
        this.index = -1;
    }
}