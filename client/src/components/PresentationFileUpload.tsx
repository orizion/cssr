import * as React from "react";
import Select = require('react-select');
import update = require('react-addons-update');
import * as API from "../services/api";
import {translate } from "react-i18next";

import { FormControl,FormGroup,ControlLabel,Checkbox,Button, Row, Col, } from "react-bootstrap";

export interface PresentationFileUploadProps {
  t: any;
  type:string,
  contentType:string,
  onAddFile:Function
  presentationId:number,
  componentKey: number;
}
export interface PresentationFileUploadState {
  fileMeta: API.PresentationFileMeta,
  file: any;
}

@translate(['presentationFileUpload', 'common'], { wait: true })
export class PresentationFileUpload extends React.Component<PresentationFileUploadProps, PresentationFileUploadState> {
  constructor(props: PresentationFileUploadProps){
    super(props);
    this.state = {
        fileMeta:{},
        file: {}
    };
    API.defaultHeaders["Authorization"] = "Bearer " + localStorage.token;
    this.handleChanged = this.handleChanged.bind(this);
  }
  handleChanged(e:any) {
        
    let _type: "f" | "r" = "r";
    if(this.props.type != "r"){
        _type = "f";
    }
    let fileMeta: API.PresentationFileMeta;
    let self = this;
    if(this.props.contentType == "f") {
        let file = e.target.files[0];
        let fr = new FileReader();
        fr.readAsText(file);
        fileMeta = {
            contentType: this.props.contentType,
            displayName: file.name,
            presentationId: this.props.presentationId, 
            type: _type
        };
        let content = fr.result;
        fr.onload = function(event) {
            // The file's text will be printed here
            self.props.onAddFile(fileMeta,fr.result,self.props.componentKey);
        };   

        
    }else {      
        fileMeta= {
            contentType: this.props.contentType,
            displayName: "",
            presentationId: this.props.presentationId, 
            type: _type,
            contentLink: e.target.value
        };
        self.props.onAddFile(fileMeta,"",self.props.componentKey);
    }
  }
  render() {
    let t = this.props.t;
    return (
        <FormGroup controlId="formControlsPresentationFile">
        <ControlLabel>{t('presentationFile')}</ControlLabel>
        <br/>

        {this.props.contentType == "f" ? 
            <FormControl type="file" name="file"
            onChange={this.handleChanged} />
        :
            <FormControl type="text" name="link"
            onChange={this.handleChanged} />         
        }          
        </FormGroup>
    );
  }
}
