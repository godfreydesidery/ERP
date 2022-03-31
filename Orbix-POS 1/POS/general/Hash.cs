using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace POS.general
{
    class Hash
    {

       
        public static object make(string password)
        {
            string hashedPassword = "";
            var crypt = new Crypt();
            hashedPassword = crypt.make(password);
            return hashedPassword;
        }

        public static object check(string userPassword, string hashedPassword)
        {
            bool match = false;
            var crypt = new Crypt();
            if (crypt.check(userPassword, hashedPassword) == true)
            {
                match = true;
            }
            // If userPassword = hashedPassword Then 'this should be used until the hashing has fully implemented
            // 'match = True
            // End If
            return match;
        }

        // Public Shared Function make(password As String)
        // Dim hashedPassword As String = ""

        // Return hashedPassword
        // End Function
        // Public Shared Function check(userPassword As String, hashedPassword As String)
        // Dim match As Boolean = False
        // If userPassword = hashedPassword Then
        // match = True
        // End If
        // Return match
        // End Function
        
    }
}
