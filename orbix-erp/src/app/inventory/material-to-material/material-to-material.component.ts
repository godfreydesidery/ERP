import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Component, OnInit } from '@angular/core';
import { ModalDismissReasons, NgbModal } from '@ng-bootstrap/ng-bootstrap';
import { NgxSpinnerService } from 'ngx-spinner';
import * as pdfMake from 'pdfmake/build/pdfmake';
import { finalize } from 'rxjs';
import { AuthService } from 'src/app/auth.service';
import { DataService } from 'src/app/services/data.service';
import { ErrorHandlerService } from 'src/app/services/error-handler.service';
import { ShortCutHandlerService } from 'src/app/services/short-cut-handler.service';
import { environment } from 'src/environments/environment';

const API_URL = environment.apiUrl;
@Component({
  selector: 'app-material-to-material',
  templateUrl: './material-to-material.component.html',
  styleUrls: ['./material-to-material.component.scss']
})
export class MaterialToMaterialComponent implements OnInit {
  closeResult    : string = ''

  logo!              : any
  address  : any 

  blank          : boolean = false
  
  id                       : any
  no                       : string
  reason                   : string
  status                   : string
  comments!                : string
  created                  : string
  approved                 : string
  materialToMaterialInitials : IInitialMaterial[]
  materialToMaterialFinals   : IFinalMaterial[]



  //initials
  initialId                  : any
  initialCode                : any
  initialDescription         : any
  initialQty                 : number
  initialUom                 : string
  initialCostPriceVatExcl    : number
  initialCostPriceVatIncl    : number
  initialSellingPriceVatExcl : number
  initialSellingPriceVatIncl : number
  


  //finals
  finalId                  : any
  finalCode                : any
  finalDescription         : any
  finalQty                 : number
  finalUom                 : string    
  finalCostPriceVatExcl    : number
  finalCostPriceVatIncl    : number
  finalSellingPriceVatExcl : number
  finalSellingPriceVatIncl : number

  descriptions : string[]

  materialToMaterials : IMaterialToMaterial[]

  constructor(private auth : AuthService,
              private http :HttpClient,
              private shortcut : ShortCutHandlerService, 
              private modalService: NgbModal,
              private data : DataService,
              private spinner: NgxSpinnerService) {
    this.id                       = null
    this.no                       = ''
    this.reason                   = ''
    this.status                   = ''
    this.comments                 = ''
    this.created                  = ''
    this.approved                 = ''
    this.materialToMaterialInitials = []
    this.materialToMaterialFinals   = []

    this.initialId                  = null
    this.initialCode                = ''  
    this.initialQty                 = 0
    this.initialUom                 = ''  
    this.initialCostPriceVatExcl    = 0
    this.initialCostPriceVatIncl    = 0
    this.initialSellingPriceVatExcl = 0
    this.initialSellingPriceVatIncl = 0

    this.finalId                  = null
    this.finalCode                = ''  
    this.finalQty                 = 0
    this.finalUom                 = ''  
    this.finalCostPriceVatExcl    = 0
    this.finalCostPriceVatIncl    = 0
    this.finalSellingPriceVatExcl = 0
    this.finalSellingPriceVatIncl = 0
    
    this.descriptions        = []
    this.materialToMaterials   = []
  }

  async ngOnInit(): Promise<void> {
    this.address = await this.data.getAddress()
    this.logo = await this.data.getLogo()
    this.loadConversions()
    this.loadMaterialDescriptions()
  }
  
  async save() {  
    let options = {
      headers: new HttpHeaders().set('Authorization', 'Bearer '+this.auth.user.access_token)
    }
    var material_to_material = {
      id           : this.id,
      reason       : this.reason,
      comments     : this.comments
    }
    if(this.id == null || this.id == ''){  
      this.spinner.show() 
      await this.http.post<IMaterialToMaterial>(API_URL+'/material_to_materials/create', material_to_material, options)
      .pipe(finalize(() => this.spinner.hide()))
      .toPromise()
      .then(
        data => {
          this.id           = data?.id
          this.no           = data!.no 
          this.reason       = data!.reason        
          this.status       = data!.status
          this.comments     = data!.comments
          this.created      = data!.created
          this.approved     = data!.approved
          this.get(this.id)
          alert('Conversion Created successifully')
          this.blank = true
          this.loadConversions()
        }
      )
      .catch(
        error => {
          ErrorHandlerService.showHttpErrorMessage(error, '', 'Could not save Conversion')
          console.log(error)
        }
      )
    }else{
      this.spinner.show()
      await this.http.put<IMaterialToMaterial>(API_URL+'/material_to_materials/update', material_to_material, options)
      .pipe(finalize(() => this.spinner.hide()))
      .toPromise()
      .then(
        data => {
          this.id           = data?.id
          this.no           = data!.no
          this.reason       = data!.reason
          this.status       = data!.status
          this.comments     = data!.comments
          this.created      = data!.created
          this.approved     = data!.approved
          this.get(this.id)
          alert('Conversion Updated successifully')
          this.loadConversions()
        }
      )
      .catch(
        error => {
          console.log(error)
          ErrorHandlerService.showHttpErrorMessage(error, '', 'Could not update Conversion')
        }
      )
    }
  }

