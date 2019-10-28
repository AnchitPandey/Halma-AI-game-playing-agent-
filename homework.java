import java.io.*;
import java.util.*;
import static java.util.stream.Collectors.toMap;



class homework
{    
    
    public static String findWinner (char[][] board)
    {
        ArrayList<String> coords = homeCoords("BLACK");
       int counter =0;
        for (int i =0;i<coords.size();i++)
        {
            String coors = coords.get(i);
            String[] splitter = coors.split(",");
            int x = Integer.parseInt(splitter[0]);
            int y = Integer.parseInt(splitter[1]);
            if (board[x][y] =='W')
                counter++;
            else
            {
                counter =0;
                break;
            }       
        }
        if (counter ==19)
            return "WHITE";
        
      coords = homeCoords("WHITE"); 
           for (int i =0;i<coords.size();i++)
        {
            String coors = coords.get(i);
            String[] splitter = coors.split(",");
            int x = Integer.parseInt(splitter[0]);
            int y = Integer.parseInt(splitter[1]);
            if (board[x][y] =='B')
                counter++;
            else
            {
                counter =0;
                break;
            }       
        }  
        if(counter ==19 )
            return "BLACK";
        else
            return "no";
    }
    
    
    
    public static String alphaBetaPruning2 (int depth, String currentPlayerColour, float timeLimit, double alpha, double beta, long startTime, String myPlayerColour, char[][] board)throws Exception
    {
       
        
   //     System.out.println("starting alphabeta at depth "+depth);
   //     System.out.println("Time limit is: "+ timeLimit);
  //      System.out.println("Start time is: "+ startTime);
       long currentTime = System.currentTimeMillis();   
   //    System.out.println("current time is: "+currentTime);
      //  System.out.println("Time left for this move: "+ (float)((timeLimit -currentTime +startTime) /1000));
     
        if (depth ==0)
        {
            
    //        System.out.println("Bottom most depth reached");
             return " "+utility(currentPlayerColour, board);
        }
        
        if ( depth ==0 || currentTime - startTime >= timeLimit -500 || !findWinner(board).equals("no"))
       {
   //        System.out.println(" Time OUT !!! ");
           return " "+utility(currentPlayerColour, board);
       }
        String bestMove ="";
        double bestValue;
        if (currentPlayerColour.equals (myPlayerColour))
            bestValue = Double.MIN_VALUE; 
        else 
            bestValue = Double.MAX_VALUE;       
            
        String opponentColour;
        if (currentPlayerColour.equals ("WHITE"))
            opponentColour = "BLACK";
        else
            opponentColour = "WHITE";
        
        File file = new File ("C:\\Users\\Anchit Pandey\\Desktop\\playdata.txt");
        
        ArrayList<String> allMoves = getPossibleMoves(currentPlayerColour, timeLimit, board, startTime);
        allMoves = sortMoves(allMoves, board, currentPlayerColour);
       // Initializing best move 
       bestMove = allMoves.get(0);       
       for (int i =0;i<allMoves.size();i++)
        {
      //      System.out.println("This is move number: "+ (i+1));
           String move = allMoves.get(i);        
           currentTime = System.currentTimeMillis();
           if (currentTime - startTime >= timeLimit-500)
               return bestMove+" "+bestValue; 
           
           makeMove(move, board);  
           
           // Get the hashboard value
           String hashBoard = "";
           for (int row = 0;row< 16;row++)
           {
               for (int col =0;col<16;col++)
               {
                  hashBoard += String.valueOf(board[row][col])+ String.valueOf(row)+","+String.valueOf(col)+"|";
               }   
           }      
     int hashBoardPresentFlag = 0;
     double tempVal =0;
          if (file.exists())
          {
           BufferedReader buff = new BufferedReader(new FileReader(file));
           String lineRead;        
           
           // check if the board configuration is present in the hashfunction        
           while ((lineRead =buff.readLine()) != null)
           {
               String[] splitter = lineRead.split("\\s+");
               if (splitter[0].equals (hashBoard))
               {
                  // System.out.println("Found move in playdata.txt");
                   hashBoardPresentFlag=1;
                   tempVal = Double.parseDouble (splitter[1]);
                   break;
               }
           }
          buff.close();
          }
           String returnString ="";
           String[] lineSplitter, spaceString;
           double val;
           if (hashBoardPresentFlag ==0)
           {
          //     System.out.println("hash key not found");
                 returnString =  alphaBetaPruning2(depth-1, opponentColour, timeLimit, alpha, beta, startTime, myPlayerColour, board);        
                  lineSplitter = returnString.split("\n");
                  spaceString = lineSplitter[lineSplitter.length-1].split("\\s+");
                 // System.out.println("return string value is: "+ returnString);
                  val = Double.parseDouble(spaceString[spaceString.length-1]); 
                  BufferedWriter writer = new BufferedWriter (new FileWriter(file, true));
                  writer.append(hashBoard+" "+val+"\n");
                  writer.close();
            //      System.out.println("nvm, I appended it");
                 
           }
           else
               val = tempVal;
           
           undoMove(move, board);  
           if (currentPlayerColour.equals(myPlayerColour) && val > bestValue)
           {
               bestValue = val;
               bestMove = move;
               if (val > alpha)
                   alpha = val;               
           }
           
           if (!currentPlayerColour.equals (myPlayerColour) &&  val < bestValue)
           {
               bestValue = val;
               bestMove = move;
               if (val< beta)
                   beta = val;
           }
           
           if (beta <= alpha)
           {
     //       System.out.println("pruned");
            return bestMove+" "+bestValue;      
       
           }
           
           }
  //      System.out.println("I BROKE FROM THE FOR LOOP !!!!");
     //    System.out.println("exiting alphaBetaPruning2()");
     return bestMove+" "+bestValue;
    }
   
   
    
    // Eucledian Distance 
    public static float eucledianDistance (float startX, float startY, float finishX, float finishY)
    {
        return (float)Math.sqrt(Math.pow(startY -finishY,2)+ Math.pow(startX - finishX,2));        
    }
  
    
    public static ArrayList<String> sortInitial (ArrayList<String> availableMoves, char[][] board, String colour)throws Exception
    {
      //  System.out.println("Inside sortInitial ()");
       ArrayList<String> sortedMoves;
       HashMap<String, Float> utilityMapper = new HashMap<String , Float>();
        for (int i =0;i<availableMoves.size();i++)
        {
            String move = availableMoves.get(i);
            makeMove(move, board);           
            float utilityForThisMove = utilityInitial(colour, board);
            utilityMapper.put(move, utilityForThisMove);
            undoMove(move, board);     
        }        
        
       // Sorting hashmap descending order by value      
       Map<String, Float> sortedMovesUtilityDesc = utilityMapper.entrySet()
                                                                .stream()
                                                                .sorted(Map.Entry.<String, Float>comparingByValue().reversed())
                                                                .collect(toMap(Map.Entry::getKey, 
                                                                         Map.Entry::getValue, (e1, e2) -> e1, LinkedHashMap::new));
               
        sortedMoves = new ArrayList<String>(sortedMovesUtilityDesc.keySet());   
       // System.out.println("exiting sortInitial");
        return sortedMoves;       
    }
    
    
    
    
    
    // sorts moves based on the decreasing utility function   
    public static ArrayList<String> sortMoves(ArrayList<String> allPossibleMoves, char[][] board, String colour)throws Exception
    {
        
     //   System.out.println("inside sortMoves()");
        ArrayList<String> sortedMoves;
        HashMap<String, Float> utilityMapper = new HashMap<String , Float>();
        for (int i =0;i<allPossibleMoves.size();i++)
        {
            String move = allPossibleMoves.get(i);
            makeMove(move, board);        
            float utilityForThisMove = utility(colour, board);
            utilityMapper.put(move, utilityForThisMove);
            undoMove(move, board);    
        }        
              
       // Sorting hashmap descending order by value      
       Map<String, Float> sortedMovesUtilityDesc = utilityMapper.entrySet()
                                                                .stream()
                                                                .sorted(Map.Entry.<String, Float>comparingByValue().reversed())
                                                                .collect(toMap(Map.Entry::getKey, 
                                                                         Map.Entry::getValue, (e1, e2) -> e1, LinkedHashMap::new));
               
        sortedMoves = new ArrayList<String>(sortedMovesUtilityDesc.keySet());   
    //    System.out.println("exiting sortMoves()");
        return sortedMoves;
    }
       
 
    // making a particular move 
    public static void makeMove (String move, char[][] board)
    {  
     //   System.out.println("inside makeMove()");
        String[] lineSplitter = move.split("\n");
        String finalCoordLine = lineSplitter[lineSplitter.length-1];
        String[] spaceSplitter =finalCoordLine.split("\\s+");
        String finalCoords = spaceSplitter[2];
        String[] commaSplitter = finalCoords.split(",");
        int finalX = Integer.parseInt(commaSplitter[1]);
        int finalY = Integer.parseInt(commaSplitter[0]);
        
        String initialCoordLine = lineSplitter[0];
        spaceSplitter = initialCoordLine.split("\\s+");
        String initialCoords = spaceSplitter[1];
        commaSplitter = initialCoords.split(",");
        int initialX = Integer.parseInt(commaSplitter[1]);
        int initialY = Integer.parseInt(commaSplitter[0]);
 
       char playerPawn = board[initialX][initialY];
       board[initialX][initialY] = '.';
       board[finalX][finalY] = playerPawn;
       
    //   System.out.println("exiting makeMove()");
       return;      
    }
            
    // undoing a particular move
    public static void undoMove (String move, char[][] board)
    {
   //     System.out.println("inside undoMove()");
        String[] lineSplitter = move.split("\n");
        String finalCoordLine = lineSplitter[lineSplitter.length-1];
        String[] spaceSplitter =finalCoordLine.split("\\s+");
        String finalCoords = spaceSplitter[2];
        String[] commaSplitter = finalCoords.split(",");
        int finalX = Integer.parseInt(commaSplitter[1]);
        int finalY = Integer.parseInt(commaSplitter[0]);
        
        String initialCoordLine = lineSplitter[0];
        spaceSplitter = initialCoordLine.split("\\s+");
        String initialCoords = spaceSplitter[1];
        commaSplitter = initialCoords.split(",");
        int initialX = Integer.parseInt(commaSplitter[1]);
        int initialY = Integer.parseInt(commaSplitter[0]);
        
        char playerPawn = board[finalX][finalY];
        board[finalX][finalY] = '.';
        board[initialX][initialY] = playerPawn;   
    //    System.out.println("exiting undoMove()");
        return;
    }
    
    
    // creating utility function for the initial state when not all the pawns are out of the region  
    public static float utilityInitial (String colour, char[][] board)
    {
     
   //     System.out.println("Inside utility initial");   
       float utilityValue = 0;
       String oppositionColour;
       if (colour.equals ("WHITE"))
           oppositionColour = "BLACK";
       else 
           oppositionColour = "WHITE";
       
      ArrayList<String> homeCoordinates =  homeCoords(colour);
      int numberOfPiecesOutOfhome = 0;
        String[] splitter;
     
      // awarding points that go out of the home region
      for (int i =0;i< homeCoordinates.size();i++)
      {
          String inp = homeCoordinates.get(i);
          splitter = inp.split(",");
          int arrayX = Integer.parseInt(splitter[0]);
          int arrayY = Integer.parseInt(splitter[1]);
          if (board[arrayX][arrayY]!=colour.charAt(0))
              numberOfPiecesOutOfhome++;
      }
     //   utilityValue += numberOfPiecesOutOfhome*30;
        
        
        // giving more reward to moves that increase eucledian distance away from center 
        // considering only those pieces that are inside the home region
        
         ArrayList<String> allPawnCoordinates = allTheCoords(colour, board);
        float distanceValue =0;
        float centroidX =0, centroidY =0 ;
       
     /* 
        for (int i =0;i< homeCoordinates.size();i++)
       {
          String inp = homeCoordinates.get(i);
          splitter = inp.split(",");
          int arrayX = Integer.parseInt(splitter[0]);
          int arrayY = Integer.parseInt(splitter[1]);
          if (board[arrayX][arrayY] !=colour.charAt(0))
              continue;
          centroidX+= arrayX;
          centroidY+= arrayY;
          distanceValue += (float)Math.sqrt(Math.pow (arrayX - startCoordX,2)+ Math.pow(arrayY - startCoordY,2));
       }
        */
        //   utilityValue += distanceValue * 50;   
           
                      
   // awarding moves that increase the cosine theta value;        
   /*       
  centroidX/=(float)19;
  centroidY/=(float)19;
  centroidX -=startCoordX;
  centroidY -=startCoordY;

  float cosineTheta;
  if (colour.equals("WHITE"))
     cosineTheta = ((float) -225/19 * centroidX - (float)225/19 * centroidY)/ ((float)eucledianDistance(centroidX, centroidY, 0, 0) * (float)225/19 * (float)1.414);
      
  else 
     cosineTheta = ((float) 225/19 * centroidX + (float)225/19 * centroidY)/ ((float)eucledianDistance(centroidX, centroidY, 0, 0) * (float)225/19 * (float)1.414);
  
   */  
  //utilityValue += cosineTheta* 2;           
     
  ArrayList<String> goalCoords = homeCoords(oppositionColour);

// penalizing distances maxizing distance
      float max =-1;
      for (int dup = 0; dup <homeCoordinates.size();dup++)
       {
           splitter = homeCoordinates.get(dup).split(",");
           int row = Integer.parseInt(splitter[0]);
           int col = Integer.parseInt(splitter[1]);
           if (board[row][col] == colour.charAt(0))
              {
              //    centroidX += row;
               //   centroidY +=col;
    //              hashBoard+=String.valueOf(board[row][col])+String.valueOf(row)+","+String.valueOf(col)+" ";
                for (int index =0; index < goalCoords.size();index++)
                 {
                     splitter = goalCoords.get(index).split(",");
                     int goalX = Integer.parseInt(splitter[0]);
                     int goalY = Integer.parseInt(splitter[1]);
                     if (board[goalX][goalY] != colour.charAt(0))
                     {
                        float dist = eucledianDistance(row,col, goalX, goalY);
                        if (dist > max)
                             max = dist;                   
                     }
                 }
                utilityValue -= max* 50;
              }   
           }            
       return utilityValue;
    }
    
    
 
