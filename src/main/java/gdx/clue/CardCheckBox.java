package gdx.clue;

import java.util.List;

import com.badlogic.gdx.scenes.scene2d.ui.CheckBox;
import com.badlogic.gdx.scenes.scene2d.ui.Label;

import gdx.clue.CardEnum.Card;

public class CardCheckBox extends CheckBox {
    Card card;
    
    public CardCheckBox(Card card, boolean inHand, boolean toggledInNotebook) {
        super(card.toString(), ClueMain.skin, inHand ? "card-in-hand" : toggledInNotebook ? "toggled-in-notebook" : "default");
        this.card = card;
    }

    public CardCheckBox(Card card, Player player, List<Player> players) {
        super(card.toString(), ClueMain.skin);

        Label label = this.getLabel();
        
        if (player.isCardInHand(card)) {
        	label.setColor(player.getPlayerColor());
        }
        else if (player.getNotebook().isCardToggled(card)) {
        	Player opponent = players
        		.stream()
        		.filter(p -> p.getHand().contains(card))
        		.findFirst()
        		.get();
        	
        	label.setColor(opponent.getPlayerColor());
        }

        this.card = card;
    }
    
    public Card getCard() {
        return this.card;
    }
}