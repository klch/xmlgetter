/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package xmlgetter.db;

/**
 *
 * @author Nickolay
 */
public class KodeNum {

    public static String resolveKodeNum(String aLetter, int aNumInt, int aNumLength)
    {
        String letter = aLetter;
        int numInt = aNumInt;
        int numLength = aNumLength;
        String num = Integer.toString(numInt);
        String fNumber = num;
        for(int i = 0; i < numLength - num.length(); i++ )
        {
            fNumber = "0" + fNumber;
        }
        fNumber = letter + fNumber;
        return fNumber;
    }
    private KodeNum() throws Exception
    {
        throw new Exception();
    }
}
