import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Component, OnInit } from '@angular/core';
import { Workbook } from 'exceljs';
import * as XLSX from 'xlsx';
import { NgxSpinnerService } from 'ngx-spinner';
import { finalize } from 'rxjs';
import { AuthService } from 'src/app/auth.service';
import { ShortCutHandlerService } from 'src/app/services/short-cut-handler.service';
import { environment } from 'src/environments/environment';
const fs = require('file-saver');

const API_URL = environment.apiUrl;

@Component({
  selector: 'app-customer-mass-manager',
  templateUrl: './customer-mass-manager.component.html',
  styleUrls: ['./customer-mass-manager.component.scss']
})
export class CustomerMassManagerComponent implements OnInit {
  data!          : [][]
  progress       : boolean 
  progressStatus : string 
  totalRecords   : number 
  currentRecord  : number
  
  constructor(private shortcut : ShortCutHandlerService,
              private auth : AuthService,
              private http : HttpClient,
              private spinner: NgxSpinnerService) {
    this.progress       = false
    this.progressStatus = ''
    this.totalRecords   = 0
    this.currentRecord  = 0
  }

  ngOnInit(): void {
  }

  async exportCustomerToExcel() {
    let workbook = new Workbook();
    let worksheet = workbook.addWorksheet('CustomerSheet')
   
    worksheet.columns = [
      { header: 'NO', key: 'NO'},
      { header: 'NAME', key: 'NAME'},
      { header: 'CONTACT_NAME', key: 'CONTACT_NAME'},
      { header: 'ACTIVE', key: 'ACTIVE'},
      { header: 'TIN', key: 'TIN'},
      { header: 'VRN', key: 'VRN'},
      { header: 'CREDIT_LIMIT', key: 'CREDIT_LIMIT'},
      { header: 'INVOICE_LIMIT', key: 'INVOICE_LIMIT'},
      { header: 'CREDIT_DAYS', key: 'CREDIT_DAYS'},
      { header: 'PHYSICAL_ADDRESS', key: 'PHYSICAL_ADDRESS'},
      { header: 'POST_CODE', key: 'POST_CODE'},
      { header: 'POST_ADDRESS', key: 'POST_ADDRESS'},
      { header: 'TELEPHONE', key: 'TELEPHONE'},
      { header: 'MOBILE', key: 'MOBILE'},
      { header: 'EMAIL', key: 'EMAIL'},
      { header: 'FAX', key: 'FAX'},
      { header: 'BANK_ACCOUNT_NAME', key: 'BANK_ACCOUNT_NAME'},
      { header: 'BANK_PHYSICAL_ADDRESS', key: 'BANK_PHYSICAL_ADDRESS'},
      { header: 'BANK_POST_CODE', key: 'BANK_POST_CODE'},
      { header: 'BANK_POST_ADDRESS', key: 'BANK_POST_ADDRESS'},
      { header: 'BANK_NAME', key: 'BANK_NAME'},
      { header: 'BANK_ACCOUNT_NO', key: 'BANK_ACCOUNT_NO'},
      { header: 'BILLING_ADDRESS', key: 'BILLING_ADDRESS'},
      { header: 'SHIPPING_ADDRESS', key: 'SHIPPING_ADDRESS'}
    ];

    let options = {
      headers: new HttpHeaders().set('Authorization', 'Bearer '+this.auth.user.access_token)
    }
    this.spinner.show()
    await this.http.get<ICustomer[]>(API_URL+'/customers', options)
    .pipe(finalize(() => this.spinner.hide()))
    .toPromise()
    .then(
      data => {
        data?.forEach(element => {         
          worksheet.addRow(
            {

              NO                  : element.no,
              NAME                  : element.name,
              CONTACT_NAME          : element.contactName,
              ACTIVE                : element.active,
              TIN                   : element.tin,
              VRN                   : element.vrn,
              CREDIT_LIMIT : element.creditLimit,
              INVOICE_LIMIT : element.invoiceLimit,
              CREDIT_DAYS : element.creditDays,
              PHYSICAL_ADDRESS      : element.physicalAddress,
              POST_CODE             : element.postCode,
              POST_ADDRESS          : element.postAddress,
              TELEPHONE             : element.telephone,
              MOBILE                : element.mobile,
              EMAIL                 : element.email,
              FAX                   : element.fax,
              BANK_ACCOUNT_NAME : element.bankAccountName,
              BANK_PHYSICAL_ADDRESS : element.bankPhysicalAddress,
              BANK_POST_CODE        : element.bankPostCode,
              BANK_POST_ADDRESS     : element.bankPostAddress,
              BANK_NAME             : element.bankName,
              BANK_ACCOUNT_NO       : element.bankAcountNo

            },"n"
          )
        })
        this.spinner.hide()
      }
    )
    .catch(
      error => {
        console.log(error)
      }
    )
   
    workbook.xlsx.writeBuffer().then((data) => {
      let blob = new Blob([data], { type: 'application/vnd.openxmlformats-officedocument.spreadsheetml.sheet' });
      fs.saveAs(blob, 'CustomerMaster.xlsx');
    })
   
  }

