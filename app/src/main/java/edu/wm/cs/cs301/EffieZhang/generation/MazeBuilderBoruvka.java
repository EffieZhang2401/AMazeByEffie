package edu.wm.cs.cs301.EffieZhang.generation;

import java.util.Random;
import java.util.ArrayList;

/**
 * Generate a maze by using the Boruvka's algorithm
 * @author effiezhang
 */
public class MazeBuilderBoruvka extends MazeBuilder implements Runnable {
	private int[] randomNum;
	private int count = 0;
	private int[][] weight;
	private int[][][] boruvka;
		
	public MazeBuilderBoruvka() {
		super();
		System.out.println("MazeBuilderBoruvka uses Boruvka's algorithm to generate maze.");
	}
	
	/**
	 * generate an array with unique random number inside, and pass the random number to the weight array
	 */
	private void setRandomNumber() {
		 randomNum = new int[width*(height*2-1)];

	        // initialize each value at index i to the value i 
	        for (int i = 0; i < randomNum.length; i++)
	        {
	            randomNum[i] = i+1;
	        }

	        Random randomGenerator = new Random();
	        int randomIndex; // the randomly selected index each time through the loop
	        int randomValue; // the value at nums[randomIndex] each time through the loop

	        // randomize order of values
	        for(int i = 0; i < randomNum.length; i++)
	        {
	             // select a random index
	             randomIndex = randomGenerator.nextInt(randomNum.length);

	             // swap values
	             randomValue = randomNum[randomIndex];
	             randomNum[randomIndex] = randomNum[i];
	             randomNum[i] = randomValue;
	        }
	        for(int i = 0; i< weight.length; i++) {
	        	for(int j = 0; j<weight[i].length;j++) {
	        		weight[i][j] = randomNum[count];
	        		//System.out.println(i+" "+j+" "+weight[i][j]);
	        		count++;
	        	}
	        }
	}
	
	/**
	 * get each edgeWeight by visiting the weight 2D array, if it is a part of border, return 0
	 * @param Wallboard w
	 * @return int Edge's Weight11
	 */
	public int getEdgeWeight(Wallboard w) {
	//visit the edgeWeight by using getX, getY, getDirection
	//if there is a int number, then return
	//if there is a null, set a random number, call checkRepeat until the random number is unique
		int edgeWeight = 0;
		if(floorplan.isPartOfBorder(w)!=true) {
			int x=w.getX();
			int y = w.getY();
			if(w.getDirection()==CardinalDirection.West) {
				edgeWeight = weight[x][y*2];
			}
			if(w.getDirection()==CardinalDirection.South) {
				edgeWeight = weight[x][y*2+1];
			}
			if(w.getDirection()==CardinalDirection.East) {
				edgeWeight = weight[x+1][y*2];
			}
			if(w.getDirection()==CardinalDirection.North) {
				edgeWeight = weight[x][y*2-1];
			}	
		}
		return edgeWeight;
	}
			
	
	/**
	 * This method generates pathways into the maze by using Boruvka's algorithm to generate a spanning tree for an undirected graph.
	 * The cells are the nodes of the graph and the spanning tree. An edge represents that one can move from one cell to an adjacent cell.
	 * So an edge implies that its nodes are adjacent cells in the maze and that there is no wallboard separating these cells in the maze. 
	 */
	@Override
	protected void generatePathways() {
		weight = new int[width][height*2-1];
		setRandomNumber();
		
		int num = 0;
		boruvka = new int[width][height][2];
		for(int i = 0; i<boruvka.length;i++) {
			for(int j = 0; j<boruvka[i].length;j++) {
				boruvka[i][j][0]=num;
				boruvka[i][j][1]=-1;
				num ++;
			}
		}
		final ArrayList<Wallboard> candidates = new ArrayList<Wallboard>();
		createListOfWallboards(candidates);
		boolean complete = false;
		while(complete == false) {
		for(int i = 0; i<candidates.size();i++) {
			//pick the candidate edge
				int x = candidates.get(i).getX();
				int y = candidates.get(i).getY();
				int set = boruvka[x][y][0];
				//System.out.println(set);
				int nextX = candidates.get(i).getNeighborX();
				int nextY = candidates.get(i).getNeighborY();
				//System.out.println(nextX+" "+nextY);
				int anoSet = boruvka[nextX][nextY][0];
				//System.out.println(anoSet);
				// decide what to do		
				if (set != anoSet) {
					// uv is in different co
					int uv = i;
					int wx = boruvka[x][y][1];
					if(isPerfer(uv, wx, candidates)==true) {
					setCheapestEdge(set, uv);
					}
					int yz = boruvka[nextX][nextY][1];
					if(isPerfer(uv, yz, candidates)==true) {
						setCheapestEdge(anoSet, uv);
					}
				}
			}
		if(checkNoneEdge() == true){
			complete = true;
		}
		else {
			complete = false;
			breakWall(candidates);
			
		}
		}
	}
		
		
	private boolean isPerfer(int a, int b, ArrayList<Wallboard> candidates) {
		if( b == -1 || getEdgeWeight(candidates.get(a)) < getEdgeWeight(candidates.get(b))) {
			return true;
		}
		else {
			return false;
		}
	}
	
