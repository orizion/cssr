import * as React from "react";
import Select = require('react-select');
import update = require('react-addons-update');
import * as API from "../services/api";

import { FormControl,FormGroup,ControlLabel,Checkbox,Button, Row, Col, } from "react-bootstrap";

export interface EditPresentationProps {
}
export interface EditPresentationState {
  presentation:API.Presentation,
  files: API.PresentationFileMeta[],
}

export class EditPresentation extends React.Component<any, EditPresentationState> {
  constructor(props: EditPresentationProps){
    super(props);
    this.state = {
      presentation: {

      },
      files: []
    };
    API.defaultHeaders["Authorization"] = "Bearer " + localStorage.token;
    this.handleChanged = this.handleChanged.bind(this);
  }
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
  submit(){
    /*fetch(this.props.apiUrl)
    .then((res)=> {
      if(res.ok){
        this.setState({
          title:"",
          abstract:"",
        });
      }else {

      }
    });*/
  }
  render() {
    let authorities:Array<String> = this.props.userMeta.authorities || new Array<String>();
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
                          readOnly={
                            authorities.indexOf("Role_Coord") == -1 
                            && authorities.indexOf("Role_SGL") == -1 
                            && authorities.indexOf("Role_Admin") == -1}/>
            </Col>
            <Col xs={6} md={6}>
              <FormControl type="text" name="location" placeholder=""
                          onChange={this.handleChanged} 
                          readOnly={
                            authorities.indexOf("Role_Coord") == -1 
                            && authorities.indexOf("Role_SGL") == -1 
                            && authorities.indexOf("Role_Admin") == -1}/>
            </Col>
          </Row>
          <br/>
        </FormGroup>

        <FormGroup controlId="formControlsSpeakerEmail">
          <ControlLabel>Email Referent</ControlLabel>
          <br/>
          <FormControl type="email" name="speaker_email"
              onChange={this.handleChanged} readOnly/>
        </FormGroup>

        <FormGroup controlId="formControlsType">
          <Row>
            <Col xs={6} md={6}>
              <ControlLabel>Datei ablegen als </ControlLabel>
            </Col>

            <Col xs={6} md={6}>
                <ControlLabel>Dateityp</ControlLabel>
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
          </Row>                   
        </FormGroup>

        {true &&
          <FormGroup controlId="formControlsAbstract">
            <ControlLabel>Test</ControlLabel>
            <br/>
            <FormControl componentClass="textarea" name="abstract" placeholder=""
                onChange={this.handleChanged} required/>
          </FormGroup>
        }
        { true &&
          <FormGroup controlId="formControlsAbstract">
            <ControlLabel>Abstract</ControlLabel>
            <br/>
            <FormControl componentClass="textarea" name="abstract" placeholder=""
               onChange={this.handleChanged} required/>
          </FormGroup>
        }

        <Button type="submit" bsStyle="primary" onClick={this.submit}>
          Senden
        </Button>
      </form>
    );
  }
}
