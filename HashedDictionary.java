import java.util.Iterator;
import java.util.NoSuchElementException;

/**
   A class that implements the ADT dictionary by using hashing
   and linear probing to resolve collisions.
   The dictionary is unsorted and has distinct search keys.
   Notes: Uses probe for add, but locate for remove and getValue.
          Uses linear probing, but includes code for quadratic probing.
          Has a display method for illustration and testing.
  
   @author Frank M. Carrano
   @version 3.0
*/
public class HashedDictionary<K, V> 
{
    private TableEntry<K, V>[] hashTable;                    // dictionary entries
    private int                numberOfEntries;
    private int                locationsUsed;             // number of table locations not null
    private static final int DEFAULT_CAPACITY = 101;         // must be prime 
    private static final double MAX_LOAD_FACTOR = 0.5; // fraction of hash table that can be filled
    
    public HashedDictionary()
    {
        this(DEFAULT_CAPACITY); // call next constructor
    } // end default constructor

    public HashedDictionary(int tableSize)
    {
        // if tableSize is prime, do not change it;
        // otherwise increase it until it is prime size
        int primeSize = getNextPrime(tableSize);
        
        // the cast is safe because the new array contains null entries
      @SuppressWarnings("unchecked")
      TableEntry<K, V>[] temp = (TableEntry<K, V>[])new TableEntry[primeSize];
      hashTable = temp;

        numberOfEntries = 0;
        locationsUsed = 0;
    } // end constructor
    
// -------------------------
// We've added this method to display the hash table for illustration and testing
// -------------------------
     public void display()  
     {
        for (int index = 0; index < hashTable.length; index++)
        {
        if (hashTable[index] == null)
            System.out.println("null ");
         else if (hashTable[index].isRemoved())
            System.out.println("notIn ");
         else
            System.out.println(hashTable[index].getKey() + " " + hashTable[index].getValue());
        } // end for
      System.out.println();
   } // end display
// -------------------------
    
    public V add(K key, V value)
    {
      V oldValue; // value to return

      if (isHashTableTooFull())
         rehash();

      int index = getHashIndex(key);
      index = probe(index, key); // check for and resolve collision

      // Assertion: index is within legal range for hashTable
      assert (index >= 0) && (index < hashTable.length);

      if ( (hashTable[index] == null) || hashTable[index].isRemoved())
      { // key not found, so insert new entry
         hashTable[index] = new TableEntry<K, V>(key, value);
         numberOfEntries++;
         locationsUsed++;
         oldValue = null;
      }
      else
      { // key found; get old value for return and then replace it
         oldValue = hashTable[index].getValue();
         hashTable[index].setValue(value);
      } // end if

      return oldValue;
   } // end add

    public V remove(K key)
    {
      V removedValue = null;
       
        int index = getHashIndex(key);
        index = locate(index, key);

        if (index != -1)
        {
            // key found; flag entry as removed and return its value
            removedValue = hashTable[index].getValue();
            hashTable[index].setToRemoved();
            numberOfEntries--;
        } // end if
        // else not found; result is null
        
        return removedValue;
   } // end remove

   public V getValue(K key)
   {
      V result = null;
      
      int index = getHashIndex(key);
      index = locate(index, key);

      if (index != -1)
         result = hashTable[index].getValue(); // key found; get value

      // else not found; result is null
      
      return result;
   } // end getValue

    private int probe(int index, K key)
    {
      boolean found = false;
      int removedStateIndex = -1; // index of first location in 
                                 // removed state
      //    int increment = 1;          // for quadratic probing **********

      while ( !found && (hashTable[index] != null) )
      {
         if (hashTable[index].isIn())
         {
            if (key.equals(hashTable[index].getKey()))
               found = true; // key found
            else // follow probe sequence
            {
               index = (index + 1) % hashTable.length;         // linear probing
   //                index = (index + increment) % hashTable.length; // quadratic probing **********
   //                increment = increment + 2;                      // odd values for quadratic probing **********
            }
         }
         else // skip entries that were removed
         {
            // save index of first location in removed state
            if (removedStateIndex == -1)
              removedStateIndex = index;
              
            index = (index + 1) % hashTable.length;         // linear probing
   //            index = (index + increment) % hashTable.length; // quadratic probing **********
   //            increment = increment + 2;                      // odd values for quadratic probing **********
         } // end if
      } // end while
      // Assertion: either key or null is found at hashTable[index]

      if (found || (removedStateIndex == -1) )
         return index;             // index of either key or null
      else
         return removedStateIndex; // index of an available location
    } // end probe
    
    private int locate(int index, K key)
    {
       boolean found = false;
   //    int increment = 1;          // for quadratic probing **********
      
       while ( !found && (hashTable[index] != null) )
       {
         if ( hashTable[index].isIn() && key.equals(hashTable[index].getKey()) )
            found = true; // key found
         else // follow probe sequence
         {
            index = (index + 1) % hashTable.length;         // linear probing
   //       index = (index + increment) % hashTable.length; // quadratic probing **********
   //       increment = increment + 2;                      // odd values for quadratic probing **********
         }
      } // end while
      // Assertion: either key or null is found at hashTable[index]
      
      int result = -1;
      if (found) 
         result = index;
        
      return result;
   } // end locate

    public boolean contains(K key)
   {
       return getValue(key) != null; 
   } // end contains

   public boolean isEmpty()
   {
      return numberOfEntries == 0;
   } // end isEmpty

   public boolean isFull()
   {
      return false;
   } // end isFull

   public int getSize()
   {
      return numberOfEntries;
   } // end getSize

    public final void clear()
    { 
        for (int index = 0; index < hashTable.length; index++)
            hashTable[index] = null;

      numberOfEntries = 0;
      locationsUsed = 0;
   } // end clear


