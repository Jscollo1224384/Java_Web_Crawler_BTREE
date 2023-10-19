/*
Name: Joseph M. Scollo
School: Suny Oswego
Course: CSC-365 Data Structures

Class: FreqBuckut
About Class: This class contains 5 methods and a construcor. The details on their members and how the function s documented
                  through out the code. The class creates hash buckets, set and gets values of nodes keys and values.
*/


public class FreqBucket 
{

   private String key; // A private String varriable that stores a key. 
   private int value;  // A private int varriable that stores a value, i.e in this case, the word frequency
   private FreqBucket next;    // the next node
                     
       
  //-------------------------------------------------------------------//
  // constructor that sets the key, the value and sets next to null    //
  //-------------------------------------------------------------------//
      
   FreqBucket(String K, int V)
   {
      key = K;         // Set the key
      value = V;       // Set the value
      next = null;     // Set the next element
   }
        
   //------------------------------------------------------------//
   // Method that gets a frequency value and returns it.         //
   //------------------------------------------------------------//

   public int getValue() 
   {
      return value;
   }
        
   //------------------------------------------------------------//
   // Method that sets the frequency value.                      //
   //------------------------------------------------------------//

   public void setValue(int v) 
   {
      value = v;
   }
        
   //------------------------------------------------------------//
   // Method that gets a key and returns it.                     //
   //------------------------------------------------------------//

   public String getKey() 
   {
      return key;
   }
        
               
   //------------------------------------------------------------//
   // Method that gets the next node.                            //
   //------------------------------------------------------------//

   FreqBucket getNextNode()
   {
      return next;
   }
        
   //------------------------------------------------------------//
   // Method that sets the next node.                            //
   //------------------------------------------------------------//

        
    void setNextNode(FreqBucket n)
    {
       next = n;
    }
                 
}          
            
    

