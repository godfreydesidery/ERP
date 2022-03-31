using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace POS.general
{
    class CashPickUp
    {
        public string id { get; set; }
        public double amount { get; set; }
        public string details { get; set; }
        public DateTime pickedAt { get; set; }
        public Till till { get; set; } = new Till();
        public User pickedBy { get; set; } = new User();
    }
}
