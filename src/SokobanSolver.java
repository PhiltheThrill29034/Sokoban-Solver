
import java.util.*;
import java.io.*;

public class SokobanSolver {
    PriorityQueue<SokobanState> frontier;
    HashSet<SokobanState> closedSet;
    

    SokobanSolver(){
        
        frontier=new PriorityQueue<>();
        closedSet=new HashSet<>();
      

    }



    SokobanState aStar(SokobanState initialState,int heuristic){

        int statesExplored=0;

        if (initialState.isFinal()) return initialState;

        
        initialState.evaluate(4);
        this.frontier.add(initialState);

        SokobanState currentState;
        ArrayList<SokobanState> children= new ArrayList<>();
        
        long start=System.currentTimeMillis();

        while (this.frontier.size()>0){
            
            long curTime=System.currentTimeMillis();
            if (curTime-start>10000){
                System.out.println("Couldn't find a solution, time limit reached.");
                return null;
            }
           statesExplored++;
           
           if (statesExplored%1000000==0){
            
            System.out.println(statesExplored/1000000+" million states explored.");
           }
            
              
            currentState=frontier.poll();

            if (currentState.isFinal()){
                System.out.println("Solution found");
                return currentState;
            }

            if (this.closedSet.contains(currentState)) {
                continue; 
            }

            this.closedSet.add(currentState); 
        
            // 5. Generate Children
            children = currentState.getChildren(heuristic);

          

            for (SokobanState child : children) {
            
            
            if (child.f == Integer.MAX_VALUE) continue;
            
            
            if (this.closedSet.contains(child)) {
                continue; 
            }
           
            

            // 7. Add only valid, new states
            this.frontier.add(child);
            
        }  
        }

        return null;

    }

   

    public static void main(String[] args) {
        LevelManager lm=new LevelManager();
        Scanner scanner=new Scanner(System.in);
        try{
            lm.readLevels("levelExamples.txt");

            lm.printAllLevels();
           
            System.out.print("🎮 Select level number (1-" + (lm.gameLevels.size()) + "): ");
            int levelChoice = scanner.nextInt();

            while (levelChoice<1||levelChoice>lm.gameLevels.size()){
                System.out.print("Please enter a valid choice from the level menu : ");
                levelChoice=scanner.nextInt();
            }

            char[][] chosenLevel=lm.gameLevels.get(levelChoice-1);
        
            SokobanState state1=new SokobanState(chosenLevel);
            state1.print();

            SokobanSolver solver=new SokobanSolver();
            
            System.out.println("Enter heuristic choice\n1. Boxes off goals - no deadlock detection.\n2. Boxes off goals - deadlock detection.\n3. Manhattan Distance - No deadlock detection\n4. Manhattan Distance - Deadlock detection");
            System.out.print("-> ");
            int hrChoice=scanner.nextInt();
            long startTime = System.nanoTime();
            SokobanState solution=solver.aStar(state1, hrChoice);
            if (solution!=null){
                long endTime = System.nanoTime();
                long durationNano = endTime - startTime;
                double durationSeconds = (double) durationNano / 1_000_000_000.0;
        
                SokobanState temp=solution;
                ArrayList<SokobanState> path= new ArrayList<>();
                path.add(solution);
                while (temp.getFather()!=null){
                    path.add(temp.getFather());
                    temp=temp.getFather();
                }
                Collections.reverse(path);

                for (SokobanState state: path){
                    state.print();
                }

                System.out.printf("✅ Solution found in: %.4f seconds (%,d nanoseconds)%n", durationSeconds, durationNano);
            }

            } catch(IOException e){
            System.err.println("Error reading file");

            }

        
            scanner.close();

        
        
        
        
    

    }
}
