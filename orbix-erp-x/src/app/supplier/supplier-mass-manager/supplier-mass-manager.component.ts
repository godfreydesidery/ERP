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
  selector: 'app-supplier-mass-manager',
  templateUrl: './supplier-mass-manager.component.html',
  styleUrls: ['./supplier-mass-manager.component.scss']
})
export class SupplierMassManagerComponent implements OnInit {
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

  async exportSupplierToExcel() {
    let workbook = new Workbook();
    let worksheet = workbook.addWorksheet('SupplierSheet')
   
    worksheet.columns = [
      { header: 'CODE', key: 'CODE'},
      { header: 'NAME', key: 'NAME'},
      { header: 'CONTACT_NAME', key: 'CONTACT_NAME'},
      { header: 'ACTIVE', key: 'ACTIVE'},
      { header: 'TIN', key: 'TIN'},
      { header: 'VRN', key: 'VRN'},
      { header: 'TERMS_OF_CONTRACT', key: 'TERMS_OF_CONTRACT'},
      { header: 'PHYSICAL_ADDRESS', key: 'PHYSICAL_ADDRESS'},
      { header: 'POST_CODE', key: 'POST_CODE'},
      { header: 'POST_ADDRESS', key: 'POST_ADDRESS'},
      { header: 'TELEPHONE', key: 'TELEPHONE'},
      { header: 'MOBILE', key: 'MOBILE'},
      { header: 'EMAIL', key: 'EMAIL'},
      { header: 'FAX', key: 'FAX'},
      { header: 'BANK_PHYSICAL_ADDRESS', key: 'BANK_PHYSICAL_ADDRESS'},
      { header: 'BANK_POST_CODE', key: 'BANK_POST_CODE'},
      { header: 'BANK_POST_ADDRESS', key: 'BANK_POST_ADDRESS'},
      { header: 'BANK_NAME', key: 'BANK_NAME'},
      { header: 'BANK_ACCOUNT_NO', key: 'BANK_ACCOUNT_NO'}
    ];

    let options = {
      headers: new HttpHeaders().set('Authorization', 'Bearer '+this.auth.user.access_token)
    }
    this.spinner.show()
    await this.http.get<ISupplier[]>(API_URL+'/suppliers', options)
    .pipe(finalize(() => this.spinner.hide()))
    .toPromise()
    .then(
      data => {
        data?.forEach(element => {         
          worksheet.addRow(
            {

              CODE                  : element.code,
              NAME                  : element.name,
              CONTACT_NAME          : element.contactName,
              ACTIVE                : element.active,
              TIN                   : element.tin,
              VRN                   : element.vrn,
              TERMS_OF_CONTRACT     : element.termsOfContract,
              PHYSICAL_ADDRESS      : element.physicalAddress,
              POST_CODE             : element.postCode,
              POST_ADDRESS          : element.postAddress,
              TELEPHONE             : element.telephone,
              MOBILE                : element.mobile,
              EMAIL                 : element.email,
              FAX                   : element.fax,
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
      fs.saveAs(blob, 'SupplierMaster.xlsx');
    })
   
  }

  exportTemplateToExcel() {
    let workbook = new Workbook();
    let worksheet = workbook.addWorksheet('TemplateSheet')
   
    worksheet.columns = [
      { header: 'CODE', key: 'CODE'},
      { header: 'NAME', key: 'NAME'},
      { header: 'CONTACT_NAME', key: 'CONTACT_NAME'},
      { header: 'ACTIVE', key: 'ACTIVE'},
      { header: 'TIN', key: 'TIN'},
      { header: 'VRN', key: 'VRN'},
      { header: 'TERMS_OF_CONTRACT', key: 'TERMS_OF_CONTRACT'},
      { header: 'PHYSICAL_ADDRESS', key: 'PHYSICAL_ADDRESS'},
      { header: 'POST_CODE', key: 'POST_CODE'},
      { header: 'POST_ADDRESS', key: 'POST_ADDRESS'},
      { header: 'TELEPHONE', key: 'TELEPHONE'},
      { header: 'MOBILE', key: 'MOBILE'},
      { header: 'EMAIL', key: 'EMAIL'},
      { header: 'FAX', key: 'FAX'},
      { header: 'BANK_PHYSICAL_ADDRESS', key: 'BANK_PHYSICAL_ADDRESS'},
      { header: 'BANK_POST_CODE', key: 'BANK_POST_CODE'},
      { header: 'BANK_POST_ADDRESS', key: 'BANK_POST_ADDRESS'},
      { header: 'BANK_NAME', key: 'BANK_NAME'},
      { header: 'BANK_ACCOUNT_NO', key: 'BANK_ACCOUNT_NO'}
    ];
    
    workbook.xlsx.writeBuffer().then((data) => {
      let blob = new Blob([data], { type: 'application/vnd.openxmlformats-officedocument.spreadsheetml.sheet' });
      fs.saveAs(blob, 'SupplierMasterTemplate.xlsx');
    })
   
  }
  
  clearProgress(){
    this.progressStatus = ''
    this.totalRecords   = 0
    this.currentRecord  = 0
  }

  uploadSupplierFile(evt: any) {
    var supplierData: [][]
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
      supplierData = (XLSX.utils.sheet_to_json(ws, { header: 1 }))

      this.progress = true
      if (this.validateSupplierMaster(supplierData) == true) {
        this.progressStatus = 'Uploading... please wait'
        this.uploadSuppliers(supplierData)
      } else {
        alert('Invalid supplier file')
      }
      this.progress = false

    }
    reader.readAsBinaryString(target.files[0])
  }

  updateSupplierFile(evt: any) {
    var supplierData: [][]
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
      supplierData = (XLSX.utils.sheet_to_json(ws, { header: 1 }))

      this.progress = true
      if (this.validateSupplierMaster(supplierData) == true) {
        this.progressStatus = 'Updating... please wait'
        this.updateSuppliers(supplierData)
      } else {
        alert('Invalid supplier file')
      }
      this.progress = false
    }
    reader.readAsBinaryString(target.files[0])
  }

  validateSupplierMaster(data : any [][]) : boolean{
    this.clearProgress()
    var rows            = data.length
    var cols            = data[0].length
    this.progressStatus = 'Validating supplier file'
    this.totalRecords   = rows
    var valid           = true
    /**Validate row header */
    if( data[0][0] != 'CODE'                   ||
        data[0][1] != 'NAME'                   ||
        data[0][2] != 'CONTACT_NAME'           ||
        data[0][3] != 'ACTIVE'                 ||
        data[0][4] != 'TIN'                    ||
        data[0][5] != 'VRN'                    ||
        data[0][6] != 'TERMS_OF_CONTRACT'      ||
        data[0][7] != 'PHYSICAL_ADDRESS'       ||
        data[0][8] != 'POST_CODE'              ||
        data[0][9] != 'POST_ADDRESS'           ||
        data[0][10] != 'TELEPHONE'             ||
        data[0][11] != 'MOBILE'                ||
        data[0][12] != 'EMAIL'                 ||
        data[0][13] != 'FAX'                   ||
        data[0][14] != 'BANK_PHYSICAL_ADDRESS' ||
        data[0][15] != 'BANK_POST_CODE'        ||
        data[0][16] != 'BANK_POST_ADDRESS'     ||
        data[0][17] != 'BANK_NAME'             ||
        data[0][18] != 'BANK_ACCOUNT_NO'  
        
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
  
  async uploadSuppliers(dt : any [][]){
    let options = {
      headers: new HttpHeaders().set('Authorization', 'Bearer '+this.auth.user.access_token)
    }
    this.clearProgress()
    var rows = dt.length
    var cols = dt[0].length
    this.progressStatus = 'Uploading supplier file'
    this.totalRecords = rows

    for(let i = 1; i < dt.length; i++) {
      this.currentRecord = i
      var supplier = {
        code                : dt[i][0],
        name                : dt[i][1],
        contactName         : dt[i][2],
        active              : dt[i][3],
        tin                 : dt[i][4],
        vrn                 : dt[i][5],
        termsOfContract     : dt[i][6],
        physicalAddress     : dt[i][7],
        postCode            : dt[i][8],
        postAddress         : dt[i][9],
        telephone           : dt[i][10],
        mobile              : dt[i][11],
        email               : dt[i][12],
        fax                 : dt[i][13],
        bankPhysicalAddress : dt[i][14],
        bankPostCode        : dt[i][15],
        bankPostAddress     : dt[i][16],
        bankName            : dt[i][17],
        bankAccountNo       : dt[i][18]
      }

      if(dt[i][0] == undefined){
        alert('End of file reached')
        return
      }
      this.spinner.show()
      await this.http.post(API_URL+'/suppliers/create', supplier, options)
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

  async updateSuppliers(dt : any [][]){
    let options = {
      headers: new HttpHeaders().set('Authorization', 'Bearer '+this.auth.user.access_token)
    }
    this.clearProgress()
    var rows = dt.length
    var cols = dt[0].length
    this.progressStatus = 'Updating suppliers file'
    this.totalRecords = rows

    for(let i = 1; i < dt.length; i++) {
      this.currentRecord = i
      var supplier = {
        code                : dt[i][0],
        name                : dt[i][1],
        contactName         : dt[i][2],
        active              : dt[i][3],
        tin                 : dt[i][4],
        vrn                 : dt[i][5],
        termsOfContract     : dt[i][6],
        physicalAddress     : dt[i][7],
        postCode            : dt[i][8],
        postAddress         : dt[i][9],
        telephone           : dt[i][10],
        mobile              : dt[i][11],
        email               : dt[i][12],
        fax                 : dt[i][13],
        bankPhysicalAddress : dt[i][14],
        bankPostCode        : dt[i][15],
        bankPostAddress     : dt[i][16],
        bankName            : dt[i][17],
        bankAccountNo       : dt[i][18]
      }

      if(dt[i][0] == undefined){
        alert('End of file reached')
        return
      }
      this.spinner.show()
      await this.http.put(API_URL+'/suppliers/update_by_code', supplier, options)
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

export interface ISupplier {
  id                  : any
  code                : string
  name                : string
  contactName         : string
  active              : boolean
  tin                 : string
  vrn                 : string
  termsOfContract     : string
  physicalAddress     : string
  postCode            : string
  postAddress         : string
  telephone           : string
  mobile              : string
  email               : string
  fax                 : string
  bankAcountNo        : string
  bankPhysicalAddress : string
  bankPostCode        : string
  bankPostAddress     : string
  bankName            : string
  bankAccountNo       : string
}