import java.util.Dictionary;
import java.util.Arrays;
import java.io.File;
import java.util.Scanner;
import java.io.IOException;
import java.util.Iterator;
import java.io.FileNotFoundException;
/**
 * Write a description of class SoundexNameLookUp here.
 *
 * @author (Rayan Hamid)
 * @version (a version number or a date)
 */
public class SoundexNameLookUp
{
    /**
     * Constructor for objects of class SoundexNameLookUp
     */
    public static void main(String [] args)
    {
        //We present the name of the game 
        Scanner keyboard = new Scanner(System.in);
        System.out.println("The Soundex Name Lookup System");

        //hashed dictionary
        HashedDictionary <String,String> nameHolder = new HashedDictionary<String,String>(8241);
        //file name
        String file = "nameList.txt"; 

        try{
            String nameAfter; String keyAfter;
            Scanner inputFile = new Scanner (new File(file));
            while (inputFile.hasNext())
            {
                //nameAfter is assigned to the next word in file
                nameAfter = inputFile.next();
                //invoked keyMaker, assigned to keyAfter
                keyAfter = SoundexKeyMaker.keyMaker(nameAfter);
                // in the holder, added name with soundex key
                nameHolder.add(keyAfter, nameAfter);
            }

            inputFile.close(); //file close 
        }
        catch (FileNotFoundException e)
        {
            System.out.println ("File not found: " + e.getMessage ());
        }

        // ask the user to enter a name
        System.out.println("Please enter a name: ");
        String Name; String Key;
        Name= keyboard.next();

        //assign to Key the soundex key for the name
        Key = SoundexKeyMaker.keyMaker(Name);
        System.out.println("Soundex key for " + Name + ": " + Key);

        System.out.println("The following names have the same soundex key as " + Name + ":");
        
        int counter = 0;
        //Iterator
        Iterator<String> it = nameHolder.getKeyIterator();
        String myArray[] = new String[0];
        String nameAfterArray[] = new String[0];

        //while loop that iterates through the hashed dictionary
        while(it.hasNext()){  
            String key; String name;
            //key equals the next key
            key = it.next();
            //name equals value of key name
            name = nameHolder.getValue(key);

            //if Key equals key,add to myArray
            if(Key.equals(key)){
                myArray = addArray(myArray,myArray.length,name);
                counter++;
                for(int i=0; i<5 ;i++){
                    key = it.next();
                    name = nameHolder.getValue(key);
                    nameAfterArray = addArray(myArray, myArray.length, name);
                }

            }
        }
        // if counter = 0, print statement, no names with the same soundex key
        if(counter==0){
            System.out.println("Unable to find names in list with the same soundex key");
        }

        //sort arrays, print names
        Arrays.sort(myArray);
        printArray(myArray);

        System.out.println("Here are names that are close to this soundex key:");

        Arrays.sort(nameAfterArray);
        printArray(nameAfterArray);

    }
    
    //add the name after to the array
    public static String [] addArray(String [] arr, int size, String entry){
        //newArray
        String newArray[] = new String[size+1]; 

        //for loop that add items from first array to another array
        for(int k = 0; k<size ;k++){
            newArray[k]=arr[k];
        }

        //add the new name to the very end
        newArray[size] = entry;
        //return newArray
        return newArray;
    }

    //prints names in array
    public static void printArray(String[] arr)
    {
        for (int i=0; i<arr.length; i++)
        {
            System.out.print(arr[i] + " \n");
            System.out.println();
        }
    } 

}