  get(id: any) {
    let options = {
      headers: new HttpHeaders().set('Authorization', 'Bearer '+this.auth.user.access_token)
    }
    this.spinner.show()
    this.http.get<IMaterialToMaterial>(API_URL+'/material_to_materials/get?id='+id, options)
    .pipe(finalize(() => this.spinner.hide()))
    .toPromise()
    .then(
      data => {
        this.id                       = data?.id
        this.no                       = data!.no
        this.reason                   = data!.reason
        this.status                   = data!.status
        this.comments                 = data!.comments
        this.created                  = data!.created
        this.approved                 = data!.approved
        this.materialToMaterialInitials = data!.materialToMaterialInitials
        this.materialToMaterialFinals   = data!.materialToMaterialFinals

        console.log(data)
      }
    )
    .catch(
      error => {
        ErrorHandlerService.showHttpErrorMessage(error, '', 'Could not load Conversion')
      }
    )
  }

  getByNo(no: string) {
    if(no == ''){
      return
    }
    let options = {
      headers: new HttpHeaders().set('Authorization', 'Bearer '+this.auth.user.access_token)
    }
    this.spinner.show()
    this.http.get<IMaterialToMaterial>(API_URL+'/material_to_materials/get_by_no?no='+no, options)
    .pipe(finalize(() => this.spinner.hide()))
    .toPromise()
    .then(
      data => {
        this.id                       = data?.id
        this.no                       = data!.no
        this.reason                   = data!.reason
        this.status                   = data!.status
        this.comments                 = data!.comments
        this.created                  = data!.created
        this.approved                 = data!.approved
        this.materialToMaterialInitials = data!.materialToMaterialInitials
        this.materialToMaterialFinals   = data!.materialToMaterialFinals

        console.log(data)
      }
    )
    .catch(
      error => {
        ErrorHandlerService.showHttpErrorMessage(error, '', 'Could not load Conversion')
      }
    )
  }

  approve(id: any) {
    if(!window.confirm('Confirm approval of the selected Conversion')){
      return
    }
    let options = {
      headers: new HttpHeaders().set('Authorization', 'Bearer '+this.auth.user.access_token)
    }
    var conversion = {
      id : this.id   
    }
    this.spinner.show()
    this.http.put(API_URL+'/material_to_materials/approve', conversion, options)
    .pipe(finalize(() => this.spinner.hide()))
    .toPromise()
    .then(
      () => {
        this.loadConversions()
        this.get(id)
      }
    )
    .catch(
      error => {
        console.log(error)
        ErrorHandlerService.showHttpErrorMessage(error, '', 'Could not approve')
      }
    )
  }

  cancel(id: any) {
    if(!window.confirm('Confirm canceling of the selected Conversion')){
      return
    }
    let options = {
      headers: new HttpHeaders().set('Authorization', 'Bearer '+this.auth.user.access_token)
    }
    var conversion = {
      id : this.id   
    }
    this.spinner.show()
    this.http.put(API_URL+'/material_to_materials/cancel', conversion, options)
    .pipe(finalize(() => this.spinner.hide()))
    .toPromise()
    .then(
      () => {
        this.clear()
        this.loadConversions()
      }
    )
    .catch(
      error => {
        console.log(error)
        ErrorHandlerService.showHttpErrorMessage(error, '', 'Could not cancel')
      }
    )
  }

  delete(id: any) {
    throw new Error('Method not implemented.');
  }
  