    // creating utility function
    // First adding all the utilities of own player then subtracting it with utilities of opposition     
    /// ***  CHECK THIS FUNCTION FOR OPTIMISATION *** ///
   
    
    public static float utility (String colour, char[][] board)throws Exception
    {   
  //      System.out.println("inside utility()");
       float utilityValue = 0;
       String oppositionColour = "";
       char charToSearch;
       if (colour.equals("WHITE"))
       {
            oppositionColour = "BLACK";
            charToSearch = 'W';
       }  
       else
       {
           oppositionColour = "WHITE";
           charToSearch = 'B';
       }
       

        /* MAYBE REMOVE THIS ONE */

       // Penalising the pawns that are out of their goal region

       ArrayList<String> goalCoords = homeCoords(oppositionColour);
       HashMap<String, Integer> goalCoordsHashMap = homeCoordsHashMap(oppositionColour);
       ArrayList<String> homeCoordinates = homeCoords(colour);
       
  //     int numberOfPiecesOutOfGoal = 0;
/*
       
       for (int i = 0;i< goalCoords.size();i++)
       {
           String coords = (String)goalCoords.get(i);
           String[] splitter = coords.split(",");
           int goalX = Integer.parseInt(splitter[0]);
           int goalY = Integer.parseInt(splitter[1]);
           if (board[goalX][goalY] !=charToSearch)
              numberOfPiecesOutOfGoal+=1;            
       }
        utilityValue -= numberOfPiecesOutOfGoal * 20;  
        */


        float max = -1;
        String[] splitter;
        float centroidX =0 , centroidY =0;
        
     
        // Calculating max of distances 
       for (int row = 0; row < 16;row++)
       {
           for (int col = 0;col < 16;col ++)
           {
              if (board[row][col] == charToSearch && !goalCoordsHashMap.containsKey(String.valueOf(row)+","+String.valueOf(col)) )                     // Modifying this line
              {
                  centroidX += row;
                  centroidY +=col;
    //              hashBoard+=String.valueOf(board[row][col])+String.valueOf(row)+","+String.valueOf(col)+" ";
                for (int index =0; index < goalCoords.size();index++)
                 {
                     splitter = goalCoords.get(index).split(",");
                     int goalX = Integer.parseInt(splitter[0]);
                     int goalY = Integer.parseInt(splitter[1]);
                     if (board[goalX][goalY] != charToSearch)
                     {
                        float dist = eucledianDistance(row,col, goalX, goalY);
                        if (dist > max)
                             max = dist;                   
                     }
                 }
                utilityValue -= max* 50;
              }
            
           }           
       }
       
       // Good to increase minimum distance from home coordinates
     /*  
       float min = Float.MAX_VALUE;
       for (int row = 0; row < 16;row++)
       {
           for (int col = 0;col < 16;col ++)
           {
              if (board[row][col] == charToSearch )                     // Modifying this line
              {
                  centroidX += row;
                  centroidY +=col;
    //              hashBoard+=String.valueOf(board[row][col])+String.valueOf(row)+","+String.valueOf(col)+" ";
                for (int index =0; index < homeCoordinates.size();index++)
                 {
                     splitter = goalCoords.get(index).split(",");
                     int goalX = Integer.parseInt(splitter[0]);
                     int goalY = Integer.parseInt(splitter[1]);
                     if (board[goalX][goalY] != charToSearch)
                     {
                        float dist = eucledianDistance(row,col, goalX, goalY);
                        if (dist < min)
                             min = dist;                   
                     }
                 }
                utilityValue += min* 2;
              }
            
           }           
       }
       
       
     */  
       
       
       
       
       
       // preferring moves that prevent spread of centroid      
     /*
       centroidX /=(float)19;
       centroidY /=(float)19;
       float idealCentroidX, idealCentroidY, idealCentroidModValue, centroidModValue;
       float cosineTheta;
       if (colour.equals("WHITE"))
       {
           idealCentroidX = -(float)225/19;
           idealCentroidY = -(float)225/19;
           centroidX -=15;
           centroidY -=15;
           idealCentroidModValue = (float)225/19 * (float)Math.sqrt(2);      
           centroidModValue = eucledianDistance(centroidX, centroidY, 0, 0);
           cosineTheta = (float)(centroidX * idealCentroidX +  centroidY * idealCentroidY)/ (centroidModValue * idealCentroidModValue); 
       }    
       else
       {
           idealCentroidX = (float)225/19;
           idealCentroidY = (float)225/19;
           idealCentroidModValue = (float)225/19 * (float)Math.sqrt(2);      
           centroidModValue = eucledianDistance(centroidX, centroidY, 0, 0);
           cosineTheta = (float)(centroidX * idealCentroidX +  centroidY * idealCentroidY)/ (centroidModValue * idealCentroidModValue);            
       }  
        utilityValue += cosineTheta*2;
   */     
        
        /// Playing in place of opponent ///
        
        float opponentUtilityValue =0;
        if (charToSearch =='W')
            charToSearch = 'B';
        else
           charToSearch = 'W';
        
        if (oppositionColour.equals ("WHITE"))
            oppositionColour = "BLACK";
        else
            oppositionColour = "WHITE";
        
       if (colour.equals("WHITE"))
           colour = "BLACK";
       else
           colour = "WHITE";
        
       // numberOfPiecesOutOfGoal = 0;
        goalCoords = homeCoords(oppositionColour);
        goalCoordsHashMap = homeCoordsHashMap(oppositionColour);
        homeCoordinates = homeCoords(colour);
       
        
        /* can remove this */
 
        /*
        for (int i = 0;i< goalCoords.size();i++)
        {
           String coords = (String)goalCoords.get(i);
           splitter = coords.split(",");
           int goalX = Integer.parseInt(splitter[0]);
           int goalY = Integer.parseInt(splitter[1]);
           if (board[goalX][goalY] !=charToSearch)
              numberOfPiecesOutOfGoal+=1;            
        }
        opponentUtilityValue -= numberOfPiecesOutOfGoal * 20;       
   */
        max = -1;
     //   min = Float.MAX_VALUE;
        centroidX =0 ; centroidY =0;
        
   
        
        // Calculating max of distances 
        for (int row = 0; row < 16;row++)
        {
           for (int col = 0;col < 16;col ++)
           {
              if (board[row][col] == charToSearch && !goalCoordsHashMap.containsKey(String.valueOf(row)+","+String.valueOf(col)))
              {
                  centroidX += row;
                  centroidY +=col;
                for (int index =0; index < goalCoords.size();index++)
                 {
                     splitter = goalCoords.get(index).split(",");
                     int goalX = Integer.parseInt(splitter[0]);
                     int goalY = Integer.parseInt(splitter[1]);
                     if (board[goalX][goalY] != charToSearch)
                     {
                        float dist = eucledianDistance(row,col, goalX, goalY);
                        if (dist > max)
                             max = dist;                   
                     }
                 }
                opponentUtilityValue -= max* 50;
              }
           }           
        }
        
        
        
        // Good to increase minimum distance
   /*            
       for (int row = 0; row < 16;row++)
       {
           for (int col = 0;col < 16;col ++)
           {
              if (board[row][col] == charToSearch )                     // Modifying this line
              {
                  centroidX += row;
                  centroidY +=col;
    //              hashBoard+=String.valueOf(board[row][col])+String.valueOf(row)+","+String.valueOf(col)+" ";
                for (int index =0; index < homeCoordinates.size();index++)
                 {
                     splitter = goalCoords.get(index).split(",");
                     int goalX = Integer.parseInt(splitter[0]);
                     int goalY = Integer.parseInt(splitter[1]);
                     if (board[goalX][goalY] != charToSearch)
                     {
                        float dist = eucledianDistance(row,col, goalX, goalY);
                        if (dist < min)
                             min = dist;                   
                     }
                 }
                opponentUtilityValue += min* 2;
              }
            
           }           
       }
      */  
        
       
       // preferring moves that prevent spread of centroid      
     /*
       centroidX /=(float)19;
       centroidY /=(float)19;
       if (oppositionColour.equals("BLACK"))
       {
           idealCentroidX = -(float)225/19;
           idealCentroidY = -(float)225/19;
           centroidX -=15;
           centroidY -=15;
           idealCentroidModValue = (float)225/19 * (float)Math.sqrt(2);      
           centroidModValue = eucledianDistance(centroidX, centroidY, 0, 0);
           cosineTheta = (float)(centroidX * idealCentroidX +  centroidY * idealCentroidY)/ (centroidModValue * idealCentroidModValue); 
       }
       
       else
       {
           idealCentroidX = (float)225/19;
           idealCentroidY = (float)225/19;
           idealCentroidModValue = (float)225/19 * (float)Math.sqrt(2);      
           centroidModValue = eucledianDistance(centroidX, centroidY, 0, 0);
           cosineTheta = (float)(centroidX * idealCentroidX +  centroidY * idealCentroidY)/ (centroidModValue * idealCentroidModValue);            
       }  
        opponentUtilityValue += cosineTheta*2;     
       */ 
        
        /*
        hashBoard+= (utilityValue - opponentUtilityValue);
        BufferedWriter writer = new BufferedWriter (new FileWriter(file));
        writer.append (hashBoard);
        writer.close();
        */
      //  System.out.println("exiting utility()");
        return utilityValue - opponentUtilityValue;                 
    }
    
    
    // Getting ** ARRAY COORDINATES ** of all the pawns.   
    ////////////// THIS CAN BE ONE TIME FUNCTION AS I CAN STORE THE COORDS OF ALL MY PAWNS IN THE PLAYER.DAT FILE ///////////////////
    
    public static ArrayList<String> allTheCoords(String colour, char[][] board)
    {
        ArrayList<String> listToReturn =  new ArrayList<String>();
        char charToLookFor =  colour.charAt(0);
        for (int row =0;row<16;row++)
        {
           for (int column = 0;column< 16;column++)
            {
              if (board[row][column] ==charToLookFor)
              {
                  listToReturn.add (String.valueOf(row)+","+String.valueOf(column));  
              }
            }            
        }  
        return listToReturn;    
    }
    
    
    
    
    //// FINDS MANHATTAN DISTANCE OF A PAWN FROM ITS OWN GOAL END ////
    public static int manhattanDistanceFromOwnGoal(int arrayX,int arrayY, String colour)
    {
        if (colour.equals("WHITE"))
            return Math.abs (arrayX -15)+ Math.abs(arrayY - 15);        
        else
            return Math.abs(arrayX) + Math.abs(arrayY);  
    
    }
  
    
    // Gets the initial jump moves for the pawns in the home territory
    