    public Iterator<K> getKeyIterator()
    { 
        return new KeyIterator();
    } // end getKeyIterator
    
    public Iterator<V> getValueIterator()
    {    
        return new ValueIterator();
    } // end getValueIterator
    private int getHashIndex(K key)
    {

        int hashIndex = key.hashCode() % hashTable.length; 
        
        if (hashIndex < 0)
        {
            hashIndex = hashIndex + hashTable.length;
        } // end if

        return hashIndex;
    } // end getHashIndex
    
   // Increases the size of the hash table to a prime > twice its old size. 
    private void rehash()
    {
      TableEntry<K, V>[] oldTable = hashTable;
      int oldSize = hashTable.length;
      int newSize = getNextPrime(oldSize + oldSize);
      
      // the cast is safe because the new array contains null entries
      @SuppressWarnings("unchecked")
      TableEntry<K, V>[] temp = (TableEntry<K, V>[])new TableEntry[newSize]; // increase size of array
      hashTable = temp;

      numberOfEntries = 0; // reset number of dictionary entries, since
                           // it will be incremented by add during rehash
      locationsUsed = 0;

      // rehash dictionary entries from old array to the new and bigger 
      // array; skip both null locations and removed entries
      for (int index = 0; index < oldSize; index++)
      {
         if ( (oldTable[index] != null) && oldTable[index].isIn() )
            add(oldTable[index].getKey(), oldTable[index].getValue());
      } // end for
    } // end rehash

   // Returns true if lambda > MAX_LOAD_FACTOR for hash table;
   // otherwise returns false. 
   private boolean isHashTableTooFull()
   {
      return locationsUsed > MAX_LOAD_FACTOR * hashTable.length;
   } // end isHashTableTooFull

   // Returns a prime integer that is >= the given integer.
    private int getNextPrime(int integer)
    {
        // if even, add 1 to make odd
       if (integer % 2 == 0)
      {
        integer++;
        } // end if
        
        // test odd integers
      while (!isPrime(integer))
      {
         integer = integer + 2;
      } // end while

        return integer;
    } // end getNextPrime
    
   // Returns true if the given intege is prime.
    private boolean isPrime(int integer)
    {
        boolean result;
        boolean done = false;
        
        // 1 and even numbers are not prime
        if ( (integer == 1) || (integer % 2 == 0) )
      {
            result = false; 
        }
        
        // 2 and 3 are prime
        else if ( (integer == 2) || (integer == 3) )
        {
            result = true;
        }
        
        else // integer is odd and >= 5
        {
            assert (integer % 2 != 0) && (integer >= 5);
            
            // a prime is odd and not divisible by every odd integer up to its square root
            result = true; // assume prime
            for (int divisor = 3; !done && (divisor * divisor <= integer); divisor = divisor + 2)
            {
               if (integer % divisor == 0)
          {
                    result = false; // divisible; not prime
                    done = true;
                } // end if
            } // end for
        } // end if
           
        return result;
    } // end isPrime

    private class KeyIterator implements Iterator<K>
    {
      private int currentIndex; // current position in hash table
      private int numberLeft;   // number of entries left in iteration

      private KeyIterator() 
      {
        currentIndex = 0;
        numberLeft = numberOfEntries;
      } // end default constructor
      
      public boolean hasNext() 
      {
        return numberLeft > 0; 
      } // end hasNext
      
      public K next()
      {
        K result = null;
        
        if (hasNext())
        {
          // find index of next entry
          while ( (hashTable[currentIndex] == null) || hashTable[currentIndex].isRemoved() ) 
          {
            currentIndex++;
          } // end while
          
          result = hashTable[currentIndex].getKey();
          numberLeft--;
          currentIndex++;
        }
        else
          throw new NoSuchElementException();
          
        return result;
      } // end next
      
      public void remove()
      {
        throw new UnsupportedOperationException();
      } // end remove
    } // end KeyIterator
    
    private class ValueIterator implements Iterator<V>
    {
        private int currentIndex;
        private int numberLeft; 
        
        private ValueIterator() 
        {
            currentIndex = 0;
            numberLeft = numberOfEntries;
        } // end default constructor
        
        public boolean hasNext() 
        {
            return numberLeft > 0; 
        } // end hasNext
        
        public V next()
        {
            V result = null;
            
            if (hasNext())
            {
          while ( (hashTable[currentIndex] == null) || hashTable[currentIndex].isRemoved() ) 
                {
                    currentIndex++;
                } // end while
                
                result = hashTable[currentIndex].getValue();
                numberLeft--;
                currentIndex++;
            }
            else
                throw new NoSuchElementException();
        
            return result;
        } // end next
        
        public void remove()
        {
            throw new UnsupportedOperationException();
        } // end remove
    } // end ValueIterator

    private class TableEntry<S, T> 
    {
        private S key;
        private T value;
        private boolean inTable; // true if entry is in table
        
        private TableEntry(S searchKey, T dataValue)
        {
            key = searchKey;
            value = dataValue;
            inTable = true;
        } // end constructor
        
        private S getKey()
        {
            return key;
        } // end getKey
        
        private T getValue()
        {
            return value;
        } // end getValue
        
        private void setValue(T newValue)
        {
            value = newValue;
        } // end setValue

        private boolean isIn()
        {
            return inTable;
        } // end isIn
        
        private boolean isRemoved() // opposite of isIn
        {
            return !inTable;
        } // end isRemoved

        // state = true means entry in use; false means entry not in use, ie deleted
        private void setToRemoved()
        {
            key = null;
            value = null;
            inTable = false;
        } // end setToRemoved
        
        private void setToIn() // not used
        {
            inTable = true;
        } // end setToIn
    } // end TableEntry
} // end HashedDictionary