  async saveInitial() {
    
    if(this.id == '' || this.id == null){
      /**
       * First Create a new Conversion
       */
      alert('Conversion not available, the system will create a new Conversion')
      this.save()
    }else{
      /**
       * Enter Conversion Detail
       */
      let options = {
        headers: new HttpHeaders().set('Authorization', 'Bearer '+this.auth.user.access_token)
      }   
      var detail = {
        //id : this.initialId,
        materialToMaterial : {id : this.id},
        material : {id : this.initialId},
        qty : this.initialQty
      }
      this.spinner.show()
      await this.http.post(API_URL+'/material_to_material_initials/save', detail, options)
      .pipe(finalize(() => this.spinner.hide()))
      .toPromise()
      .then(
        () => {
          this.clearInitials()
          this.clearFinals()
          this.get(this.id)
          if(this.blank == true){
            this.blank = false
            this.loadConversions()
          }
        }
      )
      .catch(
        error => {
          console.log(error)
          ErrorHandlerService.showHttpErrorMessage(error, '', 'Could not save detail')
        }
      )
    }
  }

  async saveFinal() {
    
    if(this.id == '' || this.id == null){
      /**
       * First Create a new Conversion
       */
      alert('Conversion not available, the system will create a new Conversion')
      this.save()
    }else{
      /**
       * Enter Conversion Detail
       */
      let options = {
        headers: new HttpHeaders().set('Authorization', 'Bearer '+this.auth.user.access_token)
      }   
      var detail = {
        //id : this.finalId,
        materialToMaterial : {id : this.id},
        material : {id : this.finalId},
        qty : this.finalQty
      }
      this.spinner.show()
      await this.http.post(API_URL+'/material_to_material_finals/save', detail, options)
      .pipe(finalize(() => this.spinner.hide()))
      .toPromise()
      .then(
        () => {
          this.clearInitials()
          this.clearFinals()
          this.get(this.id)
          if(this.blank == true){
            this.blank = false
            this.loadConversions()
          }
        }
      )
      .catch(
        error => {
          console.log(error)
          ErrorHandlerService.showHttpErrorMessage(error, '', 'Could not save detail')
        }
      )
    }
  }

  getDetailByNo(no: string) {
    throw new Error('Method not implemented.');
  }

  deleteInitial(id: any) {
    let options = {
      headers: new HttpHeaders().set('Authorization', 'Bearer '+this.auth.user.access_token)
    }
    this.spinner.show()
    this.http.delete(API_URL+'/material_to_material_initials/delete?id='+id, options)
    .pipe(finalize(() => this.spinner.hide()))
    .toPromise()
    .then(
      () => {
        this.get(this.id)
      }
    )
    .catch(
      error => {ErrorHandlerService.showHttpErrorMessage(error, '', 'Could not remove detail')
      }
    )
  }

  deleteFinal(id: any) {
    let options = {
      headers: new HttpHeaders().set('Authorization', 'Bearer '+this.auth.user.access_token)
    }
    this.spinner.show()
    this.http.delete(API_URL+'/material_to_material_finals/delete?id='+id, options)
    .pipe(finalize(() => this.spinner.hide()))
    .toPromise()
    .then(
      () => {
        this.get(this.id)
      }
    )
    .catch(
      error => {ErrorHandlerService.showHttpErrorMessage(error, '', 'Could not remove detail')
      }
    )
  }

  loadConversions(){
    this.materialToMaterials = []
    let options = {
      headers: new HttpHeaders().set('Authorization', 'Bearer '+this.auth.user.access_token)
    }
    this.spinner.show()
    this.http.get<IMaterialToMaterial[]>(API_URL+'/material_to_materials', options)
    .pipe(finalize(() => this.spinner.hide()))
    .toPromise()
    .then(
      data => {
        data?.forEach(element => {
          this.materialToMaterials.push(element)
        })
      }
    )
  }

  async archive(id: any) {
    if(id == null || id == ''){
      window.alert('Please select Conversion to archive')
      return
    }
    if(!window.confirm('Confirm archiving of the selected Conversion')){
      return
    }
    let options = {
      headers: new HttpHeaders().set('Authorization', 'Bearer '+this.auth.user.access_token)
    }
    var conversion = {
      id : id   
    }
    this.spinner.show()
    await this.http.put<boolean>(API_URL+'/material_to_materials/archive', conversion, options)
    .pipe(finalize(() => this.spinner.hide()))
    .toPromise()
    .then(
      data => {
        this.clear()
        this.loadConversions()
        alert('Conversion archived successifully')
      }
    )
    .catch(
      error => {
        console.log(error)
        ErrorHandlerService.showHttpErrorMessage(error, '', 'Could not archive')
      }
    )
  }

