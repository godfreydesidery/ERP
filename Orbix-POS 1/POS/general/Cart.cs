using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace POS.general
{
    class Cart
    {
        public static string NO = "";
        public string id { get; set; }
        public string no { get; set; }
        public DateTime cartDate { get; set; }
        public Till till { get; set; } = new Till();
        public List<CartDetail> cartDetails { get; set; } = new List<CartDetail>();
        public CartDetail cartDetail { get; set; }
    }
}
