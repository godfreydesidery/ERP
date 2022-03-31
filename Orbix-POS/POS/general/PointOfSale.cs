using POS.general;
using System;
using System.Collections.Generic;
using System.IO;
using System.Linq;
using System.Text;
using System.Threading.Tasks;
using System.Windows.Forms;

namespace POS.general
{
    class PointOfSale
    {
        /// <summary>
        /// pos printer settings
        /// </summary>
        public static string posPrinterLogicName = "";
        public static string posCashDrawerLogicName = "";
        public static string posLineDisplayLogicName = "";
        public static string posPrinterEnabled = "";

        /// <summary>
        /// 'fiscal printer settings
        /// </summary>
        public static string strLogicalName = InstalledPOSDevices.posLogicName;  // get the available fiscal printer logical name
        public static string fiscalPrinterDeviceName = "";
        public static string operatorName = "";
        public static string operatorPassword = "";
        public static string port = "";
        public static string drawer = "";
        public static string fiscalPrinterEnabled = "";
        /// <summary>
        /// function to print receipt
        /// </summary>
        /// 
        /// <param name="tillNo"></param>
        /// <param name="receiptNo"></param>
        /// <param name="date_"></param>
        /// <param name="TIN"></param>
        /// <param name="VRN"></param>
        /// <param name="itemCode"></param>
        /// <param name="descr"></param>
        /// <param name="qty"></param>
        /// <param name="price"></param>
        /// <param name="tax"></param>
        /// <param name="amount"></param>
        /// <param name="subTotal"></param>
        /// <param name="VAT"></param>
        /// <param name="grandTotal"></param>
        /// <returns></returns>
        private static RawPrinterHelper prn = new RawPrinterHelper();
        private static string eClear = (char)27 + "@";
        private static string eCentre = (char)27 + (char)97 + "1";
        private static string eLeft = (char)27 + (char)97 + "0";
        private static string eRight = (char)27 + (char)97 + "2";
        private static string eDrawer = eClear + (char)27 + "p" + (char)0 + ".}";
        private static string eCut = (char)27 + "i" + System.Environment.NewLine;
        private static string eSmlText = (char)27 + "!" + (char)1;
        private static string eNmlText = (char)27 + "!" + (char)0;
        private static string eInit = eNmlText + (char)13 + (char)27 +"c6" + (char)1 + (char)27 + "R3" + System.Environment.NewLine;
        
        private static string eBigCharOn = (char)27 + "!" + (char)56;
        private static string eBigCharOff = (char)27 + "!" + (char)0;

        public static object printFiscalReceipt(string tillNo, string receiptNo, string date_, string TIN, string VRN, string[] itemCode, string[] descr, string[] qty, string[] price, string[] tax, string[] amount, string subTotal, string VAT, string grandTotal, string cash, string balance)
        {
            string fileName = "Receipt";
            string strFile = System.Environment.SpecialFolder.MyDocuments + fileName + ".txt";
            StreamWriter sw;
            try
            {
                if (!File.Exists(strFile))
                {
                    sw = File.CreateText(strFile);
                }
                else
                {
                    File.WriteAllText(strFile, "");
                    sw = File.AppendText(strFile);
                }

                string str = "";



                // sw.WriteLine("R_TXT ""Receipt :" + receiptNo + """")
                str = str + "R_TXT \"Receipt :" + receiptNo + "\"" + System.Environment.NewLine;
                // sw.WriteLine("R_TXT ""Code  Qty Price@ """)
                str = str + "R_TXT \"Code  Qty Price@ \"" + System.Environment.NewLine;
                // sw.WriteLine("R_TXT ""Description """)
                str = str + "R_TXT \"Description \"" + System.Environment.NewLine;
                // sw.WriteLine("R_TXT ""===================================== """)
                str = str + "R_TXT \"===================================== \"" + System.Environment.NewLine;
                for (int i = 0, loopTo = descr.Length - 1; i <= loopTo; i++)
                {
                    // sw.WriteLine("R_TRP """ + itemCode(i) + """" + """""" + qty(i).Replace(",", "") + "* " + price(i) + "V4")
                    str = str + "R_TRP \"" + itemCode[i] + "\"" + "\"\"" + qty[i].Replace(",", "") + "* " + price[i] + "V4" + System.Environment.NewLine;
                    // sw.WriteLine("R_TXT """ + descr(i) + """")
                    str = str + "R_TXT \"" + descr[i] + "\"" + System.Environment.NewLine;
                    // sw.WriteLine("R_TXT ""------------------------------------- """)
                    str = str + "R_TXT \"------------------------------------- \"" + System.Environment.NewLine;
                }
                // sw.WriteLine("R_TXT ""===================================== """)
                str = str + "R_TXT \"===================================== \"" + System.Environment.NewLine;
                sw.WriteLine("R_STT P");
                str = str + "R_STT P";
                sw.Write(str);
                sw.Close();
            }
            catch (Exception e)
            {
                MessageBox.Show(e.StackTrace);
            }
            return null;
        }