  async archiveAll() {
    if(!window.confirm('Confirm archiving Conversion. All Approved conversions will be archived')){
      return
    }
    let options = {
      headers: new HttpHeaders().set('Authorization', 'Bearer '+this.auth.user.access_token)
    }
    this.spinner.show()
    await this.http.put<boolean>(API_URL+'/material_to_materials/archive_all', null, options)
    .pipe(finalize(() => this.spinner.hide()))
    .toPromise()
    .then(
      data => {
        this.clear()
        this.loadConversions()
        alert('Conversions archived successifully')
      }
    )
    .catch(
      error => {
        console.log(error)
        ErrorHandlerService.showHttpErrorMessage(error, '', 'Could not archive')
      }
    )
  }

  clear(){
    this.id                       = ''
    this.no                       = ''
    this.reason                   = ''
    this.status                   = ''
    this.comments                 = ''
    this.created                  = ''
    this.approved                 = ''
    this.materialToMaterialInitials = []
    this.materialToMaterialFinals   = []
  }

  clearInitials(){
    this.initialId                  = null
    this.initialCode                = ''
    this.initialDescription         = ''  
    this.initialQty                 = 0
    this.initialUom                 = ''  
    this.initialCostPriceVatExcl    = 0
    this.initialCostPriceVatIncl    = 0
    this.initialSellingPriceVatExcl = 0
    this.initialSellingPriceVatIncl = 0
  }

  clearFinals(){
    this.finalId                  = null
    this.finalCode                = '' 
    this.finalDescription         = '' 
    this.finalQty                 = 0
    this.finalUom                 = ''  
    this.finalCostPriceVatExcl    = 0
    this.finalCostPriceVatIncl    = 0
    this.finalSellingPriceVatExcl = 0
    this.finalSellingPriceVatIncl = 0
  }

  async requestNo(){
    let options = {
      headers: new HttpHeaders().set('Authorization', 'Bearer '+this.auth.user.access_token)
    }
    this.spinner.show()
    await this.http.get<any>(API_URL+'/material_to_materials/request_no', options)
    .pipe(finalize(() => this.spinner.hide()))
    .toPromise()
    .then(
      data => {
        console.log(data)
        this.no = data!['no']
        //this.lpoNoLocked  = true
      },
      error => {
        console.log(error)
        alert('Could not request PTP Number')
      }
    )
  }


  createShortCut(shortCutName : string, link : string){
    if(confirm('Create shortcut for this page?')){
      this.shortcut.createShortCut(shortCutName, link)
    }
  }

  searchInitialMaterial(code : string, description : string){
    this.clearInitials()
    this.clearFinals()
    let options = {
      headers: new HttpHeaders().set('Authorization', 'Bearer '+this.auth.user.access_token)
    }
    if(code != ''){
      this.spinner.show()
      this.http.get<IMaterial>(API_URL+'/materials/get_by_code?code='+code, options)
      .pipe(finalize(() => this.spinner.hide()))
      .toPromise()
      .then(
        data => {
          this.initialId                  = data!.id
          this.initialCode                = data!.code
          this.initialDescription         = data!.description
          this.initialCostPriceVatExcl    = data!.costPriceVatExcl
          this.initialCostPriceVatIncl    = data!.costPriceVatIncl
          if(data!.id == '' || data!.id == null){
            alert('Process failed')
          }
        }
      )
      .catch(error => {
        console.log(error)
        ErrorHandlerService.showHttpErrorMessage(error, '', 'Material not found')
      })
    }else{
      //search by description
      this.spinner.show()
      this.http.get<IMaterial>(API_URL+'/materials/get_by_description?description='+description, options)
      .pipe(finalize(() => this.spinner.hide()))
      .toPromise()
      .then(
        data => {
          this.initialId                  = data!.id
          this.initialCode                = data!.code
          this.initialDescription         = data!.description
          this.initialCostPriceVatExcl    = data!.costPriceVatExcl
          this.initialCostPriceVatIncl    = data!.costPriceVatIncl
          if(data!.id == '' || data!.id == null){
            alert('Process failed')
          }
        }
      )
      .catch(error => {
        ErrorHandlerService.showHttpErrorMessage(error, '', 'Material not found')
      })
    }
  }

