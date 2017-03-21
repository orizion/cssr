import * as React from "react";

import { FormControl,FormGroup,ControlLabel,Checkbox,Button } from "react-bootstrap";

export interface CreatePresentationProps { apiUrl:string; }
export interface CreatePresentationState {
  datetime:Date;location:string; speaker_email:string; speaker_id:number; title:string; abstract:string;
}

export class CreatePresentation extends React.Component<CreatePresentationProps, CreatePresentationState> {
  constructor(props: CreatePresentationProps){
    super(props);
    this.state = {
      datetime:new Date(),
      location:"",
      speaker_email:"",
      speaker_id:0,
      title:"",
      abstract:"",
    };
    this.handleChanged = this.handleChanged.bind(this);
  }
  inputStyle = {
    borderColor: 'red',
  }
  handleChanged(e:any) {
    this.setState({[e.target.name]: e.target.value});
    console.log(this.state);
  }
  submit(){
    fetch(this.props.apiUrl)
    .then((res)=> {
      if(res.ok){
        this.setState({
          datetime: new Date(),
          location:"",
          speaker_email:"",
          speaker_id:0,
          title:"",
          abstract:"",
        });
      }else {

      }
    });
  }
  render() {
    return (
      <form>
        <FormGroup controlId="formControlsTitle">
          <ControlLabel>Titel</ControlLabel>
          <br/>
          <FormControl type="text" name="title" placeholder=""
              onChange={this.handleChanged} required/>
        </FormGroup>

        <FormGroup controlId="formControlsAbstract">
          <ControlLabel>Abstract</ControlLabel>
          <br/>
          <FormControl componentClass="textarea" name="abstract" placeholder=""
              onChange={this.handleChanged} required/>
        </FormGroup>

        <FormGroup controlId="formControlsDateTime">
          <ControlLabel>Datum</ControlLabel>
          <br/>
          <FormControl type="date" name="datetime" placeholder="yyyy.mm.dd"
              onChange={this.handleChanged} required/>
        </FormGroup>

        <FormGroup controlId="formControlsLocation">
          <ControlLabel>Raum</ControlLabel>
          <br/>
          <FormControl type="text" name="location" placeholder=""
              onChange={this.handleChanged} required/>
        </FormGroup>

        <FormGroup controlId="formControlsSpeakerEmail">
          <ControlLabel>Email Referent</ControlLabel>
          <br/>
          <FormControl type="email" name="speaker_email"
              onChange={this.handleChanged} required/>
        </FormGroup>

        <Button type="submit" bsStyle="primary" onClick={this.submit}>
          Senden
        </Button>
      </form>
    );
  }
}