    public static ArrayList<String> jumpMovesInitial(String colour, char[][] board, int arrayX, int arrayY, ArrayList<String> moves, int origX, int origY, String parentString, HashMap<String,Integer> visited, HashMap<String, Integer> homeComing, long startTime, long initialTimeLimit) 
    {
      //  System.out.println("inside jumpMoves Initial");
        // marking the coords as visited so that I dont return back to these coords again    
        visited.put (String.valueOf(arrayX)+","+String.valueOf(arrayY),1); 
       
        long currentTime = System.currentTimeMillis();
        if (currentTime - startTime >= initialTimeLimit-1)
            return moves;
        
        if (colour.equals("WHITE"))

        {
        // checking north west direction 
        if (arrayX -2 >=0 && arrayY-2 >=0 && board[arrayX-2][arrayY-2] =='.' && board[arrayX-1][arrayY-1]!='.' && !visited.containsKey(String.valueOf(arrayX-2) +","+ String.valueOf(arrayY-2)))    
        {
           parentString += "J "+ String.valueOf(arrayY)+","+ String.valueOf(arrayX)+" "+String.valueOf(arrayY-2)+","+ String.valueOf(arrayX-2);
           if (manhattanDistanceFromOwnGoal(arrayX-2, arrayY-2, colour) > manhattanDistanceFromOwnGoal(origX, origY, colour) || (manhattanDistanceFromOwnGoal(arrayX-2, arrayY-2, colour) == manhattanDistanceFromOwnGoal(origX, origY, colour) && !homeComing.containsKey(String.valueOf(arrayX-2)+","+String.valueOf(arrayY-2))))
              moves.add (parentString);
         parentString +="\n";
         jumpMovesInitial(colour, board, arrayX-2, arrayY-2, moves,origX,origY, parentString,visited, homeComing, startTime, initialTimeLimit);
         currentTime = System.currentTimeMillis();
        if (currentTime - startTime >= initialTimeLimit-0.5)
            return moves;
         
        }
     // checking north direction 
        if (arrayX -2 >=0 &&  board[arrayX-2][arrayY] =='.' && board[arrayX-1][arrayY] !='.' &&!visited.containsKey(String.valueOf(arrayX-2) +","+ String.valueOf(arrayY)))    
        {
           parentString += "J "+ String.valueOf(arrayY)+","+ String.valueOf(arrayX)+" "+String.valueOf(arrayY)+","+ String.valueOf(arrayX-2);
           if (manhattanDistanceFromOwnGoal(arrayX-2, arrayY, colour) > manhattanDistanceFromOwnGoal(origX, origY, colour) || (manhattanDistanceFromOwnGoal(arrayX-2, arrayY, colour) == manhattanDistanceFromOwnGoal(origX, origY, colour) && !homeComing.containsKey(String.valueOf(arrayX-2)+","+String.valueOf(arrayY))))
                moves.add (parentString);
           parentString +="\n";
           jumpMovesInitial(colour, board, arrayX-2, arrayY, moves,origX,origY, parentString,visited, homeComing, startTime, initialTimeLimit);
           currentTime = System.currentTimeMillis();
           if (currentTime - startTime >= initialTimeLimit-0.5)
             return moves;  
        }     
     
     // checking west direction
        if (arrayY-2 >=0 && board[arrayX][arrayY-2] =='.' && board[arrayX][arrayY-1]!='.' &&!visited.containsKey(String.valueOf(arrayX) + ","+String.valueOf(arrayY-2)))    
        {
           parentString += "J "+ String.valueOf(arrayY)+","+ String.valueOf(arrayX)+" "+String.valueOf(arrayY-2)+","+ String.valueOf(arrayX);
           if (manhattanDistanceFromOwnGoal(arrayX, arrayY-2, colour) > manhattanDistanceFromOwnGoal(origX, origY, colour) || (manhattanDistanceFromOwnGoal(arrayX, arrayY-2, colour) == manhattanDistanceFromOwnGoal(origX, origY, colour) && !homeComing.containsKey(String.valueOf(arrayX)+","+String.valueOf(arrayY-2))))
              moves.add (parentString);
                  parentString +="\n";
           jumpMovesInitial(colour, board, arrayX, arrayY-2, moves,origX,origY, parentString,visited, homeComing, startTime, initialTimeLimit);
           currentTime = System.currentTimeMillis();
        if (currentTime - startTime >= initialTimeLimit-0.5)
            return moves;
        
        }
 
        
        
     // checking northeast direction
       if (arrayX -2 >=0 && arrayY+2 <=15 && board[arrayX-2][arrayY+2] =='.' && board[arrayX-1][arrayY+1]!='.' &&!visited.containsKey(String.valueOf(arrayX-2) +","+ String.valueOf(arrayY+2)))    
        {
           parentString += "J "+ String.valueOf(arrayY)+","+ String.valueOf(arrayX)+" "+String.valueOf(arrayY+2)+","+ String.valueOf(arrayX-2);
          if (manhattanDistanceFromOwnGoal(arrayX-2, arrayY+2, colour) > manhattanDistanceFromOwnGoal(origX, origY, colour) || (manhattanDistanceFromOwnGoal(arrayX-2, arrayY+2, colour) == manhattanDistanceFromOwnGoal(origX, origY, colour) && !homeComing.containsKey(String.valueOf(arrayX-2)+","+String.valueOf(arrayY+2))))
          moves.add (parentString);
              parentString +="\n";
           jumpMovesInitial(colour, board, arrayX-2, arrayY+2, moves,origX,origY, parentString,visited, homeComing, startTime, initialTimeLimit);
        currentTime = System.currentTimeMillis();
        if (currentTime - startTime >= initialTimeLimit-0.5)
            return moves;
        }
       
       // checking east direction
        if (arrayY+2 <=15 && board[arrayX][arrayY+2] =='.' && board[arrayX][arrayY+1]!='.' &&!visited.containsKey(String.valueOf(arrayX) +","+ String.valueOf(arrayY+2)) )    
        {
            parentString += "J "+ String.valueOf(arrayY)+","+ String.valueOf(arrayX)+" "+String.valueOf(arrayY+2)+","+ String.valueOf(arrayX);
              if (manhattanDistanceFromOwnGoal(arrayX, arrayY+2, colour) > manhattanDistanceFromOwnGoal(origX, origY, colour) || (manhattanDistanceFromOwnGoal(arrayX, arrayY+2, colour) == manhattanDistanceFromOwnGoal(origX, origY, colour) && !homeComing.containsKey(String.valueOf(arrayX)+","+String.valueOf(arrayY+2))))
            moves.add (parentString);
              parentString +="\n";
            jumpMovesInitial(colour, board, arrayX, arrayY+2, moves,origX,origY, parentString,visited, homeComing, startTime, initialTimeLimit);
            currentTime = System.currentTimeMillis();
        if (currentTime - startTime >= initialTimeLimit-0.5)
            return moves;
        }
       
               // checking south direction    
        if (arrayX +2 <=15  && board[arrayX+2][arrayY] =='.' && board[arrayX+1][arrayY] !='.' && !visited.containsKey(String.valueOf(arrayX+2) +","+ String.valueOf(arrayY)))    
        {
           parentString += "J "+ String.valueOf(arrayY)+","+ String.valueOf(arrayX)+" "+String.valueOf(arrayY)+","+ String.valueOf(arrayX+2);
             if (manhattanDistanceFromOwnGoal(arrayX+2, arrayY, colour) > manhattanDistanceFromOwnGoal(origX, origY, colour) || (manhattanDistanceFromOwnGoal(arrayX+2, arrayY, colour) == manhattanDistanceFromOwnGoal(origX, origY, colour) && !homeComing.containsKey(String.valueOf(arrayX+2)+","+String.valueOf(arrayY))))
            moves.add (parentString);
                 parentString +="\n";
           jumpMovesInitial(colour, board, arrayX+2, arrayY, moves,origX,origY, parentString,visited, homeComing,startTime, initialTimeLimit);
           currentTime = System.currentTimeMillis();
        if (currentTime - startTime >= initialTimeLimit-0.5)
            return moves;
        }
        
             
        // checking south west direction
        if (arrayX +2 <=15 && arrayY-2 >=0 && board[arrayX+2][arrayY-2] =='.' && board[arrayX+1][arrayY-1]!='.' &&!visited.containsKey(String.valueOf(arrayX+2) +","+ String.valueOf(arrayY-2)) )    
          {
                parentString += "J "+ String.valueOf(arrayY)+","+ String.valueOf(arrayX)+" "+String.valueOf(arrayY-2)+","+ String.valueOf(arrayX+2);
           if (manhattanDistanceFromOwnGoal(arrayX+2, arrayY-2, colour) > manhattanDistanceFromOwnGoal(origX, origY, colour) || (manhattanDistanceFromOwnGoal(arrayX+2, arrayY-2, colour) == manhattanDistanceFromOwnGoal(origX, origY, colour) && !homeComing.containsKey(String.valueOf(arrayX+2)+","+String.valueOf(arrayY-2))))
                   moves.add (parentString);
                       parentString +="\n";
                jumpMovesInitial(colour, board, arrayX+2, arrayY-2, moves,origX,origY, parentString,visited, homeComing, startTime, initialTimeLimit);
                currentTime = System.currentTimeMillis();
        if (currentTime - startTime >= initialTimeLimit-0.5)
            return moves;
          }
        
        // checking south east direction
        if (arrayX +2 <=15 && arrayY+2 <=15 && board[arrayX+2][arrayY+2] =='.' && board[arrayX+1][arrayY+1]!='.' &&!visited.containsKey(String.valueOf(arrayX+2) +","+ String.valueOf(arrayY+2)) )    
        {
            parentString += "J "+ String.valueOf(arrayY)+","+ String.valueOf(arrayX)+" "+String.valueOf(arrayY+2)+","+ String.valueOf(arrayX+2);
            if (manhattanDistanceFromOwnGoal(arrayX+2, arrayY+2, colour) > manhattanDistanceFromOwnGoal(origX, origY, colour) || (manhattanDistanceFromOwnGoal(arrayX+2, arrayY+2, colour) == manhattanDistanceFromOwnGoal(origX, origY, colour) && !homeComing.containsKey(String.valueOf(arrayX+2)+","+String.valueOf(arrayY+2))))
                moves.add (parentString);
            parentString +="\n";
           jumpMovesInitial(colour, board, arrayX+2, arrayY+2, moves,origX,origY, parentString,visited, homeComing, startTime, initialTimeLimit);
           currentTime = System.currentTimeMillis();
           if (currentTime - startTime >= initialTimeLimit-0.5)
            return moves;
        }
           
        }
        
        
        else
        {           
            // checking south east
            if (arrayX +2 <=15 && arrayY+2 <=15 && board[arrayX+2][arrayY+2] =='.' && board[arrayX+1][arrayY+1]!='.' &&!visited.containsKey(String.valueOf(arrayX+2) +","+ String.valueOf(arrayY+2)) )    
        {
            parentString += "J "+ String.valueOf(arrayY)+","+ String.valueOf(arrayX)+" "+String.valueOf(arrayY+2)+","+ String.valueOf(arrayX+2);
            if (manhattanDistanceFromOwnGoal(arrayX+2, arrayY+2, colour) > manhattanDistanceFromOwnGoal(origX, origY, colour) || (manhattanDistanceFromOwnGoal(arrayX+2, arrayY+2, colour) == manhattanDistanceFromOwnGoal(origX, origY, colour) && !homeComing.containsKey(String.valueOf(arrayX+2)+","+String.valueOf(arrayY+2))))
                moves.add (parentString);
            parentString +="\n";
           jumpMovesInitial(colour, board, arrayX+2, arrayY+2, moves,origX,origY, parentString,visited, homeComing, startTime, initialTimeLimit);
           currentTime = System.currentTimeMillis();
           if (currentTime - startTime >= initialTimeLimit-0.5)
            return moves;
        }
            
         // checking south direction    
        if (arrayX +2 <=15  && board[arrayX+2][arrayY] =='.' && board[arrayX+1][arrayY] !='.' && !visited.containsKey(String.valueOf(arrayX+2) +","+ String.valueOf(arrayY)))    
        {
           parentString += "J "+ String.valueOf(arrayY)+","+ String.valueOf(arrayX)+" "+String.valueOf(arrayY)+","+ String.valueOf(arrayX+2);
             if (manhattanDistanceFromOwnGoal(arrayX+2, arrayY, colour) > manhattanDistanceFromOwnGoal(origX, origY, colour) || (manhattanDistanceFromOwnGoal(arrayX+2, arrayY, colour) == manhattanDistanceFromOwnGoal(origX, origY, colour) && !homeComing.containsKey(String.valueOf(arrayX+2)+","+String.valueOf(arrayY))))
            moves.add (parentString);
                 parentString +="\n";
           jumpMovesInitial(colour, board, arrayX+2, arrayY, moves,origX,origY, parentString,visited, homeComing,startTime, initialTimeLimit);
           currentTime = System.currentTimeMillis();
        if (currentTime - startTime >= initialTimeLimit-0.5)
            return moves;
        }   
             // checking east direction
        if (arrayY+2 <=15 && board[arrayX][arrayY+2] =='.' && board[arrayX][arrayY+1]!='.' &&!visited.containsKey(String.valueOf(arrayX) +","+ String.valueOf(arrayY+2)) )    
        {
            parentString += "J "+ String.valueOf(arrayY)+","+ String.valueOf(arrayX)+" "+String.valueOf(arrayY+2)+","+ String.valueOf(arrayX);
              if (manhattanDistanceFromOwnGoal(arrayX, arrayY+2, colour) > manhattanDistanceFromOwnGoal(origX, origY, colour) || (manhattanDistanceFromOwnGoal(arrayX, arrayY+2, colour) == manhattanDistanceFromOwnGoal(origX, origY, colour) && !homeComing.containsKey(String.valueOf(arrayX)+","+String.valueOf(arrayY+2))))
            moves.add (parentString);
              parentString +="\n";
            jumpMovesInitial(colour, board, arrayX, arrayY+2, moves,origX,origY, parentString,visited, homeComing, startTime, initialTimeLimit);
            currentTime = System.currentTimeMillis();
        if (currentTime - startTime >= initialTimeLimit-0.5)
            return moves;
        }  
         
                 // checking south west direction
        if (arrayX +2 <=15 && arrayY-2 >=0 && board[arrayX+2][arrayY-2] =='.' && board[arrayX+1][arrayY-1]!='.' &&!visited.containsKey(String.valueOf(arrayX+2) +","+ String.valueOf(arrayY-2)) )    
          {
                parentString += "J "+ String.valueOf(arrayY)+","+ String.valueOf(arrayX)+" "+String.valueOf(arrayY-2)+","+ String.valueOf(arrayX+2);
           if (manhattanDistanceFromOwnGoal(arrayX+2, arrayY-2, colour) > manhattanDistanceFromOwnGoal(origX, origY, colour) || (manhattanDistanceFromOwnGoal(arrayX+2, arrayY-2, colour) == manhattanDistanceFromOwnGoal(origX, origY, colour) && !homeComing.containsKey(String.valueOf(arrayX+2)+","+String.valueOf(arrayY-2))))
                   moves.add (parentString);
                       parentString +="\n";
                jumpMovesInitial(colour, board, arrayX+2, arrayY-2, moves,origX,origY, parentString,visited, homeComing, startTime, initialTimeLimit);
                currentTime = System.currentTimeMillis();
        if (currentTime - startTime >= initialTimeLimit-0.5)
            return moves;
          }
        
            // checking west direction
        if (arrayY-2 >=0 && board[arrayX][arrayY-2] =='.' && board[arrayX][arrayY-1]!='.' &&!visited.containsKey(String.valueOf(arrayX) + ","+String.valueOf(arrayY-2)))    
        {
           parentString += "J "+ String.valueOf(arrayY)+","+ String.valueOf(arrayX)+" "+String.valueOf(arrayY-2)+","+ String.valueOf(arrayX);
           if (manhattanDistanceFromOwnGoal(arrayX, arrayY-2, colour) > manhattanDistanceFromOwnGoal(origX, origY, colour) || (manhattanDistanceFromOwnGoal(arrayX, arrayY-2, colour) == manhattanDistanceFromOwnGoal(origX, origY, colour) && !homeComing.containsKey(String.valueOf(arrayX)+","+String.valueOf(arrayY-2))))
              moves.add (parentString);
                  parentString +="\n";
           jumpMovesInitial(colour, board, arrayX, arrayY-2, moves,origX,origY, parentString,visited, homeComing, startTime, initialTimeLimit);
           currentTime = System.currentTimeMillis();
        if (currentTime - startTime >= initialTimeLimit-0.5)
            return moves;
        
        }
 
         // checking north direction 
        if (arrayX -2 >=0 &&  board[arrayX-2][arrayY] =='.' && board[arrayX-1][arrayY] !='.' &&!visited.containsKey(String.valueOf(arrayX-2) +","+ String.valueOf(arrayY)))    
        {
           parentString += "J "+ String.valueOf(arrayY)+","+ String.valueOf(arrayX)+" "+String.valueOf(arrayY)+","+ String.valueOf(arrayX-2);
           if (manhattanDistanceFromOwnGoal(arrayX-2, arrayY, colour) > manhattanDistanceFromOwnGoal(origX, origY, colour) || (manhattanDistanceFromOwnGoal(arrayX-2, arrayY, colour) == manhattanDistanceFromOwnGoal(origX, origY, colour) && !homeComing.containsKey(String.valueOf(arrayX-2)+","+String.valueOf(arrayY))))
                moves.add (parentString);
           parentString +="\n";
           jumpMovesInitial(colour, board, arrayX-2, arrayY, moves,origX,origY, parentString,visited, homeComing, startTime, initialTimeLimit);
           currentTime = System.currentTimeMillis();
           if (currentTime - startTime >= initialTimeLimit-0.5)
             return moves;  
        }     
     
        
             // checking northeast direction
       if (arrayX -2 >=0 && arrayY+2 <=15 && board[arrayX-2][arrayY+2] =='.' && board[arrayX-1][arrayY+1]!='.' &&!visited.containsKey(String.valueOf(arrayX-2) +","+ String.valueOf(arrayY+2)))    
        {
           parentString += "J "+ String.valueOf(arrayY)+","+ String.valueOf(arrayX)+" "+String.valueOf(arrayY+2)+","+ String.valueOf(arrayX-2);
          if (manhattanDistanceFromOwnGoal(arrayX-2, arrayY+2, colour) > manhattanDistanceFromOwnGoal(origX, origY, colour) || (manhattanDistanceFromOwnGoal(arrayX-2, arrayY+2, colour) == manhattanDistanceFromOwnGoal(origX, origY, colour) && !homeComing.containsKey(String.valueOf(arrayX-2)+","+String.valueOf(arrayY+2))))
          moves.add (parentString);
              parentString +="\n";
           jumpMovesInitial(colour, board, arrayX-2, arrayY+2, moves,origX,origY, parentString,visited, homeComing, startTime, initialTimeLimit);
        currentTime = System.currentTimeMillis();
        if (currentTime - startTime >= initialTimeLimit-0.5)
            return moves;
        }
       
        
          // checking north west direction 
        if (arrayX -2 >=0 && arrayY-2 >=0 && board[arrayX-2][arrayY-2] =='.' && board[arrayX-1][arrayY-1]!='.' && !visited.containsKey(String.valueOf(arrayX-2) +","+ String.valueOf(arrayY-2)))    
        {
           parentString += "J "+ String.valueOf(arrayY)+","+ String.valueOf(arrayX)+" "+String.valueOf(arrayY-2)+","+ String.valueOf(arrayX-2);
           if (manhattanDistanceFromOwnGoal(arrayX-2, arrayY-2, colour) > manhattanDistanceFromOwnGoal(origX, origY, colour) || (manhattanDistanceFromOwnGoal(arrayX-2, arrayY-2, colour) == manhattanDistanceFromOwnGoal(origX, origY, colour) && !homeComing.containsKey(String.valueOf(arrayX-2)+","+String.valueOf(arrayY-2))))
              moves.add (parentString);
         parentString +="\n";
         jumpMovesInitial(colour, board, arrayX-2, arrayY-2, moves,origX,origY, parentString,visited, homeComing, startTime, initialTimeLimit);
         currentTime = System.currentTimeMillis();
        if (currentTime - startTime >= initialTimeLimit-0.5)
            return moves;
         
        }
        
        }
               
    //    System.out.println("exiting jumpMovesInitial()");
      return moves;  
    }
    
    
    
    
    // Filtering those moves of pawn that are inside the opponent area
    