  searchFinalMaterial(code : string, description : string){
    this.clearInitials()
    this.clearFinals()
    let options = {
      headers: new HttpHeaders().set('Authorization', 'Bearer '+this.auth.user.access_token)
    }
    if(code != ''){
      this.spinner.show()
      this.http.get<IMaterial>(API_URL+'/materials/get_by_code?code='+code, options)
      .pipe(finalize(() => this.spinner.hide()))
      .toPromise()
      .then(
        data => {
          this.finalId                  = data!.id
          this.finalCode                = data!.code
          this.finalDescription         = data!.description
          this.finalCostPriceVatExcl    = data!.costPriceVatExcl
          this.finalCostPriceVatIncl    = data!.costPriceVatIncl
          if(data!.id == '' || data!.id == null){
            alert('Process failed')
          }
        }
      )
      .catch(error => {
        console.log(error)
        ErrorHandlerService.showHttpErrorMessage(error, '', 'Material not found')
      })
    }else{
      //search by description
      this.spinner.show()
      this.http.get<IMaterial>(API_URL+'/materials/get_by_description?description='+description, options)
      .pipe(finalize(() => this.spinner.hide()))
      .toPromise()
      .then(
        data => {
          this.finalId                  = data!.id
          this.finalCode                = data!.code
          this.finalDescription         = data!.description
          this.finalCostPriceVatExcl    = data!.costPriceVatExcl
          this.finalCostPriceVatIncl    = data!.costPriceVatIncl
          if(data!.id == '' || data!.id == null){
            alert('Process failed')
          }
        }
      )
      .catch(error => {
        ErrorHandlerService.showHttpErrorMessage(error, '', 'Material not found')
      })
    }
  }

  async searchInitialDetail(detailId :any){ 
    this.clearInitials()
    this.clearFinals()
    let options = {
      headers: new HttpHeaders().set('Authorization', 'Bearer '+this.auth.user.access_token)
    }
   
    this.spinner.show()
    await this.http.get<IInitialMaterial>(API_URL+'/material_to_material_initials/get?id='+detailId, options)
    .pipe(finalize(() => this.spinner.hide()))
    .toPromise()
    .then(
      data => {
        this.initialId = data!.id
        this.initialCode = data!.material.code
        this.initialDescription = data!.material.description
        this.initialQty = data!.qty
      }
    )
    .catch(error => {
      console.log(error)
      ErrorHandlerService.showHttpErrorMessage(error, '', 'Could not load detail information')
    })
  }

  openInitial(contentInitial : any, detailId : string) {
    if(detailId != ''){
      this.searchInitialDetail( detailId)
    }
    
    this.modalService.open(contentInitial, {ariaLabelledBy: 'modal-basic-title'}).result.then((result) => {
    }, (reason) => {
      this.closeResult = `Dismissed ${this.getDismissReason(reason)}`;
    });
  }

  async searchFinalDetail(detailId :any){ 
    this.clearInitials()
    this.clearFinals()
    let options = {
      headers: new HttpHeaders().set('Authorization', 'Bearer '+this.auth.user.access_token)
    }
   
    this.spinner.show()
    await this.http.get<IFinalMaterial>(API_URL+'/material_to_material_finals/get?id='+detailId, options)
    .pipe(finalize(() => this.spinner.hide()))
    .toPromise()
    .then(
      data => {
        this.finalId = data!.id
        this.finalCode = data!.material.code
        this.finalDescription = data!.material.description
        this.finalQty = data!.qty
      }
    )
    .catch(error => {
      console.log(error)
      ErrorHandlerService.showHttpErrorMessage(error, '', 'Could not load detail information')
    })
  }

  openFinal(contentFinal : any, detailId : string) {
    if(detailId != ''){
      this.searchFinalDetail( detailId)
    }
    
    this.modalService.open(contentFinal, {ariaLabelledBy: 'modal-basic-title'}).result.then((result) => {
    }, (reason) => {
      this.closeResult = `Dismissed ${this.getDismissReason(reason)}`;
    });
  }

  private getDismissReason(reason: any): string {
    this.clearInitials()
    this.clearFinals()
    if (reason === ModalDismissReasons.ESC) {
      return 'by pressing ESC';
    } else if (reason === ModalDismissReasons.BACKDROP_CLICK) {
      return 'by clicking on a backdrop';
    } else {
      return `with: ${reason}`;
    }
  }

  async loadMaterialDescriptions(){
    let options = {
      headers: new HttpHeaders().set('Authorization', 'Bearer '+this.auth.user.access_token)
    }
    this.spinner.show()
    await this.http.get<string[]>(API_URL+'/materials/get_descriptions', options)
    .pipe(finalize(() => this.spinner.hide()))
    .toPromise()
    .then(
      data => {
        this.descriptions = []
        data?.forEach(element => {
          this.descriptions.push(element)
        })
        console.log(data)
      },
      error => {
        ErrorHandlerService.showHttpErrorMessage(error, '', 'Could not load material descriptions')
      }
    )
  }

  

