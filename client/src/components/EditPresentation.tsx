import * as React from "react";
import Select = require('react-select');
import update = require('react-addons-update');
import * as API from "../services/api";
import {translate } from "react-i18next";

import { FormControl,FormGroup,ControlLabel,Checkbox,Button, Row, Col, } from "react-bootstrap";
import {PresentationFileUpload} from "./PresentationFileUpload";

export interface EditPresentationProps {
  presentationId: number,
  authorities: Array<string> | undefined,
  t: any;

}
export interface EditPresentationState {
  presentation:API.Presentation,
  files: API.PresentationFileMeta[],
  fileUploads:JSX.Element[];
}

@translate(['editPresentation', 'common'], { wait: true })
export class EditPresentation extends React.Component<EditPresentationProps, EditPresentationState> {
  constructor(props: EditPresentationProps){
    super(props);
    this.state = {
      presentation: {},
      files: [],
      fileUploads:[]
    };
    API.defaultHeaders["Authorization"] = "Bearer " + localStorage.token;

    this.presentationAPI = new API.PresentationcontrollerApi();
    this.presentationFileAPI = new API.PresentationfilecontrollerApi();
    this.presentationAPI.getSingleUsingGET({  "id": this.props.presentationId })
    .then((response)=>{
      this.setState({
        presentation: response
      });
    });

    //this.authorities = this.props.userMeta.authorities || new Array<String>();
    this.handleChanged = this.handleChanged.bind(this);
  }
  presentationAPI: API.PresentationcontrollerApi;
  presentationFileAPI: API.PresentationfilecontrollerApi;
  handleChanged(e:any) {
    this.setState({[e.target.name]: e.target.value});
    console.log(this.state);
  }
  fileTypes = [
    {value:"f",label:"Präsentation"},
    {value:"r",label:"Ressource"}
  ];
  storageTypes = [
    {value:"f",label:"Datei"},
    {value:"l",label:"Link"}
  ];
  
  handleSelectChanged(e: any) {
        this.setState({
            files: update(this.state.files, {
               files : { $set: e.value }
            })
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
  handleAddUploadComponent(e:any) {
    let newIndex = this.state.fileUploads.length+1;
    this.setState({
      fileUploads : update(this.state.fileUploads,{
        fileUploads : {
          $push:  <PresentationFileUpload t={this.props.t}  key={newIndex}/>
        }
      })
    });
      
    
  }
  presentationFileControl(props:any) {

    
  }
  submit(){
    this.presentationAPI.modifyPresentationUsingPUT({  "pres": this.state.presentation })
    .then(()=>{

    })
    .catch((err) =>{
      console.log("Error while updating Presentation: ");
      console.log(err);
    });
    this.presentationFileAPI.addFileBinaryUsingPOST(
    {
      "presentationId": this.props.presentationId,"content": "",
      "type":"","displayName": "test","contentType": "test"
    })
    .then((response)=>{

    })
    .catch((error)=>{

    });
  }
  render() {
    let authorities:Array<String> = this.props.authorities || new Array<String>();
    let t = this.props.t;
    return (
      <form>
        <FormGroup controlId="formControlsTitle">
          <ControlLabel>Titel</ControlLabel>
          <br/>
          <FormControl type="text" name="title" placeholder=""
              onChange={this.handleChanged} value={this.state.presentation.title}      
              required/>
        </FormGroup>

        <FormGroup controlId="formControlsAbstract">
          <ControlLabel>Abstract</ControlLabel>
          <br/>
          <FormControl componentClass="textarea" name="abstract" placeholder=""
              onChange={this.handleChanged} required/>
        </FormGroup>

        <FormGroup controlId="formControlsDateTime">
          <Row>
            <Col xs={6} md={6}>
              <ControlLabel>Datum</ControlLabel>
            </Col>
            <Col xs={6} md={6}>
              <ControlLabel>Raum</ControlLabel>
            </Col>
          </Row>
          
          <Row>
            <Col xs={6} md={6}>
              <FormControl type="date" name="datetime" placeholder="yyyy.mm.dd"
                          onChange={this.handleChanged} 
                          readOnly={this.isAuthorized(["Role_Coord","Role_SGL","Role_Admin"])}/>
            </Col>
            <Col xs={6} md={6}>
              <FormControl type="text" name="location" placeholder=""
                          onChange={this.handleChanged} 
                          readOnly={this.isAuthorized(["Role_Coord","Role_SGL","Role_Admin"])}/>
            </Col>
          </Row>
          <br/>
        </FormGroup>

        <FormGroup controlId="formControlsSpeakerEmail">
          <ControlLabel>{t('common:email_speaker')}</ControlLabel>
          <br/>
          <FormControl type="email" name="speaker_email"
              onChange={this.handleChanged} readOnly/>
        </FormGroup>

                <FormGroup controlId="formControlsType">
          <Row>
            <Col xs={6} md={6}>
              <ControlLabel>{t('saveFileAs')}</ControlLabel>
            </Col>
            <Col xs={6} md={6}>
                <ControlLabel>{t('dataType')}</ControlLabel>
            </Col>
          </Row>
          <Row>
            <Col xs={6} md={6}>
              <Select name="storageType"     
                  value="l"          
                  options={this.storageTypes}
                  onChange={this.handleSelectChanged}
                  clearable={false}
              />
            </Col>
            <Col xs={6} md={6}>
              <Select name="fileType"     
                  value="f"          
                  options={this.fileTypes}
                  onChange={this.handleSelectChanged}
                  clearable={false}
              />
            </Col>
            {this.state.fileUploads}
          </Row>                   
        </FormGroup>

        

        <Button type="submit" bsStyle="primary" onClick={this.submit}>
          {t('common:save')}
        </Button>
      </form>
    );
  }
}