    public static ArrayList<String> opponentAreaMoves(String colour, char[][] board, int arrayX, int arrayY, ArrayList<String> moves, int origX, int origY, String parentString, HashMap<String, Integer> visited, long startTime, float initialTimeLimit, HashMap<String, Integer> goalCoords)
    {
        
    //    System.out.println("inside opponentAreaMoves()");
       visited.put (String.valueOf(arrayX)+","+String.valueOf(arrayY),1);
        long currentTime = System.currentTimeMillis();
        if (currentTime - startTime >= initialTimeLimit-0.5)
            return moves;
        
       if (colour.equals("WHITE"))
       {
        
        // Prioritizing jumps 
        
        // checking north west direction 
        if (arrayX -2 >=0 && arrayY-2 >=0 && board[arrayX-2][arrayY-2] =='.' && board[arrayX-1][arrayY-1]!='.' && !visited.containsKey(String.valueOf(arrayX-2) +","+ String.valueOf(arrayY-2)))    
        {
            parentString += "J "+ String.valueOf(arrayY)+","+ String.valueOf(arrayX)+" "+String.valueOf(arrayY-2)+","+ String.valueOf(arrayX-2);
           if (goalCoords.containsKey(String.valueOf(arrayX-2)+","+ String.valueOf(arrayY-2)))
               moves.add (parentString);
           
           
        parentString +="\n";
        opponentAreaMoves(colour, board, arrayX-2, arrayY-2, moves,origX,origY, parentString,visited, startTime, initialTimeLimit, goalCoords);
        currentTime = System.currentTimeMillis();
               if (currentTime - startTime >= initialTimeLimit-0.5)
                   return moves;     
        }
        
     // checking north direction 
        if (arrayX -2 >=0 &&  board[arrayX-2][arrayY] =='.' && board[arrayX-1][arrayY]!='.' &&!visited.containsKey(String.valueOf(arrayX-2) +","+ String.valueOf(arrayY)))    
        {
          parentString += "J "+ String.valueOf(arrayY)+","+ String.valueOf(arrayX)+" "+String.valueOf(arrayY)+","+ String.valueOf(arrayX-2);
           if (goalCoords.containsKey(String.valueOf(arrayX-2)+","+ String.valueOf(arrayY)))
               moves.add (parentString);
          parentString +="\n";
          
          opponentAreaMoves(colour, board, arrayX-2, arrayY, moves,origX,origY, parentString,visited,startTime,initialTimeLimit, goalCoords);
          currentTime = System.currentTimeMillis();
          if (currentTime - startTime >= initialTimeLimit-0.5)
            return moves;      
          
        }     
     
        // checking west direction
        if (arrayY-2 >=0 && board[arrayX][arrayY-2] =='.' && board[arrayX][arrayY-1]!='.' &&!visited.containsKey(String.valueOf(arrayX) + ","+String.valueOf(arrayY-2)))    
        {
           parentString += "J "+ String.valueOf(arrayY)+","+ String.valueOf(arrayX)+" "+String.valueOf(arrayY-2)+","+ String.valueOf(arrayX);
           if (goalCoords.containsKey(String.valueOf(arrayX)+","+ String.valueOf(arrayY-2)))
              moves.add (parentString);
                  parentString +="\n";
           opponentAreaMoves(colour, board, arrayX, arrayY-2, moves,origX,origY, parentString,visited,startTime, initialTimeLimit, goalCoords);
           currentTime = System.currentTimeMillis();
               if (currentTime - startTime >= initialTimeLimit-0.5)
                   return moves;     
        }
 

     // checking northeast direction
       if (arrayX -2 >=0 && arrayY+2 <=15 && board[arrayX-2][arrayY+2] =='.' && board[arrayX-1][arrayY+1]!='.' &&!visited.containsKey(String.valueOf(arrayX-2) +","+ String.valueOf(arrayY+2)))    
        {
           parentString += "J "+ String.valueOf(arrayY)+","+ String.valueOf(arrayX)+" "+String.valueOf(arrayY+2)+","+ String.valueOf(arrayX-2);
           if (goalCoords.containsKey(String.valueOf(arrayX-2)+","+ String.valueOf(arrayY+2)))
          moves.add (parentString);
              parentString +="\n";
          opponentAreaMoves(colour, board, arrayX-2, arrayY+2, moves,origX,origY, parentString,visited, startTime, initialTimeLimit, goalCoords);
           currentTime = System.currentTimeMillis();
               if (currentTime - startTime >= initialTimeLimit-0.5)
                   return moves;
        }
       
       // checking east direction
        if (arrayY+2 <=15 && board[arrayX][arrayY+2] =='.' && board[arrayX][arrayY+1]!='.' &&!visited.containsKey(String.valueOf(arrayX) +","+ String.valueOf(arrayY+2)) )    
        {
           parentString += "J "+ String.valueOf(arrayY)+","+ String.valueOf(arrayX)+" "+String.valueOf(arrayY+2)+","+ String.valueOf(arrayX);
           if (goalCoords.containsKey(String.valueOf(arrayX)+","+ String.valueOf(arrayY+2)))
       
           moves.add (parentString);
              parentString +="\n";             
           opponentAreaMoves(colour, board, arrayX, arrayY+2, moves,origX,origY, parentString,visited, startTime, initialTimeLimit, goalCoords);
               currentTime = System.currentTimeMillis();
               if (currentTime - startTime >= initialTimeLimit-0.5)
                   return moves; 
        }
       
  
        
        // checking south direction
        
        if (arrayX +2 <=15  && board[arrayX+2][arrayY] =='.' && board[arrayX+1][arrayY] !='.' && !visited.containsKey(String.valueOf(arrayX+2) +","+ String.valueOf(arrayY)))    
        {
           parentString += "J "+ String.valueOf(arrayY)+","+ String.valueOf(arrayX)+" "+String.valueOf(arrayY)+","+ String.valueOf(arrayX+2);
           if (goalCoords.containsKey(String.valueOf(arrayX+2)+","+ String.valueOf(arrayY)))
           moves.add (parentString);
                 parentString +="\n";
           opponentAreaMoves(colour, board, arrayX+2, arrayY, moves,origX,origY, parentString,visited, startTime,initialTimeLimit, goalCoords);    
                currentTime = System.currentTimeMillis();
               if (currentTime - startTime >= initialTimeLimit-0.5)
                   return moves;
        }
        
        // checking south west direction
        if (arrayX +2 <=15 && arrayY-2 >=0 && board[arrayX+2][arrayY-2] =='.' && board[arrayX+1][arrayY-1]!='.' &&!visited.containsKey(String.valueOf(arrayX+2) +","+ String.valueOf(arrayY-2)) )    
          {
           parentString += "J "+ String.valueOf(arrayY)+","+ String.valueOf(arrayX)+" "+String.valueOf(arrayY-2)+","+ String.valueOf(arrayX+2);
           if (goalCoords.containsKey(String.valueOf(arrayX+2)+","+ String.valueOf(arrayY-2)))

                moves.add (parentString);
                 parentString +="\n";
                opponentAreaMoves(colour, board, arrayX+2, arrayY-2, moves,origX,origY, parentString,visited,  startTime, initialTimeLimit, goalCoords);
        currentTime = System.currentTimeMillis();
               if (currentTime - startTime >= initialTimeLimit-0.5)
                   return moves;
          }
        
        
        
              // checking south east direction
        if (arrayX +2 <=15 && arrayY+2 <=15 && board[arrayX+2][arrayY+2] =='.' && board[arrayX+1][arrayY+1]!='.' &&!visited.containsKey(String.valueOf(arrayX+2) +","+ String.valueOf(arrayY+2)) )    
        {
           parentString += "J "+ String.valueOf(arrayY)+","+ String.valueOf(arrayX)+" "+String.valueOf(arrayY+2)+","+ String.valueOf(arrayX+2);
           if (goalCoords.containsKey(String.valueOf(arrayX+2)+","+ String.valueOf(arrayY+2)))

             moves.add (parentString);
                parentString +="\n";
           opponentAreaMoves(colour, board, arrayX+2, arrayY+2, moves,origX,origY, parentString,visited, startTime, initialTimeLimit, goalCoords);
           currentTime = System.currentTimeMillis();
               if (currentTime - startTime >= initialTimeLimit-0.5)
                   return moves;
        }
        
        
        
       }
   
       
       else
       {
           
                   // checking south east direction
        if (arrayX +2 <=15 && arrayY+2 <=15 && board[arrayX+2][arrayY+2] =='.' && board[arrayX+1][arrayY+1]!='.' &&!visited.containsKey(String.valueOf(arrayX+2) +","+ String.valueOf(arrayY+2)) )    
        {
           parentString += "J "+ String.valueOf(arrayY)+","+ String.valueOf(arrayX)+" "+String.valueOf(arrayY+2)+","+ String.valueOf(arrayX+2);
           if (goalCoords.containsKey(String.valueOf(arrayX+2)+","+ String.valueOf(arrayY+2)))

             moves.add (parentString);
                parentString +="\n";
           opponentAreaMoves(colour, board, arrayX+2, arrayY+2, moves,origX,origY, parentString,visited, startTime, initialTimeLimit, goalCoords);
           currentTime = System.currentTimeMillis();
               if (currentTime - startTime >= initialTimeLimit-0.5)
                   return moves;
        }
           
           
           
                   // checking south direction
        
        if (arrayX +2 <=15  && board[arrayX+2][arrayY] =='.' && board[arrayX+1][arrayY] !='.' && !visited.containsKey(String.valueOf(arrayX+2) +","+ String.valueOf(arrayY)))    
        {
           parentString += "J "+ String.valueOf(arrayY)+","+ String.valueOf(arrayX)+" "+String.valueOf(arrayY)+","+ String.valueOf(arrayX+2);
           if (goalCoords.containsKey(String.valueOf(arrayX+2)+","+ String.valueOf(arrayY)))
           moves.add (parentString);
                 parentString +="\n";
           opponentAreaMoves(colour, board, arrayX+2, arrayY, moves,origX,origY, parentString,visited, startTime,initialTimeLimit, goalCoords);    
                currentTime = System.currentTimeMillis();
               if (currentTime - startTime >= initialTimeLimit-0.5)
                   return moves;
        }
        
           
                // checking east direction
        if (arrayY+2 <=15 && board[arrayX][arrayY+2] =='.' && board[arrayX][arrayY+1]!='.' &&!visited.containsKey(String.valueOf(arrayX) +","+ String.valueOf(arrayY+2)) )    
        {
           parentString += "J "+ String.valueOf(arrayY)+","+ String.valueOf(arrayX)+" "+String.valueOf(arrayY+2)+","+ String.valueOf(arrayX);
           if (goalCoords.containsKey(String.valueOf(arrayX)+","+ String.valueOf(arrayY+2)))
       
           moves.add (parentString);
              parentString +="\n";             
           opponentAreaMoves(colour, board, arrayX, arrayY+2, moves,origX,origY, parentString,visited, startTime, initialTimeLimit, goalCoords);
               currentTime = System.currentTimeMillis();
               if (currentTime - startTime >= initialTimeLimit-0.5)
                   return moves; 
        }  
           
           
                // checking south west direction
        if (arrayX +2 <=15 && arrayY-2 >=0 && board[arrayX+2][arrayY-2] =='.' && board[arrayX+1][arrayY-1]!='.' &&!visited.containsKey(String.valueOf(arrayX+2) +","+ String.valueOf(arrayY-2)) )    
          {
           parentString += "J "+ String.valueOf(arrayY)+","+ String.valueOf(arrayX)+" "+String.valueOf(arrayY-2)+","+ String.valueOf(arrayX+2);
           if (goalCoords.containsKey(String.valueOf(arrayX+2)+","+ String.valueOf(arrayY-2)))

                moves.add (parentString);
                 parentString +="\n";
                opponentAreaMoves(colour, board, arrayX+2, arrayY-2, moves,origX,origY, parentString,visited,  startTime, initialTimeLimit, goalCoords);
        currentTime = System.currentTimeMillis();
               if (currentTime - startTime >= initialTimeLimit-0.5)
                   return moves;
          }
        
        
                // checking west direction
        if (arrayY-2 >=0 && board[arrayX][arrayY-2] =='.' && board[arrayX][arrayY-1]!='.' &&!visited.containsKey(String.valueOf(arrayX) + ","+String.valueOf(arrayY-2)))    
        {
           parentString += "J "+ String.valueOf(arrayY)+","+ String.valueOf(arrayX)+" "+String.valueOf(arrayY-2)+","+ String.valueOf(arrayX);
           if (goalCoords.containsKey(String.valueOf(arrayX)+","+ String.valueOf(arrayY-2)))
              moves.add (parentString);
                  parentString +="\n";
           opponentAreaMoves(colour, board, arrayX, arrayY-2, moves,origX,origY, parentString,visited,startTime, initialTimeLimit, goalCoords);
           currentTime = System.currentTimeMillis();
               if (currentTime - startTime >= initialTimeLimit-0.5)
                   return moves;     
        }
                // checking north direction
    
                 if (arrayX -2 >=0 &&  board[arrayX-2][arrayY] =='.' && board[arrayX-1][arrayY]!='.' &&!visited.containsKey(String.valueOf(arrayX-2) +","+ String.valueOf(arrayY)))    
        {
          parentString += "J "+ String.valueOf(arrayY)+","+ String.valueOf(arrayX)+" "+String.valueOf(arrayY)+","+ String.valueOf(arrayX-2);
           if (goalCoords.containsKey(String.valueOf(arrayX-2)+","+ String.valueOf(arrayY)))
               moves.add (parentString);
          parentString +="\n";
          
          opponentAreaMoves(colour, board, arrayX-2, arrayY, moves,origX,origY, parentString,visited,startTime,initialTimeLimit, goalCoords);
          currentTime = System.currentTimeMillis();
          if (currentTime - startTime >= initialTimeLimit-0.5)
            return moves;      
          
        }
                 
                 
                      // checking northeast direction
       if (arrayX -2 >=0 && arrayY+2 <=15 && board[arrayX-2][arrayY+2] =='.' && board[arrayX-1][arrayY+1]!='.' &&!visited.containsKey(String.valueOf(arrayX-2) +","+ String.valueOf(arrayY+2)))    
        {
           parentString += "J "+ String.valueOf(arrayY)+","+ String.valueOf(arrayX)+" "+String.valueOf(arrayY+2)+","+ String.valueOf(arrayX-2);
           if (goalCoords.containsKey(String.valueOf(arrayX-2)+","+ String.valueOf(arrayY+2)))
          moves.add (parentString);
              parentString +="\n";
          opponentAreaMoves(colour, board, arrayX-2, arrayY+2, moves,origX,origY, parentString,visited, startTime, initialTimeLimit, goalCoords);
           currentTime = System.currentTimeMillis();
               if (currentTime - startTime >= initialTimeLimit-0.5)
                   return moves;
        }
       
              
       // checking north west direction 
        if (arrayX -2 >=0 && arrayY-2 >=0 && board[arrayX-2][arrayY-2] =='.' && board[arrayX-1][arrayY-1]!='.' && !visited.containsKey(String.valueOf(arrayX-2) +","+ String.valueOf(arrayY-2)))    
        {
            parentString += "J "+ String.valueOf(arrayY)+","+ String.valueOf(arrayX)+" "+String.valueOf(arrayY-2)+","+ String.valueOf(arrayX-2);
           if (goalCoords.containsKey(String.valueOf(arrayX-2)+","+ String.valueOf(arrayY-2)))
               moves.add (parentString);
           
           
        parentString +="\n";
        opponentAreaMoves(colour, board, arrayX-2, arrayY-2, moves,origX,origY, parentString,visited, startTime, initialTimeLimit, goalCoords);
        currentTime = System.currentTimeMillis();
               if (currentTime - startTime >= initialTimeLimit-0.5)
                   return moves;     
        }
                             
       }
              
      return moves;          
    } 
    
   
    
    
    