  exportTemplateToExcel() {
    let workbook = new Workbook();
    let worksheet = workbook.addWorksheet('TemplateSheet')
   
    worksheet.columns = [
      { header: 'NO', key: 'NO'},
      { header: 'NAME', key: 'NAME'},
      { header: 'CONTACT_NAME', key: 'CONTACT_NAME'},
      { header: 'ACTIVE', key: 'ACTIVE'},
      { header: 'TIN', key: 'TIN'},
      { header: 'VRN', key: 'VRN'},
      { header: 'CREDIT_LIMIT', key: 'CREDIT_LIMIT'},
      { header: 'INVOICE_LIMIT', key: 'INVOICE_LIMIT'},
      { header: 'CREDIT_DAYS', key: 'CREDIT_DAYS'},
      { header: 'PHYSICAL_ADDRESS', key: 'PHYSICAL_ADDRESS'},
      { header: 'POST_CODE', key: 'POST_CODE'},
      { header: 'POST_ADDRESS', key: 'POST_ADDRESS'},
      { header: 'TELEPHONE', key: 'TELEPHONE'},
      { header: 'MOBILE', key: 'MOBILE'},
      { header: 'EMAIL', key: 'EMAIL'},
      { header: 'FAX', key: 'FAX'},
      { header: 'BANK_ACCOUNT_NAME', key: 'BANK_ACCOUNT_NAME'},
      { header: 'BANK_PHYSICAL_ADDRESS', key: 'BANK_PHYSICAL_ADDRESS'},
      { header: 'BANK_POST_CODE', key: 'BANK_POST_CODE'},
      { header: 'BANK_POST_ADDRESS', key: 'BANK_POST_ADDRESS'},
      { header: 'BANK_NAME', key: 'BANK_NAME'},
      { header: 'BANK_ACCOUNT_NO', key: 'BANK_ACCOUNT_NO'},
      { header: 'BILLING_ADDRESS', key: 'BILLING_ADDRESS'},
      { header: 'SHIPPING_ADDRESS', key: 'SHIPPING_ADDRESS'}
    ];
    
    workbook.xlsx.writeBuffer().then((data) => {
      let blob = new Blob([data], { type: 'application/vnd.openxmlformats-officedocument.spreadsheetml.sheet' });
      fs.saveAs(blob, 'CustomerMasterTemplate.xlsx');
    })
   
  }
  
  clearProgress(){
    this.progressStatus = ''
    this.totalRecords   = 0
    this.currentRecord  = 0
  }

  uploadCustomerFile(evt: any) {
    var customerData: [][]
    if (this.progress == true) {
      alert('Could not process, a mass operation going on')
      return
    }
    const target: DataTransfer = <DataTransfer>(evt.target)
    if (target.files.length !== 1) {
      alert("Cannot use multiple files")
      return
    }
    const reader: FileReader = new FileReader()
    reader.onload = (e: any) => {
      const bstr: string = e.target.result
      const wb: XLSX.WorkBook = XLSX.read(bstr, { type: 'binary' })
      const wsname: string = wb.SheetNames[0]
      const ws: XLSX.WorkSheet = wb.Sheets[wsname]
      customerData = (XLSX.utils.sheet_to_json(ws, { header: 1 }))

      this.progress = true
      if (this.validateCustomerMaster(customerData) == true) {
        this.progressStatus = 'Uploading... please wait'
        this.uploadCustomers(customerData)
      } else {
        alert('Invalid customer file')
      }
      this.progress = false

    }
    reader.readAsBinaryString(target.files[0])
  }