	/**
	 * Pass an empty ArrayList<Wallboard>, then add Wallboard to the list
	 * @param ArrayList<Wallboard> walls
	 */
	private void createListOfWallboards(ArrayList<Wallboard> walls) {
		for (int i = 0; i < width; i++){
			for (int j = 0; j < height; j++){
					Wallboard wall = new Wallboard(i, j, CardinalDirection.East);
					if (floorplan.canTearDown(wall)==true){        // If the wall is not a border, it is added to the list
						walls.add(wall);
						//System.out.println(wall.getX()+" "+ wall.getY());
						//System.out.println(wall.getDirection());
					}
					Wallboard wall2 = new Wallboard(i, j, CardinalDirection.South);
					if (floorplan.canTearDown(wall2)==true){
						walls.add(wall2);
					//System.out.println(wall2.getX()+" "+ wall2.getY());
					//System.out.println(wall2.getDirection());}
					}
			}
		}
	}
	
	/**
	 * set the cheapest edge to the corresponding component, a helper method for generate pathway
	 * @param int set: the index of the component set
	 * @param int indexOfEdge: the index of the edge
	 */
	private void setCheapestEdge(int set, int indexOfEdge) {
		for(int i = 0; i < boruvka.length;i++) {
			for(int j = 0; j<boruvka[i].length;j++) {
				if(boruvka[i][j][0]==set) {
					boruvka[i][j][1] = indexOfEdge;
				}
			}
		}
	}
	
	/**
	 * 
	 * @return true if there is no cheapest edge for each component
	 */
	private boolean checkNoneEdge() {
		for(int i = 0; i<boruvka.length;i++) {
			for(int j = 0; j<boruvka[i].length; j++) {
				if (boruvka[i][j][1]!=-1) {
				return false;
				}
			}
		}
		return true;
	}
	
	/**
	 * break the wall if needed
	 * @param candidates: ArrayList<Wallboard>
	 */
	private void breakWall(ArrayList<Wallboard> candidates) {
		for(int i = 0; i<boruvka.length;i++) {
			for(int j = 0; j<boruvka[i].length;j++) {
				if (boruvka[i][j][1]!=-1) {
					int index = boruvka[i][j][1];
					int set = boruvka[i][j][0];
					Wallboard wall = candidates.get(index);
					int nextx = wall.getNeighborX();
					int nexty = wall.getNeighborY();
					int anoset = boruvka[nextx][nexty][0];
					if(floorplan.canTearDown(candidates.get(index))) {
						floorplan.deleteWallboard(candidates.get(index));
						//merge the set
						merge(set,anoset);
					}
				}
			}
		}
	}
	
	/**
	 * A helper method for breakWall, merge two component if needed
	 * @param set
	 * @param anoset
	 */
	private void merge(int set, int anoset) {
		for(int i = 0; i<boruvka.length;i++) {
			for(int j =0; j<boruvka[i].length;j++) {
				if (boruvka[i][j][0]==anoset || boruvka[i][j][0]==set) {
					boruvka[i][j][0]=set;
					boruvka[i][j][1]=-1;
				}
			}
		}
	}
}