  public static ArrayList<String> jumpMovesOutsideHome(String colour, char[][] board, int arrayX, int arrayY, ArrayList<String> moves, int origX, int origY, String parentString, HashMap<String,Integer> visited, HashMap<String, Integer> homeComing, long startTime, float initialTimeLimit) 
    {
    //    System.out.println("inside jumpMovesOutsideHome");
        // marking the coords as visited so that I dont return back to these coords again    
        visited.put (String.valueOf(arrayX)+","+String.valueOf(arrayY),1);
        long currentTime = System.currentTimeMillis();
        if (currentTime - startTime >= initialTimeLimit-0.5)
            return moves;
        
        if (colour.equals ("WHITE"))
        {      
        // checking north west direction 
        if (arrayX -2 >=0 && arrayY-2 >=0 && board[arrayX-2][arrayY-2] =='.' && board[arrayX-1][arrayY-1]!='.' && !visited.containsKey(String.valueOf(arrayX-2) +","+ String.valueOf(arrayY-2)))    
        {
            parentString += "J "+ String.valueOf(arrayY)+","+ String.valueOf(arrayX)+" "+String.valueOf(arrayY-2)+","+ String.valueOf(arrayX-2);
            if ( !homeComing.containsKey(String.valueOf(arrayX-2)+","+String.valueOf(arrayY-2)))
                moves.add (parentString);
        parentString +="\n";
        jumpMovesOutsideHome(colour, board, arrayX-2, arrayY-2, moves,origX,origY, parentString,visited, homeComing,startTime, initialTimeLimit);
        currentTime = System.currentTimeMillis();
               if (currentTime - startTime >= initialTimeLimit-0.5)
                   return moves;
        }
        
     // checking north direction 
        if (arrayX -2 >=0 &&  board[arrayX-2][arrayY] =='.' && board[arrayX-1][arrayY]!='.' &&!visited.containsKey(String.valueOf(arrayX-2) +","+ String.valueOf(arrayY)))    
        {
          parentString += "J "+ String.valueOf(arrayY)+","+ String.valueOf(arrayX)+" "+String.valueOf(arrayY)+","+ String.valueOf(arrayX-2);
          if ( !homeComing.containsKey(String.valueOf(arrayX-2)+","+String.valueOf(arrayY)))
                moves.add (parentString);
               parentString +="\n";
          jumpMovesOutsideHome(colour, board, arrayX-2, arrayY, moves,origX,origY, parentString,visited, homeComing, startTime,initialTimeLimit);
          currentTime = System.currentTimeMillis();
          if (currentTime - startTime >= initialTimeLimit-0.5)
            return moves; 
        }     
     
     // checking west direction
        if (arrayY-2 >=0 && board[arrayX][arrayY-2] =='.' && board[arrayX][arrayY-1]!='.' &&!visited.containsKey(String.valueOf(arrayX) + ","+String.valueOf(arrayY-2)))    
        {
           parentString += "J "+ String.valueOf(arrayY)+","+ String.valueOf(arrayX)+" "+String.valueOf(arrayY-2)+","+ String.valueOf(arrayX);
           if (!homeComing.containsKey(String.valueOf(arrayX)+","+String.valueOf(arrayY-2)))
              moves.add (parentString);
                  parentString +="\n";
           jumpMovesOutsideHome(colour, board, arrayX, arrayY-2, moves,origX,origY, parentString,visited, homeComing,startTime, initialTimeLimit);
           currentTime = System.currentTimeMillis();
               if (currentTime - startTime >= initialTimeLimit-0.5)
                   return moves;
        }
 

     // checking northeast direction
       if (arrayX -2 >=0 && arrayY+2 <=15 && board[arrayX-2][arrayY+2] =='.' && board[arrayX-1][arrayY+1]!='.' &&!visited.containsKey(String.valueOf(arrayX-2) +","+ String.valueOf(arrayY+2)))    
        {
           parentString += "J "+ String.valueOf(arrayY)+","+ String.valueOf(arrayX)+" "+String.valueOf(arrayY+2)+","+ String.valueOf(arrayX-2);
          if (!homeComing.containsKey(String.valueOf(arrayX-2)+","+String.valueOf(arrayY+2)))
          moves.add (parentString);
              parentString +="\n";
           jumpMovesOutsideHome(colour, board, arrayX-2, arrayY+2, moves,origX,origY, parentString,visited, homeComing, startTime, initialTimeLimit);
           currentTime = System.currentTimeMillis();
               if (currentTime - startTime >= initialTimeLimit-0.5)
                   return moves;
        }
       
       // checking east direction
        if (arrayY+2 <=15 && board[arrayX][arrayY+2] =='.' && board[arrayX][arrayY+1]!='.' &&!visited.containsKey(String.valueOf(arrayX) +","+ String.valueOf(arrayY+2)) )    
        {
           parentString += "J "+ String.valueOf(arrayY)+","+ String.valueOf(arrayX)+" "+String.valueOf(arrayY+2)+","+ String.valueOf(arrayX);
              if (!homeComing.containsKey(String.valueOf(arrayX)+","+String.valueOf(arrayY+2)))
           moves.add (parentString);
              parentString +="\n";             
           jumpMovesOutsideHome(colour, board, arrayX, arrayY+2, moves,origX,origY, parentString,visited, homeComing, startTime, initialTimeLimit);
                currentTime = System.currentTimeMillis();
               if (currentTime - startTime >= initialTimeLimit-0.5)
                   return moves;
        }
       
 
        
        // checking south direction
        
        if (arrayX +2 <=15  && board[arrayX+2][arrayY] =='.' && board[arrayX+1][arrayY] !='.' && !visited.containsKey(String.valueOf(arrayX+2) +","+ String.valueOf(arrayY)))    
        {
           parentString += "J "+ String.valueOf(arrayY)+","+ String.valueOf(arrayX)+" "+String.valueOf(arrayY)+","+ String.valueOf(arrayX+2);
           if ( !homeComing.containsKey(String.valueOf(arrayX+2)+","+String.valueOf(arrayY)))
           moves.add (parentString);
                 parentString +="\n";
           jumpMovesOutsideHome(colour, board, arrayX+2, arrayY, moves,origX,origY, parentString,visited, homeComing, startTime,initialTimeLimit);    
                currentTime = System.currentTimeMillis();
               if (currentTime - startTime >= initialTimeLimit-0.5)
                   return moves;
        }
        
        // checking south west direction
        if (arrayX +2 <=15 && arrayY-2 >=0 && board[arrayX+2][arrayY-2] =='.' && board[arrayX+1][arrayY-1]!='.' &&!visited.containsKey(String.valueOf(arrayX+2) +","+ String.valueOf(arrayY-2)) )    
          {
           parentString += "J "+ String.valueOf(arrayY)+","+ String.valueOf(arrayX)+" "+String.valueOf(arrayY-2)+","+ String.valueOf(arrayX+2);
           if (!homeComing.containsKey(String.valueOf(arrayX+2)+","+String.valueOf(arrayY-2)))
                moves.add (parentString);
                 parentString +="\n";
                jumpMovesOutsideHome(colour, board, arrayX+2, arrayY-2, moves,origX,origY, parentString,visited, homeComing, startTime, initialTimeLimit);
                 currentTime = System.currentTimeMillis();
               if (currentTime - startTime >= initialTimeLimit-0.5)
                   return moves;
          }
        
               // checking south east direction
        if (arrayX +2 <=15 && arrayY+2 <=15 && board[arrayX+2][arrayY+2] =='.' && board[arrayX+1][arrayY+1]!='.' && !visited.containsKey(String.valueOf(arrayX+2) +","+ String.valueOf(arrayY+2)) )    
        {
           parentString += "J "+ String.valueOf(arrayY)+","+ String.valueOf(arrayX)+" "+String.valueOf(arrayY+2)+","+ String.valueOf(arrayX+2);
            if (!homeComing.containsKey(String.valueOf(arrayX+2)+","+String.valueOf(arrayY+2)))
             moves.add (parentString);
                parentString +="\n";
           jumpMovesOutsideHome(colour, board, arrayX+2, arrayY+2, moves,origX,origY, parentString,visited, homeComing, startTime, initialTimeLimit);
 currentTime = System.currentTimeMillis();
               if (currentTime - startTime >= initialTimeLimit-0.5)
                   return moves;
        }
        
        }
        
   else
        {
            
            // checking south east direction
        if (arrayX +2 <=15 && arrayY+2 <=15 && board[arrayX+2][arrayY+2] =='.' && board[arrayX+1][arrayY+1]!='.' && !visited.containsKey(String.valueOf(arrayX+2) +","+ String.valueOf(arrayY+2)) )    
        {
           parentString += "J "+ String.valueOf(arrayY)+","+ String.valueOf(arrayX)+" "+String.valueOf(arrayY+2)+","+ String.valueOf(arrayX+2);
            if (!homeComing.containsKey(String.valueOf(arrayX+2)+","+String.valueOf(arrayY+2)))
             moves.add (parentString);
                parentString +="\n";
           jumpMovesOutsideHome(colour, board, arrayX+2, arrayY+2, moves,origX,origY, parentString,visited, homeComing, startTime, initialTimeLimit);
 currentTime = System.currentTimeMillis();
               if (currentTime - startTime >= initialTimeLimit-0.5)
                   return moves;
        }
        
        // checking south direction
        
        if (arrayX +2 <=15  && board[arrayX+2][arrayY] =='.' && board[arrayX+1][arrayY] !='.' && !visited.containsKey(String.valueOf(arrayX+2) +","+ String.valueOf(arrayY)))    
        {
           parentString += "J "+ String.valueOf(arrayY)+","+ String.valueOf(arrayX)+" "+String.valueOf(arrayY)+","+ String.valueOf(arrayX+2);
           if ( !homeComing.containsKey(String.valueOf(arrayX+2)+","+String.valueOf(arrayY)))
           moves.add (parentString);
                 parentString +="\n";
           jumpMovesOutsideHome(colour, board, arrayX+2, arrayY, moves,origX,origY, parentString,visited, homeComing, startTime,initialTimeLimit);    
                currentTime = System.currentTimeMillis();
               if (currentTime - startTime >= initialTimeLimit-0.5)
                   return moves;
        }
            
      
        
         // checking east direction
        if (arrayY+2 <=15 && board[arrayX][arrayY+2] =='.' && board[arrayX][arrayY+1]!='.' &&!visited.containsKey(String.valueOf(arrayX) +","+ String.valueOf(arrayY+2)) )    
        {
           parentString += "J "+ String.valueOf(arrayY)+","+ String.valueOf(arrayX)+" "+String.valueOf(arrayY+2)+","+ String.valueOf(arrayX);
              if (!homeComing.containsKey(String.valueOf(arrayX)+","+String.valueOf(arrayY+2)))
           moves.add (parentString);
              parentString +="\n";             
           jumpMovesOutsideHome(colour, board, arrayX, arrayY+2, moves,origX,origY, parentString,visited, homeComing, startTime, initialTimeLimit);
                currentTime = System.currentTimeMillis();
               if (currentTime - startTime >= initialTimeLimit-0.5)
                   return moves;
        }
        
                // checking south west direction
        if (arrayX +2 <=15 && arrayY-2 >=0 && board[arrayX+2][arrayY-2] =='.' && board[arrayX+1][arrayY-1]!='.' &&!visited.containsKey(String.valueOf(arrayX+2) +","+ String.valueOf(arrayY-2)) )    
          {
           parentString += "J "+ String.valueOf(arrayY)+","+ String.valueOf(arrayX)+" "+String.valueOf(arrayY-2)+","+ String.valueOf(arrayX+2);
           if (!homeComing.containsKey(String.valueOf(arrayX+2)+","+String.valueOf(arrayY-2)))
                moves.add (parentString);
                 parentString +="\n";
                jumpMovesOutsideHome(colour, board, arrayX+2, arrayY-2, moves,origX,origY, parentString,visited, homeComing, startTime, initialTimeLimit);
                 currentTime = System.currentTimeMillis();
               if (currentTime - startTime >= initialTimeLimit-0.5)
                   return moves;
          } 
            
            
             // checking west direction
        if (arrayY-2 >=0 && board[arrayX][arrayY-2] =='.' && board[arrayX][arrayY-1]!='.' &&!visited.containsKey(String.valueOf(arrayX) + ","+String.valueOf(arrayY-2)))    
        {
           parentString += "J "+ String.valueOf(arrayY)+","+ String.valueOf(arrayX)+" "+String.valueOf(arrayY-2)+","+ String.valueOf(arrayX);
           if (!homeComing.containsKey(String.valueOf(arrayX)+","+String.valueOf(arrayY-2)))
              moves.add (parentString);
                  parentString +="\n";
           jumpMovesOutsideHome(colour, board, arrayX, arrayY-2, moves,origX,origY, parentString,visited, homeComing,startTime, initialTimeLimit);
           currentTime = System.currentTimeMillis();
               if (currentTime - startTime >= initialTimeLimit-0.5)
                   return moves;
        }
 
        

     // checking north direction 
        if (arrayX -2 >=0 &&  board[arrayX-2][arrayY] =='.' && board[arrayX-1][arrayY]!='.' &&!visited.containsKey(String.valueOf(arrayX-2) +","+ String.valueOf(arrayY)))    
        {
          parentString += "J "+ String.valueOf(arrayY)+","+ String.valueOf(arrayX)+" "+String.valueOf(arrayY)+","+ String.valueOf(arrayX-2);
          if ( !homeComing.containsKey(String.valueOf(arrayX-2)+","+String.valueOf(arrayY)))
                moves.add (parentString);
               parentString +="\n";
          jumpMovesOutsideHome(colour, board, arrayX-2, arrayY, moves,origX,origY, parentString,visited, homeComing, startTime,initialTimeLimit);
          currentTime = System.currentTimeMillis();
          if (currentTime - startTime >= initialTimeLimit-0.5)
            return moves; 
        }     
     


     // checking northeast direction
       if (arrayX -2 >=0 && arrayY+2 <=15 && board[arrayX-2][arrayY+2] =='.' && board[arrayX-1][arrayY+1]!='.' &&!visited.containsKey(String.valueOf(arrayX-2) +","+ String.valueOf(arrayY+2)))    
        {
           parentString += "J "+ String.valueOf(arrayY)+","+ String.valueOf(arrayX)+" "+String.valueOf(arrayY+2)+","+ String.valueOf(arrayX-2);
          if (!homeComing.containsKey(String.valueOf(arrayX-2)+","+String.valueOf(arrayY+2)))
          moves.add (parentString);
              parentString +="\n";
           jumpMovesOutsideHome(colour, board, arrayX-2, arrayY+2, moves,origX,origY, parentString,visited, homeComing, startTime, initialTimeLimit);
           currentTime = System.currentTimeMillis();
               if (currentTime - startTime >= initialTimeLimit-0.5)
                   return moves;
        }
       
       
                   // checking north west direction 
        if (arrayX -2 >=0 && arrayY-2 >=0 && board[arrayX-2][arrayY-2] =='.' && board[arrayX-1][arrayY-1]!='.' && !visited.containsKey(String.valueOf(arrayX-2) +","+ String.valueOf(arrayY-2)))    
        {
            parentString += "J "+ String.valueOf(arrayY)+","+ String.valueOf(arrayX)+" "+String.valueOf(arrayY-2)+","+ String.valueOf(arrayX-2);
            if ( !homeComing.containsKey(String.valueOf(arrayX-2)+","+String.valueOf(arrayY-2)))
                moves.add (parentString);
        parentString +="\n";
        jumpMovesOutsideHome(colour, board, arrayX-2, arrayY-2, moves,origX,origY, parentString,visited, homeComing,startTime, initialTimeLimit);
        currentTime = System.currentTimeMillis();
               if (currentTime - startTime >= initialTimeLimit-0.5)
                   return moves;
        }
        
            
        }
         
        
      return moves;  
    }
 
               
    
