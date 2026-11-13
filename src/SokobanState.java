
import java.io.IOException;
import java.util.*;

class SokobanState implements Comparable<SokobanState> {

    char[][] levelState;
    private int player_row;
    private int player_col;
    private int g;
    private int score;
    int f=0;
    
    List<Pair<Integer,Integer>> goalPos; //list of all goal positions (static)
    List<Pair<Integer,Integer>> boxPos; //list of all box positions (dynamic, changes after a box-push move)
    
    private SokobanState father = null;
    


    public SokobanState(char[][] levelState) {
    // DEEP COPY THE 2D ARRAY
        this.levelState = new char[levelState.length][];
        for (int i = 0; i < levelState.length; i++) {
            this.levelState[i] = levelState[i].clone(); 
        }
    
        
        findPlayerPosition();
        findGoalPositions();
        findBoxPositions();
    }

     public SokobanState(SokobanState parent) {
       
        this.levelState = new char[levelState.length][];
        for (int i = 0; i < levelState.length; i++) {
            this.levelState[i] = parent.getLevelState()[i].clone(); 
        }
        
        
        this.goalPos = parent.goalPos; 
    }

    SokobanState(){
        LevelManager lm=new LevelManager();

        try {
            lm.readLevels("levelExamples.txt");

        } catch (IOException e){
            System.out.println("File not found, using fallback level.");
            this.levelState=getDefaultLevel();

        }

        if (lm.getLevels()!=null&&!lm.getLevels().isEmpty()){
            Random rand=new Random();
            int randIndex=rand.nextInt(lm.getLevels().size());

            this.levelState=lm.getLevels().get(randIndex);
            


        } else {
            this.levelState=getDefaultLevel();
        }

        findPlayerPosition();
        findGoalPositions();
        findBoxPositions();

    }

    private char[][] getDefaultLevel(){
          return new char[][] {
            {'#','#','#','#','#'},
            {'#','@',' ',' ','#'},
            {'#','$','$',' ','#'},
            {'#','.','.',' ','#'},
            {'#','#','#','#','#'}
        };
    }

    private void findPlayerPosition(){
        
        for (int i=0; i<levelState.length; i++){
            for (int j=0;j<levelState[i].length;j++){
                if (levelState[i][j]=='@'||levelState[i][j]=='+'){
                    this.player_row=i;
                    this.player_col=j;
                    return;
                }
            }
        }
        throw new RuntimeException("BRO! NO PLAYER '@' FOUND IN LEVEL! 🚨");
    }

    private void findGoalPositions(){
        goalPos=new ArrayList<>();
        for (int i=0; i<levelState.length; i++){
            for (int j=0;j<levelState[i].length;j++){
                if (levelState[i][j]=='.'||levelState[i][j]=='+'||levelState[i][j]=='*'){
                    goalPos.add(new Pair<>(i,j));
                }
            }
        }

    }
    
    private void findBoxPositions(){
        boxPos=new ArrayList<>();
        for (int i=0; i<levelState.length; i++){
            for (int j=0;j<levelState[i].length;j++){
                if (levelState[i][j]=='*'||levelState[i][j]=='$'){
                    boxPos.add(new Pair<>(i,j));
                }
            }
        }

    }

    void print(){
        System.out.println("-----------------------");

        for (int i=0;i<levelState.length;i++){
            for (int j=0;j<levelState[i].length;j++){
                System.out.print(levelState[i][j]);
            }
            System.out.println();
        }

        System.out.println("-----------------------");
    }

    double getScore() {return this.score;}

    void setScore(int score) {this.score = score;}

    public void setFather(SokobanState father){ this.father=father; }
    public SokobanState getFather(){return this.father;}

    public int getGCost(){return this.g;}

    public double getF() {return this.f;}

    public char[][] getLevelState(){ return this.levelState;}

  

