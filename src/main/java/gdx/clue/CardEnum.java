package gdx.clue;

public class CardEnum {
    public static final int NUM_SUSPECTS = 6;
    public static final int NUM_ROOMS = 9;
    public static final int NUM_WEAPONS = 9;
    public static final int TOTAL = NUM_ROOMS + NUM_SUSPECTS + NUM_WEAPONS;

    public static final int TYPE_SUSPECT = 0;
    public static final int TYPE_WEAPON = 1;
    public static final int TYPE_ROOM = 2;

	public static enum Card {
	    SCARLET(CardType.SUSPECT, 0, "Kasandra Scarlet"),
	    WHITE(CardType.SUSPECT, 1, "Diane White"),
	    PLUM(CardType.SUSPECT, 2, "Victor Plum"),
	    MUSTARD(CardType.SUSPECT, 3, "Jack Mustard"),
	    GREEN(CardType.SUSPECT, 4, "Jacob Green"),
	    PEACOCK(CardType.SUSPECT, 5, "Eleanor Peacock"),
	    HALL(CardType.ROOM, 0, "Hall", false),
	    LOUNGE(CardType.ROOM, 1, "Lounge", true, 5),
	    DINING(CardType.ROOM, 2, "Dining Room", false),
	    KITCHEN(CardType.ROOM, 3, "Kitchen", true, 7),
	    BALLROOM(CardType.ROOM, 4, "Ballroom", false),
	    CONSERVATORY(CardType.ROOM, 5, "Conservatory", true, 1),
	    BILLIARD(CardType.ROOM, 6, "Billiard Room", false),
	    STUDY(CardType.ROOM, 7, "Study", true, 3),
	    LIBRARY(CardType.ROOM, 8, "Library", false),
	    AXE(CardType.WEAPON, 0, "Axe"),
	    BAT(CardType.WEAPON, 1, "Bat"),
	    CANDLESTICK(CardType.WEAPON, 2, "Candlestick"),
	    DUMBBELL(CardType.WEAPON, 3, "Dumbbell"),
	    KNIFE(CardType.WEAPON, 4, "Knife"),
	    PISTOL(CardType.WEAPON, 5, "Pistol"),
	    POISON(CardType.WEAPON, 6, "Poison"),
	    ROPE(CardType.WEAPON, 7, "Rope"),
	    TROPHY(CardType.WEAPON, 8, "Trophy");
	    
	    private CardType type;
	    private int id;
	    private String title;
	    private boolean secretPassage;
	    private int passageId;
	    
	    Card(CardType type, int id, String title) {
	    	this.type = type;
	        this.id = id;
	        this.title = title;
	    }	    
	    
	    Card(CardType type, int id, String title, boolean secretPassage, int passageId) {
	    	this.type = type;
	        this.id = id;
	        this.title = title;
	        this.secretPassage = secretPassage;
	        this.passageId = passageId;
	    }
	    
	    Card(CardType type, int id, String title, boolean secretPassage) {
	    	this.type = type;
	        this.id = id;
	        this.title = title;
	        this.secretPassage = secretPassage;
	    }

	    public CardType type() {
	        return this.type;
	    }
	    
	    public int id() {
	        return this.id;
	    }

	    public String title() {
	        return this.title;
	    }
	    
	    public boolean hasSecretPassage() {
	    	return this.secretPassage;
	    }
	    
	    public int passageId() {
	    	return this.passageId;
	    }
	    
	    @Override
	    public String toString() {
	    	return this.title();
	    }
	}
	
    public static enum CardType {
        ROOM(0, "Room"),
        SUSPECT(1, "Suspect"),
        WEAPON(2, "Weapon");
        
        private int id;
        private String title;

        CardType(int id, String title) {
        	this.id = id;
            this.title = title;
        }

        public int id() {
            return this.id;
        }

        public String title() {
            return this.title;
        }
    }
}