  updateCustomerFile(evt: any) {
    var customerData: [][]
    if (this.progress == true) {
      alert('Could not process, a mass operation going on')
      return
    }
    const target: DataTransfer = <DataTransfer>(evt.target)
    if (target.files.length !== 1) {
      alert("Cannot use multiple files")
      return
    }
    const reader: FileReader = new FileReader()
    reader.onload = (e: any) => {
      const bstr: string = e.target.result
      const wb: XLSX.WorkBook = XLSX.read(bstr, { type: 'binary' })
      const wsname: string = wb.SheetNames[0]
      const ws: XLSX.WorkSheet = wb.Sheets[wsname]
      customerData = (XLSX.utils.sheet_to_json(ws, { header: 1 }))

      this.progress = true
      if (this.validateCustomerMaster(customerData) == true) {
        this.progressStatus = 'Updating... please wait'
        this.updateCustomers(customerData)
      } else {
        alert('Invalid customer file')
      }
      this.progress = false
    }
    reader.readAsBinaryString(target.files[0])
  }

  validateCustomerMaster(data : any [][]) : boolean{
    this.clearProgress()
    var rows            = data.length
    var cols            = data[0].length
    this.progressStatus = 'Validating customer file'
    this.totalRecords   = rows
    var valid           = true
    /**Validate row header */
    if( data[0][0] != 'NO'                     ||
        data[0][1] != 'NAME'                   ||
        data[0][2] != 'CONTACT_NAME'           ||
        data[0][3] != 'ACTIVE'                 ||
        data[0][4] != 'TIN'                    ||
        data[0][5] != 'VRN'                    ||
        data[0][6] != 'CREDIT_LIMIT'           ||
        data[0][7] != 'INVOICE_LIMIT'          ||
        data[0][8] != 'CREDIT_DAYS'            ||
        data[0][9] != 'PHYSICAL_ADDRESS'       ||
        data[0][10] != 'POST_CODE'             ||
        data[0][11] != 'POST_ADDRESS'          ||
        data[0][12] != 'TELEPHONE'             ||
        data[0][13] != 'MOBILE'                ||
        data[0][14] != 'EMAIL'                 ||
        data[0][15] != 'FAX'                   ||
        data[0][16] != 'BANK_ACCOUNT_NAME'     ||
        data[0][17] != 'BANK_PHYSICAL_ADDRESS' ||
        data[0][18] != 'BANK_POST_CODE'        ||
        data[0][19] != 'BANK_POST_ADDRESS'     ||
        data[0][20] != 'BANK_NAME'             ||
        data[0][21] != 'BANK_ACCOUNT_NO'       ||
        data[0][22] != 'BILLING_ADDRESS'       ||
        data[0][23] != 'SHIPPING_ADDRESS'  
        
        )
    {
      valid = false
    }
    for(let i = 1; i < data.length; i++) {
      this.currentRecord = i
      /**Check for empty code and name */
      if( data[i][0] == '' ||
          data[i][2] == '' ||
          data[i][3] == ''      
        )
      {
        alert(i)
        valid = false
      }
    }
    this.clearProgress()
    return valid;
  }
  
  async uploadCustomers(dt : any [][]){
    let options = {
      headers: new HttpHeaders().set('Authorization', 'Bearer '+this.auth.user.access_token)
    }
    this.clearProgress()
    var rows = dt.length
    var cols = dt[0].length
    this.progressStatus = 'Uploading customer file'
    this.totalRecords = rows

    for(let i = 1; i < dt.length; i++) {
      this.currentRecord = i
      var customer = {
        no                  : dt[i][0],
        name                : dt[i][1],
        contactName         : dt[i][2],
        active              : dt[i][3],
        tin                 : dt[i][4],
        vrn                 : dt[i][5],
        creditLimit         : dt[i][6],
        invoiceLimit        : dt[i][7],
        creditDays          : dt[i][8],
        physicalAddress     : dt[i][9],
        postCode            : dt[i][10],
        postAddress         : dt[i][11],
        telephone           : dt[i][12],
        mobile              : dt[i][13],
        email               : dt[i][14],
        fax                 : dt[i][15],
        bankAccountName     : dt[i][16],
        bankPhysicalAddress : dt[i][17],
        bankPostCode        : dt[i][18],
        bankPostAddress     : dt[i][19],
        bankName            : dt[i][20],
        bankAccountNo       : dt[i][21],
        billingAddress      : dt[i][22],
        shippingAddress     : dt[i][23]
      }

      if(dt[i][0] == undefined){
        alert('End of file reached')
        return
      }
      this.spinner.show()
      await this.http.post(API_URL+'/customers/create', customer, options)
      .pipe(finalize(() => this.spinner.hide()))
      .toPromise()
      .catch(
        error => {
          console.log(error)
        }
      )
      }
    this.clearProgress()
  }

