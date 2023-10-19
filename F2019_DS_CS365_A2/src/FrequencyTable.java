/*
Name: Joseph M. Scollo
School: Suny Oswego
Course: CSC-365 Data Structures

Class: FrequencyTable 
About Class: This class contains 5 methods and a construcor. The details on their members and how the function is documented
                  through out the code. This class is a custom Hash Table that takes strings as keys 
                  and the frequency in which they occur as their values.
*/


import java.io.FileNotFoundException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.io.RandomAccessFile;
import java.io.File;
import java.io.IOException;


public class FrequencyTable {//class

    private double LOADFACTOR;
    private FreqBucket[] fTable;
    private int elements;
    private ArrayList<String> keyList, uKeys;
    private File file;
    private RandomAccessFile raf;
    ByteBuffer fb;
    private FileChannel channel;
    private long endpos;
    // long word;


    FrequencyTable()   //Constructor that sets innitializes LOADFACOR, ININTSIZE, fTable, elements, and keyList.
    {//constructor
        LOADFACTOR = .75;
        int INITSIZE = 500;
        fTable = new FreqBucket[INITSIZE];
        elements = 0;
        keyList = new ArrayList<String>();
        uKeys = new ArrayList<String>();
        String fName = "dataStore2.txt";
        file = new File(fName);
        //file = new RandomAccessFile(word,"rw");
        // word = "";


    }


    

    public void addKey(String key) //Method thats adds keys entered into the thable to an array list.
    {//void addKey
        keyList.add(key);
        for(String k:keyList) {

            if(!uKeys.contains(k))
                uKeys.add(k);  // adds all keys to a master list.

        }
    }

    public ArrayList<String> getKeyList() //Method tht returns the keys from the keyList.
    {//void

        return keyList;
    }

    public void put(String key, int value) // method that puts the keys and values into the hash table.
    {//put
        int index = Math.abs(key.hashCode()) % fTable.length;


        file = new File(key);
        FreqBucket node; // a new instance of FreqBucket called node/

        if (fTable[index] == null) // Checks is position in the array is empty
        {//if
            fTable[index] = new FreqBucket(key, value);// creates a new Freqbucket or "node ready to take an element's key and value.

            elements++; //A couter that keeps track up the elemnts stored in memeory in order to prevent overflow.
        }
        //node = fTable[index];
        while (fTable[index] != null) //while loop that handles collisions. The body of this loop is entered while an index is not null.
        {//begin while
            node = fTable[index]; // sets the node = the current hashcode assigned to index.

            if (key.equals(node.getKey())) // checks to see if the current key matches the key in the current index
                node.setValue(node.getValue() + 1);// if the key mathches, increment its value by one.

            else if (node.getNextNode() == null) //if the next node is null set that node to receive an element with its key and a value.
            {
                node.setNextNode(new FreqBucket(key, value)); // set the next node with a new FreqBucket.

                elements++;  //increment element count

            } else if (elements >= (fTable.length * LOADFACTOR)) {                         // check tosee if the table is equal or greator than 75% of the table size.
                resize();
            }                         // if so, call the reSize method to create a new table with double the current tables size.

            else //the node has an elment get the next node.
                node = node.getNextNode();

            return;  //Exit the while loop.

        }//end while

    }

    //*************************************************************************//
    // public method of type integer that returns the associated key values    //
    //*************************************************************************//


    public int get(String key) // public method of type integer that takes a String parameter and returns the associated key values.  
    {//get
        int index;

        index = Math.abs(key.hashCode()) % fTable.length; //sets the index = to the hashcode.

        FreqBucket node;
        if (fTable[index] == null) // Checks if position in the array is empty
        {
            return 0; //position is empty return 0.

        } else
            node = fTable[index];

        while (node != null) {
            if (key.equals(node.getKey()))

                return node.getValue();
            else
                node = node.getNextNode();
        }

        return 0;
    }

    private void resize() // Method that resizes the hashtable when the load factor reaches or goes over 75 percent capacity.
    {//resize

        FreqBucket[] tmp = fTable;     // Assign temp to the current Ftable (or frequency table)
        FreqBucket next;                // Create a new  frequency Table
        fTable = new FreqBucket[fTable.length * 2];   // increases ftable's table size by twice its current size.

        for (FreqBucket element : tmp) // a for loop that hashes the next elements into the new table and maintains the old one.
        {//for

            next = element;
            while (true) {//while

                if (next == null)    // Check if next is null
                {
                    break;
                } else {//else

                    this.put(next.getKey(), next.getValue()); //Rehashes the elements.
                    next = next.getNextNode();
                }
            }
        }
    }
}
























































/*public void resize2()
{//re

   FreqBucket[] temp = fTable;
   FreqBucket node;
   fTable = new FreqBucket[fTable.length * 2];
   
   for(int i = 0; i < temp.length; i++)
   {
     
      node = temp[i];
      
      if(node == null)
         break;
      else
      if(node != null)
      {
         
         if(node.equals(node.getKey()))
            this.put(node.getKey(),node.getValue());
            
         node = temp[i].getNextNode();
         //System.out.println("*****");
      }
      
      
         
   }*/
