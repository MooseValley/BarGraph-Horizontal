/*
   Author: Mike O'Malley
   Description: Bar Graph
   My solution 8-7 Application - Bar Graph of Construction Data,
   Chapter 8, p266-274.

   Structured Fortran 77 for Engineers and Scientists,
   D. M. Etter.
   (C) 1983.  ISBN: 0-8053-2520-4

My old QIT (Uni) textbook from my uni days 1983-1987 - VERY
weather beaten and worn now (almost 30 years later).



/*

Plot these 3 values:

-5  *****|
10       |**********
5        |*****


-----------------------------------------------------
Scenario #1: Simplest possible scanario

* if range of values is 100 and we have a max bar length of 100, and all values are positive, then bar length = value of each item we want to plot.

-----------------------------------------------------
Scenario #2: slightly more realistic

* if range of values is 100 and we have positive and vegative values, and max bar length of 100:

bar length = abs (value * max bar length / range)

e.g. minValue = -30
maxValue = 70
range = max + min = 100

Origin = abs (minValue)
For -30 (= min value), bar length = abs (-30 * 100 / 100) = 30
=> draw 30 stars then origin line |

For -20, bar length = abs (-20 * 100 / 100) = 20
=> draw 10 spaces, then 20 stars then origin line |

For 40, bar length = 40 * 100 / 100 = 40
=> draw minvalue spaces, then origin |, then 40 stars

-30 ******************************|
-20           ********************|
 40                               |****************************************


-----------------------------------------------------
Scenario #3: realistic

-200
-120
-8
27.5
199
300

* range of values is 500 and we have positive and vegative values, and max bar length of 80 (screen width in characters):

bar length = abs (value * max bar length / range)

Origin Offset = abs (minValue * max bar length / range) = 32
   i.e. draw 32 spaces followed by |

For -200, bar length = abs (-200 * 100 / 500) = 32 = Origin Offset
=> draw 32 stars then origin line |

For -120, bar length = abs (-120 * 100 / 500) = 19
=> draw Origin Offset - 19 (32-19) spaces and then 19 stars and then origin line |

For 199, bar length = abs (199 * 100 / 500) = 31
=> draw Origin Offset (32) spaces and then origin line | and then 31 stars.


-----------------------------------------------------
Scenario #4: realistic

27.5
199
300
875

min value > 0 so make minValue = 0
=> range of values is 0 - 875

* range of values is 875 and we have positive and vegative values, and max bar length of 80 (screen width in characters):

bar length = abs (value * max bar length / range)


This works perfectly and is exactly what I need !

See my Excel spreadsheet:
   BarGraph_Horizontal - Mike's working to get the scaling, etc right.xls


*/

/*

Sample Output:  (with additional values useful when debugging):

          minValue= -200.0
          maxValue= 300.0
     rangeOfValues= 500.0
    maxLabelLength= 7
      maxBarLength= 69
      originOffset= 200.0
scaledOriginOffset= 27
 -200.00 ***************************|
 -120.00            ****************|
   -8.00                           *|
  -27.50                         ***|
  199.00                            |***************************
  300.00                            |*****************************************

          minValue= -200.0
          maxValue= 300.0
     rangeOfValues= 500.0
    maxLabelLength= 0
      maxBarLength= 78
      originOffset= 200.0
scaledOriginOffset= 31
*******************************|
             ******************|
                              *|
                           ****|
                               |*******************************
                               |**********************************************

          minValue= -200.0
          maxValue= 300.0
     rangeOfValues= 500.0
    maxLabelLength= 7
      maxBarLength= 69
      originOffset= 200.0
scaledOriginOffset= 27
 -200.00 *                          |
 -120.00            *               |
   -8.00                           *|
  -27.50                         *  |
  199.00                            |                          *
  300.00                            |                                        *


          minValue= -30.1
          maxValue= 25.5
     rangeOfValues= 55.6
    maxLabelLength= 6
      maxBarLength= 70
      originOffset= 30.1
scaledOriginOffset= 37
  -3.30                                  ****|
  25.50                                      |********************************
  18.70                                      |***********************
 -30.10 *************************************|
   6.00                                      |*******
   3.00                                      |***

          minValue= -30.1
          maxValue= 25.5
     rangeOfValues= 55.6
    maxLabelLength= 6
      maxBarLength= 70
      originOffset= 30.1
scaledOriginOffset= 37
  -3.30                                  *   |
  25.50                                      |                               *
  18.70                                      |                      *
 -30.10 *                                    |
   6.00                                      |      *
   3.00                                      |  *


          minValue= -301.0
          maxValue= 255.0
     rangeOfValues= 556.0
    maxLabelLength= 7
      maxBarLength= 69
      originOffset= 301.0
scaledOriginOffset= 37
  -33.00                                  ****|
  255.00                                      |*******************************
  187.00                                      |***********************
 -301.00 *************************************|
   60.00                                      |*******
   30.00                                      |***

          minValue= -301.0
          maxValue= 255.0
     rangeOfValues= 556.0
    maxLabelLength= 7
      maxBarLength= 69
      originOffset= 301.0
scaledOriginOffset= 37
  -33.00                                  *   |
  255.00                                      |                              *
  187.00                                      |                      *
 -301.00 *                                    |
   60.00                                      |      *
   30.00                                      |  *

*/

public class BarGraph_Horizontal
{
   public static String GetStringOfChars (char aChar, int strLength, boolean outputOnlyEndChar)
   {
      String outString = "";

      if (strLength > 0)
      {
         for (int k = 0; k < strLength - 1; k++)
         {
            if (outputOnlyEndChar == true)
               outString = outString + ' ';
            else
               outString = outString + aChar;
         }
         outString = outString + aChar; // add on the last char.
      }

      return outString;
   }