        public static void printReceipt(string tillNo, string receiptNo, string date_, string TIN, string VRN, string[] code, string[] descr, string[] qty, string[] price, string[] tax, string[] amount, string subTotal, string VAT, string grandTotal, string cash, string balance)
        {
            string space = "";
            for (int i = 1, loopTo = (40 - Company.NAME.ToString().Length) / 2; i <= loopTo; i++)
                space = space + " ";
            string companyName = space + Company.NAME;
            space = "";
            for (int i = 1, loopTo1 = (40 - Company.POST_CODE.ToString().Length) / 2; i <= loopTo1; i++)
                space = space + " ";
            string postCode = space + Company.POST_CODE;
            space = "";
            for (int i = 1, loopTo2 = (40 - Company.PHYSICAL_ADDRESS.ToString().Length) / 2; i <= loopTo2; i++)
                space = space + " ";
            string physicalAddress = space + Company.PHYSICAL_ADDRESS;
            space = "";
            for (int i = 1, loopTo3 = (40 - Company.TELEPHONE.ToString().Length) / 2; i <= loopTo3; i++)
                space = space + " ";
            string telephone = space + Company.TELEPHONE;
            space = "";
            for (int i = 1, loopTo4 = (40 - Company.EMAIL.ToString().Length) / 2; i <= loopTo4; i++)
                space = space + " ";
            string email = space + Company.EMAIL;
            string fDateTime;
            string strOutputData = "";
            object CRLF;
            object ESC;
            fDateTime = DateTime.Now.ToString("yyyy/MM/dd HH:mm:ss"); // System date and time
            CRLF = (char)13 + (char)10;
            ESC = '\u001b';// check here
            strOutputData = strOutputData + companyName + CRLF;
            strOutputData = strOutputData + postCode + CRLF;
            strOutputData = strOutputData + physicalAddress + CRLF;
            strOutputData = strOutputData + telephone + CRLF;
            strOutputData = strOutputData + email + CRLF + CRLF;
            strOutputData = strOutputData + "       ***  Sales Receipt  ***" + CRLF;
            strOutputData = strOutputData + "TIN:        " + TIN + CRLF;
            strOutputData = strOutputData + "VRN:        " + VRN + CRLF;
            strOutputData = strOutputData + "Till No:    " + tillNo + CRLF;
            strOutputData = strOutputData + "Receipt No: " + receiptNo + CRLF;
            strOutputData = strOutputData + CRLF;
            strOutputData = strOutputData + "CODE        QTY   PRICE@     AMOUNT" + CRLF;
            strOutputData = strOutputData + "DESCRIPTION" + CRLF;
            strOutputData = strOutputData + "====================================" + CRLF;

            for (int i = 0; i < code.Length; i++)
            {
                strOutputData = strOutputData + code[i] + " x " + qty[i] + "  " + price[i] + "  " + amount[i] + CRLF;
                strOutputData = strOutputData + descr[i] + CRLF;
            }
            strOutputData = strOutputData + "------------------------------------" + CRLF;
            strOutputData = strOutputData + "Sub Total                   " + subTotal + CRLF;
            strOutputData = strOutputData + "Tax                         " + VAT + CRLF;
            strOutputData = strOutputData + "Total Amount                " + grandTotal + CRLF;
            strOutputData = strOutputData + "====================================" + CRLF;
            strOutputData = strOutputData + "Cash              " + cash + CRLF;
            strOutputData = strOutputData + "Balance           " + balance + CRLF;
            strOutputData = strOutputData + "====================================" + CRLF;
            strOutputData = strOutputData + "        You are Welcome !" + CRLF;
            strOutputData = strOutputData + "Sale Date&Time : " + fDateTime + CRLF + CRLF;
            strOutputData = strOutputData + CRLF;
            strOutputData = strOutputData + "  Served by: " + User.FIRST_NAME + " " + User.LAST_NAME + CRLF;
            strOutputData = strOutputData + "\u001d";
            try
            {
                prn.OpenPrint(posPrinterLogicName);
                prn.SendStringToPrinter(posPrinterLogicName, strOutputData); // Print(strOutputData);
                prn.ClosePrint();
            }
            catch (Exception)
            {
            }
        }  
    }
}