    boolean moveUp(){
        if (this.player_row<=1){ //if player is on the 2nd row (first row is walls every time), then he can't move up
            return false;
        }


        char tileAbove=this.levelState[this.player_row-1][this.player_col];
      
        if (tileAbove=='#'){ //wall above
            return false;
        }

        boolean isBox=false;
        char tileAboveBox = '\0' ;
        if (tileAbove == '$' || tileAbove == '*') {  // box above
            tileAboveBox = this.levelState[this.player_row-2][this.player_col];
            isBox=true;
            if (tileAboveBox=='$'||tileAboveBox=='#'||tileAboveBox=='*'){ //box above box
                return false;
            }
        }

        
        boolean playerOnGoal=false;
        if (this.levelState[this.player_row][this.player_col]=='+'){
            playerOnGoal=true;
        }

        boolean goalAbove=false;
        if (this.levelState[this.player_row-1][this.player_col]=='.'|| this.levelState[this.player_row-1][this.player_col]=='*'){
            goalAbove=true;
        }

        if (playerOnGoal){
            this.levelState[this.player_row][this.player_col]='.';
        } else {
            this.levelState[this.player_row][this.player_col]=' ';
        }

        if (goalAbove){
            this.levelState[this.player_row-1][this.player_col]='+';
        } else {
            this.levelState[this.player_row-1][this.player_col]='@';
        }

        if (isBox){
            
            if (tileAboveBox==' '){
                this.levelState[this.player_row-2][this.player_col]='$';
              
            }
            else if (tileAboveBox=='.') {
                this.levelState[this.player_row-2][this.player_col]='*'; 
            }
            this.gCost(2);
        }else{
            this.gCost(1);
        }

        this.player_row-=1;
        findBoxPositions();
        return true;


    }

    boolean moveDown(){
        if (this.player_row>=this.levelState.length-2){ //if player is on the 2nd to last row (last row is walls every time), then he can't move down
            return false;
        }


        char tileBelow=this.levelState[this.player_row+1][this.player_col];
      
        if (tileBelow=='#'){ //wall below
            return false;
        }

        boolean isBox=false;
        char tileBelowBox = '\0' ;
        if (tileBelow == '$' || tileBelow == '*') {  // box below
            tileBelowBox = this.levelState[this.player_row+2][this.player_col];
            isBox=true;
            if (tileBelowBox=='$'||tileBelowBox=='#'||tileBelowBox=='*'){ //box below box
                return false;
            }
        }

        
        boolean playerOnGoal=false;
        if (this.levelState[this.player_row][this.player_col]=='+'){
            playerOnGoal=true;
        }

        boolean goalBelow=false;
        if (this.levelState[this.player_row+1][this.player_col]=='.'||this.levelState[this.player_row+1][this.player_col]=='*'){
            goalBelow=true;
        }

        if (playerOnGoal){
            this.levelState[this.player_row][this.player_col]='.';
        } else {
            this.levelState[this.player_row][this.player_col]=' ';
        }

        if (goalBelow){
            this.levelState[this.player_row+1][this.player_col]='+';
        } else {
            this.levelState[this.player_row+1][this.player_col]='@';
        }

        if (isBox){
            
            if (tileBelowBox==' '){
                this.levelState[this.player_row+2][this.player_col]='$';
              
            }
            else if (tileBelowBox=='.') {
                this.levelState[this.player_row+2][this.player_col]='*';
                
            }
            this.gCost(2);
        } else{
            this.gCost(1);
        }

        this.player_row+=1;
        findBoxPositions();
        return true;


    }

