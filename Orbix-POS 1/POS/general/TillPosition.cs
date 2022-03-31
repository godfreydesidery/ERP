using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace POS.general
{
    class TillPosition
    {
        public string id { get; set; }
        public double cash { get; set; }
        public double voucher { get; set; }
        public double deposit { get; set; }
        public double loyalty { get; set; }
        public double crCard { get; set; }
        public double cheque { get; set; }
        public double cap { get; set; }
        public double invoice { get; set; }
        public double crNote { get; set; }
        public double mobile { get; set; }
        public double other { get; set; }
        public double floatBalance { get; set; }
        public Till till { get; set; } = new Till();
    }
}
