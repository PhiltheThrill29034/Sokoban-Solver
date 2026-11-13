
import java.util.*;


import java.io.*;

class LevelManager{
    ArrayList<char[][]> gameLevels=new ArrayList<>(); //list of 2D char levels

    public void makeLevel(List<String> lines){
        if (lines.isEmpty()||lines==null){
            System.out.println("Empty level");
            return;
        }
        int rows=lines.size();
        int cols=0;

        for (int i=0;i<rows;i++){
            if (lines.get(i).length()>cols){
                cols=lines.get(i).length();
            }
        }

        char[][] level=new char[rows][cols];
        for (int i=0;i<rows;i++){
            String currentLine=lines.get(i);
            for (int c = 0; c < cols; c++) {
                if (c<currentLine.length()){
                    level[i][c]=currentLine.charAt(c);
                } else {
                    level[i][c]=' '; // Pad with spaces
                }
            }
        }

        this.gameLevels.add(level); //add level to the list


    }

    public ArrayList<char[][]> getLevels(){
        return this.gameLevels;
    }
     private boolean isValidLevelChar(char c) {
        return c == '#' || c == '.' || c == '@' || c == '+' || c == '$' || c == '*' || c == ' ';
    }
    private boolean validateLine(String line) { 
    
    for (char c : line.toCharArray()) { 
        if (!isValidLevelChar(c)) {   
            return false;
        }
    }
    return true;
}

    public void readLevels(String filePath)throws IOException{
        

       try (BufferedReader br=new BufferedReader(new FileReader(filePath))){
            String line;
            
            while ((line=br.readLine())!=null){
                line=line.trim();
                if (!line.startsWith("Level"))
                    continue;
                String numberStr = line.replaceAll("[^0-9]", "");
                
                while ((line = br.readLine())!=null) {
                    if (!line.trim().isEmpty())
                        break;
                }

                if (line==null) {
                    System.out.println("Expected level, Level #"+numberStr+" was Empty");
                    break; //end of file
                }
                
                
                
                
                

                List<String> currentLevelLines=new ArrayList<>();
                boolean validLevel=true;
                while ((line)!=null&&!line.isEmpty()){
                    
                    if (validateLine(line)){
                        currentLevelLines.add(line);
                    } else {
                        validLevel=false;
                        break;
                    }
                    line=br.readLine();

                }

                if(!currentLevelLines.isEmpty()&&validLevel){
                    makeLevel(currentLevelLines);    
                } else{
                    System.out.println("Invalid level format or empty level, skipping level #"+numberStr);
                }

                


            }
       } catch (IOException e){
            System.err.println("ERROR");
       }
    }

    public void printLevel(int index){
        if (index < 0 || index >= gameLevels.size()) {
            System.out.println("BRO! INVALID LEVEL INDEX: " + index);
            return;
        }

        char[][] level=gameLevels.get(index);
        System.out.println("=== LEVEL "+(index+1)+" ===");
        for (int i=0;i<level.length;i++){
            for (int j=0;j<level[i].length;j++){
                System.out.print(level[i][j]);
            }
            System.out.println();
        }

        System.out.println("===============");
    }

    public void printAllLevels(){
       for (int i=0;i<gameLevels.size();i++){
            printLevel(i);
       }
    }

    public static void main(String[] args) {
        LevelManager lm=new LevelManager();
        try{
            File file=new File("levelExamples.txt");
            if(!file.exists()){
                System.out.println("File not found!");
                return;
            }
            System.out.println("✅ FILE FOUND! LOADING LEVELS...");
            lm.readLevels("levelExamples.txt");
            System.out.println("🎉 SUCCESS! LOADED " + lm.gameLevels.size() + " LEVELS!");
        
            // QUICK TEST
            if (lm.gameLevels.size() > 0) {
                System.out.println("🔥 PRINTING FIRST LEVEL:");
                lm.printAllLevels();
            } else {
                System.out.println("🤔 NO LEVELS LOADED - CHECK FILE FORMAT");
            }
        
        } catch (IOException e) {
            System.out.println("💥 IO ERROR: " + e.getMessage());
            e.printStackTrace();
        } catch (Exception e) {
            System.out.println("💀 UNEXPECTED ERROR: " + e.getMessage());
            e.printStackTrace();
        }
    
        System.out.println("🏁 PROGRAM FINISHED!");
    }
}