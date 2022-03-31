using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace POS.general
{
    class Float
    {
        public string id { get; set; }
        public double addition { get; set; }
        public double deduction { get; set; }
        public DateTime registeredAt { get; set; }
        public Till till { get; set; } = new Till();
        public User registeredBy { get; set; } = new User();
    }
}
