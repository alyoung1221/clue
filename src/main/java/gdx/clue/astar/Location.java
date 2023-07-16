package gdx.clue.astar;

import java.util.ArrayList;
import java.util.List;

public class Location implements Node<Location> {
    private final int x;
    private final int y;
    private int height;
    private boolean blocked;
    private boolean highlight;
    private boolean isRoom;
	private boolean isWithin;
    private int roomId = -1;
    private final List<Location> neighbors;

    public Location(int x, int y) {
        this.x = x;
        this.y = y;
        neighbors = new ArrayList<>();
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    @Override
    public int hashCode() {
        return x * y;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof Location) {
            Location loc = (Location) obj;
            return (loc.getX() == this.x && loc.getY() == this.y);
        } else {
            return false;
        }
    }

    public void setBlocked(boolean blocked) {
        this.blocked = blocked;
    }

    public boolean isBlocked() {
        return blocked;
    }

    public void setHighlighted(boolean highlight) {
        this.highlight = highlight;
    }

    public boolean isHighlighted() {
        return highlight;
    }

    public void setIsRoom(boolean isRoom) {
        this.isRoom = isRoom;
    }

    public boolean isRoom() {
        return isRoom;
    }

	public void setIsWithinRoll(boolean within) {
		// TODO Auto-generated method stub
		this.isWithin = within;
	}
	
    public boolean isWithinRoll() {
        return isWithin;
    }


	public void setRoomId(int id) {
        this.roomId = id;
    }

    public int getRoomId() {
        return roomId;
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public double getDistance(Location dest) {
        double a = dest.x - x;
        double b = dest.y - y;
        return Math.sqrt(a * a + b * b);
    }
    
    @Override
    public double pathCostEstimate(Location goal) {
        return getDistance(goal) * 0.99;
    }
    
    @Override
    public double traverseCost(Location target) {
        double distance = getDistance(target);
        double diff = target.getHeight() - getHeight();
        return Math.abs(diff) + distance;
    }
    
    @Override
    public Iterable<Location> neighbors() {
        List<Location> realNeighbors = new ArrayList<>();
        
        if (!blocked) {
            for (Location loc: neighbors) {
                if (!loc.blocked) {
                    realNeighbors.add(loc);
                }
            }
        }

        return realNeighbors;
    }

    public void addNeighbor(Location l) {
        neighbors.add(l);
    }

    public void removeNeighbor(Location l) {
        neighbors.remove(l);
    }

    /*public boolean isWithin(PathFinder pathFinder, Integer maxX, Integer maxY, List<Location> locations) {
    	if (locations.contains(this)) {
    		return true;
    	}

    	Boolean available = locations.stream().allMatch(l -> this.x > l.getX() && this.y <= l.getY());

    	List<Integer> x = locations.stream().map(Location::getX).toList();
		List<Integer> y = locations.stream().map(Location::getY).toList();
		
    	Integer minX = Collections.min(x);
    	Integer minY = Collections.min(y);

    	maxX = Collections.max(x);
    	maxY = Collections.max(y);
    	
        return available;      
    }
    
    public boolean isWithin(Integer maxX, Integer maxY, List<Location> locations) {
    	if (locations.contains(this)) {
    		return true;
    	}
    	
    	Boolean available = locations.stream().anyMatch(l -> this.x > l.getX() && this.y <= l.getY());

    	List<Integer> x = locations.stream().map(Location::getX).toList();
		List<Integer> y = locations.stream().map(Location::getY).toList();
		
    	Integer minX = Collections.min(x);
    	Integer minY = Collections.min(y);

    	maxX = Collections.max(x);
    	maxY = Collections.max(y);
    	
        return available;      
    }
    
    public boolean isWithin(Location min, Location max) {
    	if (this.equals(min) || this.equals(max)) {
    		return true;
    	}
    	
        return (min.x <= this.x && this.x >= max.x) && (min.y >= this.y && this.y <= max.y);      
    }*/
    
    @Override
    public String toString() {
        return "Location [" + x + "][" + y + "]";
    }
}
