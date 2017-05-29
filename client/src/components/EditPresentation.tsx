import * as React from "react";
import Select = require('react-select');
import update = require('react-addons-update');
import * as API from "../services/apiWrapper";
import {translate } from "react-i18next";
import * as Datetime from "react-datetime";
import moment = require("moment");


import { FormControl,FormGroup,ControlLabel,Checkbox,Button, Row, Col, } from "react-bootstrap";
import {PresentationFileUpload} from "./PresentationFileUpload";

export interface EditPresentationProps {
  presentationId: number,
  authorities: Array<string> | undefined,
  t: any;

}
export interface EditPresentationState {
  presentation:API.Presentation,
  files: {meta:API.PresentationFileMeta,file:any}[],
  fileUploads:JSX.Element[],
  contentType: string,
  type: string;
}

@translate(['editPresentation', 'common'], { wait: true })
export class EditPresentation extends React.Component<EditPresentationProps, EditPresentationState> {
  constructor(props: EditPresentationProps){
    super(props);
    this.state = {
      presentation: {},
      files: [],
      fileUploads:[],
      contentType:"f",
      type: "f"
    };
    
    new API.PresentationcontrollerApi().getSingleUsingGET({  "id": this.props.presentationId })
    .then((response)=>{
      this.setState({
        presentation: response
      });
    });

    //this.authorities = this.props.userMeta.authorities || new Array<String>();
    this.handleChanged = this.handleChanged.bind(this);
    this.handleDateChanged = this.handleDateChanged.bind(this);
    this.handleAddUploadComponent = this.handleAddUploadComponent.bind(this);
    this.handleFileChanged = this.handleFileChanged.bind(this);
    this.handleStorageChanged = this.handleStorageChanged.bind(this);
    this.handleAddFile = this.handleAddFile.bind(this);
    this.submit = this.submit.bind(this);
  }
  presentationAPI = new API.PresentationcontrollerApi();
  presentationFileAPI = new API.PresentationfilecontrollerApi();
  userAPI = new API.UsercontrollerApi();
  handleChanged(e: any) {
    let val: any = e.target.value;
    this.setState({
        presentation: update(this.state.presentation, {
            [e.target.name]: { $set: val }
        })
    });
  }
  type = [
    {value:"f",label:"Präsentation"},
    {value:"r",label:"Ressource"}
  ];
  contentType = [
    {value:"f",label:"Datei"},
    {value:"l",label:"Link"}
  ];
  handleDateChanged(moment: any) {
      this.setState({
          presentation: update(this.state.presentation, {
              dateTime: { $set: moment.toDate() }
          })
      });
  }
  handleSelectChanged(e: any) {
      this.setState({
          presentation: update(this.state.presentation, {
              speakerId: { $set: e.value }
          })
      });
  }
  handleStorageChanged(e: any) {
    this.setState({
        contentType: e.value
    });
  }
  handleFileChanged(e: any) {
    this.setState({
        type: e.value
    });
  }
  getSpeakerList = (input: any) => {
        return this.userAPI.getAllUsingGET(input)
        .then((response) => {
            let speakers: any = response.map((s) => {
                return { label: s.email, value: s.userId };
            });
            return speakers;
        }).then((json) => {
            return { options: json };
        });
    }
  isAuthorized(authorizedRoles:Array<string>):boolean {
    let authorities = this.props.authorities || new Array<string>();
    for (var i = 0; i < authorizedRoles.length; i++) {
      if(authorities.indexOf(authorizedRoles[i]) != -1) {
        return true;
      }
    }
    return false;
  }
  handleAddFile(_meta: API.PresentationFileMeta, _file:any,key:number) {
    this.setState({
      files: update(this.state.files,{
          [key]:{ 
            $set : {meta:_meta,file:_file}        
          }
        }
      )
    });
  }
  handleAddUploadComponent(e:any) {
    let newIndex = this.state.fileUploads.length;
    this.setState({
      fileUploads : this.state.fileUploads.concat(
            <PresentationFileUpload t={this.props.t}  componentKey={newIndex} 
              presentationId={this.state.presentation.presentationId || -1}
              type={this.state.type}
              contentType={this.state.contentType} 
              onAddFile={this.handleAddFile}/>
      ),
      files: this.state.files.concat({meta:"", file:""})
    });  
    
  }