  async updateCustomers(dt : any [][]){
    let options = {
      headers: new HttpHeaders().set('Authorization', 'Bearer '+this.auth.user.access_token)
    }
    this.clearProgress()
    var rows = dt.length
    var cols = dt[0].length
    this.progressStatus = 'Updating customers file'
    this.totalRecords = rows

    for(let i = 1; i < dt.length; i++) {
      this.currentRecord = i
      var customer = {
        no                  : dt[i][0],
        name                : dt[i][1],
        contactName         : dt[i][2],
        active              : dt[i][3],
        tin                 : dt[i][4],
        vrn                 : dt[i][5],
        creditLimit         : dt[i][6],
        invoiceLimit        : dt[i][7],
        creditDays          : dt[i][8],
        physicalAddress     : dt[i][9],
        postCode            : dt[i][10],
        postAddress         : dt[i][11],
        telephone           : dt[i][12],
        mobile              : dt[i][13],
        email               : dt[i][14],
        fax                 : dt[i][15],
        bankAccountName     : dt[i][16],
        bankPhysicalAddress : dt[i][17],
        bankPostCode        : dt[i][18],
        bankPostAddress     : dt[i][19],
        bankName            : dt[i][20],
        bankAccountNo       : dt[i][21],
        billingAddress      : dt[i][22],
        shippingAddress     : dt[i][23]
      }

      if(dt[i][0] == undefined){
        alert('End of file reached')
        return
      }
      this.spinner.show()
      await this.http.put(API_URL+'/customers/update_by_no', customer, options)
      .pipe(finalize(() => this.spinner.hide()))
      .toPromise()
      .catch(
        error => {
          console.log(error)
        }
      )
      }
    this.clearProgress()
  }

  onFileChange(evt: any) {
    if (this.progress == true) {
      alert('Could not process, a mass operation going on')
      return
    }
    const target: DataTransfer = <DataTransfer>(evt.target)
    if (target.files.length !== 1) {
      alert("Cannot use multiple files")
      return
    }
    const reader: FileReader = new FileReader()
    reader.onload = (e: any) => {
      const bstr: string = e.target.result
      const wb: XLSX.WorkBook = XLSX.read(bstr, { type: 'binary' })
      const wsname: string = wb.SheetNames[0]
      const ws: XLSX.WorkSheet = wb.Sheets[wsname]
      this.data = (XLSX.utils.sheet_to_json(ws, { header: 1 }))
      this.progress = true
      if (this.validateData(this.data) == true) {
        this.uploadData(this.data)
      }
      this.progress = false
    }
    reader.readAsBinaryString(target.files[0])
  }

  validateData(data :  [][]) : boolean{
    var valid = true
    for(let j = 0; j < data[0].length; j++){
      //validate the row header
    }
    for(let i = 1; i < data.length; i++) {
      for(let j = 0; j < data[i].length; j++) {
      
      }
    }
    return valid;
  }

  uploadData(data : [][]){
    var object : any
    for(let i = 1; i < data.length; i++) {
      
    }
  }

  createShortCut(shortCutName : string, link : string){
    if(confirm('Create shortcut for this page?')){
      this.shortcut.createShortCut(shortCutName, link)
    }
  }

}

export interface ICustomer {
  id                  : any
  no                : string
  name                : string
  contactName         : string
  active              : boolean
  tin                 : string
  vrn                 : string
  creditLimit : number
  invoiceLimit : number
  creditDays : number
  physicalAddress     : string
  postCode            : string
  postAddress         : string
  telephone           : string
  mobile              : string
  email               : string
  fax                 : string
  bankAccountName     : string
  bankAcountNo        : string
  bankPhysicalAddress : string
  bankPostCode        : string
  bankPostAddress     : string
  bankName            : string
  bankAccountNo       : string
  billingAddress      : string
  shippingAddress     : string
}