    boolean moveRight(){

        if (this.player_col>=this.levelState[1].length-2){ //if player is at second to last column (last is walls) he can't move right
            return false;
        }

        char tileOnRight=this.levelState[this.player_row][this.player_col+1];
        if (tileOnRight=='#'){
            return false;
        }

        char tileRightOfBox='\0';
        boolean isBox=false;

         if (tileOnRight== '$' || tileOnRight == '*') {  // box on the right
            tileRightOfBox = this.levelState[this.player_row][this.player_col+2];
            isBox=true;
            if (tileRightOfBox=='$'||tileRightOfBox=='#'||tileRightOfBox=='*'){ //box on the right of box
                return false;
            }
        }

        boolean playerOnGoal=false;
        if (this.levelState[this.player_row][this.player_col]=='+'){
            playerOnGoal=true;
        }

        boolean goalOnRight=false;
        if (this.levelState[this.player_row][this.player_col+1]=='.'||this.levelState[this.player_row][this.player_col+1]=='*'){
            goalOnRight=true;
        }

        if (playerOnGoal){
            this.levelState[this.player_row][this.player_col]='.';
        } else {
            this.levelState[this.player_row][this.player_col]=' ';
        }

        if (goalOnRight){
            this.levelState[this.player_row][this.player_col+1]='+';
        } else {
            this.levelState[this.player_row][this.player_col+1]='@';
        }

        if (isBox){
            
            if (tileRightOfBox==' '){
                this.levelState[this.player_row][this.player_col+2]='$';
              
            }
            else if (tileRightOfBox=='.') {
                this.levelState[this.player_row][this.player_col+2]='*';
                
            }
             this.gCost(2);
        } else{
            this.gCost(1);
        }

        this.player_col+=1;
        findBoxPositions();
        return true;




    }

    boolean moveLeft(){

        if (this.player_col<=1){ //if player is at second column (first is walls always), then he can't move left
            return false;
        }

        char tileOnLeft=this.levelState[this.player_row][this.player_col-1];
        if (tileOnLeft=='#'){
            return false;
        }

        char tileLeftOfBox='\0';
        boolean isBox=false;

         if (tileOnLeft== '$' || tileOnLeft == '*') {  // box on the left
            tileLeftOfBox = this.levelState[this.player_row][this.player_col-2];
            isBox=true;
            if (tileLeftOfBox=='$'||tileLeftOfBox=='#'||tileLeftOfBox=='*'){ //box on the left of box
                return false;
            }
        }

        boolean playerOnGoal=false;
        if (this.levelState[this.player_row][this.player_col]=='+'){
            playerOnGoal=true;
        }

        boolean goalOnLeft=false;
        if (this.levelState[this.player_row][this.player_col-1]=='.'||this.levelState[this.player_row][this.player_col-1]=='*'){
            goalOnLeft=true;
        }

        if (playerOnGoal){
            this.levelState[this.player_row][this.player_col]='.';
        } else {
            this.levelState[this.player_row][this.player_col]=' ';
        }

        if (goalOnLeft){
            this.levelState[this.player_row][this.player_col-1]='+';
        } else {
            this.levelState[this.player_row][this.player_col-1]='@';
        }

        if (isBox){
            
            if (tileLeftOfBox==' '){
                this.levelState[this.player_row][this.player_col-2]='$';
              
            }
            else if (tileLeftOfBox=='.') {
                this.levelState[this.player_row][this.player_col-2]='*';
                
            }
            
             this.gCost(2);
        } else{
            this.gCost(1);
        }
        this.player_col-=1;
        findBoxPositions();
        return true;

    }

    public boolean isFinal(){

        findGoalPositions();
        for (Pair<Integer,Integer> i: goalPos){ //check all goal positions
            if (levelState[i.getFirst()][i.getSecond()]!='*'){ //if the character is not box on goal (*), it is not final.
                return false;
            }
        }

        return true;
    }

 ArrayList<SokobanState> getChildren(int heuristic){
        ArrayList<SokobanState> children=new ArrayList<>();
        SokobanState child=new SokobanState(this.levelState);
        child.setFather(this);
        if (child.moveUp()){
            child.evaluate(heuristic);
            children.add(child);
        }

        child = new SokobanState(this.levelState); 
        child.setFather(this);
        if(child.moveDown())
        {
			child.evaluate(heuristic);
            children.add(child);
        }
        child = new SokobanState(this.levelState); 
        child.setFather(this);
        if(child.moveLeft())
        {
            child.evaluate(heuristic);
            children.add(child);
        }
        child = new SokobanState(this.levelState);
        child.setFather(this);
        if(child.moveRight())
        { 
			child.evaluate(heuristic);
            children.add(child);
        }
        return children;
    }
    

    

    