    // RETURNS ARRAY COORDINATES AND NOT CARTESIAN COORDS IN THE FORM OF A HASHMAP
    public static HashMap<String, Integer> homeCoordsHashMap(String colour)
    {
        
        
     //   System.out.println("inside homeCoordsHashMap()");
       HashMap<String, Integer> mapper = new HashMap<String ,Integer>();
        if (colour.equals ("WHITE"))
             {
                 mapper.put ("11,14",1);
                mapper.put ("11,15",1);
                 mapper.put ("12,13",1);
               mapper.put ("12,14",1);   
                mapper.put ("12,15",1);
                 mapper.put ("13,12",1);
              mapper.put ("13,13",1);
               mapper.put ("13,14",1);
                mapper.put ("13,15",1);
                 mapper.put ("14,11",1);
               mapper.put ("14,12",1);
                mapper.put ("14,13",1);
               mapper.put ("14,14",1);
              mapper.put ("14,15",1);
               mapper.put ("15,11",1);
                mapper.put ("15,12",1);
              mapper.put ("15,13",1);
    mapper.put ("15,14",1);           
     mapper.put ("15,15",1);     
     
             }
        else
            {
                
                  mapper.put ("4,0",1);
                   mapper.put ("4,1",1);  
                   
               mapper.put ("3,0",1);
                mapper.put ("3,1",1);
                 mapper.put ("3,2",1);
              
                 mapper.put ("2,0",1);
              mapper.put ("2,1",1);
              mapper.put ("2,2",1);
              mapper.put ("2,3",1);
              
                   mapper.put ("1,0",1);
                 mapper.put ("1,1",1);
                  mapper.put ("1,2",1);
                mapper.put ("1,3",1);
                mapper.put ("1,4",1);
   
                mapper.put ("0,0",1);
               mapper.put ("0,1",1);
             mapper.put ("0,2",1);
              mapper.put ("0,3",1);
               mapper.put ("0,4",1);
             
            }        
     //    System.out.println("exiting homeCoordsHashMap()");
        return mapper;
    }
 
   
    
    
      