  submit(e:any){
    e.preventDefault(); 
    this.presentationAPI.modifyPresentationUsingPUT({  "pres": this.state.presentation })
    .then(()=>{
      
    })
    .catch((err) =>{
      console.log("Error while updating Presentation: ");
      console.log(err);
    });

    this.state.files.forEach((file) => {
      console.log(file);
      if(file.meta.contentType == "f") {
        this.presentationFileAPI.addFileBinaryUsingPOST(
        {
          "presentationId": this.props.presentationId,"content": file.file,
          "type":file.meta.type || "f","displayName": file.meta.displayName || "",
          "contentType": file.meta.contentType || ""
        })
        .then((response)=>{

        })
        .catch((error)=>{

        });
      }else {
        console.log("error on file:");
        this.presentationFileAPI.addFileLinkUsingPOST({
          "presentationId" :this.props.presentationId,
          "file": file.meta
        })
        .then((response)=>{
          console.log("Server responded to file meta:");
          console.log(response);
        })
        .catch((error)=>{
          console.log("Server had error on file meta:");
          console.log(error);
        });
      }
      
    });

    
  }
  render() {
    let authorities:Array<String> = this.props.authorities || new Array<String>();
    let auth = this.isAuthorized(["Role_Coord","Role_SGL","Role_Admin"]);

    let date = moment(this.state.presentation.dateTime || new Date()).format();
    let t = this.props.t;
    return (
      <form>
        <FormGroup controlId="formControlsTitle">
          <ControlLabel>{t('common:title')}</ControlLabel>
          <br/>
          <FormControl type="text" name="title" placeholder=""
              onChange={this.handleChanged} value={this.state.presentation.title}      
              required/>
        </FormGroup>

        <FormGroup controlId="formControlsAbstract">
          <ControlLabel>{t('common:abstract')}</ControlLabel>
          <br/>
          <FormControl componentClass="textarea" name="abstract" placeholder=""
              onChange={this.handleChanged} 
              value={this.state.presentation.abstract}
              required/>
        </FormGroup>

        <FormGroup controlId="formControlsDateTime">
          <Row>
            <Col xs={6} md={6}>
              <ControlLabel>{t('common:date')}</ControlLabel>
            </Col>
            <Col xs={6} md={6}>
              <ControlLabel>{t('common:room')}</ControlLabel>
            </Col>
          </Row>
          
          <Row>
            <Col xs={6} md={6}>
              
              { auth ?
                <FormControl type="text" name="datetime" placeholder="yyyy.mm.dd" value={date} readOnly/>
                : 
                <Datetime input={true} locale="de" value={this.state.presentation.dateTime} utc={true}
                        onChange={this.handleDateChanged} />
              }
              
            </Col>
            <Col xs={6} md={6}>
              <FormControl type="text" name="location" placeholder=""
                          onChange={this.handleChanged} 
                          value={this.state.presentation.location}
                          readOnly={this.isAuthorized(["Role_Coord","Role_SGL","Role_Admin"])}/>
            </Col>
          </Row>
          <br/>
        </FormGroup>

        <FormGroup controlId="formControlsSpeakerEmail">
          <ControlLabel>{t('common:email_speaker')}</ControlLabel>
          <br/>
            <Select.Async
              name="speakerId"
              value={this.state.presentation.speakerId}
              loadOptions={this.getSpeakerList}
              onChange={this.handleSelectChanged}
              searchable={true}
              clearable={true}
              disabled={this.isAuthorized(["Role_Coord","Role_SGL","Role_Admin"])}
              />
        </FormGroup>

        <FormGroup controlId="formControlsType">
          <Row>
            <Col xs={4} md={4}>
              <ControlLabel>{t('saveFileAs')}</ControlLabel>
            </Col>
            <Col xs={4} md={4}>
                <ControlLabel>{t('dataType')}</ControlLabel>
            </Col>
          </Row>
          <Row>
            <Col xs={4} md={4}>
              <Select name="contentType"     
                  value={this.state.contentType}          
                  options={this.contentType}
                  onChange={this.handleStorageChanged}
                  clearable={false}
              />
            </Col>
            <Col xs={4} md={4}>
              <Select name="fileType"     
                  value={this.state.type}          
                  options={this.type}
                  onChange={this.handleFileChanged}
                  clearable={false}
              />
              </Col>
              <Col>
                <Button bsStyle="success" onClick={this.handleAddUploadComponent} data-navigo>
                  <span className="glyphicon glyphicon-plus-sign" aria-hidden="true"></span>
                  &nbsp; {t('addFiles')}
                </Button>
              </Col>
          </Row>                   

           
        </FormGroup>
        {this.state.fileUploads}
        

        <Button type="submit" bsStyle="primary" onClick={this.submit}>
          {t('common:save')}
        </Button>
      </form>
    );
  }
}
