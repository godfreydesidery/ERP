using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace POS.general
{
    class RandomKeyGenerator
    {
        // <date>27072005</date><time>070339</time>
        // <type>class</type>
        // <summary>
        // REQUIRES PROPERTIES: KeyLetters, KeyNumbers, MaxChars
        // </summary>
        private string Key_Letters;
        private string Key_Numbers;
        private int Key_Chars;
        private char[] LettersArray;
        private char[] NumbersArray;

        // <date>27072005</date><time>071924</time>
        // <type>property</type>
        // <summary>
        // WRITE ONLY PROPERTY. HAS TO BE SET BEFORE CALLING GENERATE()
        // </summary>
        protected internal string KeyLetters
        {
            set
            {
                Key_Letters = value;
            }
        }

        // <date>27072005</date><time>071924</time>
        // <type>property</type>
        // <summary>
        // WRITE ONLY PROPERTY. HAS TO BE SET BEFORE CALLING GENERATE()
        // </summary>
        protected internal string KeyNumbers
        {
            set
            {
                Key_Numbers = value;
            }
        }

        // <date>27072005</date><time>071924</time>
        // <type>property</type>
        // <summary>
        // WRITE ONLY PROPERTY. HAS TO BE SET BEFORE CALLING GENERATE()
        // </summary>
        protected internal int KeyChars
        {
            set
            {
                Key_Chars = value;
            }
        }

        // <date>27072005</date><time>072344</time>
        // <type>function</type>
        // <summary>
        // GENERATES A RANDOM STRING OF LETTERS AND NUMBERS.
        // LETTERS CAN BE RANDOMLY CAPITAL OR SMALL.
        // </summary>
        // <returns type="String">RETURNS THE
        // RANDOMLY GENERATED KEY</returns>
        public string Generate111()
        {
            int i_key;
            float Random1;
            short arrIndex;
            var sb = new StringBuilder();
            string RandomLetter;

            // CONVERT LettersArray & NumbersArray TO CHARACTR ARRAYS
            LettersArray = Key_Letters.ToCharArray();
            NumbersArray = Key_Numbers.ToCharArray();
            var loopTo = Key_Chars;
            for (i_key = 1; i_key <= loopTo; i_key++)
            {
                // START THE CLOCK    - LAITH - 27/07/2005 18:01:18 -
                Random1 = (new Random()).Next();
                arrIndex = -1;
                // IF THE VALUE IS AN EVEN NUMBER WE GENERATE A LETTER,
                // OTHERWISE WE GENERATE A NUMBER  
                // - LAITH - 27/07/2005 18:02:55 -
                // THE NUMBER '111' WAS RANDOMLY CHOSEN. ANY NUMBER
                // WILL DO, WE JUST NEED TO BRING THE VALUE
                // ABOVE '0'     - LAITH - 27/07/2005 18:40:48 -
                if ((int)(Random1 * 111f) % 2 == 0)
                {
                    // GENERATE A RANDOM INDEX IN THE LETTERS
                    // CHARACTER ARRAY   - LAITH - 27/07/2005 18:47:44 -
                    while (arrIndex < 0)
                        arrIndex = Convert.ToInt16(LettersArray.GetUpperBound(0) * Random1);

                    RandomLetter = LettersArray[arrIndex].ToString();
                    // CREATE ANOTHER RANDOM NUMBER. IF IT IS ODD,
                    // WE CAPITALIZE THE LETTER
                    // - LAITH - 27/07/2005 18:55:59 -
                    if ((int)(arrIndex * Random1 * 99f) % 2 != 0)
                    {
                        RandomLetter = LettersArray[arrIndex].ToString();
                        RandomLetter = RandomLetter.ToUpper();
                    }

                    sb.Append(RandomLetter);
                }
                else
                {
                    // GENERATE A RANDOM INDEX IN THE NUMBERS
                    // CHARACTER ARRAY   - LAITH - 27/07/2005 18:47:44 -
                    while (arrIndex < 0)
                        arrIndex = Convert.ToInt16(NumbersArray.GetUpperBound(0) * Random1);

                    sb.Append(NumbersArray[arrIndex]);
                }
            }

            return sb.ToString();
        }
    }
}