     //  RETURNS ARRAY COORDINATES AND NOT CARTESIAN COORDS     
    public static ArrayList<String> homeCoords(String colour)
    {
    //     System.out.println("inside homeCoords()");
        ArrayList<String> listToReturn = new ArrayList<>();
        if (colour.equals ("WHITE"))
             {
               listToReturn.add ("11,14");
               listToReturn.add ("11,15");
               
               listToReturn.add ("12,13");
               listToReturn.add ("12,14");
               listToReturn.add ("12,15");
               
               listToReturn.add ("13,12");
               listToReturn.add ("13,13");
               listToReturn.add ("13,14");
               listToReturn.add ("13,15");
               
               listToReturn.add ("14,11");
               listToReturn.add ("14,12");
               listToReturn.add ("14,13");
               listToReturn.add ("14,14");
               listToReturn.add ("14,15");
               
               listToReturn.add ("15,11");
               listToReturn.add ("15,12");
               listToReturn.add ("15,13");
               listToReturn.add ("15,14");
               listToReturn.add ("15,15");    
             }
        else
            {
                
               listToReturn.add ("4,0");
               listToReturn.add ("4,1");   
               
               
               listToReturn.add ("3,0");
               listToReturn.add ("3,1");
               listToReturn.add ("3,2");
               
               
               listToReturn.add ("2,0");
               listToReturn.add ("2,1");
               listToReturn.add ("2,2");
               listToReturn.add ("2,3");
                      
               listToReturn.add ("1,0");
               listToReturn.add ("1,1");
               listToReturn.add ("1,2");
               listToReturn.add ("1,3");
               listToReturn.add ("1,4");
                          
               listToReturn.add ("0,0");
               listToReturn.add ("0,1");
               listToReturn.add ("0,2");
               listToReturn.add ("0,3");
               listToReturn.add ("0,4");
                         
            }     
    //     System.out.println("exiting homeCoords()");
        return listToReturn;
    }

    
    // This function is for getting the coordinates of the pawns located outside of the home territory 
    public static ArrayList<String> getPossibleMoves(String colour, float initialTimeLimit, char[][] board, long startTime)
    {
        
     //   System.out.println("Inside getPossibleMoves()");
        ArrayList<String> moves = new ArrayList<String>();      
         
        // stores coordinates of home territory
        HashMap<String , Integer > homeCoordinates = homeCoordsHashMap(colour);
        String opponentColour = "";
        if (colour.equals ("WHITE"))
            opponentColour = "BLACK";
        else
            opponentColour = "WHITE";
        
        // stores coordinates of opponents territory
        HashMap<String, Integer> opponentCoordinates = homeCoordsHashMap(opponentColour);
        
        // getting coordinates of all the pawns
        ArrayList<String> allPawnCoordinates = allTheCoords(colour, board);
        
        if (colour.equals("WHITE"))
        {
            for (int i= 0;i< allPawnCoordinates.size();i++)
            {         
               long currentTime = System.currentTimeMillis();
               if (currentTime - startTime >= initialTimeLimit-0.5)
                   return moves;
                
               String[] splitter = allPawnCoordinates.get(i).split(",");
               int arrayX = Integer.parseInt(splitter[0]);
               int arrayY = Integer.parseInt(splitter[1]);
               HashMap<String, Integer> visited = new HashMap<String, Integer>();
               
               // Prioritize jump moves first     
               if (!opponentCoordinates.containsKey(String.valueOf(arrayX)+","+String.valueOf(arrayY)))         
               jumpMovesOutsideHome(colour, board, arrayX, arrayY, moves, arrayX, arrayY, "", visited, homeCoordinates, startTime, initialTimeLimit);
 
               
               currentTime = System.currentTimeMillis();
               if (currentTime - startTime >= initialTimeLimit-0.5)
                   return moves;
               
               if (opponentCoordinates.containsKey(String.valueOf(arrayX)+","+String.valueOf(arrayY)))
                   opponentAreaMoves(colour, board, arrayX, arrayY, moves, arrayX, arrayY, "", visited, startTime, initialTimeLimit, opponentCoordinates);
                   
               
                   currentTime = System.currentTimeMillis();
               if (currentTime - startTime >= initialTimeLimit-0.5)
                   return moves;
                            
               
               // Considering single moves first                          
             
               if (!opponentCoordinates.containsKey(String.valueOf(arrayX)+ ","+String.valueOf(arrayY)))
               {
               
                      // north-west move
               if (arrayX -1 >=0 && arrayY-1 >=0 && board[arrayX-1][arrayY-1]=='.' && !homeCoordinates.containsKey(String.valueOf(arrayX-1)+ ","+ String.valueOf(arrayY-1)))
               {
                 moves.add ("E "+ String.valueOf(arrayY)+","+String.valueOf(arrayX)+" "+ String.valueOf(arrayY-1)+","+ String.valueOf(arrayX-1));                    
               } 
               
                // north move   
               if (arrayX -1 >=0 && board[arrayX-1][arrayY]=='.' && !homeCoordinates.containsKey(String.valueOf(arrayX-1)+ ","+ String.valueOf(arrayY)))
               {
                 moves.add ("E "+ String.valueOf(arrayY)+","+String.valueOf(arrayX)+" "+ String.valueOf(arrayY)+","+ String.valueOf(arrayX-1));            
               }
               
            
               // west move
                if (arrayY -1 >=0 && board[arrayX][arrayY-1] =='.' && !homeCoordinates.containsKey(String.valueOf(arrayX)+ ","+ String.valueOf(arrayY-1)))
               {
                 moves.add ("E "+ String.valueOf(arrayY)+","+String.valueOf(arrayX)+" "+ String.valueOf(arrayY-1)+","+ String.valueOf(arrayX)); 
                 
               }
              
               // south-west move
                if (arrayX +1 <16  && arrayY-1 >=0 && board[arrayX+1][arrayY-1] =='.' && !homeCoordinates.containsKey(String.valueOf(arrayX+1)+ ","+ String.valueOf(arrayY-1)))
               {
                 moves.add ("E "+ String.valueOf(arrayY)+","+String.valueOf(arrayX)+" "+ String.valueOf(arrayY-1)+","+ String.valueOf(arrayX+1));     
               }
                
               // adding south move
               if (arrayX +1 < 16  && board[arrayX+1][arrayY] =='.' &&!homeCoordinates.containsKey(String.valueOf(arrayX+1)+ ","+ String.valueOf(arrayY)))
               {
                 moves.add ("E "+ String.valueOf(arrayY)+","+String.valueOf(arrayX)+" "+ String.valueOf(arrayY)+","+ String.valueOf(arrayX+1));     
               }
          
                     // adding east move       
               if (arrayY +1 <16 && board[arrayX][arrayY+1] =='.' && !homeCoordinates.containsKey(String.valueOf(arrayX)+ ","+ String.valueOf(arrayY+1)))
               {
                 moves.add ("E "+ String.valueOf(arrayY)+","+String.valueOf(arrayX)+" "+ String.valueOf(arrayY+1)+","+ String.valueOf(arrayX));     
               }
               // adding south-east move
               if (arrayX +1 <16  && arrayY+1 <16 && board[arrayX+1][arrayY+1] =='.' && !homeCoordinates.containsKey(String.valueOf(arrayX+1)+ ","+ String.valueOf(arrayY+1)))
               {
                 moves.add ("E "+ String.valueOf(arrayY)+","+String.valueOf(arrayX)+" "+ String.valueOf(arrayY+1)+","+ String.valueOf(arrayX+1));     
               }
               
         
               
               // adding north-east move
              if (arrayX -1 >=0  && arrayY+1 <=15 && board[arrayX-1][arrayY+1] =='.' && !homeCoordinates.containsKey(String.valueOf(arrayX-1)+ ","+ String.valueOf(arrayY+1)))
               {
                 moves.add ("E "+ String.valueOf(arrayY)+","+String.valueOf(arrayX)+" "+ String.valueOf(arrayY+1)+","+ String.valueOf(arrayX-1));     
               }   
              
               }
 ////////////////////////////////////////////////////////////////////////////////////////////////////
               
               // if the pawns are in the opponent goal they cannot come out of the goal
               else
               {   
                           // north-west move
               if (arrayX -1 >=0 && arrayY-1 >=0 && board[arrayX-1][arrayY-1] =='.' && opponentCoordinates.containsKey(String.valueOf(arrayX-1) +"," + String.valueOf(arrayY-1)))
               {
                 moves.add ("E "+ String.valueOf(arrayY)+","+String.valueOf(arrayX)+" "+ String.valueOf(arrayY-1)+","+ String.valueOf(arrayX-1));                    
               } 
                   
                   // north move
               if (arrayX -1 >=0  && board[arrayX-1][arrayY] =='.' && opponentCoordinates.containsKey(String.valueOf(arrayX-1) +"," + String.valueOf(arrayY)))
               {
                 moves.add ("E "+ String.valueOf(arrayY)+","+String.valueOf(arrayX)+" "+ String.valueOf(arrayY)+","+ String.valueOf(arrayX-1));            
               }
               
       
               
               // west move
               if (arrayY -1 >=0 && board[arrayX][arrayY-1] =='.' && opponentCoordinates.containsKey(String.valueOf(arrayX) +"," + String.valueOf(arrayY-1)))
               {
                 moves.add ("E "+ String.valueOf(arrayY)+","+String.valueOf(arrayX)+" "+ String.valueOf(arrayY-1)+","+ String.valueOf(arrayX));                 
               }
              
               // south-west move
                if (arrayX +1 <16  && arrayY-1 >=0 && board[arrayX+1][arrayY-1] =='.' && opponentCoordinates.containsKey(String.valueOf(arrayX+1) +"," + String.valueOf(arrayY-1)))
               {
                 moves.add ("E "+ String.valueOf(arrayY)+","+String.valueOf(arrayX)+" "+ String.valueOf(arrayY-1)+","+ String.valueOf(arrayX+1));     
               }
                
               // adding south move
               if (arrayX +1 < 16 && board[arrayX+1][arrayY] =='.' && opponentCoordinates.containsKey(String.valueOf(arrayX+1) +"," + String.valueOf(arrayY)))
               {
                 moves.add ("E "+ String.valueOf(arrayY)+","+String.valueOf(arrayX)+" "+ String.valueOf(arrayY)+","+ String.valueOf(arrayX+1));     
               }
          
                           // adding east move       
               if (arrayY +1 <16 && board[arrayX][arrayY+1] =='.' && opponentCoordinates.containsKey(String.valueOf(arrayX) +"," + String.valueOf(arrayY+1)))
               {
                 moves.add ("E "+ String.valueOf(arrayY)+","+String.valueOf(arrayX)+" "+ String.valueOf(arrayY+1)+","+ String.valueOf(arrayX));     
               }
               // adding south-east move
               if (arrayX +1 <16  && arrayY+1 <16 && board[arrayX+1][arrayY+1] =='.' && opponentCoordinates.containsKey(String.valueOf(arrayX+1) +"," + String.valueOf(arrayY+1)))
               {
                 moves.add ("E "+ String.valueOf(arrayY)+","+String.valueOf(arrayX)+" "+ String.valueOf(arrayY+1)+","+ String.valueOf(arrayX+1));     
               }
               
   
               
               // adding north-east move
               if (arrayX -1 >=0  && arrayY+1 <=15 && board[arrayX-1][arrayY+1] =='.' && opponentCoordinates.containsKey(String.valueOf(arrayX-1) +"," + String.valueOf(arrayY+1)))
               {
                 moves.add ("E "+ String.valueOf(arrayY)+","+String.valueOf(arrayX)+" "+ String.valueOf(arrayY+1)+","+ String.valueOf(arrayX-1));     
               }                      
               
               }
                              
            }         
        }
        
        else
        {
          for (int i= 0;i< allPawnCoordinates.size();i++)
            {
               String[] splitter = allPawnCoordinates.get(i).split(",");
               int arrayX = Integer.parseInt(splitter[0]);
               int arrayY = Integer.parseInt(splitter[1]);
               HashMap<String, Integer> visited =  new HashMap<String, Integer>();
               
               // Prioritize jump moves first
                if (!opponentCoordinates.containsKey(String.valueOf(arrayX)+","+String.valueOf(arrayY)))  
                jumpMovesOutsideHome(colour, board, arrayX, arrayY, moves, arrayX, arrayY, "", visited, homeCoordinates, startTime,initialTimeLimit);
                 long currentTime = System.currentTimeMillis();
               if (currentTime - startTime >= initialTimeLimit-0.5)
                   return moves;
               
               if (opponentCoordinates.containsKey(String.valueOf(arrayX)+","+String.valueOf(arrayY)))
                   opponentAreaMoves(colour, board, arrayX, arrayY, moves, arrayX, arrayY, "", visited, startTime, initialTimeLimit, opponentCoordinates);
                   
               
                   currentTime = System.currentTimeMillis();
               if (currentTime - startTime >= initialTimeLimit-0.5)
                   return moves;
                
               if (!opponentCoordinates.containsKey(String.valueOf(arrayX)+","+ String.valueOf(arrayY)))
               {               
               // Considering single moves first     
               
            // adding south-east move
               if (arrayX +1 <16  && arrayY+1 <16  && board[arrayX+1][arrayY+1] =='.' && !homeCoordinates.containsKey(String.valueOf(arrayX+1)+ ","+ String.valueOf(arrayY+1)))
               {
                 moves.add ("E "+ String.valueOf(arrayY)+","+String.valueOf(arrayX)+" "+ String.valueOf(arrayY+1)+","+ String.valueOf(arrayX+1));     
               }
        
            // adding south move
                              
               if (arrayX +1 < 16 && board[arrayX+1][arrayY] =='.'  && !homeCoordinates.containsKey(String.valueOf(arrayX+1)+ ","+ String.valueOf(arrayY)))
               {
                 moves.add ("E "+ String.valueOf(arrayY)+","+String.valueOf(arrayX)+" "+ String.valueOf(arrayY)+","+ String.valueOf(arrayX+1));     
               }
           
               // adding east move       
               if (arrayY +1 <16  && board[arrayX][arrayY+1] =='.' &&!homeCoordinates.containsKey(String.valueOf(arrayX)+ ","+ String.valueOf(arrayY+1)))
               {
                 moves.add ("E "+ String.valueOf(arrayY)+","+String.valueOf(arrayX)+" "+ String.valueOf(arrayY+1)+","+ String.valueOf(arrayX));     
               }
              
                    // adding north-east move
              if (arrayX -1 >=0  && arrayY+1 <=15 && board[arrayX-1][arrayY+1] =='.' && !homeCoordinates.containsKey(String.valueOf(arrayX-1)+ ","+ String.valueOf(arrayY+1)))
               {
                 moves.add ("E "+ String.valueOf(arrayY)+","+String.valueOf(arrayX)+" "+ String.valueOf(arrayY+1)+","+ String.valueOf(arrayX-1));     
               } 
              
                    // north move
               if (arrayX -1 >=0 && board[arrayX-1][arrayY] =='.' && !homeCoordinates.containsKey(String.valueOf(arrayX-1)+ ","+ String.valueOf(arrayY)))
               {
                 moves.add ("E "+ String.valueOf(arrayY)+","+String.valueOf(arrayX)+" "+ String.valueOf(arrayY)+","+ String.valueOf(arrayX-1));            
               }
               
                                  // west move
                if (arrayY -1 >=0 && board[arrayX][arrayY-1] =='.'  && !homeCoordinates.containsKey(String.valueOf(arrayX)+ ","+ String.valueOf(arrayY-1)))
               {
                 moves.add ("E "+ String.valueOf(arrayY)+","+String.valueOf(arrayX)+" "+ String.valueOf(arrayY-1)+","+ String.valueOf(arrayX)); 
                 
               }
                
               // north-west move
               if (arrayX -1 >=0 && arrayY-1 >=0 && board[arrayX-1][arrayY-1] =='.' && !homeCoordinates.containsKey(String.valueOf(arrayX-1)+ ","+ String.valueOf(arrayY-1)))
               {
                 moves.add ("E "+ String.valueOf(arrayY)+","+String.valueOf(arrayX)+" "+ String.valueOf(arrayY-1)+","+ String.valueOf(arrayX-1));                    
               } 
               
                // south-west move
                if (arrayX +1 <16  && arrayY-1 >=0 && board[arrayX+1][arrayY-1] =='.' && !homeCoordinates.containsKey(String.valueOf(arrayX+1)+ ","+ String.valueOf(arrayY-1)))
               {
                 moves.add ("E "+ String.valueOf(arrayY)+","+String.valueOf(arrayX)+" "+ String.valueOf(arrayY-1)+","+ String.valueOf(arrayX+1));     
               }
                   
               }
               
         ///////////////////////////////////////////////////////////////////////////////////////////      
           else
               {
                   
                // adding south-east move
               if (arrayX +1 <16  && arrayY+1 <16  && board[arrayX+1][arrayY+1] =='.' && opponentCoordinates.containsKey(String.valueOf(arrayX+1)+","+ String.valueOf(arrayY+1)))
               {
                 moves.add ("E "+ String.valueOf(arrayY)+","+String.valueOf(arrayX)+" "+ String.valueOf(arrayY+1)+","+ String.valueOf(arrayX+1));     
               }
               
                     // south move                 
               if (arrayX +1 < 16 && board[arrayX+1][arrayY] =='.'  && opponentCoordinates.containsKey(String.valueOf(arrayX+1)+","+ String.valueOf(arrayY)))
               {
                 moves.add ("E "+ String.valueOf(arrayY)+","+String.valueOf(arrayX)+" "+ String.valueOf(arrayY)+","+ String.valueOf(arrayX+1));     
               }
          
    
               // adding east move       
               if (arrayY +1 <16 && board[arrayX][arrayY+1] =='.' && opponentCoordinates.containsKey(String.valueOf(arrayX)+","+ String.valueOf(arrayY+1)))
               {
                 moves.add ("E "+ String.valueOf(arrayY)+","+String.valueOf(arrayX)+" "+ String.valueOf(arrayY+1)+","+ String.valueOf(arrayX));     
               }
                   
               // adding north-east move
              if (arrayX -1 >=0  && arrayY+1 <=15 && board[arrayX-1][arrayY+1] =='.' && opponentCoordinates.containsKey(String.valueOf(arrayX-1)+","+ String.valueOf(arrayY+1)))
               {
                 moves.add ("E "+ String.valueOf(arrayY)+","+String.valueOf(arrayX)+" "+ String.valueOf(arrayY+1)+","+ String.valueOf(arrayX-1));     
               }                               
                  
               
           // north move
               if (arrayX -1 >=0  && board[arrayX-1][arrayY] =='.' && opponentCoordinates.containsKey(String.valueOf(arrayX-1)+","+ String.valueOf(arrayY)))
               {
                 moves.add ("E "+ String.valueOf(arrayY)+","+String.valueOf(arrayX)+" "+ String.valueOf(arrayY)+","+ String.valueOf(arrayX-1));            
               }
               
                              // west move
                if (arrayY -1 >=0 && board[arrayX][arrayY-1] =='.' && opponentCoordinates.containsKey(String.valueOf(arrayX)+","+ String.valueOf(arrayY-1)))
               {
                 moves.add ("E "+ String.valueOf(arrayY)+","+String.valueOf(arrayX)+" "+ String.valueOf(arrayY-1)+","+ String.valueOf(arrayX)); 
                 
               }
                
                                    // north-west move
               if (arrayX -1 >=0 && arrayY-1 >=0 && board[arrayX-1][arrayY-1] =='.' && opponentCoordinates.containsKey(String.valueOf(arrayX-1)+","+ String.valueOf(arrayY-1)))
               {
                 moves.add ("E "+ String.valueOf(arrayY)+","+String.valueOf(arrayX)+" "+ String.valueOf(arrayY-1)+","+ String.valueOf(arrayX-1));                    
               } 
                  
                
                
                // south-west move
                if (arrayX +1 <16  && arrayY-1 >=0 && board[arrayX+1][arrayY-1] =='.' && opponentCoordinates.containsKey(String.valueOf(arrayX+1)+","+ String.valueOf(arrayY-1)))
               {
                 moves.add ("E "+ String.valueOf(arrayY)+","+String.valueOf(arrayX)+" "+ String.valueOf(arrayY-1)+","+ String.valueOf(arrayX+1));     
               }
                 
                 
               }
            }               
        }   
    //    System.out.println("exiting getPossibleMoves()");
        return moves;
    }
    
      
    // This function is for getting the moves of pawns located in the home territory 
    public static ArrayList<String> checkLegality(String colour, float initialTimeLimit, char[][] board, long startTime)
    {
        
        
    //    System.out.println("inside check legality");
        ArrayList<String> moves = new ArrayList<String>();              
        // checking if there are pawns in own goal and getting a list of possible moves for these pawns       
        // getting home coordinates of colour which I am playing with
        // homeCoordinates of the form (array X coord, array Y coords        
        // ArrayList<String> homeCoordinates = homeCoords(colour);
        
        HashMap<String, Integer> homeComing = homeCoordsHashMap(colour);
        ArrayList<String> coordsOfAllPawns = null;
        ArrayList<String> homeCoordinates = new ArrayList(homeComing.keySet());     
        
        long currentTime;
        if (colour.equals("WHITE"))
        {
            // checking for moves that lie in the region
            for (int i =0;i<homeCoordinates.size();i++)
                {   
                    currentTime = System.currentTimeMillis();
                    if (currentTime - startTime >=initialTimeLimit -0.5)
                    {
                      
                        return moves;
                    }
                        String pos = homeCoordinates.get(i);
                    String[] splitter =  pos.split(",");
                    int arrayX = Integer.parseInt(splitter[0]);
                    int arrayY = Integer.parseInt(splitter[1]);                 
                    if (board[arrayX][arrayY] =='W')
                    {
                    HashMap<String, Integer> visited = new HashMap<String, Integer>();
                    
                    // Prioritizing jumping moves first 
                    jumpMovesInitial(colour, board, arrayX, arrayY, moves, arrayX, arrayY, "", visited, homeComing, startTime, (long)initialTimeLimit);  
                    currentTime = System.currentTimeMillis();
                    if (currentTime - startTime >=initialTimeLimit -0.5)
                        return moves;
                    
                    // Then evaluating single moves
                    
                    // north west single move
                
                    if (board[arrayX-1][arrayY-1] =='.')
                        moves.add("E "+arrayY+","+arrayX+" "+(arrayY-1)+","+(arrayX-1));                                               
                    
                    
                    // north single move
                    if (board[arrayX-1][arrayY] =='.')
                        moves.add("E "+arrayY+","+(arrayX)+" "+arrayY+","+(arrayX-1));                                                          
                    
                    
                    // west single move
                    if (board[arrayX][arrayY-1] =='.')
                    {          
                       moves.add("E "+arrayY+","+arrayX+" "+(arrayY-1)+","+arrayX);                                                                   
                    }       
                    }              
                } 
            }
           
        else
        {            
           for (int i =0;i< homeCoordinates.size();i++)
           {
                    currentTime = System.currentTimeMillis();
                    if (currentTime - startTime >=initialTimeLimit -1)
                        return moves;
                
                    String pos = homeCoordinates.get(i);
                    String[] splitter =  pos.split(",");
                    int arrayX = Integer.parseInt(splitter[0]);
                    int arrayY = Integer.parseInt(splitter[1]);
                    if(board[arrayX][arrayY]=='B')
                    {
                       HashMap<String,Integer> visited = new HashMap<String, Integer>();
                    
                    // Prioritizing jump moves first
                   jumpMovesInitial(colour, board, arrayX, arrayY, moves, arrayX, arrayY, "", visited, homeComing, startTime, (long)initialTimeLimit);
                   currentTime = System.currentTimeMillis();
                   if (currentTime - startTime >=initialTimeLimit -1)
                      return moves;
                                      
                    // south-east single move
                    if (board[arrayX+1][arrayY+1] =='.')
                    {
                        moves.add("E "+arrayY+","+arrayX+" "+(arrayY+1)+","+(arrayX+1));                                                          
                    }
                    
                    // south single move
                    if (board[arrayX+1][arrayY] =='.')
                    {       
                        moves.add("E "+arrayY+","+arrayX+" "+(arrayY)+","+(arrayX+1));                                                          
                    }
                    
                    // east single move 
                   if (board[arrayX][arrayY+1] =='.')
                    { 
                        moves.add("E "+arrayY+","+arrayX+" "+(arrayY+1)+","+arrayX);                                                          
                    }                                           
                }                      
           }              
        } 
        
        
     //   System.out.println("exiting checkLegality()");
        return moves;
    }
    
