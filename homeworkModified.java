import com.sun.xml.internal.fastinfoset.tools.StAX2SAXReader;
import java.io.*;
import java.util.*;
import static java.util.stream.Collectors.toMap;

class homeworkModified        
{
 
        public static HashMap<String, Integer> homeCoordsHashMap(String colour)
    {
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
               mapper.put ("0,0",1);
               mapper.put ("0,1",1);
             mapper.put ("0,2",1);
              mapper.put ("0,3",1);
               mapper.put ("0,4",1);
             
                mapper.put ("1,0",1);
                 mapper.put ("1,1",1);
                  mapper.put ("1,2",1);
                mapper.put ("1,3",1);
                mapper.put ("1,4",1);
               
              mapper.put ("2,0",1);
              mapper.put ("2,1",1);
              mapper.put ("2,2",1);
              mapper.put ("2,3",1);
 
               mapper.put ("3,0",1);
                mapper.put ("3,1",1);
                 mapper.put ("3,2",1);
              
                  mapper.put ("4,0",1);
                   mapper.put ("4,1",1);               
            }        
        return mapper;
    }
        
        
        public static void printBoard(char[][] board)
        {
            
            for (int i =0;i< 16;i++)
            {
                for (int j =0;j<16;j++)
                    System.out.print(board[i][j]);
                
                System.out.println();
                
            }
            
            
        }
    public static String condition(char[][] board, File file)throws Exception
    {         
        
    HashMap<String , Integer> whiteGoals = homeCoordsHashMap("BLACK");
    HashMap<String , Integer> blackGoals = homeCoordsHashMap("WHITE");
    ArrayList<String> whiteG = new ArrayList<String>(whiteGoals.keySet());
  
 
    
    BufferedReader buff1 = new BufferedReader (new FileReader(file));
    String single = buff1.readLine();
    String colur = buff1.readLine();
    String opp = "";
    if(colur.equals("WHITE"))
        opp = "BLACK";
    else
        opp = "WHITE";
    
   float time  = Float.parseFloat (buff1.readLine());
   
   if (time<=0)
       return opp;
   
    int counter = 0;    
    for (int i =0;i<whiteG.size();i++)
    {
        String input = whiteG.get(i);
       String[] splitter = input.split(",");
       int x = Integer.parseInt(splitter[0]);
        int y = Integer.parseInt(splitter[1]);
       if (board[x][y] !='W')
           break;
   else
           counter++;
    }
    if (counter == 19)
        return "WHITE"; 
      ArrayList<String> blackG = new ArrayList<String>(blackGoals.keySet());
 
      
    counter =0;
    for (int i =0;i<blackG.size();i++)
    {
       String input = blackG.get(i);
       String[] splitter = input.split(",");
       int x = Integer.parseInt(splitter[0]);
       int y = Integer.parseInt(splitter[1]);
       if (board[x][y] !='B')
           break;
   else
           counter++;
    }
    
    if (counter == 19)
        return "BLACK";
       
   return "NONE";  
    
    }
       
    public static void main(String[] args)throws Exception
    {
    
       File file  = new File ("C:\\Users\\Anchit Pandey\\Desktop\\input.txt");
       BufferedReader reader = new BufferedReader(new FileReader(file));
       String type = reader.readLine();
       String colourA = reader.readLine();
       String timeLeft = reader.readLine();
       String colourB = "";
       if (colourA.equals("WHITE"))
           colourB = "BLACK";
       
       else 
           colourB = "WHITE";

       ProcessBuilder pb = new ProcessBuilder ("python","C:\\Users\\Anchit Pandey\\Desktop\\home.py");
       
       char[][] board = new char[16][16];
       for (int i =0;i< 16;i++)
       {
           String teta =reader.readLine();             
           for (int j =0;j<16;j++)
           {
              board[i][j] = teta.charAt(j); 
           }           
       }
       
   //     printBoard(board);
       
       
       float time1 = 300;
       float time2 = 300;
       File fileNew = new File ("C:\\Users\\Anchit Pandey\\Desktop\\output.txt");
       BufferedReader buf1 = new BufferedReader(new InputStreamReader(System.in));
       String wait= "";
       int whiteMoves = 0;
       int blackMoves =0;
       
       int totalMoveCounter  =0;   
       while (condition(board, file).equals ("NONE") && time1 >0 && time2 >0)  
       {    
           
       long startPlayerA = System.currentTimeMillis();
//NewClass.main (new String[0]);
   System.out.println("problem confirmed");   
pb.start();

  int  count = 0;
if (count ==0)
    break;


System.out.println("definitely");   
// homework.main(new String[0]);
       long endPlayerA = System.currentTimeMillis();
       time1 = time1 - (float)(endPlayerA - startPlayerA)/1000;
       BufferedReader buff = new BufferedReader(new FileReader(fileNew));
             
       String inputReader = "";
       int counter = 0;
       String initialCoordline ="";
       String finalCoordLine = "";
       String whitesMove  ="";
       
       while ((inputReader = buff.readLine())!=null)
       { 
           whitesMove+=inputReader+"\n";
       }
       buff.close();
   

       String[] lineSplitter = whitesMove.split("\n");
       String[] spaceLimiter = lineSplitter[0].split("\\s+");
       System.out.println("space limiter of white is: ");
       for (int i =0;i<spaceLimiter.length;i++)
           System.out.println(spaceLimiter[i]);
       
       String[] commaLimiter = spaceLimiter[1].split(",");
       int xNew = Integer.parseInt(commaLimiter[1]);   
       int yNew = Integer.parseInt(commaLimiter[0]);
       
       
       spaceLimiter = lineSplitter[lineSplitter.length-1].split("\\s+");
        commaLimiter = spaceLimiter[2].split(",");
       int xNewFinal = Integer.parseInt(commaLimiter[1]);
       int yNewFinal = Integer.parseInt(commaLimiter[0]);
      
       
       char cha = board[xNew][yNew];
       board[xNew][yNew] = board[xNewFinal][yNewFinal];
       board[xNewFinal][yNewFinal] = cha;
        whiteMoves++;
       
  //     System.out.println(colourA+" played: ");
 //      System.out.println(whitesMove);
           printBoard(board);
     
           System.out.println("time remaining for "+ colourA+ " is "+time1);
           
        //       System.out.println("Enter somthing to continue");
        //         String timepass  = buf1.readLine();
       
  
       // Writing to the file        
       
       BufferedWriter writer = new BufferedWriter (new FileWriter (file));
       writer.write ("GAME\n");
       writer.write (colourB+"\n");
       writer.write (time2+"\n");
       for (int i = 0;i< 16;i++)
       {
           for (int j =0;j<16;j++)
           {  
               writer.write (board[i][j]);
           }
           writer.write ("\n");           
       }     
       writer.close();
        
       
       long startPlayerB = System.currentTimeMillis();    
        homework.main(new String[0]); 
//NewClass.main (new String[0]);
//      pb.start();
//   homework.main(new String[0]);
       long endPlayerB = System.currentTimeMillis();
       time2 = time2 - (float)(endPlayerB - startPlayerB)/1000;
       buff = new BufferedReader(new FileReader(fileNew));
       
       
       
       inputReader = "";
       counter = 0;
       String blacksMove ="";
           
       while ((inputReader = buff.readLine())!=null)
       { 
           blacksMove+=inputReader+"\n";
       }       
       System.out.println("move played by "+ colourB +" is "+blacksMove);
       buff.close(); 
        lineSplitter = blacksMove.split("\n");
       spaceLimiter = lineSplitter[0].split("\\s+");
       commaLimiter = spaceLimiter[1].split(",");
       
       xNew = Integer.parseInt(commaLimiter[1]);
       yNew = Integer.parseInt(commaLimiter[0]);
         
       spaceLimiter= lineSplitter[lineSplitter.length-1].split("\\s+");
       commaLimiter = spaceLimiter[2].split(",");   
        xNewFinal = Integer.parseInt(commaLimiter[1]);
        yNewFinal = Integer.parseInt(commaLimiter[0]);
        
                   
       cha = board[xNew][yNew];    
       board[xNew][yNew] = board[xNewFinal][yNewFinal];
       board[xNewFinal][yNewFinal] = cha;
        
           blackMoves++;
      //     System.out.println(colourB+" played: ");
      //     System.out.println(blacksMove);
           //System.out.println(colourB+" played: "+ blacksMove);
           printBoard(board);
       
       System.out.println("time remaining for "+ colourB+ " is "+time2);
       
         //  System.out.println("Enter somthing to continue");
        //   buf1.readLine();
        
            
    writer = new BufferedWriter (new FileWriter (file));
       writer.write ("GAME\n");
       writer.write (colourA+"\n");
       writer.write (time1+"\n");
       for (int i = 0;i< 16;i++)
       {
           for (int j =0;j<16;j++)
           {
               
               writer.write (board[i][j]);
           }
           writer.write ("\n");           
       }    
       writer.close(); 
       totalMoveCounter+=1;
       
      }
       
       /*
       if (time1 <=0 && time2<=0)
           System.out.println("Decrease depth.. something is wrong");
       else if (time1 <=0)
           System.out.println("WHITE LOST DUE TO TIMEOUT");
       else if (time2<=0)
           System.out.println("BLACK LOST DUE TO TIMEOUT");
          
      System.out.println("Total moves by winner: "+totalMoveCounter);      
    */
    }   
}