    private void boxesOffGoals(boolean detectDeadlocks){
        this.score=0; //re-calculate every time heuristic is called
        for (Pair<Integer, Integer> box : boxPos) {
            if (detectDeadlocks){
                if (deadlockDetection(box.getFirst(), box.getSecond())){
                    this.score=Integer.MAX_VALUE;
                    return;
                }
            }
            if (levelState[box.first][box.second] != '*') {
                this.score++;
            }   
        }  
    }

    private void calculateManhattan(boolean detectDeadlocks){
        this.score=0; //re-calculate every time heuristic is called
        int manhattan;
        
        for (Pair<Integer,Integer> box:boxPos){
            if (detectDeadlocks){
                if (deadlockDetection(box.getFirst(),box.getSecond())){
                    this.score=Integer.MAX_VALUE;
                    return;
                }
            }
            if (this.levelState[box.getFirst()][box.getSecond()] == '*') {
                // Skip this box, it is already on goal.
                continue; 
            }
            int minDst=Integer.MAX_VALUE;
            for (Pair<Integer,Integer> goal:goalPos){
                if (this.levelState[goal.getFirst()][goal.getSecond()] == '*') {
                    //don't calculate distance to goal that is already taken
                    continue; 
                }
                manhattan=Math.abs(box.getFirst()-goal.getFirst())+Math.abs(box.getSecond()-goal.getSecond());
                if (manhattan<minDst){
                    minDst=manhattan;
                }
                
            }
            this.score+=minDst;  
        }
    }

    private boolean deadlockDetection(int r,int c){
       

        if (goalPos.contains(new Pair<Integer,Integer>(r,c))) return false;

        

        boolean wallUp = (levelState[r - 1][c] == '#');
        boolean wallDown = (levelState[r + 1][c] == '#');
        boolean wallLeft = (levelState[r][c - 1] == '#');
        boolean wallRight = (levelState[r][c + 1] == '#');

        if (wallDown&&wallLeft) return true;
        if (wallDown&&wallRight) return true;
        if (wallUp&&wallRight) return true;
        if (wallUp&&wallLeft) return true;

        return false;


    }

    private void gCost(int c){
    
   
    
        if (this.father == null) {
            this.g = 0;
            return;
        }

        // Since we know the father exists, we can safely look up the parent's cost
        int parentGCost = this.father.getGCost();
    
        switch (c) {
            case 1: // Player Move (cost 0)
                this.g = parentGCost;
                break;
            case 2: // Box Push (cost 1)
                this.g = parentGCost + 1;
                break;
            default:
            //Handle unknown move
                this.g = parentGCost; 
                break;
        }
    }

    public boolean equals (Object other){
        if (other.getClass()!=this.getClass()||other==null){
            return false;
        }
        if (this==other){
            return true;
        }
        
        SokobanState that= (SokobanState) other;
        if (this.player_row!=that.player_row||this.player_col!=that.player_col){
            return false;
        }

        if (this.levelState.length != that.levelState.length||this.levelState[0].length!=that.levelState[0].length){
            return false;
        } 

        if (!Arrays.deepEquals(this.levelState, that.levelState)){
            return false;
        }

        return true;


    }

    @Override
    public int compareTo(SokobanState other) {
        return Double.compare(this.f, other.f); // lowest f first
    }

    public void evaluate(int heuristic){
        this.gCost(100); //get cost g if not yet calculated, assume normal , no box-push case
        switch(heuristic){
            case 1:
                boxesOffGoals(false);
                break;
            case 2:
                boxesOffGoals(true);
                break;
            case 3:
                calculateManhattan(false);
                break;
            case 4:
                calculateManhattan(true);
                break;
            default:
                calculateManhattan(true);
            
            
        }

        this.f=this.score+this.g;  
    }

    public int hashCode() {
        // 1. Hash the contents of the entire 2D map array (Boxes, Walls, Goals)
        int mapContentHash = Arrays.deepHashCode(this.levelState);
    
        // 2. Combine the map hash with the player coordinates
        return Objects.hash(mapContentHash, this.player_row, this.player_col);
    }

    

    









    public static void main(String[] args) {
        SokobanState state=new SokobanState();
        state.evaluate(2);
        System.out.println("F value:" + state.getF());

        
        


    } 

}