    public static void singleMove (String colour, float initialTimeLimit, char[][] board, long startTime)throws Exception
    {       
        //System.out.println("inside singleMove()");
        // checking if all pawns removed from starting region
         ArrayList<String> removePawnsFromHomeMoves = checkLegality(colour, initialTimeLimit, board, startTime);
         
        // If all Pawns are out of home territory
        if (removePawnsFromHomeMoves.size() ==0)
        {
           //System.out.println("All pawns out of the region");
           ArrayList<String> availableMoves = getPossibleMoves(colour, initialTimeLimit, board, startTime);            
           File file  = new File ("C:\\Users\\Anchit Pandey\\Desktop\\output.txt");
           BufferedWriter writer = new BufferedWriter(new FileWriter(file));
           String validMove = availableMoves.get(0);
           writer.write(validMove);
           writer.close();
           return;                   
        }  
         // Removing the pawns from the home territory      
        else
        {
           //System.out.println("Not all pawns out");
           File file  = new File ("C:\\Users\\Anchit Pandey\\Desktop\\output.txt");
           BufferedWriter writer = new BufferedWriter(new FileWriter(file));
           String validMove = removePawnsFromHomeMoves.get(0);
           writer.write(validMove);
           writer.close();
           return;           
        }  
    } 
    
    public static void game(String colour, float initialTimeLimit, char[][] board, long startTime)throws Exception
    {   
        
     //   System.out.println("Inside game()");
        
        ArrayList<String> removePawnsFromHomeMoves = checkLegality(colour, initialTimeLimit, board, startTime);     
        // Can play outside moves
        if (removePawnsFromHomeMoves.size() ==0)
        {
         //   System.out.println("All pawns are out, I can play outside moves");
           // ArrayList<String> availableMoves = getPossibleMoves(colour, initialTimeLimit, board, startTime);
            //availableMoves = sortMoves(availableMoves, board, colour);
           // String backUpBestMove =  availableMoves.get(0);
            double alpha = Double.MIN_VALUE;
            double beta = Double.MAX_VALUE;
           // long startTiming = System.currentTimeMillis();
            String opponentColour = "";
            if (colour.equals ("WHITE"))
                opponentColour ="BLACK";
            else
                opponentColour = "WHITE";
            
            int numberOf= 0;
            //ArrayList<String> goalCoords = homeCoords(colour);
            ArrayList<String> goalCoords = homeCoords(opponentColour);
            for (int i=0;i<goalCoords.size();i++)
            {
                String[] splitter = goalCoords.get(i).split(",");
                int xCoord = Integer.parseInt(splitter[0]);
                int yCoord = Integer.parseInt(splitter[1]);
                if (board[xCoord][yCoord] == colour.charAt(0))
                {
                    numberOf++;  
                }
            }
            
          
            /** CHECK THIS **/
           initialTimeLimit = (float)initialTimeLimit /(65+ numberOf);
           System.out.println("Initial Time limit for this entire move is "+ (float)(initialTimeLimit/1000));
           
             String bestMove = "";
           int depth = 4;
           bestMove = alphaBetaPruning2(depth, colour, initialTimeLimit, alpha, beta, startTime, colour, board);
  
        /*    
           outer: while (true)
          {
           bestMove = alphaBetaPruning2(depth, colour, initialTimeLimit, alpha, beta, startTime, colour, board);
          float currentTime = (float) System.currentTimeMillis();
          System.out.println("USP is: "+ (currentTime - startTime));
           if (currentTime- startTime >= initialTimeLimit-500)
               break outer;
           depth+=1;
          System.out.println("Time left is : "+ ((initialTimeLimit - currentTime + startTime)/1000));
          }
           
         */  
           String[] lineSplitter = bestMove.split("\n");
           String[] spaceSplitter = lineSplitter[lineSplitter.length-1].split("\\s+");
          bestMove = "";
          for (int i =0;i< lineSplitter.length-1;i++)
              bestMove +=lineSplitter[i]+"\n";
          
          for (int i=0;i<spaceSplitter.length-1;i++)
              bestMove+=spaceSplitter[i]+" ";
       //   bestMove += spaceSplitter[0]+" "+spaceSplitter[1]+" "+spaceSplitter[2]; 
           
 //          if (bestMove.equals(""))
   //            bestMove = backUpBestMove;
 
          File file = new File("C:\\Users\\Anchit Pandey\\Desktop\\output.txt");
          BufferedWriter writer = new BufferedWriter(new FileWriter(file));
          writer.write(bestMove);
          writer.close();        
        }
        
        // First remove the pawns that are inside
        else
        {
        //    System.out.println("All pawns are NOT out, have to remove them first");
            File file = new File ("C:\\Users\\Anchit Pandey\\Desktop\\output.txt");
            BufferedWriter writer = new BufferedWriter (new FileWriter(file));
            ArrayList<String> availableMoves = sortInitial(removePawnsFromHomeMoves, board, colour);
            String bestMove = availableMoves.get(0);
            writer.write(bestMove);
            writer.close();   
            return;
        } 
    //    System.out.println("exiting game()");
    }  
     
    public static void main(String[] args)throws Exception
    {        
       long startTime = System.currentTimeMillis();
       File file  = new File ("C:\\Users\\Anchit Pandey\\Desktop\\input.txt");
       BufferedReader buff = new BufferedReader(new FileReader(file));
       String singleOrGame = buff.readLine();
       String colour = buff.readLine();
     //  System.out.println("howa are you doing");
       
       float initialTimeLimit = Float.parseFloat(buff.readLine());
       initialTimeLimit *=1000;
       System.out.println(initialTimeLimit);
       
       char[][] board = new char[16][16];
       String splitter;
       for (int i=0;i<16;i++)
       {
           splitter = buff.readLine();
           for (int j=0;j<16;j++)
           {
                board[i][j] = splitter.charAt(j);               
           }          
       }     
       System.out.println("Original startTime is: "+ startTime);
       if (singleOrGame.equals("SINGLE"))
           singleMove(colour, initialTimeLimit, board, startTime);        
       // GAME 
       else
           game(colour, initialTimeLimit, board, startTime);        
    
}    
}