   public static String BarGraph (double dataArray [], String dataLabels [], char barChar, boolean outputOnlyEndChar)
   {
      String outString = "";
      double minValue = dataArray [0];
      double maxValue = dataArray [0];
      double scalingFactor;
      double rangeOfValues;
      int screenWidth    = 79; // characters.
      int maxBarLength   = 0;
      int maxLabelLength = 0;
      int barLength = 0;
      double originOffset = 0;
      int scaledOriginOffset = 0;
      String originOffsetString;

      if (dataArray.length == 0)
      {
         System.out.println ("Error: dataArray is empty");

      }

      if (barChar == ' ')
         barChar = '*';

      for (int k = 0; k < dataArray.length; k++)
      {
         if (minValue > dataArray [k])
            minValue = dataArray [k];
         if (maxValue < dataArray [k])
            maxValue = dataArray [k];
      }

      if (minValue > 0)
         minValue = 0.0;

      rangeOfValues = maxValue - minValue;

      // Determine the maximum label length - so we know how far in to offset the BarGraph.
      if (dataLabels != null)
      {
         for (int k = 0; k < dataLabels.length; k++)
         {
            if (maxLabelLength < dataLabels [k].length())
               maxLabelLength = dataLabels [k].length();
         }
      }


      if ((minValue > 0) && (maxValue > 0)) // // Both > 0
         originOffset = 0;

      else if ((minValue < 0) && (maxValue > 0))
         originOffset = Math.abs (minValue);

      else if ((minValue < 0) && (maxValue < 0)) // Both < 0
         originOffset = Math.abs (maxValue);

      //maxLabelLength = maxLabelLength + 1;  // Leave a gap between label and bar line.

      if (maxLabelLength > 0)
         maxBarLength = screenWidth - maxLabelLength - 1 - 2; // -2 for the space gaps added below.
      else
         maxBarLength = screenWidth - maxLabelLength - 1;

      scaledOriginOffset = (int) (originOffset * maxBarLength / rangeOfValues);

      outString = outString + "\n" + "          minValue= " + minValue;
      outString = outString + "\n" + "          maxValue= " + maxValue;
      outString = outString + "\n" + "     rangeOfValues= " + rangeOfValues;
      outString = outString + "\n" + "    maxLabelLength= " + maxLabelLength;
      outString = outString + "\n" + "      maxBarLength= " + maxBarLength;
      outString = outString + "\n" + "      originOffset= " + originOffset;
      outString = outString + "\n" + "scaledOriginOffset= " + scaledOriginOffset;

      // OK, let's generate the Bar Graph.
      for (int k = 0; k < dataArray.length; k++)
      {
         outString = outString + "\n";


         if (maxLabelLength > 0)
         {
            outString = outString + " ";  // Leave a gap side of screen and label.

            if (k < dataLabels.length)  // Do we have a label for this line ?
               outString = outString + String.format ("%" + maxLabelLength + "s", dataLabels [k]);
            else
               outString = outString + String.format ("%" + maxLabelLength + "s", "");

            outString = outString + " ";  // Leave a gap between label and bar line.
         }

         // bar length = abs (value * max bar length / range)
         barLength = (int) (Math.abs(dataArray [k] * maxBarLength / rangeOfValues));

         //System.out.println ("dataArray [" + k + "]=" + dataArray [k] + ", barLength=" + barLength);

         if (dataArray [k] < 0)
         {
            if (outputOnlyEndChar == true)
            {
               outString = outString +
                           GetStringOfChars (' ', scaledOriginOffset - barLength, false) + barChar +
                           GetStringOfChars (' ', barLength - 1, outputOnlyEndChar) + "|";
            }
            else
            {
               outString = outString +
                           GetStringOfChars (' ', scaledOriginOffset - barLength, false) +
                           GetStringOfChars (barChar, barLength, outputOnlyEndChar) + "|";
            }
         }
         else
         {
            outString = outString +
                        GetStringOfChars (' ', scaledOriginOffset, false)    + "|" +
                        GetStringOfChars (barChar, barLength, outputOnlyEndChar);
         }

         //System.out.println ("dataArray [k]=" + dataArray [k] + ", barLength=" + barLength);
      }

      return outString;
   }


   public static String [] CreateLabelsFromValues (double dataArray [], String formatString)
   {
      String dataLabels [] = new String [dataArray.length];

      if (formatString == "")
         formatString = "%,.2f"; // Default format if none is provided.

      for (int k = 0; k < dataLabels.length; k++)
      {
         dataLabels [k] = String.format (formatString, dataArray [k]);
      }

      return dataLabels;
   }


   public static void main (String [] args)
   {
      double myArray1 []  = {-200, -120, -8, -27.5, 199, 300};
      double myArray2 []  = {-3.3, 25.5, 18.7, -30.1, 6, 3};
      double myArray3 [] = {-33.0, 255.0, 187.0, -301.0, 60, 30};

      String dataLabels1 [] = CreateLabelsFromValues (myArray1, "");
      String dataLabels2 [] = CreateLabelsFromValues (myArray2, "");
      String dataLabels3 [] = CreateLabelsFromValues (myArray3, "");

      System.out.println ();
      System.out.println (BarGraph (myArray1, dataLabels1, ' ', false));
      System.out.println (BarGraph (myArray1, null,        ' ', false));
      System.out.println (BarGraph (myArray1, dataLabels1, ' ', true));
//System.exit(0);

      System.out.println ();
      System.out.println (BarGraph (myArray2, dataLabels2, ' ', false));
      System.out.println (BarGraph (myArray2, dataLabels2, ' ', true));

      System.out.println ();
      System.out.println (BarGraph (myArray3, dataLabels3, ' ', false));
      System.out.println (BarGraph (myArray3, dataLabels3, ' ', true));
   }
}