  showList(listContent: any) {
    
    this.modalService.open(listContent, {ariaLabelledBy: 'modal-basic-title'}).result.then((result) => {
    }, (reason) => {
      this.closeResult = `Dismissed ${this.getDismissReason(reason)}`;
    });
  }


  exportToPdf = () => {
    var header = ''
    var footer = ''
    var title  = 'Material to Material Conversion'
    var logo : any = ''
    var total : number = 0
    if(this.logo == ''){
      logo = { text : '', width : 70, height : 70, absolutePosition : {x : 40, y : 40}}
    }else{
      logo = {image : this.logo, width : 70, height : 70, absolutePosition : {x : 40, y : 40}}
    }
    var initialMaterial = [
      [
        {text : 'Code', fontSize : 9}, 
        {text : 'Description', fontSize : 9},
        {text : 'Qty', fontSize : 9},
      ]
    ]  
    var finalMaterial = [
      [
        {text : 'Code', fontSize : 9}, 
        {text : 'Description', fontSize : 9},
        {text : 'Qty', fontSize : 9},
      ]
    ] 
        
    this.materialToMaterialInitials.forEach((element) => {
      var detail = [
        {text : element.material.code.toString(), fontSize : 9}, 
        {text : element.material.description.toString(), fontSize : 9},
        {text : element.qty.toString(), fontSize : 9}, 
      ]
      initialMaterial.push(detail)
    })

    this.materialToMaterialFinals.forEach((element) => {
      var detail = [
        {text : element.material.code.toString(), fontSize : 9}, 
        {text : element.material.description.toString(), fontSize : 9},
        {text : element.qty.toString(), fontSize : 9}, 
      ]
      finalMaterial.push(detail)
    })
   
    const docDefinition = {
      header: '',
      watermark : { text : title, color: 'blue', opacity: 0.1, bold: true, italics: false },
        content : [
          {
            columns : 
            [
              logo,
              {width : 10, columns : [[]]},
              {
                width : 300,
                columns : [
                  this.address
                ]
              },
            ]
          },
          '  ',
          '  ',
          {text : title, fontSize : 12, bold : true},
          '  ',
          {
            layout : 'noBorders',
            table : {
              widths : [75, 300],
              body : [
                [
                  {text : 'Conversion No', fontSize : 9}, 
                  {text : this.no, fontSize : 9} 
                ],
                [
                  {text : 'Reason', fontSize : 9}, 
                  {text : this.reason, fontSize : 9} 
                ],
                [
                  {text : 'Status', fontSize : 9}, 
                  {text : this.status, fontSize : 9} 
                ]
              ]
            },
          },
          '  ',
          'Initial Materials',
          {
            table : {
              headerRows : 1,
              widths : ['auto', 200, 'auto'],
              body : 
                initialMaterial
            }
        },
        ' ',
        'Final Materials',
        {
          table : {
            headerRows : 1,
            widths : ['auto', 200, 'auto'],
            body : 
              finalMaterial
          }
      },
        ' ',
        ' ',
        ' ',
        'Verified ____________________________________', 
        ' ',
        ' ',
        'Approved __________________________________',             
      ]     
    };
    pdfMake.createPdf(docDefinition).open(); 
  }


}

interface IMaterialToMaterial{
  id           : any
  no           : string
  status       : string
  reason       : string
  comments     : string
  created      : string
  approved     : string
  materialToMaterialInitials : IInitialMaterial[]
  materialToMaterialFinals   : IFinalMaterial[]
}



interface IInitialMaterial{
  id                  : any
  barcode             : string
  code                : string
  description         : string
  uom                 : string
  qty                 : number
  costPriceVatExcl    : number
  costPriceVatIncl    : number
  material             : IMaterial
}

interface IFinalMaterial{
  id                  : any
  barcode             : string
  code                : string
  description         : string
  uom                 : string
  qty                 : number
  costPriceVatExcl    : number
  costPriceVatIncl    : number
  material             : IMaterial
}

interface IMaterial{
  id                  : any
  barcode             : string
  code                : string
  description         : string
  uom                 : string
  costPriceVatExcl    : number
  costPriceVatIncl    : number
}