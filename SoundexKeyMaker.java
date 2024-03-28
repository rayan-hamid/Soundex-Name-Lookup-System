
/**
 * Write a description of class SoundexKeyMaker here.
 *
 * @author (your name)
 * @version (a version number or a date)
 */

public class SoundexKeyMaker
{
    /**
     * Takes a String and returns a soundex key based on the string
     * @param name, the name that will be made into a soundex key
     * @return key, the key produced from the string provided
     */
    public static String keyMaker(String name){
       
       /*1. Entire name is translated into a series of digit character*/
       String digits = "";
       for(int k=0; k<name.length() ; k++){
          digits+= letterCheck(name.charAt(k));
       }
       
       /*2. Remove or skip double digits*/
       
       String digits2 = "";
       
         for(int k=0; k<digits.length()-1 ; k++){
            if(k==0 && digits.charAt(k)==digits.charAt(k+1) && k+1 !=digits.length()-1){
             digits2+=digits.charAt(k);
             k++;
          }
          else if(k>0 && digits.charAt(k)==digits.charAt(k+1) && k+1 !=digits.length()-1){
             digits2+=digits.charAt(k);
             k++;
          }
            
          else{
             digits2+= digits.charAt(k);
          }
       }
       
        digits = digits2;
          
                  
       //3. Replace the first digit with the first letter of the name
       digits2= name.charAt(0) + "";
       for(int k=1; k<digits.length() ; k++){
          digits2 += digits.charAt(k);
          }
       
       //4. Remove all 7s
       digits = "";
       for(int k=0; k<digits2.length() ; k++){
          if(digits2.charAt(k)=='7'){
             digits+= "";
          }
          else{
             digits+= digits2.charAt(k);
          }
          
          }
          
       //5. Shorten or lengthen the keys to 4 in length
       digits2=digits;
       
       if(digits.length()==4){
          digits=digits;
       }
       else if(digits.length()<4){
          for(int k=digits.length(); k<4 ; k++){
          digits+='0';
          }
       }
       else{
          digits = "";
           for(int k=0; k<4 ; k++){
          
          digits+=digits2.charAt(k);
          }
       }
       
       return digits;
    }
    
    /**
     * Takes a character and returns the number to change it to soundex
     * @param character, character to be translated
     * @return number the character matchs to in soundex translation
     */
    public static int letterCheck(char character){
       /*return the int matching the char in accordance with soundex translation*/
       int value;
       character = Character.toLowerCase(character);
       if(character == 'b' || character == 'p' || character == 'f' || character == 'v'){
          value = 1;
       }
       
       else if(character == 'c' || character == 's' || character == 'k' || character == 'g'
               || character == 'j' || character == 'q' || character == 'x' || character == 'z'){
          value = 2;
       }
       
       else if(character == 'd' || character == 't'){
          value = 3;
       }
       
       else if(character == 'l'){
          value = 4;
       }
       
       else if(character == 'm' || character == 'n'){
          value = 5;
       }
       
       else if(character == 'r'){
          value = 6;
       }
       
       else{
          value = 7;
       }
       
       return value;
      }
      
    }