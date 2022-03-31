using Newtonsoft.Json;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace POS.general
{
    class Product
    {       
        public string id { get; set; }
        public string primary { get; set; }
        public string code { get; set; }
        public string description { get; set; }
        public string shortDescription { get; set; }
        public string commonDescription { get; set; }
        public string standardUom { get; set; }
        public double packSize { get; set; }
        public string ingredients { get; set; }
        public double costPriceVatIncl { get; set; }
        public double costPriceVatExcl { get; set; }
        public double sellingPriceVatIncl { get; set; }
        public double sellingPriceVatExcl { get; set; }
        public double profitMargin { get; set; }
        public double vat { get; set; }
        public double discount { get; set; }
        public double stock { get; set; }
        public double minimumInventory { get; set; }
        public double maximumInventory { get; set; }
        public double defaultReorderLevel { get; set; }
        public double defaultReorderQty { get; set; }
        public string status { get; set; }
        public bool sellable { get; set; }
        public bool returnable { get; set; }

        public List<string> getDescriptions()
        {
            // Returns a list of descriptions of all the products
            var list = new List<string>();
            try
            {
                var response = new object();
                var json = new Newtonsoft.Json.Linq.JObject();
                response = Web.get_("products/get_descriptions");
                list = JsonConvert.DeserializeObject<List<string>>(response.ToString());
                return list;
            }
            catch (Exception)
            {
                return list;
            }              
        }
    }  
}
